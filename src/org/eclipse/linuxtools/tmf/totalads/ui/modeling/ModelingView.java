/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads.ui.modeling;


import java.util.HashSet;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.linuxtools.tmf.totalads.ui.models.DataModelsView;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * @author  <p> Efraim Lopez </p>
 *          <p> Syed Shariyar Murtaza justsshary@hotmail.com </p> 
 *
 */
public class ModelingView extends ViewPart {

	public static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.ModelingView";
	

	private Modeling modeling;
	
	/**
	 * 
	 */
	public ModelingView() {
		modeling=new Modeling();

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite compParent) {
		modeling.createControls(compParent);;
		
		/// Registers a listener to Eclipse to get the list of models selected (checked) by the user 
        getSite().getPage().addSelectionListener(DataModelsView.ID,	new ISelectionListener() {
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		
				 if (part instanceof DataModelsView) {  
					   Object obj = ((org.eclipse.jface.viewers.StructuredSelection) selection).getFirstElement();
					   HashSet<String> modelList= (HashSet<String>)obj;
					   modeling.updateonModelSelection(modelList); 
				    }  
				}
		});
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		

	}

}
