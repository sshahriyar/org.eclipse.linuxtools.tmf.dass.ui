package org.eclipse.linuxtools.tmf.totalads.dbms;

import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;

/**
 * An interface to iterator over the collection of records (or documents)
 *
 * @author Syed Shariyar Mutaza
 *
 */
public interface IDBCursor extends AutoCloseable {
    /**
     * Determines whether the next record (or document) exists or not
     *
     * @return True if there is a next record, else it is false
     */
    public boolean hasNext();

    /**
     * Returns the next record (or document)
     *
     * @return An object of type {@link IDBRecord}
     */
    public IDBRecord next();

    /*
     * (non-Javadoc)
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws TotalADSDBMSException;
}
