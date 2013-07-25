package C02_Thresholding;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.threshold.global.GlobalThresholder;
import imagingbook.pub.threshold.global.QuantileThresholder;

/**
 * Demo plugin showing the use of the QuantileThresholder class.
 * @author W. Burger
 * @version 2013/05/30
 */
public class Threshold_Global_Quantile implements PlugInFilter {
	
	static double quantile = 0.5;

	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}
	
	public void run(ImageProcessor ip) {
		ByteProcessor bp = (ByteProcessor) ip;
		
		quantile = IJ.getNumber("Black quantile [0,1]", quantile);
		if (quantile < 0) quantile = 0;
		if (quantile > 1) quantile = 1;
		
		GlobalThresholder thr = new QuantileThresholder(quantile);
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
