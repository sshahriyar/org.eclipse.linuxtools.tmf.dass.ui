package org.eclipse.linuxtools.tmf.totalids.ui;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class DBMS {
	private String HOST;
	private Integer PORT;
	private MongoClient mongoClient;
	//private String DB;
	
	public DBMS() throws UnknownHostException{
		//DB=database;
		HOST=Configuration.localHost;
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
						
	}
	
	public void closeConnection(){
		mongoClient.close();
	}
	
	public Boolean insert(String query, String database){
		
		DB db = mongoClient.getDB(database);
		
		return true;
	}

	public String select(String query, String database){
		
		DB db = mongoClient.getDB(database);
		
		return "";
	}
	
	public Boolean update(String query, String database){
		DB db = mongoClient.getDB(database);
		return true;
	}
}
