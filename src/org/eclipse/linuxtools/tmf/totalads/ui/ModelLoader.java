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
import org.eclipse.swt.widgets.Label;

public class ModelLoader {
	Group grpAnalysisModelSelection=null;
	Button btnAnalysisEvaluateModels=null;
	Tree treeAnalysisModels=null;
	Method listener;
	Object instanceOfListener;
	public ModelLoader(Composite comptbtmAnalysis, Object object, Method function){
		
		this.listener=function;
		this.instanceOfListener=object;
		/**
		 *  Group model selection
		 */
		
		grpAnalysisModelSelection=new Group(comptbtmAnalysis,SWT.NONE);	
		grpAnalysisModelSelection.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,5));
		grpAnalysisModelSelection.setLayout(new GridLayout(2,false));
		grpAnalysisModelSelection.setText("Select a Model");
		
		treeAnalysisModels = new Tree(grpAnalysisModelSelection, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION| SWT.V_SCROLL | SWT.H_SCROLL);
		treeAnalysisModels.setLinesVisible(true);
		treeAnalysisModels.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,1,5));
		
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
		Composite compModelSelection=new Composite(grpAnalysisModelSelection, SWT.NONE);
	    compModelSelection.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		compModelSelection.setLayout(new GridLayout(1,false));
	    
		btnAnalysisEvaluateModels=new Button(compModelSelection, SWT.NONE);
		btnAnalysisEvaluateModels.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,1,1));
		btnAnalysisEvaluateModels.setText("Evaluate");
	
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
		
		Button btnSettings=new Button(compModelSelection, SWT.NONE);
		btnSettings.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,1,1));
		btnSettings.setText("Settings");
		
		Button btnDelete=new Button(compModelSelection, SWT.NONE);
		btnDelete.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,1,1));
		btnDelete.setText("Delete");
			
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
