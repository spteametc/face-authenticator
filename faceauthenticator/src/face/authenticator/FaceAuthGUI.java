package face.authenticator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;

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
	public void init(FaceAuthImage img)
	{
		
		disp = Display.getDefault();
		mainWindow = new Shell(disp);
		mainWindow.setLayout(new FormLayout());
		mainWindow.setSize(640, 480);
		/*Composite composite = new Composite(mainWindow, SWT.NONE);
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(0, 232);
		fd_composite.right = new FormAttachment(0, 301);
		fd_composite.top = new FormAttachment(0);
		fd_composite.left = new FormAttachment(0);
		composite.setLayoutData(fd_composite);
		composite.setLayout(new GridLayout(2, false));*/
		
		Canvas canvas = new Canvas(mainWindow, SWT.BORDER);
		FormData fd_canvas = new FormData();
		fd_canvas.bottom = new FormAttachment(100);
		fd_canvas.top = new FormAttachment(0);
		fd_canvas.left = new FormAttachment(0);
		fd_canvas.right = new FormAttachment(0, 634);
		canvas.setLayoutData(fd_canvas);
		GridData gd_canvas = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 2);
		gd_canvas.heightHint = 480;
		gd_canvas.widthHint = 640;
		//canvas.setLayoutData(gd_canvas);
		
		final Image image = img.getImageData();
		canvas.addPaintListener(new PaintListener() { 
			
			@Override
			public void paintControl(PaintEvent e) {
				// TODO Auto-generated method stub
				 e.gc.drawImage(image, 0, 0);
			}
			});
		
		

		/*Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.setText("New Button");
		new Label(composite, SWT.NONE);
		*/

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
