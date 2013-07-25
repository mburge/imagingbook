package C05_EdgePresFilters;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.filters.GenericFilter;
import imagingbook.lib.math.VectorNorm;
import imagingbook.lib.math.VectorNorm.NormType;
import imagingbook.lib.util.Enums;
import imagingbook.pub.edgepreservingfilters.BilateralFilter;
import imagingbook.pub.edgepreservingfilters.BilateralFilter.Parameters;

/**
 * This plugin demonstrates the use of the BilateralFilter class.
 * @author W. Burger
 * @version 2013/05/30
 */
public class Bilateral_Filter implements PlugInFilter {
	
	public int setup(String arg0, ImagePlus imp) {
        return DOES_ALL;
	}
	
	public void run(ImageProcessor ip) {
		

		Class<?>[] classes = VectorNorm.class.getDeclaredClasses();
		for (Class<?> c : classes) {
			IJ.log(c.getName());
			IJ.log(c.getCanonicalName());
			IJ.log(c.getSimpleName());
		}

		
		
		Parameters params = new Parameters();
    	if (!getParameters(params, (ip instanceof ColorProcessor)))
    		return;
		GenericFilter filter = new BilateralFilter(params);
		filter.applyTo(ip);
	}

    boolean getParameters(Parameters params, boolean isColor) {
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


