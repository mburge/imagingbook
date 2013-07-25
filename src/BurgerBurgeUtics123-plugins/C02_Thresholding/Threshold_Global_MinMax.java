package C02_Thresholding;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * Demo plugin showing MinMax thresholding.
 * @author W. Burger
 * @version 2013/05/30
 */
public class Threshold_Global_MinMax implements PlugInFilter {

	private int theMin, theMax;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}
	
	public void run(ImageProcessor ip) {
		ByteProcessor bp = (ByteProcessor) ip;
		getMinMax(bp);
		double minVal = theMin; 
		double maxVal = theMax;
		getMinMax(bp);
		
		int q = (int) Math.rint((minVal + maxVal)/2);
	
		if (q < maxVal) {
			IJ.log("threshold = " + q);
			ip.threshold(q);
		}
		else {
			IJ.showMessage("no threshold found");
		}
	}
	
	void getMinMax(ByteProcessor bp) {
		int N = bp.getWidth() * bp.getHeight();
		theMin = bp.get(0);
		theMax = theMin;
		for (int i=1; i<N; i++) {
			int p = bp.get(i);
			if (p < theMin) 
				theMin = p;
			if (p > theMax)
				theMax = p;
		}
	}
}
