package org.eclipse.linuxtools.tmf.totalads.ui;

import java.io.File;

import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfEvent;

public class TextLineReader implements ITraceTypeReader {

	public TextLineReader() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Text-line Reader";
	}

	@Override
	public StringBuilder getTrace(File file) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleEvents(CtfTmfEvent event, StringBuilder traceBuffer) {
		// TODO Auto-generated method stub

	}
	
	 public static void registerTraceTypeReader() throws Exception{
	    	TraceTypeFactory trcTypFactory=TraceTypeFactory.getInstance();
	    	TextLineReader textFileReader=new TextLineReader();
	    	trcTypFactory.registerModelWithFactory(textFileReader.getName(), textFileReader);
	    }
}
