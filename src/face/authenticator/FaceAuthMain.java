package face.authenticator;

import static org.bytedeco.javacpp.opencv_core.*;

import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import javax.swing.ImageIcon;
import java.awt.image.*;


public class FaceAuthMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub	
		try {
			IplImage image, cropped;
                        BufferedImage img;

                        faceAuthGUI gui = new faceAuthGUI();
                        ImageIcon icon = new ImageIcon();
                        
                        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
			grabber.start();
			
                        gui.setVisible(true);
			
			while(true){		
				image = grabber.grab();
				image = FaceAuthDetector.detect(image);
                                //cropped = FaceAuthDetector.getFace();
                                //cropped = FaceAuthProcessImage.processImage(image, 200, 200);
                                img = image.getBufferedImage();
                                icon.setImage(img);
                                gui.jLabel1.setIcon(icon);
                                gui.repaint();
			}
			//gui.close();						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
