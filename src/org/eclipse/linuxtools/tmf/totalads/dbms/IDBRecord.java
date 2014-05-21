package org.eclipse.linuxtools.tmf.totalads.dbms;
/**
 *
 * Interface to access a record (or a document) in a database
 * @author Syed Shariyar Murtaza
 *
 */
public interface IDBRecord {
    /**
     * Returns an object corresponding to a field
     * @param fieldName The name of the field in a record (document)
     *
     * @return An object corresponding to the field
     */
public Object get(String fieldName);
}
