package org.eclipse.linuxtools.tmf.totalads.ui;

import java.lang.reflect.Field;
import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

public class DBMS {
	private String HOST;
	private Integer PORT;
	private MongoClient mongoClient;
	//private String DB;
	
	public DBMS() throws UnknownHostException{
		//DB=database;
		HOST=Configuration.host;
		PORT=Configuration.port;
		connect();
	}
	
	public DBMS(String host, Integer port) throws UnknownHostException{
		HOST=host;
		PORT=port;
		//DB=dataBase;
		connect();
	}
	
	private void connect() throws UnknownHostException{
		
		mongoClient = new MongoClient( HOST , PORT );
		mongoClient.setWriteConcern(WriteConcern.JOURNALED);				
	}
	
	
	/**
	 * Creates an index
	 * @param database
	 * @param collection
	 * @param field
	 */
	public void createAscendingIndex(String database, String collection, String field){
		DBCollection coll = mongoClient.getDB(database).getCollection(collection);
		coll.createIndex(new BasicDBObject(field, 1));
	}
	/**
	 * Close the connection
	 */
	public void closeConnection(){
		mongoClient.close();
	}
	
	/**
	 *  Insert an object's field with only one level of nesting. The object's class should  only
	 *  have data fields when calling this function.
	 * @param query
	 * @param database
	 * @param collection
	 * @throws Exception
	 */
	public void insert(Object record, String database, String collection) throws Exception{
		
		WriteResult writeRes=null;
		String exception="";
		DB db = mongoClient.getDB(database);
		DBCollection coll = db.getCollection(collection);
	
		
		BasicDBObject document = new BasicDBObject();
		for (Field field : record.getClass().getDeclaredFields()) {
				
				 String key=field.getName();
				
				 if (field.getType()==String.class){
					 String val=(String)field.get(record);
					 document.append(key, val);
				 } else if (field.getType()==Integer.class){
					 Integer val=(Integer)field.get(record);
					 document.append(key, val);
				 } else if (field.getType()==Double.class){
					 Double val=(Double)field.get(record);
					 document.append(key, val);
				 }else if (field.getType()==Boolean.class){
					 Boolean val=(Boolean)field.get(record);
					 document.append(key, val);
				 }
		}
				
		    
		 try {
			  writeRes= coll.insert(document);
	     } catch ( MongoException  e ) {
	          exception ="Caught MongoException cause: "+e.getMessage();
	     }   
          
         CommandResult cmdResult = writeRes.getLastError();
         if( !cmdResult.ok()) 
              exception+="\n error : "+cmdResult.getErrorMessage();
      	  
	     if (!exception.isEmpty())
		   throw new Exception(exception);
		
		
	}
	/**
	 * 
	 * @param query
	 * @param database
	 * @return
	 */

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
	/**
	 * 
	 * @param key
	 * @param operator
	 * @param value
	 * @param database
	 * @param collection
	 * @return
	 */
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
	/**
	 * 
	 */
	public Boolean update(String query, String database){
		DB db = mongoClient.getDB(database);
		return true;
	}
}
