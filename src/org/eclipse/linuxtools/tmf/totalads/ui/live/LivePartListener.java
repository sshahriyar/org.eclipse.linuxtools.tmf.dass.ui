package org.eclipse.linuxtools.tmf.totalads.ui.live;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.linuxtools.tmf.totalads.ui.results.ResultsAndFeedback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
/**
 * Implements an Eclipse Part Listener for Diagnosis View
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class LivePartListener implements IPartListener {
	private List <ILiveObserver> fObservers;
	/**
	 * Constructor to create the part listener
	 */
	public LivePartListener() {
		fObservers=new ArrayList<>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void partActivated(IWorkbenchPart part) {
	try{
			if (part instanceof LiveMonitorView){
			    //Launching Results View if it is not opened
				IViewPart viewRes=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(LiveResultsView.VIEW_ID);
			  	LiveResultsView liveResultsView=(LiveResultsView)viewRes;
				notifyObservers(liveResultsView.getResults());

			}
	  } catch (PartInitException e) {

      	   MessageBox msgBox=new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),SWT.OK);
			if(e.getMessage()!=null){
				msgBox.setMessage(e.getMessage());
			} else {
                msgBox.setMessage("Unable to launch a view");
            }
			msgBox.open();
			Logger.getLogger(LivePartListener.class.getName()).log(Level.SEVERE,null,e);
	  }

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void partBroughtToTop(IWorkbenchPart part) {


	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void partClosed(IWorkbenchPart part) {

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void partDeactivated(IWorkbenchPart part) {


	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void partOpened(IWorkbenchPart part) {


	}

	/**
	 * Adds an observer
	 * @param observer
	 */
	public void addObserver(ILiveObserver observer){
		fObservers.add(observer);

	}

	/**
	 * Removes an observer
	 * @param observer
	 */
	public void removeObserver(ILiveObserver observer){
		fObservers.remove(observer);

	}
	/**
	 * Notifies the fObservers
	 * @param results
	 */
	private void notifyObservers(ResultsAndFeedback results){
		for (ILiveObserver ob: fObservers) {
            ob.update(results);
        }
	}


}
