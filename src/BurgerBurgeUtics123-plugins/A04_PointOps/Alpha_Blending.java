package A04_PointOps;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

public class Alpha_Blending implements PlugInFilter {
	
	static double alpha = 0.5;	// transparency of foreground image
	ImagePlus fgIm = null;				// fgIm = foreground image
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}	
	
	public void run(ImageProcessor bgIp) {	// bgIp = background image
		if(runDialog()) {
			ImageProcessor fgIp = fgIm.getProcessor().convertToByte(false);
			fgIp = fgIp.duplicate();
			fgIp.multiply(1-alpha);
			bgIp.multiply(alpha);
			bgIp.copyBits(fgIp,0,0,Blitter.ADD);
		}
	}	

	boolean runDialog() {
		// get list of open images
		int[] windowList = WindowManager.getIDList();
		if (windowList == null){
			IJ.noImage();
			return false;
		}
		// get image titles
		String[] windowTitles = new String[windowList.length];
		for (int i = 0; i < windowList.length; i++) {
			ImagePlus im = WindowManager.getImage(windowList[i]);
			if (im == null)
				windowTitles[i] = "untitled";
			else
				windowTitles[i] = im.getShortTitle();
		}
		// create dialog and show
		GenericDialog gd = new GenericDialog("Alpha Blending");
		gd.addChoice("Foreground image:", windowTitles, windowTitles[0]);
		gd.addNumericField("Alpha value [0..1]:", alpha, 2);
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		else {
			int fgIdx = gd.getNextChoiceIndex();
			fgIm = WindowManager.getImage(windowList[fgIdx]);
			alpha = gd.getNextNumber();
			return true;
		}
	}
}
