package Ch05_Punktoperationen;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.histogram.HistogramMatcher;
import imagingbook.pub.histogram.HistogramPlot;
import imagingbook.pub.histogram.Util;

public class Match_To_Gaussian_Histogram implements PlugInFilter { 
	
	public int setup(String arg0, ImagePlus im) {
		return DOES_8G;
	}
	
	public void run(ImageProcessor ipA) {
		
		// get histograms
		int[] hA = ipA.getHistogram();
		int[] hB = Util.makeGaussianHistogram(128,50);
				
		(new HistogramPlot(hA, "Histogram A")).show();
		(new HistogramPlot(hB, "Cumulative Histogram A")).show();
		
		double[] nhB = Util.normalizeHistogram(hB);
		(new HistogramPlot(nhB, "Gauss")).show();
		
		double[] chB = Util.Cdf(hB);
    	(new HistogramPlot(chB, "Gauss cumulative")).show();
		
		HistogramMatcher m = new HistogramMatcher();
		int[] F = m.matchHistograms(hA, hB);
		
//		for (int i = 0; i < F.length; i++) {
//			IJ.log(i + " -> " + F[i]);
//		}
		
		ipA.applyTable(F);
		int[] hAm = ipA.getHistogram();
		(new HistogramPlot(hAm, "Histogram A (mod)")).show();
		(new HistogramPlot(Util.Cdf(hAm), "Cumulative Histogram A (mod)")).show();
	}

}

