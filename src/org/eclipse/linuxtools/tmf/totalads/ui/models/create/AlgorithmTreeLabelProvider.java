package org.eclipse.linuxtools.tmf.totalads.ui.models.create;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;

public class AlgorithmTreeLabelProvider extends StyledCellLabelProvider {

	public AlgorithmTreeLabelProvider() {

	}

	public AlgorithmTreeLabelProvider(int style) {
		super(style);

	}
	
	//
	// This function is used to update the contents of the cells with only names of the algorithms
	//
	@Override
	 public void update(ViewerCell cell) {
	      Object element = cell.getElement();
	      StyledString text = new StyledString();
	      String algorithmName = ((IDetectionAlgorithm) element).getName();
	      text.append(algorithmName);
	      cell.setText(text.toString());
	      cell.setStyleRanges(text.getStyleRanges());
	      super.update(cell);

	    }
}
