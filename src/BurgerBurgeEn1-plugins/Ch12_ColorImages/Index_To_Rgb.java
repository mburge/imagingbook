package Ch12_ColorImages;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.image.IndexColorModel;

public class Index_To_Rgb implements PlugInFilter {
	static final int R = 0, G = 1, B = 2;

	public void run(ImageProcessor ip) {
		int w = ip.getWidth();
		int h = ip.getHeight();
		
		//retrieve the lookup tables (maps) for R,G,B
		IndexColorModel icm = (IndexColorModel) ip.getColorModel(); 
		int mapSize = icm.getMapSize(); 
		byte[] Rmap = new byte[mapSize]; icm.getReds(Rmap);  
		byte[] Gmap = new byte[mapSize]; icm.getGreens(Gmap); 
		byte[] Bmap = new byte[mapSize]; icm.getBlues(Bmap);
		  
		//create new 24-bit RGB image
		ColorProcessor cp = new ColorProcessor(w,h);
		int[] RGB = new int[3];
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int idx = ip.getPixel(u, v);
				RGB[R] = Rmap[idx];
				RGB[G] = Gmap[idx];
				RGB[B] = Bmap[idx];
				cp.putPixel(u, v, RGB); 
			}
		}
		ImagePlus cwin = new ImagePlus("RGB Image",cp);
		cwin.show();
	}

	public int setup(String arg, ImagePlus imp) {
		return DOES_8C + NO_CHANGES;	//does not alter original image	
	}
}

