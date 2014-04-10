package org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.live;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import org.eclipse.linuxtools.tmf.totalads.ui.modeling.ProgressConsole;



/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
/**
 * http://www.jcraft.com/jsch/examples/Sudo.java.html
 * @author umroot
 *
 */
public class SSHConnector {
	private JSch jsch; 
	private Integer port;
	private UserInfo ui;
	private String user;
	private  String host;
	
	public SSHConnector() {
		jsch=new JSch(); 
		port =22;
	}

	public void openSSHConnection(String userAtHost,String password, Integer portToConnect, ProgressConsole console){
	try{
	       
		 if (portToConnect!=null)
			 this.port=portToConnect;
			 
		 
	     
	     user=userAtHost.substring(0, userAtHost.indexOf('@'));
	     host=userAtHost.substring(userAtHost.indexOf('@')+1);

	      
	      
	      // username and password will be given via UserInfo interface.
	      ui=new UserInfoSSH(password, console) ;
	      Session session=jsch.getSession(user, host, 22);
			 
			 session.setUserInfo(ui);
		      session.connect();
	      
		      
	       executeCommand("sudo -S -p '' lttng create ag-session\n lttng enable-event -a -k;\nlttng start;"
	       		+ "lttng stop\nlttng destroy ag-session", 	    		   
	    		    console,session,password);
	      //executeCommand("sudo -S -p '' lttng enable-event -a -k",  console,password);
	     // executeCommand("lttng start", console,session, password);
	     // executeCommand("lttng stop", console,session, password);
	    //  executeCommand("sudo -S -p  '' lttng destroy nome-session", console, session,password);

	     
	      session.disconnect();
	    
	    }
	    catch(Exception e){
	      System.out.println(e);
	    }
	}
	/**
	 * 
	 * @param command
	 * @param session
	 */
	private void executeCommand(String command, ProgressConsole console,Session session, String sudoPass){
		try{
			 
			Channel channel=session.openChannel("exec");
			((ChannelExec)channel).setCommand(command);
			  ((ChannelExec)channel).setErrStream(System.err);
		      InputStream in=channel.getInputStream();
		      OutputStream out=channel.getOutputStream();
		      
		       channel.connect();
		      out.write((sudoPass+"\n").getBytes());
		      out.flush();
		      displayStream(in, channel, console);
		      channel.disconnect();
		      
		     
	    }
	    catch(Exception e){
	    		System.out.println(e);
	    }
	}
	/**
	 * 
	 * @param in
	 * @param channel
	 * @param console
	 * @throws IOException
	 */
	private void displayStream(InputStream in, Channel channel,ProgressConsole console) throws IOException{
		
		byte[] tmp=new byte[1024];
	      while(true){
	        while(in.available()>0){
	          int i=in.read(tmp, 0, 1024);
	          	if(i<0)break;
	          		System.out.print(new String(tmp, 0, i));
	        }
	        
	        if(channel.isClosed()){
	          if(in.available()>0) continue; 
	          		System.out.println("exit-status: "+channel.getExitStatus());
	          break;
	        }
	        try{Thread.sleep(1000);}catch(Exception ee){}
	      }
	}
	
	public static void main (String args[]){
		SSHConnector ssh=new SSHConnector();
		
		ssh.openSSHConnection("umroot@localhost", "grt_654321", null, null);
	}
}
