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

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.eclipse.linuxtools.tmf.totalads.dbms.DBMSFactory;
import org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders.CTFLTTngSysCallTraceReader;

import com.google.common.base.Stopwatch;

/**
 * Utility class to execute algorithms by implementing common recurring tasks
 * required for algorithms.
 *
 * @author <p>
 *         Syed Shariyar Murtaza justsshary@hotmail.com
 *         </p>
 *
 */
public class AlgorithmUtility {

    private AlgorithmUtility() {

    }

    /**
     * Creates a model in the database with training settings
     *
     * @param modelName
     *            Model name
     * @param algorithm
     *            Algorithm type
     * @param connection
     *            IDataAccessObject object
     * @param trainingSettings
     *            Training SettingsForm object
     * @throws TotalADSDBMSException
     *             An exception related to DBMS
     * @throws TotalADSGeneralException
     *             An exception related to validation of parameters
     */
    public static void createModel(String modelName, IDetectionAlgorithm algorithm, IDataAccessObject connection, String[] trainingSettings) throws TotalADSDBMSException, TotalADSGeneralException {
        String model = modelName + "_" + algorithm.getAcronym(); //$NON-NLS-1$
        model = model.toUpperCase();
        algorithm.initializeModelAndSettings(model, DBMSFactory.INSTANCE.getDataAccessObject(), trainingSettings);
    }

