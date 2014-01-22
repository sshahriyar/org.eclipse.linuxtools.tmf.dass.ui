package org.eclipse.linuxtools.tmf.totalads.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.reflections.Reflections;

public class ModelTypeFactory {
private static ModelTypeFactory modelTypes=null;
public static enum ModelTypes {Anomaly, Classification};
private HashMap<ModelTypes,ArrayList<IDetectionModels>> modelList=null;
/**
 * 
 */

private ModelTypeFactory( ){
	modelList=new HashMap<ModelTypes,ArrayList<IDetectionModels>>();
		
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
 * @param detectionModel
 * @param modelType
 */
public void registerModelWithFactory(ModelTypes modelType,IDetectionModels detectionModel){
	ArrayList<IDetectionModels>  list=modelList.get(modelType);
	
	if (list==null)
		list=new ArrayList<IDetectionModels>();
	
	list.add(detectionModel);
	
	modelList.put(modelType, list);
	//modelList.put(modelType,detectionModel);
		
}
/**
 * 
 */
public void initialize(){
	
 //Reflections reflections = new Reflections("org.eclipse.linuxtools.tmf.totalads.ui");
 ////java.util.Set<Class<? extends IDetectionModels>> modules = reflections.getSubTypesOf
	//		 							(org.eclipse.linuxtools.tmf.totalads.ui.IDetectionModels.class);
	// The following code needs to be replaced with reflection in future versions
	KernelStateModeling.registerModel();
	SlidingWindow.registerModel();
	DecisionTree.registerModel();
	
}



}
