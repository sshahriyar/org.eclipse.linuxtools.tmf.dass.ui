package org.eclipse.linuxtools.tmf.totalads.ui;



import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Settings {
	Display display;
	Shell dialogShel;
	Label []lblOption;
	Text []txtOption;
	String []modelOptions;
	Button btnOK;
	Button btnCancel;
	//Composite composite;
	public Settings(String []options) throws TotalADSUIException {
		
		if (options.length % 2==1)
			 throw new TotalADSUIException("Options must be even: key and value pairs.");
		
		
		 display = Display.getDefault();
		 dialogShel = new Shell(display, SWT.BORDER | SWT.CLOSE | SWT.APPLICATION_MODAL|SWT.V_SCROLL);
		
		 dialogShel.setLayout(new GridLayout(4, false));
	
		 modelOptions=options;
		 Integer widgetsCount=options.length/2;
		 lblOption=new Label[widgetsCount];
		 txtOption=new Text[widgetsCount];
		  
		 for (int j=0; j<options.length; j++){
			 int idx=j/2;
			 if (j%2==0){
				 
				 lblOption[idx]=new Label(dialogShel, SWT.NONE);
				 lblOption[idx].setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,false,2,1));
				 lblOption[idx].setText(options[j]);
			 }
			 else{
				 txtOption[idx]=new Text(dialogShel, SWT.NONE);
				 txtOption[idx].setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,2,1));
				 txtOption[idx].setText(options[j]);
			 }
		 }
		 new Label(dialogShel, SWT.NONE);// add two empty labels for first two cells
		 new Label(dialogShel, SWT.NONE);
		 
		 
		 btnOK=new Button(dialogShel,SWT.NONE);
		 btnOK.setLayoutData(new GridData(SWT.RIGHT,SWT.BOTTOM,true,false,1,1));
		 btnOK.setText("      OK       ");
		 
		 btnCancel=new Button(dialogShel,SWT.NONE);
		 btnCancel.setLayoutData(new GridData(SWT.RIGHT,SWT.BOTTOM,false,false,1,1));
		 btnCancel.setText("   Cancel   ");
		 dialogShel.setSize(500, 150);
		 addListeners();
		 
	}
	/**
	 * Shows the modal form
	 */
	public void showForm(){
		dialogShel.open();
		while (!dialogShel.isDisposed()) {
		    if (!display.readAndDispatch()) {
		        display.sleep();
		    }
		}
		
	}// end function ShowForm
	
	/**
	 * Returns selected options 
	 * @return
	 */
	public String[] getOptions(){
		return modelOptions;
	}
	/**
     * Adding listeners to buttons 
     */
	private void addListeners(){
		btnOK.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				   int optionCount=-1;
				   for (int i=0; i<txtOption.length;i++){
					   optionCount=optionCount+2;
					   modelOptions[optionCount]=txtOption[i].getText();
				   }
				
					dialogShel.dispose();
				}
	 });
	 
	 
	 btnCancel.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseUp(MouseEvent e) {
					
					//modelOptions=null;
					dialogShel.dispose();
				}
	 });
	
	}
}
