package org.eclipse.linuxtools.tmf.totalads.reader.tmfreaders;

import org.eclipse.linuxtools.tmf.core.parsers.custom.CustomTxtTraceDefinition;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;

public class CustomTmfReaderInitializer {

    public static void registerAllCustomTmfTextTReaders() throws TotalADSGeneralException {
        CustomTxtTraceDefinition[] cust = CustomTxtTraceDefinition.loadAll();

        if (cust != null) {
            //CustomTmfTextReader tmfTextReaders[] = new CustomTmfTextReader[cust.length];
            for (int j = 0; j < cust.length; j++) {
                CustomTmfTextReader tmfTextReaders  = new CustomTmfTextReader(cust[j]);
                CustomTmfTextReader.registerTraceTypeReader(tmfTextReaders);
            }

        }

    }

}
