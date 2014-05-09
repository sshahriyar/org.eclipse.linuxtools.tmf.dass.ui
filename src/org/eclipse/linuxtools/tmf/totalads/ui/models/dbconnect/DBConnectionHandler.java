package org.eclipse.linuxtools.tmf.totalads.ui.models.dbconnect;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.linuxtools.tmf.totalads.ui.models.create.CreateModelWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

public class DBConnectionHandler implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		

	}

	@Override
	public void dispose() {
		

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		WizardDialog wizardDialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
			      new DBConnectWizard());
	
		if (wizardDialog.open()==Window.OK){
			MessageBox msgBox= new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
					SWT.ICON_WORKING);
			msgBox.setMessage("Connection sucessful...");
			msgBox.open();
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		
		return true;
	}

	@Override
	public boolean isHandled() {
		
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		

	}

}
