package org.eclipse.linuxtools.tmf.totalids.ui;

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
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.TableItem;

public class compDetective extends Composite {
	private Table tblDetailsAnomHist;
	private Text txtDetailsIdentify;
	private Text txtDetailsAnomalyDetails;
	private Text txtEnterRegularExpression;
	private Text lblAnomaliesTrain;
	private Text lblAnomaliesValidate;
	private Text lblAnomaliesTest;
	private Text txtAnomaliesProgress;
	private Table tableClassificationPredictions;
	private Text text;
	private Text text_1;
	private Table table;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public compDetective(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		CTabFolder tabFolderDetector = new CTabFolder(this, SWT.BORDER);
		tabFolderDetector.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmDetails = new CTabItem(tabFolderDetector, SWT.NONE);
		tbtmDetails.setText("Details");
		
		Composite comptbtmDetails = new Composite(tabFolderDetector, SWT.NONE);
		tbtmDetails.setControl(comptbtmDetails);
		comptbtmDetails.setLayout(null);
		
		Tree treeDetailsModels = new Tree(comptbtmDetails, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION| SWT.V_SCROLL | SWT.H_SCROLL);
		treeDetailsModels.setLinesVisible(true);		
		treeDetailsModels.setBounds(20, 75, 203, 146);
		///////data
		TreeItem item1 = new TreeItem(treeDetailsModels, SWT.NONE);
	    item1.setText("KSM (alpha(0.04)");
	     TreeItem item2 = new TreeItem(treeDetailsModels, SWT.NONE);
	    item2.setText("Sliding Window (width=5)");
	    TreeItem item3 = new TreeItem(treeDetailsModels, SWT.NONE);
	    item3.setText("HMM (states=10)");
		
		tblDetailsAnomHist = new Table(comptbtmDetails, SWT.BORDER | SWT.FULL_SELECTION);
		tblDetailsAnomHist.setBounds(248, 75, 240, 146);
		tblDetailsAnomHist.setHeaderVisible(true);
		tblDetailsAnomHist.setLinesVisible(true);
		
		TableColumn tblclmnDetailsTime = new TableColumn(tblDetailsAnomHist, SWT.NONE);
		tblclmnDetailsTime.setWidth(92);
		tblclmnDetailsTime.setText("Time");
		
		TableColumn tblclmnDetailsTraceId = new TableColumn(tblDetailsAnomHist, SWT.NONE);
		tblclmnDetailsTraceId.setWidth(100);
		tblclmnDetailsTraceId.setText("Trace ID");
		
		TableItem tableItem2DetailsAnomHistory = new TableItem(tblDetailsAnomHist, SWT.NONE);
		tableItem2DetailsAnomHistory.setText(new String[] {"08-12-2013: 3:45", "kernel--session-12-2013"});
		
		TableItem tableItemDetailsAnomHistory = new TableItem(tblDetailsAnomHist, SWT.NONE);
		tableItemDetailsAnomHistory.setText(new String[] {"07-12-2013: 2:40", "kernmel-session-01-03"});
		
		Group grpDetailsIdentify = new Group(comptbtmDetails, SWT.NONE);
		grpDetailsIdentify.setBounds(115, 233, 233, 95);
		grpDetailsIdentify.setText("Identify Anomaly");
		
		Combo comboDetailsIdentify = new Combo(grpDetailsIdentify, SWT.NONE);
		comboDetailsIdentify.setItems(new String[] {"Yes", "No", "Other"});
		comboDetailsIdentify.setBounds(10, 27, 75, 29);
		comboDetailsIdentify.select(0);
		
		txtDetailsIdentify = new Text(grpDetailsIdentify, SWT.BORDER);
		txtDetailsIdentify.setEnabled(false);
		txtDetailsIdentify.setText("Enter other type");
		txtDetailsIdentify.setBounds(103, 27, 120, 27);
		
		Button btnSubmit = new Button(grpDetailsIdentify, SWT.NONE);
		btnSubmit.setBounds(75, 62, 91, 29);
		btnSubmit.setText("Submit");
		
		CLabel lblDetailsSelectedTrace = new CLabel(comptbtmDetails, SWT.NONE);
		lblDetailsSelectedTrace.setBounds(29, 10, 319, 23);
		lblDetailsSelectedTrace.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblDetailsSelectedTrace.setText("Selected Trace:  kernel-session-12-2013");
		
			
		CLabel lblDetailsChart = new CLabel(comptbtmDetails, SWT.NONE);
		lblDetailsChart.setBounds(494, 141, 296, 207);
		lblDetailsChart.setImage(SWTResourceManager.getImage("/home/umroot/experiments/workspace/tmf-ads/"
				+ "org.eclipse.linuxtools/lttng/org.eclipse.linuxtools.tmf.totalids.ui/icons/java-twiki-metrpreter2.png"));
		lblDetailsChart.setText("");
		
		txtDetailsAnomalyDetails = new Text(comptbtmDetails, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		txtDetailsAnomalyDetails.setText("\"FS\" : 0.53\n \"MM\" : 0.12\n \"KL\" : 0.18\n \"AC\" : 0.01 \n\"IPC\" : 0\n \"NT\" : 0.01\n \"SC\" : 0\n \"UN\" : 0.18");
		txtDetailsAnomalyDetails.setBounds(524, 29, 248, 102);
		
		CLabel lblDetailsAnomaly = new CLabel(comptbtmDetails, SWT.NONE);
		lblDetailsAnomaly.setBounds(513, 0, 212, 23);
		lblDetailsAnomaly.setText("Anomaly Details");
		lblDetailsAnomaly.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		
		CLabel labalDetailsStatus = new CLabel(comptbtmDetails, SWT.NONE);
		labalDetailsStatus.setText("Tracing Mode: LTTng-kernel");
		labalDetailsStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		labalDetailsStatus.setBounds(20, 334, 186, 23);
		
		CLabel lblSoftwareSystemHostapp = new CLabel(comptbtmDetails, SWT.NONE);
		lblSoftwareSystemHostapp.setText("Software System: Host-app-01");
		lblSoftwareSystemHostapp.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblSoftwareSystemHostapp.setBounds(20, 363, 233, 23);
		
		CLabel lblDetailsModels = new CLabel(comptbtmDetails, SWT.NONE);
		lblDetailsModels.setText("Models");
		lblDetailsModels.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblDetailsModels.setBounds(20, 46, 203, 23);
		
		CLabel lblDetailsTraceInfo = new CLabel(comptbtmDetails, SWT.NONE);
		lblDetailsTraceInfo.setText("Trace Info");
		lblDetailsTraceInfo.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblDetailsTraceInfo.setBounds(248, 46, 203, 23);
		
		CTabItem tbtmAnomalies = new CTabItem(tabFolderDetector, SWT.NONE);
		tbtmAnomalies.setText("Anomalies");
		
		Composite comptbtmAnomalies = new Composite(tabFolderDetector, SWT.NONE);
		tbtmAnomalies.setControl(comptbtmAnomalies);
		
		Group grpTraceType = new Group(comptbtmAnomalies, SWT.NONE);
		grpTraceType.setText("Tracing Mode");
		grpTraceType.setBounds(21, 10, 627, 55);
		
		Button btnAnomaliesLttngkernel = new Button(grpTraceType, SWT.CHECK);
		btnAnomaliesLttngkernel.setBounds(30, 20, 115, 24);
		btnAnomaliesLttngkernel.setText("LTTng-kernel");
		
		Button btnAnomaliesLttngust = new Button(grpTraceType, SWT.CHECK);
		btnAnomaliesLttngust.setBounds(162, 20, 115, 24);
		btnAnomaliesLttngust.setText("LTTng-UST");
		
		Button btnAnomaliesText = new Button(grpTraceType, SWT.CHECK);
		btnAnomaliesText.setBounds(283, 20, 67, 24);
		btnAnomaliesText.setText("Text");
		
		txtEnterRegularExpression = new Text(grpTraceType, SWT.BORDER);
		txtEnterRegularExpression.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		txtEnterRegularExpression.setText("Enter Regular Expression");
		txtEnterRegularExpression.setBounds(356, 20, 245, 27);
		
		Tree treeAnomaliesModels = new Tree(comptbtmAnomalies, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI);
		treeAnomaliesModels.setLinesVisible(true);
		treeAnomaliesModels.setBounds(21, 174, 194, 209);
		///////data
		TreeItem item1Anom = new TreeItem(treeAnomaliesModels, SWT.NONE);
	    item1Anom.setText("KSM");
	     TreeItem item2Anom = new TreeItem(treeAnomaliesModels, SWT.NONE);
	    item2Anom.setText("Sliding Window");
	    TreeItem item3Anom = new TreeItem(treeAnomaliesModels, SWT.NONE);
	    item3Anom.setText("HMM");
		
		CLabel lblAnomaliesModels = new CLabel(comptbtmAnomalies, SWT.NONE);
		lblAnomaliesModels.setText("Select Models");
		lblAnomaliesModels.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblAnomaliesModels.setBounds(21, 140, 151, 23);
		
		lblAnomaliesTrain = new Text(comptbtmAnomalies, SWT.BORDER);
		lblAnomaliesTrain.setBounds(21, 72, 192, 27);
		
		lblAnomaliesValidate = new Text(comptbtmAnomalies, SWT.BORDER);
		lblAnomaliesValidate.setBounds(262, 72, 183, 27);
		
		lblAnomaliesTest = new Text(comptbtmAnomalies, SWT.BORDER);
		lblAnomaliesTest.setBounds(465, 71, 183, 27);
		
		Button btnAnomaliesTrain = new Button(comptbtmAnomalies, SWT.NONE);
		btnAnomaliesTrain.setBounds(128, 105, 91, 29);
		btnAnomaliesTrain.setText("Train");
		
		Button btnAnomaliesValidate = new Button(comptbtmAnomalies, SWT.NONE);
		btnAnomaliesValidate.setBounds(355, 105, 91, 29);
		btnAnomaliesValidate.setText("Validate");
		
		Button btnAnomaliesTest = new Button(comptbtmAnomalies, SWT.NONE);
		btnAnomaliesTest.setBounds(557, 105, 91, 29);
		btnAnomaliesTest.setText("Test");
		
		CLabel lblAnomaliesProgress = new CLabel(comptbtmAnomalies, SWT.NONE);
		lblAnomaliesProgress.setText("Progress Console");
		lblAnomaliesProgress.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblAnomaliesProgress.setBounds(263, 142, 151, 23);
		
		txtAnomaliesProgress = new Text(comptbtmAnomalies, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		txtAnomaliesProgress.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		txtAnomaliesProgress.setText("Reading Trace Kernel-session-27-13\nTransforming to states\nInserting into the database host-app-01\n.....................\n");
		txtAnomaliesProgress.setBounds(262, 174, 386, 209);
		
		Tree treeAnomaliesSystems = new Tree(comptbtmAnomalies, SWT.BORDER | SWT.CHECK);
		treeAnomaliesSystems.setBounds(667, 43, 123, 340);
		///////data
		TreeItem item1AnomS = new TreeItem(treeAnomaliesSystems, SWT.NONE);
	    item1AnomS.setText("Host-app-01");
		TreeItem item2AnomS = new TreeItem(treeAnomaliesSystems, SWT.NONE);
		item2AnomS.setText("Android-01s");
		TreeItem item3AnomS = new TreeItem(treeAnomaliesSystems, SWT.NONE);
		item3AnomS.setText("Host-Sys-01");
	
		
		CLabel lblSelectSystem = new CLabel(comptbtmAnomalies, SWT.NONE);
		lblSelectSystem.setText("System");
		lblSelectSystem.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblSelectSystem.setBounds(667, 10, 59, 27);
		
		Button btnAnomaliesAddSystem = new Button(comptbtmAnomalies, SWT.NONE);
		btnAnomaliesAddSystem.setBounds(731, 10, 59, 29);
		btnAnomaliesAddSystem.setText("Add");
		
		CTabItem tbtmClassification = new CTabItem(tabFolderDetector, SWT.NONE);
		tbtmClassification.setText("Classification");
		
		Composite comptbtmClassification = new Composite(tabFolderDetector, SWT.NONE);
		tbtmClassification.setControl(comptbtmClassification);
		
		tableClassificationPredictions = new Table(comptbtmClassification, SWT.BORDER | SWT.FULL_SELECTION);
		tableClassificationPredictions.setLinesVisible(true);
		tableClassificationPredictions.setHeaderVisible(true);
		tableClassificationPredictions.setBounds(314, 130, 294, 241);
		
		TableColumn tblclmnClassificationProbability = new TableColumn(tableClassificationPredictions, SWT.NONE);
		tblclmnClassificationProbability.setWidth(92);
		tblclmnClassificationProbability.setText("Probability");
		
		TableColumn tblclmnClassificationType = new TableColumn(tableClassificationPredictions, SWT.NONE);
		tblclmnClassificationType.setWidth(100);
		tblclmnClassificationType.setText("Type");
		
		TableItem tableItem = new TableItem(tableClassificationPredictions, SWT.NONE);
		tableItem.setText(new String[] {"0.95", "Function-foo"});
		
		TableItem tableItem_1 = new TableItem(tableClassificationPredictions, SWT.NONE);
		tableItem_1.setText(new String[] {"0.30", "Function-foo2"});
		
		TableItem tableItem_2 = new TableItem(tableClassificationPredictions, SWT.NONE);
		tableItem_2.setText(new String[] {"0.05", "function-foo10"});
		
		TableItem tableItem_3 = new TableItem(tableClassificationPredictions, SWT.NONE);
		tableItem_3.setText(new String[] {"0.05", "Function-foo222"});
		
		Group group = new Group(comptbtmClassification, SWT.NONE);
		group.setText("Tracing Mode");
		group.setBounds(10, 10, 627, 55);
		
		Button button = new Button(group, SWT.CHECK);
		button.setText("LTTng-kernel");
		button.setBounds(30, 20, 115, 24);
		
		Button button_1 = new Button(group, SWT.CHECK);
		button_1.setText("LTTng-UST");
		button_1.setBounds(162, 20, 115, 24);
		
		Button button_2 = new Button(group, SWT.CHECK);
		button_2.setText("Text");
		button_2.setBounds(283, 20, 67, 24);
		
		text = new Text(group, SWT.BORDER);
		text.setText("Enter Regular Expression");
		text.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		text.setBounds(356, 20, 245, 27);
		
		Button btnTrainClassifier = new Button(comptbtmClassification, SWT.NONE);
		btnTrainClassifier.setText("Train Classifier");
		btnTrainClassifier.setBounds(643, 22, 142, 37);
		
		Button button_4 = new Button(comptbtmClassification, SWT.NONE);
		button_4.setText("Test");
		button_4.setBounds(190, 71, 91, 29);
		
		text_1 = new Text(comptbtmClassification, SWT.BORDER);
		text_1.setBounds(20, 71, 164, 29);
		
		table = new Table(comptbtmClassification, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(20, 135, 261, 233);
		
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(119);
		tableColumn.setText("Time");
		
		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(100);
		tableColumn_1.setText("Trace ID");
		
		TableItem tableItem_4 = new TableItem(table, SWT.NONE);
		tableItem_4.setText(new String[] {"10-11-2013: 20:08", "ust-android-01"});
		
		TableItem tableItem_5 = new TableItem(table, SWT.NONE);
		tableItem_5.setText(new String[] {"9-12-2013: 22:04", "ust-m04-00"});
		
		TableItem tableItem_6 = new TableItem(table, SWT.NONE);
		tableItem_6.setText(new String[] {"05-12-2013: 22:04", "ust-ubuntu-00"});
		
		CLabel lblTraceInfo = new CLabel(comptbtmClassification, SWT.NONE);
		lblTraceInfo.setText("Select Traces");
		lblTraceInfo.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblTraceInfo.setBounds(30, 106, 203, 23);
		
		CLabel lblTraceClassification = new CLabel(comptbtmClassification, SWT.NONE);
		lblTraceClassification.setText("Trace Classification");
		lblTraceClassification.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblTraceClassification.setBounds(314, 101, 203, 23);
		
		Tree treeClassificationSystem = new Tree(comptbtmClassification, SWT.BORDER | SWT.CHECK);
		treeClassificationSystem.setBounds(643, 130, 123, 241);
	///////data
			TreeItem item1Classf = new TreeItem(treeClassificationSystem, SWT.NONE);
		    item1Classf.setText("Ust-app-01");
			TreeItem item2Classf = new TreeItem(treeClassificationSystem, SWT.NONE);
			item2Classf.setText("Android-01s");
			TreeItem item3Classf = new TreeItem(treeClassificationSystem, SWT.NONE);
			item3Classf.setText("Ubuntu-UST-01");
		
		
		CLabel lblSoftwareSystem = new CLabel(comptbtmClassification, SWT.NONE);
		lblSoftwareSystem.setText("Software System");
		lblSoftwareSystem.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblSoftwareSystem.setBounds(643, 101, 123, 23);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	public void SWTApp(Display display) {
	       
	        
	    }


	   


	    public static void main(String[] args) {
	        Display display = new Display();
	        org.eclipse.swt.widgets.Shell shell = new org.eclipse.swt.widgets.Shell(display);
	        shell.setText("Center");
	        shell.setSize(250, 200);

	       //center(shell);
	        compDetective det=new compDetective(shell, SWT.BORDER);
	      
	        /// centre
	        org.eclipse.swt.graphics.Rectangle bds = shell.getDisplay().getBounds();

	        org.eclipse.swt.graphics.Point p = shell.getSize();

	        int nLeft = (bds.width - p.x) / 2;
	        int nTop = (bds.height - p.y) / 2;

	        shell.setBounds(nLeft, nTop, p.x, p.y);
	        
	        
	        det.pack();
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
