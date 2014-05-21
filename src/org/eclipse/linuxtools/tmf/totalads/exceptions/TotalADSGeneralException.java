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
 * This class defines custom UI exceptions that are thrown when a user does not
 * select the proper User Interface settings
 *
 * @author <p>
 *         Syed Shariyar Murtaza justsshary@hotmail.com
 *         </p>
 *
 */
public class TotalADSGeneralException extends Exception {

    /**
     * Serializable id
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public TotalADSGeneralException() {

    }

    /**
     * Creates an exception with a message
     *
     * @param message
     *            Message for the exception
     */
    public TotalADSGeneralException(String message) {
        super(message);

    }

    /**
     * Creates an exception with an object of type Throwable
     *
     * @param cause
     *            An object of type Throwable
     */
    public TotalADSGeneralException(Throwable cause) {
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
    public TotalADSGeneralException(String message, Throwable cause) {
        super(message, cause);

    }

}
