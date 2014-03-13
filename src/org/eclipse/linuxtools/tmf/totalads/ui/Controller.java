//package org.eclipse.linuxtools.tmf.totalads.ui;
//
//import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfEvent;
//import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfTrace;
//import org.eclipse.linuxtools.tmf.core.exceptions.TmfTraceException;
//import java.io.File;
//import java.util.ArrayList;
//import java.lang.reflect.*;
//
//public class Controller {
//	ArrayList  <IDetectionModels> models;
//	//private final String fPath;
//    
//	public Controller(){
//		models= new ArrayList<IDetectionModels>();
//	}
//
//	/**
//	 * 
//	 * @param trainDirectory
//	 * @throws Exception
//	 */
//	public void trainModels(String trainDirectory) throws Exception{
//		
//		Boolean isLastTrace=false;
//		File fileList[]=getDirectoryHandler(trainDirectory);
//		ITraceTypeReader input = new CTFSysCallTraceReader();////------------------
//		for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
//			 // get the trace
//			 StringBuilder trace=input.getTrace(fileList[trcCnt]);
//			 
//			 if (trace.length()>0){
//					 /** Getting char representation in memory of StringBuilder trace  
//					  * to avoid extra memory consumption and making it final to avoid 
//					  * any manipulation from models  */ 
//					 //String.class.getDeclaredField("value");
//					 Field field = StringBuilder.class.getSuperclass().getDeclaredField("value");
//					 field.setAccessible(true);
//					 final char[] traceChar = (char[]) field.get(trace);
//					 
//					 if (trcCnt==fileList.length-1)
//						 isLastTrace=true;
//						 
//					 for (int modlCnt=0; modlCnt<models.size();modlCnt++){
//						IDetectionModels model= models.get(modlCnt);
//						model.train(traceChar, isLastTrace);
//						 
//					 }
//			}
//		}
//		
//		
//	}
//	
//public void validateModels(String validationDirectory) throws Exception{
//		
//		
//		File fileList[]=getDirectoryHandler(validationDirectory);
//		ITraceTypeReader input = new CTFSysCallTraceReader();////------------------
//		
//		for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
//			 // get the trace
//			 StringBuilder trace=input.getTrace(fileList[trcCnt]);
//			
//			 if (trace.length()>0){ 
//					 /** Getting char representation in memory of StringBuilder trace  
//					  * to avoid extra memory consumption and making it final to avoid 
//					  * any manipulation from models  */ 
//					 //String.class.getDeclaredField("value");
//					 Field field = StringBuilder.class.getSuperclass().getDeclaredField("value");
//					 field.setAccessible(true);
//					 final char[] traceChar = (char[]) field.get(trace);
//								 
//					 for (int modlCnt=0; modlCnt<models.size();modlCnt++){
//						IDetectionModels model= models.get(modlCnt);
//						if (model.isValidationAllowed())
//							model.validate(traceChar);
//						 
//					 }
//			}
//		}
//		
//		
//	}
//
///**
// * 
// * @param traceFilePath
// */
//public void testTraceUsingModels(String traceFilePath) throws Exception{
//	
//	 ITraceTypeReader input = new CTFSysCallTraceReader();////------------------
//	 StringBuilder trace=input.getTrace(new File(traceFilePath));
//	 
//	 /** Getting char representation in memory of StringBuilder trace  
//	  * to avoid extra memory consumption and making it final to avoid 
//	  * any manipulation from models  */ 
//	 
//	 Field field = StringBuilder.class.getSuperclass().getDeclaredField("value");
//	 field.setAccessible(true);
//	 final char[] traceChar = (char[]) field.get(trace);
//				 
//	 for (int modlCnt=0; modlCnt<models.size();modlCnt++){
//		IDetectionModels model= models.get(modlCnt);
//		model.test(traceChar,traceFilePath);
//		 
//	 }
//}
///**
// * 
// * @param traceBuffer
// *           StringBuilder
// */
//public void testTraceUsingModels(StringBuilder traceBuffer, String tracePath) throws Exception{
//	
//	 
//	 /** Getting char representation in memory of StringBuilder trace  
//	  * to avoid extra memory consumption and making it final to avoid 
//	  * any manipulation from models  */ 
//	 
//	 Field field = StringBuilder.class.getSuperclass().getDeclaredField("value");
//	 field.setAccessible(true);
//	 final char[] traceChar = (char[]) field.get(traceBuffer);
//				 
//	 for (int modlCnt=0; modlCnt<models.size();modlCnt++){
//		IDetectionModels model= models.get(modlCnt);
//		model.test(traceChar,tracePath);
//		 
//	 }
//}
//	/**
//	 * 
//	 * @param model
//	 * @return
//	 */
//	public boolean addModels(IDetectionModels model){
//		return models.add(model);
//	}
//	/**
//	 * 
//	 * @param model
//	 * @return
//	 */
//	public boolean removeModels(IDetectionModels model){
//		return models.remove(model);
//	}
//	/**
//	 * 
//	 */
//	public void removeAllModels(){
//		 models.clear();
//	}
//	/**
//	 * 
//	 * @param trainDirectory
//	 * @return
//	 */
//	private File[] getDirectoryHandler(String trainDirectory){
//		File traces=new File(trainDirectory);
//		File []fileList;
//		
//		
//		if (traces.isDirectory())
//	            fileList=traces.listFiles();
//	    else{
//	            fileList= new File[1];
//	            fileList[0]=traces;
//	    }
//		return fileList;
//	}
//	
//	public static void main (String args[]){
//		 //StringBuilder trace= new StringBuilder();
//		 /*CtfTmfTestTrace testTrace = CtfTmfTestTrace.KERNEL;
//		 TraceReader input = new TraceReader(testTrace.getTrace(),trace);
//		 input.readTrace();
//		 System.out.println(trace.toString());*/
//		
//		//String trainDirectory="/home/umroot/experiments/workspace/tmf-ads/org.eclipse.linuxtools"
//			//	+ "/lttng/org.eclipse.linuxtools.ctf.core.tests/traces/";
//		String trainDirectory="/home/umroot/lttng-traces/trace1-session-20131121-150203/";
//		String validationDirectory="/home/umroot/experiments/workspace/tmf-ads/org.eclipse.linuxtools"
//					+ "/lttng/org.eclipse.linuxtools.ctf.core.tests/traces/kl/kernel";
//        Controller ctrl=new Controller();
//       
//        try {
//        	DBMS conn= new  DBMS();
//        	 ctrl.addModels(new KernelStateModeling(conn));
//        	// ctrl.trainModels(trainDirectory);
//        	 //ctrl.validateModels(validationDirectory);
//        	 ctrl.testTraceUsingModels(validationDirectory);
//        	 conn.closeConnection();
//        	 System.out.println("Done");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		 
//
//	}
//	
//}
