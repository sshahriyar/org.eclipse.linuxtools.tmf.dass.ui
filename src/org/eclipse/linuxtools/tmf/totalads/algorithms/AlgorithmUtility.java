package org.eclipse.linuxtools.tmf.totalads.algorithms;

import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;

/**
 * Utility class to execute algorithms by performing common recurring tasks.
 * It also enforces on how to use algorithms.
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
	 * @param connection DBMS object
	 * @param trainingSettings Training Settings object
	 * @throws TotalADSDBMSException
	 * @throws TotalADSUIException
	 */
	public static void createModel(String modelName, IDetectionAlgorithm algorithm, DBMS connection, String []trainingSettings ) throws TotalADSDBMSException, TotalADSUIException{
		modelName+="_"+algorithm.getAcronym();
		modelName=modelName.toUpperCase();		
		algorithm.initializeModelAndSettings(modelName, Configuration.connection, trainingSettings);
	}
}
