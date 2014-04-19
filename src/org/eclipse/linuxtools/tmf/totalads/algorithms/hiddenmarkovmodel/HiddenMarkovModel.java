/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.algorithms.hiddenmarkovmodel;

import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmTypes;
import org.eclipse.linuxtools.tmf.totalads.algorithms.Results;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.ui.ProgressConsole;
import org.swtchart.Chart;
/**
 * This class implements a Hidden Markov Model
 * @author <p>Syed Shariyar Murtaza justsshary@hotmail.com</p>
 * 
 */
public class HiddenMarkovModel implements IDetectionAlgorithm {
	/**
	 * Constructor
	 */
	public HiddenMarkovModel() {
		
	}

	/**
	 *  Self registration of the model with the modelFactory 
	 */
	public static void registerModel() throws TotalADSUIException{
		AlgorithmFactory modelFactory= AlgorithmFactory.getInstance();
		HiddenMarkovModel hmm=new HiddenMarkovModel();
		modelFactory.registerModelWithFactory( AlgorithmTypes.ANOMALY,  hmm);
	}
	/**
	 * Creates database
	 */
	@Override
	public void createDatabase(String databaseName, DBMS connection)
			throws TotalADSDBMSException {
		throw new TotalADSDBMSException("HMM is not implemented yet");

	}
	@Override
	public void saveTrainingOptions(String [] options, String database, DBMS connection) throws TotalADSUIException, TotalADSDBMSException
	{
		
	}
	
	


	@Override
	public String[] getTrainingOptions() {
		
		return null;
	}

	@Override
	public String[] getTestingOptions(String database, DBMS connection) {
		
		return null;
	}
   @Override
	public void saveTestingOptions(String [] options, String database, DBMS connection) throws TotalADSUIException, TotalADSDBMSException
	{ 
	   
	}

	@Override
	public void train(ITraceIterator trace, Boolean isLastTrace,
			String database, DBMS connection, ProgressConsole console,
			String[] options, Boolean isNewDB) throws TotalADSUIException {
		throw new TotalADSUIException("HMM is not implemented yet");

	}

	@Override
	public void validate(ITraceIterator trace, String database,
			DBMS connection, Boolean isLastTrace, ProgressConsole console)
			throws TotalADSUIException {
		throw new TotalADSUIException("HMM is not implemented yet");

	}

	@Override
	public Results test(ITraceIterator trace, String database, DBMS connection,
			String[] options) throws TotalADSUIException, TotalADSDBMSException {
		throw new TotalADSUIException("HMM is not implemented yet");
		
	}

	@Override
	public void crossValidate(Integer folds, String database, DBMS connection,
			ProgressConsole console, ITraceIterator trace, Boolean isLastTrace) throws TotalADSUIException, TotalADSDBMSException {
		// TODO Auto-generated method stub

	}

	@Override
	public Double getTotalAnomalyPercentage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Chart graphicalResults(ITraceIterator traceIterator) {
	
		return null;
	}

	@Override
	public IDetectionAlgorithm createInstance() {
	
		return new HiddenMarkovModel();
	}

	@Override
	public String getName() {
		
		return "Hidden Markov Model (HMM)";
	}

	@Override
	public String getAcronym() {
		
		return "HMM";
	}

}
