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

/**
 * This purpose of this class is to contain results. It is returned from the
 * algorithms implementing {@link IDetectionAlgorithm}
 *
 * @author <p>
 *         Syed Shariyar Murtaza justsshary@hotmail.com
 *         </p>
 *
 */
public class Results {
    /** Assigns yes or no */
    private Boolean isAnomaly;
    /** Assigns anomaly type iff any */
    private String anomalyType;
    /** Assigns details separated by new line '\n' */
    private StringBuilder details;

    /**
     * Constructor
     */
    public Results() {
        details = new StringBuilder();
        isAnomaly = false;
        anomalyType = ""; //$NON-NLS-1$
    }

    /**
     * Returns whether a trace is an anomaly or not
     *
     * @return True if it is an anomaly, else false
     */
    public Boolean getAnomaly() {
        return isAnomaly;
    }

    /**
     * Assign to a trace whether it is an anomaly or not
     *
     * @param isAnomaly
     *            Indicates an anomaly or not
     */
    public void setAnomaly(Boolean isAnomaly) {
        this.isAnomaly = isAnomaly;
    }

    /**
     * Return the type of anomaly if of a trace
     *
     * @return Anomaly type
     */
    public String getAnomalyType() {
        return anomalyType;

    }

    /**
     * Assigns an anomaly type to a trace
     *
     * @param anomalyType
     *            The type of anomaly
     */
    public void setAnomalyType(String anomalyType) {
        this.anomalyType = anomalyType;
    }

    /**
     * Returns details about a trace if it is an anomaly
     *
     * @return An object of type StringBuilder
     */
    public StringBuilder getDetails() {
        return details;
    }

    /**
     * Appends details to the existing details about a trace
     *
     * @param information
     *            Information to display in details
     */
    public void setDetails(String information) {
        details.append(information);
    }

}
