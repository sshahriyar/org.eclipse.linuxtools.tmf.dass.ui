package org.eclipse.linuxtools.tmf.totalads.ui;


import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;




public class ModelSelector {
	Group grpAnalysisModelSelection=null;
	Button btnAnalysisEvaluateModels=null;
	Tree treeAnalysisModels=null;
	MessageBox msgBox;
	TreeItem currentlySelectedTreeItem=null;
	IDetectionModels currentlySelectedModel;
//	Object instanceOfListener;
	public ModelSelector(Composite comptbtmAnalysis){
		
		//this.listener=function;
		//this.instanceOfListener=object;
		/**
		 *  Group model selection
		 */
		
		grpAnalysisModelSelection=new Group(comptbtmAnalysis,SWT.NONE);	
		grpAnalysisModelSelection.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,3));
		grpAnalysisModelSelection.setLayout(new GridLayout(2,false));
		grpAnalysisModelSelection.setText("Select an Algorithm");
		
		treeAnalysisModels = new Tree(grpAnalysisModelSelection, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION| SWT.V_SCROLL | SWT.H_SCROLL);
		
		treeAnalysisModels.setLinesVisible(true);
		treeAnalysisModels.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,2,6));
		
		TreeItem treeItmAnom = new TreeItem(treeAnalysisModels, SWT.NONE);
		treeItmAnom.setText("Anomaly Detection");
		
		populateTreeItems(treeItmAnom,ModelTypeFactory.ModelTypes.Anomaly);
		treeItmAnom.setExpanded(true);
		TreeItem treeItmClassf = new TreeItem(treeAnalysisModels, SWT.NONE);
		treeItmClassf.setText("Classification");
		populateTreeItems(treeItmClassf,ModelTypeFactory.ModelTypes.Classification);
		treeItmClassf.setExpanded(true);
		
		
		treeAnalysisModels.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem item=(TreeItem)e.item;
			      
				if (item.getParentItem()==null)
						item.setChecked(false);
				else{
						if (currentlySelectedTreeItem!=null)
								 currentlySelectedTreeItem.setChecked(false);
						 item.setChecked(true);
						 currentlySelectedTreeItem=item;
						 currentlySelectedModel= (IDetectionModels)currentlySelectedTreeItem.getData();
				}
					 
						
				
			
			}
		});
		
		
		
	    
		/**
		 * End group model selection 
		*/
	}
	
	/**
	 * populates the tree with the list of models from the model factory
	 */
	private void populateTreeItems(TreeItem treeItem,ModelTypeFactory.ModelTypes modelType){
			///////data
			ModelTypeFactory  modFac=ModelTypeFactory.getInstance();
			
		    // populating anomaly detection models		
		    
			IDetectionModels []models  = modFac.getModels(modelType);
			
			if (models!=null){
				TreeItem []items=new TreeItem[models.length];
				for (int i=0;i <items.length;i++){
					items[i]=new TreeItem(treeItem,SWT.NONE);
					items[i].setText(models[i].getName());
					items[i].setData(models[i]);
					
				}
			}
			   
		    
	}
	

	/**
	 * Checks  selection of a model in the tree
	 */
	private boolean checkItemSelection(){
		
		if (currentlySelectedTreeItem ==null )
				return false;
		else
				return true;
		
	}
	/** Checks if database exists or not
	 * 
	 * @return
	 */
	private boolean checkDBExistence(String database){
		
			return Configuration.connection.datbaseExists(database);
			
	}
	/**
	 * This function trains a model
	 * @param trainDirectory
	 * @throws Exception
	 */
	public void trainAndValidateModels(String trainDirectory, String validationDirectory, ITraceTypeReader traceReader, String database,
				Boolean isCreateDB, ProgressConsole console ) throws TotalADSUiException,Exception {
		
		// First, verify selections
		Boolean isLastTrace=false;
				
		if (!checkItemSelection())
			throw new TotalADSUiException("Please, first select an algorithm!");
       
		
		File fileList[]=getDirectoryHandler(trainDirectory);// Get a file and a db handler
		DBMS connection=Configuration.connection;
		
		
		try{ //Check for valid trace type reader and traces before creating a database
			traceReader.getTraceIterator(fileList[0]);
		}catch (Exception ex){
			String message="Invalid trace reader and traces: "+ex.getMessage();
			throw new TotalADSUiException(message);
		}
		
		if(isCreateDB){
			if (database.contains("_"))
				throw new TotalADSUiException("Databse name cannot contain underscore \"_\"");
			else{
				database=database.trim()+"_"+currentlySelectedModel.getAcronym()+"_"+ traceReader.getAcronym();
				database=database.toUpperCase();
				currentlySelectedModel.createDatabase(database, connection);// throws TotalADSUiException
			}
		}
		else if (!checkDBExistence(database))
			throw new TotalADSUiException("Database does not exist!");
							
		// Second, start training
		console.clearText();
		console.printTextLn("Training the model....");
		
		for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
	
			if (trcCnt==fileList.length-1)
						 isLastTrace=true;
			 
			ITraceIterator trace=traceReader.getTraceIterator(fileList[trcCnt]);// get the trace
	 		
			console.printTextLn("Processing file "+fileList[trcCnt].getName());
	 		currentlySelectedModel.train(trace, isLastTrace, database,connection, console, null);
		
		}
		//Third, start Validation
		validateModels(validationDirectory, traceReader, database, console);
	}

/**
 * It  validates a model for a given database of that model
 * @param validationDirectory
 * @throws Exception
 */
	private void validateModels(String validationDirectory, ITraceTypeReader traceReader, String database,
				ProgressConsole console) throws TotalADSUiException,Exception {
		
	
		
		File fileList[]=getDirectoryHandler(validationDirectory);
		
		
		// process now
		console.printTextLn("Starting validation....");
		
		Boolean isLastTrace=false;
		
		for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
			 // get the trace
			if (trcCnt==fileList.length-1)
					isLastTrace=true;
			
 			ITraceIterator trace=traceReader.getTraceIterator(fileList[trcCnt]);
 		
 			console.printTextLn("Processing file "+fileList[trcCnt].getName());
 			
	 		currentlySelectedModel.validate(trace, database, Configuration.connection, isLastTrace, console );

		}
		
		
		
	}
	
	/**
	 * 
	 * @param trainDirectory
	 * @return
	 */
	private File[] getDirectoryHandler(String trainDirectory){
		File traces=new File(trainDirectory);
		File []fileList;
		
		
		if (traces.isDirectory())
	            fileList=traces.listFiles();
	    else{
	            fileList= new File[1];
	            fileList[0]=traces;
	    }
		return fileList;
	}
}
