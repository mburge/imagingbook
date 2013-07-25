package C02_Thresholding;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.threshold.global.GlobalThresholder;
import imagingbook.pub.threshold.global.MedianThresholder;

/**
 * Demo plugin showing the use of the MedianThresholder class.
 * @author W. Burger
 * @version 2013/05/30
 */
public class Threshold_Global_Median implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}
	
	public void run(ImageProcessor ip) {
		ByteProcessor bp = (ByteProcessor) ip;
		
		GlobalThresholder thr = new MedianThresholder();
		int q = thr.getThreshold(bp);
		
		if (q >= 0) {
			IJ.log("threshold = " + q);
			ip.threshold(q);
		}
		else {
			IJ.showMessage("no threshold found");
		}
		

	}
}
