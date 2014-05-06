package org.eclipse.linuxtools.tmf.totalads.ui.datamodels;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmTypes;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;

public class AlgorithmTreeContentProvider implements ITreeContentProvider {

	public AlgorithmTreeContentProvider() {
		
	}

	@Override
	public void dispose() {
		// Nothing to dispose

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		//Nothing changes

	}

	@Override
	public Object[] getElements(Object inputElement) {
		AlgorithmFactory  algFac=AlgorithmFactory.getInstance();
		IDetectionAlgorithm []algorithms  = algFac.getAlgorithms((AlgorithmTypes) inputElement);
		
		return algorithms;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		
		return null;
	}

	@Override
	public Object getParent(Object element) {
		
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		
		return false;
	}

}
