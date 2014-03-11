package org.eclipse.linuxtools.tmf.totalads.ui;

import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class Modeling {
	ModelSelector modelSelector=null;
	TracingTypeSelector traceTypeSelector=null;
	Text txtTrainingTraces;
	Text txtValidationTraces=null;
	MessageBox msgBox;
	//String selectedDB;
	Combo cmbDBNames;
	Text txtNewDBName;
	ProgressConsole  progConsole;
	
	public Modeling(CTabFolder tabFolderDetector){
		
		
		ScrolledComposite scrolCompModel=new ScrolledComposite(tabFolderDetector, SWT.H_SCROLL | SWT.V_SCROLL);
		
		CTabItem tbtmModeling = new CTabItem(tabFolderDetector, SWT.NONE);
		tbtmModeling.setText("Modeling");
		
		GridLayout gridTwoColumns=new GridLayout(4,false);
		
		
		Composite comptbtmModeling = new Composite(scrolCompModel, SWT.NONE);
		comptbtmModeling.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		comptbtmModeling.setLayout(gridTwoColumns);
		
		tbtmModeling.setControl(scrolCompModel);
		
		selectTrainingTraces(comptbtmModeling);
		selectTraceTypeandDatabase(comptbtmModeling);
		
		
		//Class []parameterTypes= new Class[1];
		//parameterTypes[0]=IDetectionModels[].class;
		//Method modelObserver=Modeling.class.getMethod("observeSelectedModels", parameterTypes);
    	modelSelector=new ModelSelector(comptbtmModeling);
		
		validation(comptbtmModeling);
	    
		buildModel(comptbtmModeling);
		
	    progConsole=new ProgressConsole(comptbtmModeling);
		
	    scrolCompModel.setContent(comptbtmModeling);
		 // Set the minimum size
		scrolCompModel.setMinSize(600, 600);
	    // Expand both horizontally and vertically
		scrolCompModel.setExpandHorizontal(true);
		scrolCompModel.setExpandVertical(true);
		
		msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
			           ,SWT.ICON_ERROR|SWT.OK);
	
	}
	/**
	 * 
	 * @param comptbtmModeling
	 */
	//StringBuilder trainingTracePath=new StringBuilder();
	
	public void selectTrainingTraces(Composite comptbtmModeling){
		/**
		 * Group modeling type and traces
		 */
		Group grpTracesModeling=new Group(comptbtmModeling, SWT.NONE);
		grpTracesModeling.setText("Select Training Traces");
		grpTracesModeling.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
		grpTracesModeling.setLayout(new GridLayout(1,false));//gridTwoColumns);
		

		txtTrainingTraces = new Text(grpTracesModeling, SWT.BORDER);
		txtTrainingTraces.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		
		new TraceBrowser(grpTracesModeling,txtTrainingTraces,new GridData(SWT.LEFT,SWT.TOP,false,false));
		
		//Button btnTrainingBrowse =new Button(grpTracesModeling, SWT.NONE);
		//btnTrainingBrowse.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false));
		//btnTrainingBrowse.setText("Browse Directory");
		
		
	}
	
	
	
	
	/**
	 * 
	 * @param comptbtmModeling
	 */
	//Text txtModelingTraces;
	public void selectTraceTypeandDatabase(Composite comptbtmModeling){
		/**
		 * Group modeling type and traces
		 */
		Group grpTraceTypesAndDB=new Group(comptbtmModeling, SWT.NONE);
		grpTraceTypesAndDB.setText("Trace Type and DB");
		grpTraceTypesAndDB.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
		grpTraceTypesAndDB.setLayout(new GridLayout(3,false));//gridTwoColumns);
				
		Label lblTraceType= new Label(grpTraceTypesAndDB, SWT.BORDER);
		lblTraceType.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false,1,1));
		lblTraceType.setText("Select the Trace Type");
		
		traceTypeSelector=new TracingTypeSelector(grpTraceTypesAndDB);
		//Combo cmbTraceTypes= new Combo(grpTraceTypesAndDB,SWT.BORDER);
		//cmbTraceTypes.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		//cmbTraceTypes.add("LTTng Kernel");
		//cmbTraceTypes.add("LTTng UST");
		//cmbTraceTypes.add("Regular Expression");
		//cmbTraceTypes.select(0);
		Label emptyLabel=new Label(grpTraceTypesAndDB, SWT.BORDER);// An empty label for a third cell
		emptyLabel.setVisible(false);
		
		Label lblDB=new Label(grpTraceTypesAndDB, SWT.BORDER);
		lblDB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false,1,1));
		lblDB.setText("Select or Enter a DB Name");
		
		cmbDBNames= new Combo(grpTraceTypesAndDB,SWT.READ_ONLY | SWT.V_SCROLL);
		cmbDBNames.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		
		
		populateComboWithDatabaseList();
		 
		
		txtNewDBName = new Text(grpTraceTypesAndDB, SWT.BORDER);
		txtNewDBName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		//txtNewDBName.setText("Enter");
		txtNewDBName.setTextLimit(7);
		//txtNewDBName.setEnabled(false);
		
		
		cmbDBNames.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//if (e.text.equalsIgnoreCase("Enter New  Name")){
				if ( cmbDBNames.getSelectionIndex()==0){
					txtNewDBName.setText("");
					txtNewDBName.setEnabled(true);
					
					
				}
				
				else {
					
					txtNewDBName.setText("Enter");
					txtNewDBName.setEnabled(false);
										
					//selectedDB=cmbDBNames.getItem(cmbDBNames.getSelectionIndex());
					
				}
			}
		});
		
	  // Add an observer to DBMS connection to automatically update 
	 //the list of databases when new ones are created and old ones are deleted
	   Configuration.connection.addObserver(new Observer() {
				@Override
				public void update() {
					Display.getDefault().asyncExec(new Runnable(){
						@Override
						public void run(){
							populateComboWithDatabaseList();
						}
					});
			
				}
	   });
		

		/**
		 * End group modeling type and traces
		 */
	}
	/**
	 * Populates the combo box with models (database) list
	 */
	private void populateComboWithDatabaseList(){
		
				cmbDBNames.removeAll(); // first clear it
				cmbDBNames.add("Enter a new  name");
				//Populate combo box
				List<String> modelsList=Configuration.connection.getDatabaseList();
				for (int j=0; j<modelsList.size();j++)
					cmbDBNames.add(modelsList.get(j));
				// slect the first item in the combo box		
				cmbDBNames.select(0);
	}
	
	/**
	 * 
	 * @param comptbtmModeling
	 */
	public void validation(Composite comptbtmModeling){
		/**
		 * Group modeling type and traces
		 */
		Group grpValidation=new Group(comptbtmModeling, SWT.NONE);
		grpValidation.setText("Validation");
		grpValidation.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,2));
		grpValidation.setLayout(new GridLayout(2,false));//gridTwoColumns);
				
		Button radioBtnCrossVal=new Button(grpValidation, SWT.RADIO);
		radioBtnCrossVal.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,2,1));
		radioBtnCrossVal.setText("Cross Validation");
		
		
		Label lblCrossVal=new Label(grpValidation, SWT.BORDER);
		lblCrossVal.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false,1,1));
		lblCrossVal.setText("Specify Folds:");
		Text txtCrossVal = new Text(grpValidation, SWT.BORDER);
		txtCrossVal.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		txtCrossVal.setText("3");
		
		Button radioBtnVal=new Button(grpValidation, SWT.RADIO);
		radioBtnVal.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,2,1));
		radioBtnVal.setText("Validation");
		radioBtnVal.setSelection(true);
		
		
		TraceBrowser traceBrowser= new TraceBrowser(grpValidation,new GridData(SWT.RIGHT,SWT.TOP,false,false));
		//Button btnValidationBrowse =new Button(grpValidation, SWT.NONE);
		//btnValidationBrowse.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,false,false));
		//btnValidationBrowse.setText("Browse Directory");
		
		txtValidationTraces = new Text(grpValidation, SWT.BORDER);
		//txtValidationTraces.setText("");
		txtValidationTraces.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		traceBrowser.setTextBox(txtValidationTraces);
		
	
		
		//---------
		/**
		 * End group modeling type and traces
		 */
	}
	/**
	 * Method to handle model building button
	 * @param comptbtmModeling
	 */
	Button btnBuildModel;
	public void buildModel(Composite comptbtmModeling){
		
		btnBuildModel=new Button(comptbtmModeling,SWT.NONE);
		btnBuildModel.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,true,false,4,1));
		btnBuildModel.setText("Start building the model");
		btnBuildModel.addMouseListener(new MouseAdapter() {
		
		@Override
		public void mouseUp(MouseEvent e) {
				
						String selectedDB;
						Boolean isNewDB;
						btnBuildModel.setEnabled(false);
						String trainingTraces=txtTrainingTraces.getText().trim();
						String validationTraces=txtValidationTraces.getText().trim();
						
						if (trainingTraces.isEmpty()){
						
							msgBox.setMessage("Please, select training traces.");
							msgBox.open();
							btnBuildModel.setEnabled(true);
							return;
						}
						else if (validationTraces.isEmpty()){
							
							msgBox.setMessage("Please, select validation traces.");
							msgBox.open();
							btnBuildModel.setEnabled(true);
							return;
						}
						
						   // get the database name from the text box or combo
						 else if (txtNewDBName.getEnabled()==false){
								selectedDB=cmbDBNames.getItem(cmbDBNames.getSelectionIndex());
								isNewDB=false;
						 }
						 else if (txtNewDBName.getText().isEmpty()){
								msgBox.setMessage("Please, enter a database name.");
								msgBox.open();
								btnBuildModel.setEnabled(true);
								return;
						} else{
								 selectedDB=txtNewDBName.getText();
								 isNewDB=true;
						}
						
						ITraceTypeReader traceReader=traceTypeSelector.getSelectedType();	 
						BackgroundModeling modeling=new BackgroundModeling(trainingTraces, selectedDB, isNewDB, validationTraces,traceReader);
						modeling.start();
				
				
			}
		 });
	}

	private class BackgroundModeling extends Thread{
		String trainingTraces;
		String selectedDB;
		Boolean isNewDB;
		String validationTraces;
		ITraceTypeReader traceReader;

		public BackgroundModeling(String trainingTraces,String selectedDB, Boolean isNewDB, String validationTraces,ITraceTypeReader traceReader){
			this.trainingTraces=trainingTraces;
			this.selectedDB=selectedDB;
			this.isNewDB=isNewDB;
			this.validationTraces=validationTraces;
			this.traceReader=traceReader;
		}
			
		@Override
		public void run(){
				String msg=null;
				
				try {
					
						modelSelector.trainAndValidateModels(trainingTraces, validationTraces, traceReader,selectedDB,isNewDB,progConsole);
						
					
				} 
				catch(TotalADSUiException ex){// handle UI exceptions here
					if (ex.getMessage()==null)
						msg="Severe error: see log.";	
					else
						msg=ex.getMessage();
				}
				catch (Exception ex) { // handle all other exceptions here and log them too.
										//UI exceptions are simply notifications--no need to log them
										
					if (ex.getMessage()==null)
						msg="Severe error: see log.";	
					else
						msg=ex.getMessage();
					ex.printStackTrace();
				}
				finally{
					final String exception=msg;
					
					
					 Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							if (exception!=null){ // if there has been any exception then show its message
								msgBox.setMessage(exception);
								msgBox.open();
							}
							btnBuildModel.setEnabled(true);
							//if (isNewDB)
								//populateComboWithDatabaseList(); //refresh the combo box with new database list
							
						}
					});
					
					
				}
			}
	}
	/*** **/
	
	
}
