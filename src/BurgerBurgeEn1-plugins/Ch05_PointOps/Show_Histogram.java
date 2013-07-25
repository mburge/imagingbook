package Ch05_PointOps;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.histogram.HistogramPlot;
import imagingbook.pub.histogram.Util;

public class Show_Histogram implements PlugInFilter { 
	
	String title;
	
	public int setup(String arg0, ImagePlus im) {
		title = im.getTitle();
		return DOES_8G + NO_CHANGES;
	}
	
	public void run(ImageProcessor ip) {
		int[] h = ip.getHistogram();
		(new HistogramPlot(h, "Histogram of " + title)).show();
		(new HistogramPlot(Util.Cdf(h), "Cum. Histogram of " + title)).show();
	}
	
}

