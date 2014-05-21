package org.eclipse.linuxtools.tmf.totalads.dbms;

import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * This class creates an iterator over the collection of documents of a MongoDB
 * by wrapping the MongoDB {@link DBCursor} class
 *
 * @author Syed Shariyar Murtaza
 *
 */
class MongoDBCursor implements IDBCursor {
    private DBCursor fDbCursor;

    /**
     * Constructor to create an iterator over the documents' collection for a
     * query
     *
     * @param dbCursor
     *            Cursor on a collection
     */
    public MongoDBCursor(DBCursor dbCursor) {
        fDbCursor = dbCursor;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDBCursor#hasNext()
     */
    @Override
    public boolean hasNext() {

        return fDbCursor.hasNext();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDBCursor#next()
     */
    @Override
    public IDBRecord next() {
        DBObject document = fDbCursor.next();
        MongoDBRecord record = new MongoDBRecord(document);
        return record;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws TotalADSDBMSException {
        try {
            fDbCursor.close();
        }catch (Exception ex){
            throw new TotalADSDBMSException(ex);
        }
    }

}
