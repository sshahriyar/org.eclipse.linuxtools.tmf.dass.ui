/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author efraimlopez
 *
 */
public class AnomaliesTableContentProvider 
	implements IStructuredContentProvider, AnomaliesObserver{

	private Viewer viewer = null;
	private Anomalies model = null;
		
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
	    if(oldInput != null) {
	    	Anomalies old = (Anomalies) oldInput;
	        old.removeAnomaliesObserver(this);
	    }
	    if(newInput != null) {
	    	model = (Anomalies) newInput;
	    	model.addAnomaliesObserver(this);
	    }
	}

	@Override
	public Object[] getElements(Object inputElement) {
		System.out.println("inputElement:"+inputElement);
		if(model!=null){
			return model.listAnomalies().toArray();
		}
		return null;
	}

	@Override
	public void anomaliesUpdated() {
		if(viewer!=null)
			this.viewer.refresh();
	}
	
}
