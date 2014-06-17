package org.eclipse.linuxtools.tmf.totalads.ui.utilities;

import org.eclipse.linuxtools.internal.tmf.ui.dialogs.ManageCustomParsersDialog;
import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;
import org.eclipse.linuxtools.tmf.core.exceptions.TmfTraceException;
import org.eclipse.linuxtools.tmf.core.parsers.custom.CustomTxtEvent;
import org.eclipse.linuxtools.tmf.core.parsers.custom.CustomTxtTrace;
import org.eclipse.linuxtools.tmf.core.parsers.custom.CustomTxtTraceDefinition;
import org.eclipse.linuxtools.tmf.core.parsers.custom.CustomXmlEvent;
import org.eclipse.linuxtools.tmf.core.parsers.custom.CustomXmlTrace;
import org.eclipse.linuxtools.tmf.core.parsers.custom.CustomXmlTraceDefinition;
import org.eclipse.linuxtools.tmf.core.trace.ITmfContext;
import org.eclipse.swt.widgets.Display;

public class CustomParser {
	//org.eclipse.linuxtools.tmf.totalads.ui.utilities.CustomParser.loadTxtParsers();
	//org.eclipse.linuxtools.tmf.totalads.ui.utilities.CustomParser.loadXmlParsers();
	//org.eclipse.linuxtools.tmf.totalads.ui.utilities.CustomParser.openDialog();

	public static void main(String[] args) {
		loadTxtParsers();
		}

	public static void loadTxtParsers(){
		String path="/home/shary/totalads-attacks-normal-trace/customtxtparser.txt";
		CustomTxtTraceDefinition []cust= CustomTxtTraceDefinition.loadAll();
		for (int j=0; j<cust.length; j++) {
            System.out.println(cust[j].definitionName);
        }
		CustomTxtTrace txtTraceParser=new CustomTxtTrace(cust[0]);
		try {
			txtTraceParser.initTrace(null, path, CustomTxtEvent.class);
			ITmfContext ctx=txtTraceParser.seekEvent(0);
			ITmfEvent event;

			while ((event=txtTraceParser.getNext(ctx))!=null){
						//System.out.println(event.toString());
						//System.out.println(event.getType().getName());
						System.out.print(event.getContent().getField("0").getName());
						System.out.println(" " +event.getContent().getField("0").getValue());
			}
			txtTraceParser.dispose();
			/*ITmfEventRequest request = new TmfEventRequest(CustomTxtEvent.class, TmfTimeRange.ETERNITY, 0, 1) {

				@Override
				public void handleData(ITmfEvent data) {
					super.handleData(data);
					// do your stuff
				}
			};
			txtTraceParser.sendRequest(request);
			//request.waitForCompletion();
			*/
		} catch (TmfTraceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//txtTraceParser
		//txtTrace.
	}


	public static void loadXmlParsers(){
		String path="/home/shary/totalads-attacks-normal-trace/customxmlparser.txt";
		CustomXmlTraceDefinition []cust= CustomXmlTraceDefinition.loadAll();
		for (int j=0; j<cust.length; j++) {
            System.out.println(cust[j].definitionName);
        }
		CustomXmlTrace txtTraceParser=new CustomXmlTrace(cust[0]);
		try {
			txtTraceParser.initTrace(null, path, CustomXmlEvent.class);
			ITmfContext ctx=txtTraceParser.seekEvent(0);
			ITmfEvent event;

			while ((event=txtTraceParser.getNext(ctx))!=null){
						//System.out.println(event.toString());
						//System.out.println(event.getType().getName());
						System.out.print(event.getContent().getField("").getName());
						System.out.println(" "+event.getContent().getField("0").getValue());
			}
			txtTraceParser.dispose();
			/*ITmfEventRequest request = new TmfEventRequest(CustomTxtEvent.class, TmfTimeRange.ETERNITY, 0, 1) {

				@Override
				public void handleData(ITmfEvent data) {
					super.handleData(data);
					// do your stuff
				}
			};
			txtTraceParser.sendRequest(request);
			//request.waitForCompletion();
			*/
		} catch (TmfTraceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//txtTraceParser
		//txtTrace.

		}

	public static void openDialog(){
		 ManageCustomParsersDialog dialog = new ManageCustomParsersDialog(Display.getDefault().getActiveShell());
	        dialog.open();

	}

}
