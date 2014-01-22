package org.eclipse.linuxtools.tmf.totalads.ui;

import java.io.File;

import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfEvent;

/** Each Trace type parser should implement the functions of this interface and, in addition, a static function registerTraceTypeReader*/
public interface ITraceTypeReader {
	/** Returns a self created instance of the model**/
	public ITraceTypeReader createInstance();
	/** Gets the name of the model**/
	public String getName();
	public StringBuilder getTrace(File file) throws Exception;
	public void handleEvents(CtfTmfEvent event, StringBuilder traceBuffer);
}
