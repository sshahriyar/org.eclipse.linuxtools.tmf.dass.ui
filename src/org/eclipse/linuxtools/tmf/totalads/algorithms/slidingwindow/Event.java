/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.algorithms.slidingwindow;

import java.util.ArrayList;

/**
 * This class represents an Event of a trace in a tree and the corresponding
 * fBranches
 *
 * @author <p>
 *         Syed Shariyar Murtaza justsshary@hotmail.com
 *         </p>
 *
 */
class Event {

    // Data variables
    private Integer fEvent;
    private ArrayList<Event[]> fBranches;

    /** constructor */
    public Event() {
    }

    /**
     * Returns an fEvent
     *
     * @return fEvent
     */
    public Integer getEvent() {
        return fEvent;
    }

    /**
     * Sets an event
     *
     * @param event
     *            string
     */
    public void setEvent(Integer event) {
        this.fEvent = event;
    }

    /**
     * Returns the branch of events
     *
     * @return ArrayList of events
     */
    public ArrayList<Event[]> getBranches() {
        return fBranches;
    }

    /**
     * Sets the branch of events
     *
     * @param branchesAtEvent
     *            Arraylist of events
     */
    public void setBranches(ArrayList<Event[]> branchesAtEvent) {
        this.fBranches = branchesAtEvent;
    }

}
