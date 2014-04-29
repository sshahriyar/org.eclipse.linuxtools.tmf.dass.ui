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
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.ui.Settings;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.BackgroundTesting;
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
	private Settings settingsDialog;
	//private String []algorithmSettings;
	private HashMap<String,String[]> models= new HashMap<String,String[]>();
	
	/**
	 * Constructor used in Live modeling
	 * @param compParent
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
		/** 
		 * Event handler for Settings button
		 * 
		 */
		btnSettings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				
					try {
							
							IDetectionAlgorithm []algorithm=getCurrentlySelectedAlgorithms();
							String []databases=getModels();
							
							if (databases.length >1){
								msgBox.setMessage("Please seelect only one model to edit settings");
								msgBox.open();
								return;
							}
								
							
							if (settingsDialog==null)
								settingsDialog= new Settings(algorithm[0].getTestingOptions(databases[0], Configuration.connection));
						
							settingsDialog.showForm();
							String []algorithmSettings=settingsDialog.getOptions();
							//models.put(databases[0], algorithmSettings);
							if (algorithmSettings!=null)
								algorithm[0].saveTestingOptions(algorithmSettings, databases[0], Configuration.connection);
							//settingsDialog=null;
						
					} catch (TotalADSUIException ex) {
						msgBox.setMessage(ex.getMessage());
						msgBox.open();
					}catch (TotalADSDBMSException ex) {
						msgBox.setMessage(ex.getMessage());
						msgBox.open();
					}finally{
						settingsDialog=null;
					}
				
			}
		});
		/**
		 * Event handler for Delete button
		 */
		btnDelete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				 try{
					 
						IDetectionAlgorithm []algorithm=getCurrentlySelectedAlgorithms();;
						String []databases=getModels();
											
						
						if (databases.length >1){
							msgBox.setMessage("Let's not delete models in haste, let us delete them one by one.");
							msgBox.open();
							return;
						}
						
						MessageBox msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
									SWT.ICON_INFORMATION|SWT.YES|SWT.NO);
						
						msgBox.setMessage("Do you want to delete the model "+databases[0]+ "?");
						if (msgBox.open()==SWT.YES){
							Configuration.connection.deleteDatabase(databases[0]);
							models.remove(databases[0]);
						}
				} catch (TotalADSUIException ex) {
					msgBox.setMessage(ex.getMessage());
					msgBox.open();
				}
				
			}
		});
		/**
		 * Event handler to update the list of models automatically whenever the database changes
		 */
		Configuration.connection.addObserver(new IObserver() {
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
	 * @throws TotalADSUIException
	 */
	public IDetectionAlgorithm[] getCurrentlySelectedAlgorithms() throws TotalADSUIException{
	
		
		
		if (models.size()<=0)
			throw new TotalADSUIException("Please select a model first");
		
		IDetectionAlgorithm  []algorithms=new IDetectionAlgorithm[models.size()];
		String []models=new String[this.models.size()];
		
		int idx=0;
		java.util.Iterator<String> it=this.models.keySet().iterator();
		while (it.hasNext()){
				
					String database=it.next();
					models[idx]=database;
					String []modelKey=database.split("_");
					
					//if(modelKey==null ||  modelKey.length<2)
						//throw new TotalADSUIException(database+ " is not a valid model created by TotalADS!");
					
					algorithms[idx]= algFactory.getAlgorithmByAcronym(modelKey[1]);
					//if  (algorithms[idx]==null)
						//throw new TotalADSUIException(database+" is not a valid model created by TotalADS!");
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