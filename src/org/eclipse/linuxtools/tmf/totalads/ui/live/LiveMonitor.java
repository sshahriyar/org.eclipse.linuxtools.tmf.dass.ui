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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmUtility;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.ui.io.DirectoryBrowser;
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
 * This class creates the GUI elements/widgets for live diagnosis using ssh. Any
 * host can be selected and LTTNg tracing can be started on that system and one
 * of the eqarlier model can be used to test the live tracing
 *
 *
 * @author <p>
 *         Syed Shariyar Murtaza justsshary@hotmail.com
 *         </p>
 *
 */
public class LiveMonitor {

    private Text fTxtUserAtHost;
    private Combo fCmbSnapshot;
    private Combo fCmbInterval;
    private Text fTxtPort;
    private Text fTxtSudoPassword;
    private ResultsAndFeedback fResultsAndFeedback;
    private Button fBtnStart;
    private Button fBtnStop;
    private Text fTxtTraces;
    private BackgroundLiveMonitor fLiveExecutor;
    private LiveXYChart fLiveChart;
    private Button fBtnTrainingAndEval;
    private Button fBtnTesting;
    private HashSet<String> fModelsList;

    /**
     * Constructor for the Live Monitor
     */
    public LiveMonitor() {
        fModelsList = new HashSet<>();
    }

