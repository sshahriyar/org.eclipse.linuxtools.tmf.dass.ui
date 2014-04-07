package org.eclipse.linuxtools.tmf.totalads.core;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class TMFTotalADSPerspectiveFactory implements IPerspectiveFactory {
	private static final String VIEW_ID ="org.eclipse.linuxtools.tmf.totalads.ui.ADS01";
	       
  private static final String BOTTOM = "bottom";
	public TMFTotalADSPerspectiveFactory() {
		
	}

	@Override
	public void createInitialLayout(IPageLayout layout) {
		 
		layout.addView(IPageLayout.ID_OUTLINE,IPageLayout.LEFT,0.30f,
					layout.getEditorArea());
		IFolderLayout bot = layout.createFolder(BOTTOM,IPageLayout.BOTTOM,0.76f,
					layout.getEditorArea());
			bot.addView(VIEW_ID);

		// layout.createFolder("left", IPageLayout.LEFT, 0.2f, IPageLayout.ID_EDITOR_AREA);;
	      //layout.createFolder("right", IPageLayout.RIGHT, 0.6f, IPageLayout.ID_EDITOR_AREA);;
	      //layout.createFolder("bottom", IPageLayout.BOTTOM, 0.8f, IPageLayout.ID_EDITOR_AREA);;
	      //layout.createFolder("top", IPageLayout.TOP, 0.6f, IPageLayout.ID_EDITOR_AREA);;

	}

}
