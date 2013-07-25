package C02_Thresholding;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.threshold.BackgroundMode;
import imagingbook.pub.threshold.adaptive.SauvolaThresholder;
import imagingbook.pub.threshold.adaptive.SauvolaThresholder.Parameters;

/**
 * Demo plugin showing the use of the SauvolaThresholder class.
 * @author W. Burger
 * @version 2013/05/30
 */
public class Threshold_Adaptive_Sauvola implements PlugInFilter {
	

	private static boolean showThresholdImage = false;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		ByteProcessor I = (ByteProcessor) ip;
		
		Parameters params = new Parameters();
		if (!setParameters(params))
			return;
		
		SauvolaThresholder thr = new SauvolaThresholder(params);
		ByteProcessor Q = thr.getThreshold(I);
		thr.threshold(I, Q);
		
		if (showThresholdImage) {
			(new ImagePlus("Sauvola-Threshold", Q)).show();
		}
	}
	
	boolean setParameters(Parameters params) {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addNumericField("radius", params.radius, 0);
		gd.addNumericField("kappa", params.kappa, 2);
		gd.addNumericField("sigmaMax", params.sigmaMax, 2);
		gd.addCheckbox("bright background", (params.bgMode == BackgroundMode.BRIGHT));
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		params.radius = (int) gd.getNextNumber();
		params.kappa = gd.getNextNumber();
		params.sigmaMax = gd.getNextNumber();
		params.bgMode = (gd.getNextBoolean()) ? BackgroundMode.BRIGHT : BackgroundMode.DARK;
		return true;
	}
}
