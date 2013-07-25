package C05_EdgePresFilters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.util.Enums;
import imagingbook.pub.edgepreservingfilters.PeronaMalikFilter;
import imagingbook.pub.edgepreservingfilters.PeronaMalikFilter.ColorMode;
import imagingbook.pub.edgepreservingfilters.PeronaMalikFilter.Parameters;

/**
 * This plugin demonstrates the use of the PeronaMalikFilter class.
 * @author W. Burger
 * @version 2013/05/30
 */
public class Perona_Malik_Filter implements PlugInFilter {

	public int setup(String arg0, ImagePlus imp) {
		return DOES_ALL + DOES_STACKS;
	}

	public void run(ImageProcessor ip) {
		Parameters params = new Parameters();
		if (!setParameters(params, ip))
			return;
		PeronaMalikFilter filter = new PeronaMalikFilter(params);
		filter.applyTo(ip);
	}
	
	boolean setParameters(Parameters params, ImageProcessor ip) {
		boolean isColor = (ip instanceof ColorProcessor);
		GenericDialog gd = new GenericDialog("Anisotropic Diffusion Filter");
		gd.addNumericField("Number of iterations", params.iterations, 0);
		gd.addNumericField("Alpha (0,..,0.25)", params.alpha, 2);
		gd.addNumericField("K", params.kappa, 0);
		gd.addCheckbox("Smoother regions", params.smoothRegions);
		if (isColor) {
			gd.addChoice("Color method", Enums.getEnumNames(ColorMode.class), params.colorMode.name());
			gd.addCheckbox("Use linear RGB", params.useLinearRgb);
		}
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		params.iterations = (int) Math.max(gd.getNextNumber(), 1);
		params.alpha = (float) gd.getNextNumber();
		params.kappa = (float) gd.getNextNumber();
		params.smoothRegions = gd.getNextBoolean();
		if (isColor) {
			params.colorMode = ColorMode.valueOf(gd.getNextChoice());
			params.useLinearRgb = gd.getNextBoolean();
		}
		return true;
	}
}



