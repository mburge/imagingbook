package Ch04_Histograms;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/*
 * This plugin demonstrates how to create and display a 
 * new byte image 
 */

public class Create_New_Image implements PlugInFilter {
	String title = null;

	public int setup(String arg, ImagePlus im) {
		title = im.getTitle();
		return DOES_8G + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		int w = 256; 
		int h = 100; 
		int[] hist = ip.getHistogram();
		
		ImageProcessor histIp = new ByteProcessor(w, h);
		histIp.setValue(255);	// white = 255
		histIp.fill();

		// draw the histogram values as black bars in histIp here, 
		// for example, using histIp.putpixel(u,v,0)
		// ...
		
		// display histogram:
		String hTitle = "Histogram of " + title;
		ImagePlus histIm = new ImagePlus(hTitle, histIp);
		histIm.show();
	}
}
