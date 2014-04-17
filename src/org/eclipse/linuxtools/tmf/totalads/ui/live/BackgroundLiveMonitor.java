package org.eclipse.linuxtools.tmf.totalads.ui.live;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
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
import org.eclipse.linuxtools.tmf.totalads.ui.ProgressConsole;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.BackgroundTesting;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.ResultsAndFeedback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

public class BackgroundLiveMonitor extends Thread {
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
  	private HashMap<String,LinkedList<Double>> modelsAndAnomalyCounts;
  	private Integer anomalyIdx=0;
	private Integer maxPoints;
	private LiveXYChart liveXYChart;
	private String []seriesNames;
	private AlgorithmFactory algFac;
	private ITraceTypeReader lttngSyscallReader;
	private LinkedList<Double> xSeries;
	private boolean isTrainAndEval;
	private volatile boolean isExecuting=true;
	
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
		this.maxPoints=intervalBetweenSnapshots*20;
		this.isTrainAndEval=isTrainEval;
		
		LinkedList<Double> anomalyCounts=new LinkedList<Double>();
		modelsAndAnomalyCounts=new HashMap<String, LinkedList<Double>>();
		
		seriesNames=new String[modelsAndSettings.size()];
		int idx=0;
		
		for (Map.Entry<String, String[]> models:modelsAndSettings.entrySet()){
			this.modelsAndAnomalyCounts.put(models.getKey(), anomalyCounts);
			seriesNames[idx++]=models.getKey();
		}
		
		
		ssh = new SSHConnector();
		msgBox=new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,SWT.ICON_ERROR|SWT.OK);
		
		algFac=AlgorithmFactory.getInstance();
		lttngSyscallReader= TraceTypeFactory.getInstance().getCTFKernelorUserReader(true);
		
		xSeries=new LinkedList<Double>();
		
		//for (idx=0;idx < maxPoints;idx++)
		//	xSeries.add( intervalBetweenSnapshots.doubleValue()*idx);
		
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
					ssh.collectATrace(sudoPassword);
					String tracePath=ssh.getTrace();
					
					if (xSeries.isEmpty()){
						xSeries.add(0.0);
						//liveXYChart.setXRange(0, 1);
					}
					else{
						if (anomalyIdx>maxPoints)
							xSeries.remove();
						xSeries.add(intervalsBetweenSnapshots.doubleValue()+xSeries.getLast());
						liveXYChart.setXRange(xSeries.getFirst().intValue(), xSeries.getLast().intValue(),
								100);
					}
					//Convert it into a series for plotting a chart
					Double []xVals=new Double[xSeries.size()];
					xVals=xSeries.toArray(xVals);
					
					  
					for (Map.Entry<String, String[]> modAndSettings:modelsAndSettings.entrySet()){
						
							String model=modAndSettings.getKey();
							String []settings=modAndSettings.getValue();
							
							IDetectionAlgorithm algorithm=algFac.getAlgorithmByAcronym(model.split("_")[1]);
							
							
							
							console.printTextLn("Evaluting trace on the model "+model+ " created using "+algorithm.getName()+" algorithm");
							console.printTextLn("Please wait while the trace is evaluated....");
							//Getting a trace iterator
							ITraceIterator 	traceIterator = lttngSyscallReader.getTraceIterator(new File(tracePath));
							Results results=algorithm.test(traceIterator,model , Configuration.connection, settings);
						
							//put result if anomaly  into an array list for a key 
							// by the name of a model in the map
							// in the result and feedback class
							// in the result and feedback class set upper limit to remove the first one automatically
							//
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
							
							LinkedList<Double> anomalies=modelsAndAnomalyCounts.get(model);
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
								xSeries.remove();// remove head
								anomalyIdx--;
							}else
								anomalyIdx++;
							
							//Convert it into a series for plotting a chart
							Double []ySeries=new Double[anomalies.size()];
						
							ySeries=anomalies.toArray(ySeries);
							
							liveXYChart.addYSeriesValues(ySeries, model);
							liveXYChart.addXSeriesValues(xVals,model);
							liveXYChart.drawChart();
			
						}
			
						
						
						
						//ssh.close();
						
						//
						//Run on main GUI thread
						//
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								btnDetails.setEnabled(true);
							}
						});
						
						if (isExecuting==false)// Check if stop has been requested
							break;// break out of the loop 
						
						console.printTextLn("Pausing for "+intervalsBetweenSnapshots+ " min to restart tracing on"
										+ " the remote host "+userAtHost.replaceAll(".*@","" ));
						try{
							TimeUnit.MINUTES.sleep(intervalsBetweenSnapshots);
							
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
					
					if (!err.isEmpty()){
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
		liveXYChart.setXRange(1, 8,100);
		liveXYChart.setYRange(0, 1);
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
	
	
}
