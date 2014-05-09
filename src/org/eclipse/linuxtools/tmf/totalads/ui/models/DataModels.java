/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads.ui.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.linuxtools.tmf.totalads.TotalAdsDAOException;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMSFactory;
//import org.eclipse.linuxtools.tmf.totalads.dbms.IDAOModel;
import org.eclipse.linuxtools.tmf.totalads.dbms.IDBMS;

/**
 * @author efraimlopez
 *
 */
public class DataModels {
	
	private final static DataModels INSTANCE = new DataModels();
	
	private final List<IDataModelsObserver> observerList;
	private List<String> models; 
	
	private DataModels(){
		observerList = new ArrayList<IDataModelsObserver>();	
	}
	
	public static DataModels getInstance(){
		return INSTANCE;
	}
	
	// TODO function to add models / call notify update
	// TODO function to remove models / same thing
	
	public List<String> listModels(){
	
		if(models==null){
			synchronized(this){
				if(models==null){
					models = new CopyOnWriteArrayList<String>();
					IDBMS connection = DBMSFactory.INSTANCE.getDBMSInstance();
					
					if (connection.isConnected())
							models.addAll(connection.getDatabaseList());
								
				}
			}
		}
		
		return new ArrayList<String>(this.models);
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
