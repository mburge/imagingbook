package C05_EdgePresFilters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.filters.GenericFilter;
import imagingbook.lib.math.VectorNorm.NormType;
import imagingbook.lib.util.Enums;
import imagingbook.pub.edgepreservingfilters.BilateralFilterSeparable;
import imagingbook.pub.edgepreservingfilters.BilateralFilter.Parameters;

public class Bilateral_Filter_Separable implements PlugInFilter {

	public int setup(String arg0, ImagePlus imp) {
        return DOES_ALL;
	}
	
	public void run(ImageProcessor ip) {
		Parameters params = new Parameters();
    	if (!getParameters(params, ip))
    		return;
		GenericFilter filter = new BilateralFilterSeparable(params);
		filter.applyTo(ip);
	}

    boolean getParameters(Parameters params, ImageProcessor ip) {
    	boolean isColor = (ip instanceof ColorProcessor);
		GenericDialog gd = new GenericDialog("Bilateral Filter");
		gd.addNumericField("Sigma (domain)", params.sigmaD, 1);
		gd.addNumericField("Sigma (range)", params.sigmaR, 1);
		if (isColor) {
			gd.addChoice("Color norm", Enums.getEnumNames(NormType.class), params.colorNormType.name());
		}
		gd.showDialog();
		if(gd.wasCanceled()) return false;
		params.sigmaD = Math.max(gd.getNextNumber(), 0.5);
		params.sigmaR = Math.max(gd.getNextNumber(), 1);
		if (isColor) {
			params.colorNormType = NormType.valueOf(gd.getNextChoice());
		}
		return true;
    }
    
}



