package face.authenticator;


import java.util.concurrent.ExecutionException;


import static org.bytedeco.javacpp.opencv_core.*;

import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameGrabber;




public class FaceAuthMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	
		try {
			OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
			grabber.start();
			IplImage image = grabber.getDelayedImage();

			FaceAuthData db = new FaceAuthData();
			db.init();

			FaceAuthGUI gui = FaceAuthGUI.getInstance();
			gui.init();

			while(true){		
				image=grabber.grab();
				image = FaceAuthDetector.detect(image);
				FaceAuthImage imageObj = new FaceAuthImage(image);				
				gui.setImage(imageObj);
				gui.open();
			}			
			//gui.close();						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
		

}
