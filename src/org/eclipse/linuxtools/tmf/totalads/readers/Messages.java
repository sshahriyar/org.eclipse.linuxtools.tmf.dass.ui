package org.eclipse.linuxtools.tmf.totalads.readers;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.eclipse.linuxtools.tmf.totalads.readers.messages"; //$NON-NLS-1$
    public static String TraceTypeFactory_DuplicateKey;
    public static String TraceTypeFactory_EmptyKey;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
