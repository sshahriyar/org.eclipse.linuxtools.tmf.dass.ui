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
 * This class provides choices of Algorithm Types that must be used to register
 * an algorithm with {@link AlgorithmFactory} or to retrieve a list of
 * algorithms from {@link AlgorithmFactory}
 *
 * @author <p>
 *         Syed Shariyar Murtaza justsshary@hotmail.com
 *         </p>
 *
 */
public enum AlgorithmTypes {
    /**
     * Represents Anomaly Detection Algorithms
     */
    ANOMALY,
    /**
     * Represents Classification Algorithms
     */
    CLASSIFCATION,
    /**
     * Represents Clustering Algorithms
     */
    CLUSTERING;

}
