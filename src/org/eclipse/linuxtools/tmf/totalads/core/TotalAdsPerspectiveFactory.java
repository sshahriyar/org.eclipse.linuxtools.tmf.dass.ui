/**
  * Copyright (C) 2012 - Concordia University, CANADA
  *
  * This program is free software; you can redistribute it and/or modify
  * it under the terms of the GNU General Public License, version 3 only,
  * as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful, but WITHOUT
  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
  * more details.
  *
  * You should have received a copy of the GNU General Public License along
  * with this program; if not, write to the Free Software Foundation, Inc.,
  * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
  */
package org.eclipse.linuxtools.tmf.totalads.core;


import org.eclipse.linuxtools.internal.lttng2.ui.views.control.ControlView;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.DiagnosisView;
import org.eclipse.linuxtools.tmf.totalads.ui.live.LiveResultsView;
import org.eclipse.linuxtools.tmf.totalads.ui.live.LiveMonitorView;
import org.eclipse.linuxtools.tmf.totalads.ui.modeling.ModelingView;
import org.eclipse.linuxtools.tmf.totalads.ui.models.DataModelsView;
import org.eclipse.linuxtools.tmf.totalads.ui.properties.PropertiesView;
import org.eclipse.linuxtools.tmf.totalads.ui.results.ResultsView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
/**
 * A simple implementation of {@link IPerspectiveFactory} that is used by the workbench
 * to produce a custom perspective for the plugin
 * 
 * @author  <p> Efraim J Lopez  efraimlopez@gmail.com </p>
 * 			<p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class TotalAdsPerspectiveFactory implements IPerspectiveFactory {
	
    
   
    private static final String PROJECT_VIEW_ID = IPageLayout.ID_PROJECT_EXPLORER;
    private static final String CONTROL_VIEW_ID = ControlView.ID;
   
    
    @Override
	public void createInitialLayout(IPageLayout layout) {
		
		layout.setEditorAreaVisible(false);
		//Create right folders
        IFolderLayout topRightFolder = layout.createFolder(
                "topRightFolder", IPageLayout.RIGHT, 0.80f,IPageLayout.ID_EDITOR_AREA); 
        topRightFolder.addView(DataModelsView.ID);
        
        IFolderLayout bottomRightFolder = layout.createFolder(
                "bottomRightFolder", IPageLayout.BOTTOM, 0.50f,"topRightFolder"); 
        bottomRightFolder.addView(PropertiesView.ID);
        //bottomRightFolder.addView(IPageLayout.ID_PROP_SHEET);
        
		// Create Left folders
        IFolderLayout topLeftFolder = layout.createFolder(
                "topLeftFolder", IPageLayout.LEFT, 0.20f, IPageLayout.ID_EDITOR_AREA); 
        topLeftFolder.addView(PROJECT_VIEW_ID);

        
        IFolderLayout bottomLeftFolder = layout.createFolder(
                "bottomLeftFolder", IPageLayout.BOTTOM, 0.70f, "topLeftFolder"); 
        bottomLeftFolder.addView(CONTROL_VIEW_ID);

        // Create the center folders
        IFolderLayout centerTopFolder = layout.createFolder(
                "centerTopFolder", IPageLayout.TOP, 0.70f, IPageLayout.ID_EDITOR_AREA); 
        centerTopFolder.addView(DiagnosisView.VIEW_ID);
        centerTopFolder.addView(ModelingView.VIEW_ID);
        centerTopFolder.addView(LiveMonitorView.VIEW_ID);
        
        IFolderLayout centerMiddleFolder = layout.createFolder(
                "centerMiddleFolder", IPageLayout.BOTTOM, 0.25f,"centerTopFolder"); 
        centerMiddleFolder.addView(ResultsView.VIEW_ID);
       centerMiddleFolder.addView(LiveResultsView.VIEW_ID);
        
        IFolderLayout centerBottomFolder = layout.createFolder(
                "centerBottomFolder", IPageLayout.BOTTOM, 0.70f,"centerMiddleFolder"); 
        centerBottomFolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
	}
    
   
	

}
