package face.authenticator;

import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

public class FaceAuthMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello World !");

		

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
		System.out.println("mat = " + mat.dump());

		try {
		
			Mat img = captureImage();
			FaceAuthImage imageObj = new FaceAuthImage(img);
			FaceAuthGUI gui = new FaceAuthGUI();
			gui.init(imageObj);
			gui.open();

			FaceAuthData db = new FaceAuthData();
			db.init();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("First change !");

		
		
	}

	
	

	public static Mat captureImage() throws FileNotFoundException
	{
		//System.out.println("Hello, OpenCV");
		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		VideoCapture camera = new VideoCapture(0);
		camera.open(0); //Useless
		if(!camera.isOpened()){
			System.out.println("Camera Error");
		}
		else{
			System.out.println("Camera OK?");
		}

		Mat frame = new Mat();	

		camera.read(frame);
		System.out.println("Frame Obtained");
	
		
		
		//FaceAuthImage img = new FaceAuthImage();
		File file = new File("captured.jpg");
		InputStream stream = new FileInputStream(file);
		ImageData ideaImage = new ImageData(stream);
		System.out.println("Padding: " + ideaImage.scanlinePad);

		System.out.println("Captured Frame Width " + frame.width());  
		System.out.println("Test 1 Timo");
		System.out.println("Test 2 Timo");
		return frame;
	}	

}
