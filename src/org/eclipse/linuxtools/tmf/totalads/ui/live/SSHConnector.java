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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSNetException;
import org.eclipse.linuxtools.tmf.totalads.ui.io.ProgressConsole;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;
/**
 * This class connects to a remote system using SSH and collects an LTTng trace
 * Some of the code in this class is extracted from the following link:
 * http://www.jcraft.com/fJsch/examples/Sudo.java.html
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class SSHConnector {
	private JSch fJsch;
	private Integer fPort;
	private UserInfo fUi;
	private String fUser;
	private  String fHost;
	private Session fSession;
	private ProgressConsole fConsole;
	//private  String fTotalADSLocalDir;
	private Integer fSnapshotDuration;
	/**
	 * Constructor
	 */
	public SSHConnector() {
		fJsch=new JSch();
		//fTotalADSLocalDir=getCurrentPath()+"totalads-traces"; //$NON-NLS-1$
		//File file =new File(fTotalADSLocalDir);
		//if (!file.isDirectory())
         //{
          //  file.mkdir();
		//else // delete all its contents if it the system was close abruptly last time without stopping the thread
			//deleteFolderContents(file);
        //}
	}

	/**
	 * Returns the path of the remote LTTng trace on the local hard disk
	 * @return The path of a trace
	 *
	public String getTrace(){
		return fTotalADSLocalDir;
	}*/
	/**
	 * Opens an SSH connection using a password, executes LTTng commands on a remote system and
	 * @param userAtHost User and host (user@host)
	 * @param password Password
	 * @param portToConnect Port
	 * @param console Console object
	 * @param snapshotDurationInSeconds Snapshot duration in seconds
	 * @throws TotalADSNetException A network related exception
	 */
	public void openSSHConnectionUsingPassword(String userAtHost,String password, Integer portToConnect,
				ProgressConsole console, Integer snapshotDurationInSeconds) throws TotalADSNetException{
	try{

		 fPort=portToConnect;
		 fUser=userAtHost.substring(0, userAtHost.indexOf('@'));
	     fHost=userAtHost.substring(userAtHost.indexOf('@')+1);
      	 this.fConsole=console;
      	 this.fSnapshotDuration=snapshotDurationInSeconds;
	      // password will be given via UserInfo interface.
	     fUi=new UserInfoSSH(password, console) ;
	     fSession=fJsch.getSession(fUser, fHost, fPort);

		 fSession.setUserInfo(fUi);
		 fSession.connect();
		 console.println("SSH connection established");

	    } catch (JSchException e) {
	    	 throw new TotalADSNetException("SSH Communication Error\n"+e.getMessage());
		}
	}

	/**
	 * Connects with an SSH server using a private Key file present on the hard disk (Public name
	 * should be present on the server)
	 * @param userAtHost User and host (user@host)
     * @param pathToPrivateKey Private Key
     * @param portToConnect Port
     * @param console Console object
	 * @param snapshotDurationInSeconds Snapshot Duration
     * @throws TotalADSNetException A network related exception */
	public void openSSHConnectionUsingPrivateKey(String userAtHost,String pathToPrivateKey, Integer portToConnect,
			ProgressConsole console, Integer snapshotDurationInSeconds) throws TotalADSNetException{
		try{

			 fPort=portToConnect;
			 fUser=userAtHost.substring(0, userAtHost.indexOf('@'));
		     fHost=userAtHost.substring(userAtHost.indexOf('@')+1);
	      	 this.fConsole=console;
	      	 this.fSnapshotDuration=snapshotDurationInSeconds;

	      	 fJsch.addIdentity(pathToPrivateKey);
	         console.println("Identity added ");
		     fSession=fJsch.getSession(fUser, fHost, fPort);
		     java.util.Properties config = new java.util.Properties();
	         config.put("StrictHostKeyChecking", "no");
	         fSession.setConfig(config);
			 fSession.setUserInfo(fUi);
			 fSession.connect();
			 console.println("SSH connection established");

		    } catch (JSchException e) {
		    	 throw new TotalADSNetException("SSH Communication Error\n"+e.getMessage());
			}
		}
	/**
	 * Executes LTTng commands on a remote system and downloads the trace in a local folder
	 * @param sudoPassword  Sudo password
	 * @param traceStorageDir Trace directory
	 * @return Path of the trace
	 * @throws TotalADSNetException A netwrork related exception
	 */
	public String collectATrace( String sudoPassword, String traceStorageDir) throws TotalADSNetException{

	      String totalADSRemoteDir="/tmp/totalads";
	      String totalADSRemoteTrace=totalADSRemoteDir+"/kernel/";
	   	  String  sessionName="totalads-trace-"+getCurrentTimeStamp();
	   	  // If an exception occurs, don't execute further commands and let the exception be thrown
	   	  executeSudoCommand("sudo -S -p  '' rm -rf "+totalADSRemoteTrace, sudoPassword);
	   	  executeSudoCommand("sudo -S -p '' mkdir -p "+totalADSRemoteDir, sudoPassword);
	      executeSudoCommand("sudo -S -p '' lttng create "+sessionName+" -o "+totalADSRemoteDir,  sudoPassword);
	      executeSudoCommand("sudo -S -p '' lttng enable-event -a -k",  sudoPassword);
	      executeSudoCommand("sudo -S -p '' lttng start",  sudoPassword);

	      // Wait for these many seconds and then stop the trace
	      try{
	    	  fConsole.println("Tracing for "+fSnapshotDuration+" secs.....");
	    	  TimeUnit.SECONDS.sleep(fSnapshotDuration);
	      }catch (InterruptedException ee){}

	      executeSudoCommand("sudo -S -p '' lttng stop",  sudoPassword);
	      executeSudoCommand("sudo -S -p  '' lttng destroy "+sessionName, sudoPassword);
	      executeSudoCommand("sudo -S -p  '' chmod -R 777 "+totalADSRemoteDir, sudoPassword);

	      String trace="trace-"+getCurrentTimeStamp();
	      File localDir=new File(traceStorageDir+File.separator+trace);
	      localDir.mkdir();

	      downloadTrace(fSession,totalADSRemoteTrace , localDir.getPath());
	      executeSudoCommand("sudo -S -p  '' rm -rf "+totalADSRemoteTrace, sudoPassword);

	      return localDir.getPath();
		//return "/home/shary/totalads-attacks-normal-trace/new_totaladstraces/trace-2014-04-22-23-42";
	   	}

	 /**
	  * Executes a sudo (root) command
	  * @param command
	  * @param sudoPass
	 *
	  */
	private void executeSudoCommand(String command, String sudoPass)throws TotalADSNetException {
		Channel channel=null;
		try{

			  channel=fSession.openChannel("exec");
			 ((ChannelExec)channel).setCommand(command);
			  ((ChannelExec)channel).setErrStream(System.err);
		      InputStream in=channel.getInputStream();
		      OutputStream out=channel.getOutputStream();

		      channel.connect();
		      out.write((sudoPass+"\n").getBytes());
		      out.flush();
		      displayStream(in, channel);


	    }
		 catch(IOException e){
	    		fConsole.println("Error:"+e.getMessage());
	    		throw new TotalADSNetException(e);// Don't continue further
	    }
	    catch(JSchException e){
	    		fConsole.println("Error: "+e.getMessage());
	    		throw new TotalADSNetException(e);// Don't continue further
	    }finally{
	    	if  (channel!=null) {
                channel.disconnect();
            }

	    }
	}

	/**
	 * Display the output of a command on a remote system
	 * @param in
	 * @param channel
	 * @throws IOException
	 */
	private void displayStream(InputStream in, Channel channel) throws IOException{

		byte[] tmp=new byte[1024];
	      while(true){
	        while(in.available()>0){
	          int i=in.read(tmp, 0, 1024);
	          	if(i<0) {
                    break;
                }
	          		//System.out.print(new String(tmp, 0, i));
	          		fConsole.print(new String(tmp, 0, i));
	        }

	        if(channel.isClosed()){
	          if(in.available()>0) {
                continue;
            }
	          		//System.out.println("exit-status: "+channel.getExitStatus());
	          		fConsole.println("exit-status: "+channel.getExitStatus());
	          break;
	        }
	        try{
	        	Thread.sleep(1000); // Wait for some time to get more data over network stream
	        	}catch(Exception ex){}
	      }
	}

	/**
	 * This functions downloads the trace collected at the remote system
	 * @param fSession
	 * @param remoteFolder
	 * @throws IOException
	 */
	private void downloadTrace(Session session, String remoteFolder, String localDownloadFolder) throws TotalADSNetException {
		ChannelSftp sftpChannel=null;
		  try{

				sftpChannel = (ChannelSftp) session.openChannel("sftp");
				sftpChannel.connect();
				sftpChannel.cd(remoteFolder);
			    java.util.Vector<ChannelSftp.LsEntry> list= sftpChannel.ls("*");

		        for (ChannelSftp.LsEntry entry : list){
		        	fConsole.println("Processing remote "+ entry.getFilename()); // actually downloading

			        sftpChannel.get(entry.getFilename(),localDownloadFolder+File.separator+entry.getFilename());
		        }

			}
		   catch(SftpException e){
		 		fConsole.println("Error: "+e.getCause().getMessage()); // Exception printed
		  		throw new TotalADSNetException(e);// Don't continue further
		   }
		   catch(JSchException e){
		   		fConsole.println("Error: "+e.getCause().getMessage()); // Exception printed
		   		throw new TotalADSNetException(e);// Don't continue further
		   }
		    finally{
		   		if (sftpChannel!=null) {
                    sftpChannel.exit();
                }
		   	}

	}
	 /**
	  * Closes the SSH connection and clears the trace from the local drive
	  */
	 public void close(){
		 //deleteFolderContents(new File(this.totalADSLocalDir));
		 fSession.disconnect();
	 }


	/**
	 * 	 Get current time stamp
	 */
	private static String getCurrentTimeStamp(){
	 		  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm"); //$NON-NLS-1$
			   //get current date time with Date()
			   Date date = new Date();
			    return dateFormat.format(date);

	}


}
