package org.eclipse.linuxtools.tmf.totalads.dbms;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.eclipse.linuxtools.tmf.totalads.dbms.messages"; //$NON-NLS-1$
    public static String DBMSFactory_NoConnection;
    public static String DBMSFactory_VerifyConnection;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
