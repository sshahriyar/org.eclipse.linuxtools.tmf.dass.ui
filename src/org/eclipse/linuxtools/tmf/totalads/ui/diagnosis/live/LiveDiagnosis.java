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


import java.awt.event.MouseAdapter;

import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.core.TMFTotalADSView;
import org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders.CTFLTTngSysCallTraceReader;
import org.eclipse.linuxtools.tmf.totalads.ui.TotalADS;
import org.eclipse.linuxtools.tmf.totalads.ui.TraceBrowser;
import org.eclipse.linuxtools.tmf.totalads.ui.TracingTypeSelector;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.ModelLoader;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.ResultsAndFeedback;
import org.eclipse.linuxtools.tmf.totalads.ui.modeling.ProgressConsole;
import org.eclipse.linuxtools.tmf.totalads.ui.modeling.StatusBar;
import org.eclipse.linuxtools.tmf.totalads.ui.utilities.SWTResourceManager;
import org.eclipse.osgi.framework.internal.core.Msg;
import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
//import org.eclipse.swt.widgets.TabItem;
//import org.eclipse.swt.widgets.Table;
//import org.eclipse.swt.widgets.TableColumn;
//import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * This class creates the GUI elements/widgets for live diagnosis using ssh. Any host can be selected and LTTNg tracing
 * can be started on that system and one of the eqarlier model can be used to test the live tracing
 * 
 * 
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class LiveDiagnosis {
	//Initializes variables
	private TracingTypeSelector traceTypeSelector;
	private Text txtPassword;
	private Text txtUserAtHost;
	private TraceBrowser traceBrowser;
	private StringBuilder tmfTracePath;
	private StringBuilder currentlySelectedTracesPath;
	private ModelLoader modelLoader;
	private ResultsAndFeedback resultsAndFeedback;
	private Button btnSelTestTraces;
	private Button btnSelTMFTrace;
	private Button btnLiveTracing;
	private ProgressConsole console;

	/**
	 * Constructor of the LiveDiagnosis class
	 * @param tabFolderParent TabFolder object
	 *
	 */
	public LiveDiagnosis(CTabFolder tabFolderParent){
		tmfTracePath=new StringBuilder();
		currentlySelectedTracesPath=new StringBuilder();
		//LiveDiagnosis Tab Item
		CTabItem tbItmDiagnosis = new CTabItem(tabFolderParent, SWT.NONE);
		tbItmDiagnosis.setText("Live Diagnosis");
		//Making scrollable tab item 
		ScrolledComposite scrolCompAnom=new ScrolledComposite(tabFolderParent, SWT.H_SCROLL | SWT.V_SCROLL);
		Composite comptbItmDiagnosis = new Composite(scrolCompAnom,SWT.NONE);
		tbItmDiagnosis.setControl(scrolCompAnom);
		
		//Desiging the Layout of the GUI Items  for the LiveDiagnosis Tab Item
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan=1;
		comptbItmDiagnosis.setLayoutData(gridData);
		comptbItmDiagnosis.setLayout(new GridLayout(2, false));
	
		///////////////////////////////////////////////////////////////////////////
		//Creating GUI widgets for selection of a trace type and a selection of the model
		///////////////////////////////////////////////////////////////////
		Composite compTraceTypeAndModel=new Composite(comptbItmDiagnosis, SWT.NONE);
		compTraceTypeAndModel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		compTraceTypeAndModel.setLayout(new GridLayout(1, false));
		
	
		selectHostUsingSSH(compTraceTypeAndModel);
		// Create GUI elements for a selection of a trace
		
		//Initialize a class which loads model names from db and create appropriate GUI elements
		
		modelLoader=new ModelLoader(compTraceTypeAndModel);
		modelLoader.setTrace(currentlySelectedTracesPath);///////////////////////////////
		modelLoader.setTraceTypeSelector(traceTypeSelector);
		
		
		//////////////////////////////////////////////////////////////////////
		// Creating GUI widgets for status, results and feedback
		//////////////////////////////////////////////////////////////////
		Composite compStatusResults=new Composite(comptbItmDiagnosis, SWT.NONE);
		compStatusResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		compStatusResults.setLayout(new GridLayout(1, false));
		StatusBar statusBar=new StatusBar(compStatusResults);
		/*Composite compStatus=new Composite(compStatusResults, SWT.NONE);
		compStatus.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		compStatus.setLayout(new GridLayout(2, false));
		
		Label lblProgress= new Label(compStatus, SWT.NONE);
		lblProgress.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		lblProgress.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblProgress.setText("Connected to localhost..");
		//lblProgress.setVisible(false);
		
		
		Label lblStatus= new Label(compStatus, SWT.BORDER);
		lblStatus.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,true,false,1,1));
		lblStatus.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
		lblStatus.setText("        "); */
		
		//ProgressBar progressBar = new ProgressBar(compStatus, SWT.NONE);
		//progressBar.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,false,false,1,1));
		//progressBar.setMaximum(5);
		
		//progressBar.setSelection(5);
		
		
		resultsAndFeedback=new ResultsAndFeedback(compStatusResults);
		modelLoader.setResultsAndFeedback(resultsAndFeedback);
		modelLoader.setStautsBar(statusBar);
		
		//Adjust settings for scrollable LiveDiagnosis Tab Item
		scrolCompAnom.setContent(comptbItmDiagnosis);
		 // Set the minimum size
		scrolCompAnom.setMinSize(500, 500);
	    // Expand both horizontally and vertically
		scrolCompAnom.setExpandHorizontal(true);
		scrolCompAnom.setExpandVertical(true);
	}

	
	
	/**
	 * Creates GUI widgets for a selection of traces and trace types
	 * @param compDiagnosis Composite of LiveDiagnosis
	 */
	private void selectHostUsingSSH(Composite compDiagnosis){
		/**
		 *  Group trace selection
		 */
		Group grpTraceSelection = new Group(compDiagnosis, SWT.NONE);
		grpTraceSelection.setText("Select Traces and Trace Type");
		
		grpTraceSelection.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false));
		grpTraceSelection.setLayout(new GridLayout(1,false));
		
		// Creating widgets for the selection of a trace type
		//Composite compTraceType=new Composite(grpTraceSelection, SWT.NONE);
		//compTraceType.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		//compTraceType.setLayout(new GridLayout(2,false));
		
		Label userAtHost= new Label(grpTraceSelection, SWT.NONE);
		userAtHost.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false,1,1));
		userAtHost.setText("Enter username@hostname   ");
		   // 
	//	btnSelTestTraces = new Button(grpTraceSelection, SWT.RADIO);
		//btnSelTestTraces.setText("Select the  Folder Containing Test Traces");
		//btnSelTestTraces.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false, false,2,1));
		//btnSelTestTraces.setSelection(true);
		
		txtUserAtHost=new Text(grpTraceSelection, SWT.BORDER);
		txtUserAtHost.setEnabled(true);
		txtUserAtHost.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false,false));
		txtUserAtHost.setText(System.getProperty("user.name")+"@localhost");
		
		//btnSelTMFTrace = new Button(grpTraceSelection, SWT.RADIO);
		//btnSelTMFTrace.setText("Select the Trace Selected in TMF (Only Kernel Trace)");
		//btnSelTMFTrace.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false, false,2,1));
		Label lblPassword= new Label(grpTraceSelection, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false,1,1));
		lblPassword.setText("Enter Password ");
		
		txtPassword=new Text(grpTraceSelection,SWT.BORDER|SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,1,1));
		
		
		 
		
		btnLiveTracing=new Button(grpTraceSelection, SWT.BORDER);
		btnLiveTracing.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false,1,1));
		btnLiveTracing.setText("Connect");
		
		console =new ProgressConsole(grpTraceSelection);
		
		btnLiveTracing.addMouseListener(new MouseListener() {
		
			
			@Override
			public void mouseUp(MouseEvent e) {
				if (txtUserAtHost.getText().isEmpty() || txtPassword.getText().isEmpty()){
					MessageBox msgBox=new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,SWT.ICON_ERROR|SWT.OK);
					msgBox.setMessage("Empty fields are not allowed");
					msgBox.open();
				}else {
					SSHConnector ssh=new SSHConnector();
					ssh.openSSHConnection(txtUserAtHost.getText(), txtPassword.getText(), null, console);
				}
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				
				
			}
		});
		/**
		 * End group trace selection
		 */
	}
	
	
	
}
