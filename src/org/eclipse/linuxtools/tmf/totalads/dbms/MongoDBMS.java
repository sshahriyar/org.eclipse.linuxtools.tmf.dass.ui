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

import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;
/**
 * Database Management System (IDataAccessObject) class. This calss connects with MongoDB,
 * and performs  the manipulations required in a program with MongoDB 
 *  
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
class MongoDBMS implements  IDataAccessObject, IDBMSConnection {
	
	private MongoClient mongoClient;
	private Boolean isConnected=false;
	private ArrayList<IDBMSObserver> observers;
	
	/**
	 * Constructor
	 */
	public MongoDBMS() {
		observers=new ArrayList<IDBMSObserver>();
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDBMSConnection#connect(java.lang.String, java.lang.Integer)
	 */
	@Override
	public String connect(String host, Integer port) {
		String message="";
		synchronized (this){
				try {
					
					mongoClient = new MongoClient( host , port );
					mongoClient.setWriteConcern(WriteConcern.JOURNALED);
							
					mongoClient.getDatabaseNames(); // if this doesn't work then there is no running DB. 
													// Unfortunately,mongoClient doesn't tell whether there is a DB or not
				} catch (UnknownHostException ex) {
					isConnected=false;
					message=ex.getMessage();
					notifyObservers();
					return message;
				} catch (Exception ex){ // Just capture an exception and don't let the system crash when db is not there
					isConnected=false;
					message="Unable to connect to MongoDB.";
					notifyObservers();
					return message;
				}
				
				
				isConnected=true;// if it reaches here then it is connected
				notifyObservers();
		}
		return message;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDBMSConnection#connect(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String connect(String host, Integer port, String username, String password, String database) {
		
		String message="";
		synchronized (this) {
			try {
							
					mongoClient = new MongoClient( host , port );
					mongoClient.setWriteConcern(WriteConcern.JOURNALED);
					
					DB db=mongoClient.getDB(database);
					
					if (db.authenticate(username, password.toCharArray())==false){
						isConnected=false;
						message="Authentication failed with MongoDB using user id "+username +" and database "+database+".";
					}
					else{
						isConnected=true;// if it reaches here then everything is fine
						
					}
					
				} catch (UnknownHostException ex) {
					isConnected=false;
					message=ex.getMessage();
					//notifyObservers();
					//return message;
				} catch (Exception ex){ // Just capture an exception and don't let the system crash when db is not there
					isConnected=false;
					message="Unable to connect to MongoDB";
					//notifyObservers();
					//return message;
				}finally{
				
					
				  notifyObservers();
				
				}
		}
		return message;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject#isConnected()
	 */
	@Override
	public Boolean isConnected(){
		return isConnected;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject#getDatabaseList()
	 */
	@Override
	public List<String> getDatabaseList(){
		synchronized (this){
			List <String> dbList=mongoClient.getDatabaseNames();
			dbList.remove("admin");
			dbList.remove("local");
			return dbList;
		}
	}
	
	/*
	 * 	@Override(non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject#datbaseExists(java.lang.String)
	 */
	public boolean datbaseExists(String database){
	List<String> databaseNames = getDatabaseList();
	
	for (int j=0; j<databaseNames.size();j++) // For comparison, we have to iterate manually
		if (databaseNames.get(j).equals(database))
			return true;
	return false;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject#createDatabase(java.lang.String, java.lang.String[])
	 */
	@Override
	public void createDatabase(String dataBase, String[] collectionNames) throws TotalADSDBMSException{
		synchronized (this){
			  if (datbaseExists(dataBase)){
				 	if (isConnected==false){// This code snippet is a check for the breakage of connection during the excution
				 		isConnected=true;   // if reconnection occurs  during execution it will notify all obervers
				 		notifyObservers();
				 	}
					throw new TotalADSDBMSException("Database already exists!");
					
			   }
				
				DB db=mongoClient.getDB(dataBase);
				DBObject options = com.mongodb.BasicDBObjectBuilder.start().add("capped", false).get();
				 
				for (int j=0; j<collectionNames.length;j++)
					db.createCollection(collectionNames[j], options);
				//db.createCollection(Configuration.settingsCollection, options);
				isConnected=true; // if this code is executed after the breakage of connection, make sure isConnected is true if it h
				notifyObservers();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject#createAscendingUniquesIndexes(java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public void createAscendingUniquesIndexes(String dataBase, String collection, String []fields){
		
		DBCollection coll = mongoClient.getDB(dataBase).getCollection(collection);
		DBObject fieldsDB=new BasicDBObject();
		for (int j=0; j<fields.length;j++){
			fieldsDB.put(fields[j],1);
		}
			
		coll.ensureIndex(fieldsDB,new BasicDBObject("unique", true));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject#createDescendingUniquesIndexes(java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public void createDescendingUniquesIndexes(String dataBase, String collection, String []fields){
	
		DBCollection coll = mongoClient.getDB(dataBase).getCollection(collection);
		DBObject fieldsDB=new BasicDBObject();
		for (int j=0; j<fields.length;j++){
			fieldsDB.put(fields[j],-1);
		}
			
		coll.ensureIndex(fieldsDB,new BasicDBObject("unique", true));
		
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDBMSConnection#closeConnection()
	 */
	@Override
	public void closeConnection(){
		synchronized (this){
			isConnected=false;
			mongoClient.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDBMSConnection#deleteDatabase(java.lang.String)
	 */
	@Override
	public void deleteDatabase(String database){
		synchronized(this){
			mongoClient.dropDatabase(database);
			notifyObservers();
		}
		
	}
	/**
	 * Extracts keys and values from public fields of a class's object using reflection and assign it
	 * to the document object based on the data types
	 * @param record Object from which to extract fields
	 * @param document Document object in a collection which will store these fields and values
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 
	private void extractKeysAndValuesfromTheObject(Object record, BasicDBObject document) throws IllegalArgumentException, IllegalAccessException{
			
		  for (Field field : record.getClass().getDeclaredFields()) {
					
					 String name=field.getName();
					 field.setAccessible(true);
					 if (field.getType()==String.class){
						 String val=(String)field.get(record);
						 document.append(name, val);
					 } else if (field.getType()==Integer.class){
						 Integer val=(Integer)field.get(record);
						 document.append(name, val);
					 } else if (field.getType()==Double.class){
						 Double val=(Double)field.get(record);
						 document.append(name, val);
					 }else if (field.getType()==Boolean.class){
						 Boolean val=(Boolean)field.get(record);
						 document.append(name, val);
					 }
			}
	}
	*/
	/**
	 *  Insert an object's field with only one level of nesting. The object's class should  only
	 *  have public data fields when calling this function. For example, create a class A{String b; Integer b},
	 *  assign values after instantiating the object and pass it to the function
	 * @param record Object from which to extract fields and values
	 * @param database Database name
	 * @param collection Collection name
	 * @throws TotalADSDBMSException
	 * @throws IllegalAccessException
	 * @throws IllegalAccessException
	
	public void insert(Object record, String database, String collection) throws TotalADSDBMSException,IllegalAccessException,IllegalAccessException{
		
		WriteResult writeRes=null;
		String exception="";
		DB db = mongoClient.getDB(database);
		DBCollection coll = db.getCollection(collection);
	
		
		BasicDBObject document = new BasicDBObject();
		extractKeysAndValuesfromTheObject(record, document);
		    
		 try {
			  writeRes= coll.insert(document);
	     } catch ( MongoException  e ) {
	          exception ="Caught MongoException cause: "+e.getMessage();
	     }   
          if (writeRes!=null){
        	  CommandResult cmdResult = writeRes.getLastError();
          	  if( !cmdResult.ok()) 
                exception+="\n error : "+cmdResult.getErrorMessage();
          }
	     if (!exception.isEmpty())
		   throw new TotalADSDBMSException(exception);
		
		
	}*/
	
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject#insertUsingJSON(java.lang.String, com.google.gson.JsonObject, java.lang.String)
	 */
	@Override
	public void insertUsingJSON(String database, JsonObject jsonObject, String collection) throws TotalADSDBMSException{
		  try{
		   DB db = mongoClient.getDB(database);
		   DBCollection coll = db.getCollection(collection);
		   BasicDBObject obj = (BasicDBObject)JSON.parse(jsonObject.toString());
		   coll.insert(obj);
		  } catch (Exception ex){ // If there is any exception here cast it as IDataAccessObject exception
			  throw new TotalADSDBMSException(ex.getMessage());
		  }
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject#insertOrUpdateUsingJSON(java.lang.String, com.google.gson.JsonObject, com.google.gson.JsonObject, java.lang.String)
	 */
	@Override
	public void insertOrUpdateUsingJSON(String database, JsonObject keytoSearch, 
						JsonObject jsonObjectToUpdate, String collection) throws TotalADSDBMSException{
		   DB db = mongoClient.getDB(database);
		   DBCollection coll = db.getCollection(collection);
		   
		   BasicDBObject docToUpdate = (BasicDBObject)JSON.parse(jsonObjectToUpdate.toString());
		   
		   BasicDBObject keyToSearch = (BasicDBObject)JSON.parse(keytoSearch.toString());
		   		   
		   WriteResult writeRes=coll.update(keyToSearch, docToUpdate,true,false);
			          
		   CommandResult cmdResult = writeRes.getLastError();
		   if( !cmdResult.ok()) 
			         throw new TotalADSDBMSException ("Error : "+cmdResult.getErrorMessage());
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject#updateFieldsInExistingDocUsingJSON(java.lang.String, com.google.gson.JsonObject, com.google.gson.JsonObject, java.lang.String)
	 */
	@Override
	public void updateFieldsInExistingDocUsingJSON(String database, JsonObject keytoSearch, JsonObject jsonObjectToUpdate, String collection) 
				throws TotalADSDBMSException{
		
		DB db = mongoClient.getDB(database);			
		DBCollection col = db.getCollection(collection);

		BasicDBObject query = (BasicDBObject)JSON.parse(keytoSearch.toString());
		//new BasicDBObject();
		//query.put("name", "MongoDB");

		BasicDBObject newDocument = (BasicDBObject)JSON.parse(jsonObjectToUpdate.toString());
		//newDocument.put("name", "MongoDB-updated");

		BasicDBObject updateObj = new BasicDBObject();
		updateObj.put("$set", newDocument);

		col.update(query, updateObj);

	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject#selectMax(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String selectMax(String key, String database, String collection){
		String maxVal="";
		DB db = mongoClient.getDB(database);
		DBCollection coll = db.getCollection(collection);
		BasicDBObject query=new BasicDBObject(key,-1);
		DBCursor curs= coll.find().sort(query).limit(1);
		if (curs.hasNext())
			maxVal=curs.next().get(key).toString();
		curs.close();
		
		return maxVal;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject#select(java.lang.String, java.lang.String, java.lang.Double, java.lang.String, java.lang.String)
	 */
	@Override
	public DBCursor select(String key, String operator, Double value,String database, String collection ){
		DBCursor cursor;
		BasicDBObject query;
		
		DB db =mongoClient.getDB(database);
		DBCollection coll = db.getCollection(collection);
		
		if ( operator==null || operator.isEmpty() )
			query=new BasicDBObject(key,value);
		else
			query=new BasicDBObject(key, new BasicDBObject(operator, value));
		
		cursor= coll.find(query);
		if (!cursor.hasNext())
			cursor=null;
		return cursor;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject#select(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public DBCursor select(String key, String operator,String value,String database, String collection ){
		DBCursor cursor;
		BasicDBObject query;
		
		DB db =mongoClient.getDB(database);
		DBCollection coll = db.getCollection(collection);
		
		if ( operator==null || operator.isEmpty() )
			query=new BasicDBObject(key,value);
		else
			query=new BasicDBObject(key, new BasicDBObject(operator, value));
		
		cursor= coll.find(query);
		if (!cursor.hasNext())
			cursor=null;
		return cursor;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject#selectAll(java.lang.String, java.lang.String)
	 */
	@Override
	public DBCursor selectAll(String database, String collection ){
		
			DBCursor cursor;
					
			DB db =mongoClient.getDB(database);
			DBCollection coll = db.getCollection(collection);
					
			cursor= coll.find();
			if (!cursor.hasNext())
				cursor=null;
			return cursor;
		
	}
	/**
	 * This function is used to update the values of individual fields--specified by the replacementFieldsAndValue object--
	 *  in documents--specified by the searchFieldsandValues object. Pass two objects of  classes that only has primitive data types
	 *   as fields--no methods. Each object's fields' values  and their data types will be automatically extracted and used
	 *   in the update. If no document matches the criteria then new document will be inserted
	 * @param searchKeyAndItsValue Search fields
	 * @param replacementFieldsAndValues Replacement fields
	 * @param database Database name
	 * @param collection Collection name
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 
	public void replaceFields(Object searchKeyAndItsValue, Object replacementFieldsAndValues,String database, String collection) 
										throws IllegalArgumentException, IllegalAccessException, TotalADSDBMSException{
		
			DB db = mongoClient.getDB(database);
			DBCollection coll=db.getCollection(collection);
			
			BasicDBObject replacementDocument = new BasicDBObject();
		    BasicDBObject setFieldValDocument=new BasicDBObject();
		    
		    extractKeysAndValuesfromTheObject(replacementFieldsAndValues, setFieldValDocument);
		    
			replacementDocument.append("$set", setFieldValDocument);
		 
			BasicDBObject searchQueryDocument = new BasicDBObject();
			//.append("hosting", "hostB");
			extractKeysAndValuesfromTheObject(searchKeyAndItsValue, searchQueryDocument);
		 
			WriteResult writeRes=coll.update(searchQueryDocument, replacementDocument,true,false);
			
								          
			CommandResult cmdResult = writeRes.getLastError();
			if( !cmdResult.ok()) 
			         throw new TotalADSDBMSException ("Error : "+cmdResult.getErrorMessage());
			
			
	}*/
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDBMSSubject#addObserver(org.eclipse.linuxtools.tmf.totalads.dbms.IDBMSObserver)
	 */
	@Override
	public void addObserver(IDBMSObserver observer){
		
		observers.add(observer);
		
		
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDBMSSubject#removeObserver(org.eclipse.linuxtools.tmf.totalads.dbms.IDBMSObserver)
	 */
	@Override
	public void removeObserver(IDBMSObserver observer){
		observers.remove(observer);
		
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDBMSSubject#notifyObservers()
	 */
	@Override
	public void notifyObservers(){
		for (IDBMSObserver ob: observers)
			ob.update();
	}
	
	
}
