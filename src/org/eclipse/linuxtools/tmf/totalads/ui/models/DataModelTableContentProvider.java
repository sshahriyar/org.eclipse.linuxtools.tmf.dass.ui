/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads.ui.models;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.linuxtools.tmf.totalads.dbms.IDBMS;
import org.eclipse.linuxtools.tmf.totalads.dbms.IDBMSObserver;

/**
 * @author efraimlopez
 *
 */
public class DataModelTableContentProvider 
	implements IStructuredContentProvider, IDBMSObserver{

	private Viewer viewer = null;
	private IDBMS model = null;
		
	@Override
	public void dispose() {
		
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
	    if(oldInput != null) {
	    	IDBMS old = (IDBMS) oldInput;
	        old.removeObserver(this);
	    }
	    if(newInput != null) {
	    	model = (IDBMS) newInput;
	    	model.addObserver(this);
	    }
	}

	@Override
	public Object[] getElements(Object inputElement) {
		model = (IDBMS) inputElement;
		
		if(model.isConnected())
			return model.getDatabaseList().toArray();
		else
			return new String[] {"No Connection"};
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.dbms.IDBMSObserver#update()
	 */

	@Override
	public void update() {
		if(viewer!=null)
			this.viewer.refresh();
		
	}
	
}
