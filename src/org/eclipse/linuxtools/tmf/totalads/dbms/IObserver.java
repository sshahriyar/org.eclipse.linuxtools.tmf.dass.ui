package org.eclipse.linuxtools.tmf.totalads.dbms;
/**
 * 
 * @author Syed Shriyar Murtaza
 * Onserver interface to call functions across classes
 */
public interface IObserver {
	   /**
	    * Updates the observer
	    */
       public void update();
       /**
        * Provides the information as text when updating
        *
         public void update(String information);*/

}
