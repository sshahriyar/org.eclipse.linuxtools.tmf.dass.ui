package org.eclipse.linuxtools.tmf.totalads.ui;



import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.KeyEvent;
/**
 * 
 * @author Syed Shariyar Murtaza 
 *
 */
public class TraceBrowser {
	Button btnTrainingBrowse;
	Composite parent;
	Text txtBox;
	/**
	 * 
	 * @param parent
	 * @param txtBox
	 * @param gridData
	 */
	public TraceBrowser(Composite parent, Text txtBox, GridData gridData ){
		this.txtBox=txtBox;
		this.parent=parent;
	    btnTrainingBrowse =new Button(parent, SWT.NONE);
		btnTrainingBrowse.setLayoutData(gridData);
		btnTrainingBrowse.setText("Browse Traces");
		btnTrainingBrowse.addMouseListener(new MouseUpEvent());
		btnTrainingBrowse.addKeyListener(new KeyPressEvent());
				
	}
	/**
	 * 
	 * @param parent
	 * @param gridData
	 */
	public TraceBrowser(Composite parent, GridData gridData ){
		
		this.parent=parent;
	    btnTrainingBrowse =new Button(parent, SWT.NONE);
		btnTrainingBrowse.setLayoutData(gridData);
		btnTrainingBrowse.setText("Browse Traces");
		btnTrainingBrowse.addMouseListener(new MouseUpEvent());
		btnTrainingBrowse.addKeyListener(new KeyPressEvent());
				
	}
	/**
	 * 
	 * @param text
	 */
	public void setTextBox(Text text){
		this.txtBox=text;
	}
	
	/** inner class for mouse up event on the button
	  */
    private class MouseUpEvent extends MouseAdapter {
	 	@Override
	 	public void mouseUp(MouseEvent e) {
	 		fileDialog();
	 	}
	 }
    
    /**inner class for key press event on the button
     */
    private class KeyPressEvent extends KeyAdapter{
	 	@Override
	 	public void keyPressed(KeyEvent e) {
	 		fileDialog();
	 	}
    }
    /**
     * Method to open file dialog box
     */
    private void fileDialog(){
        //FileDialog dD = new FileDialog(parent.getShell(), SWT.OPEN|SWT.MULTI|SWT.);
        DirectoryDialog dD=new DirectoryDialog(parent.getShell());
        dD.setText("Open");
       // dD.setFilterPath("/home");
       // String[] filterExt = { "*.txt", "*.doc", ".rtf", "*.*" };
        //fd.setFilterExtensions(filterExt);
        //path.delete(0, path.length());
        //path.append(fd.open());
        this.txtBox.setText(dD.open());
        //System.out.println(selected);
    }
	
    
    // code for testing only
    /*
	public static void main(String args[]) {
		  org.eclipse.swt.widgets.Display display = new org.eclipse.swt.widgets.Display();
		  org.eclipse.swt.widgets.Shell shell = new org.eclipse.swt.widgets.Shell(display);
		  shell.setText("TreeExample");
		  
		  shell.setLayout(new org.eclipse.swt.layout.GridLayout(1, false));
		  TraceBrowser tb= new TraceBrowser(shell, new StringBuilder());
		 
		 Button btnNewButton = new Button(shell, SWT.NONE);
				 btnNewButton.setText("New Button");
		  shell.open();
		  while (!shell.isDisposed()) {
		    if (!display.readAndDispatch()) {
		      display.sleep();
		    }
		  }
		  display.dispose();
		}
		*/
	
}
