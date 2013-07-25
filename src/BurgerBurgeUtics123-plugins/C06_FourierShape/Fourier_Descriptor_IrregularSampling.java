package C06_FourierShape;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.math.Complex;
import imagingbook.pub.fd.FourierDescriptor;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * This plugin demonstrates the use of the trigonometric method for calculating Fourier
 * descriptors. The input image is assumed to contain a ROI whose (polygon) vertices are used 
 * to calculate the Fourier descriptor, The actual image content is ignored, thus the 
 * image type is irrelevant.
 * A partial reconstruction of the contour (with 100 sample points) is calculated 
 * from the Fourier descriptor 'fd'. Finally, a pair of invariant descriptors (contained in the 
 * array 'fdAB') is calculated.
 * 
 * @author W. Burger
 * @version 2013/05/30
 */

public class Fourier_Descriptor_IrregularSampling extends Fourier_Descriptor_RegularSampling { //implements PlugInFilter {
			
	private ImagePlus img;

	public int setup(String arg, ImagePlus img) { 
    	this.img = img;
		return DOES_8G + ROI_REQUIRED + NO_CHANGES;
	}
	
	public void run(ImageProcessor ip) {
		if (!setParameters()) 
			return;
		
		Roi anyRoi = img.getRoi();
		
		// create the Fourier descriptor for 'anyRoi' with Mp coefficient pairs:
		int Mp = FourierDescriptorPairs;
		FourierDescriptor fd = new FourierDescriptor.Nonuniform(anyRoi, Mp);

		// reconstruct the corresponding shape with 100 contour points:
		Complex[] R = fd.getReconstruction(ShapeReconstructionPoints);
		
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
	
	// helper methods ------------------------------------------------------
	
	Point2D[] getRoiPoints(Roi roi) {
		Polygon poly = roi.getPolygon();
		int[] xp = poly.xpoints;
		int[] yp = poly.ypoints;
		// copy vertices for all non-zero-length polygon segments:
		List<Point> points = new ArrayList<Point>(xp.length);
		points.add(new Point(xp[0], yp[0]));
		int last = 0;
		for (int i = 1; i < xp.length; i++) {
			if (xp[last] != xp[i] || yp[last] != yp[i]) {
				points.add(new Point(xp[i], yp[i]));
				last = i;
			}
		}
		// remove last point if the closing segment has zero length:
		if (xp[last] == xp[0] && yp[last] == yp[0]) {
			points.remove(last);
		}
		return points.toArray(new Point2D[0]);
	}
	
}
