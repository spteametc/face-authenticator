package face.authenticator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core.CvFileStorage;
import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSize;
import org.bytedeco.javacpp.opencv_core.CvTermCriteria;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.helper.opencv_legacy.cvCalcEigenObjects;
import static org.bytedeco.javacpp.helper.opencv_legacy.cvEigenDecomposite;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_legacy.*;


public class FaceAuthFaceRecognizer {

	private static final Logger LOGGER = Logger.getLogger(FaceAuthFaceRecognizer.class.getName());
	/** the number of training faces */
	private int nTrainFaces = 0;
	/** the training face image array */
	IplImage[] trainingFaceImgArr;
	/** the test face image array */
	IplImage[] testFaceImgArr;
	/** the person number array */
	CvMat personNumTruthMat;
	/** the number of persons */
	int nPersons;
	/** the person names */
	final List<String> personNames = new ArrayList<String>();
	/** the number of eigenvalues */
	int nEigens = 0;
	/** eigenvectors */
	IplImage[] eigenVectArr;
	/** eigenvalues */
	CvMat eigenValMat;
	/** the average image */
	IplImage pAvgTrainImg;
	/** the projected training faces */
	CvMat projectedTrainFaceMat;


	public FaceAuthFaceRecognizer()
	{

	}

	private String floatPointerToString(final FloatPointer floatPointer) {
		final StringBuilder stringBuilder = new StringBuilder();
		boolean isFirst = true;
		stringBuilder.append('[');
		for (int i = 0; i < floatPointer.capacity(); i++) {
			if (isFirst) {
				isFirst = false;
			} else {
				stringBuilder.append(", ");
			}
			stringBuilder.append(floatPointer.get(i));
		}
		stringBuilder.append(']');

		return stringBuilder.toString();
	}



	public String oneChannelCvMatToString(final CvMat cvMat) {
		//Preconditions
		if (cvMat.channels() != 1) {
			throw new RuntimeException("illegal argument - CvMat must have one channel");
		}

		final int type = cvMat.type();
		StringBuilder s = new StringBuilder("[ ");
		for (int i = 0; i < cvMat.rows(); i++) {
			for (int j = 0; j < cvMat.cols(); j++) {
				if (type == CV_32FC1 || type == CV_32SC1) {
					s.append(cvMat.get(i, j));
				} else {
					throw new RuntimeException("illegal argument - CvMat must have one channel and type of float or signed integer");
				}
				if (j < cvMat.cols() - 1) {
					s.append(", ");
				}
			}
			if (i < cvMat.rows() - 1) {
				s.append("\n  ");
			}
		}
		s.append(" ]");
		return s.toString();
	}


	private void doPCA() {
		int i;
		CvTermCriteria calcLimit;
		CvSize faceImgSize = new CvSize();

		// set the number of eigenvalues to use
		nEigens = nTrainFaces - 1;

		LOGGER.info("allocating images for principal component analysis, using " + nEigens + (nEigens == 1 ? " eigenvalue" : " eigenvalues"));

		// allocate the eigenvector images
		faceImgSize.width(trainingFaceImgArr[0].width());
		faceImgSize.height(trainingFaceImgArr[0].height());
		eigenVectArr = new IplImage[nEigens];
		for (i = 0; i < nEigens; i++) {
			eigenVectArr[i] = cvCreateImage(
					faceImgSize, // size
					IPL_DEPTH_32F, // depth
					1); // channels
		}

		// allocate the eigenvalue array
		eigenValMat = cvCreateMat(
				1, // rows
				nEigens, // cols
				CV_32FC1); // type, 32-bit float, 1 channel

		// allocate the averaged image
		pAvgTrainImg = cvCreateImage(
				faceImgSize, // size
				IPL_DEPTH_32F, // depth
				1); // channels

		// set the PCA termination criterion
		calcLimit = cvTermCriteria(
				CV_TERMCRIT_ITER, // type
				nEigens, // max_iter
				1); // epsilon

		LOGGER.info("computing average image, eigenvalues and eigenvectors");
		// compute average image, eigenvalues, and eigenvectors
		cvCalcEigenObjects(
				nTrainFaces, // nObjects
				trainingFaceImgArr, // input
				eigenVectArr, // output
				CV_EIGOBJ_NO_CALLBACK, // ioFlags
				0, // ioBufSize
				null, // userData
				calcLimit,
				pAvgTrainImg, // avg
				eigenValMat.data_fl()); // eigVals

		LOGGER.info("normalizing the eigenvectors");
		cvNormalize(
				eigenValMat, // src (CvArr)
				eigenValMat, // dst (CvArr)
				1, // a
				0, // b
				CV_L1, // norm_type
				null); // mask
	}

