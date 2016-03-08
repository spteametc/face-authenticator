package face.authenticator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.swt.graphics.ImageData;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

public class FaceAuthMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello World !");
		
		FaceAuthGUI gui = new FaceAuthGUI();
		gui.init("WINDOW NAME");
		gui.open();
		
		FaceAuthData db = new FaceAuthData();
		db.init();
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
		System.out.println("mat = " + mat.dump());

		captureImage();
		System.out.println("First change !");
		
	}


	public static void captureImage()
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

		//camera.grab();
		//System.out.println("Frame Grabbed");
		//camera.retrieve(frame);
		//System.out.println("Frame Decoded");

		camera.read(frame);
		System.out.println("Frame Obtained");

		/* No difference
		    camera.release();
		 */

		Imgcodecs.imwrite("captured.jpg", frame);

		try {
			toImageData(frame);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Captured Frame Width " + frame.width());  

	}




	public static ImageData toImageData(Mat img) throws IOException
	{

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] data;
		for(int i = 0; i < img.rows(); i ++)
		{
			for( int j = 0; j < img.cols(); j ++)
			{
				data = new byte[3];
				img.get(i, j, data);				
				buffer.write(data);	

				//buffer.put(data);
			}
		}

		System.out.println("Buffer: " + buffer.size());
		//InputStream is = new ByteArrayInputStream(buffer.toByteArray());
		//ImageData image = new ImageData(is);
		return null;
	}

}
