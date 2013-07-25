package xC_Appendix;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Plugin_2 implements PlugInFilter {
	ImagePlus im;
	
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL;}

	public void run(ImageProcessor ip) {
		String key = Plugin_1.HistKey;	
		int[] hist = (int[]) im.getProperty(key); 
		if (hist == null){
			IJ.error("This image has no histogram");
		}
		else {
			// process histogram ...
		}
	}
}
