package face.authenticator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

public class FaceAuthImageRegister {

	
	private int samples;
	private String location;
	private int counter = 0;
	public FaceAuthImageRegister(int samples, String location)
	{
		
		this.samples = samples;
		this.location = location;
	}
	
	
	
	public void writeImg(Image img)
	{
		if(!(counter == samples)){		
		  ImageLoader loader = new ImageLoader();
		  loader.data = new ImageData[] {img.getImageData()};	
		  String fileName = location + counter + ".jpeg";
		  loader.save(fileName, SWT.IMAGE_JPEG);
		  counter++;
		}
	}
	
	
}
