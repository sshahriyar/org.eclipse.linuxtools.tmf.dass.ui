/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads.ui.datamodels;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.linuxtools.tmf.ui.views.TmfView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;

/**
 * @author efraimlopez
 *
 */
public class DataModelsView extends TmfView {
	
	public static final String ID = "org.eclipse.linuxtools.tmf.totalads.ModelsView";
	private ModelsList modelListViewer = null;
	
	private  class ModelsList{
		TableViewer viewer = null;
		
		public ModelsList(Composite parent){
			viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL|SWT.CHECK
				      | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
			
			final Table table = viewer.getTable();
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			
			TableViewerColumn modelName = new TableViewerColumn(viewer, SWT.NONE);
			modelName.getColumn().setWidth(200);
			modelName.getColumn().setText("DataModel");
			modelName.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					DataModel model = (DataModel) element;
					return model.getId();
				}
				@Override
				public Image getImage(Object element) {
				  return null;
				}
			});
			
			TableViewerColumn algorithmName = new TableViewerColumn(viewer, SWT.NONE);
			algorithmName.getColumn().setWidth(200);
			algorithmName.getColumn().setText("Algorithm");
			algorithmName.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					DataModel model = (DataModel) element;
					return model.getDescription();
				}
				@Override
				public Image getImage(Object element) {
				  return null;
				}
			}); 
			
			viewer.setContentProvider(new DataModelTableContentProvider());			
			viewer.setInput(TotalAdsState.INSTANCE.getModels());
			// Registering viewer as a provider with Eclipse to monitor chnages 
			getSite().setSelectionProvider(viewer);
		}
	}

	public DataModelsView() {
		super(ID);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		modelListViewer = new ModelsList(parent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
