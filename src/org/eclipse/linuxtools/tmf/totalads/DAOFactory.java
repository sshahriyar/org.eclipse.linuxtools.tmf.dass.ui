/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads;

/**
 * @author efraimlopez
 *
 */
public abstract class DAOFactory{
	
	 public abstract DAOModel createDAOModelInstance();
	 
	 public static DAOFactory newInstance(){
		 return new MongoDAOFactory();
	 }
}
