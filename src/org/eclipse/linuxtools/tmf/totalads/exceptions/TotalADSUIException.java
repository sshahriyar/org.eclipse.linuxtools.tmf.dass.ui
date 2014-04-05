package org.eclipse.linuxtools.tmf.totalads.exceptions;
/**
 * This class defines custom UI exceptions that are thrown when a user does not select the proper configurations 
 * @author Syed Shariyar Murtaza
 *
 */
public class TotalADSUIException extends Exception {

	public TotalADSUIException() {
		
	}

	public TotalADSUIException(String message) {
		super(message);
		
	}

	public TotalADSUIException(Throwable cause) {
		super(cause);
		
	}

	public TotalADSUIException(String message, Throwable cause) {
		super(message, cause);
		
	}

}
