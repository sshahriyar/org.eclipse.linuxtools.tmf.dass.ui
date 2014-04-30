/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.linuxtools.tmf.ui.views.TmfView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

/**
 * @author efraimlopez
 *
 */
public class ModelsView extends TmfView {
	
	public static final String ID = "org.eclipse.linuxtools.tmf.totalads.ModelsView";
	private ModelsTable anomaliesTable = null;
	
	public static class ModelsTable{
		TableViewer viewer = null;
		
		public ModelsTable(Composite parent){
			viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				      | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
			
			final Table table = viewer.getTable();
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			
			TableViewerColumn colAnomalyType = new TableViewerColumn(viewer, SWT.NONE);
			colAnomalyType.getColumn().setWidth(200);
			colAnomalyType.getColumn().setText("Id");
			colAnomalyType.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					Model model = (Model) element;
					return model.getId();
				}
				@Override
				public Image getImage(Object element) {
				  return null;
				}
			});
			
			TableViewerColumn colAnomalyDesc = new TableViewerColumn(viewer, SWT.NONE);
			colAnomalyDesc.getColumn().setWidth(200);
			colAnomalyDesc.getColumn().setText("Description");
			colAnomalyDesc.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					Model model = (Model) element;
					return model.getDescription();
				}
				@Override
				public Image getImage(Object element) {
				  return null;
				}
			}); 
			
			viewer.setContentProvider(new ModelTableContentProvider());			
			viewer.setInput(TotalAdsState.INSTANCE.getModels());
		}
	}
	
	public ModelsView() {
		super(ID);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		anomaliesTable = new ModelsTable(parent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
