package org.eclipse.linuxtools.tmf.totalads.ui.datamodels;


import org.eclipse.core.internal.localstore.IsSynchronizedVisitor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmTypes;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;


/**
 * This class creates the Algorithm Selection page
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class ModelNamePage extends WizardPage {
	private Label lblModelName;
	private StyledText txtDescription;
	private Text txtModelName;
	private Composite compModel;
	private Boolean isModelOK;
	private MessageBox msgBoxErr;
	/**
	 * Constructor
	 */
	public ModelNamePage() {
		super("Model Name");
		setTitle("Enter the name of the model");
		isModelOK=false;
		msgBoxErr= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
				SWT.ICON_ERROR);
		
	}
	//
	//Creates GUI widgets
	//
	@Override
	public void createControl(Composite compParent) {
		compModel=new Composite(compParent, SWT.NONE);
		compModel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		compModel.setLayout(new GridLayout(3,false));
		
		lblModelName= new Label(compModel,SWT.NONE);
		lblModelName.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false,false));
		lblModelName.setText("Model Name");
		
		txtModelName=new Text(compModel, SWT.BORDER);
		txtModelName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,false));
		txtModelName.setTextLimit(10);
		
		// Empty labels used for styling
		Label lbl1=new Label (compModel,SWT.NONE);
		lbl1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,false));
		Label lbl2=new Label (compModel,SWT.NONE);
		lbl2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,false,3,2));
		
		
		txtDescription= new StyledText(compModel,SWT.NONE|SWT.MULTI|SWT.READ_ONLY| SWT.WRAP|SWT.V_SCROLL);
		txtDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,false,3,8));
		txtDescription.setJustify(true);
		txtDescription.setText("A model is actually a database in the database management system. A model's name is\n"
				+" the name of that database. This model creation wizard only creates an empty model.\n"
				+ " \n"
				+ " A model is built when an algorithm is trained on the the normal execution traces (or logs).\n"
				+ "This is done  in the modeling view or the live monitor (training and testing) view."
				);
		
		
		setControl(compModel);
		setPageComplete(false);
		
		//Event handler
		txtModelName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				
				
				if (txtModelName.getText().matches(".*[@;:,!~`()*&^%_<>\"{}|_=+\\\\?\\/\\[\\]].*")	){
					msgBoxErr.setMessage("A model name cannot contain underscore or special characters");
					msgBoxErr.open();
					txtModelName.setText(txtModelName.getText().substring(0,txtModelName.getText().length()-1));
					txtModelName.setSelection(txtModelName.getText().length());
					isModelOK=true;
				} 
				
				if (txtModelName.getText().isEmpty()){
					isModelOK=false;
				}else
					isModelOK=true;
				
				setPageComplete(true);
			}
		});
	}
	
	//
	//This function enables next button
	//
	@Override
	public boolean canFlipToNextPage() {
		if (isModelOK)
				return true;
		else
				return false;
	}
	
	
	/**
	 * Returns the name of the model typed by the user
	 * @return Model name
	 */
	public String gettheModel(){
		return txtModelName.getText();
	}
}
