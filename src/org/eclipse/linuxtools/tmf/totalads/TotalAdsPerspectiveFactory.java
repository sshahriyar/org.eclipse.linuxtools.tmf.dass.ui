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
package org.eclipse.linuxtools.tmf.totalads;

import org.eclipse.linuxtools.internal.lttng2.kernel.ui.views.controlflow.ControlFlowView;
import org.eclipse.linuxtools.internal.lttng2.kernel.ui.views.resources.ResourcesView;
import org.eclipse.linuxtools.internal.lttng2.ui.views.control.ControlView;
import org.eclipse.linuxtools.tmf.totalads.core.TMFTotalADSView;
import org.eclipse.linuxtools.tmf.ui.views.histogram.HistogramView;
import org.eclipse.linuxtools.tmf.ui.views.statistics.TmfStatisticsView;
import org.eclipse.osgi.framework.internal.core.ConsoleManager;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.ConsolePlugin;
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
	
    // LTTng views
   // private static final String HISTOGRAM_VIEW_ID = HistogramView.ID;
    private static final String CONTROL_VIEW_ID = ControlView.ID;
  //  private static final String CONTROLFLOW_VIEW_ID = ControlFlowView.ID;
    //private static final String RESOURCES_VIEW_ID = ResourcesView.ID;
    //private static final String STATISTICS_VIEW_ID = TmfStatisticsView.ID;

    // Standard Eclipse views
    private static final String PROJECT_VIEW_ID = IPageLayout.ID_PROJECT_EXPLORER;
   // private static final String PROPERTIES_VIEW_ID = IPageLayout.ID_PROP_SHEET;
  //  private static final String BOOKMARKS_VIEW_ID = IPageLayout.ID_BOOKMARKS;

    @Override
	public void createInitialLayout(IPageLayout layout) {
		System.out.println("printing layout");
		
		layout.setEditorAreaVisible(false);
		//Create right folders
        IFolderLayout topRightFolder = layout.createFolder(
                "topRightFolder", IPageLayout.RIGHT, 0.80f,IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
        topRightFolder.addView(ModelsView.ID);
        
        IFolderLayout bottomRightFolder = layout.createFolder(
                "bottomRightFolder", IPageLayout.BOTTOM, 0.50f,"topRightFolder"); //$NON-NLS-1$
        bottomRightFolder.addView(GenericView.ID);
        //bottomRightFolder.addView(IPageLayout.ID_PROP_SHEET);
        
		// Create Left folders
        IFolderLayout topLeftFolder = layout.createFolder(
                "topLeftFolder", IPageLayout.LEFT, 0.20f, IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
        topLeftFolder.addView(PROJECT_VIEW_ID);

        
        IFolderLayout bottomLeftFolder = layout.createFolder(
                "bottomLeftFolder", IPageLayout.BOTTOM, 0.70f, "topLeftFolder"); //$NON-NLS-1$
        bottomLeftFolder.addView(CONTROL_VIEW_ID);

        // Create the center folders
        IFolderLayout centerTopFolder = layout.createFolder(
                "centerTopFolder", IPageLayout.TOP, 0.70f, IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
        centerTopFolder.addView(DiagnosisView.VIEW_ID);
        centerTopFolder.addView(ModelingView.VIEW_ID);
        
        IFolderLayout centerMiddleFolder = layout.createFolder(
                "centerMiddleFolder", IPageLayout.BOTTOM, 0.70f,"centerTopFolder"); //$NON-NLS-1$
        centerMiddleFolder.addView(AnomaliesView.ID);
        
        
        IFolderLayout centerBottomFolder = layout.createFolder(
                "centerBottomFolder", IPageLayout.BOTTOM, 0.50f, IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
        centerBottomFolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
	}

}
