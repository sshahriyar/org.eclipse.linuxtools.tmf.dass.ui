package org.eclipse.linuxtools.tmf.totalads.dbms;

import com.mongodb.DBObject;

/**
 * This class wraps a document object from MongoDB
 * @author Syed Shariyar Murtaza
 *
 */
class MongoDBRecord implements IDBRecord {

    private DBObject fDbObject;

    /**
     * Constructor to create a mongo document object for TotalADS
     *
     * @param dbObject
     *            An object representing the document
     */
    public MongoDBRecord(DBObject dbObject) {
        fDbObject = dbObject;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.linuxtools.tmf.totalads.dbms.IDBRecord#get(java.lang.String)
     */
    @Override
    public Object get(String fieldName) {
        return fDbObject.get(fieldName);
    }

}
