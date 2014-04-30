/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads;

import java.util.ArrayList;
import java.util.List;

/**
 * @author efraimlopez
 *
 */
public class Anomalies {
	
	private final static Anomalies INSTANCE = new Anomalies();
	
	private final List<IAnomaliesObserver> observerList;
	private final List<Anomaly> anomalies;
	
	private Anomalies(){
		observerList = new ArrayList<IAnomaliesObserver>();
		anomalies = new ArrayList<Anomaly>();
		
		//test data
		anomalies.add(new Anomaly("FC000", "Anonymous intrusion", "Kernel-Trace"));
		anomalies.add(new Anomaly("HkNC0", "Virus Trojan", "Kernel-Trace"));
		anomalies.add(new Anomaly("NLN123", "SYN Attack", "Kernel-Trace"));
	}
	
	public static Anomalies getInstance(){
		return INSTANCE;
	}
	
	public List listAnomalies(){
		return new ArrayList<Anomaly>(anomalies);
	}
	
	public void addAnomaliesObserver(IAnomaliesObserver observer){
		observerList.add(observer);
	}
	
	public void removeAnomaliesObserver(IAnomaliesObserver observer){
		observerList.remove(observer);
	}
	
}
