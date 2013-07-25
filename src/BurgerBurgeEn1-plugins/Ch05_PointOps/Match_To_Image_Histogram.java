package Ch05_PointOps;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.histogram.HistogramMatcher;
import imagingbook.pub.histogram.HistogramPlot;
import imagingbook.pub.histogram.Util;

public class Match_To_Image_Histogram implements PlugInFilter { 
	
	ImagePlus imB;	// reference image (selected interactively)
	
	public int setup(String arg0, ImagePlus imA) {
		return DOES_8G;
	}
	
	public void run(ImageProcessor ipA) {
		if (!runDialog()) // select the reference image
			return;
		
		ImageProcessor ipB = imB.getProcessor();
		
		// get histograms of both images
		int[] hA = ipA.getHistogram();
		int[] hB = ipB.getHistogram();
		(new HistogramPlot(hA, "Histogram A")).show();
		(new HistogramPlot(hB, "Histogram B")).show();
		(new HistogramPlot(Util.Cdf(hA), "Cumulative Histogram A")).show();
		(new HistogramPlot(Util.Cdf(hB), "Cumulative Histogram B")).show();
		
		
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

	boolean runDialog() {
		// get list of open images
		int[] windowList = WindowManager.getIDList();
		if(windowList==null){
			IJ.noImage();
			return false;
		}
		// get image titles
		String[] windowTitles = new String[windowList.length];
		for (int i = 0; i < windowList.length; i++) {
			ImagePlus imp = WindowManager.getImage(windowList[i]);
			if (imp != null)
				windowTitles[i] = imp.getShortTitle();
			else
				windowTitles[i] = "untitled";
		}
		// create dialog and show
		GenericDialog gd = new GenericDialog("Select Reference Image");
		gd.addChoice("Reference Image:", windowTitles, windowTitles[0]);
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		else {
			int img2Index = gd.getNextChoiceIndex();
			imB = WindowManager.getImage(windowList[img2Index]);
			return true;
		}
	}

}

