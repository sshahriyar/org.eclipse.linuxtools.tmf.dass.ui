package org.eclipse.linuxtools.tmf.totalads.reader.tmfreaders;

import java.io.File;

import org.eclipse.linuxtools.tmf.core.exceptions.TmfTraceException;
import org.eclipse.linuxtools.tmf.core.parsers.custom.CustomXmlTraceDefinition;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;


/**
 * @author Syed Shariyar Murtaza justsshary@hotmail.com
 *
 */
public class CustomTmfXmlReader implements ITraceTypeReader {
    private String fReaderName;
    private CustomXmlTraceDefinition fCustReader;

    /**
     * @param custReader
     */
    public CustomTmfXmlReader(CustomXmlTraceDefinition custReader){
        fReaderName="Custom-"+custReader.definitionName;
        fCustReader=custReader;
    }

    @Override
    public ITraceTypeReader createInstance() {

        return this;
    }

    /**
     * Registers itself with the TraceTypeFactory
     * @param customXmlReader
     *
     * @throws TotalADSGeneralException
     *             Exception for invalid reader
     */
    public static void registerTraceTypeReader(CustomTmfXmlReader customXmlReader) throws TotalADSGeneralException {
        TraceTypeFactory trcTypFactory = TraceTypeFactory.getInstance();
        trcTypFactory.registerTraceReaderWithFactory(customXmlReader.getName(),customXmlReader );
    }

    @Override
    public String getName() {
        return fReaderName;
    }

    @Override
    public String getAcronym() {
        int endIdx=fReaderName.length();
        if (fReaderName.length() >3) {
            endIdx=3;
        }
        return "C"+fReaderName.substring(0, endIdx); //$NON-NLS-1$
    }

    @Override
    public ITraceIterator getTraceIterator(File file) throws TotalADSReaderException {
        try {
            return new CustomTmfXmlIterator(fCustReader, file.getPath());
        } catch (TmfTraceException e) {

            throw new TotalADSReaderException(e.getMessage() + "\n File: " + file.getName()); //$NON-NLS-1$
        }

    }

}
