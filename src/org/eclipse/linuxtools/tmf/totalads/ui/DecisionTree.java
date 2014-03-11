package org.eclipse.linuxtools.tmf.totalads.ui;

import org.swtchart.Chart;

public class DecisionTree implements IDetectionModels {

	public DecisionTree() {
		
	}
	
	@Override
    public void createDatabase(String databaseName, DBMS connection) throws Exception{
	
	}

	@Override
	public void train (ITraceIterator trace, Boolean isLastTrace, String database, DBMS connection, ProgressConsole console)  throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public  void validate (ITraceIterator trace, String database, DBMS connection, Boolean isLastTrace, ProgressConsole console) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Results test (ITraceIterator trace, String database, DBMS connection) throws Exception {
		return null;

	}

	
	@Override
	public String textResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Chart graphicalResults() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDetectionModels createInstance() {
		
		return new DecisionTree();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "F007:Decision Trees";
	}
	 /**
     * Returns the acronym of the model
     */
    public String getAcronym(){
    	
    	return "F007";
    }
	/**
	 *  Self registration of the model with the modelFactory 
	 */
	public static void registerModel() throws TotalADSUiException{
		ModelTypeFactory modelFactory= ModelTypeFactory.getInstance();
		DecisionTree decisionTree =new DecisionTree();
		modelFactory.registerModelWithFactory( ModelTypeFactory.ModelTypes.Classification,decisionTree);
	}


}
