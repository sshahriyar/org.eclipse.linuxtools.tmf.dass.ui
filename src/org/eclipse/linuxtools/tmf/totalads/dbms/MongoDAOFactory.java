///**
// * 
// */
//package org.eclipse.linuxtools.tmf.totalads.dbms;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.eclipse.linuxtools.tmf.totalads.TotalAdsDAOException;
//import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
//import org.eclipse.linuxtools.tmf.totalads.ui.models.DataModel;
//
///**
// * @author efraimlopez
// *
// */
//public class MongoDAOFactory extends DBMSFactory{
//	
//	@Override
//	public IDAOModel createDAOModelInstance() {
//		
//		return new MongoDAOModelImpl();
//	}
//	
//	private static class MongoDAOModelImpl implements IDAOModel{
//
//		private IDBMS conn = new MongoDBMS();
//		
//		@Override
//		public List<DataModel> getAllModels() throws TotalAdsDAOException {
//			
//			List<DataModel> models = new ArrayList<DataModel>();
//			String error = conn.connect(Configuration.host, Configuration.port);			
//			if (!error.isEmpty()){
//			    throw new TotalAdsDAOException(error);
//			}
//			if (conn.isConnected() ){ // if there is a running DB instance
//				
//				List <String> modelsList= conn.getDatabaseList();
//				for(String strModel : modelsList){
//					models.add(new DataModel(strModel, strModel));
//				}
//				
////			}
//			conn.closeConnection();
//			return models;
//		}
//		
//	}
//
//}
