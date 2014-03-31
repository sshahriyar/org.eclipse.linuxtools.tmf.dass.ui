package org.eclipse.linuxtools.tmf.totalads.ui;

//import org.swtchart.Chart;

/** Each model should implement the functions of this interface and, in addition, a static function registerModel*/
public interface IDetectionModels {
/**Inner class that returns results */
public class Results{
	/** Assign yes or no */
	public Boolean isAnomaly; 
	/** Assign anomaly type iff  any */ 
	public String anomalyType;
	/** Assign details separated by new line '\n' */
	public StringBuilder details=new StringBuilder();
}
/**
 * Creates a database where an algorithm would store its model
 * @param databaseName
 * @param connection
 * @throws Exception
 */
public void createDatabase(String databaseName, DBMS connection) throws Exception; 
/**
 * Returns the settings of an algorithm as option name at index i and value at index i+1.
 * Pass true to get training options and false to get testing options
 * @return String[]
 */
public String[] getTrainingOptions();
/**
 * Set the settings of an algorithm as option name at index i and value ate index i+1.
 * Pass true to get training options and false to get testing options
 * @param database TODO
 * @param connection TODO
 * @return TODO
 */
public String[] getTestingOptions(String database, DBMS connection);

/** Controller will pass a trace through this function. Some models can train on 
 * the traces as they come and some need to wait till the last trace. Controller 
 * will make isLastTrace true when the lastTrace will be sent.  
 * 
 * @param trace
 * @param isLastTrace
 * @param database
 * @param connection
 * @param console
 * @param options TODO
 * @throws Exception
 */
public void train (ITraceIterator trace, Boolean isLastTrace, String database, DBMS connection, ProgressConsole console, String[] options) throws Exception;
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
 * @param database
 * @param connection
 * @param options TODO
 * @param traceName
 * @throws Exception
 */
public Results test (ITraceIterator trace, String database, DBMS connection, String[] options) throws Exception;
/**
 * This function is used to do the cross validation on the training data in the database
 * @param database
 * @param connection
 * @param console
 * @throws Exception
 */
public void crossValidate(Integer folds, String database, DBMS connection, ProgressConsole console) throws Exception;
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