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
 *
 * Observer interface to be implmented by those classes which wants update from the IDataAccessObject 
 *  @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 */
public interface IDBMSObserver {
	   /**
	    * Updates the observer
	    */
       public void update();
       /**
        * Provides the information as text when updating
        *
        public void update(String information);*/

}
