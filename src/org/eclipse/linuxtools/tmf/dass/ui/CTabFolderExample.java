package org.eclipse.linuxtools.tmf.dass.ui;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;



public class CTabFolderExample {

    public static void main2(String[] args) {

        Display display = new Display();

        Shell shell = new Shell(display);



        shell.setLayout(new GridLayout(3,false));

        // SWT.BOTTOM to show at the bottom

        CTabFolder folder = new CTabFolder(shell, SWT.BOTTOM);

        GridData data = new GridData(SWT.FILL,   SWT.FILL, true, true, 3, 3);

        folder.setLayoutData(data);

        CTabItem cTabItem1 = new CTabItem(folder, SWT.NONE);

        cTabItem1.setText("Tab1");

        CTabItem cTabItem2 = new CTabItem(folder, SWT.NONE);

        cTabItem2.setText("Tab2");

        CTabItem cTabItem3 = new CTabItem(folder, SWT.NONE);

        cTabItem3.setText("Tab3");



        Text text = new Text(folder, SWT.BORDER);

        text.setText("Hello");

        cTabItem1.setControl(text);



        shell.pack();

        shell.open();

        while (!shell.isDisposed()) {

            if (!display.readAndDispatch()) {

                display.sleep();

            }

        }

    }
    
    

public static void main (String [] args) {
final int ITEM_COUNT = 10;
Display display = new Display ();
Shell shell = new Shell(display);
 shell.setLayout(new GridLayout()); // uncomment to use a layoutinstead
Table table = new Table(shell, SWT.NONE /*SWT.NO_SCROLL |
SWT.V_SCROLL*/);
table.setHeaderVisible(true);
table.setLinesVisible(true);
int desiredHeight = table.getItemHeight() * 5 + table.getHeaderHeight();
if (shell.getLayout() == null) { // <---
table.setSize(200,desiredHeight);
} else {
table.setLayoutData(new GridData(200, desiredHeight)); // assumes GridLayout
}
for (int i = 0; i < ITEM_COUNT; i++) {
new TableItem(table, SWT.NONE).setText("item " + i);
}
shell.open ();
while (!shell.isDisposed ()) {
if (!display.readAndDispatch ()) display.sleep ();
}
display.dispose ();
}

}
 