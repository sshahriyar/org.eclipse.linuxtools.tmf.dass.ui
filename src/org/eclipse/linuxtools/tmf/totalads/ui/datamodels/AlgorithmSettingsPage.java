package org.eclipse.linuxtools.tmf.totalads.ui.datamodels;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.ui.Settings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class AlgorithmSettingsPage extends WizardPage {
	private String []settings;
	private Composite compSettings;
	
	public AlgorithmSettingsPage() {
		super("Algorithm Settings");
		setTitle("Adjust Settings of the Algorithm");
		
	}

	

	@Override
	public void createControl(Composite compParent) {
		compSettings=new Composite(compParent, SWT.NONE);
		//compSettings.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		compSettings.setLayout(new GridLayout(2,false));
		 Button btnOK=new Button(compSettings,SWT.NONE);
		 btnOK.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		 btnOK.setText("      OK       ");
		
		setControl(compSettings);
		setPageComplete(false);
	}
	
	/**
	 * 
	 * @param settings
	 */
	public void setSettings(String []settings){
		this.settings=settings;
		//getContainer().showPage(this);
	}

	@Override
	public void setVisible(boolean isVisible){
		if (isVisible){
			try {
				if (settings!=null){
				   //Settings set =new Settings(settings,compSettings);
				 //  Button btnOK=new Button(compSettings,SWT.NONE);
					// btnOK.setLayoutData(new GridData(SWT.RIGHT,SWT.BOTTOM,true,false,1,1));
					// btnOK.setText("      OK       ");
					 
				   //setControl(compSettings);
				
				}
			} catch (Exception e) {
					e.printStackTrace();
			}
			
		}
	}
}
