/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/

package org.eclipse.linuxtools.tmf.totalads.ui.live;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.Results;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSNetException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.ResultsAndFeedback;
import org.eclipse.linuxtools.tmf.totalads.ui.io.ProgressConsole;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
/**
 * This class connects to a remote system using SSH, evaluate algorithms on collected traces,
 * trains them, and it also updates the live chart. It does all this by executing as a Thread
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class BackgroundLiveMonitor implements Runnable {
	private String userAtHost;
	private String password;
	private String sudoPassword;
	private String pathToPrivateKey;
	private Integer port;
	private ProgressConsole console;
	private SSHConnector ssh;
	private Integer snapshotDuration;// In Seconds
	private Integer intervalsBetweenSnapshots; // In Minutes
	private Button btnStart;
  	private Button btnStop;
  	private Button btnDetails;
  	private HashMap<String,String[]> modelsAndSettings;
  	private ResultsAndFeedback results;
  	private MessageBox msgBox;
  	private HashMap<String,LinkedList<Double>> modelsAndAnomaliesOnChart;
  	private Integer anomalyIdx=0;
	private Integer maxPoints;
	private LiveXYChart liveXYChart;
	private String []seriesNames;
	private AlgorithmFactory algFac;
	private ITraceTypeReader lttngSyscallReader;
	private LinkedList<Double> xSeries;
	private boolean isTrainAndEval;
	private Integer totalTraces;
	private volatile boolean isExecuting=true;
	private HashMap <String, Double> modelsAndAnomalyCount;
	
	/**
	 * Constructor
	 * @param userAtHost
	 * @param password
	 * @param sudoPassowrd
	 * @param pathToPrivateKey
	 * @param port
	 * @param snapshotDuration
	 * @param intervalBetweenSnapshots
	 * @param btnStart
	 * @param btnStop
	 * @param btnDetails
	 * @param modelsAndSettings
	 * @param results
	 * @param xyChart
	 * @param console
	 */
	public BackgroundLiveMonitor(String userAtHost, String password,String sudoPassowrd, String pathToPrivateKey,
			Integer port, Integer snapshotDuration,Integer intervalBetweenSnapshots, Button btnStart,
		  	Button btnStop, Button btnDetails,HashMap<String,String[]> modelsAndSettings,
		  	ResultsAndFeedback results,	LiveXYChart xyChart,Boolean isTrainEval,ProgressConsole console ) {
		
		this.userAtHost=userAtHost;
		this.password=password;
		this.sudoPassword=sudoPassowrd;
		this.pathToPrivateKey=pathToPrivateKey;
		this.port=port;
		this.snapshotDuration=snapshotDuration;
		this.intervalsBetweenSnapshots=intervalBetweenSnapshots;
		this.btnStart=btnStart;
		this.btnStop=btnStop;
		this.btnDetails=btnDetails;
		this.modelsAndSettings=modelsAndSettings;
		this.results=results;
		this.liveXYChart=xyChart;
		this.console=console;
		this.maxPoints=intervalBetweenSnapshots*30;
		this.isTrainAndEval=isTrainEval;
		
		LinkedList<Double> anomalyCounts=new LinkedList<Double>();
		modelsAndAnomaliesOnChart=new HashMap<String, LinkedList<Double>>();
		modelsAndAnomalyCount=new HashMap<String, Double>();
		
		seriesNames=new String[modelsAndSettings.size()];
		int idx=0;
		
		for (Map.Entry<String, String[]> models:modelsAndSettings.entrySet()){
			this.modelsAndAnomaliesOnChart.put(models.getKey(), anomalyCounts);
			seriesNames[idx]=models.getKey();
			modelsAndAnomalyCount.put(seriesNames[idx], 0.0);
			idx++;
			
		}
		
		this.results.registerAllModelNames(seriesNames);
		this.results.setMaxAllowableTrace(30);
		
		ssh = new SSHConnector();
		msgBox=new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,SWT.ICON_ERROR|SWT.OK);
		
		algFac=AlgorithmFactory.getInstance();
		lttngSyscallReader= TraceTypeFactory.getInstance().getCTFKernelorUserReader(true);
		
		xSeries=new LinkedList<Double>();
		totalTraces=0;
		
		
	}

	/**
	* Overridden function to run a thread
	*/
	@Override
	public void run(){
		String exception="";
		try{
			initialise();
		
			while (isExecuting){//keep running untill the stop function is called
						//Getting a trace from remote system
					String tracePath=ssh.collectATrace(sudoPassword);
					//String tracePath=ssh.getTrace();
					//System.out.println(anomalyIdx + " "+maxPoints);
					if (xSeries.isEmpty())
						xSeries.add(0.0);
						
					else{
						if (anomalyIdx>maxPoints)
							xSeries.remove();// remove first point on the series
						xSeries.add(intervalsBetweenSnapshots.doubleValue()+xSeries.getLast());
						liveXYChart.setXRange(xSeries.getFirst().intValue(), xSeries.getLast().intValue(),100);
					}
					//Convert it into a series for plotting a chart
					Double []xVals=new Double[xSeries.size()];
					xVals=xSeries.toArray(xVals);
					
					processTraceOnModels(tracePath, xVals);
					totalTraces++;
					results.setTotalTraceCount(totalTraces.toString());	
					//calcualte percentages
					HashMap <String, Double> modelsAnoms=new HashMap<String,Double>();
					for (int i=0;i<this.seriesNames.length;i++){
						Double anoms=(modelsAndAnomalyCount.get(seriesNames[i])/totalTraces)*100;
						modelsAnoms.put(seriesNames[i],anoms);
					
					}
						results.setTotalAnomalyCount(modelsAnoms);
					//
					//Run on main GUI thread
					//
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							btnDetails.setEnabled(true);
						}
					});
					
					// Check if stop has been requested
					if (isExecuting==false)
						break;// break out of the loop 
					
					console.printTextLn("Pausing for "+intervalsBetweenSnapshots+ " min to restart tracing on"
									+ " the remote host "+userAtHost.replaceAll(".*@","" ));
					try{
						//TimeUnit.MINUTES.sleep(intervalsBetweenSnapshots);
						TimeUnit.SECONDS.sleep(2);
						
					} catch (InterruptedException ex){}
			
		  }// End of while
			
		} 
		catch (TotalADSNetException ex){
			exception=ex.getMessage();
			Logger.getLogger(BackgroundLiveMonitor.class.getName()).log(Level.SEVERE,exception, ex);
			
		} catch (TotalADSReaderException ex) {
			exception=ex.getMessage();
			Logger.getLogger(BackgroundLiveMonitor.class.getName()).log(Level.SEVERE,exception, ex);
			
	
		} catch (TotalADSDBMSException ex) {
			exception=ex.getMessage();
			Logger.getLogger(BackgroundLiveMonitor.class.getName()).log(Level.SEVERE,exception, ex);
			
	
		} catch (TotalADSUIException ex) {
			exception=ex.getMessage();
			Logger.getLogger(BackgroundLiveMonitor.class.getName()).log(Level.SEVERE,exception, ex);
	
		}catch (Exception ex){
			exception=ex.getMessage();
			
			Logger.getLogger(BackgroundLiveMonitor.class.getName()).log(Level.SEVERE,exception, ex);
			ex.printStackTrace();
			
		}
		finally{
			ssh.close();
			console.printTextLn("SSH connection terminated");
			console.printTextLn("Monitor stopped");
			final String err=exception;
			//
			//Run on main GUI thread
			//
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					
					if (err !=null && !err.isEmpty()){
						msgBox.setMessage(err);
						msgBox.open();
					}
						
					btnStop.setEnabled(false);
					btnStart.setEnabled(true);
					
				}
			});
			
		}
		
	}
	
	/**
	 * Stops the thread
	 */
	public void stopMonitoring(){
		isExecuting=false;
		console.printTextLn("Stopping monitor");
		console.printTextLn("It could take few minutes to safely stop the monitor");
		console.printTextLn("Please wait...");		
	}
	/**
	 * This is a key function that evaluates traces on models and trains the model on those traces if needed
	 * @param tracePath
	 * @param xVals
	 * @throws TotalADSReaderException 
	 * @throws TotalADSDBMSException 
	 * @throws TotalADSUIException 
	 */
	private void processTraceOnModels(String tracePath, Double []xVals) throws TotalADSReaderException, TotalADSUIException, TotalADSDBMSException{
		
		HashMap<String, Results> modelsAndResults=new HashMap<String,Results>();
		boolean isAnomCountThres=false;
		for (Map.Entry<String, String[]> modAndSettings:modelsAndSettings.entrySet()){
			
				String model=modAndSettings.getKey();
				String []settings=modAndSettings.getValue();
				
				IDetectionAlgorithm algorithm=algFac.getAlgorithmByAcronym(model.split("_")[1]);
				
				
				
				console.printTextLn("Evaluting trace on the model "+model+ " created using "+algorithm.getName()+" algorithm");
				console.printTextLn("Please wait while the trace is evaluated....");
				//Getting a trace iterator
				ITraceIterator 	traceIterator = lttngSyscallReader.getTraceIterator(new File(tracePath));
				Results results=algorithm.test(traceIterator,model , Configuration.connection, settings);
				
				Double anomCount=modelsAndAnomalyCount.get(model);
				if (results.getAnomaly()==true)
								if (anomCount==null)
									modelsAndAnomalyCount.put(model, 1.0);
								else
									modelsAndAnomalyCount.put(model, ++anomCount);
				
				modelsAndResults.put(model, results);
				
				
				if (isTrainAndEval){//if it is both training and evaluation, then first train it
									// we are nit passing settings here because the assumption is that 
									// database has already been created and settings for test are only passed to this threa
									// in the constructor. Also, this will always be a last trace, and new db is false
					
					console.printTextLn("Now taking the trace to update the model "+model+ " via  "+algorithm.getName()+" algorithm");
					console.printTextLn("Please wait while the model is updated....");
					traceIterator = lttngSyscallReader.getTraceIterator(new File(tracePath));
					algorithm.train(traceIterator, true, model, Configuration.connection, console, null, false);
				} 
				
				console.printTextLn("Execution  finished for "+model);
				
				LinkedList<Double> anomalies=modelsAndAnomaliesOnChart.get(model);
				if (results.getAnomaly()){
							anomalies.add(1.0);
							console.printTextLn("It is an anomaly");
							
				}
				else{
							anomalies.add(0.0);
							console.printTextLn("It is not an anomaly");
				}
				
				console.printTextLn("Plotting anomaly on the chart");
				
				if (anomalyIdx>maxPoints){
					anomalies.remove();//remove head
				
					isAnomCountThres=true;
				}else
					isAnomCountThres=false;
				
				//Convert it into a series for plotting a chart
				Double []ySeries=new Double[anomalies.size()];
			
				ySeries=anomalies.toArray(ySeries);
				
				liveXYChart.addYSeriesValues(ySeries, model);
				liveXYChart.addXSeriesValues(xVals, model);
				liveXYChart.drawChart();

			}
		
		if (isAnomCountThres)// only increment or decrement this once for all algorithms
			anomalyIdx--;
		else
			anomalyIdx++;
		
		String traceName=tracePath.substring(tracePath.lastIndexOf(File.separator)+1, tracePath.length());
		String traceToDelete=results.addTraceResult(traceName, modelsAndResults);
		
		if (!traceToDelete.isEmpty()){
			// decrease total traces when a trace is removed from the reuslts
			totalTraces--;
			//Also decrease the count of total anoamlies for each model
			Set<String> keys= modelsAndAnomalyCount.keySet();
			Iterator<String> it=keys.iterator();
			while (it.hasNext()){
				String key=it.next();
				Double anom=modelsAndAnomalyCount.get(key);
				modelsAndAnomalyCount.put(key,--anom);;
			}
			
			String folderName=tracePath.substring(0,tracePath.lastIndexOf(File.separator));
		//	deleteLTTngTrace(new File(folderName+traceName));
		}
	}
	
	
	/**
	 * Initialises the connection and chart
	 * @throws TotalADSNetException 
	 */
	private void initialise() throws TotalADSNetException{
		console.clearText();
		console.printTextLn("Starting SSH....waiting for response from the remote host");
		//Connecting to SSH
		if (!pathToPrivateKey.isEmpty())
			ssh.openSSHConnectionUsingPrivateKey(userAtHost, pathToPrivateKey, port, console,snapshotDuration);
		else
			ssh.openSSHConnectionUsingPassword(userAtHost, password, port, console,snapshotDuration);
		
		liveXYChart.clearChart();
		liveXYChart.inititaliseSeries(seriesNames);
		liveXYChart.setXRange(1, 10,100);
		liveXYChart.setYRange(-1, 2);
		liveXYChart.drawChart();
		
		
		//
		//Run on main GUI thread
		//
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				btnDetails.setEnabled(false);
			}
		});
	}
	
	 /**
	  * Deletes all the contents of a folder. This function is used to delete an LTTng trace,
	  * which is a collection of files in a folder 
	  * @param folder Folder name
	  */
	 private  void deleteLTTngTrace(File folder) {
		    File[] files = folder.listFiles();
		    if(files!=null) { 
		        for(File f: files) {
		            if(f.isDirectory()) {
		                deleteLTTngTrace(folder);
		            } else {
		                f.delete();
		            }
		        }
		    }
		    folder.delete();
		}
	 

	
}
