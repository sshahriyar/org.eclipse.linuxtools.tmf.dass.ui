package org.eclipse.linuxtools.tmf.totalads.readers;

import java.io.File;

import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfEvent;

/** Each Trace type parser should implement the functions of this interface and, in addition, a static function registerTraceTypeReader*/
public interface ITraceTypeReader {
	/** Returns a self created instance of the model**/
	public ITraceTypeReader createInstance();
	/** Gets the name of the model**/
	public String getName();
	/** Returns the acronym of the Trace Reader; should only be three characters Long  */
	public String getAcronym();
	/** Gets a trace from a file */
	public ITraceIterator getTraceIterator(File file) throws Exception;

}
