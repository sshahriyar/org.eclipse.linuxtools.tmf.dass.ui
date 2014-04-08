/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.diagnosis;


import org.eclipse.linuxtools.tmf.totalads.core.TMFTotalADSView;
import org.eclipse.linuxtools.tmf.totalads.ui.TotalADS;
import org.eclipse.linuxtools.tmf.totalads.ui.TraceBrowser;
import org.eclipse.linuxtools.tmf.totalads.ui.TracingTypeSelector;
import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.TabItem;
//import org.eclipse.swt.widgets.Table;
//import org.eclipse.swt.widgets.TableColumn;
//import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * This class creates the GUI elements/widgets for diagnosis.
 * It creates a diagnosis tab and then creates other GUI widgets by instantiating other classes and SWT widgets.
 * 
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class Diagnosis {
	//Initializes variables
	private TracingTypeSelector traceTypeSelector;
	private Text txtTraceID;
	private Text txtTraceSource;
	private Text txtTraceCount;
	private Text txtTestTraceDir;
	private TraceBrowser traceBrowser;
	private StringBuilder tracePath; 
	private ModelLoader modelLoader;
	//private ResultsAndFeedback resultsAndFeedback;
	/**
	 * Constructor of the Diagnosis class
	 * @param tabFolderParent TabFolder object
	 *
	 */
	public Diagnosis(CTabFolder tabFolderParent){
		tracePath=new StringBuilder();
		//Diagnosis Tab Item
		CTabItem tbItmDiagnosis = new CTabItem(tabFolderParent, SWT.NONE);
		tbItmDiagnosis.setText("Diagnosis");
		//Making scrollable tab item 
		ScrolledComposite scrolCompAnom=new ScrolledComposite(tabFolderParent, SWT.H_SCROLL | SWT.V_SCROLL);
		Composite comptbItmDiagnosis = new Composite(scrolCompAnom,SWT.NONE);
		tbItmDiagnosis.setControl(scrolCompAnom);
		
		//Desiging the Layout of the GUI Items  for the Diagnosis Tab Item
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan=1;
		comptbItmDiagnosis.setLayoutData(gridData);
		comptbItmDiagnosis.setLayout(new GridLayout(2, false));
		// Create GUI elements for selection of a trace type
		selectTraceTypeAndTraces(comptbItmDiagnosis);
		// Create GUI elements for a selection of a trace
		currentlySelectedTrace(comptbItmDiagnosis);
		//Initialize a class which loads model names from db and create appropriate GUI elements
		modelLoader=new ModelLoader(comptbItmDiagnosis);
		modelLoader.setTrace(tracePath);
		modelLoader.setTraceTypeSelector(traceTypeSelector);
		
		//Adjust settings for scrollable Diagnosis Tab Item
		scrolCompAnom.setContent(comptbItmDiagnosis);
		 // Set the minimum size
		scrolCompAnom.setMinSize(500, 500);
	    // Expand both horizontally and vertically
		scrolCompAnom.setExpandHorizontal(true);
		scrolCompAnom.setExpandVertical(true);
	}

	
	
	/**
	 * Creates GU elements for a selection of traces and trace types
	 * @param compDiagnosis Composite of Diagnosis
	 */
	private void selectTraceTypeAndTraces(Composite compDiagnosis){
		/**
		 *  Group trace selection
		 */
		Group grpTraceSelection = new Group(compDiagnosis, SWT.NONE);
		grpTraceSelection.setText("Select Traces");
		
		grpTraceSelection.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		grpTraceSelection.setLayout(new GridLayout(2,false));
		
		Label lblTraceType= new Label(grpTraceSelection, SWT.BORDER);
		lblTraceType.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		lblTraceType.setText("Select a trace type");
		
	    traceTypeSelector=new TracingTypeSelector(grpTraceSelection,new GridData(SWT.FILL, SWT.TOP, false, false,1,1));
		
		Label lblSelTestTraces = new Label(grpTraceSelection, SWT.CHECK);
		lblSelTestTraces.setText("Select a folder containing traces or a single trace");
		lblSelTestTraces.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true, false,2,1));
		
		txtTestTraceDir=new Text(grpTraceSelection, SWT.BORDER);
		txtTestTraceDir.setEnabled(true);
		txtTestTraceDir.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		
		traceBrowser= new TraceBrowser(grpTraceSelection,txtTestTraceDir,new GridData(SWT.LEFT,SWT.TOP,false,false));
		//
		//Event handler for a text box modification
		//
		txtTestTraceDir.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				tracePath.delete(0, tracePath.length());
				tracePath.append(txtTestTraceDir.getText());
				txtTraceID.setText("Manually typed!");
				txtTraceSource.setText("Manually typed!");
				txtTraceCount.setText("Manually typed!");
			}
		});	
		
		
		/**
		 * End group trace selection
		 */
	}
	
	
	/**
	 * Creates GUI elements for a currently selected trace
	 * @param compParentDaignosis
	 */
	private void currentlySelectedTrace(Composite compParentDaignosis){
		 
			Group grpCurrentTrace = new Group(compParentDaignosis, SWT.NONE);
			grpCurrentTrace.setText("Currently Selected Trace(s)");
			
			grpCurrentTrace.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
			grpCurrentTrace.setLayout(new GridLayout(2,false));
			
			Label lblTraceID= new Label(grpCurrentTrace, SWT.BORDER);
			lblTraceID.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			lblTraceID.setText("Trace id");
			
			txtTraceID=new Text(grpCurrentTrace,SWT.BORDER);
			txtTraceID.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			txtTraceID.setEditable(false);
		
			
			Label lblTraceSource= new Label(grpCurrentTrace, SWT.BORDER);
			lblTraceSource.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			lblTraceSource.setText("Source");
			
			txtTraceSource=new Text(grpCurrentTrace,SWT.BORDER);
			txtTraceSource.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			txtTraceSource.setEditable(false);
			//txtTraceSource.setText("TMF");
			
			Label lblTraceCount= new Label(grpCurrentTrace, SWT.BORDER);
			lblTraceCount.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			lblTraceCount.setText("Trace Count");
			
			txtTraceCount=new Text(grpCurrentTrace,SWT.BORDER);
			txtTraceCount.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			txtTraceCount.setEditable(false);
			//txtTraceCount.setText("1"); 
			// Add a reference of the GUI elements to a trace browser; whenever, a trace browser
			// is used by the user to browse traces, these elements will be updated to show
			// the state of the system
			traceBrowser.setTextSelectedTraceFields(txtTraceID,txtTraceSource,txtTraceCount);
			
	}
	

	/**
	 * This function gets called from {@link TotalADS} and {@link TMFTotalADSView} to notify which
	 * trace is selected by a user in the TMF
	 * @param traceLocation
	 * @param traceTypeName
	 */
	public void updateOnTraceSelection(String traceLocation, String traceTypeName){
		txtTestTraceDir.setText("");
		tracePath.delete(0, tracePath.length());
		tracePath.append(traceLocation);
		
		String traceName=traceLocation.substring(traceLocation.lastIndexOf('/')+1, traceLocation.length());
		txtTraceID.setText(traceName);
		txtTraceSource.setText("TMF:"+traceTypeName);
		txtTraceCount.setText("1");
		
		traceTypeSelector.selectTraceType(traceTypeName);
	
	}


}
