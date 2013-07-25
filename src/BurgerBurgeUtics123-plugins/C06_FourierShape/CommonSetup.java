package C06_FourierShape;

import ij.gui.GenericDialog;
import ij.gui.ShapeRoi;
import ij.process.ByteProcessor;
import imagingbook.lib.math.Complex;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;


abstract class CommonSetup {
	
	static int 		FourierDescriptorPairs = 15;
	static int 		ShapeReconstructionPoints = 100;
		
	static boolean	DrawOriginalContour = true;
	static boolean	DrawOriginalSamplePoints = true;
	static boolean 	DrawShapeCenter = true;
	static boolean 	DrawReconstruction = true;
	static boolean 	DrawNormalizedShapes = true;
	
	static Color 	ContourColor = Color.blue;
	static double 	ContourStrokeWidth = 0.5;
	
	static Color 	SampleStrokeColor = Color.magenta;
	static double 	SampleStrokeWidth = 0.1;
	static Color 	SampleFillColor = null;
	static double 	SampleRadius = 0.5;
	
	static Color 	ReconstructionColor = Color.green.darker();
	static double 	ReconstructionStrokeWidth = 0.5; //0.75;
	
	static Color 	CanonicalShapeAColor = Color.blue.brighter();
	static Color 	CanonicalShapeBColor = new Color(128, 66, 36); // brown

	boolean setParameters() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addNumericField("Fourier coefficient pairs", FourierDescriptorPairs, 0);
		gd.addNumericField("Shape reconstruction points", ShapeReconstructionPoints, 0);
		gd.addCheckbox("Draw original contour", DrawOriginalContour);
		gd.addCheckbox("Draw original sample points", DrawOriginalSamplePoints);
		gd.addCheckbox("Draw shape center", DrawShapeCenter);
		gd.addCheckbox("Draw Fourier reconstruction", DrawReconstruction);
		gd.addCheckbox("Draw original sample points", DrawOriginalSamplePoints);
		gd.addCheckbox("Draw normalized shapes", DrawNormalizedShapes);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		FourierDescriptorPairs = (int) gd.getNextNumber();
		ShapeReconstructionPoints = (int) gd.getNextNumber();
		DrawOriginalContour = gd.getNextBoolean();
		DrawOriginalSamplePoints = gd.getNextBoolean();
		DrawShapeCenter = gd.getNextBoolean();
		DrawReconstruction = gd.getNextBoolean();
		DrawOriginalSamplePoints = gd.getNextBoolean();
		DrawNormalizedShapes = gd.getNextBoolean();
		return true;
	}
	
	// helper methods ------------------------------------------------------
	
		ShapeRoi makeClosedPathShape(Complex[] points, double dx, double dy) {
			Path2D path = new Path2D.Float();
			for (int i = 0; i < points.length; i++) {
				Complex pt = points[i];
				double xt = pt.re + dx; 
				double yt = pt.im + dy; 
				if (i == 0) {
					path.moveTo(xt, yt);
				}
				else {
					path.lineTo(xt, yt);
				}
			}
			path.closePath();
			return new ShapeRoi(path);
		}
		
		ShapeRoi makeCircleShape(Complex c, double r, double dx, double dy) {
			double d = 2 * r;
			double x = c.re + dx;
			double y = c.im + dy;
			Ellipse2D oval = new Ellipse2D.Double(x - r, y - r, d, d);
			return new ShapeRoi(oval);
		}
		
		ShapeRoi makeCrossShape(Complex c, int crossSize, double dx, double dy) {
			double xc = c.re + dx;
			double yc = c.im + dy;
			Path2D path = new Path2D.Double();
			path.moveTo(xc - crossSize, yc);
			path.lineTo(xc + crossSize, yc);
			path.moveTo(xc, yc - crossSize);
			path.lineTo(xc, yc + crossSize);
			return new ShapeRoi(path);
		}
		
		void brighten(ByteProcessor ip, int minGray) {
			if (minGray > 254) minGray = 254;
			 float scale = (255 - minGray) / 255f;
			 int[] table = new int[256];
			for (int i = 0; i < 256; i++) {
				 table[i] = (int) Math.round(minGray + scale * i);
			 }
			 ip.applyTable(table);
		}
}
