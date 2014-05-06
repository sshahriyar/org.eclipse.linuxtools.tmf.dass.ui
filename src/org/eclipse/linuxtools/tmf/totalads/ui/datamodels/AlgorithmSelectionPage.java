package org.eclipse.linuxtools.tmf.totalads.ui.datamodels;


import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmTypes;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;



public class AlgorithmSelectionPage extends WizardPage {
	private CheckboxTreeViewer treeViewer;
	
	public AlgorithmSelectionPage() {
		super("Algorithm Selection");
		setTitle("Select an algorithm");
	}

	@Override
	public void createControl(Composite compParent) {
		Composite compAlgorithms=new Composite(compParent, SWT.NONE);
		compAlgorithms.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false,1,6));
		compAlgorithms.setLayout(new GridLayout(1,false));
		
		treeViewer=new CheckboxTreeViewer(compAlgorithms);
		treeViewer.getTree().setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false,1,6));
		treeViewer.setContentProvider(new AlgorithmTreeContentProvider());
		treeViewer.setLabelProvider(new AlgorithmTreeLabelProvider());
		treeViewer.setInput(AlgorithmTypes.ANOMALY);
		
		// Event handler for the tree
		treeViewer.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
	
				String algorithm=((IDetectionAlgorithm)event.getElement()).getName();
				for (int i=0;i<treeViewer.getTree().getItemCount(); i++){
			    	 if (!treeViewer.getTree().getItem(i).getText().equalsIgnoreCase(algorithm))
			    	     	treeViewer.getTree().getItem(i).setChecked(false);// Make all unchecked
				}
				
			}
		});

		setControl(compAlgorithms);
		setPageComplete(false);
	}
	
	@Override
	public boolean canFlipToNextPage() {
	return true;
	}

	
}
