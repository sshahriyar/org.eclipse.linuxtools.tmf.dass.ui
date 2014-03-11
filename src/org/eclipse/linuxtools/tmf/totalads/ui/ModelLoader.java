package org.eclipse.linuxtools.tmf.totalads.ui;

import java.util.ArrayList;
import java.util.List;
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
				//ModelTypeFactory.getInstance().getModels(modTypes)
				System.out.println(tracePath.toString());
				System.out.println(traceReader.toString());
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
					//items[i].setData(models[i]);
					
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
	
	public void setTrace(StringBuilder tracePath){
		this.tracePath=tracePath;
	}

	public void setTraceTypeSelector(TracingTypeSelector traceTypeSelector){
		this.traceTypeSelector= traceTypeSelector;
	}
}
