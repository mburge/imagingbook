package B09_DCT;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.dct.Dct2d;

/** 
 * Computes the 2-dimensional DCT on a float image
 * of arbitrary size. Be patient, this is quite slow!
 */
public class DCT_2D implements PlugInFilter{

	static boolean center = false;    //center the resulting spectrum?
	
	public int setup(String arg, ImagePlus im) {
		return DOES_8G + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		FloatProcessor ip2 = (FloatProcessor) ip.convertToFloat();
		Dct2d dct = new Dct2d(ip2, center);
		
		ImageProcessor ipP = dct.makePowerImage();
		ImagePlus win = new ImagePlus("DCT Spectrum (byte)", ipP);
		win.show();
	}

}
