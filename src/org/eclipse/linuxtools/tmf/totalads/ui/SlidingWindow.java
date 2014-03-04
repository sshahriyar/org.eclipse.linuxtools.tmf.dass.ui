/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads.ui;

import org.swtchart.Chart;

/**
 * @author umroot
 *
 */
public class SlidingWindow implements IDetectionModels {

	/**
	 * 
	 */
	public SlidingWindow(DBMS connection) {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.ui.IDetectionModels#train(char[], java.lang.Boolean)
	 */
	@Override
	public void train (ITraceIterator trace, Boolean isLastTrace, String database, DBMS connection, ProgressConsole console)  throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.ui.IDetectionModels#validate(char[])
	 */
	@Override
	public  void validate (ITraceIterator trace, String database, DBMS connection, Boolean isLastTrace, ProgressConsole console) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.ui.IDetectionModels#test(char[], java.lang.String)
	 */
	@Override
	public void test (ITraceIterator trace, String traceName, String database, DBMS connection) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.ui.IDetectionModels#isValidationAllowed()
	 */
	

	/* (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.ui.IDetectionModels#textResult()
	 */
	@Override
	public String textResult() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.ui.IDetectionModels#graphicalResults()
	 */
	@Override
	public Chart graphicalResults() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.ui.IDetectionModels#createInstance()
	 */
	@Override
	public IDetectionModels createInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * Returns the name
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Sliding Window";
	}
	
	 /**
     * Returns the acronym of the model
     */
    public String getAcronym(){
    	
    	return "SWN";
    }
	/**
	 *  Self registration of the model with the modelFactory 
	 */
	public static void registerModel(){
		ModelTypeFactory modelFactory= ModelTypeFactory.getInstance();
		SlidingWindow sldWin=new SlidingWindow(Configuration.connection);
		modelFactory.registerModelWithFactory( ModelTypeFactory.ModelTypes.Anomaly,sldWin);
	}
}
