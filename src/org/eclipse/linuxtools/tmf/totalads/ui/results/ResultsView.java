/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.results;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.DiagnosisView;
import org.eclipse.linuxtools.tmf.totalads.ui.models.DataModelsView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * Launches a Results View
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 * 		   <p> Efraim Lopez </p>
 *
 */
public class ResultsView extends ViewPart {
	private ResultsAndFeedback results;
	public static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.ResultsView";
	
	
	/**
	 * Constructor
	 */
	public ResultsView() {
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		
		 results=new ResultsAndFeedback(parent, false);
		 try {
			 //Shows a Diagnosis View
			getSite().getWorkbenchWindow().getActivePage().showView(DataModelsView.ID);
			
		} catch (PartInitException e) {
			MessageBox msgBox=new MessageBox(getSite().getShell(),SWT.OK);
			if(e.getMessage()!=null){
				msgBox.setMessage(e.getMessage());
			}else
				msgBox.setMessage("Unable to launch a view");
			msgBox.open();
			Logger.getLogger(ResultsView.class.getName()).log(Level.SEVERE,null,e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		

	}
	
	/**
	 * Returns the instance of {@link ResultsAndFeedback}
	 * @return
	 */
	public ResultsAndFeedback getResultsAndFeddbackInstance(){
		
		return results;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose(){
		//Hiding Diagnosis View
		
		IViewReference []refs= getSite().getWorkbenchWindow().getActivePage().getViewReferences();
			for (int i=0; i< refs.length; i++){
				if (refs[i].getId().equals(DiagnosisView.VIEW_ID))
					getSite().getWorkbenchWindow().getActivePage().hideView(refs[i]);
			}
		}
	
	
}
