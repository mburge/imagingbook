package C03_ColorFilters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.math.VectorNorm.NormType;
import imagingbook.lib.util.Enums;
import imagingbook.pub.colorfilters.VectorMedianFilter;
import imagingbook.pub.colorfilters.VectorMedianFilter.Parameters;

/**
 * This plugin applies a vector median filter to a RGB color image.
 * @author W. Burger
 * @version 2013/05/30
 */
public class MedianFilter_Color_Vector implements PlugInFilter {
	
	ImagePlus imp = null;
	
    public int setup(String arg, ImagePlus imp) {
    	this.imp = imp;
        return DOES_RGB;
    }

    public void run(ImageProcessor ip) {
    	Parameters params = new VectorMedianFilter.Parameters();
    	if (!setParameters(params)) return;

//    	VectorMedianFilter filter = new VectorMedianFilter(params.radius, params.distanceNorm);
    	VectorMedianFilter filter = new VectorMedianFilter(params);
    	filter.applyTo(ip);
    	
 //   	IJ.log("Pixels modified: " + filter.modifiedCount + " of " + (ip.getPixelCount()));
    }
    
    boolean setParameters(Parameters params) {
		GenericDialog gd = new GenericDialog("Median Filter");
		gd.addNumericField("Radius", params.radius, 1);
		String[] normChoices = Enums.getEnumNames(NormType.class);
		gd.addChoice("Distance norm", normChoices, params.distanceNorm.name());
		gd.addCheckbox("Mark modified pixels", params.markModifiedPixels);
		gd.addCheckbox("Show mask", params.showMask);
		
		gd.showDialog();
		if(gd.wasCanceled()) return false;
		params.radius = Math.max(gd.getNextNumber(),0.5);
		params.distanceNorm = NormType.valueOf(gd.getNextChoice());
		params.markModifiedPixels = gd.getNextBoolean();
		params.showMask = gd.getNextBoolean();
		return true;
    }
}

