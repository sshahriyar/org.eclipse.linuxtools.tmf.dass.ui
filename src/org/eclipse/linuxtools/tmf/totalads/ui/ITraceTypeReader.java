package org.eclipse.linuxtools.tmf.totalads.ui;

import java.io.File;

import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfEvent;

/** Each Trace type parser should implement the functions of this interface and, in addition, a static function registerTraceTypeReader*/
public interface ITraceTypeReader {
	///** Returns a self created instance of the model**/
	//public ITraceTypeReader createInstance();
	/** Gets the name of the model**/
	public String getName();
	/** Gets a trace from a file */
	public StringBuilder getTrace(File file) throws Exception;
	/** Handle events directly from the TMF View if the trace is already loaded in TMF. This function does not require a
	 *  body from those trace readers which do not interact with TMF view*/
	public void handleEvents(CtfTmfEvent event, StringBuilder traceBuffer);
}
