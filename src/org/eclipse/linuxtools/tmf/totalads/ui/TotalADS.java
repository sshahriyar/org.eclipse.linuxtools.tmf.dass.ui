package org.eclipse.linuxtools.tmf.totalads.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.layout.GridData;

import swing2swt.layout.FlowLayout;
import swing2swt.layout.BoxLayout;

import org.eclipse.swt.custom.StackLayout;

import swing2swt.layout.BorderLayout;

import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.wb.swt.SWTResourceManager;
//import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.custom.ScrolledComposite;

//public class CompDetective extends Composite {
public class TotalADS  {
	private Table tblAnalysisTraceList;
	private Text txtAnalysisIdentify;
	private Text txtAnalysisDetails;
	private Text txtTraceTypeRegularExpression;
	private Text lblAnomaliesTrain;
	private Text lblAnomaliesValidate;
	private Text lblAnomaliesTest;
	private Text txtAnomaliesProgress;
	private Table tableClassificationPredictions;
	
	
	
	//GridData gridDataFullFill=new GridData(SWT.FILL, SWT.FILL, true, true );
	//GridData gridDataHorizontalFill=new GridData(SWT.FILL, SWT.TOP, true, false );
	//GridData gridDataVerticalFill=new GridData(SWT.TOP, SWT.FILL, false, true );
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TotalADS(Composite parent, int style) {
	
		ModelTypeFactory mod= ModelTypeFactory.getInstance();
		mod.initialize();
		
		//super(parent, style);
		parent.setLayout(new GridLayout(2,false));
		
		leftPane(parent);
		
		CTabFolder tabFolderDetector = new CTabFolder(parent, SWT.BORDER);
		tabFolderDetector.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan=1;
		tabFolderDetector.setLayoutData(gridData);
		
		Diagnosis diagnosis=new Diagnosis(tabFolderDetector);
		Modeling modeling =new Modeling(tabFolderDetector);	
	
		
		tabFolderDetector.setFocus();


	}
	/**
	 * 
	 * @param parent
	 */
	private void leftPane(Composite parent){
			
		Composite compLeftPane=new Composite(parent,SWT.BORDER);
		GridData gridData=new GridData(SWT.LEFT, SWT.FILL, false, true);
		gridData.horizontalSpan=1;
		compLeftPane.setLayoutData(gridData);
		compLeftPane.setLayout(new GridLayout(1,false));
		
		SystemController sysController=new SystemController(compLeftPane);
		TracingTypeSelector traceTypeSelector=new TracingTypeSelector(compLeftPane);
		
		
	}

	
	//@Override
//	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	//}
	
	//public void SWTApp(Display display) {
	       
	        
	  //  }


	   


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
	    }
}
