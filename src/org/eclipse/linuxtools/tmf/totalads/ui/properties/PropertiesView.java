/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.properties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmUtility;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMSFactory;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.ui.models.DataModelsView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
/**
 * This class displays settings of a model selected last in the {@link DataModelsView} 
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class PropertiesView extends ViewPart  implements ISelectionListener {
	
	public static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.PropertiesView";
	private TableViewer viewer = null;
	private Table table=null;
	
	/**
	 * Constructor
	 */
	public PropertiesView() {
		
	}

	/*
	 * 
	 */
	@Override
	public void createPartControl(Composite parent) {
		table =new Table(parent,  SWT.H_SCROLL   | SWT.V_SCROLL | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer = new TableViewer(table);
		
		TableViewerColumn name = new TableViewerColumn(viewer, SWT.NONE);
		name.getColumn().setWidth(100);
		name.getColumn().setText("Name");
		name.setLabelProvider(new NameLabelProvider());
		
		TableViewerColumn value = new TableViewerColumn(viewer, SWT.NONE);
		value.getColumn().setWidth(50);
		value.getColumn().setText("Value");
		value.setLabelProvider(new ValueLabelProvider());
		
		viewer.setContentProvider(new ArrayContentProvider() );// new PropertiesTableContentProvider());
		
		// Registers a listener to Eclipse to get the list of models selected (checked) by the user 
        getSite().getPage().addSelectionListener(DataModelsView.ID,	this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		

	}
	
	/**
	 * Updates the viewers with settings of the model selected in the end
	 * @param models List of selected models as a HashSet
	 */
	private void updateViewer(HashSet<String> models){
		String msg="";
		try {
				if (models!=null && !models.isEmpty()){
						Iterator<String> it=models.iterator();
						String model="";
						
						while (it.hasNext())//Get the last selected model
							model=it.next();
						
					
					
					
						IDetectionAlgorithm alg=AlgorithmUtility.getAlgorithmFromModelName(model);
						String []defaultSettings={"",""};
						String []settings=alg.getSettingsToDisplay(model, DBMSFactory.INSTANCE.getDataAccessObject());
						
						//Validate settings object
						if (settings!=null && (settings.length % 2==1)){
								msg="Invalid settings the model "+model+
							      ". Must be even number of name and value pairs";
								settings=defaultSettings;
								
						}else if (settings==null){
							settings=defaultSettings;
						}
						//Display settings if everything is alright
						ArrayList<NameVal> settingCollection=new ArrayList<NameVal>();
						settingCollection.add(new NameVal("Algorithm",alg.getName()));
						settingCollection.add(new NameVal("Model",model));
						
						for (int j=0; j<settings.length;j+=2){
							NameVal keyVal=new NameVal(settings[j], settings[j+1]);
							settingCollection.add(keyVal);
						}
						if (alg.isOnlineLearningSupported())
							settingCollection.add(new NameVal("Online Learninig","Yes"));
						else
							settingCollection.add(new NameVal("Online Learninig","No"));
						
						
						viewer.setInput(settingCollection);
						viewer.refresh();
				}else{
					if (viewer!=null && !viewer.getTable().isDisposed()){
						viewer.getTable().removeAll();
						
					}
				}
					
			
			} //Catch Exceptions
			catch (TotalADSGeneralException e) {
				msg=e.getMessage();
			}catch (Exception e){
				if (e.getMessage()!=null)
					msg=e.getMessage();
				else
					msg="Unkoown error; see log";
				Logger.getLogger(PropertiesView.class.getName()).log(Level.SEVERE,null,e);
			}finally{
				//Display error message if there is something wrong
				if (!msg.isEmpty()){
					MessageBox msgBox=new MessageBox(getSite().getShell(), SWT.ERROR);
					msgBox.setMessage(msg);
					msgBox.open();
				}
			}
			
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		 if (part instanceof DataModelsView) {  
			   Object obj = ((org.eclipse.jface.viewers.StructuredSelection) selection).getFirstElement();
			   HashSet<String> modelList= (HashSet<String>)obj;
			  updateViewer(modelList);
		    }  
		}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose(){
		getSite().getPage().removeSelectionListener(DataModelsView.ID, this);
	}
}
