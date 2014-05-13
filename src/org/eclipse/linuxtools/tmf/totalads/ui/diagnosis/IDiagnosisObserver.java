/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.diagnosis;

import org.eclipse.linuxtools.tmf.totalads.ui.results.ResultsAndFeedback;

/**
 *
 * Observer interface to be implemented by the {@link DiagnosisView} so that it could be updated by the 
 * {@link DiagnosisPartListener} 
 *  @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 */
public interface IDiagnosisObserver {
	   /**
	    * Updates the Diagnosis observer
	    */
       public void update(ResultsAndFeedback results);
       

}
