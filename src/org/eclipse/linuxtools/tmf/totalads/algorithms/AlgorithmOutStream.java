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

import java.util.ArrayList;

/**
 * This class implements the GUI widget for the progress console visible on the
 * LiveMonitor tab
 *
 * @author <p>
 *         Syed Shariyar Murtaza justsshary@hotmail.com
 *         </p>
 *
 */
public class AlgorithmOutStream implements IAlgorithmOutStream {

    private ArrayList<IAlgorithmOutObserver> observers;

    /**
     * Constructor
     */
    public AlgorithmOutStream() {
        observers = new ArrayList<>();
    }

    /**
     * Adds new event to the output stream
     *
     * @param event
     *            Information to display on the output stream
     */
    @Override
    public void addOutputEvent(String event) {
        notifyObservers(event);

    }

    /**
     * Adds new line to the output Stream
     */
    @Override
    public void addNewLine() {
        notifyObservers("\n"); //$NON-NLS-1$
    }

    /**
     * Adds an observer of type {@link IAlgorithmOutObserver}
     *
     * @param observer
     *            Observer
     */
    public void addObserver(IAlgorithmOutObserver observer) {
        observers.add(observer);

    }

    /**
     * Removes an observer of type {@link IAlgorithmOutObserver}
     *
     * @param observer
     *            Observer
     */

    public void removeObserver(IAlgorithmOutObserver observer) {
        observers.remove(observer);

    }

    /**
     *
     * Notifies all observers of type
     *
     * @param event
     *            Message to observers
     */
    private void notifyObservers(String event) {
        for (IAlgorithmOutObserver ob : observers) {
            ob.updateOutput(event);
        }
    }
}
