package org.eclipse.linuxtools.tmf.totalads.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.reflections.Reflections;

public class ModelTypeFactory {
private static ModelTypeFactory modelTypes=null;
public static enum ModelTypes {Anomaly, Classification};
private HashMap<ModelTypes,ArrayList<IDetectionModels>> modelList=null;
private HashMap<String,IDetectionModels> acronymModels=null;
/**
 * 
 */

private ModelTypeFactory( ){
	modelList=new HashMap<ModelTypes,ArrayList<IDetectionModels>>();
	acronymModels=new HashMap<String, IDetectionModels>();
}
/**
 * Creates the instance if ModelTypeFactory
 * @return ModelTypeFactory
 */
public static ModelTypeFactory getInstance(){
	if (modelTypes==null)
		modelTypes=new ModelTypeFactory();
	return modelTypes;
}
/**
 * 
 * @return
 */
public IDetectionModels[] getModels(ModelTypes modTypes){
	ArrayList<IDetectionModels> list= modelList.get(modTypes);
	if (list==null)
		return null;
	else{	
		IDetectionModels []models=new IDetectionModels[list.size()];
		models=list.toArray(models);
		return models;
	}
}
/**
 * 
 * @param key
 * @param detectionModel
 * @throws TotalADSUiException
 */
private void registerModelWithAcronym(String key, IDetectionModels detectionModel) throws TotalADSUiException{
	
	if (key.isEmpty())
		throw new TotalADSUiException("Empty key/acronym!");
	else if (key.contains("_"))
			throw new TotalADSUiException("Acronym cannot contain unerscore");
	else {
		
		IDetectionModels model =acronymModels.get(key);
		if (model==null)
			acronymModels.put(key, detectionModel);
		else
			throw new TotalADSUiException("Duplicate key/acronym!");
	}
		
		
}
/**
 * Registers a model with the factory
 * @param detectionModel
 * @param modelType
 */
public void registerModelWithFactory(ModelTypes modelType,  IDetectionModels detectionModel)
																	throws TotalADSUiException{
	
	registerModelWithAcronym(detectionModel.getAcronym(), detectionModel);
	ArrayList<IDetectionModels>  list=modelList.get(modelType);
	
	if (list==null)
		list=new ArrayList<IDetectionModels>();
	
	list.add(detectionModel);
	
	modelList.put(modelType, list);
	//modelList.put(modelType,detectionModel);
		
}
/**
 * Get a model by acronym
 * @param key
 * @return
 */
public IDetectionModels getModelyByAcronym(String key){
	IDetectionModels model= acronymModels.get(key);
	if (model==null)
		return null;
	else
		return model.createInstance();
}
/**
 * Get all models to register with the factory
 */
public void initialize() throws TotalADSUiException{
	
 //Reflections reflections = new Reflections("org.eclipse.linuxtools.tmf.totalads.ui");
 ////java.util.Set<Class<? extends IDetectionModels>> modules = reflections.getSubTypesOf
	//		 							(org.eclipse.linuxtools.tmf.totalads.ui.IDetectionModels.class);
	// The following code needs to be replaced with reflection in future versions
	KernelStateModeling.registerModel();
	SlidingWindow.registerModel();
	DecisionTree.registerModel();
	
}



}
