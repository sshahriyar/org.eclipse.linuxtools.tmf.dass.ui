/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.live;

import org.eclipse.linuxtools.tmf.totalads.ui.modeling.ProgressConsole;

import com.jcraft.jsch.*;
/**
 * This class implements the UserInfo Interface in JSch package. This interface
 * is necessary to implment in order to connect to an SSH server
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class UserInfoSSH implements UserInfo {
		private String password;
		private ProgressConsole progConsole;
		
		/**
		 * Constructor
		 */
		public UserInfoSSH(String password, ProgressConsole console) {
			this.password=password;
			this.progConsole=console;
		}
		
		
		@Override
		public void showMessage(String msg) {
			progConsole.printTextLn(msg);
			//System.out.println(msg);
		}
		
		@Override
		public boolean promptYesNo(String arg0) {
					return true;
		}
		
		@Override
		public boolean promptPassword(String arg0) {
			
			return true;
		}
		
		@Override
		public boolean promptPassphrase(String arg0) {
			
			return true;
		}
		
		@Override
		public String getPassword() {
			
			return password;
		}
		
		@Override
		public String getPassphrase() {
			
			return null;
		}
	

}
