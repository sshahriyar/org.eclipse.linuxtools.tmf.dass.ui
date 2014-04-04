package org.eclipse.linuxtools.tmf.totalads.algorithms.hiddenmarkovmodel;

import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory.ModelTypes;
import org.eclipse.linuxtools.tmf.totalads.algorithms.slidingwindow.SlidingWindow;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUiException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.ui.ProgressConsole;
import org.swtchart.Chart;

public class HiddenMarkovModel implements IDetectionAlgorithm {

	public HiddenMarkovModel() {
		// TODO Auto-generated constructor stub
	}

	/**
	 *  Self registration of the model with the modelFactory 
	 */
	public static void registerModel() throws TotalADSUiException{
		AlgorithmFactory modelFactory= AlgorithmFactory.getInstance();
		HiddenMarkovModel hmm=new HiddenMarkovModel();
		modelFactory.registerModelWithFactory( AlgorithmFactory.ModelTypes.Anomaly,  hmm);
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
	public IDetectionAlgorithm createInstance() {
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
