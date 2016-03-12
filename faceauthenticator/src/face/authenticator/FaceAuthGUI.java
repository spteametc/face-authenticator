package face.authenticator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class FaceAuthGUI implements PaintListener{

	private Display disp;
	private Shell mainWindow;
	private Canvas canvas;
	private Image image;
	private static FaceAuthGUI gui = new FaceAuthGUI();

	private FaceAuthGUI()
	{
		disp = Display.getDefault();
		mainWindow = new Shell(disp);
	}

	public static FaceAuthGUI getInstance()
	{
		return gui;
	}
	
	public void init()
	{
		disp = Display.getDefault();
		mainWindow = new Shell(disp);
		mainWindow.setLayout(new FormLayout());
		mainWindow.setSize(640, 480);
		canvas = new Canvas(mainWindow, SWT.BORDER);
		FormData fd_canvas = new FormData();
		fd_canvas.bottom = new FormAttachment(100);
		fd_canvas.top = new FormAttachment(0);
		fd_canvas.left = new FormAttachment(0);
		fd_canvas.right = new FormAttachment(0, 634);
		canvas.setLayoutData(fd_canvas);
		GridData gd_canvas = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 2);
		gd_canvas.heightHint = 480;
		gd_canvas.widthHint = 640;		
		canvas.addPaintListener(this);	
		mainWindow.open();	
	}

	public void setImage(FaceAuthImage img)
	{
		image = img.getImageData();
	}

	private void update()
	{	
		canvas.redraw();	
		mainWindow.update();
	}
	
	public void open()
	{		
		if(!mainWindow.isDisposed())
		{
			if (!disp.readAndDispatch())
				//disp.sleep();
			System.out.println("Updating");
			update();			
		}		
	}
	
	
	public void close()
	{
		mainWindow.dispose();
	}

	@Override
	public void paintControl(PaintEvent arg0) {
		// TODO Auto-generated method stub
		if(image != null)
			arg0.gc.drawImage(image, 0, 0);
	}
	
}
