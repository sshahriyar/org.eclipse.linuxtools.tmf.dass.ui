package org.eclipse.linuxtools.tmf.totalids.ui;

import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfEvent;
import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfTrace;
import org.eclipse.linuxtools.tmf.core.exceptions.TmfTraceException;
import java.io.File;
import java.util.ArrayList;
import java.lang.reflect.*;

public class Controller {
	ArrayList  <IDetectionModels> models;
	//private final String fPath;
    
	public Controller(){
		models= new ArrayList<IDetectionModels>();
	}
	/**
	 * 
	 * @param file
	 * @return
	 */
	private StringBuilder getTrace(File file){
		
		 String filePath=file.getPath();
		 StringBuilder traceBuffer= new StringBuilder();
		 CtfTmfTrace  fTrace = new CtfTmfTrace();
	
		 try {
	            fTrace.initTrace(null, filePath, CtfTmfEvent.class);
	      } catch (TmfTraceException e) {
	            /* Should not happen if tracesExist() passed */
	            throw new RuntimeException(e);
	      }
	      
		 	 
    	 TraceReader input = new TraceReader(fTrace,traceBuffer);
    	 input.readTrace();
    	 fTrace.dispose();
    	 return traceBuffer;
	}
	/**
	 * 
	 * @param trainDirectory
	 * @throws Exception
	 */
	public void trainModels(String trainDirectory) throws Exception{
		File traces=new File(trainDirectory);
		File []fileList;
		Boolean isLastTrace=false;
		if (traces.isDirectory())
	            fileList=traces.listFiles();
	    else{
	            fileList= new File[1];
	            fileList[0]=traces;
	    }
		
		
		for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
			 // get the trace
			 StringBuilder trace=this.getTrace(fileList[trcCnt]);
			 
			 /** Getting char representation in memory of StringBuilder trace  
			  * to avoid extra memory consumption and making it final to avoid 
			  * any manipulation from models  */ 
			 //String.class.getDeclaredField("value");
			 Field field = StringBuilder.class.getSuperclass().getDeclaredField("value");
			 field.setAccessible(true);
			 final char[] traceChar = (char[]) field.get(trace);
			 
			 if (trcCnt==fileList.length-1)
				 isLastTrace=true;
				 
			 for (int modlCnt=0; modlCnt<models.size();modlCnt++){
				IDetectionModels model= models.get(modlCnt);
				model.train(traceChar, isLastTrace);
				 
			 }
		}
		
		
	}
	/**
	 * 
	 * @param model
	 * @return
	 */
	public boolean addModels(IDetectionModels model){
		return models.add(model);
	}
	/**
	 * 
	 * @param model
	 * @return
	 */
	public boolean removeModels(IDetectionModels model){
		return models.remove(model);
	}
	/**
	 * 
	 */
	public void removeAllModels(){
		 models.clear();
	}
	
	public static void main (String args[]){
		 //StringBuilder trace= new StringBuilder();
		 /*CtfTmfTestTrace testTrace = CtfTmfTestTrace.KERNEL;
		 TraceReader input = new TraceReader(testTrace.getTrace(),trace);
		 input.readTrace();
		 System.out.println(trace.toString());*/
		
		//String trainDirectory="/home/umroot/experiments/workspace/tmf-ads/org.eclipse.linuxtools"
			//	+ "/lttng/org.eclipse.linuxtools.ctf.core.tests/traces/";
		String trainDirectory="/home/umroot/lttng-traces/trace1-session-20131121-150203/";
        Controller ctrl=new Controller();
        ctrl.addModels(new KernelStateModeling());
        try {
			ctrl.trainModels(trainDirectory);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 

	}
	
}
