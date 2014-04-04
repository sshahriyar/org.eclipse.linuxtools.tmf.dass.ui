package org.eclipse.linuxtools.tmf.totalads.readers.textreaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfEvent;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUiException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;

public class TextLineTraceReader implements ITraceTypeReader {

	//inner class
	class TextLineIterator implements ITraceIterator{
		BufferedReader bufferedReader;
		String event="";
		Boolean isClose=false;
		String regEx="";
		public TextLineIterator(BufferedReader  bReader){
			bufferedReader=bReader;
			regEx=""; //[| ]+([a-zA-Z0-9_:-]+)[ ]+.*"; //function call extraction pattern
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
					 if(!regEx.isEmpty())
						 event= event.replaceAll(regEx, "$1");
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


	
	 public static void registerTraceTypeReader() throws TotalADSUiException{
	    	TraceTypeFactory trcTypFactory=TraceTypeFactory.getInstance();
	    	TextLineTraceReader textFileReader=new TextLineTraceReader();
	    	trcTypFactory.registerTraceReaderWithFactory(textFileReader.getName(), textFileReader);
	    }
}
