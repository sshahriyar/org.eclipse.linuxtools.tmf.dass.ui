package org.eclipse.linuxtools.tmf.totalads.ui;

import java.util.ArrayList;
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
		grpAnalysisModelSelection.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		grpAnalysisModelSelection.setLayout(new GridLayout(2,false));
		grpAnalysisModelSelection.setText("Select Models");
		
		treeAnalysisModels = new Tree(grpAnalysisModelSelection, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION| SWT.V_SCROLL | SWT.H_SCROLL);
		treeAnalysisModels.setLinesVisible(true);
		treeAnalysisModels.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,2,6));
		
		TreeItem treeItmAnom = new TreeItem(treeAnalysisModels, SWT.NONE);
		treeItmAnom.setText("Anomaly Detection");
		
		populateTreeItems(treeItmAnom,ModelTypeFactory.ModelTypes.Anomaly);
		
		TreeItem treeItmClassf = new TreeItem(treeAnalysisModels, SWT.NONE);
		treeItmClassf.setText("Classification");
		populateTreeItems(treeItmClassf,ModelTypeFactory.ModelTypes.Classification);
		
		//TreeItem item4 = new TreeItem(treeItmClassf, SWT.NONE);
	    //item4.setText("Decision Tree");
	    treeItmClassf.setExpanded(true);
		
		btnAnalysisEvaluateModels=new Button(grpAnalysisModelSelection, SWT.NONE);
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
		btnAnalysisEvaluateModels.setText("Evaluate Models");
				
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
}
