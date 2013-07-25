package C05_EdgePresFilters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.filters.GenericFilter;
import imagingbook.pub.edgepreservingfilters.NagaoMatsuyamaFilter;
import imagingbook.pub.edgepreservingfilters.NagaoMatsuyamaFilter.Parameters;


/**
 * This plugin implements a 5x5 Nagao-Matsuyama filter, as described in
 * NagaoMatsuyama (1979).
 * @author W. Burger
 * @version 2013/05/30
 */

public class Nagao_Matsuyama_Filter implements PlugInFilter {
	
	ImagePlus imp = null;
	
    public int setup(String arg, ImagePlus imp) {
    	this.imp = imp;
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
    	Parameters params = new Parameters();
    	if (!setParameters(params)) 
    		return;
    	GenericFilter filter = new NagaoMatsuyamaFilter(params);
    	filter.applyTo(ip);
    }
    
    boolean setParameters(Parameters params) {
		GenericDialog gd = new GenericDialog("5x5 Nagao-Matsuyama Filter");
		gd.addNumericField("Variance threshold", params.varThreshold, 0);
		gd.showDialog();
		if(gd.wasCanceled()) return false;
		params.varThreshold = Math.max(gd.getNextNumber(),0);
		return true;
    }
    
}

