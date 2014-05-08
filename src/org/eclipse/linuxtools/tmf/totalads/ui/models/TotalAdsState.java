/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads.ui.models;

import org.eclipse.linuxtools.tmf.totalads.Anomalies;


/**
 * @author efraimlopez
 *
 */
public enum TotalAdsState {
	INSTANCE;
	
	private boolean init = false;
	private Anomalies anomalies = null;
	private DataModels models = null;
	
	public void init(){
		synchronized(this){
			if (!init){
				anomalies = Anomalies.getInstance();
				models = DataModels.getInstance();
				init = true;
			}
		}
	}
	
	public Anomalies getAnomalies(){
		return anomalies;
	}
	
	public DataModels getModels(){
		return models;
	}
	
}
