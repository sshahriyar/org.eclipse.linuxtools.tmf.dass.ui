/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads;

/**
 * @author efraimlopez
 *
 */
public class Anomaly {

	private final String id;
	private final String description;
	private final String trace;
	
	public Anomaly(String id, String description, String trace){
		this.id = id; this.description = description; this.trace = trace;
	}
	
	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getTrace() {
		return trace;
	}
}
