package C03_ColorFilters;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.util.Enums;
import imagingbook.pub.colorimage.ColorStack;
import imagingbook.pub.colorimage.ColorStack.ColorStackType;
import imagingbook.pub.geometry.interpolators.PixelInterpolator.Method;
import imagingbook.pub.geometry.mappings.linear.Rotation;

/**
 * This plugin performs a Gaussian filter in a user-selectable color space.
 * Demonstrates the use of a generic LinearFilter for Gaussian blurring 
 * (brute force, not separated).
 * @author W. Burger
 * @version 2013/05/30
 */
public class Geometry_Rotate_Color implements PlugInFilter {
	
	static double angle = 15; // rotation angle (in degrees)
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
    	
    	Rotation map = new Rotation((2 * Math.PI * angle) / 360);
    	FloatProcessor[] processors = ColorStack.getProcessors(colStack);
  
   		for (FloatProcessor fp : processors) {
   			map.applyTo(fp, Method.Bilinear);
   		}
       	
       	ColorStack.toSrgb(colStack);
       	colStack.setTitle(imp.getShortTitle() + "-rotated-" + csType.name());
       	ImagePlus result = ColorStack.toColorImage(colStack);
       	result.show();
    }
    
    boolean getParameters() {
    	String[] colorChoices = Enums.getEnumNames(ColorStackType.class);
		GenericDialog gd = new GenericDialog("Gaussian Filter");
		gd.addChoice("Color space", colorChoices, csType.name());
	
		gd.addNumericField("rotation angle", angle, 0);
		gd.showDialog();
		if(gd.wasCanceled())
			return false;
		csType = ColorStackType.valueOf(gd.getNextChoice());
		angle = gd.getNextNumber();

		return true;
    }

}

