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
	Button btnTraceBrowser;
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
	    btnTraceBrowser =new Button(parent, SWT.NONE);
		btnTraceBrowser.setLayoutData(gridData);
		btnTraceBrowser.setText("Browse Traces");
		btnTraceBrowser.addMouseListener(new MouseUpEvent());
		btnTraceBrowser.addKeyListener(new KeyPressEvent());
				
	}
	/**
	 * 
	 * @param parent
	 * @param gridData
	 */
	public TraceBrowser(Composite parent, GridData gridData ){
		
		this.parent=parent;
	    btnTraceBrowser =new Button(parent, SWT.NONE);
		btnTraceBrowser.setLayoutData(gridData);
		btnTraceBrowser.setText("Browse Traces");
		btnTraceBrowser.addMouseListener(new MouseUpEvent());
		btnTraceBrowser.addKeyListener(new KeyPressEvent());
				
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
	 		directoryDialog();
	 	}
	 }
    
    /**inner class for key press event on the button
     */
    private class KeyPressEvent extends KeyAdapter{
	 	@Override
	 	public void keyPressed(KeyEvent e) {
	 		directoryDialog();
	 	}
    }
    /**
     * Method to open file dialog box
     */
    private void directoryDialog(){
        //FileDialog dD = new FileDialog(parent.getShell(), SWT.OPEN|SWT.MULTI|SWT.);
        DirectoryDialog dD=new DirectoryDialog(parent.getShell());
        dD.setText("Open");
       // dD.setFilterPath("/home");
       // String[] filterExt = { "*.txt", "*.doc", ".rtf", "*.*" };
        //fd.setFilterExtensions(filterExt);
        //path.delete(0, path.length());
        //path.append(fd.open());
       String path= dD.open();
       if (path!=null)
    	   this.txtBox.setText(path);
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
