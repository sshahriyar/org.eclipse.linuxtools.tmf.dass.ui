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

//import java.util.ArrayList;
import java.util.List;
//import java.io.File;
//import java.lang.reflect.InvocationTargetException;



//import java.lang.reflect.Method;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
//import org.eclipse.linuxtools.tmf.totalads.algorithms.Results;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
//import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.dbms.IObserver;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
//import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
//import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.linuxtools.tmf.totalads.ui.Settings;
import org.eclipse.linuxtools.tmf.totalads.ui.TracingTypeSelector;
import org.eclipse.linuxtools.tmf.totalads.ui.modeling.StatusBar;
import org.eclipse.linuxtools.tmf.totalads.ui.utilities.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.TreeAdapter;

//import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

import org.eclipse.swt.widgets.Label;
/**
 * This class connects with the database and loads all the model names in a tree and creates the related GUI wdigets.
 * It also instantiates ResultsAndFeedback class and shows the results of the evaluation of the model 
 * using that class.
 * @author <p>Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */

public class ModelLoader {
	private Group grpModelSelection;
	private Button btnEvaluateModels;
	private Tree treeModels;
	private Button btnSettings;
	private Button btnDelete;
	private  TreeItem currentlySelectedTreeItem;
	private MessageBox msgBox;
	private StringBuilder tracePath;
	private StatusBar statusBar;
	private TracingTypeSelector traceTypeSelector;
	private ResultsAndFeedback resultsAndFeedback;
	private Settings settingsDialog;
	private String []algorithmSettings;
	private Composite compModelSelection;
	
	/**
	 * Constructor
	 * @param comptbtmAnalysis Composite
	 */
	public ModelLoader(Composite comptbtmAnalysis ){
		
		msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,SWT.ICON_ERROR|SWT.OK);
		
		/**
		*  Group model selection
		*/
		
