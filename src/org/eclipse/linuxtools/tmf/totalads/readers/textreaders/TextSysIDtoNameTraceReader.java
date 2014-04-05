package org.eclipse.linuxtools.tmf.totalads.readers.textreaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.linuxtools.tmf.totalads.readers.textreaders.MapSysCallIDToName;

public class TextSysIDtoNameTraceReader implements ITraceTypeReader {

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
					 String syscall=MapSysCallIDToName.getSysCallName(Integer.parseInt(event));
					 event=syscall;
					 
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
	public TextSysIDtoNameTraceReader() {
	
	}
	
	@Override
	public ITraceTypeReader createInstance(){
		return new TextSysIDtoNameTraceReader();
	}
	@Override
	public String getName() {
		// This is for lab experiments only
		return "Text-Syscall ID to Name Reader (for lab)";
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


	
	 public static void registerTraceTypeReader() throws TotalADSUIException{
	    	TraceTypeFactory trcTypFactory=TraceTypeFactory.getInstance();
	    	TextSysIDtoNameTraceReader textFileReader=new TextSysIDtoNameTraceReader();
	    	trcTypFactory.registerTraceReaderWithFactory(textFileReader.getName(), textFileReader);
	    }
}
