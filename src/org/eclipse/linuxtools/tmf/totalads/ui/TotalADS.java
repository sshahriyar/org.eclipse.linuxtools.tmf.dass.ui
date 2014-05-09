/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.IDBMS;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.Diagnosis;
import org.eclipse.linuxtools.tmf.totalads.ui.live.LiveMonitor;
import org.eclipse.linuxtools.tmf.totalads.ui.modeling.Modeling;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.MessageBox;
//import org.eclipse.swt.widgets.Table;
//import org.eclipse.swt.widgets.Shell;

//import org.eclipse.swt.custom.TableTree;
/**
 * This is the main class for intializing GUI elements. It instantiates two  classes
 *  {@link LiveMonitor} and {@link Modeling} which in turn further instantiate different components
 *  of GUI elements in TotalADS
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */

public class TotalADS  {
	private Modeling modeling;
	private Diagnosis diagnosis;
	private LiveMonitor liveDiagnosis;
	private CTabFolder tabFolderTotalADS;
	private Handler handler;
	private AlgorithmFactory algFactory;
	private TraceTypeFactory trcTypeFactory;
	/**
	 * Constructor: creates the composite.
	 * @param parent Parent composite
	 * @param style  SWT style
	 */
	public TotalADS(Composite parent, int style) {
		
	  try{
		    	Configuration.connection=new IDBMS();
			//	Configuration.connection.connect(Configuration.host, Configuration.port, "u","p");
				String error=Configuration.connection.connect(Configuration.host, Configuration.port);
		  	
			if (!error.isEmpty()){
					MessageBox msg=new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),SWT.ICON_ERROR);
				    msg.setMessage(error);
				    msg.open();
			}	    
		  
			
			
			algFactory= AlgorithmFactory.getInstance();
			algFactory.initialize();
		
			trcTypeFactory=TraceTypeFactory.getInstance();
			trcTypeFactory.initialize();
			// Intialize the logger
			handler=null;
			handler= new  FileHandler(Configuration.getCurrentPath()+"totalads_log.xml");
            Logger.getLogger("").addHandler(handler);
			//super(parent, style);
            // Intialize the parent composite GUI Layout
			parent.setLayout(new GridLayout(2,false));
			
			//leftPane(parent);
			
			tabFolderTotalADS = new CTabFolder(parent, SWT.BORDER);
			tabFolderTotalADS.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			gridData.horizontalSpan=1;
			tabFolderTotalADS.setLayoutData(gridData);
			
			
				
			diagnosis=new Diagnosis(tabFolderTotalADS);
			modeling =new Modeling(tabFolderTotalADS);
			liveDiagnosis=new LiveMonitor(tabFolderTotalADS);
			
			////////////*********************************
			
			
		
	   } catch (Exception ex) { // capture all the exceptions here, which are missed by Diagnois and Modeling classes
			
		   MessageBox msg=new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),SWT.ICON_ERROR);
		   if (ex.getMessage()!=null){
		      msg.setMessage(ex.getMessage());
		      msg.open();
		   }
		   Logger.getLogger(TotalADS.class.getName()).log(Level.SEVERE,null,ex);
		}

	}
	/**
	 * Sets focus on TotalADS
	 */
	public void setFocus(){
		tabFolderTotalADS.setFocus();
	}
	/*
	 	 
	private void leftPane(Composite parent){
			
		Composite compLeftPane=new Composite(parent,SWT.BORDER);
		GridData gridData=new GridData(SWT.LEFT, SWT.FILL, false, true);
		gridData.horizontalSpan=1;
		compLeftPane.setLayoutData(gridData);
		compLeftPane.setLayout(new GridLayout(1,false));
		
		SystemController sysController=new SystemController(compLeftPane);
		TracingTypeSelector traceTypeSelector=new TracingTypeSelector(compLeftPane);
		
		
	}
*/
	/**
	 * Gets notifed from the {@link TMFTotalADSView} about trace selection
	 * @param traceBuffer
	 * @param tracePath
	 * @param traceTypeName
	 */
	public void notifyOnTraceSelection(String tracePath, String traceTypeName){
		diagnosis.updateOnTraceSelection(tracePath, traceTypeName);
	}
	/**
	 * Destroys the instance with final processing
	 */
	public void destroy(){
		this.liveDiagnosis.destroy();
	}
// For testing
/*
  public static void main(String[] args) {
	        Display display = new Display();
	        org.eclipse.swt.widgets.Shell shell= new org.eclipse.swt.widgets.Shell(display);
	        //shell.setText("Center");
	       // shell.setSize(250, 200);
	       // shell.setLayout(new GridLayout(3,false));
	       //center(shell);
	        TotalADS det=new TotalADS(shell, SWT.BORDER);
	      
	        /// centre
	        org.eclipse.swt.graphics.Rectangle bds = shell.getDisplay().getBounds();

	        org.eclipse.swt.graphics.Point p = shell.getSize();

	        int nLeft = (bds.width - p.x) / 2;
	        int nTop = (bds.height - p.y) / 2;

	        shell.setBounds(nLeft, nTop, p.x, p.y);
	        
	        
	        //det.pack();
	        shell.pack();
	        shell.open();
	        

	        while (!shell.isDisposed()) {
	          if (!display.readAndDispatch()) {
	            display.sleep();
	          }
	        }
	        
	        display.dispose();
	    }*/
}
