/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.properties;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
/**
 * This class a mechanism to correctly display the contents of each cell in the table.
 * It actually formats the object into a displayable form
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
class ValueLabelProvider extends ColumnLabelProvider {
	/*
	 * Constructor
	 */
	public ValueLabelProvider() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		NameVal keyVal = (NameVal) element;
		return keyVal.getVal();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
	  return null;
	}

}
