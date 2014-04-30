/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads;

import java.util.List;

/**
 * @author efraimlopez
 *
 */
public interface DAOModel {
	public List<Model> getAllModels() throws TotalAdsDAOException;
}
