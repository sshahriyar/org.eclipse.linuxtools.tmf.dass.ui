package org.eclipse.linuxtools.tmf.totalads.ui.slidingwindow;

import java.io.Serializable;
import java.util.ArrayList;


public class Event implements Serializable {
	 String event; 
	 ArrayList<Event[]> branches= null;  
	 
	 /** constructor */
	 public Event() { }
	 
	 

}
