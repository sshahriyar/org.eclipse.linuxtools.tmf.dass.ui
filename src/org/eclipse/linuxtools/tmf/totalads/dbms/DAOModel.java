/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads.dbms;

import java.util.List;

import org.eclipse.linuxtools.tmf.totalads.TotalAdsDAOException;
import org.eclipse.linuxtools.tmf.totalads.ui.datamodels.DataModel;

/**
 * @author efraimlopez
 *
 */
public interface DAOModel {
	public List<DataModel> getAllModels() throws TotalAdsDAOException;
}
