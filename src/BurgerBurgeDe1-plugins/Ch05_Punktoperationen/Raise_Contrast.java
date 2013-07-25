package Ch05_Punktoperationen;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Raise_Contrast implements PlugInFilter {

	public int setup(String arg, ImagePlus img) {
		return DOES_8G;
	}
    
	public void run(ImageProcessor ip) {
		int w = ip.getWidth();
		int h = ip.getHeight();

		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int a = (int) (ip.get(u, v) * 1.5 + 0.5);
				if (a > 255)
					a = 255; 		// clamp to max. value
				ip.set(u, v, a);
			}
		}
	}
	
}
