package org.eclipse.linuxtools.tmf.totalads.ui.models.create;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.ui.models.SettingsForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;


public class AlgorithmSettingsPage extends WizardPage {
	private String []settings;
	private Composite compSettings;
	private SettingsForm settingForm;
	public AlgorithmSettingsPage() {
		super("Algorithm Settings");
		setTitle("Adjust Settings of the Algorithm");
		
	}

	

	@Override
	public void createControl(Composite compParent) {
		compSettings=new Composite(compParent, SWT.NONE);
		//compSettings.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		compSettings.setLayout(new GridLayout(1,false));
		//compSettings.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,false,false));
		//Text txtOption=new Text(compSettings, SWT.NONE);
		// txtOption.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		// txtOption.setText("lala");
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
					Control []widgets= compSettings.getChildren();
					for (int i=0; i<widgets.length; i++)
						widgets[i].dispose();
					compSettings.layout();
				 
				    settingForm =new SettingsForm(settings,compSettings);
				
				    compSettings.layout(); 
				    setPageComplete(true);
				
			}
			} catch (Exception e) {
					e.printStackTrace();
			}
			
		}
		super.setVisible(isVisible);
		
	}
	
  /**
   * Returns the selected settings from the user	
   * @return An array of settings
   */
  public String[] getSettingsSelectedByTheUser(){
	  String []settingFromUser=null;
	  
	  try {
		settingFromUser=settingForm.getSettings();
	} catch (TotalADSGeneralException e) {
		MessageBox msgBoxErr= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
				SWT.ICON_ERROR);
		msgBoxErr.setMessage(e.getMessage());
		msgBoxErr.open();
		
	}
	return settingFromUser; 
	 
  }
  
  @Override
  public boolean canFlipToNextPage() {
				return false;
  }
	
  
  
}
