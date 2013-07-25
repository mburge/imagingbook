package C06_FourierShape;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.math.Complex;
import imagingbook.pub.regions.Contour;
import imagingbook.pub.fd.FourierDescriptor;
import imagingbook.pub.regions.RegionContourLabeling;

import java.awt.geom.Point2D;
import java.util.List;


/**
 * This plugin corresponds to the Fourier descriptor code example in the book's implementation 
 * section (Program 6.1 in UtICS Vol. 3).
 * The input image ip is assumed to contain a binary image. The class 'RegionContourLabeling'
 * is used to find connected regions. Then the list of outer contours is retrieved and the 
 * longest contour is assigned to 'V' as an array of type 'Point2D'. Then the contour 'V' is 
 * used to create a Fourier descriptor with 15 coefficient pairs. Alternatively, we could have 
 * created a Fourier descriptor of the same length (number of coefficients) as the contour and 
 * then truncated it (using the'truncate()' method) to the specified number of coefficient 
 * pairs. A partial reconstruction of the contour (with 100 sample points) is calculated 
 * from the Fourier descriptor 'fd'. Finally, a pair of invariant descriptors (contained in the 
 * array 'fdAB') is calculated.
 * 
 * Note: This plugin only works with BYTE (grayscale) images, pixel values >0 are assumed to be 
 * foreground. Lookup-tables are ignored.
 * 
 * @author W. Burger
 * @version 2013/05/30
 */

public class Fourier_Descriptor_RegularSampling extends CommonSetup implements PlugInFilter {
		
	private ImagePlus img;

	public int setup(String arg, ImagePlus img) { 
    	this.img = img;
		return DOES_8G + NO_CHANGES; 
	}
	
	public void run(ImageProcessor ip) {
		if (!setParameters()) 
			return;
		
		ByteProcessor bp = (ByteProcessor) ip;
		
		// segment the image and select the longest outer region contour:
		RegionContourLabeling labeling = new RegionContourLabeling(bp);
		List<Contour> outerContours = labeling.getAllOuterContours(true);
		if (outerContours.isEmpty()) {
			IJ.error("no regions found");
			return;
		}
		Contour contr = outerContours.get(0);	// select the longest contour
		Point2D[] V = contr.getPointArray();
		
		// create the Fourier descriptor for 'V' with Mp coefficient pairs:
		int Mp = FourierDescriptorPairs;
		FourierDescriptor fd = new FourierDescriptor.Uniform(V, Mp);
		
		// reconstruct the corresponding shape with 100 contour points:
		Complex[] R = fd.getReconstruction(100);
		
		// create a pair of invariant descriptors (G^A, G^B):
		FourierDescriptor[] fdAB = fd.makeInvariant();
		FourierDescriptor fdA = fdAB[0];	// = G^A
		FourierDescriptor fdB = fdAB[1];	// = G^B

		// ----------------------------------------------------------------
		// show various reconstructions (as a vector overlay)
		// ----------------------------------------------------------------
		
		Overlay oly = new Overlay();
		
		if (DrawOriginalContour) {
			Roi roi = makeClosedPathShape(fd.getSamples(), 0.5, 0.5);
			roi.setStrokeColor(ContourColor);
			roi.setStrokeWidth(ContourStrokeWidth);
			oly.add(roi);
		}
		
		if (DrawOriginalSamplePoints) {
			for (Complex c : fd.getSamples()) {
				ShapeRoi roi = makeCircleShape(c, SampleRadius, 0.5, 0.5);
				roi.setStrokeColor(SampleStrokeColor);
				roi.setStrokeWidth(SampleStrokeWidth);
				if (SampleFillColor != null) {
					roi.setFillColor(SampleFillColor);
				}
				oly.add(roi);
			}
		}
		
		if (DrawShapeCenter) {
			ShapeRoi roi = makeCrossShape(fd.getCoefficient(0), 2, 0.5, 0.5);
			roi.setStrokeColor(ReconstructionColor);
			roi.setStrokeWidth(ReconstructionStrokeWidth);
			oly.add(roi);
		}
	
		if (DrawReconstruction) {
			ShapeRoi roi = makeClosedPathShape(R, 0.5, 0.5);
			roi.setStrokeColor(ReconstructionColor);
			roi.setStrokeWidth(ReconstructionStrokeWidth);
			oly.add(roi);
		}
		
		if (DrawNormalizedShapes) {
			ShapeRoi roiA = makeClosedPathShape(fdA.getReconstruction(ShapeReconstructionPoints), 0.5, 0.5);
			roiA.setStrokeColor(CanonicalShapeAColor);
			roiA.setStrokeWidth(ReconstructionStrokeWidth);
			oly.add(roiA);
			ShapeRoi roiB = makeClosedPathShape(fdB.getReconstruction(ShapeReconstructionPoints), 0.5, 0.5);
			roiB.setStrokeColor(CanonicalShapeBColor);
			roiB.setStrokeWidth(ReconstructionStrokeWidth);
			oly.add(roiB);
		}
		
		String title = img.getShortTitle() + "-Fourier-Descriptors";
		ByteProcessor ip2 = (ByteProcessor) ip.duplicate();
		ImagePlus im = new ImagePlus(title, ip2);	
		if (ip2.isInvertedLut()) {
			ip2.invert();
			ip2.invertLut();
		}
		brighten(ip2, 220);
		im.setOverlay(oly);
		im.show();
	}
	
}
