package org.eclipse.linuxtools.tmf.totalads.algorithms.hiddenmarkovmodel;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * This class maps a name to integer id
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com</p>
 *
 */
public class NameToIDMapper {
	
	private HashBiMap <String, Integer> nameToID;
	
	/**
	 * Constructor
	 */
	public NameToIDMapper(){
		nameToID=HashBiMap.create();
	}
	/**
	 * Returns the id mapped to a name
	 * @param name Event name
	 * @return Integer id
	 */
	public Integer getId(String name){
		Integer id=nameToID.get(name);
		if (id==null){
			Integer size=nameToID.size();
			updateId(name, size);
			return size;
		}else
		  return id;
	}
	/**
	 * Sets the id to a name
	 * @param name Event name
	 * @param value Id value
	 */
	private void updateId(String name, Integer value){
		nameToID.put(name, value);
		System.out.println(name + " "+ value);
	}
	/**
	 * Size
	 * @return
	 */
	public Integer getSize(){
		return nameToID.size();
		
	}
	/**
	 * Returns the name for an id
	 * @param id Integer id
	 * @return Key 
	 */
	public String getKey(Integer id){
		return nameToID.inverse().get(id);
	}
	
	/**
	 * Stroes the map into db for reuse
	 * @param connection
	 * @param database
	 * @throws TotalADSDBMSException
	 */
	public void saveMap(DBMS connection, String database) throws TotalADSDBMSException{
		Gson gson =new Gson();
		JsonElement jsonMap= gson.toJsonTree(nameToID);
		JsonObject jsonObject= new JsonObject();
		jsonObject.addProperty(NameToIDCollection.KEY.toString(), "nametoid");
		jsonObject.add(NameToIDCollection.MAP.toString(), jsonMap);
		
		JsonObject jsonKey=new JsonObject();
		jsonKey.addProperty(NameToIDCollection.KEY.toString(), "nametoid");
		
		//console.printTextLn(jsonObject.toString());
		connection.insertOrUpdateUsingJSON(database, jsonKey, jsonObject, NameToIDCollection.COLLECTION_NAME.toString());
		
	}
	/**
	 * Loads an existing map from db, if exist
	 * @param connection
	 * @param database
	 */
	@SuppressWarnings("unchecked")
	public void loadMap(DBMS connection, String database){
		DBCursor cursor=connection.selectAll(database, NameToIDCollection.COLLECTION_NAME.toString());
		if (cursor!=null){
			 Gson gson =new Gson();
		     DBObject dbObject=cursor.next();
			 Object obj=dbObject.get(NameToIDCollection.MAP.toString());
			 if (obj!=null){
				 	Type stringIntMap = new TypeToken<BiMap<String, Integer>>(){}.getType();
					 nameToID= gson.fromJson(obj.toString(), stringIntMap);
			 }
			cursor.close();
	     }
	}
	
}// End class