		grpModelSelection=new Group(comptbtmAnalysis,SWT.NONE);	
		grpModelSelection.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true));
		grpModelSelection.setLayout(new GridLayout(1,false));
		grpModelSelection.setText("Evaluate a Model");
		
		compModelSelection=new Composite(grpModelSelection, SWT.NONE);
	    compModelSelection.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false));
		compModelSelection.setLayout(new GridLayout(3,false));
	    	
		btnEvaluateModels=new Button(compModelSelection, SWT.NONE);
		btnEvaluateModels.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false,false,1,1));
		btnEvaluateModels.setText(" Evaluate ");
	   
		btnSettings=new Button(compModelSelection, SWT.NONE);
		btnSettings.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false,false,1,1));
		btnSettings.setText(" Settings ");
		
		btnDelete=new Button(compModelSelection, SWT.NONE);
		btnDelete.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false,false,1,1));
		btnDelete.setText("   Delete   ");
		
		treeModels = new Tree(grpModelSelection, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION| SWT.V_SCROLL | SWT.H_SCROLL);
		treeModels.setLinesVisible(true);
		treeModels.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		
		
		/*statusAndResults=new Composite(grpModelSelection, SWT.NONE);
		statusAndResults.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,2));
		statusAndResults.setLayout(new GridLayout(1,false));
		
		lblProgress= new Label(compModelSelection, SWT.NONE);
		lblProgress.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		lblProgress.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblProgress.setText("Processing........");
		lblProgress.setVisible(false);*/
		
		/*resultsAndFeedback=new ResultsAndFeedback(statusAndResults);
		*/
		
		populateTreeWithModels();
		addEventHandlers();		
			
		/**
		 * End group model selection 
		*/
	}
	
	/**
	 * Populates the tree with the list of models (databases) from the database
	 */
	private void populateTreeWithModels(){
		treeModels.removeAll();
		if (Configuration.connection.isConnected() ){ // if there is a running DB instance
			
				List <String> modelsList= Configuration.connection.getDatabaseList();
				
			    if (modelsList!=null){
					TreeItem []items=new TreeItem[modelsList.size()];
					for (int i=0;i <items.length;i++){
						items[i]=new TreeItem(treeModels,SWT.NONE);
						items[i].setText(modelsList.get(i));
										
					}
				}
		}
		 currentlySelectedTreeItem=null;
	}
	
	
	/**
	 * 
	 * Adds event handlers to different widgets
	 * 
	 */
	private void addEventHandlers(){
		/**
		 * Event handler for the evaluate button
		 */
		btnEvaluateModels.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				
				if (!checkItemSelection()){
					msgBox.setMessage("Please, select a model first!");
					msgBox.open();
					return;
				} 
				
				String database=currentlySelectedTreeItem.getText();
				String []modelKey=database.split("_");
				
				if(modelKey==null ||  modelKey.length<3){
					msgBox.setMessage("Not a valid model created by TotalADS!");
					msgBox.open();
					return;
				}
				
				ITraceTypeReader traceReader=traceTypeSelector.getSelectedType();
				
				AlgorithmFactory modFac= AlgorithmFactory.getInstance();
				
				IDetectionAlgorithm algorithm= modFac.getModelyByAcronym(modelKey[1]);
				
				if(algorithm==null){
					msgBox.setMessage("This doesn't seem to be a valid model created by TotalADS!");
					msgBox.open();
					return;
				}
				
				resultsAndFeedback.clearData();
				
				btnEvaluateModels.setEnabled(false);
				btnSettings.setEnabled(false);
				btnDelete.setEnabled(false);
				
				BackgroundTesting testTheModel=new BackgroundTesting(tracePath.toString(), traceReader, algorithm, database,
							statusBar, btnDelete, btnSettings, btnEvaluateModels, resultsAndFeedback, algorithmSettings);
				testTheModel.start();
				
				
			}
		});
		/**
		 * Event handler for the tree selection event
		 */
		treeModels.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem item=(TreeItem)e.item;
				if (currentlySelectedTreeItem==null){
					item.setChecked(true);
					currentlySelectedTreeItem=item;
				}else{
					if (item.getText(0).equalsIgnoreCase(currentlySelectedTreeItem.getText())){
							if (currentlySelectedTreeItem.getChecked() ){
								currentlySelectedTreeItem.setChecked(false);
							}
							else
								currentlySelectedTreeItem.setChecked(true);
					
					}else{
						currentlySelectedTreeItem.setChecked(false);
						item.setChecked(true);
						currentlySelectedTreeItem=item;
					}
						
					
				}
	     		
				
				
			}
			
		});	
		/** 
		 * Event handler for Settings button
		 * 
		 */
		btnSettings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (!checkItemSelection()){
					msgBox.setMessage("Please, select a model first!");
					msgBox.open();
				} else{
					try {
							AlgorithmFactory modFac= AlgorithmFactory.getInstance();
							String database=currentlySelectedTreeItem.getText();
							String []modelKey=database.split("_");
							if(modelKey==null ||  modelKey.length<3){
								msgBox.setMessage("Not a valid model created by TotalADS!");
								msgBox.open();
								return;
							}
							IDetectionAlgorithm model= modFac.getModelyByAcronym(modelKey[1]);
							if(model==null){
								msgBox.setMessage("This doesn't seem to be a valid model created by TotalADS!");
								msgBox.open();
								return;
							}
							
							settingsDialog= new Settings(model.getTestingOptions(database, Configuration.connection));
						
							settingsDialog.showForm();
							algorithmSettings=settingsDialog.getOptions();
						
					} catch (TotalADSUIException ex) {
						msgBox.setMessage(ex.getMessage());
						msgBox.open();
					}
				}
			}
		});
		/**
		 * Event handler for Delete button
		 */
		btnDelete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (!checkItemSelection()){
					msgBox.setMessage("Please, select a model first!");
					msgBox.open();
				} else{
					
					MessageBox msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
								SWT.ICON_INFORMATION|SWT.YES|SWT.NO);
					
					msgBox.setMessage("Do you want to delete the model "+currentlySelectedTreeItem.getText()+ "?");
					if (msgBox.open()==SWT.YES)
						Configuration.connection.deleteDatabase(currentlySelectedTreeItem.getText());
					
						
					//populateTreeWithModels();
				}
			}
		});
		/**
		 * Event handler to update the list of models automatically whenever the database changes
		 */
		Configuration.connection.addObserver(new IObserver() {
			@Override
			public void update() {
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run(){
						populateTreeWithModels();
					}
				});
		
			}

			
		});
	 // end of function addEventHandler
	}
	
	/**
	* 
	* Checks selection of a model in the tree
	* 
	*/
	private boolean checkItemSelection(){
		
		if (currentlySelectedTreeItem ==null )
				return false;
		else
				return true;
		
	}
	/**
	 * Assigns tracePath object from LiveMonitor class to a local variable
	 * @param tracePath Trace Path 
	 */
	public void setTrace(StringBuilder tracePath){
		this.tracePath=tracePath;
	}
	/**
	 *  Assigns TraceTypeSelector object from LiveMonitor class to a local object
	 * @param traceTypeSelector Trace type selector
	 */
	public void setTraceTypeSelector(TracingTypeSelector traceTypeSelector){
		this.traceTypeSelector= traceTypeSelector;
	}
	
	/**
	 * Assigns ResultsAndFeddback object from LiveMonitor class to a local variable
	 * @param resultsAndFeedback Results and Feedback object
	 */
	public void setResultsAndFeedback(ResultsAndFeedback resultsAndFeedback){
		this.resultsAndFeedback=resultsAndFeedback;
	}
	/**
	 * Sets the StatusBar object to a local variable to update progress during processing
	 * @param statusBar An object of the StatusBar
	 */
	public void setStautsBar(StatusBar statusBar){
		this.statusBar=statusBar;
	}
	
	// End of class
	}
