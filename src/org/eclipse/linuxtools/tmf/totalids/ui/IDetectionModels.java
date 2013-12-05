package org.eclipse.linuxtools.tmf.totalids.ui;
/** Each model should implement the functions of this interface*/
public interface IDetectionModels {
/** Controller will pass a trace through this function. Some models can train on 
 * the traces as they come and some need to wait till the last trace. Controller 
 * will make isLastTrace true when the lastTrace will be sent.  */	
public void train (char[] trace, Boolean isLastTrace) throws Exception;
/**  Controller will pass traces for validation using this function.  */
public  void validate (char[] trace) throws Exception;
/** Controller will pass traces for testing using this function */
public void test (char[] trace) throws Exception;
}
