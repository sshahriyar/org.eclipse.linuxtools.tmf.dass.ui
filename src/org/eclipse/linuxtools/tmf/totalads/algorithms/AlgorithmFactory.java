/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.algorithms;


//import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.eclipse.linuxtools.tmf.totalads.algorithms.hiddenmarkovmodel.HiddenMarkovModel;
import org.eclipse.linuxtools.tmf.totalads.algorithms.ksm.KernelStateModeling;
import org.eclipse.linuxtools.tmf.totalads.algorithms.slidingwindow.SlidingWindow;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;

/**
 * This is an AlgorithmFactory class that registers all the Algorithms with itself.
 * The class follows a factory design pattern, and it  creates a singleton object.
 *   
 * @author <p>Syed Shariyar Murtaza justsshary@hotmail.com</p>
 * 
 */
public class AlgorithmFactory {

//Variables used in the class that keep track of algorithms in the class
private static AlgorithmFactory algorithmTypes=null;

private HashMap<AlgorithmTypes,HashSet<String>> algList=null;
private HashMap<String,IDetectionAlgorithm> acronymModels=null;

/**
 * Constructor
 */
private AlgorithmFactory( ){
	algList=new HashMap<AlgorithmTypes,HashSet<String>>();
	acronymModels=new HashMap<String, IDetectionAlgorithm>();
}
/**
 * Creates an instance of AlgorithmFactory
 * @return  Instance of AlgorithmFactory
 */
public static AlgorithmFactory getInstance(){
	if (algorithmTypes==null)
		algorithmTypes=new AlgorithmFactory();
	return algorithmTypes;
}

/**
 * Destroys the instance of factory if already exists' This code is necessary because when Eclipse is 
 * running and TotalADS window is closed then reopened again, the static
 * object is not recreated on the creation of new object of TotalADS. We need to destroy all the objects.
 */
public static void destroyInstance(){
	if (algorithmTypes!=null)
		algorithmTypes=null;
}
/**
 * Gets the list of algorithms by a type; e.g., classification, clustering, etc.
 * @param algorithmTypes  an enum of AlgorithmTypes
 * @return Array of Algorithms 
 */
public IDetectionAlgorithm[] getAlgorithms(AlgorithmTypes algorithmTypes){
	HashSet<String> list= algList.get(algorithmTypes);
	if (list==null)
		return null;
	else{	
		IDetectionAlgorithm []models=new IDetectionAlgorithm[list.size()];
		Iterator<String> it=list.iterator();
		int count=0;
		while (it.hasNext()){
			models[count++]= getAlgorithmByAcronym(it.next());
		}
		return models;
	}
}
/**
 * This function registers an algorithm by using its acronym as a key
 * @param key Acronym of the algorithm
 * @param detectionAlgorithm The Algorithm to register
 * @throws TotalADSUIException 
 */
private void registerAlgorithmWithAcronym(String key, IDetectionAlgorithm detectionAlgorithm) throws TotalADSUIException{
	
	if (key.isEmpty())
		throw new TotalADSUIException("Empty key/acronym!");
	else if (key.contains("_"))
			throw new TotalADSUIException("Acronym cannot contain underscore");
	else {
		
		IDetectionAlgorithm model =acronymModels.get(key);
		if (model==null) 
			acronymModels.put(key, detectionAlgorithm);
		else
			throw new TotalADSUIException("Duplicate key/acronym!");
	}
		
		
}
/**
 * Registers an algorithm with this factory
 * @param detectionAlgorithm The Algorithm to register
 * @param algorithmType An enum of {@link AlgorithmTypes}
 */
public void registerModelWithFactory(AlgorithmTypes algorithmType,  IDetectionAlgorithm detectionAlgorithm)
																	throws TotalADSUIException{
	
	registerAlgorithmWithAcronym(detectionAlgorithm.getAcronym(), detectionAlgorithm);
	HashSet<String>  list=algList.get(algorithmType);
	
	if (list==null)
		list=new HashSet<String>();
	
	list.add(detectionAlgorithm.getAcronym());
	
	algList.put(algorithmType, list);
	
		
}
/**
 * Get an algorithm by acronym
 * @param key
 * @return an instance of the algorithm
 */
public IDetectionAlgorithm getAlgorithmByAcronym(String key){
	IDetectionAlgorithm model= acronymModels.get(key);
	if (model==null)
		return null;
	else
		return model.createInstance();
}
/**
 * Gets all algorithms to register with the factory. Currently all the algorithms
 * are manually intialized in this function but in future versions, this code would be
 * replace  with reflection and  will register all algorithms derived from the 
 * interface IDetectionAlgorithms automatically
 */
public void initialize() throws TotalADSUIException{
	
 //Reflections reflections = new Reflections("org.eclipse.linuxtools.tmf.totalads.ui");
 ////java.util.Set<Class<? extends IDetectionAlgorithm>> modules = reflections.getSubTypesOf
	//		 							(org.eclipse.linuxtools.tmf.totalads.ui.IDetectionModels.class);

	KernelStateModeling.registerModel();
	SlidingWindow.registerModel();
	HiddenMarkovModel.registerModel();
	
	
}



}
