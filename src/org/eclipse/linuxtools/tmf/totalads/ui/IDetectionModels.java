package org.eclipse.linuxtools.tmf.totalads.ui;

//import org.swtchart.Chart;

/** Each model should implement the functions of this interface and, in addition, a static function registerModel*/
public interface IDetectionModels {

public void createDatabase(String databaseName, DBMS connection) throws Exception; 

/** Controller will pass a trace through this function. Some models can train on 
 * the traces as they come and some need to wait till the last trace. Controller 
 * will make isLastTrace true when the lastTrace will be sent.  
 * 
 * @param trace
 * @param isLastTrace
 * @param database
 * @param connection
 * @param console
 * @throws Exception
 */
public void train (ITraceIterator trace, Boolean isLastTrace, String database, DBMS connection, ProgressConsole console) throws Exception;
/**
 * Controller will pass traces for validation using this function.  
 * @param trace
 * @param database
 * @param connection
 * @param isLastTrace
 * @param console
 * @throws Exception
 */
public  void validate (ITraceIterator trace, String database, DBMS connection, Boolean isLastTrace, ProgressConsole console) throws Exception;
/**
 * Controller will pass traces for testing using this function 
 * @param trace
 * @param traceName
 * @param database
 * @param connection
 * @throws Exception
 */
public void test (ITraceIterator trace, String traceName, String database, DBMS connection) throws Exception;

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