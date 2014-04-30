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
public class ModelTableContentProvider 
	implements IStructuredContentProvider, ModelsObserver{

	private Viewer viewer = null;
	private Models model = null;
		
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
	    if(oldInput != null) {
	    	Models old = (Models) oldInput;
	        old.removeModelsObserver(this);
	    }
	    if(newInput != null) {
	    	model = (Models) newInput;
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
	
}
