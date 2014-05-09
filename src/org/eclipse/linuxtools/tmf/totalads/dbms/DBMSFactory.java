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
 * Initializes a singleton instance of the database management system
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 * 			<p>	Efraim Lopez </p>
 *
 */
public enum DBMSFactory{
	 INSTANCE;
	 private boolean init = false;
	 private IDBMS connection;

	/**
	 * Instance of the database object
	 * @return
	 */
	 public IDBMS getDBMSInstance(){
			synchronized(this){
				if (!init){
					connection=new MongoDBMS();
					init = true;
				}
			}
			return connection;
	 }
		
}
