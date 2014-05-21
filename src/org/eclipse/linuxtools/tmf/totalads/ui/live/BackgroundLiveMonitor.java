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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmUtility;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.Results;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmOutStream;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMSFactory;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSNetException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.linuxtools.tmf.totalads.ui.io.ProgressConsole;
import org.eclipse.linuxtools.tmf.totalads.ui.results.ResultsAndFeedback;
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
	private String fUserAtHost;
	private String fPassword;
	private String fSudoPassword;
	private String fPathToPrivateKey;
	private Integer fPort;
	private AlgorithmOutStream fOutStreamAlg;
	private ProgressConsole fProgConsole;
	private SSHConnector fSsh;
	private Integer fSnapshotDuration;// In Seconds
	private Integer fIntervalsBetweenSnapshots; // In Minutes
	private Button fBtnStart;
  	private Button fBtnStop;
  	private HashSet<String> fModelsList;
  	private ResultsAndFeedback fResults;
  	private MessageBox fMsgBox;
  	private HashMap<String,LinkedList<Double>> fModelsAndAnomaliesOnChart;
  	private Integer fAnomalyIdx=0;
	private Integer fMaxPoints;
	private LiveXYChart fLiveXYChart;
	private String []fSeriesNames;
	//private AlgorithmFactory algFac;
	private ITraceTypeReader fLttngSyscallReader;
	private LinkedList<Double> fXSeries;
	private boolean fIsTrainAndEval;
	private Integer fTotalTraces;
	private String fDirectoryToStoreTraces;
	private volatile boolean fIsExecuting=true;
	private HashMap <String, Double> fModelsAndAnomalyCount;

	/**
	 * Constructor
	 * @param userAtHost User and host name (user@host)
	 * @param password Password
	 * @param sudoPassowrd Sudo Password
	 * @param pathToPrivateKey Private key
	 * @param port Port
	 * @param snapshotDuration Snapshot duration
	 * @param intervalBetweenSnapshots Snapshot Interval
	 * @param btnStart Start button
	 * @param btnStop Stop button
	 * @param modelsList Models' list
	 * @param results Results's object
	 * @param xyChart Chart object
	 * @param traceStorageDirectory Directory to store traces
	 * @param isTrainEval True for training and testing simultaneously, and false for only testing
	 */
	public BackgroundLiveMonitor(String userAtHost, String password,String sudoPassowrd, String pathToPrivateKey,
			Integer port, Integer snapshotDuration,Integer intervalBetweenSnapshots, Button btnStart,
		  	Button btnStop,HashSet<String> modelsList,
		  	ResultsAndFeedback results,	LiveXYChart xyChart, String traceStorageDirectory, Boolean isTrainEval ) {

		this.fUserAtHost=userAtHost;
		this.fPassword=password;
		this.fSudoPassword=sudoPassowrd;
		this.fPathToPrivateKey=pathToPrivateKey;
		this.fPort=port;
		this.fSnapshotDuration=snapshotDuration;
		this.fIntervalsBetweenSnapshots=intervalBetweenSnapshots;
		this.fBtnStart=btnStart;
		this.fBtnStop=btnStop;
		this.fModelsList=modelsList;
		this.fResults=results;
		this.fLiveXYChart=xyChart;
		this.fDirectoryToStoreTraces=traceStorageDirectory;
		this.fIsTrainAndEval=isTrainEval;
		//Setting up the maximum threshold beyond which the number of points would start
		// getting reduced on the chart. In other words the chart would move right
		this.fMaxPoints=30;

		fProgConsole=new ProgressConsole("Live Monitor");
		fOutStreamAlg=new AlgorithmOutStream();
		fOutStreamAlg.addObserver(fProgConsole);


		fModelsAndAnomaliesOnChart=new HashMap<>();
		fModelsAndAnomalyCount=new HashMap<>();

		fSeriesNames=new String[modelsList.size()];
		int idx=0;

		Iterator<String> it=modelsList.iterator();
		while (it.hasNext()){
			String model=it.next();
			LinkedList<Double> anomalyCounts=new LinkedList<>();
			this.fModelsAndAnomaliesOnChart.put(model, anomalyCounts);
			fSeriesNames[idx]=model;
			fModelsAndAnomalyCount.put(fSeriesNames[idx], 0.0);
			idx++;

		}

		this.fResults.registerAllModelNames(fSeriesNames);
		this.fResults.setMaxAllowableTrace(30);

		fSsh = new SSHConnector();
		fMsgBox=new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,SWT.ICON_ERROR|SWT.OK);

		//algFac=AlgorithmFactory.getInstance();
		fLttngSyscallReader= TraceTypeFactory.getInstance().getCTFKernelorUserReader(true);

		fXSeries=new LinkedList<>();
		fTotalTraces=0;


	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run(){
		String exception="";
		try{
			initialise();

			while (fIsExecuting){//keep running untill the stop function is called
						//Getting a trace from remote system
					String tracePath=fSsh.collectATrace(fSudoPassword,fDirectoryToStoreTraces);
					//String tracePath=fSsh.getTrace();
					//System.out.println(fAnomalyIdx + " "+fMaxPoints);
					if (fXSeries.isEmpty()) {
                        fXSeries.add(0.0);
                    } else{
						//System.out.println (fAnomalyIdx + " "+fMaxPoints);
						if (fAnomalyIdx>fMaxPoints)
                         {
                            fXSeries.remove();// remove first point on the series
                        }
						Double interval=1.0;
						if (fIntervalsBetweenSnapshots.doubleValue()!=0) {
                            interval=fIntervalsBetweenSnapshots.doubleValue();
                        }
						fXSeries.add(interval+fXSeries.getLast());
						fLiveXYChart.setXRange(fXSeries.getFirst().intValue(), fXSeries.getLast().intValue(),100);
					}
					//Convert it into a series for plotting a chart
					Double []xVals=new Double[fXSeries.size()];
					xVals=fXSeries.toArray(xVals);

					processTraceOnModels(tracePath, xVals);

					fTotalTraces++;
					fResults.setTotalTraceCount(fTotalTraces.toString());
					//calculate percentages
					HashMap <String, Double> modelsAnoms=new HashMap<>();
					for (int i=0;i<this.fSeriesNames.length;i++){
						Double anoms=(fModelsAndAnomalyCount.get(fSeriesNames[i])/fTotalTraces)*100;
						modelsAnoms.put(fSeriesNames[i],anoms);

					}
					fResults.setTotalAnomalyCount(modelsAnoms);


					// Check if stop has been requested
					if (fIsExecuting==false)
                     {
                        break;// break out of the loop
                    }

					// if there is more than 0 interval duration
					if (fIntervalsBetweenSnapshots >10){
						fOutStreamAlg.addOutputEvent("Pausing for "+fIntervalsBetweenSnapshots+ " min to restart tracing on"
										+ " the remote host "+fUserAtHost.replaceAll(".*@","" ));
						try{
							TimeUnit.MINUTES.sleep(fIntervalsBetweenSnapshots);

						} catch (InterruptedException ex){}
					}

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


		} catch (TotalADSGeneralException ex) {
			exception=ex.getMessage();
			Logger.getLogger(BackgroundLiveMonitor.class.getName()).log(Level.SEVERE,exception, ex);

		}catch (Exception ex){
			if (ex.getMessage()!=null) {
                exception=ex.getMessage();
            } else {
                exception="Unknown error: check LTTng on the remote system, log might be helpful too";
            }

			Logger.getLogger(BackgroundLiveMonitor.class.getName()).log(Level.SEVERE,exception, ex);
			ex.printStackTrace();
			// An exception could be thrown due to unavailability of the db,
			// make sure that the connection is not lost
			DBMSFactory.INSTANCE.verifyConnection();
			// We don't have to worry about exceptions here as the above function handles all the exceptions
			// and just returns a message. This function also initializes connection info  to a correct value
			// We cannot write above function under ConnectinException block because such exception is never thrown
			// and Eclipse starts throwing errors

		}
		finally{
			fSsh.close();

			fProgConsole.println("SSH connection terminated");
			fProgConsole.println("Monitor stopped");
			final String err=exception;
			//
			//Run on main GUI thread
			//
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {

					if (err !=null && !err.isEmpty()){
						fMsgBox.setMessage(err);
						fMsgBox.open();
					}

					fBtnStop.setEnabled(false);
					fBtnStart.setEnabled(true);


				}
			});
			fProgConsole.closeConsole();

		}

	}

	/**
	 * Stops the thread
	 */
	public void stopMonitoring(){
		fIsExecuting=false;
		fProgConsole.println("Stopping monitor");
		fProgConsole.println("It could take few minutes to safely stop the monitor");
		fProgConsole.println("Please wait...");
	}

	/**
	 * This is a name function that evaluates traces on models and trains the model on those traces if needed
	 * @param tracePath
	 * @param xVals
	 * @throws TotalADSReaderException
	 * @throws TotalADSDBMSException
	 * @throws TotalADSGeneralException
	 */
	private void processTraceOnModels(String tracePath, Double []xVals) throws TotalADSReaderException, TotalADSGeneralException, TotalADSDBMSException{

		HashMap<String, Results> modelsAndResults=new HashMap<>();
		boolean isAnomCountThres=false;
		Iterator<String> it=fModelsList.iterator();
		while (it.hasNext()){

				String model=it.next();


				IDetectionAlgorithm algorithm=AlgorithmUtility.getAlgorithmFromModelName(model);

				fProgConsole.println("Evaluting trace on the model "+model+ " created using "+algorithm.getName()+" algorithm");
				fProgConsole.println("Please wait while the trace is evaluated....");
				Results results;
				//Getting a trace iterator
				try (ITraceIterator traceIterator =
				        fLttngSyscallReader.getTraceIterator(new File(tracePath))){
				//Testing it
				 results=algorithm.test(traceIterator,model , DBMSFactory.INSTANCE.getDataAccessObject(), fOutStreamAlg);
				}

				if (results==null) {
                    results=new Results();
                }

				Double anomCount=fModelsAndAnomalyCount.get(model);
				if (results.getAnomaly()==null || results.getAnomaly()==true) {
                    if (anomCount==null) {
                        fModelsAndAnomalyCount.put(model, 1.0);
                    } else {
                        fModelsAndAnomalyCount.put(model, ++anomCount);
                    }
                }

				modelsAndResults.put(model, results);


				if (fIsTrainAndEval){//if it is both training and evaluation


					fProgConsole.println("Now taking the trace to update the model "+model+ " via  "+algorithm.getName()+" algorithm");
					fProgConsole.println("Please wait while the model is updated....");
					try (ITraceIterator traceIterator =
					        fLttngSyscallReader.getTraceIterator(new File(tracePath)) ){

					    algorithm.train(traceIterator, true, model,
					            DBMSFactory.INSTANCE.getDataAccessObject(), fOutStreamAlg);
					}
				}

				fProgConsole.println("Execution  finished for "+model);


				LinkedList<Double> anomalies=fModelsAndAnomaliesOnChart.get(model);
				if (results.getAnomaly()==null || results.getAnomaly()){
							anomalies.add(1.0);
							fProgConsole.println("It is an anomaly");

				}
				else{
							anomalies.add(0.0);
							fProgConsole.println("It is not an anomaly");
				}

				fProgConsole.println("Plotting anomaly on the chart");

				if (fAnomalyIdx>fMaxPoints){
					anomalies.remove();//remove head

					isAnomCountThres=true;
				} else {
                    isAnomCountThres=false;
                }

				//Convert it into a series for plotting a chart
				Double []ySeries=new Double[anomalies.size()];

				ySeries=anomalies.toArray(ySeries);


				fLiveXYChart.addYSeriesValues(ySeries, model);
				fLiveXYChart.addXSeriesValues(xVals, model);

				fLiveXYChart.drawChart();

			}

		if (isAnomCountThres) {
            fAnomalyIdx--;
        } else {
            fAnomalyIdx++;
        }

		String traceName=tracePath.substring(tracePath.lastIndexOf(File.separator)+1, tracePath.length());
		String traceToDelete=fResults.addTraceResult(traceName, modelsAndResults);

		if (!traceToDelete.isEmpty()){
			// decrease total traces when a trace is removed from the fResults
			fTotalTraces--;
			//Also decrease the count of total anomalies for each model
			Set<String> keys= fModelsAndAnomalyCount.keySet();
			Iterator<String> iter=keys.iterator();
			while (iter.hasNext()){
				String key=iter.next();
				Double anom=fModelsAndAnomalyCount.get(key);
				fModelsAndAnomalyCount.put(key,--anom);
			}
		//This code will be enabled if it is necessary to delete traces on the folder after
		// piling up a certain number of them
		//	String folderName=tracePath.substring(0,tracePath.lastIndexOf(File.separator));
		//	deleteLTTngTrace(new File(folderName+traceName));
		}
	}


	/**
	 * Initialises the connection and chart
	 * @throws TotalADSNetException
	 */
	private void initialise() throws TotalADSNetException{
		fProgConsole.clearConsole();
		fProgConsole.println("Starting SSH....waiting for response from the remote host");
		//Connecting to SSH
		if (!fPathToPrivateKey.isEmpty()) {
            fSsh.openSSHConnectionUsingPrivateKey(fUserAtHost, fPathToPrivateKey, fPort,fProgConsole,fSnapshotDuration);
        } else {
            fSsh.openSSHConnectionUsingPassword(fUserAtHost, fPassword, fPort, fProgConsole,fSnapshotDuration);
        }

		fLiveXYChart.clearChart();
		fLiveXYChart.inititaliseSeries(fSeriesNames);
		fLiveXYChart.setXRange(1, 10,100);
		fLiveXYChart.setYRange(-1, 2);
		fLiveXYChart.drawChart();


	}

	 /**
	  * Deletes all the contents of a folder. This function is used to delete an LTTng trace,
	  * which is a collection of files in a folder
	  * @param folder Folder name

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
     */


}
