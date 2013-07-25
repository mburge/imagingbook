package C07_SIFT;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.sift.scalespace.DogScaleSpace;
import imagingbook.pub.sift.scalespace.GaussianScaleSpace;

/**
 * Builds a hierarchical Difference-of-Gaussian scale space representation 
 * from the input image and displays all scale levels.
 * To create a linear scale space (without decimation) set 'P' = 1 and 
 * 'topLevel' to an arbitrary value.
 * Consult the book for additional details.
 */

public class Show_DogScaleSpace implements PlugInFilter {

	public int setup(String arg0, ImagePlus arg1) {
		return DOES_8G + DOES_32 + NO_CHANGES;
	}
	
	static double sigma_0 = 1.6;	// initial scale level
	static double sigma_s = 0.5;	// original image (sampling) scale
	
	static int Q = 3;	// scale steps per octave
	static int P = 4;	// number of octaves
	static int botLevel = 0;	// index q of bottom level in each octave
	static int topLevel = Q;	// index q of top level in each octave
	
	public void run(ImageProcessor ip) {
		FloatProcessor fp = (FloatProcessor) ip.convertToFloat();
		GaussianScaleSpace gss =
			new GaussianScaleSpace(fp, sigma_s, sigma_0, P, Q, botLevel, topLevel);
		DogScaleSpace dss = new DogScaleSpace(gss);
		dss.show("DoG");
	}

}
