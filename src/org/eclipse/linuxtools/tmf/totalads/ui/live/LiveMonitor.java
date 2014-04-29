/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.live;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.linuxtools.tmf.totalads.ui.FileBrowser;
//import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
//import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
//import org.eclipse.linuxtools.tmf.totalads.core.TMFTotalADSView;
//import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSNetException;
//import org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders.CTFLTTngSysCallTraceReader;
import org.eclipse.linuxtools.tmf.totalads.ui.ProgressConsole;
//import org.eclipse.linuxtools.tmf.totalads.ui.TotalADS;
import org.eclipse.linuxtools.tmf.totalads.ui.DirectoryBrowser;
//import org.eclipse.linuxtools.tmf.totalads.ui.TracingTypeSelector;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.ModelLoader;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.ResultsAndFeedback;
//import org.eclipse.linuxtools.tmf.totalads.ui.modeling.StatusBar;
import org.eclipse.linuxtools.tmf.totalads.ui.utilities.SWTResourceManager;
//import org.eclipse.osgi.framework.internal.core.Msg;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
//import org.eclipse.swt.events.SelectionAdapter;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
//import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
//import org.eclipse.swt.widgets.ProgressBar;
//import org.eclipse.swt.widgets.TabItem;
//import org.eclipse.swt.widgets.Table;
//import org.eclipse.swt.widgets.TableColumn;
//import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

