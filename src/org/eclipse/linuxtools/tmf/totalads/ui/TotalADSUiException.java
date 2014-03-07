package org.eclipse.linuxtools.tmf.totalads.ui;
/**
 * This class defines custom UI exceptions that are thrown when a user does not select the proper configurations 
 * @author Syed Shariyar Murtaza
 *
 */
public class TotalADSUiException extends Exception {

	public TotalADSUiException() {
		
	}

	public TotalADSUiException(String message) {
		super(message);
		
	}

	public TotalADSUiException(Throwable cause) {
		super(cause);
		
	}

	public TotalADSUiException(String message, Throwable cause) {
		super(message, cause);
		
	}

}
