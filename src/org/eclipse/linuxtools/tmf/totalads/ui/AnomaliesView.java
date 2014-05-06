/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.ResultsAndFeedback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

/**
 * @author efraimlopez
 *
 */
public class AnomaliesView extends ViewPart {
	private ResultsAndFeedback results;
	public static final String ID = "org.eclipse.linuxtools.tmf.totalads.AnomaliesView";
	//private AnomaliesTable anomaliesTable = null;
	
	/*public static class AnomaliesTable{
		TableViewer viewer = null;
		
		public AnomaliesTable(Composite parent){
			viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				      | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
			
			final Table table = viewer.getTable();
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			
			TableViewerColumn colAnomalyTrace = new TableViewerColumn(viewer, SWT.NONE);
			colAnomalyTrace.getColumn().setWidth(200);
			colAnomalyTrace.getColumn().setText("Trace");
			colAnomalyTrace.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					Anomaly anomaly = (Anomaly) element;
					return anomaly.getTrace();
				}
				@Override
				public Image getImage(Object element) {
				  return null;
				}
			});
			
			TableViewerColumn colAnomalyType = new TableViewerColumn(viewer, SWT.NONE);
			colAnomalyType.getColumn().setWidth(200);
			colAnomalyType.getColumn().setText("Type");
			colAnomalyType.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					Anomaly anomaly = (Anomaly) element;
					return anomaly.getId();
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
					Anomaly anomaly = (Anomaly) element;
					return anomaly.getDescription();
				}
				@Override
				public Image getImage(Object element) {
				  return null;
				}
			}); 
			
			viewer.setContentProvider(new AnomaliesTableContentProvider());			
			viewer.setInput(TotalAdsState.INSTANCE.getAnomalies());
		}
	}*/
	
	public AnomaliesView() {
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		//anomaliesTable = new AnomaliesTable(parent);
		 results=new ResultsAndFeedback(parent, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	public ResultsAndFeedback getResultsAndFeddbackInstance(){
		//System.out.println("results");
		return results;
		
	}

}
