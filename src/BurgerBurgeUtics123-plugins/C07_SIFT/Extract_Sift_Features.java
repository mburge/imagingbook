package C07_SIFT;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.sift.SiftDescriptor;
import imagingbook.pub.sift.SiftDetector;
import imagingbook.pub.sift.util.Colors;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.util.Collections;
import java.util.List;

/**
 * This plugin extracts multi-scale SIFT features from the current image and displays
 * them as M-shaped markers.
 * @author W. Burger
 * @version 2013/05/28
 */

public class Extract_Sift_Features implements PlugInFilter {

	static double FeatureScale = 1.0; // 1.5;
	static double FeatureStrokewidth = 1.0;
	static double DisplayAngleOffset = -Math.PI / 2;
	
	static boolean ListSiftFeatures = false;

	ImagePlus imp;
	Color[] colors = Colors.defaultDisplayColors;

	public int setup(String arg0, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		FloatProcessor fp = (FloatProcessor) ip.convertToFloat();
		SiftDetector.Parameters params = new SiftDetector.Parameters();
		
		if (!setParameters(params)) {
			return;
		}
		
		SiftDetector sd = new SiftDetector(fp, params);
		
		IJ.log("Extracting SIFT features ...");
		List<SiftDescriptor> features = sd.getSiftFeatures();
		IJ.log("SIFT features found: " + features.size());
		
		if (ListSiftFeatures) {
			int i = 0;
			for (SiftDescriptor d : features) {
				IJ.log(i + ": " + d.toString());
				i++;
			}
		}

		ImageProcessor ip2 = ip.duplicate();
		ImagePlus imp2 = new ImagePlus(imp.getShortTitle() + "-SIFT", ip2);
	
		Overlay oly = drawToOverlay(features);	// show key points (with orientation)

		if (oly != null) {
			imp2.setOverlay(oly);
			imp2.show();
		}
	}
	
	boolean setParameters(SiftDetector.Parameters params) {
			GenericDialog gd = new GenericDialog("Set SIFT parameters");
			gd.addNumericField("tMag :", params.t_Mag, 3, 6, "");
			gd.addNumericField("rMax :", params.reMax, 3, 6, "");
			gd.addNumericField("orientation histogram smoothing :", params.n_Smooth, 0, 6, "");
			gd.addCheckbox("list all SIFT features (might be many!)", ListSiftFeatures);
			gd.showDialog();
			if (gd.wasCanceled()) {
				return false;
			}
			params.t_Mag = gd.getNextNumber();
			params.reMax = gd.getNextNumber();
			params.n_Smooth = (int) gd.getNextNumber();
			ListSiftFeatures = gd.getNextBoolean();
			return true;
	}

	Overlay drawToOverlay(List<SiftDescriptor> desc) {
		Collections.reverse(desc);
		Overlay oly = new Overlay();
		for (SiftDescriptor sd : desc) {
			Roi shapeRoi = makeFeatureShape(sd);
			shapeRoi.setStrokeWidth((float) FeatureStrokewidth);
			shapeRoi.setStrokeColor(colors[0]);
			oly.add(shapeRoi);
		}
		return oly;
	}
	
	private ShapeRoi makeFeatureShape(SiftDescriptor sd) {
		final double x = sd.getX(); 
		final double y = sd.getY(); 
		final double scale = FeatureScale * sd.getScale();
		final double orient = sd.getOrientation() + DisplayAngleOffset;
		final double sin = Math.sin(orient);
		final double cos = Math.cos(orient);
		Path2D poly = new Path2D.Double();	
		poly.moveTo(x + (sin - cos) * scale, y - (sin + cos) * scale);
		poly.lineTo(x + (sin + cos) * scale, y + (sin - cos) * scale);
		poly.lineTo(x, y);
		poly.lineTo(x - (sin - cos) * scale, y + (sin + cos) * scale);
		poly.lineTo(x - (sin + cos) * scale, y - (sin - cos) * scale);
		poly.closePath();
		return new ShapeRoi(poly);
	}
	
}
