package org.eclipse.linuxtools.tmf.totalads.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;


public class Modeling {
	
	public Modeling(CTabFolder tabFolderDetector){
		ScrolledComposite scrolCompModel=new ScrolledComposite(tabFolderDetector, SWT.H_SCROLL | SWT.V_SCROLL);
		
		CTabItem tbtmModeling = new CTabItem(tabFolderDetector, SWT.NONE);
		tbtmModeling.setText("Modeling");
		
		GridLayout gridTwoColumns=new GridLayout(2,false);
		
		
		Composite comptbtmModeling = new Composite(scrolCompModel, SWT.NONE);
		comptbtmModeling.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		comptbtmModeling.setLayout(gridTwoColumns);
		
		tbtmModeling.setControl(scrolCompModel);
		
		tracesAndModelingType(comptbtmModeling);
		
		ModelSelector mdlSelector=new ModelSelector(comptbtmModeling);
			
	    
	    ProgressConsole progConsole=new ProgressConsole(comptbtmModeling);
		scrolCompModel.setContent(comptbtmModeling);
		 // Set the minimum size
		scrolCompModel.setMinSize(600, 600);
	    // Expand both horizontally and vertically
		scrolCompModel.setExpandHorizontal(true);
		scrolCompModel.setExpandVertical(true);

	}
	/**
	 * 
	 * @param comptbtmModeling
	 */
	public void tracesAndModelingType(Composite comptbtmModeling){
		/**
		 * Group modeling type and traces
		 */
		Group grpTracesModeling=new Group(comptbtmModeling, SWT.NONE);
		grpTracesModeling.setText("Select Traces and Modeling Type");
		grpTracesModeling.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
		grpTracesModeling.setLayout(new GridLayout(2,false));//gridTwoColumns);
		
		Button btnModelingTrain = new Button(grpTracesModeling, SWT.CHECK);
		btnModelingTrain.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		btnModelingTrain.setText("Training");
		
		Button btnModelingValidation = new Button(grpTracesModeling, SWT.CHECK);
		btnModelingValidation.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		btnModelingValidation.setText("Validation");
		
		Text txtModelingTraces = new Text(grpTracesModeling, SWT.BORDER);
		txtModelingTraces.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		Button btnModelingBrowse =new Button(grpTracesModeling, SWT.NONE);
		btnModelingBrowse.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false));
		btnModelingBrowse.setText("Browse Directory");
		
		//---------
		MultipleTracesLoader multLoader=new MultipleTracesLoader();
		/**
		 * End group modeling type and traces
		 */
	}
	
}
