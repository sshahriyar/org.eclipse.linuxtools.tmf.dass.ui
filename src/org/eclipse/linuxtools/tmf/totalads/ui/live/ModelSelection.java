package org.eclipse.linuxtools.tmf.totalads.ui.live;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.IObserver;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.BackgroundTesting;
import org.eclipse.linuxtools.tmf.totalads.ui.models.SettingsForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class ModelSelection {

	private Button btnSettings;
	private Button btnDelete;
	private Tree treeModels;
	private AlgorithmFactory algFactory;
	private MessageBox msgBox;
	private SettingsForm settingsDialog;
	//private String []algorithmSettings;
	private HashMap<String,String[]> models= new HashMap<String,String[]>();
	
	/**
	 * Consturctor
	 * @param btnSettings
	 * @param btnDelete
	 * @param tree
	 */
	public ModelSelection(Button btnSettings, Button btnDelete, Tree tree) {
		this.btnSettings=btnSettings;
		this.btnDelete=btnDelete;
		this.treeModels=tree;
		this.algFactory= AlgorithmFactory.getInstance();
		msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,SWT.ICON_ERROR|SWT.OK);
		populateTreeWithModels();
		addEventHandlers();
		
	}
	
	/**
	 * Populates the tree with the list of models (databases) from the database
	 */
	private void populateTreeWithModels(){
		treeModels.removeAll();
		if (Configuration.connection.isConnected() ){ // if there is a running DB instance
			
				List <String> modelsList= Configuration.connection.getDatabaseList();
				
			    if (modelsList!=null){
					TreeItem []items=new TreeItem[modelsList.size()];
					for (int i=0;i <items.length;i++){
						items[i]=new TreeItem(treeModels,SWT.NONE);
						items[i].setText(modelsList.get(i));
										
					}
				}
		}
		
	}
	
	/**
	 * 
	 * Adds event handlers to different widgets
	 * 
	 */
	private void addEventHandlers(){
		/**
		 * Event handler for the tree selection event
		 */
		treeModels.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem item=(TreeItem)e.item;
				
				
				if (item.getChecked()){
					String []modelKey=item.getText().split("_");
					
					if(modelKey==null ||  modelKey.length<2){
						msgBox.setMessage(item.getText()+ " is not a valid model created by TotalADS!");
						msgBox.open();
						item.setChecked(false);
						return;
					}
					if (algFactory.getAlgorithmByAcronym(modelKey[1])==null){
							msgBox.setMessage(item.getText()+" is not a valid model created by TotalADS!");
							msgBox.open();
							item.setChecked(false);
							return;
					}
				
				}
				//if it reaches here then
				if (item.getChecked() )
					models.put(item.getText(),null);
				else
					models.remove(item.getText());
				
			}
			
		});	
	
	 // end of function addEventHandler
	}
	/**
	 * Returns the numebr of models selected
	 * @return
	 */
	public int getSelectedModelsCount(){
		return models.size();
	}

	/**
	 *  Returns the currently selected algorithms as an array
	 * @return  An array of {@link IDetectionAlgorithm}
	 * @throws TotalADSGeneralException
	 */
	public IDetectionAlgorithm[] getCurrentlySelectedAlgorithms() throws TotalADSGeneralException{
	
		
		
		if (models.size()<=0)
			throw new TotalADSGeneralException("Please select a model first");
		
		IDetectionAlgorithm  []algorithms=new IDetectionAlgorithm[models.size()];
		String []models=new String[this.models.size()];
		
		int idx=0;
		java.util.Iterator<String> it=this.models.keySet().iterator();
		while (it.hasNext()){
				
					String database=it.next();
					models[idx]=database;
					String []modelKey=database.split("_");
					
					//if(modelKey==null ||  modelKey.length<2)
						//throw new TotalADSGeneralException(database+ " is not a valid model created by TotalADS!");
					
					algorithms[idx]= algFactory.getAlgorithmByAcronym(modelKey[1]);
					//if  (algorithms[idx]==null)
						//throw new TotalADSGeneralException(database+" is not a valid model created by TotalADS!");
					idx++;
				
		
		}
		return algorithms;
		
	}
	/**
	 * Returns the selected models and their settings
	 * @return
	 */
	public HashMap<String,String[]> getModelaAndSettings(){
			return models;
		}
	/**
	 * Gets the list of models
	 * @return
	 */
	private String[] getModels(){
		String modelList[]=new String[models.keySet().size()];
		return models.keySet().toArray(modelList);
	}
	
}
