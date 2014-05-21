package org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders;

import org.eclipse.linuxtools.internal.lttng2.kernel.core.LttngStrings;
import org.eclipse.linuxtools.tmf.core.exceptions.TmfTraceException;
import org.eclipse.linuxtools.tmf.ctf.core.CtfIterator;
import org.eclipse.linuxtools.tmf.ctf.core.CtfTmfEvent;
import org.eclipse.linuxtools.tmf.ctf.core.CtfTmfTrace;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
/**
 * A trace iterator to read events in the trace
 * @author Syed Shariyar Murtaza
 *
 */
class CTFSystemCallIterator implements ITraceIterator {
    private CtfIterator fTraceIterator = null;
    private Boolean fIsDispose = false;
    private String fSyscall;
    private CtfTmfTrace fTrace;

    /**
     * Constructor to initialize the trace
     * @param filePath file Name
     * @throws TmfTraceException An exception during trace reading
     */
    public CTFSystemCallIterator(String filePath) throws TmfTraceException {
        fTrace = new CtfTmfTrace();
        fTrace.initTrace(null, filePath, CtfTmfEvent.class);
        fTraceIterator = fTrace.createIterator();
    }

    /**
     * Moves Iterator to the next event, and returns true if the iterator
     * can advance or false if the iterator cannot advance
     **/
    @Override
    public boolean advance() {
        boolean isAdvance = true;
        fSyscall = ""; //$NON-NLS-1$
        do {
            CtfTmfEvent event = fTraceIterator.getCurrentEvent();
            fSyscall = handleSysEntryEvent(event);
            isAdvance = fTraceIterator.advance();
        } while (fSyscall.isEmpty() && isAdvance);

        if (!isAdvance) {
            fIsDispose = true;
            fTrace.dispose();
        }

        return isAdvance;

    }

    /** Returns the event for the location of the iterator **/
    @Override
    public String getCurrentEvent() {
        return fSyscall;
    }

    /** Closes the iterator stream **/
    @Override
    public void close() {
        if (!fIsDispose) {
            fTrace.dispose();
        }
    }

    /**
     * Returns System Call
     *
     * @param event
     *            Event object of type CtfTmfEvent
     * @return Event as a String
     */
    private static String handleSysEntryEvent(CtfTmfEvent event) {
        String eventName = event.getType().getName();
        String systemCall = ""; //$NON-NLS-1$

        if (eventName.startsWith(LttngStrings.SYSCALL_PREFIX)) {
            systemCall = eventName.trim();
        }
        return systemCall;

    }

}
