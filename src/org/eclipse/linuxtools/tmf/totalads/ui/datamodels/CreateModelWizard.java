package org.eclipse.linuxtools.tmf.totalads.ui.datamodels;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
/**
 * 
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class CreateModelWizard extends Wizard {

	public CreateModelWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public String getWindowTitle() {
 	    return "Create A New Model";
     }

	
	@Override
	 public void addPages() {
	   
	   addPage(new AlgorithmSelectionPage());
	   addPage(new AlgorithmSettingsPage());
	   //getNextPage(page)

	  }
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		return super.getNextPage(page);
	}

	@Override
	public boolean performFinish() {
		
		return true;
	}

}
