package Ch04_Histogramme;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Compute_Histogram implements PlugInFilter {

	public int setup(String arg, ImagePlus img) {
		return DOES_8G + NO_CHANGES; 
	}
    
	public void run(ImageProcessor ip) {
		int[] H = new int[256]; // histogram array
		int w = ip.getWidth();
		int h = ip.getHeight();

		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int i = ip.getPixel(u, v);
				H[i] = H[i] + 1;
			}
		}

		// ... histogram H[] can now be used
		IJ.showMessage("This plugin only calculates the histogram but does not show anything");
	}
}
