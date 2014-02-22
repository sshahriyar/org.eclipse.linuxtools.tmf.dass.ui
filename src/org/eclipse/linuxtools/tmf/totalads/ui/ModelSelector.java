package org.eclipse.linuxtools.tmf.totalads.ui;

import java.util.ArrayList;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

public class ModelSelector {
	Group grpAnalysisModelSelection=null;
	Button btnAnalysisEvaluateModels=null;
	Tree treeAnalysisModels=null;
	Method listener;
	Object instanceOfListener;
	public ModelSelector(Composite comptbtmAnalysis, Object object, Method function){
		
		this.listener=function;
		this.instanceOfListener=object;
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
		
		//TreeItem item4 = new TreeItem(treeItmClassf, SWT.NONE);
	    //item4.setText("Decision Tree");
	    treeItmClassf.setExpanded(true);
		
		/*btnAnalysisEvaluateModels=new Button(grpAnalysisModelSelection, SWT.NONE);
		btnAnalysisEvaluateModels.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				 TreeItem []items= treeAnalysisModels.getSelection();
				 if (items.length > 0){
					 
					 ArrayList<IDetectionModels> selectedModelsList= new ArrayList<IDetectionModels>();
					
					 for (int i=0;i<items.length;i++){
						 if (items[i].getParentItem()!=null && items[i].getChecked()){
							 selectedModelsList.add((IDetectionModels)items[i].getData());
						 }
					 }
					 if (selectedModelsList.size() > 0 ) {
						
						 	 IDetectionModels [] selectedModels=new IDetectionModels[selectedModelsList.size()];
							 selectedModels=selectedModelsList.toArray(selectedModels);
							 
							 Object []args= {selectedModels};// wrap an array in an array to pass an argument of array
							 	 
							 try {
								listener.invoke(instanceOfListener, args);
							} catch (Exception ex) {
								// TODO Auto-generated catch block
								//MessageBox msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
									//	           ,SWT.ICON_ERROR|SWT.OK);
								//msgBox.setMessage(ex.getMessage());
								//msgBox.open();
								ex.printStackTrace();
							}
								 // model.test(trace, traceName);
					 }
				 }
			}
		});
		
		btnAnalysisEvaluateModels.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,false,false,2,1));
		btnAnalysisEvaluateModels.setText("Develop the Model");
			*/	
		/**
		 * End group model selection 
		*/
	}
	/**
	 * 
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
	 * This function can train multiple models simultaneously
	 * @param trainDirectory
	 * @throws Exception
	 */
	public void trainModels(String trainDirectory, ITraceTypeReader traceReader ) throws Exception{
		
		Boolean isLastTrace=false;
		File fileList[]=getDirectoryHandler(trainDirectory);
		//ITraceTypeReader input = new CTFKernelTraceReader();////------------------
		TreeItem []items= treeAnalysisModels.getSelection();
		if (items ==null)
			 throw new Exception ("Pleas select a model first");
		 
		for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
			 // get the trace
			 StringBuilder trace=traceReader.getTrace(fileList[trcCnt]);
			 // convert trce reader to trace iterator object
			 // pas it to the algorithm
			 // trace iterator. next(string) has next,
			 
			 if (trace.length()>0){
					 /** Getting char representation in memory of StringBuilder trace  
					  * to avoid extra memory consumption and making it final to avoid 
					  * any manipulation from models  */ 
					 //String.class.getDeclaredField("value");
					 Field field = StringBuilder.class.getSuperclass().getDeclaredField("value");
					 field.setAccessible(true);
					 final char[] traceChar = (char[]) field.get(trace);
					 
					 if (trcCnt==fileList.length-1)
						 isLastTrace=true;
					
					 for (int modlCnt=0; modlCnt<items.length;modlCnt++){
						 // check if there is a parent of an item and it is checked
						if (items[modlCnt].getParentItem()!=null && items[modlCnt].getChecked()){
							 		IDetectionModels model= (IDetectionModels)items[modlCnt].getData();
							 		model.train(traceChar, isLastTrace);
						}
						 
					 }
			}
		}
		
		
	}

/**
 * It can validate multiple models simultaneously
 * @param validationDirectory
 * @throws Exception
 */
	public void validateModels(String validationDirectory, ITraceTypeReader traceReader) throws Exception{
		
		
		File fileList[]=getDirectoryHandler(validationDirectory);
		
		TreeItem []items= treeAnalysisModels.getSelection();
		if (items ==null)
			 throw new Exception ("Pleas select a model first");
		
		for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
			 // get the trace
			 StringBuilder trace=traceReader.getTrace(fileList[trcCnt]);
			
			 if (trace.length()>0){ 
					 /** Getting char representation in memory of StringBuilder trace  
					  * to avoid extra memory consumption and making it final to avoid 
					  * any manipulation from models  */ 
					 //String.class.getDeclaredField("value");
					 Field field = StringBuilder.class.getSuperclass().getDeclaredField("value");
					 field.setAccessible(true);
					 final char[] traceChar = (char[]) field.get(trace);
								 
					 for (int modlCnt=0; modlCnt<items.length;modlCnt++){
						 // check if there is a parent of an item and it is checked
						 if (items[modlCnt].getParentItem()!=null && items[modlCnt].getChecked()){
								 		IDetectionModels model= (IDetectionModels)items[modlCnt].getData();
								 		model.validate(traceChar);
						 }
						 
						 
					 }
			}
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
