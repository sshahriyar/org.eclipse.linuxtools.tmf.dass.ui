package org.eclipse.linuxtools.tmf.totalads.ui;

//import org.swtchart.Chart;

/** Each model should implement the functions of this interface and, in addition, a static function registerModel*/
public interface IDetectionModels {


/** Controller will pass a trace through this function. Some models can train on 
 * the traces as they come and some need to wait till the last trace. Controller 
 * will make isLastTrace true when the lastTrace will be sent.  
 * @param database TODO*/	
public void train (ITraceIterator trace, Boolean isLastTrace, String database) throws Exception;
/**  Controller will pass traces for validation using this function.  
 * @param database TODO*/
public  void validate (ITraceIterator trace, String database) throws Exception;
/** Controller will pass traces for testing using this function 
 * @param database TODO*/
public void test (ITraceIterator trace, String traceName, String database) throws Exception;
///** Returns true or false for a model if it allows validation or not**/
//public Boolean isValidationAllowed();
/** Returns the textual representation of the details of the results for a trace **/
public String textResult();
/** Returns the graphical result in the form of a chart if any for a trace **/
public org.swtchart.Chart graphicalResults();
/** Returns a self created instance of the model**/
public IDetectionModels createInstance();
// Model Register itself with the ModelTypeFactory
//Each derived class must implement the following static method
//public  void registerModel() throws Exception;
/** Gets the name of the model**/
public String getName();

/** Returns the acronym of the model; should only be three characters long */
public String getAcronym();
}