package C03_ColorFilters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.math.VectorNorm.NormType;
import imagingbook.lib.util.Enums;
import imagingbook.pub.colorfilters.VectorMedianFilterSharpen;
import imagingbook.pub.colorfilters.VectorMedianFilterSharpen.Parameters;

/**
 * This plugin applies a sharpening vector median filter to a RGB color image.
 * @author W. Burger
 * @version 2013/05/30
 */
public class MedianFilter_Color_VectorSharpen implements PlugInFilter {
	
	ImagePlus imp = null;
	
    public int setup(String arg, ImagePlus imp) {
    	this.imp = imp;
        return DOES_RGB;
    }

    public void run(ImageProcessor ip) {
    	Parameters params = new VectorMedianFilterSharpen.Parameters();
    	if (!setParameters(params))
    		return;

    	VectorMedianFilterSharpen filter = new VectorMedianFilterSharpen(params);
    	filter.applyTo(ip);
    	
 //   	IJ.log("Pixels modified: " + filter.modifiedCount + " of " + (ip.getPixelCount()));
    }
    
    boolean setParameters(Parameters params) {
		GenericDialog gd = new GenericDialog("Median Filter");
		gd.addNumericField("Radius", params.radius, 1);
		gd.addNumericField("Sharpen", params.sharpen, 1);
		gd.addNumericField("Threshold", params.threshold, 1);
		String[] normChoices = Enums.getEnumNames(NormType.class);
		gd.addChoice("Distance norm", normChoices, params.distanceNorm.name());
		gd.addCheckbox("Mark modified pixels", params.markModifiedPixels);
		gd.addCheckbox("Show mask", params.showMask);
		
		gd.showDialog();
		if(gd.wasCanceled()) return false;
		params.radius = Math.max(gd.getNextNumber(),0.5);
		params.sharpen = gd.getNextNumber();
		params.threshold = gd.getNextNumber();
		params.distanceNorm = NormType.valueOf(gd.getNextChoice());
		params.markModifiedPixels = gd.getNextBoolean();
		params.showMask = gd.getNextBoolean();
		return true;
    }
}

