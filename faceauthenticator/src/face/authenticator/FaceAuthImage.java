package face.authenticator;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;
import org.opencv.core.Mat;

public class FaceAuthImage{
	
	private Mat img;
	
	public FaceAuthImage(Mat img)
	{
		this.img = img;
	}	
	
	public Image getImageData()
	{
		BufferedImage buffImage = matToBufferedImage(img);
		Image image = bufferedImageToImage(buffImage);
		return image;
	}
	
	private BufferedImage matToBufferedImage(Mat matrix) {
		BufferedImage image;
		long startTime = System.nanoTime();  
		int cols = matrix.cols();  
		int rows = matrix.rows();  
		int elemSize = (int)matrix.elemSize();  
		byte[] data = new byte[cols * rows * elemSize];  
		int type;  
		matrix.get(0, 0, data);  
		switch (matrix.channels()) {  
		case 1:  
			type = BufferedImage.TYPE_BYTE_GRAY;  
			break;  
		case 3:   
			type = BufferedImage.TYPE_3BYTE_BGR;  
			// bgr to rgb  
			byte b;  
			for(int i=0; i<data.length; i=i+3) {  
				b = data[i];  
				data[i] = data[i+2];  
				data[i+2] = b;  
			}  
			break;  
		default:  
			return null; // Error  
		}  
		image = new BufferedImage(cols, rows, type);  
		image.getRaster().setDataElements(0, 0, cols, rows, data);  

		long endTime = System.nanoTime();  
		System.out.println(String.format("Elapsed time: %.2f ms", (float)(endTime - startTime)/1000000));  
		return image; // Successful  
	}  
	
	private Image bufferedImageToImage( BufferedImage srcImage) {
		// We can force bitdepth to be 24 bit because BufferedImage getRGB allows us to always
		// retrieve 24 bit data regardless of source color depth.
		final PaletteData PALETTE_DATA = new PaletteData(0xFF0000, 0xFF00, 0xFF);
		ImageData swtImageData =
				new ImageData(srcImage.getWidth(), srcImage.getHeight(), 24, PALETTE_DATA);

		// ensure scansize is aligned on 32 bit.
		int scansize = (((srcImage.getWidth() * 3) + 3) * 4) / 4;

		WritableRaster alphaRaster = srcImage.getAlphaRaster();
		byte[] alphaBytes = new byte[srcImage.getWidth()];

		for (int y=0; y<srcImage.getHeight(); y++) {
			int[] buff = srcImage.getRGB(0, y, srcImage.getWidth(), 1, null, 0, scansize);
			swtImageData.setPixels(0, y, srcImage.getWidth(), buff, 0);

			// check for alpha channel
			if (alphaRaster != null) {
				int[] alpha = alphaRaster.getPixels(0, y, srcImage.getWidth(), 1, (int[])null);
				for (int i=0; i<srcImage.getWidth(); i++)
					alphaBytes[i] = (byte)alpha[i];
				swtImageData.setAlphas(0, y, srcImage.getWidth(), alphaBytes, 0);
			}
		}

		return new Image(Display.getCurrent(), swtImageData);
	}
	
	
	
	
	
	
	

}
