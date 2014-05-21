package org.eclipse.linuxtools.tmf.totalads.ui.diagnosis;

import org.eclipse.osgi.util.NLS;

/**
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.messages"; //$NON-NLS-1$
    public static String BackgroundTesting_DBMSException;
    public static String BackgroundTesting_FolderContainsDir;
    public static String BackgroundTesting_CommonException;
    public static String BackgroundTesting_CompletionMessage;
    public static String BackgroundTesting_ConsoleStartMessage;
    public static String BackgroundTesting_ConsoleTitle;
    public static String BackgroundTesting_EmptyDirectory;
    public static String BackgroundTesting_ReaderException;
    public static String BackgroundTesting_TraceCountMessage;
    public static String BackgroundTesting_TraceLimit;
    public static String BackgroundTesting_GeneralException;
    public static String BackgroundTesting_InvalidTrace;
    public static String BackgroundTesting_LTTngFolderContainsFilesandDir;
    public static String BackgroundTesting_ModelEval;
    public static String BackgroundTesting_SelectFolder;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
