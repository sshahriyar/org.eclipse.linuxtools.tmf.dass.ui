package org.eclipse.linuxtools.tmf.totalads.reader.tmfreaders;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;
import org.eclipse.linuxtools.tmf.core.exceptions.TmfTraceException;
import org.eclipse.linuxtools.tmf.core.parsers.custom.CustomTxtEvent;
import org.eclipse.linuxtools.tmf.core.parsers.custom.CustomTxtTrace;
import org.eclipse.linuxtools.tmf.core.parsers.custom.CustomTxtTraceDefinition;
import org.eclipse.linuxtools.tmf.core.trace.ITmfContext;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;

/**
 * @author Syed Shariyar Murtaza justsshary@hotmail.com
 *
 */
public class CustomTmfTextIterator implements ITraceIterator {

    private CustomTxtTrace fTxtTraceParser;
    private ITmfContext fCtx;
    private ITmfEvent fEvent;
    private String fTrainingField;

    /**
     * @param custTraceDef
     * @param path
     * @param trainingField
     * @throws TmfTraceException
     */
    public CustomTmfTextIterator(CustomTxtTraceDefinition custTraceDef, String path) throws TmfTraceException{
        fTxtTraceParser=new CustomTxtTrace(custTraceDef);
        fTxtTraceParser.initTrace(null, path, CustomTxtEvent.class);
        fCtx=fTxtTraceParser.seekEvent(0);

    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator#advance()
     */
    @Override
    public boolean advance() throws TotalADSReaderException {
        if ((fEvent=fTxtTraceParser.getNext(fCtx))!=null){

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
        fTxtTraceParser.dispose();


    }

}
