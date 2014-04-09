/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/

package org.eclipse.linuxtools.tmf.totalads.ui.modeling;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
/**
 * This class builds a model by executing as a thread in the background.
 * It is instantiated and executed from the {@link Modeling} class.
 * @author <p>Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */

public class BackgroundModeling extends Thread{
	String trainingTraces;
	String validationTraces;
	ITraceTypeReader traceReader;
	AlgorithmModelSelector algorithmSelector;
	ProgressConsole progConsole;
	Button btnMain;
	/**
	 *  Constructor 
	 * @param trainingTraces Training traces
	 * @param validationTraces Validation trace folder
	 * @param traceReader Trace reader selected by the user
	 * @param modelSel ModelSelector object
	 * @param progConsole ProgressConsole object
	 * @param btnBuild Button to enable
	 */
	public BackgroundModeling(String trainingTraces,
				String validationTraces,ITraceTypeReader traceReader, AlgorithmModelSelector algSel
				, ProgressConsole progConsole, Button btnBuild){
		this.trainingTraces=trainingTraces;
		this.validationTraces=validationTraces;
		this.traceReader=traceReader;
		this.algorithmSelector=algSel;
		this.progConsole=progConsole;
		this.btnMain=btnBuild;
	}
	/**
	 * Implementation of the thread	
	 */
	@Override
	public void run(){
			String msg=null;
			
			try {
				
					algorithmSelector.trainAndValidateModels(trainingTraces, validationTraces, traceReader,progConsole);
							
			} 
			catch(TotalADSUIException ex){// handle UI exceptions here
				if (ex.getMessage()==null)
					msg="UI error";	
				else
					msg=ex.getMessage();
			}
			catch(TotalADSDBMSException ex){// handle DBMS exceptions here
				if (ex.getMessage()==null)
					msg="DBMS error: see log.";
				else
					msg=ex.getMessage(); 
				Logger.getLogger(BackgroundModeling.class.getName()).log(Level.WARNING,msg,ex);
				
			}
			catch(TotalADSReaderException ex){// handle Reader exceptions here
				if (ex.getMessage()==null){
					msg="Reader error: see log.";
				}
				else
					msg=ex.getMessage();  
				Logger.getLogger(BackgroundModeling.class.getName()).log(Level.WARNING,msg,ex);
				
			} 
			catch (Exception ex) { // handle all other exceptions here and log them too.
									//UI exceptions are simply notifications--no need to log them
									
				if (ex.getMessage()==null)
					msg="Severe error: see log.";	
				else
					msg=ex.getMessage();
				 //ex.printStackTrace();
				Logger.getLogger(BackgroundModeling.class.getName()).log(Level.SEVERE,msg,ex);
				// An exception could be thrown due to unavailability of the db, 
				// make sure that the connection is not lost
				Configuration.connection.connect(Configuration.host, Configuration.port);
				// We don't have to worry about exceptions here as the above function handles all the exceptions
				// and just returns a message. This function also initializes connection info to correct value
				// We cannot write above function under ConnectinException block because such exception is never thrown
				// and Eclipse starts throwing errors
			}
			finally{
				final String exception=msg;
				
				// Executing GUI elements on a main thread from another thread 
				 Display.getDefault().syncExec(new Runnable() {
					
					 @Override
					public void run() {
						if (exception!=null){ // if there has been any exception then show its message
							MessageBox msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
							           ,SWT.ICON_ERROR|SWT.OK);
							msgBox.setMessage(exception);
							msgBox.open();
							
						}
						btnMain.setEnabled(true);
						
					}
				});
				
				
			}
		}
}
