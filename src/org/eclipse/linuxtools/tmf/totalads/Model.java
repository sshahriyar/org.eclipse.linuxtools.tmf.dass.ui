/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads;

/**
 * @author efraimlopez
 *
 */
public class Model {

	private final String id;
	private final String description;
	
	public Model(String id, String description){
		this.id = id; this.description = description;
	}
	
	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

}
