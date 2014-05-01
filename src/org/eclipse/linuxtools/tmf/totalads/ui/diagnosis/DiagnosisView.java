package org.eclipse.linuxtools.tmf.totalads.ui.diagnosis;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.linuxtools.tmf.core.signal.TmfSignalHandler;
import org.eclipse.linuxtools.tmf.core.signal.TmfTraceSelectedSignal;
import org.eclipse.linuxtools.tmf.core.trace.ITmfTrace;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.linuxtools.tmf.totalads.ui.TotalADS;
import org.eclipse.linuxtools.tmf.totalads.ui.datamodels.DataModel;
import org.eclipse.linuxtools.tmf.totalads.ui.datamodels.DataModelsView;
import org.eclipse.linuxtools.tmf.totalads.ui.modeling.Modeling;
import org.eclipse.linuxtools.tmf.ui.views.TmfView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

public class DiagnosisView extends TmfView {

	public static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.DiagnosisView";
	private ITmfTrace currentTrace;
	private Diagnosis diagnosis;
	private Modeling modeling;
	private Handler handler;
	private AlgorithmFactory algFactory;
	private TraceTypeFactory trcTypeFactory;
	
	public DiagnosisView() {
		super(VIEW_ID);
	}

	@Override
	public void createPartControl(Composite parent) {
		init();
		diagnosis=new Diagnosis(parent);
		//modeling=new Modeling(parent);
        ITmfTrace trace = getActiveTrace();
        if (trace != null) {
            traceSelected(new TmfTraceSelectedSignal(this, trace));
        } 
        
        getSite().getPage().addSelectionListener(DataModelsView.ID,new ISelectionListener() {
			
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		
				 if (part instanceof DataModelsView) {  
					   Object text = ((org.eclipse.jface.viewers.StructuredSelection) selection).getFirstElement();  
					    
					    if (text!=null)  
					    	System.out.println(((DataModel)text).getId());
					  }  
				//System.out.println(model.getId());
				
			}
		});
	}

	private void init(){
		try{
		    Configuration.connection=new DBMS();
			//	Configuration.connection.connect(Configuration.host, Configuration.port, "u","p");
			String error=Configuration.connection.connect(Configuration.host, Configuration.port);
		
		if (!error.isEmpty()){
				MessageBox msg=new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),SWT.ICON_ERROR);
			    msg.setMessage(error);
			    msg.open();
		}	    
		
		algFactory= AlgorithmFactory.getInstance();
		algFactory.initialize();
		
		trcTypeFactory=TraceTypeFactory.getInstance();
		trcTypeFactory.initialize();
		// Intialize the logger
		handler=null;
		handler= new  FileHandler("totaladslog.xml");
          Logger.getLogger("").addHandler(handler);	
			
		} catch (Exception ex) { // capture all the exceptions here, which are missed by Diagnois and Modeling classes
			
		   MessageBox msg=new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),SWT.ICON_ERROR);
		   if (ex.getMessage()!=null){
		      msg.setMessage(ex.getMessage());
		      msg.open();
		   }
		   Logger.getLogger(TotalADS.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * Gets called from TMF when a trace is selected
	 * @param signal
	 */
	@TmfSignalHandler
    public void traceSelected(final TmfTraceSelectedSignal signal) {
      //  // Don't populate the view again if we're already showing this trace
       // if (currentTrace == signal.getTrace()) {
        //   return;
       // }
        currentTrace = signal.getTrace();
     
            
        //ITmfTrace trace = signal.getTrace();
    	// Right now we are not sure how to determine whether a trace is a user space trace or kernel space trace
        // so we are only considering kernel space traces
        Boolean isKernelSpace=true;
		ITraceTypeReader traceReader=TraceTypeFactory.getInstance().getCTFKernelorUserReader(isKernelSpace);

        diagnosis.updateOnTraceSelection(currentTrace.getPath(), traceReader.getName());
        // trace.sendRequest(req);
    }
	
	@Override
	public void dispose(){
		super.dispose();
		Configuration.connection.closeConnection();
		// This code deinitializes the  Factory instance. It was necessary because
		// if TotalADS plugin is reopened in running Eclipse, the static objects are not 
		// deinitialized on previous close of the plugin. 
		AlgorithmFactory.destroyInstance();
		TraceTypeFactory.destroyInstance();
		
	
	}
}