    /**
     * Creates GUI widgets
     *
     * @param compParent
     *            Composite
     */
    public void createControls(Composite compParent) {

        ScrolledComposite scrolCompAnom = new ScrolledComposite(compParent, SWT.H_SCROLL | SWT.V_SCROLL);
        Composite comptbItmLive = new Composite(scrolCompAnom, SWT.NONE);

        // Designing the Layout of the GUI Items for the LiveMonitor Tab Item
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.horizontalSpan = 1;
        comptbItmLive.setLayoutData(gridData);
        comptbItmLive.setLayout(new GridLayout(2, false));

        // /////////////////////////////////////////////////////////////////////////
        // Creating GUI widgets for selection of a trace type and a selection of
        // the model
        // /////////////////////////////////////////////////////////////////
        // Composite compTraceTypeAndModel=new Composite(comptbItmLive,
        // SWT.NONE);
        // compTraceTypeAndModel.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
        // false, true));
        // compTraceTypeAndModel.setLayout(new GridLayout(1, false));

        // Create GUI elements for SSH Configuration
        selectHostUsingSSH(comptbItmLive);
        trainingAndTesting(comptbItmLive);
        traceStorage(comptbItmLive);
        // ////////////////////////////////////////////////////////////////////
        // Creating GUI widgets for buttons
        // ////////////////////////////////////////////////////////////////

        Composite compButtons = new Composite(comptbItmLive, SWT.NONE);
        compButtons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
        compButtons.setLayout(new GridLayout(5, false));

        fBtnStart = new Button(compButtons, SWT.BORDER);
        fBtnStart.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 1, 1));
        fBtnStart.setText("Start");

        fBtnStop = new Button(compButtons, SWT.BORDER);
        fBtnStop.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
        fBtnStop.setText("Stop");
        fBtnStop.setEnabled(false);

        // Adjust settings for scrollable LiveMonitor Tab Item
        scrolCompAnom.setContent(comptbItmLive);
        // Set the minimum size
        scrolCompAnom.setMinSize(200, 200);
        // Expand both horizontally and vertically
        scrolCompAnom.setExpandHorizontal(true);
        scrolCompAnom.setExpandVertical(true);
        addHandlers();
    }

    /**
     * Creates GUI widgets for a selection of traces and trace types
     *
     * @param compParent
     *            Composite of LiveMonitor
     */
    private void selectHostUsingSSH(Composite compParent) {
        /**
         * Group trace selection
         */
        Group grpSSHConfig = new Group(compParent, SWT.NONE);
        grpSSHConfig.setText("Select SSH Configurations");

        grpSSHConfig.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 2));
        grpSSHConfig.setLayout(new GridLayout(2, false));

        // /////////////////////////////////////////////
        // /User name, password, and port
        // ////////////////////////////////////////////
        Composite compUserPasPort = new Composite(grpSSHConfig, SWT.NONE);
        compUserPasPort.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        compUserPasPort.setLayout(new GridLayout(3, false));

        Label userAtHost = new Label(compUserPasPort, SWT.NONE);
        userAtHost.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
        userAtHost.setText("Enter username@hostname   ");

        Label lblSudoPassword = new Label(compUserPasPort, SWT.NONE);
        lblSudoPassword.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
        lblSudoPassword.setText("Enter Password ");

        Label lblPort = new Label(compUserPasPort, SWT.NONE);
        lblPort.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
        lblPort.setText("Enter Port");

        fTxtUserAtHost = new Text(compUserPasPort, SWT.BORDER);
        fTxtUserAtHost.setEnabled(true);
        fTxtUserAtHost.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        fTxtUserAtHost.setText(System.getProperty("user.name") + "@localhost");
        // fTxtUserAtHost.setText("shary@172.30.103.143");

        fTxtSudoPassword = new Text(compUserPasPort, SWT.BORDER | SWT.PASSWORD);
        fTxtSudoPassword.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        fTxtSudoPassword.setText("grt_654321");

        fTxtPort = new Text(compUserPasPort, SWT.BORDER);
        fTxtPort.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        fTxtPort.setText("22");
        // ///////
        // /SSH Password and Private Key: Currently disabling this to provide it
        // in the next version
        // ///////
        /*
         * Group grpPrivacy = new Group(grpSSHConfig, SWT.NONE);
         * grpPrivacy.setText("SSH Password/Pvt. Key");
         *
         * grpPrivacy.setLayoutData(new
         * GridData(SWT.FILL,SWT.FILL,false,false,3,2));
         * grpPrivacy.setLayout(new GridLayout(3,false));
         *
         * btnPassword = new Button(grpPrivacy, SWT.RADIO);
         * btnPassword.setText("Enter Password"); btnPassword.setLayoutData(new
         * GridData(SWT.FILL,SWT.TOP,true, false,1,1));
         * btnPassword.setSelection(true);
         *
         * btnPvtKey = new Button(grpPrivacy, SWT.RADIO);
         * btnPvtKey.setText("Select Private Key"); btnPvtKey.setLayoutData(new
         * GridData(SWT.FILL,SWT.TOP,true, false,2,1));
         *
         *
         * txtPassword=new Text(grpPrivacy,SWT.BORDER|SWT.PASSWORD);
         * txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
         * false,1,1)); txtPassword.setText("grt_654321");
         *
         * txtPvtKey=new Text(grpPrivacy,SWT.BORDER);
         * txtPvtKey.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
         * false,1,1)); //txtPvtKey.setText(""); txtPvtKey.setEnabled(false);
         *
         * trcbrowser=new FileBrowser(grpPrivacy, txtPvtKey, new
         * GridData(SWT.RIGHT, SWT.TOP, false, false));
         * trcbrowser.disableBrowsing();
         */
        // ////////
        // /End SSH password and private name
        // ////
        // /////////////
        // Duration and Port
        // ////////
        Composite compDuration = new Composite(grpSSHConfig, SWT.NONE);
        compDuration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        compDuration.setLayout(new GridLayout(2, false));

        Label lblSnapshotDuration = new Label(compDuration, SWT.NONE);
        lblSnapshotDuration.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
        lblSnapshotDuration.setText("Snapshot Duration (secs)");

        Label lblIntervalDuration = new Label(compDuration, SWT.NONE);
        lblIntervalDuration.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
        lblIntervalDuration.setText("Snapshots Interval (mins)");

        fCmbSnapshot = new Combo(compDuration, SWT.NONE | SWT.READ_ONLY);
        fCmbSnapshot.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
        fCmbSnapshot.add("5");
        fCmbSnapshot.add("10");
        fCmbSnapshot.add("15");
        fCmbSnapshot.add("20");
        fCmbSnapshot.add("35");
        fCmbSnapshot.add("60");
        fCmbSnapshot.select(0);

        fCmbInterval = new Combo(compDuration, SWT.NONE | SWT.READ_ONLY);
        fCmbInterval.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
        fCmbInterval.add("0");
        fCmbInterval.add("1");
        fCmbInterval.add("3");
        fCmbInterval.add("5");
        fCmbInterval.add("7");
        fCmbInterval.add("10");
        fCmbInterval.add("15");
        fCmbInterval.add("20");
        fCmbInterval.add("30");
        fCmbInterval.select(0);

        /**
         * End group trace selection
         */
    }

    /**
     * Training and Testing Widgets
     */
    public void trainingAndTesting(Composite compParent) {
        // ///////
        // /Training and Evaluation
        // ///////
        Group grpTrainingAndEval = new Group(compParent, SWT.NONE);
        grpTrainingAndEval.setText("Select Evaluation Type");
        grpTrainingAndEval.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        grpTrainingAndEval.setLayout(new GridLayout(2, false));

        fBtnTrainingAndEval = new Button(grpTrainingAndEval, SWT.NONE | SWT.RADIO);
        fBtnTrainingAndEval.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        fBtnTrainingAndEval.setText("Training and Testing");

        fBtnTesting = new Button(grpTrainingAndEval, SWT.NONE | SWT.RADIO);
        fBtnTesting.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        fBtnTesting.setText("Testing");
        fBtnTesting.setSelection(true);

    }

    /**
     * Trace storage widgets
     *
     * @param compParent
     *            Composite
     */
    private void traceStorage(Composite compParent) {
        // ///////
        // /Training and Evaluation
        // ///////
        Group grpStorage = new Group(compParent, SWT.NONE);
        grpStorage.setText("Trace Storage Directory");
        grpStorage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        grpStorage.setLayout(new GridLayout(3, false));

        fTxtTraces = new Text(grpStorage, SWT.BORDER);
        fTxtTraces.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        new DirectoryBrowser(grpStorage, fTxtTraces, new GridData(SWT.RIGHT, SWT.TOP, false, false));

    }

    /**
     * Handlers
     */
    private void addHandlers() {
        /**
		 *
		 */
        fBtnStart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {

                if (findInvalidSSettings() == false && inValidModel() == false) {
                    fResultsAndFeedback.clearData();

                    String password = ""; //$NON-NLS-1$
                    String privateKey = ""; //$NON-NLS-1$
                    Boolean isTrainAndEval = false;

                    int port = Integer.parseInt(fTxtPort.getText());
                    int snapshotDuration = Integer.parseInt(fCmbSnapshot.getItem(fCmbSnapshot.getSelectionIndex()));
                    int snapshotIntervals = Integer.parseInt(fCmbInterval.getItem(fCmbInterval.getSelectionIndex()));

                    /*
                     * // Will be enabled in the next version if
                     * (btnPassword.getSelection())
                     * password=txtPassword.getText(); else if
                     * (btnPvtKey.getSelection())
                     * privateKey=txtPvtKey.getText();
                     */
                    // so using the following for this version
                    password = fTxtSudoPassword.getText();

                    if (fBtnTrainingAndEval.getSelection()) {
                        isTrainAndEval = true;
                    } else if (fBtnTesting.getSelection()) {
                        isTrainAndEval = false;
                    }

                    fBtnStart.setEnabled(false);
                    fBtnStop.setEnabled(true);

                    fLiveExecutor = new BackgroundLiveMonitor
                            (fTxtUserAtHost.getText(), password, fTxtSudoPassword.getText(),
                                    privateKey, port, snapshotDuration, snapshotIntervals, fBtnStart,
                                    fBtnStop, fModelsList, fResultsAndFeedback, fLiveChart, fTxtTraces.getText(),
                                    isTrainAndEval);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(fLiveExecutor);
                    executor.shutdown();
                }

            }

        });
        /**
         * Stop button event handler
         */
        fBtnStop.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                fLiveExecutor.stopMonitoring();
            }
        });

        // **** Pvt name button handler: Will be enabled in the next version
        /*
         * btnPvtKey.addSelectionListener(new SelectionAdapter() {
         *
         * @Override public void widgetSelected(SelectionEvent e) {
         * trcbrowser.enableBrowsing(); txtPvtKey.setEnabled(true);
         * txtPassword.setEnabled(false); txtPassword.setText(""); } });
         */

        // **** Password button name handler
        /*
         * btnPassword.addSelectionListener(new SelectionAdapter() {
         *
         * @Override public void widgetSelected(SelectionEvent e) {
         *
         * txtPvtKey.setEnabled(false); txtPvtKey.setText("");
         * trcbrowser.disableBrowsing(); txtPassword.setEnabled(true); } });
         */

    }

    /**
     * Validates the fields before execution
     *
     * @return
     */
    private Boolean findInvalidSSettings() {

        String msg = ""; //$NON-NLS-1$
        if (fTxtUserAtHost.getText().isEmpty()) {
            msg = "User@Host cannot be empty";
        } else if (fTxtSudoPassword.getText().isEmpty()) {
            msg = "Password cannot be empty";
        } else if (fTxtPort.getText().isEmpty()) {
            msg = "Port cannot be empty";
        } else if (fModelsList.size() <= 0) {
            msg = "Please select a model first";
        }
        else if (fTxtTraces.getText().isEmpty()) {
            msg = "Select directory to store traces";
        }
        else {
            try {
                Integer.parseInt(fTxtPort.getText());
            } catch (Exception ex) {
                msg = "Port number can only be a number";
            }
            if (msg.isEmpty()) {
                File file = new File(fTxtTraces.getText() + File.separator + "tmp");
                try {
                    file.createNewFile();
                    file.delete();
                } catch (IOException e) {
                    msg = "Cannot write to the directory";
                }

            }
        }

        if (!msg.isEmpty()) {
            MessageBox msgBox = new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR | SWT.OK);
            msgBox.setMessage(msg);
            msgBox.open();
            return true;
        }
        return false;
    }

    /**
     * Checks whether an algorithm of a model supports online training
     *
     * @return
     */
    private Boolean inValidModel() {

        if (fBtnTrainingAndEval.getSelection() == false) {
            return false;
        }

        String exception = ""; //$NON-NLS-1$
        Iterator<String> it = fModelsList.iterator();
        while (it.hasNext()) {

            String model = it.next();
            try {
                IDetectionAlgorithm algorithm = AlgorithmUtility.getAlgorithmFromModelName(model);
                if (!algorithm.isOnlineLearningSupported()) {
                    exception = algorithm.getName() + " does not support online training. It can only be used"
                            + " for online testing";
                    break;

                }
            } catch (TotalADSGeneralException e) {
                exception = e.getMessage();
            }

        }

        if (exception.isEmpty()) {
            return false;
        }
        MessageBox msgBox = new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR | SWT.OK);
        msgBox.setMessage(exception);
        msgBox.open();
        return true;
    }

    /**
     * Sets the chart object
     *
     * @param chart
     *            Chart object
     */
    public void setLiveChart(LiveXYChart chart) {
        fLiveChart = chart;
    }

    /**
     * Sets ResultsAndFeedback object
     *
     * @param results
     *            Results object
     */
    public void setResultsAndFeedback(ResultsAndFeedback results) {

        this.fResultsAndFeedback = results;
    }

    /**
     * Updates the selected model list
     *
     * @param modelsList
     *            Models' list
     */
    public void updateOnModelSelction(HashSet<String> modelsList) {
        this.fModelsList = modelsList;
    }
}
