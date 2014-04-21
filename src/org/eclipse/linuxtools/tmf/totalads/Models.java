/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author efraimlopez
 *
 */
public class Models {
	
	private final static Models INSTANCE = new Models();
	
	private final List<ModelsObserver> observerList;
	private List<Model> models; 
	
	private Models(){
		observerList = new ArrayList<ModelsObserver>();	
	}
	
	public static Models getInstance(){
		return INSTANCE;
	}
	
	public List<Model> listModels(){
	
		if(models==null){
			synchronized(this){
				if(models==null){
					models = new CopyOnWriteArrayList<Model>();
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
		
		return new ArrayList<Model>(this.models);
	}
	
	private void notifyModelsUpdate(){
		for (ModelsObserver observer : observerList)
			observer.modelsUpdated();
	}
	
	public void addModelsObserver(ModelsObserver observer){
		observerList.add(observer);
	}
	
	public void removeModelsObserver(ModelsObserver observer){
		observerList.remove(observer);
	}
	
}
