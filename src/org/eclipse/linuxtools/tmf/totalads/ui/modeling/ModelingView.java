/**
 *
 */
package org.eclipse.linuxtools.tmf.totalads.ui.modeling;


import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.linuxtools.tmf.totalads.ui.models.DataModelsView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * @author <p> Efraim Lopez </p>
 *          <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class ModelingView extends ViewPart {
    /**
     * View ID
     */
	public static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.ModelingView"; //$NON-NLS-1$
	// Variables
	private Modeling fModeling;
	private SelectionListener fSelectionListener;
	/////////////////////////////////////////
	///Inner class implementing a listener for another view
	////////////////////////////////////////
	private class SelectionListener implements ISelectionListener {
		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {

			 if (part instanceof DataModelsView) {
				   Object obj = ((org.eclipse.jface.viewers.StructuredSelection) selection).getFirstElement();
				   HashSet<String> modelList= (HashSet<String>)obj;
				   fModeling.updateonModelSelection(modelList);
			    }
			}
	}

	/**
	 * Constructor
	 */
	public ModelingView() {
		fModeling=new Modeling();
		fSelectionListener=new SelectionListener();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite compParent) {
		fModeling.createControls(compParent);
		try{
			// Trying to clear the already selected instances in the models view when this view is opened in the middle of execution
	        // If the view is opened in the middle, already selected models are not available using the event handler
	        IViewPart dataModelsView= getSite().getWorkbenchWindow().getActivePage().showView(DataModelsView.ID);
	        if (dataModelsView instanceof DataModelsView) {
                ((DataModelsView)dataModelsView).refresh();
            }
			/// Registers a listener to Eclipse to get the list of models selected (checked) by the user
	        getSite().getPage().addSelectionListener(DataModelsView.ID,	fSelectionListener);

		}catch (PartInitException e) {

	        	MessageBox msgBox=new MessageBox(getSite().getShell(),SWT.OK);
				if(e.getMessage()!=null){
					msgBox.setMessage(e.getMessage());
				} else {
                    msgBox.setMessage("Unable to launch a view");
                }
				msgBox.open();
				Logger.getLogger(Modeling.class.getName()).log(Level.SEVERE,null,e);
		}


	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {


	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose(){
		getSite().getPage().removeSelectionListener(DataModelsView.ID,	 fSelectionListener);
	 }

}
