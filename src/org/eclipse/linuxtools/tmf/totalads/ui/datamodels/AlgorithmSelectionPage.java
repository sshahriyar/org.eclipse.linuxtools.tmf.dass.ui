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


/**
 * This class creates the Algorithm Selection page
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class AlgorithmSelectionPage extends WizardPage {
	private CheckboxTreeViewer treeViewer;
	private Boolean isItemSelected;
	private IDetectionAlgorithm algorithmSelected;
	/**
	 * Constructor
	 */
	public AlgorithmSelectionPage() {
		super("Algorithm Selection");
		setTitle("Select an algorithm");
		isItemSelected=false;
		
	}
	//
	//Creates GUI widgets
	//
	@Override
	public void createControl(Composite compParent) {
		Composite compAlgorithms=new Composite(compParent, SWT.NONE);
		compAlgorithms.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
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
	
				IDetectionAlgorithm algorithm= ((IDetectionAlgorithm)event.getElement());
				String algorithmName=algorithm.getName();
				for (int i=0;i<treeViewer.getTree().getItemCount(); i++){
			    	 if (!treeViewer.getTree().getItem(i).getText().equalsIgnoreCase(algorithmName))
			    	     	treeViewer.getTree().getItem(i).setChecked(false);// Make all unchecked
			    	 else{
			    		 isItemSelected=treeViewer.getTree().getItem(i).getChecked();
			    	 }
				}
				if (isItemSelected)
					algorithmSelected=algorithm;
				else
					algorithmSelected=null;
				
				getWizard().getContainer().updateButtons();
							
			}
		});
		
		setControl(compAlgorithms);
		setPageComplete(false);
	}
	
	//
	//This function enables next button
	//
	@Override
	public boolean canFlipToNextPage() {
		if (isItemSelected)
				return true;
		else
				return false;
	}
	
	
	/**
	 * Gets the currently selected algorithm
	 * @return Algorithm object
	 */
	public IDetectionAlgorithm getSelectedAlgorithm(){
		return algorithmSelected;
	}
}
