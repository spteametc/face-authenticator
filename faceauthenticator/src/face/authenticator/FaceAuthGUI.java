package face.authenticator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class FaceAuthGUI {

	Display disp;
	Shell mainWindow;
	public FaceAuthGUI()
	{
		disp = Display.getDefault();
		mainWindow = new Shell(disp);
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void init(String name)
	{

		disp = Display.getDefault();
		mainWindow = new Shell(disp);
		mainWindow.setLayout(new FormLayout());

		Composite composite = new Composite(mainWindow, SWT.NONE);
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(0, 232);
		fd_composite.right = new FormAttachment(0, 301);
		fd_composite.top = new FormAttachment(0);
		fd_composite.left = new FormAttachment(0);
		composite.setLayoutData(fd_composite);
		composite.setLayout(new GridLayout(1, false));

		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.setText("New Button");
		

	}

	public void open()
	{
		mainWindow.open();
		while(!mainWindow.isDisposed())
		{
			if (!disp.readAndDispatch())
				disp.sleep();

		}
		mainWindow.dispose();
	}
}
