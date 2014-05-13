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
 * Initializes a singleton instance of the database management system and provides utility functions
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 * 			<p>	Efraim Lopez </p>
 *
 */
public enum DBMSFactory{
	 INSTANCE;
	 private boolean init = false;
	 private MongoDBMS mongoDBMS;
	 private IDataAccessObject daoRef;
	 private IDBMSConnection connRef;
	 private String host="";
	 private Integer port;
	 private String userName="";
	 private String database="";
	 private String password="";
	/**
	 * Initializes an object the database
	 * @return An object of type IDataAccessObject
	 */
	  public IDataAccessObject getDataAccessObject(){
		 synchronized(this){
				if (!init){
					mongoDBMS=new MongoDBMS();
					daoRef=(IDataAccessObject)mongoDBMS;
					connRef=(IDBMSConnection)mongoDBMS;
					init = true;
				}
			}
		 return daoRef;
	 }
	
	 
	 /**
	  * Closes the connection
	  */
	 public void closeConnection(){
		 synchronized (this){
			 if (daoRef!=null && daoRef.isConnected())
				 connRef.closeConnection();
		 }
	 }
	 
	
	 /**
	  * Opens a connection
	  * @param host Host name
	  * @param port Port number
	  * @return An empty string on success, else an error message
	  */
	 public String openConnection(String host, Integer port) {
		String err="";
		 synchronized (this){
			 if (daoRef==null)
				 getDataAccessObject();//initialize it;
			 if (daoRef.isConnected()) // Don't open multiple connections
					connRef.closeConnection();
		 	 err= connRef.connect(host, port);
		 	if (err.isEmpty()){
				 	 this.host=host;
				 	 this.port=port;
			 }
			 
		 }
		 return err;
	 }
	 
	 /**
	  * Opens a connection
	  * @param host Host name
	  * @param port Port number
	  * @param userName User name
	  * @param password Password
	  * @param database Database name
	  * @return An empty string on success, else an error message
	  */
	 public String openConnection(String host, Integer port, String userName, String password, String database){
		 String err="";
		 synchronized (this){
			 if (daoRef==null)
				 getDataAccessObject();//initialize it;
			 if (daoRef.isConnected()) // Don't open multiple connections
					connRef.closeConnection();
		 	 err= connRef.connect(host, port, userName, password, database);
		 	if (err.isEmpty()){
			 	 this.host=host;
			 	 this.port=port;
			 	 this.userName=userName;
			 	 this.password=password;
			 	 this.database=database;
		 	}
		 }
		 return err;
	 }
	 
	 /**
	  * Deletes a database
	  * @param database Database name
	  * @return An empty string on success, else an error message
	  */
	 public String deleteDatabase(String database){
		 String err="";
		 synchronized (this){
			 if (daoRef!=null && daoRef.isConnected()) 
				 connRef.deleteDatabase(database);
			 else
				 err="No databse connection exists.....";
		 }
		 return err;
	 }
	 /**
	  * Reconnects to the database. Use it when the connection is lost and you need to verify the connection
	  * @return
	  */
	 public String verifyConnection(){
		 String err="";
		 synchronized (this){
			 if (daoRef!=null && daoRef.isConnected()) // Don't open multiple connections
					connRef.closeConnection();
			 if (host.isEmpty())
				 err="Before using verifyConnection, first open a connection";
			 else if (userName.isEmpty())
				 err= connRef.connect(host, port);
			 else	 
				 err= connRef.connect(host, port, userName, password, database);
		 }
		 return err;
	 }
}
