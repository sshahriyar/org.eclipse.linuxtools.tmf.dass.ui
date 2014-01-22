package org.eclipse.linuxtools.tmf.totalads.ui;

import org.swtchart.Chart;

public class DecisionTree implements IDetectionModels {

	public DecisionTree(DBMS connection) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void train(char[] trace, Boolean isLastTrace) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void validate(char[] trace) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void test(char[] trace, String traceName) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Boolean isValidationAllowed() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Decision Tree";
	}
	
	/**
	 *  Self registration of the model with the modelFactory 
	 */
	public static void registerModel(){
		ModelTypeFactory modelFactory= ModelTypeFactory.getInstance();
		DecisionTree decisionTree =new DecisionTree(Configuration.connection);
		modelFactory.registerModelWithFactory( ModelTypeFactory.ModelTypes.Classification,decisionTree);
	}


}
