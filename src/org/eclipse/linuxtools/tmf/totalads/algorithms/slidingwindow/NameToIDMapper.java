package org.eclipse.linuxtools.tmf.totalads.algorithms.slidingwindow;

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
 * This class maps a name to integer id and id to name.
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
        // / for testing only
        // addPredefinedIDs();
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
     * Stores the map into db for reuse
     *
     * @param dataAccessObject
     * @param database
     * @throws TotalADSDBMSException
     */
    public void saveMap(IDataAccessObject dataAccessObject, String database) throws TotalADSDBMSException {
        Gson gson = new Gson();
        JsonElement jsonMap = gson.toJsonTree(fNameToID);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(NameToIDCollection.KEY.toString(), "nametoid");
        jsonObject.add(NameToIDCollection.MAP.toString(), jsonMap);

        JsonObject jsonKey = new JsonObject();
        jsonKey.addProperty(NameToIDCollection.KEY.toString(), "nametoid");

        // console.printTextLn(jsonObject.toString());
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
                    Type stringIntMap = new TypeToken<HashBiMap<String, Integer>>() {
                    }.getType();
                    // gson doesn't recognize bimap and always return a map,
                    // which can not be casted to a bimap, strangely
                    Map<String, Integer> guavaLinkedMap = gson.fromJson(obj.toString(), stringIntMap);
                    fNameToID.putAll(guavaLinkedMap);
                    guavaLinkedMap.clear();// now get rid of it
                    guavaLinkedMap = null;

                }

            }
        }
    }

    /**
	 *
	 */
    public void addPredefinedIDs() {
        for (Integer i = 0; i < 340; i++) {
            fNameToID.put(i.toString(), i);
        }
    }

}// End class
