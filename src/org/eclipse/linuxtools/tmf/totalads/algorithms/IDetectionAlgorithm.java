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

import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.ProgressConsole;

/** Each model should implement the functions of this interface and, in addition, 
 * a static function registerModel
 * @author <p>Syed Shariyar Murtaza justsshary@hotmail.com</p>
 * */
public interface IDetectionAlgorithm {

/**
 * Creates a database where an algorithm would store its model
 * @param databaseName 
 * @param connection
 * @throws Exception
 */
public void createDatabase(String databaseName, DBMS connection) throws TotalADSDBMSException; 
/**
 * Returns the settings of an algorithm as option name at index i and value at index i+1.
 * Pass true to get training options and false to get testing options
 * @return String[]
 */
public String[] getTrainingOptions();
/**
 * Set the settings of an algorithm as option name at index i and value ate index i+1.
 * Pass true to get training options and false to get testing options
 * @param database TODO
 * @param connection TODO
 * @return TODO
 */
public String[] getTestingOptions(String database, DBMS connection);

/** Controller will pass a trace through this function. Some models can train on 
 * the traces as they come and some need to wait till the last trace. Controller 
 * will make isLastTrace true when the lastTrace will be sent.  
 * 
 * @param trace
 * @param isLastTrace
 * @param database
 * @param connection
 * @param console
 * @param options TODO
 * @throws TotalADSUIException TODO
 * @throws TotalADSDBMSException 
 * @throws TotalADSReaderException 
 */
public void train (ITraceIterator trace, Boolean isLastTrace, String database, DBMS connection, ProgressConsole console, String[] options) throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException;
/**
 * Controller will pass traces for validation using this function.  
 * @param trace
 * @param database
 * @param connection
 * @param isLastTrace
 * @param console
 * @throws TotalADSUIException 
 * @throws TotalADSDBMSException 
 * @throws TotalADSReaderException 
 */
public  void validate (ITraceIterator trace, String database, DBMS connection, Boolean isLastTrace, ProgressConsole console) throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException;
/**
 * Controller will pass traces for testing using this function 
 * @param trace
 * @param database
 * @param connection
 * @param options TODO
 * @param traceName
 * @throws TotalADSUIException 
 * @throws TotalADSDBMSException 
 * @throws TotalADSReaderException 
 */
public Results test (ITraceIterator trace, String database, DBMS connection, String[] options) throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException;
/**
 * This function is used to do the cross validation on the training data in the database
 * @param folds
 * @param database
 * @param connection
 * @param console
 * @param trace
 * @throws TotalADSUIException
 * @throws TotalADSDBMSException TODO
 */
public void crossValidate(Integer folds, String database, DBMS connection, ProgressConsole console, ITraceIterator trace) throws TotalADSUIException, TotalADSDBMSException;
/** Returns the summary of the results **/
public String getSummaryOfTestResults();
/** Returns the graphical result in the form of a chart if any for a trace **/
public org.swtchart.Chart graphicalResults();
/** Returns a self created instance of the model**/
public IDetectionAlgorithm createInstance();
///////////////////////////////////////////////////////////////////////////////////
//An algorithm registers itself with the AlgorithmFactory
//Each derived class must implement the following static method
//public static void registerModel() throws Exception;
///////////////////////////////////////////////////////////////////////////////////
/** Gets the name of the model**/
public String getName();

/** Returns the acronym of the model; should only be three characters long */
public String getAcronym();
}