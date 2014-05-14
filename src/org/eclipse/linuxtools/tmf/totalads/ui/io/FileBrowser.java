/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/

package org.eclipse.linuxtools.tmf.totalads.ui.io;

//import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.KeyEvent;
/**
 * This class takes care of the browsing capabilities of files on a hard disk
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p> 
 *
 */
public class FileBrowser {
	private Button btnTraceBrowser;
	private Composite parent;
	private Text txtPath;
	//private Text txtTraceID;//,txtTraceSource,txtTraceCounter;
	
	/**
	 * Constructor
	 * @param parent Composite object
	 * @param txtBox Textbox object
	 * @param gridData GriaData Layout
	 */
	public FileBrowser(Composite parent, Text txtBox, GridData gridData ){
		this.txtPath=txtBox;
		this.parent=parent;
	    btnTraceBrowser =new Button(parent, SWT.NONE);
		btnTraceBrowser.setLayoutData(gridData);
		btnTraceBrowser.setText("Browse...");
		btnTraceBrowser.addMouseListener(new MouseUpEvent());
		btnTraceBrowser.addKeyListener(new KeyPressEvent());
				
	}
	/**
	 * Constructor
	 * @param parent Composite
	 * @param gridData Griddata Layout
	 */
	public FileBrowser(Composite parent, GridData gridData ){
		
		this.parent=parent;
	    btnTraceBrowser =new Button(parent, SWT.NONE);
		btnTraceBrowser.setLayoutData(gridData);
		btnTraceBrowser.setText("Browse Traces");
		btnTraceBrowser.addMouseListener(new MouseUpEvent());
		btnTraceBrowser.addKeyListener(new KeyPressEvent());
				
	}
	/**
	 * Sets the trace  path text box to a local variable, which is updated when a user clicks browse
	 * @param text 
	 */
	public void setTextBox(Text text){
		this.txtPath=text;
	}
	/**
	 * Disables the Browse button
	 */
	public void disableBrowsing(){
		this.btnTraceBrowser.setEnabled(false);
	}
    /*
     * Enables the Browse button
     */
	public void enableBrowsing(){
		this.btnTraceBrowser.setEnabled(true);
	}
	
    /**
     * Method to open file dialog box
     */
    private void fileDialogBox(){
        FileDialog fD = new FileDialog(parent.getShell(), SWT.OPEN);
       // DirectoryDialog dD=new DirectoryDialog(parent.getShell());
        fD.setText("Open");
        if (!txtPath.getText().isEmpty())
         fD.setFilterPath(txtPath.getText());
       // String[] filterExt = { "*.txt", "*.doc", ".rtf", "*.*" };
        //fd.setFilterExtensions(filterExt);
        //path.delete(0, path.length());
        //path.append(fd.open());
       String path= fD.open();
       
       if (path!=null)
    	   this.txtPath.setText(path);
    	   
       
        
    }
	
	/////////////////////////////////////////////////////////////////////////////////////
	//
	//Inner classes (event handlers) for listeners of GUI elements (widgets)
	//
	///////////////////////////////////////////////////////////////////////////////////
	/** Inner class for mouse up event on the button  */
	private class MouseUpEvent extends MouseAdapter {
		@Override
		public void mouseUp(MouseEvent e) {
		fileDialogBox();
		}
	}
	
	/**Inner class for name press event on the button     */
	private class KeyPressEvent extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			fileDialogBox();
		}
	}

  
	
}
