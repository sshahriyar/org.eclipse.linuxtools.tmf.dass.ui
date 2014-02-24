package org.eclipse.linuxtools.tmf.totalads.ui;
/** Iterator to move through the trace and read events one by one without loading the whole trace in memory **/
public interface ITraceIterator {
	/** Moves Iterator to the next event, and returns true if the iterator can advance or false if the iterator cannot advance **/ 
	public boolean advance();
	/** Returns an event for the location of the iterator **/ 
	public String getCurrentEvent();
	/** Closes the iterator stream **/
	public void close();

}
