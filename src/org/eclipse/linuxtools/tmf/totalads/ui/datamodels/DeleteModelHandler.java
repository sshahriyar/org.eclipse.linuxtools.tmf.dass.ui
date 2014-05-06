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
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.BackgroundTesting;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
/**
 * This class implements the delete command (icon on the {@link DataModelsView}). Its object is executed by Eclipse 
 * automatically whenever the delete icon is clicked.
 * * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class DeleteModelHandler implements IHandler {
	private HashSet<String> selectedModels;
	/**
	 * Constructor
	 */
	public DeleteModelHandler(){
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
		// TODO 

	}

	@Override
	public void dispose() {
		// TODO 

	}
	///
	//Execute function
	//
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		MessageBox msgBoxErr= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
				SWT.ICON_ERROR);
		MessageBox msgBoxYesNo= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
				SWT.ICON_INFORMATION|SWT.YES|SWT.NO);
	
		try{
			 
			if (selectedModels.size() <=0){
				msgBoxErr.setMessage("Please select a model first");
				msgBoxErr.open();
				return null;
			}
			
				
			Iterator<String> it=selectedModels.iterator();
			String message="Do you really want to delete ";
			while (it.hasNext())
				   message+=it.next()+", ";
			message=message.substring (0,message.lastIndexOf(","));
			message+="?";
			
			msgBoxYesNo.setMessage(message);
			
			if (msgBoxYesNo.open()==SWT.YES){
				it=selectedModels.iterator();
				while (it.hasNext())
					Configuration.connection.deleteDatabase(it.next());
			}
			
		} catch (Exception ex) {
			if (ex.getMessage()!=null)
				msgBoxErr.setMessage(ex.getMessage());
			else
				msgBoxErr.setMessage("Error deleting model(s).");
			msgBoxErr.open();
			Logger.getLogger(DeleteModelHandler.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	
	    return null;
	}

	@Override
	public boolean isEnabled() {
		
		return true;
	}

	@Override
	public boolean isHandled() {
		
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO 

	}

}
