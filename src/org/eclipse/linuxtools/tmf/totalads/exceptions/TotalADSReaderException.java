/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.exceptions;

/**
 * This class defines custom reader exceptions that are thrown when trace
 * reading encounters a problem
 *
 * @author <p>
 *         Syed Shariyar Murtaza justsshary@hotmail.com
 *         </p>
 *
 */
public class TotalADSReaderException extends Exception {

    /**
     * Serializable id
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public TotalADSReaderException() {

    }

    /**
     * Creates an exception with a message
     *
     * @param message
     *            Message for the exception
     */
    public TotalADSReaderException(String message) {
        super(message);

    }

    /**
     * Creates an exception with an object of type Throwable
     *
     * @param cause
     *            An object of type Throwable
     */
    public TotalADSReaderException(Throwable cause) {
        super(cause);

    }

    /**
     * Creates an exception with a message and a Throwable object
     *
     * @param message
     *            Message for the exception
     * @param cause
     *            An object of type Throwable
     */
    public TotalADSReaderException(String message, Throwable cause) {
        super(message, cause);

    }

}
