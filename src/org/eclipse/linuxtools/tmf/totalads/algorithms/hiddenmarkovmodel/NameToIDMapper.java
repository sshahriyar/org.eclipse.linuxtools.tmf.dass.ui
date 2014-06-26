package org.eclipse.linuxtools.tmf.totalads.algorithms.hiddenmarkovmodel;

import java.lang.reflect.Type;
import java.util.Map;

import org.eclipse.linuxtools.tmf.totalads.dbms.IDBCursor;
import org.eclipse.linuxtools.tmf.totalads.dbms.IDBRecord;
import org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;

import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * This class maps a name to integer id
 *
 * @author <p>
 *         Syed Shariyar Murtaza justsshary@hotmail.com
 *         </p>
 *
 */
class NameToIDMapper {

    private HashBiMap<String, Integer> fNameToID;

    /**
     * Constructor
     */
    public NameToIDMapper() {
        fNameToID = HashBiMap.create();
    }

    /**
     * Returns the id mapped to a name
     *
     * @param name
     *            Event name
     * @return Integer id
     */
    public Integer getId(String name) {
        Integer id = fNameToID.get(name);
        if (id == null) {
            Integer size = fNameToID.size();
            updateId(name, size);
            return size;
        }
        return id;
    }

    /**
     * Sets the id to a name
     *
     * @param name
     *            Event name
     * @param value
     *            Id value
     */
    private void updateId(String name, Integer value) {
        fNameToID.put(name, value);
        // System.out.println(name + " "+ value);
    }

    /**
     * Size
     *
     * @return
     */
    public Integer getSize() {
        return fNameToID.size();

    }

    /**
     * Returns the name for an id
     *
     * @param id
     *            Integer id
     * @return Key
     */
    public String getKey(Integer id) {
        return fNameToID.inverse().get(id);
    }

    /**
     * Stores the map into database for reuse
     *
     * @param dataAccessObject
     * @param database
     * @throws TotalADSDBMSException
     */
    public void saveMap(IDataAccessObject dataAccessObject, String database) throws TotalADSDBMSException {
        Gson gson = new Gson();
        JsonElement jsonMap = gson.toJsonTree(fNameToID.inverse());// Store as integer id and string value
                                                                    // because if there is a dot in the field name
                                                                    //mongodb would result in error
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(NameToIDCollection.KEY.toString(), "nametoid");// we add a key here because it could
                                                                                // be the first time insertion
        jsonObject.add(NameToIDCollection.MAP.toString(), jsonMap);

        JsonObject jsonKey = new JsonObject();
        jsonKey.addProperty(NameToIDCollection.KEY.toString(), "nametoid");

        dataAccessObject.insertOrUpdateUsingJSON(database, jsonKey, jsonObject, NameToIDCollection.COLLECTION_NAME.toString());

    }

    /**
     * Loads an existing map from db, if exist
     *
     * @param dataAccessObject
     * @param database
     * @throws TotalADSDBMSException
     */
    public void loadMap(IDataAccessObject dataAccessObject, String database) throws TotalADSDBMSException {

        try (IDBCursor cursor = dataAccessObject.selectAll(database,
                NameToIDCollection.COLLECTION_NAME.toString())) {
            if (cursor.hasNext()) {
                Gson gson = new Gson();
                IDBRecord record = cursor.next();

                Object obj = record.get(NameToIDCollection.MAP.toString());
                if (obj != null) {
                    Type stringIntMap = new TypeToken<HashBiMap<Integer, String>>() {
                    }.getType();
                    // gson doesn't recognize bimap and always return a map,
                    // which can not be casted to a bimap, strangely
                    Map<Integer, String> guavaLinkedMap = gson.fromJson(obj.toString(), stringIntMap);

                    // Transform id to name mapping to name to id
                    // We used id to name mapping to avoid errors while storing in mongodb
                    for (Map.Entry<Integer,String> keyVal:guavaLinkedMap.entrySet()){
                     // Insert string as a key and id a value in the map
                        fNameToID.put(keyVal.getValue(), keyVal.getKey());
                    }

                    guavaLinkedMap.clear();// now get rid of it
                    guavaLinkedMap = null;

                }
            }
        }
    }

}// End class
