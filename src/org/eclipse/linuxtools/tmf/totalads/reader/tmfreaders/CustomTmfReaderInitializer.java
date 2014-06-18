package org.eclipse.linuxtools.tmf.totalads.reader.tmfreaders;

import org.eclipse.linuxtools.tmf.core.parsers.custom.CustomTxtTraceDefinition;
import org.eclipse.linuxtools.tmf.core.parsers.custom.CustomXmlTraceDefinition;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
/**
 * This class loads all the custom text and xml readers created by users into TotalADS.
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class CustomTmfReaderInitializer {
    /**
     * This function loads all the custom text readers created in TMF and registers them with the
     * {@link TraceTypeFactory}
     * @throws TotalADSGeneralException An exception for invalid readers
     */
    public static void registerAllCustomTmfTextTReaders() throws TotalADSGeneralException {
        CustomTxtTraceDefinition[] cust = CustomTxtTraceDefinition.loadAll();

        if (cust != null) {

            for (int j = 0; j < cust.length; j++) {
                CustomTmfTextReader tmfTextReaders  = new CustomTmfTextReader(cust[j]);
                CustomTmfTextReader.registerTraceTypeReader(tmfTextReaders);
            }

        }

    }

    /**
     * This function loads all the custom XML readers created in TMF and registers them with the
     * {@link TraceTypeFactory}
     * @throws TotalADSGeneralException An exception for invalid readers
     */
    public static void registerAllCustomTmfXmlReaders() throws TotalADSGeneralException {
        CustomXmlTraceDefinition[] cust = CustomXmlTraceDefinition.loadAll();

        if (cust != null) {

            for (int j = 0; j < cust.length; j++) {
                CustomTmfXmlReader tmfXmlReaders  = new CustomTmfXmlReader(cust[j]);
                CustomTmfXmlReader.registerTraceTypeReader(tmfXmlReaders);
            }

        }

    }

}
