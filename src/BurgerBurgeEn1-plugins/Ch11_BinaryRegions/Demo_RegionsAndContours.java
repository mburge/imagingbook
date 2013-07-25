package Ch11_BinaryRegions;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.util.List;

import imagingbook.pub.regions.BinaryRegion;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.Contour;

/*
 * This plugin demonstrates the use of the class CombinedContourLabeling
 * to perform both region labeling and contour tracing simultaneously.
 * The resulting contours are displayed as a non-destructive vector overlay.
 * 
 * Author: W. Burger
 * 2010-11-19: initial version.
 */

public class Demo_RegionsAndContours implements PlugInFilter {
	
	static boolean listRegions = true;
	static boolean listContourPoints = false;
	static boolean showContours = true;
	
	public int setup(String arg, ImagePlus im) { 
		return DOES_ALL + NO_CHANGES; 
	}
	
	public void run(ImageProcessor ip) {
	   	if (!getUserInput())
    		return;
	   	
	   	// Make sure we have a proper byte image:
	   	ByteProcessor bp = (ByteProcessor) ip.convertToByte(false);
	   	
	   	bp.autoThreshold();
	   	bp.invert();
	   	
	   	// Create the region labeler / contour tracer:
		RegionContourLabeling segmenter = new RegionContourLabeling(bp);
		
		// Retrieve the list of detected regions:
		List<BinaryRegion> regions = segmenter.getRegions(true);	// regions are sorted by size
		if (listRegions) {
			IJ.log("Detected regions (sorted by size): " + regions.size());
			for (BinaryRegion r: regions) {
				IJ.log(r.toString());
			}
		}
		
		// See how the list of regions is put into an array: 
		BinaryRegion[] regionArray = regions.toArray(new BinaryRegion[0]);
		
		if (regionArray.length > 0) {
			BinaryRegion largestRegion = regionArray[0];
			IJ.log("The largest region is of size " + largestRegion.getSize());
		
			// Obtain the outer contour of the largest region:
			Contour oc = largestRegion.getOuterContour();
			Point[] points = oc.getPointArray();
			if (listContourPoints) {
				for (int i=0; i<points.length; i++) {
					Point p = points[i];
					IJ.log("Point " + p.x + "/" + p.y);
				}
			}
		}
		
		// Display the contours if desired:
		if (showContours) {
			ImageProcessor lip = segmenter.makeLabelImage(false);
			ImagePlus lim = new ImagePlus("Region labels and contours", lip);
			Overlay oly = segmenter.makeContourOverlay();
			lim.setOverlay(oly);
			lim.show();
		}
	}
	
	boolean getUserInput() {
		GenericDialog gd = new GenericDialog("Contour Tracer");
		gd.addCheckbox("List regions", listRegions);
		gd.addCheckbox("List contour points", listContourPoints);
		gd.addCheckbox("Show contours", showContours);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		listRegions = gd.getNextBoolean();
		listContourPoints = gd.getNextBoolean();
		showContours = gd.getNextBoolean();
		return true;
	}
}