	private IplImage[] loadFaceImgArray(final String filename) {
		IplImage[] faceImgArr;
		BufferedReader imgListFile;
		String imgFilename;
		int iFace = 0;
		int nFaces = 0;
		int i;
		try {
			// open the input file
			imgListFile = new BufferedReader(new FileReader(filename));

			// count the number of faces
			while (true) {
				final String line = imgListFile.readLine();
				if (line == null || line.isEmpty()) {
					break;
				}
				nFaces++;
			}
			LOGGER.info("nFaces: " + nFaces);
			imgListFile = new BufferedReader(new FileReader(filename));

			// allocate the face-image array and person number matrix
			faceImgArr = new IplImage[nFaces];
			personNumTruthMat = cvCreateMat(
					1, // rows
					nFaces, // cols
					CV_32SC1); // type, 32-bit unsigned, one channel

			// initialize the person number matrix - for ease of debugging
			for (int j1 = 0; j1 < nFaces; j1++) {
				personNumTruthMat.put(0, j1, 0);
			}

			personNames.clear();        // Make sure it starts as empty.
			nPersons = 0;

			// store the face images in an array
			for (iFace = 0; iFace < nFaces; iFace++) {
				String personName;
				String sPersonName;
				int personNumber;

				// read person number (beginning with 1), their name and the image filename.
				final String line = imgListFile.readLine();
				if (line.isEmpty()) {
					break;
				}
				final String[] tokens = line.split(" ");
				personNumber = Integer.parseInt(tokens[0]);
				personName = tokens[1];
				imgFilename = tokens[2];
				sPersonName = personName;
				LOGGER.info("Got " + iFace + " " + personNumber + " " + personName + " " + imgFilename);

				// Check if a new person is being loaded.
				if (personNumber > nPersons) {
					// Allocate memory for the extra person (or possibly multiple), using this new person's name.
					personNames.add(sPersonName);
					nPersons = personNumber;
					LOGGER.info("Got new person " + sPersonName + " -> nPersons = " + nPersons + " [" + personNames.size() + "]");
				}

				// Keep the data
				personNumTruthMat.put(
						0, // i
						iFace, // j
						personNumber); // v

				// load the face image
				faceImgArr[iFace] = cvLoadImage(
						imgFilename, // filename
						CV_LOAD_IMAGE_GRAYSCALE); // isColor

				if (faceImgArr[iFace] == null) {
					throw new RuntimeException("Can't load image from " + imgFilename);
				}
			}

			imgListFile.close();

		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		LOGGER.info("Data loaded from '" + filename + "': (" + nFaces + " images of " + nPersons + " people).");
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("People: ");
		if (nPersons > 0) {
			stringBuilder.append("<").append(personNames.get(0)).append(">");
		}
		for (i = 1; i < nPersons && i < personNames.size(); i++) {
			stringBuilder.append(", <").append(personNames.get(i)).append(">");
		}
		LOGGER.info(stringBuilder.toString());

		return faceImgArr;
	}

	private void storeTrainingData() {
		CvFileStorage fileStorage;
		int i;

		LOGGER.info("writing facedata.xml");

		// create a file-storage interface
		fileStorage = cvOpenFileStorage(
				"facedata.xml", // filename
				null, // memstorage
				CV_STORAGE_WRITE, // flags
				null); // encoding

		// Store the person names. Added by Shervin.
		cvWriteInt(
				fileStorage, // fs
				"nPersons", // name
				nPersons); // value

		for (i = 0; i < nPersons; i++) {
			String varname = "personName_" + (i + 1);
			cvWriteString(
					fileStorage, // fs
					varname, // name
					personNames.get(i), // string
					0); // quote
		}

		// store all the data
		cvWriteInt(
				fileStorage, // fs
				"nEigens", // name
				nEigens); // value

		cvWriteInt(
				fileStorage, // fs
				"nTrainFaces", // name
				nTrainFaces); // value

		cvWrite(
				fileStorage, // fs
				"trainPersonNumMat", // name
				personNumTruthMat); // value

		cvWrite(
				fileStorage, // fs
				"eigenValMat", // name
				eigenValMat); // value

		cvWrite(
				fileStorage, // fs
				"projectedTrainFaceMat", // name
				projectedTrainFaceMat);

		cvWrite(fileStorage, // fs
				"avgTrainImg", // name
				pAvgTrainImg); // value

		for (i = 0; i < nEigens; i++) {
			String varname = "eigenVect_" + i;
			cvWrite(
					fileStorage, // fs
					varname, // name
					eigenVectArr[i]); // value
		}

		// release the file-storage interface
		cvReleaseFileStorage(fileStorage);
	}

	private IplImage convertFloatImageToUcharImage(IplImage srcImg) {
		IplImage dstImg;
		if ((srcImg != null) && (srcImg.width() > 0 && srcImg.height() > 0)) {
			// Spread the 32bit floating point pixels to fit within 8bit pixel range.
			double[] minVal = new double[1];
			double[] maxVal = new double[1];
			cvMinMaxLoc(srcImg, minVal, maxVal);
			// Deal with NaN and extreme values, since the DFT seems to give some NaN results.
			if (minVal[0] < -1e30) {
				minVal[0] = -1e30;
			}
			if (maxVal[0] > 1e30) {
				maxVal[0] = 1e30;
			}
			if (maxVal[0] - minVal[0] == 0.0f) {
				maxVal[0] = minVal[0] + 0.001;  // remove potential divide by zero errors.
			}                        // Convert the format
			dstImg = cvCreateImage(cvSize(srcImg.width(), srcImg.height()), 8, 1);
			cvConvertScale(srcImg, dstImg, 255.0 / (maxVal[0] - minVal[0]), -minVal[0] * 255.0 / (maxVal[0] - minVal[0]));
			return dstImg;
		}
		return null;
	}


	private void storeEigenfaceImages() {
		// Store the average image to a file
		LOGGER.info("Saving the image of the average face as 'out_averageImage.bmp'");
		cvSaveImage("out_averageImage.bmp", pAvgTrainImg);

		// Create a large image made of many eigenface images.
		// Must also convert each eigenface image to a normal 8-bit UCHAR image instead of a 32-bit float image.
		LOGGER.info("Saving the " + nEigens + " eigenvector images as 'out_eigenfaces.bmp'");

		if (nEigens > 0) {
			// Put all the eigenfaces next to each other.
			int COLUMNS = 8;        // Put upto 8 images on a row.
			int nCols = Math.min(nEigens, COLUMNS);
			int nRows = 1 + (nEigens / COLUMNS);        // Put the rest on new rows.
			int w = eigenVectArr[0].width();
			int h = eigenVectArr[0].height();
			CvSize size = cvSize(nCols * w, nRows * h);
			final IplImage bigImg = cvCreateImage(
					size,
					IPL_DEPTH_8U, // depth, 8-bit Greyscale UCHAR image
					1);        // channels
			for (int i = 0; i < nEigens; i++) {
				// Get the eigenface image.
				IplImage byteImg = convertFloatImageToUcharImage(eigenVectArr[i]);
				// Paste it into the correct position.
				int x = w * (i % COLUMNS);
				int y = h * (i / COLUMNS);
				CvRect ROI = cvRect(x, y, w, h);
				cvSetImageROI(
						bigImg, // image
						ROI); // rect
				cvCopy(
						byteImg, // src
						bigImg, // dst
						null); // mask
				cvResetImageROI(bigImg);
				cvReleaseImage(byteImg);
			}
			cvSaveImage(
					"out_eigenfaces.bmp", // filename
					bigImg); // image
			cvReleaseImage(bigImg);
		}
	}


	public void learn(final String trainingFileName) {
		int i;

		// load training data
		LOGGER.info("===========================================");
		LOGGER.info("Loading the training images in " + trainingFileName);
		trainingFaceImgArr = loadFaceImgArray(trainingFileName);
		nTrainFaces = trainingFaceImgArr.length;
		LOGGER.info("Got " + nTrainFaces + " training images");
		if (nTrainFaces < 3) {
			LOGGER.severe("Need 3 or more training faces\n"
					+ "Input file contains only " + nTrainFaces);
			return;
		}

		// do Principal Component Analysis on the training faces
		doPCA();

		LOGGER.info("projecting the training images onto the PCA subspace");
		// project the training images onto the PCA subspace
		projectedTrainFaceMat = cvCreateMat(
				nTrainFaces, // rows
				nEigens, // cols
				CV_32FC1); // type, 32-bit float, 1 channel

		// initialize the training face matrix - for ease of debugging
		for (int i1 = 0; i1 < nTrainFaces; i1++) {
			for (int j1 = 0; j1 < nEigens; j1++) {
				projectedTrainFaceMat.put(i1, j1, 0.0);
			}
		}

		LOGGER.info("created projectedTrainFaceMat with " + nTrainFaces + " (nTrainFaces) rows and " + nEigens + " (nEigens) columns");
		if (nTrainFaces < 5) {
			LOGGER.info("projectedTrainFaceMat contents:\n" + oneChannelCvMatToString(projectedTrainFaceMat));
		}

		final FloatPointer floatPointer = new FloatPointer(nEigens);
		for (i = 0; i < nTrainFaces; i++) {
			cvEigenDecomposite(
					trainingFaceImgArr[i], // obj
					nEigens, // nEigObjs
					eigenVectArr, // eigInput (Pointer)
					0, // ioFlags
					null, // userData (Pointer)
					pAvgTrainImg, // avg
					floatPointer); // coeffs (FloatPointer)

			if (nTrainFaces < 5) {
				LOGGER.info("floatPointer: " + floatPointerToString(floatPointer));
			}
			for (int j1 = 0; j1 < nEigens; j1++) {
				projectedTrainFaceMat.put(i, j1, floatPointer.get(j1));
			}
		}
		if (nTrainFaces < 5) {
			LOGGER.info("projectedTrainFaceMat after cvEigenDecomposite:\n" + projectedTrainFaceMat);
		}

		// store the recognition data as an xml file
		storeTrainingData();

		// Save all the eigenvectors as images, so that they can be checked.
		storeEigenfaceImages();
	}

}
