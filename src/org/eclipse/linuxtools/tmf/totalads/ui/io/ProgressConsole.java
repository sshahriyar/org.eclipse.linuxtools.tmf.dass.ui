/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **/
package org.eclipse.linuxtools.tmf.totalads.ui.io;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.linuxtools.tmf.totalads.algorithms.IAlgorithmOutObserver;
import org.eclipse.linuxtools.tmf.totalads.ui.live.BackgroundLiveMonitor;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsole;
/**
 * Class to display output to the Eclipse console
 * @author <p> Syed shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class ProgressConsole implements IAlgorithmOutObserver {

	private MessageConsoleStream out;
	private MessageConsole myConsole;
	/**
	 *
	 * Constructor
	 *
	 */
	public ProgressConsole(String consoleName) {
		myConsole = findConsole(consoleName);
		out = myConsole.newMessageStream();

	}
	/**
	 * Prints a message with a new line
	 * @param message Message as a String object
	 */
	public void println(String message){
		out.println(message);
	}
	/**
	 * Prints a message
	 * @param message Message as a String object
	 */
	public void print(String message){
		out.print(message);
	}

  /**
   * Gets the console object
   * @param name Name of the console
   * @return
   */
  private MessageConsole findConsole(String name) {

	  	ConsolePlugin plugin = org.eclipse.ui.console.ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();

		for (int i = 0; i < existing.length; i++) {
            if (name.equals(existing[i].getName())) {
                return (org.eclipse.ui.console.MessageConsole) existing[i];
            }
        }

		  //No console found, so create a new one
	      org.eclipse.ui.console.MessageConsole console = new org.eclipse.ui.console.MessageConsole(name, null);
	      conMan.addConsoles(new IConsole[]{myConsole});
	      return console;
	   }

  /**
   * Closes the console streaming
   * @throws IOException
   */
  public void closeConsole() {

		try {
			out.close();
		} catch (IOException ex) {
			Logger.getLogger(BackgroundLiveMonitor.class.getName()).log(Level.SEVERE,null, ex);
			ex.printStackTrace();
		}
   }
  /**
   * Clears the console
   */
  public void clearConsole(){
	  myConsole.clearConsole();
  }
  /**
   * Implements a method of {@link IAlgorithmOutObserver}
   */
   @Override
   public void updateOutput(String message) {
		print(message);

   }

}
