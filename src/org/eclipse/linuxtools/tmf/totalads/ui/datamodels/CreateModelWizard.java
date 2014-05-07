package org.eclipse.linuxtools.tmf.totalads.ui.datamodels;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
/**
 * 
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class CreateModelWizard extends Wizard {
	private AlgorithmSelectionPage pageAlgoSelection;
	private AlgorithmSettingsPage pageAlgoSettings;
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
		pageAlgoSelection=new AlgorithmSelectionPage();
	    pageAlgoSettings=new AlgorithmSettingsPage();
		addPage(pageAlgoSelection);
		addPage(pageAlgoSettings);
	  }
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page.equals(pageAlgoSelection)){
			IDetectionAlgorithm alg= pageAlgoSelection.getSelectedAlgorithm();
			String []settings=alg.getTrainingOptions(null, null, true);
			//addPage(pageAlgoSettings);
			   pageAlgoSettings.setSettings(settings);
		}
		
		return super.getNextPage(page);
	}

	@Override
	public boolean performFinish() {
		
		return true;
	}

}
