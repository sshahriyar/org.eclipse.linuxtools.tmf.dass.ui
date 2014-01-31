package org.eclipse.linuxtools.tmf.totalads.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SystemController {

	public SystemController(Composite parent){
		
	//Uncomment the following line when deisgining using window builder and comment it while compiling the application
	//		parent=windowBuilderConfig(parent);
		
		CLabel lblSelectSystem = new CLabel(parent, SWT.NONE);
		lblSelectSystem.setLayoutData(new GridData(SWT.FILL, SWT.TOP,true,false));
		lblSelectSystem.setText("Hosts");
		lblSelectSystem.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));

		//button
		Button btnAnomaliesAddSystem = new Button(parent, SWT.NONE);
		btnAnomaliesAddSystem.setLayoutData(new GridData(SWT.FILL, SWT.TOP,true,false));
		btnAnomaliesAddSystem.setText("Add New Host");
		
		
		//tree
		Tree treeAnomaliesSystems = new Tree(parent, SWT.BORDER | SWT.CHECK|SWT.SINGLE );
		treeAnomaliesSystems.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem ti = (TreeItem) e.item;
				int count=ti.getParent().getItemCount();
				for (int idx=0;idx<count;idx++)
						ti.getParent().getItem(idx).setChecked(false);
				ti.setChecked(true);
				Configuration.selectedDB=ti.getText();
				
				
			}
		});
		treeAnomaliesSystems.setToolTipText("Please select one of the databases");
		//treeAnomaliesSystems.setLayoutData(gridData);
		treeAnomaliesSystems.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
			
		///////data
		TreeItem item1AnomS = new TreeItem(treeAnomaliesSystems, SWT.NONE);
	    item1AnomS.setText("Host-app-01");
		TreeItem item2AnomS = new TreeItem(treeAnomaliesSystems, SWT.NONE);
		item2AnomS.setText("Android-01s");
		TreeItem item3AnomS = new TreeItem(treeAnomaliesSystems, SWT.NONE);
		item3AnomS.setText("states");
	}
	
	private Composite windowBuilderConfig(Composite parents){
		Composite parent=new Composite(parents,SWT.BORDER);
		GridData gridData=new GridData(SWT.LEFT, SWT.FILL, false, true);
		gridData.horizontalSpan=1;
		parent.setLayoutData(gridData);
		parent.setLayout(new GridLayout(1,false));
		return parent;
	}
}
