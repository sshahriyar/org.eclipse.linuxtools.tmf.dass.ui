package org.eclipse.linuxtools.tmf.totalads.ui.ctfreaders;

import java.io.File;

import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfEvent;
import org.eclipse.linuxtools.tmf.totalads.ui.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.ui.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.ui.TotalADSUiException;
import org.eclipse.linuxtools.tmf.totalads.ui.TraceTypeFactory;

public class CTFFunctionEntryExitTraceReader implements ITraceTypeReader {
	// It is difficult to implement this as this requires mapping of symbol files because lttng generates only addresses
	// not the function names. This is just a structure for future
	public CTFFunctionEntryExitTraceReader() {
		
	}

	@Override
	public String getName() {
		
		return "CTF Function Entry/Exit Reader";
	}
	@Override
	public ITraceTypeReader createInstance(){
		return new  CTFFunctionEntryExitTraceReader();
	}
	
	/**
     * Returns the acronym of the User space Reader
     */
    public String getAcronym(){
    	
    	return "FUN";
    }
	 public static void registerTraceTypeReader() throws TotalADSUiException{
	    	TraceTypeFactory trcTypFactory=TraceTypeFactory.getInstance();
	    	CTFFunctionEntryExitTraceReader userTraceReader=new CTFFunctionEntryExitTraceReader();
	    	trcTypFactory.registerTraceReaderWithFactory(userTraceReader.getName(), userTraceReader);
	    }
	 
	@Override
	public ITraceIterator getTraceIterator(File file) throws Exception {
	
		return null;
	}



}
