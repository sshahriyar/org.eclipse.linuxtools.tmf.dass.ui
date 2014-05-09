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
	private DataModels model = null;
		
	@Override
	public void dispose() {
		
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
	    if(oldInput != null) {
	    	IDBMS old = (IDBMS) oldInput;
	        old..removeModelsObserver(this);
	    }
	    if(newInput != null) {
	    	model = (DataModels) newInput;
	    	model.addModelsObserver(this);
	    }
	}

	@Override
	public Object[] getElements(Object inputElement) {
		System.out.println("inputElement:"+inputElement);
		if(model!=null){
			return model.listModels().toArray();
		}
		return null;
	}

	@Override
	public void modelsUpdated() {
		if(viewer!=null)
			this.viewer.refresh();
	}

	@Override
	public void update() {
		if(viewer!=null)
			this.viewer.refresh();
		
	}
	
}
