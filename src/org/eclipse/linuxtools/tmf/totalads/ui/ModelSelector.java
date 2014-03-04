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
	//Method listener;
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
		grpAnalysisModelSelection.setText("Select a Model");
		
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
						if (previousTreeItem!=null)
								 previousTreeItem.setChecked(false);
						 item.setChecked(true);
						 previousTreeItem=item;
				}
					 
						
				
			
			}
		});
		
		
		msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
		           ,SWT.ICON_ERROR|SWT.OK);
		
	    
		/**
		 * End group model selection 
		*/
	}
	 TreeItem previousTreeItem;
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
	 * Creates a new database 
	 * @param dataBase
	 * @return
	 */
	private boolean createDatabase(String database, String model, String reader){
		database=database+"_"+model+"_"+reader;
		try {
			Configuration.connection.createDatabase(database);
		} catch (Exception ex) {
			String message=ex.getMessage();
			msgBox.setMessage(message);
			msgBox.open();
			return false;			
		}
		
		return true;
	}
	/**
	 * Checks the selection of the model in the tree
	 */
	private boolean checkItemSelection(){
		
		TreeItem []items= treeAnalysisModels.getSelection();
		if (items ==null || items.length==0){
			msgBox.setMessage("Please, select a model first.");
			msgBox.open();
			return false;
	
		}
		else{
				return true;
		}
	}
	/** Checks if database exists or not
	 * 
	 * @return
	 */
	private boolean checkDBExistence(String database){
		try {
			Configuration.connection.datbaseExists(database);
		} catch (Exception ex) {
			String message=ex.getMessage();
			msgBox.setMessage(message);
			msgBox.open();
			return false;			
		}
		
		return true;
		
		
	}
	/**
	 * This function trains a model
	 * @param trainDirectory
	 * @throws Exception
	 */
	public void trainModels(String trainDirectory, ITraceTypeReader traceReader, String database, Boolean isCreateDB, ProgressConsole console ) throws Exception {
		
		TreeItem item= treeAnalysisModels.getSelection()[0];
		IDetectionModels model= (IDetectionModels)item.getData();
		
		if (!checkItemSelection())
			return;
		
		if(isCreateDB  &&	!createDatabase(database, model.getAcronym(), traceReader.getAcronym()) )
			return;
		else if (!checkDBExistence(database))
			return;
		
			
		Boolean isLastTrace=false;
		File fileList[]=getDirectoryHandler(trainDirectory);
		
		
		for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
			
						 
			 if (trcCnt==fileList.length-1)
						 isLastTrace=true;
			 // get the trace
			 ITraceIterator trace=traceReader.getTraceIterator(fileList[trcCnt]);
	 		
	 		model.train(trace, isLastTrace, database,Configuration.connection, console);
				 
			
		}
		
		
	}

/**
 * It  validates a model for a given database of that model
 * @param validationDirectory
 * @throws Exception
 */
	public void validateModels(String validationDirectory, ITraceTypeReader traceReader, String database, ProgressConsole console) throws Exception {
		
		if (!checkItemSelection() || !checkDBExistence(database))
			return;
				
		File fileList[]=getDirectoryHandler(validationDirectory);
		TreeItem item= treeAnalysisModels.getSelection()[0];
		IDetectionModels model= (IDetectionModels)item.getData();	 
		Boolean isLastTrace=false;
		for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
			 // get the trace
			if (trcCnt==fileList.length-1)
					isLastTrace=true;
			
 			ITraceIterator trace=traceReader.getTraceIterator(fileList[trcCnt]);
	 		
	 		model.validate(trace, database, Configuration.connection, isLastTrace, console );

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
