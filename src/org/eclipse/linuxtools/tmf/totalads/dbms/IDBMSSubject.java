/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.dbms;
/**
 * Subject interface used by IDBMS to notify observers
 *  @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public interface IDBMSSubject {
	/**
	 *  Adds an observer of type {@link IDBMSObserver}
	 * @param observer
	 */
	
	public void addObserver(IDBMSObserver observer);
	/**
	 * Removes an observer of type {@link IDBMSObserver}
	 * @param observer
	 */
	
	public void removeObserver(IDBMSObserver observer);
	/**
	 * Notifies all observers of type {@link IDBMSObserver}
	 */
	
	public void notifyObservers();
	
	
}
