package face.authenticator;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;

public class FaceAuthImage{


	private IplImage iplImg;
	
	public FaceAuthImage(Object img)
	{
		if(img instanceof IplImage)
			this.iplImg = (IplImage)img;
	}	

	public Image getImageData()
	{
		if(iplImg != null)
		{
			Image image = bufferedImageToImage(iplImg.getBufferedImage());
			return image;
		}
		else
			return null;
	}


	private Image bufferedImageToImage( BufferedImage srcImage ) {		
		final PaletteData PALETTE_DATA = new PaletteData(0xFF0000, 0xFF00, 0xFF);
		ImageData swtImageData =
				new ImageData(srcImage.getWidth(), srcImage.getHeight(), 24, PALETTE_DATA);
		int scansize = (((srcImage.getWidth() * 3) + 3) * 4) / 4;
		WritableRaster alphaRaster = srcImage.getAlphaRaster();
		byte[] alphaBytes = new byte[srcImage.getWidth()];
		for (int y=0; y<srcImage.getHeight(); y++) {
			int[] buff = srcImage.getRGB(0, y, srcImage.getWidth(), 1, null, 0, scansize);
			swtImageData.setPixels(0, y, srcImage.getWidth(), buff, 0);
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
