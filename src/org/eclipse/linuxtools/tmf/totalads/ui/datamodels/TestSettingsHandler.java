/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/

package org.eclipse.linuxtools.tmf.totalads.ui.datamodels;
import java.util.HashSet;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.ui.Settings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * This class implements the edit command. It allows to edit settings for a selected model in the {@link DataModelsView}.
 * @author <p> Syed Shariyar Murtaza justsshary@htomail.com </p>
 *
 */
public class TestSettingsHandler implements IHandler {
	private HashSet<String> selectedModels;
	private Settings settingsDialog;
	/**
	 * Constructor
	 */
	public TestSettingsHandler(){
		selectedModels=new HashSet<String>();
		
		 /// Registers a listener to Eclipse to get the list of models selected (checked) by the user 
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(DataModelsView.ID,	new ISelectionListener() {
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		
				 if (part instanceof DataModelsView) {  
					   Object obj = ((org.eclipse.jface.viewers.StructuredSelection) selection).getFirstElement();
					    selectedModels= (HashSet<String>)obj;
					   
				    }  
				}
		});
	}
	
	
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	

	}

	@Override
	public void dispose() {
		// TODO 

	}
	//
	//Execute function
	//
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
			MessageBox msgBoxErr= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
					SWT.ICON_ERROR);
			try {
				
				
				if (selectedModels.size() >1){
					
					msgBoxErr.setMessage("Please select only one model to edit settings");
					msgBoxErr.open();
					return null;
					
				} else if (selectedModels.size() < 1){
					
					msgBoxErr.setMessage("Please select a model to edit settings");
					msgBoxErr.open();
					return null;
				}
				String databases=selectedModels.iterator().next();	// get the only selected model
				String algorithmAcronym=databases.split("_")[1];
				IDetectionAlgorithm algorithm=AlgorithmFactory.getInstance().getAlgorithmByAcronym(algorithmAcronym);
				
				if (settingsDialog==null)
					settingsDialog= new Settings(algorithm.getTestingOptions(databases, Configuration.connection));
			
				settingsDialog.showForm();
				String []algorithmSettings=settingsDialog.getOptions();
				//models.put(databases[0], algorithmSettings);
				if (algorithmSettings!=null)
					algorithm.saveTestingOptions(algorithmSettings, databases, Configuration.connection);
				//settingsDialog=null;
			
		} catch (TotalADSUIException ex) {
			msgBoxErr.setMessage(ex.getMessage());
			msgBoxErr.open();
		}catch (TotalADSDBMSException ex) {
			msgBoxErr.setMessage(ex.getMessage());
			msgBoxErr.open();
		}finally{
			settingsDialog=null;
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
