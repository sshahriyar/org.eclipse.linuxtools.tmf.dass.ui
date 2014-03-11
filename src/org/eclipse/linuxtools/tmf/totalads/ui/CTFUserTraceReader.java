package org.eclipse.linuxtools.tmf.totalads.ui;

import java.io.File;

import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfEvent;

public class CTFUserTraceReader implements ITraceTypeReader {

	public CTFUserTraceReader() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "CTF User-space Reader";
	}
	@Override
	public ITraceTypeReader createInstance(){
		return new  CTFUserTraceReader();
	}
	
	/**
     * Returns the acronym of the User space Reader
     */
    public String getAcronym(){
    	
    	return "USR";
    }
	 public static void registerTraceTypeReader() throws TotalADSUiException{
	    	TraceTypeFactory trcTypFactory=TraceTypeFactory.getInstance();
	    	CTFUserTraceReader userTraceReader=new CTFUserTraceReader();
	    	trcTypFactory.registerTraceReaderWithFactory(userTraceReader.getName(), userTraceReader);
	    }
	 
	@Override
	public ITraceIterator getTraceIterator(File file) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleEvents(CtfTmfEvent event, StringBuilder traceBuffer) {
		// TODO Auto-generated method stub

	}

}
