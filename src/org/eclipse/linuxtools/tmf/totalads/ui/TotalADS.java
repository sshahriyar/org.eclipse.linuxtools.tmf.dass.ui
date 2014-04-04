package org.eclipse.linuxtools.tmf.totalads.ui;

import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Shell;

//import org.eclipse.swt.custom.TableTree;


//public class CompDetective extends Composite {
public class TotalADS  {
	/*private Table tblAnalysisTraceList;
	private Text txtAnalysisIdentify;
	private Text txtAnalysisDetails;
	private Text txtTraceTypeRegularExpression;
	private Text lblAnomaliesTrain;
	private Text lblAnomaliesValidate;
	private Text lblAnomaliesTest;
	private Text txtAnomaliesProgress;
	private Table tableClassificationPredictions;*/
	private Modeling modeling;
	private Diagnosis diagnosis;
	
	//GridData gridDataFullFill=new GridData(SWT.FILL, SWT.FILL, true, true );
	//GridData gridDataHorizontalFill=new GridData(SWT.FILL, SWT.TOP, true, false );
	//GridData gridDataVerticalFill=new GridData(SWT.TOP, SWT.FILL, false, true );
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TotalADS(Composite parent, int style) {
		
	  try{
		    	Configuration.connection=new DBMS();
			
			//  (Configuration.host, Configuration.port);
			//	Configuration.connection.connect(Configuration.host, Configuration.port, "u","p");
				String error=Configuration.connection.connect(Configuration.host, Configuration.port);
		  	
			if (!error.isEmpty()){
					MessageBox msg=new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),SWT.ICON_ERROR);
				    msg.setMessage(error);
				    msg.open();
			}	    
		  
			// This code deinitializes the  Factory instance. It was necessary because
			// if TotalADS plugin is reopened in running Eclipse, the static objects are not 
			// deinitialized on previous close of the plugin. 
			AlgorithmFactory.destroyInstance();
			TraceTypeFactory.destroyInstance();
			
			AlgorithmFactory modFactory= AlgorithmFactory.getInstance();
			modFactory.initialize();
		
			TraceTypeFactory trcTypeFactory=TraceTypeFactory.getInstance();
			trcTypeFactory.initialize();
						
			//super(parent, style);
			parent.setLayout(new GridLayout(2,false));
			
			//leftPane(parent);
			
			CTabFolder tabFolderDetector = new CTabFolder(parent, SWT.BORDER);
			tabFolderDetector.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			gridData.horizontalSpan=1;
			tabFolderDetector.setLayoutData(gridData);
			
			diagnosis=new Diagnosis(tabFolderDetector);
			modeling =new Modeling(tabFolderDetector);	
		
			
			tabFolderDetector.setFocus();
		
	   } catch (Exception ex) {
			
		   MessageBox msg=new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),SWT.ICON_ERROR);
		   if (ex.getMessage()!=null){
		      msg.setMessage(ex.getMessage());
		      msg.open();
		   }
		   ex.printStackTrace();
		}

	}
	/*
	 	 
	private void leftPane(Composite parent){
			
		Composite compLeftPane=new Composite(parent,SWT.BORDER);
		GridData gridData=new GridData(SWT.LEFT, SWT.FILL, false, true);
		gridData.horizontalSpan=1;
		compLeftPane.setLayoutData(gridData);
		compLeftPane.setLayout(new GridLayout(1,false));
		
		SystemController sysController=new SystemController(compLeftPane);
		TracingTypeSelector traceTypeSelector=new TracingTypeSelector(compLeftPane);
		
		
	}
*/
	/**
	 * 
	 * @param traceBuffer
	 * @param tracePath
	 * @param traceTypeName
	 */
	public void notifyOnTraceSelection(String tracePath, String traceTypeName){
		diagnosis.updateOnTraceSelection(tracePath, traceTypeName);
	}

	   
// For testing
/*
  public static void main(String[] args) {
	        Display display = new Display();
	        org.eclipse.swt.widgets.Shell shell= new org.eclipse.swt.widgets.Shell(display);
	        //shell.setText("Center");
	       // shell.setSize(250, 200);
	       // shell.setLayout(new GridLayout(3,false));
	       //center(shell);
	        TotalADS det=new TotalADS(shell, SWT.BORDER);
	      
	        /// centre
	        org.eclipse.swt.graphics.Rectangle bds = shell.getDisplay().getBounds();

	        org.eclipse.swt.graphics.Point p = shell.getSize();

	        int nLeft = (bds.width - p.x) / 2;
	        int nTop = (bds.height - p.y) / 2;

	        shell.setBounds(nLeft, nTop, p.x, p.y);
	        
	        
	        //det.pack();
	        shell.pack();
	        shell.open();
	        

	        while (!shell.isDisposed()) {
	          if (!display.readAndDispatch()) {
	            display.sleep();
	          }
	        }
	        
	        display.dispose();
	    }*/
}
