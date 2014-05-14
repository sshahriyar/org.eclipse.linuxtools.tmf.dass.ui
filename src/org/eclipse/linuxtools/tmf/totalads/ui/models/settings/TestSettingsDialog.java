package org.eclipse.linuxtools.tmf.totalads.ui.models.settings;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMSFactory;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.ui.models.SettingsForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
/**
 * This class implements the settings dialog box shown when test settings is selected
 * @author <p> Syed Shariyar Murtaza </p>
 *
 */
public class TestSettingsDialog extends TitleAreaDialog {
	//Variables
	private String []settingsForAlgorithm;
	private SettingsForm settingsForm;
	private MessageBox msgErr;
	private IDetectionAlgorithm algorithm;
	private String modelName;
	/**
	 * Constructor
	 * @param parentShell
	 */
	public TestSettingsDialog(Shell parentShell, IDetectionAlgorithm algorithm, String modelName, String []settings) {
		super(parentShell);
		this.settingsForAlgorithm=settings;
		this.algorithm=algorithm;
		this.modelName=modelName;
		msgErr=new MessageBox(parentShell, SWT.ERROR);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	  public void create() {
	    super.create();
	    setTitle("Adjust Settings for Testing");
	   
	  }
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	  protected Control createDialogArea(Composite compParent) {
		Composite compSuper = (Composite) super.createDialogArea(compParent);
		Composite compSettingsDialog=new Composite(compSuper, SWT.NONE);
		compSettingsDialog.setLayoutData(new GridData(GridData.FILL_BOTH));
		compSettingsDialog.setLayout(new GridLayout(1, false));
		try {
			settingsForm=new SettingsForm(settingsForAlgorithm, compSettingsDialog);
		} catch (TotalADSGeneralException e) {
			msgErr.setMessage(e.getMessage());
			msgErr.open();
			cancelPressed();
		}
		
		return compSuper;
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
	 */
	@Override
	  protected boolean isResizable() {
	    return false;
	  }
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	  protected void okPressed() {
	     try {
			
	    	settingsForAlgorithm= settingsForm.getSettings();
			algorithm.saveTestSettings(settingsForAlgorithm, modelName, DBMSFactory.INSTANCE.getDataAccessObject());
		
	     } catch (TotalADSGeneralException ex) {
			setErrorMessage(ex.getMessage());
			return;
		} catch (TotalADSDBMSException ex) {
			setErrorMessage(ex.getMessage());
			return;
		}
	     
	    super.okPressed();
	  }
	

}
