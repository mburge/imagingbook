package Ch14_DFT_2D;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.dft.Dft2d;

/** 
 * Computes the 2-dimensional (power-spectrum) DFT on a float image
 * of arbitrary size.
 */
public class DFT_2D implements PlugInFilter{

	static boolean center = true;    //center the resulting spectrum?
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G+NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		FloatProcessor ip2 = (FloatProcessor) ip.convertToFloat();
		Dft2d dft = new Dft2d(ip2,center);
		
		ImageProcessor ipP = dft.makePowerImage();
		ImagePlus win = new ImagePlus("DFT Power Spectrum (byte)",ipP);
		win.show();
	}

}
