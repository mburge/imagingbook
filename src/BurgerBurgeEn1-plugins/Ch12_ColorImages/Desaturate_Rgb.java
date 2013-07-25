package Ch12_ColorImages;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Desaturate_Rgb implements PlugInFilter {
	static double sCol = 0.3; // color saturation factor

	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB;
	}
	
	public void run(ImageProcessor ip) { 
		//iterate over all pixels
		for (int v = 0; v < ip.getHeight(); v++) {
			for (int u = 0; u < ip.getWidth(); u++) {

				//get int-packed color pixel
				int c = ip.get(u, v);

				//extract RGB components from color pixel
				int r = (c & 0xff0000) >> 16;
				int g = (c & 0x00ff00) >> 8;
				int b = (c & 0x0000ff);

				//compute equivalent gray value
				double y = 0.299 * r + 0.587 * g + 0.114 * b;

				// linearly interpolate $(yyy) --> (rgb)
				r = (int) (y + sCol * (r - y));
				g = (int) (y + sCol * (g - y));
				b = (int) (y + sCol * (b - y));

				// reassemble color pixel
				c = ((r & 0xff)<<16) | ((g & 0xff)<<8) | b & 0xff; 
				ip.set(u, v, c);
			}
		}
	}

}
