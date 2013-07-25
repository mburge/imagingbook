package Ch12_ColorImages;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.color1.ColorStatistics;

public class Count_Colors implements PlugInFilter {
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		int n = ColorStatistics.countColors((ColorProcessor) ip);
		IJ.log("This image has " + n + " different colors.");
	}

}
