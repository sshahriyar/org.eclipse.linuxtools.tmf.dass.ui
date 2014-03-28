package org.eclipse.linuxtools.tmf.totalads.ui;

import org.eclipse.linuxtools.tmf.totalads.ui.ksm.*;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;





import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

public class DBMS {
	//private String HOST;
	//private Integer PORT;
	private MongoClient mongoClient;
	private Boolean isConnected=false;
	//private String DB;
	ArrayList<Observer> observers=new ArrayList<Observer>();
	
	public DBMS() {
		
	}
	
	
	
	public String connect(String host, Integer port) throws UnknownHostException{
		String message="";
		mongoClient = new MongoClient( host , port );
		mongoClient.setWriteConcern(WriteConcern.JOURNALED);
		
				
		try {
			
			mongoClient.getDatabaseNames(); // if this doesn't work then there is no running DB. 
											// Unfortunately,mongoClient doesn't tell whether there is a DB or not
		} catch (Exception ex){
			isConnected=false;
			message="Unable to connect to MongoDB.";
			return message;
		}
		isConnected=true;// if it reaches here then it is connected
		return message;
	}
	/**
	 * 
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @return Empty message if connected or error message
	 * @throws UnknownHostException
	 */
	public String connect(String host, Integer port, String username, String password) throws UnknownHostException{
		
		String message=connect(host,port);
		if (message.isEmpty()){
			// if there is a running db then check this
			DB db=mongoClient.getDB("admin");
		
			if (db.authenticate(username, password.toCharArray())==false){
				isConnected=false;
				message="Invalid user name or password.";
			}
			else
				isConnected=true;// if it reaches here then everything is fine
		}
		return message;
	}
	
	Boolean isConnected(){
		return isConnected;
	}
	/**
	 * Get database list
	 * @return
	 */
	public List<String> getDatabaseList(){
		List <String> dbList=mongoClient.getDatabaseNames();
		// remove system databases
		dbList.remove("admin");
		dbList.remove("local");
		return dbList;
		
	}
	/**
	 * Checks the existence of the database
	 * @param database
	 * @return
	 */
	public boolean datbaseExists(String database){
	List<String> databaseNames = getDatabaseList();
	
	for (int j=0; j<databaseNames.size();j++) // For a case insensitive comparison, we have to iterate manually
		if (databaseNames.get(j).equalsIgnoreCase(database))
			return true;
	return false;
		
	}
	/**
	 * Creates a database and collections
	 * @param dataBase
	 * @throws Exception
	 */
	public void createDatabase(String dataBase, String[] collectionNames) throws TotalADSUiException{
	
	 if (datbaseExists(dataBase))
			throw new TotalADSUiException("Database already exists!");
		
		DB db=mongoClient.getDB(dataBase);
		DBObject options = com.mongodb.BasicDBObjectBuilder.start().add("capped", false).get();
		 
		for (int j=0; j<collectionNames.length;j++)
			db.createCollection(collectionNames[j], options);
		//db.createCollection(Configuration.settingsCollection, options);
		notifyObservers();
	}
	
	/**
	 * Creates an index
	 * @param database
	 * @param collection
	 * @param field
	 */
	public void createAscendingIndex(String dataBase, String collection, String field){
		DBCollection coll = mongoClient.getDB(dataBase).getCollection(collection);
		coll.createIndex(new BasicDBObject(field, 1));
	}
	/**
	 * Close the connection
	 */
	public void closeConnection(){
		mongoClient.close();
	}
	/**
	 * 
	 * @param database
	 */
	public void deleteDatabase(String database){
		mongoClient.dropDatabase(database);
		notifyObservers();
	}
	/**
	 * Extracts keys and values from a claas's object using reflection and assign it
	 * to the document object based on the data types
	 * @param record
	 * @param document
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void extractKeysAndValuesfromTheObject(Object record, BasicDBObject document) throws IllegalArgumentException, IllegalAccessException{
			
		  for (Field field : record.getClass().getDeclaredFields()) {
					
					 String key=field.getName();
					 field.setAccessible(true);
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
	}
	
	/**
	 *  Insert an object's field with only one level of nesting. The object's class should  only
	 *  have data fields when calling this function. For example, create a class A{String b; Integer b},
	 *  assign values after instantiating the object and pass it to the function
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
		extractKeysAndValuesfromTheObject(record, document);
		    
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
	 * Inserts an object in the form of json representation into the database. Any kind of complex
	 *  data structure can be converted to JSON  using gson library and passed to this function 
	 * @param database
	 * @param jsonObject
	 * @param collection
	 */
	public void insertUsingJSON(String database, JsonObject jsonObject, String collection){
		   DB db = mongoClient.getDB(database);
		   DBCollection coll = db.getCollection(collection);
		   BasicDBObject obj = (BasicDBObject)JSON.parse(jsonObject.toString());
		   coll.insert(obj);
	}

	/**
	 * Inserts or updates (if already exists) an object in the form of json representation into the database. Any kind of complex
	 *  data structure can be converted to JSON  using gson library and passed to this function 
	 * @param database
	 * @param jsonObject
	 * @param collection
	 */
	public void insertOrUpdateUsingJSON(String database, JsonObject keytoSearch, 
						JsonObject jsonObjectToUpdate, String collection) throws Exception{
		   DB db = mongoClient.getDB(database);
		   DBCollection coll = db.getCollection(collection);
		   
		   BasicDBObject docToUpdate = (BasicDBObject)JSON.parse(jsonObjectToUpdate.toString());
		   
		   BasicDBObject keyToSearch = (BasicDBObject)JSON.parse(keytoSearch.toString());
		   		   
		   WriteResult writeRes=coll.update(keyToSearch, docToUpdate,true,false);
			          
		   CommandResult cmdResult = writeRes.getLastError();
		   if( !cmdResult.ok()) 
			         throw new Exception ("Error : "+cmdResult.getErrorMessage());
		
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
	 * Returns a set of documents based on a key search
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
	 * Returns a set of documents based on a key search
	 * @param key
	 * @param operator
	 * @param value
	 * @param database
	 * @param collection
	 * @return
	 */
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
	
	/**
	 * Returns a set of documents based on a key search
	 * @param key
	 * @param operator
	 * @param value
	 * @param database
	 * @param collection
	 * @return
	 */
	
	public DBCursor selectAll(String database, String collection ){
		DBCursor cursor;
		BasicDBObject query;
		
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
	 * @param searchKeyAndItsValue searchFields
	 * @param replacementFieldsAndValues replacement fields
	 * @param database database name
	 * @param collection collection name
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void replaceFields(Object searchKeyAndItsValue, Object replacementFieldsAndValues,String database, String collection) 
										throws IllegalArgumentException, IllegalAccessException, Exception{
		
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
			         throw new Exception ("Error : "+cmdResult.getErrorMessage());
			
			
	}
	//// observer methods implemented without any interface
	public void addObserver(Observer observer){
		observers.add(observer);
		
	}
	public void removeObserver(Observer observer){
		observers.remove(observer);
		
	}
	
	public void notifyObservers(){
		for (Observer ob: observers)
			ob.update();
	}
	
	
}
