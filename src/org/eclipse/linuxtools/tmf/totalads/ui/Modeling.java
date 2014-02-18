package org.eclipse.linuxtools.tmf.totalads.ui;

import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


public class Modeling {
	
	public Modeling(CTabFolder tabFolderDetector) throws SecurityException, NoSuchMethodException{
		ScrolledComposite scrolCompModel=new ScrolledComposite(tabFolderDetector, SWT.H_SCROLL | SWT.V_SCROLL);
		
		CTabItem tbtmModeling = new CTabItem(tabFolderDetector, SWT.NONE);
		tbtmModeling.setText("Modeling");
		
		GridLayout gridTwoColumns=new GridLayout(4,false);
		
		
		Composite comptbtmModeling = new Composite(scrolCompModel, SWT.NONE);
		comptbtmModeling.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		comptbtmModeling.setLayout(gridTwoColumns);
		
		tbtmModeling.setControl(scrolCompModel);
		
		selectTraces(comptbtmModeling);
		slectTraceTypeandDatabase(comptbtmModeling);
		Class []parameterTypes= new Class[1];
		parameterTypes[0]=IDetectionModels[].class;
		Method modelObserver=Modeling.class.getMethod("observeSelectedModels", parameterTypes);
		ModelSelector mdlSelector=new ModelSelector(comptbtmModeling,this,modelObserver);
		
		validation(comptbtmModeling);
	    
		buildModel(comptbtmModeling);
		
	    ProgressConsole progConsole=new ProgressConsole(comptbtmModeling);
		scrolCompModel.setContent(comptbtmModeling);
		 // Set the minimum size
		scrolCompModel.setMinSize(600, 600);
	    // Expand both horizontally and vertically
		scrolCompModel.setExpandHorizontal(true);
		scrolCompModel.setExpandVertical(true);

	}
	/**
	 * 
	 * @param comptbtmModeling
	 */
	public void selectTraces(Composite comptbtmModeling){
		/**
		 * Group modeling type and traces
		 */
		Group grpTracesModeling=new Group(comptbtmModeling, SWT.NONE);
		grpTracesModeling.setText("Select Training Traces");
		grpTracesModeling.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
		grpTracesModeling.setLayout(new GridLayout(1,false));//gridTwoColumns);
		
		//Button btnModelingTrain = new Button(grpTracesModeling, SWT.CHECK);
		//btnModelingTrain.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		//btnModelingTrain.setText("Training");
		
		//Button btnModelingValidation = new Button(grpTracesModeling, SWT.CHECK);
		//btnModelingValidation.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		//btnModelingValidation.setText("Validation");
		
		Text txtTrainingTraces = new Text(grpTracesModeling, SWT.BORDER);
		txtTrainingTraces.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		Button btnTrainingBrowse =new Button(grpTracesModeling, SWT.NONE);
		btnTrainingBrowse.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false));
		btnTrainingBrowse.setText("Browse Directory");
		
		//---------
		MultipleTracesLoader multLoader=new MultipleTracesLoader();
		/**
		 * End group modeling type and traces
		 */
	}
	
	
	/**
	 * 
	 * @return
	 */
	public void observeSelectedModels(IDetectionModels []models){
		
		//TableItem []tblItem= tblAnalysisTraceList.getSelection();
		//String trace=tblItem[0].getText(0);
		//System.out.println(trace);
		
		for (int modlCount=0; modlCount<models.length;modlCount++){
			System.out.println(models[modlCount].getName());
		}
		//return trace;
		
	}
	
	
	/**
	 * 
	 * @param comptbtmModeling
	 */
	public void slectTraceTypeandDatabase(Composite comptbtmModeling){
		/**
		 * Group modeling type and traces
		 */
		Group grpTraceTypesAndDB=new Group(comptbtmModeling, SWT.NONE);
		grpTraceTypesAndDB.setText("Trace Type and DB");
		grpTraceTypesAndDB.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
		grpTraceTypesAndDB.setLayout(new GridLayout(2,false));//gridTwoColumns);
				
		Label lblTraceType= new Label(grpTraceTypesAndDB, SWT.BORDER);
		lblTraceType.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false,1,1));
		lblTraceType.setText("Select Trace Type:");
		
		Combo cmbTraceTypes= new Combo(grpTraceTypesAndDB,SWT.BORDER);
		cmbTraceTypes.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		cmbTraceTypes.add("LTTng Kernel");
		cmbTraceTypes.add("LTTng UST");
		cmbTraceTypes.add("Regular Expression");
		cmbTraceTypes.select(1);
		
		Label lblDB=new Label(grpTraceTypesAndDB, SWT.BORDER);
		lblDB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false,1,1));
		lblDB.setText("Enter DB Name (Optional):");
		Text txtModelingTraces = new Text(grpTraceTypesAndDB, SWT.BORDER);
		txtModelingTraces.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		
		//---------
		/**
		 * End group modeling type and traces
		 */
	}
	
	
	/**
	 * 
	 * @param comptbtmModeling
	 */
	public void validation(Composite comptbtmModeling){
		/**
		 * Group modeling type and traces
		 */
		Group validation=new Group(comptbtmModeling, SWT.NONE);
		validation.setText("Validation");
		validation.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,2));
		validation.setLayout(new GridLayout(2,false));//gridTwoColumns);
				
		Button radioBtnCrossVal=new Button(validation, SWT.RADIO);
		radioBtnCrossVal.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,2,1));
		radioBtnCrossVal.setText("Cross Validation");
		
		
		Label lblCrossVal=new Label(validation, SWT.BORDER);
		lblCrossVal.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false,1,1));
		lblCrossVal.setText("Specify Folds:");
		Text txtCrossVal = new Text(validation, SWT.BORDER);
		txtCrossVal.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		txtCrossVal.setText("3");
		
		Button radioBtnVal=new Button(validation, SWT.RADIO);
		radioBtnVal.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,2,1));
		radioBtnVal.setText("Validation");
		
		Button btnValidationBrowse =new Button(validation, SWT.NONE);
		btnValidationBrowse.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,false,false));
		btnValidationBrowse.setText("Browse Directory");
		Text txtValidationTraces = new Text(validation, SWT.BORDER);
		txtValidationTraces.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		
	
		
		//---------
		/**
		 * End group modeling type and traces
		 */
	}
	
	public void buildModel(Composite comptbtmModeling){
		Button btnBuildModel=new Button(comptbtmModeling,SWT.NONE);
		btnBuildModel.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,true,false,4,1));
		btnBuildModel.setText("Start Building the Model");
	}
}
