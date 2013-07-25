package Ch12_ColorImages;

import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import java.awt.image.IndexColorModel;

public class Brighten_Index_Image implements PlugInFilter {

	public void run(ImageProcessor ip) {
		IndexColorModel icm = (IndexColorModel) ip.getColorModel(); 
		//IJ.write("Color Model=" + ip.getColorModel() + " " + ip.isColorLut());
	
		int pixBits = icm.getPixelSize(); 
		int mapSize = icm.getMapSize(); 
		
		//retrieve the current lookup tables (maps) for R,G,B
		byte[] Rmap = new byte[mapSize]; icm.getReds(Rmap);  
		byte[] Gmap = new byte[mapSize]; icm.getGreens(Gmap);  
		byte[] Bmap = new byte[mapSize]; icm.getBlues(Bmap);  
		
		//modify the lookup tables	
		for (int idx = 0; idx < mapSize; idx++){ 
			int r = 0xff & Rmap[idx];	//mask to treat as unsigned byte 
			int g = 0xff & Gmap[idx];
			int b = 0xff & Bmap[idx];   
			Rmap[idx] = (byte) Math.min(r + 10, 255); 
			Gmap[idx] = (byte) Math.min(g + 10, 255);
			Bmap[idx] = (byte) Math.min(b + 10, 255); 
		}
		//create a new color model and apply to the image
		IndexColorModel icm2 = new IndexColorModel(pixBits, mapSize, Rmap, Gmap,Bmap);  
		ip.setColorModel(icm2);
		
		WindowManager.getCurrentImage().updateAndDraw();
	}

	public int setup(String arg, ImagePlus imp) {
		return DOES_8G + DOES_8C;	// this plugin works on indexed color images 
	}
}

