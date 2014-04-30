/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;

/**
 * @author efraimlopez
 *
 */
public class MongoDAOFactory extends DAOFactory{
	
	@Override
	public DAOModel createDAOModelInstance() {
		// TODO Auto-generated method stub
		return new MongoDAOModelImpl();
	}
	
	private static class MongoDAOModelImpl implements DAOModel{

		private DBMS conn = new DBMS();
		
		@Override
		public List<Model> getAllModels() throws TotalAdsDAOException {
			
			List<Model> models = new ArrayList<Model>();
			String error = conn.connect(Configuration.host, Configuration.port);			
			if (!error.isEmpty()){
			    throw new TotalAdsDAOException(error);
			}
			if (conn.isConnected() ){ // if there is a running DB instance
				
				List <String> modelsList= conn.getDatabaseList();
				for(String strModel : modelsList){
					models.add(new Model(strModel, strModel));
				}
				
			}
			conn.closeConnection();
			return models;
		}
		
	}

}
