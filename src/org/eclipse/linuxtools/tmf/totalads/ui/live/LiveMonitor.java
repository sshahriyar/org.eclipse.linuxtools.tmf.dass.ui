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


import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.linuxtools.tmf.totalads.ui.results.ResultsAndFeedback;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;


/**
 * This class creates the GUI elements/widgets for live diagnosis using ssh. Any host can be selected and LTTNg tracing
 * can be started on that system and one of the eqarlier model can be used to test the live tracing
 * 
 * 
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class LiveMonitor {
	
	private Text txtUserAtHost;
	private Combo cmbSnapshot;
	private Combo cmbInterval;
	private Text txtPort;
	private Text txtSudoPassword;
	private ResultsAndFeedback resultsAndFeedback;
	private Button btnStart;
	private Button btnStop;
	//private Button btnDetails;
	private BackgroundLiveMonitor liveExecutor;
	private LiveXYChart liveChart;
	private Button btnTrainingAndEval;
	private Button btnTesting;
	private HashSet<String> modelsList;
	
	 public LiveMonitor(){
		 modelsList=new HashSet<String>();
	 }
	/**
	 * Creates GUI widgets
	 * @param compParent
	 */
	public void createControls(Composite compParent){
	
		ScrolledComposite scrolCompAnom=new ScrolledComposite(compParent, SWT.H_SCROLL | SWT.V_SCROLL);
		Composite comptbItmLive = new Composite(scrolCompAnom,SWT.NONE);
		
		
		//Designing the Layout of the GUI Items  for the LiveMonitor Tab Item
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan=1;
		comptbItmLive.setLayoutData(gridData);
		comptbItmLive.setLayout(new GridLayout(1, false));
	
		///////////////////////////////////////////////////////////////////////////
		//Creating GUI widgets for selection of a trace type and a selection of the model
		///////////////////////////////////////////////////////////////////
		//Composite compTraceTypeAndModel=new Composite(comptbItmLive, SWT.NONE);
		//compTraceTypeAndModel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		//compTraceTypeAndModel.setLayout(new GridLayout(1, false));
		
	
		// Create GUI elements for SSH Configuration
		selectHostUsingSSH(comptbItmLive);
			
		//////////////////////////////////////////////////////////////////////
		// Creating GUI widgets for buttons
		//////////////////////////////////////////////////////////////////
		
		Composite compButtons=new Composite(comptbItmLive, SWT.NONE);
		compButtons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		compButtons.setLayout(new GridLayout(5, false));
		
		btnStart=new Button(compButtons, SWT.BORDER);
		btnStart.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false,1,1));
		btnStart.setText("Start");
		
		
		btnStop=new Button(compButtons, SWT.BORDER);
		btnStop.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false,1,1));
		btnStop.setText("Stop");
		btnStop.setEnabled(false);
		
		
		

		
		//Adjust settings for scrollable LiveMonitor Tab Item
		scrolCompAnom.setContent(comptbItmLive);
		 // Set the minimum size
		scrolCompAnom.setMinSize(250, 250);
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
		grpSSHConfig.setText("Select SSH Configurations");
		
		grpSSHConfig.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		grpSSHConfig.setLayout(new GridLayout(3,false));
		
		Label userAtHost= new Label(grpSSHConfig, SWT.NONE);
		userAtHost.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false,1,1));
		userAtHost.setText("Enter username@hostname   ");
		
		Label lblSudoPassword= new Label(grpSSHConfig, SWT.NONE);
		lblSudoPassword.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false,1,1));
		lblSudoPassword.setText("Enter Password ");
		
		Label lblPort= new Label(grpSSHConfig, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		lblPort.setText("Enter Port");
			
		txtUserAtHost=new Text(grpSSHConfig, SWT.BORDER);
		txtUserAtHost.setEnabled(true);
		txtUserAtHost.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		txtUserAtHost.setText(System.getProperty("user.name")+"@localhost");
		//txtUserAtHost.setText("shary@172.30.103.143");
		
		txtSudoPassword=new Text(grpSSHConfig,SWT.BORDER|SWT.PASSWORD);
		txtSudoPassword.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		txtSudoPassword.setText("grt_654321");
		
		txtPort=new Text(grpSSHConfig,SWT.BORDER);
		txtPort.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
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
		compDurationPort.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,3,2));
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
		cmbInterval.add("0");
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
		grpTrainingAndEval.setText("Select Evaluation Type");
		grpTrainingAndEval.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		grpTrainingAndEval.setLayout(new GridLayout(2,false));
		
		btnTrainingAndEval=new Button(grpTrainingAndEval, SWT.NONE|SWT.RADIO);
		btnTrainingAndEval.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		btnTrainingAndEval.setText("Training and Testing");
		
		btnTesting=new Button(grpTrainingAndEval, SWT.NONE|SWT.RADIO);
		btnTesting.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		btnTesting.setText("Testing");
		btnTesting.setSelection(true);
		
		
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
					//btnDetails.setEnabled(false);
					
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
						
					liveExecutor= new BackgroundLiveMonitor
							  (txtUserAtHost.getText(), password, txtSudoPassword.getText(), 
									  privateKey, port,snapshotDuration,snapshotIntervals, btnStart,
									  	btnStop, modelsList,resultsAndFeedback,liveChart,
									  	isTrainAndEval);
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
		else if (modelsList.size() <=0){
			 msg="Please select a model first";
		}
		
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
	 * Sets the chart object
	 * @param chart
	 */
	public void setLiveChart(LiveXYChart chart){
		liveChart=chart;
	}
	/**
	 * Sets ResultsAndFeedback object
	 * @param results
	 */
	public void setResultsAndFeedback(ResultsAndFeedback results){
		System.out.println("Results in live Monitor");
		this.resultsAndFeedback=results;
	}

	/**
	 *Updates the selected model list 
	 * @param modelsList
	 */
	public void updateOnModelSelction(HashSet<String> modelList){
		this.modelsList=modelList;
	}
}
