/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads.dbms;


/**
 * Initializes a singleton instance of the database management system
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 * 			<p>	Efraim Lopez </p>
 *
 */
public enum DBMSFactory{
	 INSTANCE;
	 private boolean init = false;
	 private IDBMS connection;

	/**
	 * Instance of the database object
	 * @return
	 */
	 public IDBMS getDBMSInstance(){
			synchronized(this){
				if (!init){
					connection=new MongoDBMS();
					init = true;
				}
			}
			return connection;
	 }
		
}
