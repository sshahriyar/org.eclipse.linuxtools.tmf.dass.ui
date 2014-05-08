/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads.ui.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.linuxtools.tmf.totalads.TotalAdsDAOException;
import org.eclipse.linuxtools.tmf.totalads.dbms.DAOFactory;
import org.eclipse.linuxtools.tmf.totalads.dbms.DAOModel;

/**
 * @author efraimlopez
 *
 */
public class DataModels {
	
	private final static DataModels INSTANCE = new DataModels();
	
	private final List<IDataModelsObserver> observerList;
	private List<DataModel> models; 
	
	private DataModels(){
		observerList = new ArrayList<IDataModelsObserver>();	
	}
	
	public static DataModels getInstance(){
		return INSTANCE;
	}
	
	// TODO function to add models / call notify update
	// TODO function to remove models / same thing
	
	public List<DataModel> listModels(){
	
		if(models==null){
			synchronized(this){
				if(models==null){
					models = new CopyOnWriteArrayList<DataModel>();
					DAOFactory daoFactory = DAOFactory.newInstance();
					DAOModel daoModel = daoFactory.createDAOModelInstance();	
					try {
						models.addAll(daoModel.getAllModels());
						//notifyModelsUpdate();
					} catch (TotalAdsDAOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}			
				}
			}
		}
		
		return new ArrayList<DataModel>(this.models);
	}
	
	private void notifyModelsUpdate(){
		for (IDataModelsObserver observer : observerList)
			observer.modelsUpdated();
	}
	
	public void addModelsObserver(IDataModelsObserver observer){
		observerList.add(observer);
	}
	
	public void removeModelsObserver(IDataModelsObserver observer){
		observerList.remove(observer);
	}
	
}
