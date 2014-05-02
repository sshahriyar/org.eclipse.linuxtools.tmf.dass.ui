package org.eclipse.linuxtools.tmf.totalads.ui.io;

public class ProgressConsole {

	public ProgressConsole() {
		
	}

	
	private org.eclipse.ui.console.MessageConsole findConsole(String name) {
		org.eclipse.ui.console.ConsolePlugin plugin = org.eclipse.ui.console.ConsolePlugin.getDefault();
		org.eclipse.ui.console.IConsoleManager conMan = plugin.getConsoleManager();
		org.eclipse.ui.console.IConsole[] existing = conMan.getConsoles();
	   
		for (int i = 0; i < existing.length; i++)
	         if (name.equals(existing[i].getName()))
	            return (org.eclipse.ui.console.MessageConsole) existing[i];
	      //no console found, so create a new one
	      org.eclipse.ui.console.MessageConsole myConsole = new org.eclipse.ui.console.MessageConsole(name, null);
	      conMan.addConsoles(new org.eclipse.ui.console.IConsole[]{myConsole});
	      return myConsole;
	   }

}
