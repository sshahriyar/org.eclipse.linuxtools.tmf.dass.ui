/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads;


/**
 * @author efraimlopez
 *
 */
public enum TotalAdsState {
	INSTANCE;
	
	private boolean init = false;
	private Anomalies anomalies = null;
	private Models models = null;
	
	public void init(){
		synchronized(this){
			if (!init){
				anomalies = Anomalies.getInstance();
				models = Models.getInstance();
				init = true;
			}
		}
	}
	
	public Anomalies getAnomalies(){
		return anomalies;
	}
	
	public Models getModels(){
		return models;
	}
	
}
