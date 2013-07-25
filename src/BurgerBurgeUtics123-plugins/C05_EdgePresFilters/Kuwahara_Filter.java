package C05_EdgePresFilters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.edgepreservingfilters.KuwaharaFilter;
import imagingbook.pub.edgepreservingfilters.KuwaharaFilter.Parameters;

/**
 * This plugin implements a Kuwahara-type filter, similar to the filter suggested in 
 * Tomita and Tsuji (1977). It structures the filter region into  five overlapping, 
 * square subregions of size (r+1) x (r+1). Unlike the original Kuwahara filter,
 * it includes a centered subregion.
 * @author W. Burger
 * @version 2013/05/30
 */
public class Kuwahara_Filter implements PlugInFilter {
	
	ImagePlus imp = null;
	
    public int setup(String arg, ImagePlus imp) {
    	this.imp = imp;
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
    	Parameters params = new Parameters();
    	if (!getParameters(params))
    		return;
    	KuwaharaFilter filter = new KuwaharaFilter(params);
    	filter.applyTo(ip);
    }
    
    boolean getParameters(Parameters params) {
		GenericDialog gd = new GenericDialog("Median Filter");
		gd.addNumericField("Radius (>1)", params.radius, 0);
		gd.addNumericField("Variance threshold", params.tsigma, 0);
		gd.showDialog();
		if(gd.wasCanceled()) 
			return false;
		params.radius = (int) Math.max(gd.getNextNumber(),1);
		params.tsigma = Math.max(gd.getNextNumber(),0);
		return true;
    }
}

