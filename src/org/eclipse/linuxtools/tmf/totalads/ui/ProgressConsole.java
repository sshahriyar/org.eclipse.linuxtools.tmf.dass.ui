package org.eclipse.linuxtools.tmf.totalads.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.sun.corba.se.impl.ior.NewObjectKeyTemplateBase;

public class ProgressConsole {
	private CLabel lblProgressConsole;
	private Text txtAnomaliesProgress;
	private int MAX_TEXT_SIZE=20000;
	
	public ProgressConsole(Composite comptbtmModeling){
	/**
	 * Progress Console
	 * 		
	 */
		lblProgressConsole = new CLabel(comptbtmModeling, SWT.NONE);
		lblProgressConsole.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,false,4,1));
		lblProgressConsole.setText("Progress Console");
			
		
		txtAnomaliesProgress = new Text(comptbtmModeling, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL |SWT.MULTI);
		txtAnomaliesProgress.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,4,4));
		txtAnomaliesProgress.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtAnomaliesProgress.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		
	}
	
	/**
	 * checks text size
	 */
	private void checksTextSize(){
		String text=txtAnomaliesProgress.getText();
		
		if (text.length()>=MAX_TEXT_SIZE){
		
				int newLineIndex=txtAnomaliesProgress.getText().indexOf("\n");
				if (newLineIndex==-1)
					newLineIndex=20;//deltefirst 20 characters if there is no new line
				
				txtAnomaliesProgress.setText(text.substring(newLineIndex+1,text.length()));
		}
	}
	/**
	 *  Prints text in the text box
	 * @param txt
	 */
	
	public void printText(final String txt){
		
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				checksTextSize();		
				txtAnomaliesProgress.append(txt);
			
			}
		});
	}
	/**
	 *  Prints text in the text box with a new line
	 * @param txt
	 */
	
	public void printTextLn(final String txt){
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				checksTextSize();		
				txtAnomaliesProgress.append(txt);
				txtAnomaliesProgress.append("\n");
				
			}
		});
		
	}


	/**
	 * Prints new line in the text box
	 */
	public void printNewLine(){
			Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				checksTextSize();		
				txtAnomaliesProgress.append("\n");
				
			}
		});
	}
	/**
	 * clears the text box
	 */
	public void clearText(){
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
					txtAnomaliesProgress.setText("");
			}
		});
		
	}
}
