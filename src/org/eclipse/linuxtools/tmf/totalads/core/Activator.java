/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.core;

//import org.eclipse.linuxtools.tmf.totalads.ui.models.TotalAdsState;
import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMSFactory;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    /**
     * The id of the plugin
     */
    public static final String PLUGIN_ID = "org.eclipse.linuxtools.tmf.totalads.ui"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        init();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        DBMSFactory.INSTANCE.closeConnection();
        // This code deinitializes the Factory instance. It was necessary
        // because
        // if TotalADS plugin is reopened in running Eclipse, the static objects
        // are not
        // deinitialized on previous close of the plugin.
        AlgorithmFactory.destroyInstance();
        TraceTypeFactory.destroyInstance();
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     *
     * Initialises TotalADS
     *
     */

    private static void init() {
        try {

            AlgorithmFactory algFactory = AlgorithmFactory.getInstance();
            algFactory.initialize();

            TraceTypeFactory trcTypeFactory = TraceTypeFactory.getInstance();
            trcTypeFactory.initialize();

            // Initialise the logger
            Handler handler = null;
            handler = new FileHandler(getCurrentPath() + "totaladslog.xml"); //$NON-NLS-1$
            Logger.getLogger("").addHandler(handler); //$NON-NLS-1$

        } catch (Exception ex) { // capture all the exceptions here, which are
                                 // missed by Diagnois and Modeling classes

            MessageBox msg = new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR);
            if (ex.getMessage() != null) {
                msg.setMessage(ex.getMessage());
                msg.open();
            }
            Logger.getLogger(Activator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the current directory of the application
     *
     * @return Path
     * @throws Exception
     */
    private static String getCurrentPath() {
        String applicationDir = Activator.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return applicationDir + File.separator;
    }
}
