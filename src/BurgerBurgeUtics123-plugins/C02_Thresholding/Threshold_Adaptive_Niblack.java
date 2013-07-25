package C02_Thresholding;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.threshold.BackgroundMode;
import imagingbook.pub.threshold.adaptive.AdaptiveThresholder;
import imagingbook.pub.threshold.adaptive.NiblackThresholder;
import imagingbook.pub.threshold.adaptive.NiblackThresholder.Parameters;

/**
 * Demo plugin showing the use of the NiblackThresholder class.
 * @author W. Burger
 * @version 2013/05/30 
 */
public class Threshold_Adaptive_Niblack implements PlugInFilter {
	
	final static String[] regionTypes = {"Box", "Disk", "Gaussian"};
	static int regionTypeIndex = 0;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		ByteProcessor I = (ByteProcessor) ip;
		Parameters params = new Parameters();
		
		if (!setParameters(params))
			return;
		
		AdaptiveThresholder thr = null; 
		switch (regionTypeIndex) {
		case 0 : thr = new NiblackThresholder.Box(params); break;
		case 1 : thr = new NiblackThresholder.Disk(params); break;
		case 2 : thr = new NiblackThresholder.Gauss(params); break;
		}

		ByteProcessor Q = thr.getThreshold(I);
		thr.threshold(I, Q);
	}
	
	boolean setParameters(Parameters params) {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addChoice("region type", regionTypes, regionTypes[regionTypeIndex]);
		gd.addNumericField("radius", params.radius, 0);
		gd.addNumericField("kappa", params.kappa, 2);
		gd.addNumericField("dMin", params.dMin, 0);
		gd.addCheckbox("bright background", (params.bgMode == BackgroundMode.BRIGHT));
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		regionTypeIndex = gd.getNextChoiceIndex();
		params.radius = (int) gd.getNextNumber();
		params.kappa = gd.getNextNumber();
		params.dMin = (int) gd.getNextNumber();
		params.bgMode = (gd.getNextBoolean()) ? BackgroundMode.BRIGHT : BackgroundMode.DARK;
		return true;
	}
}
