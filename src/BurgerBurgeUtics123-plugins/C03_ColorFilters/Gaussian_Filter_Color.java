package C03_ColorFilters;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.filters.GaussianFilter;
import imagingbook.lib.filters.GenericFilter;
import imagingbook.lib.util.Enums;
import imagingbook.pub.colorimage.ColorStack;
import imagingbook.pub.colorimage.ColorStack.ColorStackType;

/**
 * This plugin performs a Gaussian filter in a user-selectable color space.
 * Demonstrates the use of a generic LinearFilter for Gaussian blurring 
 * (brute force, not separated).
 * @author W. Burger
 * @version 2013/05/30
 */
public class Gaussian_Filter_Color implements PlugInFilter {
	
	static double sigma = 3.0;
	static int nIterations = 1;
	static ColorStackType csType = ColorStackType.sRGB;
	
	ImagePlus imp = null;
	
    public int setup(String arg, ImagePlus imp) {
    	this.imp = imp;
        return DOES_RGB + NO_CHANGES;
    }

    public void run(ImageProcessor ip) {
    	if (!getParameters()) 
    		return;
    	ImagePlus colStack = ColorStack.createFrom(imp);
    	switch (csType) {
	    	case Lab : 	ColorStack.srgbToLab(colStack); break;
			case Luv: 	ColorStack.srgbToLuv(colStack); break;
			case RGB: 	ColorStack.srgbToRgb(colStack); break;
			case sRGB: 	break;
		default:
			IJ.error("Color space " + csType.name() + " not implemented!"); 
			return;
    	}
    	
    	GenericFilter filter = new GaussianFilter(sigma);
    	FloatProcessor[] processors = ColorStack.getProcessors(colStack);
    	
       	for (int k = 0; k < nIterations; k++) {
       		for (FloatProcessor fp : processors) {
       			filter.applyTo(fp);
       		}
    	}
       	
       	ColorStack.toSrgb(colStack);
       	colStack.setTitle(imp.getShortTitle() + "-filtered-" + csType.name());
       	ImagePlus result = ColorStack.toColorImage(colStack);
       	result.show();
    }
    
    boolean getParameters() {
    	String[] colorChoices = Enums.getEnumNames(ColorStackType.class);
		GenericDialog gd = new GenericDialog("Gaussian Filter");
		gd.addChoice("Color space", colorChoices, csType.name());
		gd.addNumericField("sigma", sigma, 1);
		gd.addNumericField("iterations", nIterations, 0);
		gd.showDialog();
		if(gd.wasCanceled())
			return false;
		sigma = gd.getNextNumber();
		csType = ColorStackType.valueOf(gd.getNextChoice());
		nIterations = (int)gd.getNextNumber();
		if (nIterations < 1) nIterations = 1;
		return true;
    }

}

