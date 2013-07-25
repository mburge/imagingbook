package C03_ColorFilters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.filters.GenericFilter;
import imagingbook.pub.colorfilters.ScalarMedianFilter;
import imagingbook.pub.colorfilters.ScalarMedianFilter.Parameters;

/**
 * This plugin applies a scalar median filter to all three planes
 * of a RGB color image.
 * @author W. Burger
 * @version 2013/05/30
 */
public class MedianFilter_Color_Scalar implements PlugInFilter {
	
	ImagePlus imp;
	
    public int setup(String arg, ImagePlus imp) {
    	this.imp = imp;
        return DOES_RGB;
    }

    public void run(ImageProcessor ip) {
    	Parameters params = new ScalarMedianFilter.Parameters();
    	if (!setParameters(params)) 
    		return;
    	GenericFilter filter = new ScalarMedianFilter(params);
    	filter.applyTo(ip);
    }
    
    boolean setParameters(Parameters params) {
		GenericDialog gd = new GenericDialog("Scalar Median Filter");
		gd.addNumericField("Radius", params.radius, 1);
		gd.showDialog();
		if(gd.wasCanceled()) return false;
		params.radius = Math.max(gd.getNextNumber(), 1);
		return true;
    }

}

