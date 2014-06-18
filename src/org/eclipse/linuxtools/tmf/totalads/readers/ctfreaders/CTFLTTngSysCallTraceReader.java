/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders;

import java.io.File;
import org.eclipse.linuxtools.tmf.core.exceptions.TmfTraceException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;

/**
 * Class to read system calls from LTTng traces by using CtfTmfTrace class.
 *
 * @author <p>
 *         Syed Shariyar Murtaza justsshary@hotmail.com
 *         </p>
 */
public class CTFLTTngSysCallTraceReader implements ITraceTypeReader {
     /**
     * Constructor: Instantiate a new trace reader
     *
     */
    public CTFLTTngSysCallTraceReader() {
    }

    /**
     * Creates an instance through the use of ITraceTypeReader
     */
    @Override
    public ITraceTypeReader createInstance() {
        return new CTFLTTngSysCallTraceReader();
    }

    /**
     * Registers itself with the TraceTypeFactory
     *
     * @throws TotalADSGeneralException
     *             Exception for invalid reader
     */
    public static void registerTraceTypeReader() throws TotalADSGeneralException {
        TraceTypeFactory trcTypFactory = TraceTypeFactory.getInstance();
        CTFLTTngSysCallTraceReader kernelTraceReader = new CTFLTTngSysCallTraceReader();
        trcTypFactory.registerTraceReaderWithFactory(kernelTraceReader.getName(), kernelTraceReader);
    }

    /**
     * Returns the name of the model
     *
     * @return Name
     */
    @Override
    public String getName() {
        return "LTTng System Call"; //$NON-NLS-1$
    }

    /**
     * Returns the acronym of the Kernel space reader
     *
     * @return Acronym
     */
    @Override
    public String getAcronym() {

        return "SYS"; //$NON-NLS-1$
    }

    /**
     * Return the iterator to go over the trace file
     *
     * @param file
     *            The file object
     * @return Iterator A trace iterator
     */
    @Override
    public ITraceIterator getTraceIterator(File file) throws TotalADSReaderException {

        String filePath = file.getPath();
        // CtfTmfTrace fTrace =null;

        try {
            return new CTFSystemCallIterator(filePath);

        } catch (TmfTraceException e) {

            /* Should not happen if tracesExist() passed */
            throw new TotalADSReaderException(e.getMessage() + "\n File: " + file.getName()); //$NON-NLS-1$
        }

    }

}
