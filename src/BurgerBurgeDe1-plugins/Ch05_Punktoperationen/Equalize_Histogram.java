package Ch05_Punktoperationen;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Equalize_Histogram implements PlugInFilter {

	public int setup(String arg, ImagePlus img) {
		return DOES_8G;
	}
    
	public void run(ImageProcessor ip) {
		int w = ip.getWidth();
		int h = ip.getHeight();
		int M = w * h; // total number of image pixels
		int K = 256; // number of intensity values

		// compute the cumulative histogram:
		int[] H = ip.getHistogram();
		for (int j = 1; j < H.length; j++) {
			H[j] = H[j - 1] + H[j];
		}

		// equalize the image:
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int a = ip.get(u, v);
				int b = H[a] * (K - 1) / M;
				ip.set(u, v, b);
			}
		}
	}
	
}
