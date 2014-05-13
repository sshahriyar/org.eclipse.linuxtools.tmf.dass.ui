/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.results;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * Launches a Results View
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 * 		   <p> Efraim Lopez </p>
 *
 */
public class ResultsView extends ViewPart {
	private ResultsAndFeedback results;
	public static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.ResultsView";
	
	
	/**
	 * Constructor
	 */
	public ResultsView() {
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		
		 results=new ResultsAndFeedback(parent, false);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		

	}
	
	/**
	 * Returns the instance of {@link ResultsAndFeedback}
	 * @return
	 */
	public ResultsAndFeedback getResultsAndFeddbackInstance(){
		
		return results;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose(){
		
	}
	
	
}
