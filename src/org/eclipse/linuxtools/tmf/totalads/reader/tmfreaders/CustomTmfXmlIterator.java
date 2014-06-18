package org.eclipse.linuxtools.tmf.totalads.reader.tmfreaders;

import java.util.Collection;
import java.util.Iterator;
import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;
import org.eclipse.linuxtools.tmf.core.exceptions.TmfTraceException;
import org.eclipse.linuxtools.tmf.core.parsers.custom.CustomTxtEvent;
import org.eclipse.linuxtools.tmf.core.parsers.custom.CustomXmlTrace;
import org.eclipse.linuxtools.tmf.core.parsers.custom.CustomXmlTraceDefinition;
import org.eclipse.linuxtools.tmf.core.trace.ITmfContext;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;

/**
 * @author Syed Shariyar Murtaza justsshary@hotmail.com
 *
 */
public class CustomTmfXmlIterator implements ITraceIterator {

    private CustomXmlTrace fXmlTraceParser;
    private ITmfContext fCtx;
    private ITmfEvent fEvent;
    private String fTrainingField;

    /**
     * @param custTraceDef
     * @param path
     * @param trainingField
     * @throws TmfTraceException
     */
    public CustomTmfXmlIterator(CustomXmlTraceDefinition custTraceDef, String path) throws TmfTraceException{
        fXmlTraceParser=new CustomXmlTrace(custTraceDef);
        fXmlTraceParser.initTrace(null, path, CustomTxtEvent.class);
        fCtx=fXmlTraceParser.seekEvent(0);

    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator#advance()
     */
    @Override
    public boolean advance() throws TotalADSReaderException {
        if ((fEvent=fXmlTraceParser.getNext(fCtx))!=null){

            //Check only once for the training field
            if(fTrainingField==null){
                Collection<String> fieldName= fEvent.getContent().getFieldNames();
                Iterator <String>it=fieldName.iterator();
                while (it.hasNext()){
                    String field=it.next();
                    if (field.contains("*")){ //$NON-NLS-1$
                        fTrainingField=field;
                        return true;
                    }
                }
                //if training field is still null then there is no training field return false
                if (fTrainingField==null) {
                    throw new TotalADSReaderException("No training field found in the custom parser.");
                }
            }//End of check for training field

            return true;
         }
        return false;
    }

    @Override
    public String getCurrentEvent() {

       //fEvent.getContent().getField(fTrainingField).getName();
       return (String) fEvent.getContent().getField(fTrainingField).getValue();

    }

    @Override
    public void close() throws TotalADSReaderException {
        fXmlTraceParser.dispose();


    }

}
