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

import org.eclipse.linuxtools.tmf.totalads.dbms.IDBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;

/** Each model should implement the functions of this interface and, in addition, 
 * a static function registerModel
 * @author <p>Syed Shariyar Murtaza justsshary@hotmail.com</p>
 * */
public interface IDetectionAlgorithm {

/**
 * Creates a database where an algorithm would store its model
 * @param modelName Name of the database
 * @param connection An object of IDBMS
 * @param trainingSettings Training SettingsForm
 * @throws TotalADSDBMSException 
 * @throws TotalADSGeneralException TODO
 */
public void initializeModelAndSettings(String modelName, IDBMS connection, String[] trainingSettings) throws TotalADSDBMSException, TotalADSGeneralException; 
/**
 * Returns the training settings/options of an algorithm as setting name at index i and value at index i+1.
 * @return Array of Strings as options/settings
 */
public String[] getTrainingOptions();
/**
 * Validates the training options and saves them into the database. On error throws exception
 * @param options SettingsForm array
 * @param database Database name
 * @param connection Database connection object
 * @throws TotalADSGeneralException
 * @throws TotalADSDBMSException TODO
 
public void saveTrainingOptions(String [] options, String database, IDBMS connection) throws TotalADSGeneralException, TotalADSDBMSException;
*/

/**
 * Returns the testing options/settings of an algorithm as option name at index i and value ate index i+1.
 * It takes database name and connection information, in case if the model is already created and previously 
 * modified settings exist in the database
 * @param database Database name
 * @param connection IDBMS object
 * @return An array of String as options/settings
 */
public String[] getTestingOptions(String database, IDBMS connection);
/**
 * Validates the testing options and saves them into the database. On error throws exception
 * @param options
 * @param database
 * @param connection
 * @throws TotalADSGeneralException
 * @throws TotalADSDBMSException 
 */
public void saveTestingOptions(String [] options, String database, IDBMS connection) throws TotalADSGeneralException, TotalADSDBMSException;
/**
 * An algorithm will take a  trace through this function. Some algorithms can train on 
 * the traces as they come and some need to wait till the last trace. Caller
 * will make isLastTrace true when the lastTrace will be sent to this function. This function
 * is called for every trace separately
 * @param trace Trace iterator to a trace
 * @param isLastTrace True if the trace is the last trace, else false
 * @param database Database/mode name
 * @param connection Connection object
 * @param outStream Use this object to display the events during processing
 * @throws TotalADSGeneralException
 * @throws TotalADSDBMSException
 * @throws TotalADSReaderException
 */

public void train (ITraceIterator trace, Boolean isLastTrace, String database, IDBMS connection, IAlgorithmOutStream outStream) throws TotalADSGeneralException, TotalADSDBMSException, TotalADSReaderException;

/**
 * This function is called after the train function has finished processing and has built a model.
 * This function is called for every single trace in the validation set separately 
 * @param trace Trace iterator to one trace
 * @param database Database name
 * @param connection Connection name
 * @param isLastTrace True if the trace is the last trace, else false
 * @param outStream Use this object to display the events during processing
 * @throws TotalADSGeneralException
 * @throws TotalADSDBMSException
 * @throws TotalADSReaderException
 */
public  void validate (ITraceIterator trace, String database, IDBMS connection, Boolean isLastTrace, IAlgorithmOutStream outStream) throws TotalADSGeneralException, TotalADSDBMSException, TotalADSReaderException;
/**
 * This function evaluates an existing model in the database on the traces in the test set. It is called for every single
 * trace separately.
 * @param trace Trace iterator to a single trace
 * @param database Database name
 * @param connection IDBMS object
 * @param outputStream Use this object to display the events during processing
 * @return An object of type Result containing the evaluation information of a trace
 * @throws TotalADSGeneralException
 * @throws TotalADSDBMSException
 * @throws TotalADSReaderException
 */
public Results test (ITraceIterator trace, String database, IDBMS connection, IAlgorithmOutStream outputStream) throws TotalADSGeneralException, TotalADSDBMSException, TotalADSReaderException;
/** Returns the summary of the results **/
public Double getTotalAnomalyPercentage();
/** Returns the graphical result in the form of a chart if any for a trace. Currently unimplemented. 
 * @param traceIterator An iterator a trace**/
public org.swtchart.Chart graphicalResults(ITraceIterator traceIterator);
/** Returns a self created instance of the model**/
public IDetectionAlgorithm createInstance();
///////////////////////////////////////////////////////////////////////////////////
//An algorithm registers itself with the AlgorithmFactory
//Each derived class must implement the following static method
//public static void registerModel() throws TotalADSGeneralException;
///////////////////////////////////////////////////////////////////////////////////
/** Gets the name of the model**/
public String getName();
/** Gets the description of an algorithm*/
public String getDescription();
/** Returns the acronym of the model; should only be three to four characters long. This acronym is very important
 * as it is used in the name of the database/model and facilitates in finding out which algorithm represents the model */
public String getAcronym();
/** Returns true if online learning is supported. If false is returned it would mean the algorithm can only
 * train in batch mode and live training is not supported */
public boolean isOnlineLearningSupported();
}