    /**
     * Returns the algorithm for a given model name
     *
     * @param modelName
     *            Name of the model
     * @return An object of type IDetectionAlgorithm
     * @throws TotalADSGeneralException
     *             An exception related to validation of parameters
     */
    public static IDetectionAlgorithm getAlgorithmFromModelName(String modelName) throws TotalADSGeneralException {

        String[] modelParts = modelName.split("_");
        if (modelParts == null || modelParts.length < 2) {
            throw new TotalADSGeneralException("Not a valid model created by TotalADS");
        }

        String algorithmAcronym = modelParts[1];
        IDetectionAlgorithm algorithm = AlgorithmFactory.getInstance().getAlgorithmByAcronym(algorithmAcronym);
        if (algorithm == null) {
            throw new TotalADSGeneralException("Not a valid model created by TotalADS");
        }

        return algorithm;
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // //////// Training and Validation
    // ///////////////////////////////////////////////////////////////////////////////////
    /**
     * This function trains and validate models
     *
     * @param trainDirectory
     *            Train Directory
     * @param validationDirectory
     *            Validation Directory
     * @param traceReader
     *            Trace Reader
     * @param modelsNames
     *           Names of models as an array
     * @param outStream
     *            Output stream where the algorithm would display its output
     * @param dataAcessObject
     *            Data access object for manipulation of data from the database
     * @throws TotalADSGeneralException
     *             An exception related to validation of parameters
     * @throws TotalADSDBMSException
     *             An exception related to DBMS
     * @throws TotalADSReaderException
     *             An exception related to the trace reader
     */
    public static void trainAndValidateModels(String trainDirectory, String validationDirectory, ITraceTypeReader traceReader,
             String []modelsNames, IAlgorithmOutStream outStream,
            IDataAccessObject dataAcessObject)
            throws TotalADSGeneralException, TotalADSDBMSException, TotalADSReaderException {

        Stopwatch stopwatch = Stopwatch.createStarted();

        for (int i = 0; i < modelsNames.length; i++) {
            Boolean isLastTrace = false;
            String modelName = modelsNames[i];
            outStream.addOutputEvent("Modeling "+modelName+" on traces");
            outStream.addNewLine();

            // //////////////////
            // /File verifications of traces
            // /////////////////
            // Check for valid trace type reader and training traces before
            // creating a database
            // Get a file handler

            File fileList[] = getDirectoryHandler(trainDirectory, traceReader);
            try (ITraceIterator it = traceReader.getTraceIterator(fileList[0])) {

            } catch (TotalADSReaderException ex) {
                stopwatch.stop();
                String message = "Invalid training traces and the trace reader.\n" + ex.getMessage();
                throw new TotalADSGeneralException(message);
            }

            // Check for valid trace type reader and validation traces before
            // creating a database
            File validationFileList[] = getDirectoryHandler(validationDirectory, traceReader);
            try (ITraceIterator it = traceReader.getTraceIterator(validationFileList[0]);) {

            } catch (TotalADSReaderException ex) {
                stopwatch.stop();
                String message = "Invalid validation traces and the trace reader.\n" + ex.getMessage();
                throw new TotalADSGeneralException(message);
            }

            // /////////
            // Start training
            // //
            outStream.addOutputEvent("Training the model....");
            outStream.addNewLine();

            IDetectionAlgorithm algorithm=getAlgorithmFromModelName(modelName);

            for (int trcCnt = 0; trcCnt < fileList.length; trcCnt++) {

                if (trcCnt == fileList.length - 1) {
                    isLastTrace = true;
                }
                // Get the trace
                try (ITraceIterator trace = traceReader.getTraceIterator(fileList[trcCnt])) {

                    outStream.addOutputEvent("Processing  training trace #" + (trcCnt + 1) + ": " + fileList[trcCnt].getName());
                    outStream.addNewLine();
                    algorithm.train(trace, isLastTrace, modelName, dataAcessObject, outStream);
                }
                //Check if user has asked to stop modeling
                if(Thread.currentThread().isInterrupted()) {
                    break;
                }
            }

            // Start validation
            validateModels(validationFileList, traceReader, algorithm, modelName, outStream, dataAcessObject);
            //Check if user has asked to stop modeling
            if(Thread.currentThread().isInterrupted()) {
                break;
            }
        }

        stopwatch.stop();
        Long elapsedMins=stopwatch.elapsed(TimeUnit.MINUTES);
        Long elapsedSecs=stopwatch.elapsed(TimeUnit.SECONDS);
        outStream.addOutputEvent("Total time of execution: "+elapsedMins.toString() + " mins or "+elapsedSecs+ " secs" );
        outStream.addNewLine();
    }

    /**
     * This functions validates a model for a given database of that model
     *
     * @param fileList
     *            Array of files
     * @param traceReader
     *            trace reader
     * @param algorithm
     *            Algorithm object
     * @param database
     *            Database name
     * @param outStream
     *            console object
     * @throws TotalADSGeneralException
     *             An exception related to validation of parameters
     * @throws TotalADSReaderException
     *             An exception related to the trace reader
     * @throws TotalADSDBMSException
     *             An exception related to the DBMS
     */
    private static void validateModels(File[] fileList, ITraceTypeReader traceReader, IDetectionAlgorithm algorithm,
            String database, IAlgorithmOutStream outStream, IDataAccessObject dao) throws TotalADSGeneralException, TotalADSReaderException,
            TotalADSDBMSException {

        // process now
        outStream.addOutputEvent("Starting validation....");
        outStream.addNewLine();
        Boolean isLastTrace = false;

        for (int trcCnt = 0; trcCnt < fileList.length; trcCnt++) {
            //Check if user has asked to stop modeling
            if(Thread.currentThread().isInterrupted()) {
                break;
            }

            // get the trace
            if (trcCnt == fileList.length - 1) {
                isLastTrace = true;
            }

           try (ITraceIterator trace = traceReader.getTraceIterator(fileList[trcCnt])){
                outStream.addOutputEvent("Processing  validation trace #" + (trcCnt + 1) + ": " + fileList[trcCnt].getName());
                outStream.addNewLine();
                algorithm.validate(trace, database, dao, isLastTrace, outStream);
           }

        }

    }

    /**
     *
     * @param directory
     *            The name of the directory
     * @param traceReader
     *            An object of the trace reader
     * @return An array list of traces suited for the appropriate type
     * @throws TotalADSGeneralException
     *             An exception related to validation of parameters
     */

    private static File[] getDirectoryHandler(String directory, ITraceTypeReader traceReader) throws TotalADSGeneralException {
        File traces = new File(directory);

        CTFLTTngSysCallTraceReader kernelReader = new CTFLTTngSysCallTraceReader();
        if (traceReader.getAcronym().equals(kernelReader.getAcronym())) {
            return getDirectoryHandlerforLTTngTraces(traces);
        }
        return getDirectoryHandlerforTextTraces(traces);
    }

    /**
     * Get an array of trace list for a directory or just one file handler if
     * there is only one file
     *
     * @param traces
     *            File object representing traces
     * @return the file handler to the correct path
     * @throws TotalADSGeneralException
     *             An exception related to validation of parameters
     */
    private static File[] getDirectoryHandlerforTextTraces(File traces) throws TotalADSGeneralException {

        File[] fileList;

        if (traces.isDirectory()) { // if it is a directory return the list of
                                    // all files
            Boolean isAllFiles = false, isAllFolders = false;
            fileList = traces.listFiles();
            for (File file : fileList) {

                if (file.isDirectory()) {
                    isAllFolders = true;
                } else if (file.isFile()) {
                    isAllFiles = true;
                }

                if (isAllFolders) {
                    throw new TotalADSGeneralException("The folder " + traces.getName() + " contains"
                            + "	directories. Please put only trace files in it.");
                }

            }

            if (!isAllFiles && !isAllFolders) {
                throw new TotalADSGeneralException("Empty directory: " + traces.getName());
            }

        }
        else {// if it is a single file return the single file; however, this
              // code will never be reached
              // as in GUI we are only using a directory handle, but if in futre
              // we decide to change
              // this could come handy
            fileList = new File[1];
            fileList[0] = traces;
        }

        return fileList;
    }

    /**
     * Gets an array of list of directories
     *
     * @param traces
     *            File object representing traces
     * @return Handler to the correct path of files
     * @throws TotalADSGeneralException
     *             An exception related to validation of parameters
     */
    private static File[] getDirectoryHandlerforLTTngTraces(File traces) throws TotalADSGeneralException {

        if (traces.isDirectory()) {
            File[] fileList = traces.listFiles();
            File[] fileHandler;
            Boolean isAllFiles = false, isAllFolders = false;

            for (File file : fileList) {

                if (file.isDirectory()) {
                    isAllFolders = true;
                } else if (file.isFile()) {
                    isAllFiles = true;
                }

                if (isAllFiles && isAllFolders) {
                    throw new TotalADSGeneralException("The folder " + traces.getName() + " contains a mix of" +
                            " files and directories. Please put only LTTng traces' directories in it.");
                }

            }
            // if it has reached this far
            if (!isAllFiles && !isAllFolders) {
                throw new TotalADSGeneralException("Empty directory: " + traces.getName());
            } else if (isAllFiles) { // return the name of folder as a trace
                fileHandler = new File[1];
                fileHandler[0] = traces;
            } else {
                fileHandler = fileList;
            }

            return fileHandler;

        }
        // loader in the main view
        throw new TotalADSGeneralException("You have selected a file" + traces.getName() + ", select a folder");

    }
}
