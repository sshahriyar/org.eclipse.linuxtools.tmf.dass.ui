package org.eclipse.linuxtools.tmf.totalads.ui;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeAdapter;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

import org.eclipse.swt.widgets.Label;

public class ModelLoader {
	Group grpAnalysisModelSelection=null;
	Button btnAnalysisEvaluateModels=null;
	Tree treeAnalysisModels=null;
	Button btnSettings;
	Button btnDelete;
	TreeItem currentlySelectedTreeItem=null;
	MessageBox msgBox;
	StringBuilder tracePath;
	TracingTypeSelector traceTypeSelector;
	ResultsAndFeedback resultsAndFeedback;
	
	public ModelLoader(Composite comptbtmAnalysis ){
		

		/**
		 *  Group model selection
		 */
		msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,SWT.ICON_ERROR|SWT.OK);
		
		grpAnalysisModelSelection=new Group(comptbtmAnalysis,SWT.NONE);	
		grpAnalysisModelSelection.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,5));
		grpAnalysisModelSelection.setLayout(new GridLayout(2,false));
		grpAnalysisModelSelection.setText("Select a Model");
		
		treeAnalysisModels = new Tree(grpAnalysisModelSelection, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION| SWT.V_SCROLL | SWT.H_SCROLL);
		treeAnalysisModels.setLinesVisible(true);
		treeAnalysisModels.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,1,5));
		
		populateTreeWithModels();
		
	
		treeAnalysisModels.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem item=(TreeItem)e.item;
	     		if (currentlySelectedTreeItem!=null)
						 currentlySelectedTreeItem.setChecked(false);
				item.setChecked(true);
				currentlySelectedTreeItem=item;
				
			}
			
		});
		
		
	  
		Composite compModelSelection=new Composite(grpAnalysisModelSelection, SWT.NONE);
	    compModelSelection.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		compModelSelection.setLayout(new GridLayout(1,false));
	    
		btnAnalysisEvaluateModels=new Button(compModelSelection, SWT.NONE);
		btnAnalysisEvaluateModels.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,1,1));
		btnAnalysisEvaluateModels.setText("Evaluate");
	
		btnAnalysisEvaluateModels.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				
				ITraceTypeReader traceReader=traceTypeSelector.getSelectedType();
				
				ModelTypeFactory modFac= ModelTypeFactory.getInstance();
				String database=currentlySelectedTreeItem.getText();
				String modelKey=database.split("_")[1];
				IDetectionModels model= modFac.getModelyByAcronym(modelKey);
				
				resultsAndFeedback.clearTree();
				btnAnalysisEvaluateModels.setEnabled(false);
				btnSettings.setEnabled(false);
				btnDelete.setEnabled(false);
				
				BackgroundTesting testTheModel=new BackgroundTesting(tracePath.toString(), traceReader, model, database);
				testTheModel.start();
				
			}
		});
		
		btnSettings=new Button(compModelSelection, SWT.NONE);
		btnSettings.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,1,1));
		btnSettings.setText("Settings");
		
		btnDelete=new Button(compModelSelection, SWT.NONE);
		btnDelete.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,1,1));
		btnDelete.setText("Delete");
			
		btnDelete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (!checkItemSelection()){
					msgBox.setMessage("Please, select a model first!");
					msgBox.open();
				} else{
					
					MessageBox msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
								SWT.ICON_INFORMATION|SWT.YES|SWT.NO);
					
					msgBox.setMessage("Do you want to delete the model "+currentlySelectedTreeItem.getText()+ "?");
					if (msgBox.open()==SWT.YES)
						Configuration.connection.deleteDatabase(currentlySelectedTreeItem.getText());
					
						
					//populateTreeWithModels();
				}
			}
		});
		
		Configuration.connection.addObserver(new Observer() {
			@Override
			public void update() {
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run(){
						populateTreeWithModels();
					}
				});
		
			}
		});
		
		/**
		 * End group model selection 
		*/
	}
	/**
	 * Populates the tree with the list of models (databases) from the database
	 */
	private void populateTreeWithModels(){
	///////data
			
			List <String> modelsList= Configuration.connection.getDatabaseList();
			treeAnalysisModels.removeAll();
		    if (modelsList!=null || modelsList.size()>0){
				TreeItem []items=new TreeItem[modelsList.size()];
				for (int i=0;i <items.length;i++){
					items[i]=new TreeItem(treeAnalysisModels,SWT.NONE);
					items[i].setText(modelsList.get(i));
									
				}
			}
		    currentlySelectedTreeItem=null;
	}

	/**
	* Checks selection of a model in the tree
	*/
	private boolean checkItemSelection(){
		
		if (currentlySelectedTreeItem ==null )
				return false;
		else
				return true;
		
	}
	/**
	 * Assigns TracePath object from Diagnosis class to a local variable
	 * @param tracePath
	 */
	public void setTrace(StringBuilder tracePath){
		this.tracePath=tracePath;
	}
	/**
	 *  Assigns TraceTypeSelector object from Diagnosis class to a local object
	 * @param traceTypeSelector
	 */
	public void setTraceTypeSelector(TracingTypeSelector traceTypeSelector){
		this.traceTypeSelector= traceTypeSelector;
	}
	
	/**
	 * Assigns ResultsAndFeddback object from Diagnosis class to a local variable
	 * @param resultsAndFeedback
	 */
	public void setResultsAndFeedback(ResultsAndFeedback resultsAndFeedback){
		this.resultsAndFeedback=resultsAndFeedback;
	}
	
	/**
	 * This calss tests a model by launching a thread
	 * @param testDirectory
	 * @throws Exception
	 */
	
	private class BackgroundTesting extends Thread{
		String testDirectory;
		ITraceTypeReader traceReader;
		IDetectionModels model;
		String database;
		
		public BackgroundTesting(String testDirectory, ITraceTypeReader traceReader, IDetectionModels model, String database){
			this.testDirectory=testDirectory;
			this.traceReader=traceReader;
			this.model=model;
			this.database=database;
		}
		
		
			
		@Override
		public void run(){
				String msg=null;
				
				try {
					
					testTheModel(testDirectory, traceReader, model, database);
								
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
							btnAnalysisEvaluateModels.setEnabled(true);
							btnSettings.setEnabled(true);
							btnDelete.setEnabled(true);
							
						}
					});
					
					
				}//End finally
		}
				
		/**
		 * Tests the model
		 * @param testDirectory
		 * @param traceReader
		 * @param model
		 * @param database
		 * @throws TotalADSUiException
		 * @throws Exception
		 */
		public void testTheModel(String testDirectory, ITraceTypeReader traceReader, IDetectionModels model, String database ) throws TotalADSUiException,Exception {
					
					
				// First verify selections
				Boolean isLastTrace=false;
						
				if (!checkItemSelection())
					throw new TotalADSUiException("Please, first select a model!");
		       
				
				File fileList[]=getDirectoryHandler(testDirectory);// Get a file and a db handler
				DBMS connection=Configuration.connection;
				
				
				try{ //Check for valid trace type reader and traces before creating a database
					traceReader.getTraceIterator(fileList[0]);
				}catch (Exception ex){
					String message="Invalid trace reader and traces: "+ex.getMessage();
					throw new TotalADSUiException(message);
				}
				
				
				// Second, start testing
				
				for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
			
					if (trcCnt==fileList.length-1)
								 isLastTrace=true;
					 
					ITraceIterator trace=traceReader.getTraceIterator(fileList[trcCnt]);// get the trace
			 					
			 		final IDetectionModels.Results results= model.test(trace, database, connection);
			 		final String traceName=fileList[trcCnt].getName();
			 		
			 		Display.getDefault().syncExec(new Runnable() {
						
						@Override
						public void run() {
							resultsAndFeedback.addTraceResult(traceName, results);
							
						}
					});
			 		
				}
		
		
		
		}
		
		/**
		 * Get a Directory handle, if there is only one file it returns an array of size one
		 * @param trainDirectory
		 * @return File[]
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
		
	// End of BackgroundTesting class	
	}
	
//End of ModelLoader class	
}
