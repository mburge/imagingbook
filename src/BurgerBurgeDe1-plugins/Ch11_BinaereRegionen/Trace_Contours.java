package Ch11_BinaereRegionen;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.regions.Contour;
import imagingbook.pub.regions.ContourOverlay;
import imagingbook.pub.regions.BinaryRegion;
import imagingbook.pub.regions.RegionContourLabeling;

import java.awt.Color;
import java.util.List;

/**
 * This plugin implements the combined contour tracing and 
 * component labeling algorithm as described in  Chang, Chun-Jen: 
 * "A Component-Labeling Algorithm Using Contour Tracing 
 * Technique", Proc. ICDAR03, p. 741-75, IEEE Comp. Soc., 2003.
 * It uses the ContourTracer class to create lists of points 
 * representing the internal and external contours of each region in
 * the binary image.  Instead of drawing directly into the image, 
 * we make use of ImageJ's Overlay class to draw the contours 
 * in a separate vector layer on top of the image.
 * 2012-08-06: adapted to new 'regions' package.
*/
public class Trace_Contours implements PlugInFilter {
	
	static float strokeWidth = 0.5f;
	static Color outerColor = Color.green;
	static Color innerColor = Color.blue;
	
	ImagePlus origImage = null;
	String origTitle = null;
	static boolean verbose = true;
	
	public int setup(String arg, ImagePlus im) { 
    	origImage = im;
		origTitle = im.getTitle();
		return DOES_8G + NO_CHANGES; 
	}
	
	public void run(ImageProcessor ip) {
		ByteProcessor bp = (ByteProcessor) ip.convertToByte(false);
		//  label regions and trace contours
		RegionContourLabeling tracer = new RegionContourLabeling(bp);
		
		// extract contours and regions
		List<Contour> outerContours = tracer.getAllOuterContours(false);
		List<Contour> innerContours = tracer.getAllInnerContours(false);
		List<BinaryRegion> regions = tracer.getRegions();
		if (verbose) printRegions(regions);

		// change lookup-table to show gray regions
		bp.setMinAndMax(0,512);
		
		// create an image with overlay to show the contours
		ContourOverlay cc = new ContourOverlay();
		cc.addContours(outerContours, outerColor, strokeWidth);
		cc.addContours(innerContours, innerColor, strokeWidth);
		ImagePlus im2 = new ImagePlus("Contours of " + origTitle, bp);
		im2.setOverlay(cc);
		im2.show();
	}
	
	void printRegions(List<BinaryRegion> regions) {
		for (BinaryRegion r: regions) {
			IJ.log(r.toString());
		}
	}
}
