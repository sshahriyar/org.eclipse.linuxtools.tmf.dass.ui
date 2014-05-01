/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads.ui;

import org.eclipse.linuxtools.tmf.core.signal.TmfSignalHandler;
import org.eclipse.linuxtools.tmf.core.signal.TmfTraceSelectedSignal;
import org.eclipse.linuxtools.tmf.core.trace.ITmfTrace;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.Diagnosis;
import org.eclipse.linuxtools.tmf.totalads.ui.modeling.Modeling;
import org.eclipse.linuxtools.tmf.ui.views.TmfView;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author efraimlopez
 *
 */
public class ModelingView extends TmfView {

	public static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.ModelingView";
	

	private Modeling modeling;
	
	/**
	 * 
	 */
	public ModelingView() {
		super(VIEW_ID);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		modeling=new Modeling(parent);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		

	}

}