/**
 * This class creates the GUI elements/widgets for live diagnosis using ssh. Any host can be selected and LTTNg tracing
 * can be started on that system and one of the eqarlier model can be used to test the live tracing
 * 
 * 
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class LiveMonitor {
	//Initializes variables
	//private TracingTypeSelector traceTypeSelector;
	private Text txtPassword;
	private Text txtUserAtHost;
	private Combo cmbSnapshot;
	private Combo cmbInterval;
	private Text txtPvtKey;
	private Button btnPvtKey;
	private Button btnPassword;
	private Text txtPort;
	private Text txtSudoPassword;
	private ResultsAndFeedback resultsAndFeedback;
	private Button btnStart;
	private ProgressConsole console;
	private Button btnStop;
	private Button btnDetails;
	private BackgroundLiveMonitor liveExecutor;
	private ModelSelection modelSelectionHandler;
	private LiveXYChart liveChart;
	private Button btnTrainingAndEval;
	private Button btnTesting;
	private FileBrowser trcbrowser;
	/**
	 * Constructor of the LiveMonitor class
	 * @param tabFolderParent TabFolder object
	 *
	 */
	public LiveMonitor(CTabFolder tabFolderParent){
		//tmfTracePath=new StringBuilder();
		//currentlySelectedTracesPath=new StringBuilder();
		//LiveMonitor Tab Item
		CTabItem tbItmLive = new CTabItem(tabFolderParent, SWT.NONE);
		tbItmLive.setText("Live Monitor");
		//Making scrollable tab item 
		ScrolledComposite scrolCompAnom=new ScrolledComposite(tabFolderParent, SWT.H_SCROLL | SWT.V_SCROLL);
		Composite comptbItmLive = new Composite(scrolCompAnom,SWT.NONE);
		tbItmLive.setControl(scrolCompAnom);
		
		//Designing the Layout of the GUI Items  for the LiveMonitor Tab Item
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan=1;
		comptbItmLive.setLayoutData(gridData);
		comptbItmLive.setLayout(new GridLayout(2, false));
	
		///////////////////////////////////////////////////////////////////////////
		//Creating GUI widgets for selection of a trace type and a selection of the model
		///////////////////////////////////////////////////////////////////
		Composite compTraceTypeAndModel=new Composite(comptbItmLive, SWT.NONE);
		compTraceTypeAndModel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		compTraceTypeAndModel.setLayout(new GridLayout(1, false));
		
	
		// Create GUI elements for a selection of a trace
		
			
		//////////////////////////////////////////////////////////////////////
		// Creating GUI widgets for charts and console
		//////////////////////////////////////////////////////////////////
		Composite compButtonsChartConsole=new Composite(comptbItmLive, SWT.NONE);
		compButtonsChartConsole.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		compButtonsChartConsole.setLayout(new GridLayout(1, false));
		
		Composite compButtons=new Composite(compTraceTypeAndModel, SWT.NONE);
		compButtons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		compButtons.setLayout(new GridLayout(5, false));
		
		btnStart=new Button(compButtons, SWT.BORDER);
		btnStart.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,1,1));
		btnStart.setText("Start");
		
		
		btnStop=new Button(compButtons, SWT.BORDER);
		btnStop.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,1,1));
		btnStop.setText("Stop");
		btnStop.setEnabled(false);
		
		btnDetails=new Button(compButtons, SWT.BORDER);
		btnDetails.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,1,1));
		btnDetails.setText("Details");
		btnDetails.setEnabled(false);
		

		selectHostUsingSSH(compTraceTypeAndModel);
		///////////////
		///Creating a chart
		///////////////
		;
		
		Composite compChart = new Composite(compButtonsChartConsole,SWT.NONE);
		compChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		compChart.setLayout(new FillLayout());
		liveChart=new LiveXYChart(compChart);
		
		/////////
		///Creating a smaller console than the chart
		///////
		Composite compConsole = new Composite(compButtonsChartConsole,SWT.NONE);
		compConsole.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		compConsole.setLayout(new GridLayout(1,false));
		
		GridData gridDataConsoleText=new GridData(SWT.FILL,SWT.FILL,true,true);
		gridDataConsoleText.minimumHeight=160;
		console =new ProgressConsole (compConsole,new GridData(SWT.LEFT,SWT.BOTTOM,true,false),
				gridDataConsoleText);

		
		resultsAndFeedback=new ResultsAndFeedback();
		//Adjust settings for scrollable LiveMonitor Tab Item
		scrolCompAnom.setContent(comptbItmLive);
		 // Set the minimum size
		scrolCompAnom.setMinSize(500, 500);
	    // Expand both horizontally and vertically
		scrolCompAnom.setExpandHorizontal(true);
		scrolCompAnom.setExpandVertical(true);
		addHandlers();
	}

	
	
	/**
	 * Creates GUI widgets for a selection of traces and trace types
	 * @param compDiagnosis Composite of LiveMonitor
	 */
	private void selectHostUsingSSH(Composite compDiagnosis){
		/**
		 *  Group trace selection
		 */
		Group grpSSHConfig = new Group(compDiagnosis, SWT.NONE);
		grpSSHConfig.setText("Select Configuration");
		
		grpSSHConfig.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false));
		grpSSHConfig.setLayout(new GridLayout(3,false));
		
		Label userAtHost= new Label(grpSSHConfig, SWT.NONE);
		userAtHost.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false,1,1));
		userAtHost.setText("Enter username@hostname   ");
		
		Label lblSudoPassword= new Label(grpSSHConfig, SWT.NONE);
		lblSudoPassword.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false,1,1));
		lblSudoPassword.setText("Enter Password ");
		
		Label lblPort= new Label(grpSSHConfig, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,1,1));
		lblPort.setText("Enter Port");
			
		txtUserAtHost=new Text(grpSSHConfig, SWT.BORDER);
		txtUserAtHost.setEnabled(true);
		txtUserAtHost.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false,false));
		txtUserAtHost.setText(System.getProperty("user.name")+"@localhost");
		txtUserAtHost.setText("shary@172.30.103.143");
		
		txtSudoPassword=new Text(grpSSHConfig,SWT.BORDER|SWT.PASSWORD);
		txtSudoPassword.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,1,1));
		txtSudoPassword.setText("grt_654321");
		
		txtPort=new Text(grpSSHConfig,SWT.BORDER);
		txtPort.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,1,1));
		txtPort.setText("22");
		/////////
		///SSH Password and Private Key: Currently disabling this to provide it in the next version
		/////////
		/*Group grpPrivacy = new Group(grpSSHConfig, SWT.NONE);
		grpPrivacy.setText("SSH Password/Pvt. Key");
		
		grpPrivacy.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,3,2));
		grpPrivacy.setLayout(new GridLayout(3,false));
				
		btnPassword = new Button(grpPrivacy, SWT.RADIO);
		btnPassword.setText("Enter Password");
		btnPassword.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true, false,1,1));
		btnPassword.setSelection(true);
		
		btnPvtKey = new Button(grpPrivacy, SWT.RADIO);
		btnPvtKey.setText("Select Private Key");
		btnPvtKey.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true, false,2,1));
		
		
		txtPassword=new Text(grpPrivacy,SWT.BORDER|SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		txtPassword.setText("grt_654321");
		
		txtPvtKey=new Text(grpPrivacy,SWT.BORDER);
		txtPvtKey.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		//txtPvtKey.setText("");
		txtPvtKey.setEnabled(false);
		
		trcbrowser=new FileBrowser(grpPrivacy, txtPvtKey, new GridData(SWT.RIGHT, SWT.TOP, false, false));
		trcbrowser.disableBrowsing();
		*/
		//////////
		///End SSH password and private key
		//////
		///////////////
		// Duration and Port
		//////////
		Composite compDurationPort=new Composite(grpSSHConfig, SWT.NONE);
		compDurationPort.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,3,2));
		compDurationPort.setLayout(new GridLayout(2,false));
		
		Label lblSnapshotDuration= new Label(compDurationPort, SWT.NONE);
		lblSnapshotDuration.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false,1,1));
		lblSnapshotDuration.setText("Snapshot Duration (secs)");
		
		Label lblIntervalDuration= new Label(compDurationPort, SWT.NONE);
		lblIntervalDuration.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false,1,1));
		lblIntervalDuration.setText("Snapshots Interval (mins)");
		
		
		cmbSnapshot=new Combo(compDurationPort, SWT.NONE| SWT.READ_ONLY);
		cmbSnapshot.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false,1,1));
		cmbSnapshot.add("5"); 
		cmbSnapshot.add("10"); 
		cmbSnapshot.add("15");
		cmbSnapshot.add("20");
		cmbSnapshot.add("35");
		cmbSnapshot.add("60");
		cmbSnapshot.select(0);
		
		cmbInterval=new Combo(compDurationPort, SWT.NONE|SWT.READ_ONLY);
		cmbInterval.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false,1,1));
		cmbInterval.add("1");
		cmbInterval.add("3"); 
		cmbInterval.add("5"); 
		cmbInterval.add("7");
		cmbInterval.add("10");
		cmbInterval.add("15");
		cmbInterval.add("20");
		cmbInterval.add("30");
		cmbInterval.select(0);
		
		trainingAndEvaluation(compDiagnosis);
		
		/**
		 * End group trace selection
		 */
	}
	
	/**
	 * Training and Evaluation Widgets
	 */
	public void trainingAndEvaluation(Composite compParent){
		/////////
		///Training and Evaluation
		/////////
		Group grpTrainingAndEval = new Group(compParent, SWT.NONE);
		grpTrainingAndEval.setText("Model Selection");
		grpTrainingAndEval.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true));
		grpTrainingAndEval.setLayout(new GridLayout(2,false));
		
		btnTrainingAndEval=new Button(grpTrainingAndEval, SWT.NONE|SWT.RADIO);
		btnTrainingAndEval.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,1,1));
		btnTrainingAndEval.setText("Training and Testing");
		
		btnTesting=new Button(grpTrainingAndEval, SWT.NONE|SWT.RADIO);
		btnTesting.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,1,1));
		btnTesting.setText("Testing");
		btnTesting.setSelection(true);
		
		//////////////////
		/////// Existing Model
		//////////////
		Group grpModelSelection=new Group(grpTrainingAndEval,SWT.NONE);	
		grpModelSelection.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,2,2));
		grpModelSelection.setLayout(new GridLayout(1,false));
		grpModelSelection.setText("Existing Models");
		
		Composite compModelSelection=new Composite(grpModelSelection, SWT.NONE);
	    compModelSelection.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false));
		compModelSelection.setLayout(new GridLayout(2,false));
	    	
		   
		Button btnSettings=new Button(compModelSelection, SWT.NONE);
		btnSettings.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false,false,1,1));
		btnSettings.setText(" Settings ");
		
		Button btnDelete=new Button(compModelSelection, SWT.NONE);
		btnDelete.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false,false,1,1));
		btnDelete.setText("   Delete   ");
		
		Tree treeModels = new Tree(grpModelSelection, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION| SWT.V_SCROLL | SWT.H_SCROLL);
		treeModels.setLinesVisible(true);
		treeModels.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		modelSelectionHandler=new ModelSelection(btnSettings, btnDelete, treeModels);
		
	}
	
	/**
	 * Handlers
	 */
	private void addHandlers(){
		/**
		 * 
		 */
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				
				if (findInvalidSSettings()==false){
					resultsAndFeedback.clearData();
					btnDetails.setEnabled(false);
					
					String password=""; 
					String privateKey="";
					Boolean isTrainAndEval=false;
					
					int port=Integer.parseInt(txtPort.getText());
					int snapshotDuration=Integer.parseInt(cmbSnapshot.getItem(cmbSnapshot.getSelectionIndex()));
					int snapshotIntervals=Integer.parseInt(cmbInterval.getItem(cmbInterval.getSelectionIndex()));
					
					
				 /*// Will be enabled in the next version
					if (btnPassword.getSelection())
						password=txtPassword.getText();
					else if (btnPvtKey.getSelection())
						privateKey=txtPvtKey.getText();
					*/
					//so doing the followign for this version
					password=txtSudoPassword.getText();
					
					if (btnTrainingAndEval.getSelection())
						isTrainAndEval=true;
					else if (btnTesting.getSelection())
						isTrainAndEval=false;
							
					btnStart.setEnabled(false);
					btnStop.setEnabled(true);
					//IDetectionAlgorithm algorithms[]=modelSelectionHandler.getCurrentlySelectedAlgorithms();
					HashMap<String,String[]> modelsAndSettings=modelSelectionHandler.getModelaAndSettings();
					
					if (modelsAndSettings.size() > 5){
						 MessageBox msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,SWT.ICON_ERROR|SWT.OK);
						 msgBox.setMessage("Please select less than six models");
						 msgBox.open();
						 return;
					}

					liveExecutor= new BackgroundLiveMonitor
							  (txtUserAtHost.getText(), password, txtSudoPassword.getText(), 
									  privateKey, port,snapshotDuration,snapshotIntervals, btnStart,
									  	btnStop, btnDetails,modelsAndSettings,resultsAndFeedback,liveChart,
									  	isTrainAndEval, console);
					 ExecutorService executor = Executors.newSingleThreadExecutor();
					 executor.execute(liveExecutor);
					 executor.shutdown();
				}

			}
		
		});
		/**
		 * stop event handler
		 */
		btnStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				liveExecutor.stopMonitoring();
			}
		});
		/**
		 * details event handler
		 */
		btnDetails.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				resultsAndFeedback.showForm();	
			}
		});
		
		//**** Pvt key button handler: Will be enabled in the next version
		/*btnPvtKey.addSelectionListener(new SelectionAdapter() {
					
			@Override
			public void widgetSelected(SelectionEvent e) {
				trcbrowser.enableBrowsing();
				txtPvtKey.setEnabled(true);
				txtPassword.setEnabled(false);
				txtPassword.setText("");
			}
			});*/
		
		//**** Password button key handler
		/* btnPassword.addSelectionListener(new SelectionAdapter() {
							
					@Override
					public void widgetSelected(SelectionEvent e) {
						
						txtPvtKey.setEnabled(false);
						txtPvtKey.setText("");
						trcbrowser.disableBrowsing();
						txtPassword.setEnabled(true);
					}
			});*/
		
	}
	/**
	 * Validates the fields before execution
	 * @return
	 */
	private Boolean findInvalidSSettings(){
		
		String msg="";
		if (txtUserAtHost.getText().isEmpty())
				msg="User@Host cannot be empty";
		else if (txtSudoPassword.getText().isEmpty())
			msg="Sudo password cannot be empty";
		else if (txtPort.getText().isEmpty())
			msg="Port cannot be empty";
	//	else if (btnPassword.getSelection() && txtPassword.getText().isEmpty())
		//	msg="SSH password cannot be empty";
		//else if (btnPvtKey.getSelection() && txtPvtKey.getText().isEmpty())
         //  msg="Private key path cannot be empty";
		else if (this.modelSelectionHandler.getSelectedModelsCount()==0)
			msg="Please, first select a model";
		else {
				try{ 
					Integer.parseInt(txtPort.getText());
				} catch (Exception ex){
					msg="Port number can only be a number";
				}
			}
		
		if (!msg.isEmpty()){
			 MessageBox msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,SWT.ICON_ERROR|SWT.OK);
			 msgBox.setMessage(msg);
			 msgBox.open();
			 return true;
		}else
			 return false;
	}
	
	/**
	 * Closes the results form if open
	 */
	public void destroy (){
		this.resultsAndFeedback.destroy();
	}
	
}