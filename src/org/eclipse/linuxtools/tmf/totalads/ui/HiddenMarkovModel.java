package org.eclipse.linuxtools.tmf.totalads.ui;

import org.eclipse.linuxtools.tmf.totalads.ui.slidingwindow.SlidingWindow;
import org.swtchart.Chart;

public class HiddenMarkovModel implements IDetectionModels {

	public HiddenMarkovModel() {
		// TODO Auto-generated constructor stub
	}

	/**
	 *  Self registration of the model with the modelFactory 
	 */
	public static void registerModel() throws TotalADSUiException{
		ModelTypeFactory modelFactory= ModelTypeFactory.getInstance();
		HiddenMarkovModel hmm=new HiddenMarkovModel();
		modelFactory.registerModelWithFactory( ModelTypeFactory.ModelTypes.Anomaly,  hmm);
	}
	
	@Override
	public void createDatabase(String databaseName, DBMS connection)
			throws Exception {
		throw new TotalADSUiException("HMM is not implemented yet");

	}

	@Override
	public String[] getTrainingOptions() {
		
		return null;
	}

	@Override
	public String[] getTestingOptions(String database, DBMS connection) {
		
		return null;
	}

	@Override
	public void train(ITraceIterator trace, Boolean isLastTrace,
			String database, DBMS connection, ProgressConsole console,
			String[] options) throws Exception {
		throw new TotalADSUiException("HMM is not implemented yet");

	}

	@Override
	public void validate(ITraceIterator trace, String database,
			DBMS connection, Boolean isLastTrace, ProgressConsole console)
			throws Exception {
		throw new TotalADSUiException("HMM is not implemented yet");

	}

	@Override
	public Results test(ITraceIterator trace, String database, DBMS connection,
			String[] options) throws Exception {
		throw new TotalADSUiException("HMM is not implemented yet");
		
	}

	@Override
	public void crossValidate(Integer folds, String database, DBMS connection,
			ProgressConsole console) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSummaryOfTestResults() {
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
		// TODO Auto-generated method stub
		return new HiddenMarkovModel();
	}

	@Override
	public String getName() {
		
		return "Hidden Markov Model";
	}

	@Override
	public String getAcronym() {
		
		return "HMM";
	}

}
