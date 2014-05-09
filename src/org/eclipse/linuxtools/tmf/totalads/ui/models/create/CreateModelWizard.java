/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.models.create;


import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmUtility;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

/**
 * This class creates a wizard dialog box for the creation of a new model
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class CreateModelWizard extends Wizard {
	private AlgorithmSelectionPage pageAlgoSelection;
	private AlgorithmSettingsPage pageAlgoSettings;
	private ModelNamePage modelPage;
	/**
	 * Constructor
	 */
	public CreateModelWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getWindowTitle()
	 */
	@Override
	public String getWindowTitle() {
 	    return "Create a New Model";
     }

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	 public void addPages() {
		pageAlgoSelection=new AlgorithmSelectionPage();
	    pageAlgoSettings=new AlgorithmSettingsPage();
	    modelPage=new ModelNamePage();
		addPage(pageAlgoSelection);
		addPage(modelPage);
		addPage(pageAlgoSettings);
		
	  }
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page.equals(pageAlgoSelection)){
			
			IDetectionAlgorithm alg= pageAlgoSelection.getSelectedAlgorithm();
			String []settings=alg.getTrainingOptions();
			pageAlgoSettings.setSettings(settings);
			
		}
		
		return super.getNextPage(page);
	}

	/*
	 * Creates a model in the database when the Finish button is clicked
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		IDetectionAlgorithm alg= pageAlgoSelection.getSelectedAlgorithm();
		String modelName=modelPage.gettheModel();
		String []settings=pageAlgoSettings.getSettingsSelectedByTheUser();
		
		MessageBox msgBoxErr= new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
				SWT.ICON_ERROR);
		
		
		String exception="";
		if (settings==null)
		{
			return false;
		}
		else{	
			try {
				AlgorithmUtility.createModel(modelName, alg, Configuration.connection, settings);
				
			} catch (TotalADSDBMSException e) {
				exception=e.getMessage();
			} catch (TotalADSGeneralException e) {
				exception=e.getMessage();
			}
		
			if (exception!=null && !exception.isEmpty()){
				msgBoxErr.setMessage(exception);
				msgBoxErr.open();
				return false;
			}else
				return true;
		}
		
		
	}
	
	
}
