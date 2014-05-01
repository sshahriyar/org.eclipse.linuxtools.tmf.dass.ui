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
import org.eclipse.linuxtools.tmf.totalads.ui.io.ProgressConsole;

/** Each model should implement the functions of this interface and, in addition, 
 * a static function registerModel
 * @author <p>Syed Shariyar Murtaza justsshary@hotmail.com</p>
 * */
public interface IDetectionAlgorithm {

/**
 * Creates a database where an algorithm would store its model
 * @param databaseName Name of the database
 * @param connection An object of DBMS
 * @throws TotalADSDBMSException 
 */
public void createDatabase(String databaseName, DBMS connection) throws TotalADSDBMSException; 
/**
 * Returns the training settings/options of an algorithm as setting name at index i and value at index i+1.
 * @param connection Database connection object
 * @param database database name
 * @param isNewDatabase True if it is a new databse else it is false
 * @return Array of Strings as options/settings
 */
public String[] getTrainingOptions(DBMS connection, String database, Boolean isNewDatabase);
/**
 * Validates the training options and saves them into the database. On error throws exception
 * @param options Settings array
 * @param database Database name
 * @param connection Databse connection object
 * @throws TotalADSUIException
 * @throws TotalADSDBMSException TODO
 */
public void saveTrainingOptions(String [] options, String database, DBMS connection) throws TotalADSUIException, TotalADSDBMSException;


/**
 * Returns the testing options/settings of an algorithm as option name at index i and value ate index i+1.
 * It takes database name and connection information, in case if the model is already created and previously 
 * modified settings exist in the database
 * @param database Database name
 * @param connection DBMS object
 * @return An array of String as options/settings
 */
public String[] getTestingOptions(String database, DBMS connection);
/**
 * Validates the testing options and saves them into the database. On error throws exception
 * @param options
 * @param database
 * @param connection
 * @throws TotalADSUIException
 * @throws TotalADSDBMSException 
 */
public void saveTestingOptions(String [] options, String database, DBMS connection) throws TotalADSUIException, TotalADSDBMSException;
/**
 * An algorithm will take a  trace through this function. Some algorithms can train on 
 * the traces as they come and some need to wait till the last trace. Caller
 * will make isLastTrace true when the lastTrace will be sent to this function. This function
 * is called for every trace separately
 * @param trace Trace iterator to a trace
 * @param isLastTrace True if the trace is the last trace, else false
 * @param database Database/mode name
 * @param connection Connection object
 * @param console An object of the console where the processing information needs to be printed
 * @param options Value of the options/settings if set
 * @param isNewDB True if the database is a new database; otherwise false
 * @throws TotalADSUIException
 * @throws TotalADSDBMSException
 * @throws TotalADSReaderException
 */

public void train (ITraceIterator trace, Boolean isLastTrace, String database, DBMS connection, ProgressConsole console, String[] options, Boolean isNewDB) throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException;

/**
 * This function is called after the train function has finished processing and has built a model.
 * This function is called for every single trace in the validation set separately 
 * @param trace Trace iterator to one trace
 * @param database Database name
 * @param connection Connection name
 * @param isLastTrace True if the trace is the last trace, else false
 * @param console An object of the console where to print the processing options
 * @throws TotalADSUIException
 * @throws TotalADSDBMSException
 * @throws TotalADSReaderException
 */
public  void validate (ITraceIterator trace, String database, DBMS connection, Boolean isLastTrace, ProgressConsole console) throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException;
/**
 * This function evaluates an existing model in the database on the traces in the test set. It is called for every single
 * trace separately.
 * @param trace Trace iterator to a single trace
 * @param database Database name
 * @param connection DBMS object
 * @param options Testing Options/Settings if changed by a user are passed here
 * @return An object of type Result containing the evaluation rinformation on a trace
 * @throws TotalADSUIException
 * @throws TotalADSDBMSException
 * @throws TotalADSReaderException
 */
public Results test (ITraceIterator trace, String database, DBMS connection, String[] options) throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException;
/**
 * This function is used to do the cross validation on the training data in the database. Currently it is unimplmented.
 *  It will work on the traces already processed and stored or after processing and stroign them in the suitable form 
 *  for the evaluation. It will then virtually divide the processed  traces into different parts, train on some parts and evaluate on other parts. 
 * @param folds
 * @param database
 * @param connection
 * @param console
 * @param trace
 * @param isLastTrace
 * @throws TotalADSUIException
 * @throws TotalADSDBMSException
 */
public void crossValidate(Integer folds, String database, DBMS connection, ProgressConsole console, ITraceIterator trace, Boolean isLastTrace) throws TotalADSUIException, TotalADSDBMSException;
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
//public static void registerModel() throws TotalADSUIException;
///////////////////////////////////////////////////////////////////////////////////
/** Gets the name of the model**/
public String getName();

/** Returns the acronym of the model; should only be three to four characters long. This acronym is very important
 * as it is used in the name of the database/model and facilitates in finding out which algorithm represents the model */
public String getAcronym();
}