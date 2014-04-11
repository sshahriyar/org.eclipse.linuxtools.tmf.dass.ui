package org.eclipse.linuxtools.tmf.totalads.ui.live;

import java.util.concurrent.TimeUnit;

import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSNetException;
import org.eclipse.linuxtools.tmf.totalads.ui.modeling.ProgressConsole;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

public class BackgroundLiveEvaluation extends Thread {
	private String userAtHost;
	private String password;
	private String sudoPassword;
	private String pathToPrivateKey;
	private Integer port;
	private ProgressConsole console;
	private SSHConnector ssh;
	private Integer snapshotDuration;// In Seconds
	private Integer intervalsBetweenSnapshots; // In Minutes
	
	public BackgroundLiveEvaluation(String userAtHost, String password,String sudoPassowrd, String pathToPrivateKey,
			Integer port, Integer snapshotDuration,Integer intervalBetweenSnapshots,	ProgressConsole console ) {
		this.userAtHost=userAtHost;
		this.password=password;
		this.sudoPassword=sudoPassowrd;
		this.pathToPrivateKey=pathToPrivateKey;
		this.port=port;
		this.console=console;
		this.snapshotDuration=snapshotDuration;
		this.intervalsBetweenSnapshots=intervalBetweenSnapshots;
		ssh = new SSHConnector();
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
			ssh.getTrace();
			
			try{
				TimeUnit.MINUTES.sleep(intervalsBetweenSnapshots);
			} catch (InterruptedException ex){}
			
			ssh.close();
			
		} catch (TotalADSNetException ex){
			MessageBox msgBox=new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,SWT.ICON_ERROR|SWT.OK);
			msgBox.setMessage(ex.getMessage());
			msgBox.open();
		}
		
	}

}
