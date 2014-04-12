package org.eclipse.linuxtools.tmf.totalads.ui.live;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.ResultsAndFeedback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
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
	
	public BackgroundLiveMonitor(String userAtHost, String password,String sudoPassowrd, String pathToPrivateKey,
			Integer port, Integer snapshotDuration,Integer intervalBetweenSnapshots, Button btnStart,
		  	Button btnStop, Button btnDetails,HashMap<String,String[]> modelsAndSettings,
		  	ResultsAndFeedback results,	ProgressConsole console ) {
		
		this.userAtHost=userAtHost;
		this.password=password;
		this.sudoPassword=sudoPassowrd;
		this.pathToPrivateKey=pathToPrivateKey;
		this.port=port;
		this.console=console;
		this.snapshotDuration=snapshotDuration;
		this.intervalsBetweenSnapshots=intervalBetweenSnapshots;
		this.btnStart=btnStart;
		this.btnStop=btnStop;
		this.btnDetails=btnDetails;
		this.modelsAndSettings=modelsAndSettings;
		this.results=results;
		ssh = new SSHConnector();
		msgBox=new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,SWT.ICON_ERROR|SWT.OK);
		
	}

	/**
	* Overridden function to run a thread
	*/
	@Override
	public void run(){
		try{
			
			if (!pathToPrivateKey.isEmpty())
				ssh.openSSHConnectionUsingPrivateKey(userAtHost, pathToPrivateKey, port, console,snapshotDuration);
			else
				ssh.openSSHConnectionUsingPassword(userAtHost, password, port, console,snapshotDuration);
			
			ssh.collectATrace(sudoPassword);
			String tracePath=ssh.getTrace();
			
			ITraceTypeReader lttngSyscallReader= TraceTypeFactory.getInstance().getCTFKernelorUserReader(true);
			ITraceIterator 	traceIterator = lttngSyscallReader.getTraceIterator(new File(tracePath));
		
			AlgorithmFactory algFac=AlgorithmFactory.getInstance();
			
			for (Map.Entry<String, String[]> models:modelsAndSettings.entrySet()){
				String model=models.getKey();
				String []settings=models.getValue();
				IDetectionAlgorithm algorithm=algFac.getAlgorithmByAcronym(model.split("_")[1]);
				Results results=algorithm.test(traceIterator,model , Configuration.connection, settings);
				
			}
			
			try{
				TimeUnit.MINUTES.sleep(intervalsBetweenSnapshots);
			} catch (InterruptedException ex){}
			
			
			
		} 
		catch (TotalADSNetException ex){
			msgBox.setMessage(ex.getMessage());
			msgBox.open();
		} catch (TotalADSReaderException ex) {
			msgBox.setMessage(ex.getMessage());
			msgBox.open();
	
		} catch (TotalADSDBMSException ex) {
			msgBox.setMessage(ex.getMessage());
			msgBox.open();
	
		} catch (TotalADSUIException ex) {
			msgBox.setMessage(ex.getMessage());
			msgBox.open();
	
		} finally{
			ssh.close();
		}
		
	}
	
	public void stopMonitoring(){
		
	}

}
