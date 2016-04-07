package face.authenticator;

import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.CV_AA;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvRectangle;

import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_INTER_LINEAR;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvEqualizeHist;
import static org.bytedeco.javacpp.opencv_imgproc.cvResize;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;


public class FaceAuthDetector {
    
        public static int facesDetected;
        private static final int SCALE = 2;
        private static CvSeq seq; 
        private static IplImage face = new IplImage();
        protected static IplImage input;
        protected static int width, height;

	public static IplImage detect(IplImage src){
                

		final String XML_FILE = 
				"cascadetrainer/haarcascade_frontalface_default.xml";
		CvHaarClassifierCascade cascade = new 
				CvHaarClassifierCascade(cvLoad(XML_FILE));
	
               
                IplImage clone = src.clone();
                
                CvMemStorage storage = CvMemStorage.create();
                
		seq = cvHaarDetectObjects(
				src,
				cascade,
				storage,
				1.5,
				3,  
				1);

		cvClearMemStorage(storage);
                
		int total_Faces = seq.total();
                facesDetected = seq.total();

                for(int i = 0; i < total_Faces; i++){
			CvRect r = new CvRect(cvGetSeqElem(seq, i));
			cvRectangle (
					src,
					cvPoint(r.x(), r.y()),
					cvPoint(r.width() + r.x(), r.height() + r.y()),
					CvScalar.RED,
					2,
					CV_AA,
					0);
                        
                        cvSetImageROI(clone, r);
                        face = cvCreateImage(cvGetSize(clone), clone.depth(), clone.nChannels());;
                        cvCopy(clone, face, null);
		}

		return src;
	}
        public static int getFacesDetected(){
            return facesDetected;
        }
        public static IplImage getFace(){
            return face;
        }
        private static IplImage greyscaleImage(IplImage imageSrc){
        IplImage imageGrey;
        if (imageSrc.nChannels() == 3) {
            imageGrey = cvCreateImage( cvGetSize(imageSrc), IPL_DEPTH_8U, 1 );
            // Convert from RGB (actually it is BGR) to Greyscale.
            cvCvtColor( imageSrc, imageGrey, CV_BGR2GRAY );
        }
        else {
            // Just use the input image, since it is already Greyscale.
            imageGrey = imageSrc;
        }
        return imageGrey;
    }
        private static IplImage resizeImage(IplImage imageSrc, int width, int height){
         // Resize the image to be a consistent size, even if the aspect ratio changes.
         IplImage imageProcessed, imageGrey = greyscaleImage(imageSrc);
         imageProcessed = cvCreateImage(org.bytedeco.javacpp.opencv_core.cvSize(width, height), IPL_DEPTH_8U, 1);
        // Make the image a fixed size.
        // CV_INTER_CUBIC or CV_INTER_LINEAR is good for enlarging, and
        // CV_INTER_AREA is good for shrinking / decimation, but bad at enlarging.
         cvResize(imageGrey, imageProcessed, CV_INTER_LINEAR);
         return imageProcessed;
     }
     
        private static IplImage equalizeImage(IplImage imageSrc){
            // Give the image a standard brightness and contrast.
            cvEqualizeHist(imageSrc, imageSrc);
            return imageSrc;
     }
     
        public static IplImage processImage(IplImage img, int width, int height){
            img = greyscaleImage(img);
            img = resizeImage(img, width, height);
            //img = equalizeImage(img);
            return img;
     }
}
