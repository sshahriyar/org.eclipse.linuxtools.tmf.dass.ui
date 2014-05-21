/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.models.dbconnect;



import org.eclipse.jface.wizard.Wizard;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMSFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

/**
 * This class creates a wizard dialog box to connect to a database
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class DBConnectWizard extends Wizard {
	private  DBConfigurationPage basicConfigurationPage;
	private AdvanceDBConfigurationPage advanceDBConfigurationPage;

	/**
	 * Constructor
	 */
	public DBConnectWizard() {
		super();
		setNeedsProgressMonitor(true);

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getWindowTitle()
	 */
	@Override
	public String getWindowTitle() {
 	    return "Database Connection Wizard";
     }

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	 public void addPages() {
		basicConfigurationPage=new  DBConfigurationPage();
	    advanceDBConfigurationPage=new AdvanceDBConfigurationPage();
		addPage(basicConfigurationPage);
		addPage(advanceDBConfigurationPage);

	  }



	/*
	 * Creates a model in the database when the Finish button is clicked
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {

		String exception="";
		MessageBox msgBoxErr= new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
				SWT.ICON_ERROR);
		try {
			//DBMSFactory.INSTANCE.init();

			String portNum=basicConfigurationPage.getPort();
			String host=basicConfigurationPage.getHost();
			Integer port=Integer.parseInt(portNum);

			if (getContainer().getCurrentPage().equals(advanceDBConfigurationPage) ){

				String userName=advanceDBConfigurationPage.getUserName();
				String password=advanceDBConfigurationPage.getPassword();
				String database=advanceDBConfigurationPage.getDatabase();

				if (userName.isEmpty() || password.isEmpty() || database.isEmpty()){
					exception="Empty fields are not allowed";
				} else {
                    exception =DBMSFactory.INSTANCE.openConnection(host, port, userName, password, database);
                }
			} else {
                exception =DBMSFactory.INSTANCE.openConnection(host, port);
            }

		}catch (NumberFormatException ex){
			exception="Invalid port number";
			getContainer().showPage(basicConfigurationPage);

		}
		catch (Exception ex) {
			if (ex.getMessage()!=null) {
                exception=ex.getMessage();
            } else {
                exception="Unable to connect";
            }
		}

		if (!exception.isEmpty()){
				msgBoxErr.setMessage(exception);
				msgBoxErr.open();
				return false;
		}
        return true;


	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish(){
		return true;
	}
/*	@Override
	protected void createButtonsForButtonBar(Composite parent) {
	    super.createButtonsForButtonBar(parent);
	    Button OKButton = getButton(IDialogConstants.OK_ID);
	    OKButton.setText("Connect");
	}*/


}
