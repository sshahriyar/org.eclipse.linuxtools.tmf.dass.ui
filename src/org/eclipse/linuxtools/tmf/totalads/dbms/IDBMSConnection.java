package org.eclipse.linuxtools.tmf.totalads.dbms;

interface IDBMSConnection {
	/**
	 * Connects with the database
	 * @param host Host name 
	 * @param port Port number
	 * @return Returns an empty message if connection is made with the database, else returns the error message
	 * @throws UnknownHostException
	 */
	public String connect(String host, Integer port) ;
	
	/**
	 * Connects with the database using a authentication mechanism
	 * @param host Host name
	 * @param port Port name
	 * @param username User name
	 * @param password Password
	 * @return Empty message if connected or error message
	 * @throws UnknownHostException
	 */
	public String connect(String host, Integer port, String username, String password, String database);

	/**
	 * Closes the connection
	 */
	public void closeConnection();
	/**
	 * Deletes a database
	 * @param database Database name
	 */
	public void deleteDatabase(String database);
}
