package org.eclipse.linuxtools.tmf.totalads.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfEvent;

public class TextLineTraceReader implements ITraceTypeReader {

	//inner class
	class TextLineIterator implements ITraceIterator{
		BufferedReader bufferedReader;
		String event="";
		Boolean isClose=false;
		
		public TextLineIterator(BufferedReader  bReader){
			bufferedReader=bReader;
		}

		@Override
		public boolean advance() {
		   boolean isAdvance=false;
		   try {
				 event=bufferedReader.readLine();
				 if (event==null){
					  bufferedReader.close();
					  isClose=true;
					  isAdvance=false;
				 }
				 else{
					 isAdvance=true;
					 event=event.trim();
				 }
				
			} catch (IOException e) {
				
				e.printStackTrace();
			} 
		   return isAdvance;
		}

		@Override
		public String getCurrentEvent() {
			
			return event;
		}

		@Override
		public void close() {
			try {
				if (!isClose)
					bufferedReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
	// inner class ends
	/**
	 * Constructor
	 */
	public TextLineTraceReader() {
	
	}
	
	@Override
	public ITraceTypeReader createInstance(){
		return new TextLineTraceReader();
	}
	@Override
	public String getName() {
	
		return "Text-line Reader";
	}

	 /**
     * Returns the acronym of the text reader
     */
    public String getAcronym(){
    	
    	return "TXT";
    }
    
	@Override
	public ITraceIterator getTraceIterator(File file) throws Exception {
		
		BufferedReader bufferReader=new BufferedReader(new FileReader(file));
		TextLineIterator textLineIterator=new TextLineIterator(bufferReader);
		return textLineIterator;
	}

	@Override
	public void handleEvents(CtfTmfEvent event, StringBuilder traceBuffer) {
		// TODO Auto-generated method stub

	}
	
	 public static void registerTraceTypeReader() throws TotalADSUiException{
	    	TraceTypeFactory trcTypFactory=TraceTypeFactory.getInstance();
	    	TextLineTraceReader textFileReader=new TextLineTraceReader();
	    	trcTypFactory.registerTraceReaderWithFactory(textFileReader.getName(), textFileReader);
	    }
}
