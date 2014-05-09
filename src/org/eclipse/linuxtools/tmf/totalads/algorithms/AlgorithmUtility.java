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

import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.IDBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;

/**
 * Utility class to execute algorithms by implementing common recurring tasks required for algorithms.
 * 
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public  class AlgorithmUtility {

	private AlgorithmUtility() {
		
	}

	/**
	 * Creates a model in the database with training settings
	 * @param modelName Model name
	 * @param algorithm Algorithm type
	 * @param connection IDBMS object
	 * @param trainingSettings Training SettingsForm object
	 * @throws TotalADSDBMSException
	 * @throws TotalADSGeneralException
	 */
	public static void createModel(String modelName, IDetectionAlgorithm algorithm, IDBMS connection, String []trainingSettings ) throws TotalADSDBMSException, TotalADSGeneralException{
		modelName+="_"+algorithm.getAcronym();
		modelName=modelName.toUpperCase();		
		algorithm.initializeModelAndSettings(modelName, Configuration.connection, trainingSettings);
	}
	
	/**
	 * Returns the algorithm for a given model name
	 * @param modelName Name of the model
	 * @param connection IDBMS connection object
	 * @return An object of type IDetectionAlgorithm
	 * @throws TotalADSGeneralException 
	 */
	public static IDetectionAlgorithm getAlgorithmFromModelName(String modelName, IDBMS connection) throws TotalADSGeneralException{
		
		String []modelParts=modelName.split("_");
		if (modelParts==null  || modelParts.length <2)
			throw new TotalADSGeneralException("Not a valid model created by TotalADS");
		
		String algorithmAcronym=modelParts[1];
		IDetectionAlgorithm algorithm=AlgorithmFactory.getInstance().getAlgorithmByAcronym(algorithmAcronym);
		if (algorithm==null)
			throw new TotalADSGeneralException("Not a valid model created by TotalADS");
		
		return algorithm;
	}
}
