package org.eclipse.linuxtools.tmf.totalads.ui.datamodels;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.linuxtools.tmf.totalads.ui.Settings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class AlgorithmSettingsPage extends WizardPage {

	public AlgorithmSettingsPage() {
		super("Algorithm Settings");
		setTitle("Adjust Settings of the Algorithm");
	}

	

	@Override
	public void createControl(Composite compParent) {
		Composite compSettings=new Composite(compParent, SWT.NONE);
		compSettings.setLayout(new GridLayout(2,false));
		setControl(compSettings);
		setPageComplete(true);
	}

}
