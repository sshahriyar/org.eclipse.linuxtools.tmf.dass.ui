/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads.ui.models;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject;
import org.eclipse.linuxtools.tmf.totalads.dbms.IDBMSObserver;

/**
 * @author <p>Efraim Lopez </p>
 *         <p> Syed Shariyar Murtaza justssahry@htomail.com </p>
 *
 */
class DataModelTableContentProvider	implements IStructuredContentProvider{

	private Viewer viewer = null;
	private IDataAccessObject dao = null;
	public static final String EMPTY_VIEW_FIELD="No connection";	
	@Override
	public void dispose() {
		
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
	    if(newInput != null) {
	    	dao = (IDataAccessObject) newInput;
	     }
	}

	@Override
	public Object[] getElements(Object inputElement) {
		dao = (IDataAccessObject) inputElement;
		
		if(dao.isConnected())
			return dao.getDatabaseList().toArray();
		else
			return new String[] {EMPTY_VIEW_FIELD};
	}

	
}
