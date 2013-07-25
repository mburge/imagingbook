package xC_Appendix;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Plugin_1 implements PlugInFilter {
	ImagePlus im;
	public static final String HistKey = "HISTOGRAM"; 
	
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL + NO_CHANGES;}
		
	public void run(ImageProcessor ip) {
		int[] hist = ip.getHistogram();
		// add histogram to image properties:
		im.setProperty(HistKey, hist); 
	}
}
