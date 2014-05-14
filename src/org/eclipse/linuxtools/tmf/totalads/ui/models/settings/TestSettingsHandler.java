/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/

package org.eclipse.linuxtools.tmf.totalads.ui.models.settings;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmUtility;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMSFactory;
import org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.ui.models.DataModelsView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * This class implements the settings command. It allows to edit settings for a selected model in the 
 * {@link DataModelsView}.
 * @author <p> Syed Shariyar Murtaza justsshary@htomail.com </p>
 *
 */
public class TestSettingsHandler implements IHandler {
	private HashSet<String> selectedModels;
	private TestSettingsDialog settingsDialog;
	
	/**
	 * Constructor
	 */
	public TestSettingsHandler(){
		selectedModels=new HashSet<String>();
		
		 /// Registers a listener to Eclipse to get the list of models selected (checked) by the user 
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(DataModelsView.ID,	new ISelectionListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		
				 if (part instanceof DataModelsView) {  
					   Object obj = ((org.eclipse.jface.viewers.StructuredSelection) selection).getFirstElement();
					    selectedModels= (HashSet<String>)obj;
					   
				    }  
				}
		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#addHandlerListener(org.eclipse.core.commands.IHandlerListener)
	 */
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	

	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#dispose()
	 */
	@Override
	public void dispose() {
		

	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
			
		MessageBox msgBoxErr= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
					SWT.ICON_ERROR);
			try {
				
				
				
				// Checking for the proper selection
				if (selectedModels.size() >1){
					
					msgBoxErr.setMessage("Please select only one model to edit settings");
					msgBoxErr.open();
					return null;
					
				} else if (selectedModels.size() < 1){
					
					msgBoxErr.setMessage("Please select a model to edit settings");
					msgBoxErr.open();
					return null;
				}
				
				 
				IDataAccessObject connection=DBMSFactory.INSTANCE.getDataAccessObject();
				//Open the settings dialog
				if (connection.isConnected()){
						String model=selectedModels.iterator().next();	// get the only selected model
						IDetectionAlgorithm algorithm=AlgorithmUtility.getAlgorithmFromModelName(model);
						String []settings=algorithm.getTestSettings(model,connection );
						
						settingsDialog= new TestSettingsDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
																, algorithm, model, settings);
					
						settingsDialog.create();
						settingsDialog.open();
				}else{
					msgBoxErr.setMessage("No databse connection exists...");
					msgBoxErr.open();
				}
					
		
			
		} catch (TotalADSGeneralException ex) {
			msgBoxErr.setMessage(ex.getMessage());
			msgBoxErr.open();
		}catch (Exception ex) {
			msgBoxErr.setMessage(ex.getMessage());
			msgBoxErr.open();
			Logger.getLogger(TestSettingsHandler.class.getName()).log(Level.SEVERE,ex.getMessage(), ex);
			//Check if connection still exists and all the views are notified of the presence and absence of connection
			DBMSFactory.INSTANCE.verifyConnection();
			
		}finally{
			settingsDialog=null;
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#isHandled()
	 */
	@Override
	public boolean isHandled() {
		
		return true;
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#removeHandlerListener(org.eclipse.core.commands.IHandlerListener)
	 */
	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
	

	}

}
