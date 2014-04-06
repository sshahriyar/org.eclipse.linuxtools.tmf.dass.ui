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
import org.eclipse.wb.swt.SWTResourceManager;
/**
 * This class connects with the database and loads all the model names in a tree and creates the related GUI wdigets.
 * It also instantiates ResultsAndFeedback class and shows the results of the evaluation ofthe model 
 * in that class
 * @author <p>Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */

public class ModelLoader {
	private Group grpAnalysisModelSelection=null;
	private Button btnAnalysisEvaluateModels=null;
	private Tree treeAnalysisModels=null;
	private Button btnSettings;
	private Button btnDelete;
	private  TreeItem currentlySelectedTreeItem=null;
	private MessageBox msgBox;
	private StringBuilder tracePath;
	private Label lblProgress;
	private TracingTypeSelector traceTypeSelector;
	private ResultsAndFeedback resultsAndFeedback;
	private Settings settingsDialog=null;
	private String []modelOptions=null;
	private Composite compModelSelection;
	private Composite statusAndResults;
	/**
	 * Constructor
	 * @param comptbtmAnalysis Composite
	 */
	public ModelLoader(Composite comptbtmAnalysis ){
		/**
		*  Group model selection
		*/
		msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,SWT.ICON_ERROR|SWT.OK);
		
		grpAnalysisModelSelection=new Group(comptbtmAnalysis,SWT.NONE);	
		grpAnalysisModelSelection.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,2,5));
		grpAnalysisModelSelection.setLayout(new GridLayout(2,false));
		grpAnalysisModelSelection.setText("Evaluate a Model");
		
					
		compModelSelection=new Composite(grpAnalysisModelSelection, SWT.NONE);
	    compModelSelection.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true,1,2));
		compModelSelection.setLayout(new GridLayout(3,false));
	    	
		
		btnAnalysisEvaluateModels=new Button(compModelSelection, SWT.NONE);
		btnAnalysisEvaluateModels.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false,false,1,1));
		btnAnalysisEvaluateModels.setText(" Evaluate ");
	   
		btnSettings=new Button(compModelSelection, SWT.NONE);
		btnSettings.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false,false,1,1));
		btnSettings.setText(" Settings ");
		
		btnDelete=new Button(compModelSelection, SWT.NONE);
		btnDelete.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false,false,1,1));
		btnDelete.setText("   Delete   ");
		
		treeAnalysisModels = new Tree(compModelSelection, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION| SWT.V_SCROLL | SWT.H_SCROLL);
		treeAnalysisModels.setLinesVisible(true);
		treeAnalysisModels.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true,3,5));
		
		
		statusAndResults=new Composite(grpAnalysisModelSelection, SWT.NONE);
		statusAndResults.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,2));
		statusAndResults.setLayout(new GridLayout(1,false));
		
		lblProgress= new Label(statusAndResults, SWT.NONE);
		lblProgress.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		lblProgress.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblProgress.setText("Processing........");
		lblProgress.setVisible(false);
		
		resultsAndFeedback=new ResultsAndFeedback(statusAndResults);
		
		
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
	///////data
		if (Configuration.connection.isConnected() ){ // if there is a running DB instance
			
			List <String> modelsList= Configuration.connection.getDatabaseList();
				treeAnalysisModels.removeAll();
			    if (modelsList!=null){
					TreeItem []items=new TreeItem[modelsList.size()];
					for (int i=0;i <items.length;i++){
						items[i]=new TreeItem(treeAnalysisModels,SWT.NONE);
						items[i].setText(modelsList.get(i));
										
					}
				}
		}
		    currentlySelectedTreeItem=null;
	}
	/**
	 * Adds event handlers to different widgets
	 */
	private void addEventHandlers(){
		/**
		 * Event handler for the evaluate button
		 */
		btnAnalysisEvaluateModels.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				
				if (!checkItemSelection()){
					msgBox.setMessage("Please, select a model first!");
					msgBox.open();
					return;
				} 
				
				ITraceTypeReader traceReader=traceTypeSelector.getSelectedType();
				
				AlgorithmFactory modFac= AlgorithmFactory.getInstance();
				String database=currentlySelectedTreeItem.getText();
				String modelKey=database.split("_")[1];
				IDetectionAlgorithm model= modFac.getModelyByAcronym(modelKey);
				
				resultsAndFeedback.clearData();
				
				btnAnalysisEvaluateModels.setEnabled(false);
				btnSettings.setEnabled(false);
				btnDelete.setEnabled(false);
				lblProgress.setVisible(true);
				BackgroundTesting testTheModel=new BackgroundTesting(tracePath.toString(), traceReader, model, database);
				testTheModel.start();
				
				
			}
		});
		/**
		 * Event handler for the tree selection event
		 */
		treeAnalysisModels.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem item=(TreeItem)e.item;
	     		if (currentlySelectedTreeItem!=null)
						 currentlySelectedTreeItem.setChecked(false);
				item.setChecked(true);
				currentlySelectedTreeItem=item;
				
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
							String modelKey=database.split("_")[1];
							IDetectionAlgorithm model= modFac.getModelyByAcronym(modelKey);
							//
							
							settingsDialog= new Settings(model.getTestingOptions(database, Configuration.connection));
						
							settingsDialog.showForm();
							modelOptions=settingsDialog.getOptions();
						
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
	* Checks selection of a model in the tree
	*/
	private boolean checkItemSelection(){
		
		if (currentlySelectedTreeItem ==null )
				return false;
		else
				return true;
		
	}
	/**
	 * Assigns tracePath object from Diagnosis class to a local variable
	 * @param tracePath
	 */
	public void setTrace(StringBuilder tracePath){
		this.tracePath=tracePath;
	}
	/**
	 *  Assigns TraceTypeSelector object from Diagnosis class to a local object
	 * @param traceTypeSelector
	 */
	public void setTraceTypeSelector(TracingTypeSelector traceTypeSelector){
		this.traceTypeSelector= traceTypeSelector;
	}
	
	/**
	 * Assigns ResultsAndFeddback object from Diagnosis class to a local variable
	 * @param resultsAndFeedback
	 */
	public void setResultsAndFeedback(ResultsAndFeedback resultsAndFeedback){
		this.resultsAndFeedback=resultsAndFeedback;
	}
	
	}
