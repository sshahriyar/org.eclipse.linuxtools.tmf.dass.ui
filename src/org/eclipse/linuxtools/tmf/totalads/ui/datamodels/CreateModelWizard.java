package org.eclipse.linuxtools.tmf.totalads.ui.datamodels;


import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmUtility;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
/**
 * 
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
	//	setNeedsProgressMonitor(true);
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
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		IDetectionAlgorithm alg= pageAlgoSelection.getSelectedAlgorithm();
		String modelName=modelPage.gettheModel();
		String []settings=pageAlgoSettings.getSettingsSelectedByTheUser();
		
		MessageBox msgBoxErr= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
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
			} catch (TotalADSUIException e) {
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
