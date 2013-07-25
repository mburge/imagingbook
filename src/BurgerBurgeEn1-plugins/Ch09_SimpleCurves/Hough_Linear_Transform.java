package Ch09_SimpleCurves;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Toolbar;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.hough.LinearHT;
import imagingbook.pub.hough.LinearHT.HoughLine;

import java.awt.Color;
import java.util.List;
import java.util.Locale;

/** 
 * This plugin implements a simple Hough Transform for straight lines.
 * It expects an 8-bit binary (edge) image, with background = 0 and
 * foreground (contour) pixels > 0.
 * @version 2013/07/09
*/
public class Hough_Linear_Transform implements PlugInFilter {
	
	static int N_Angle = 256;			// resolution of angle
	static int N_Radius = 256;			// resolution of radius
	static int MaxLines = 5;			// number of strongest lines to be found
	static int MinPointsOnLine = 50;	// min. number of points on each line
	
	static boolean ShowAccumulator = false;
	static boolean ShowAccumulatorPeaks = false;
	static boolean ListStrongestLines = false;
	static boolean ShowResults = true;
	static double LineWidth = 1.0;
	static Color LineColor = Color.green;
	static boolean DrawWithPickedColor = true;
	static boolean MarkImageCenter = true;
	
	ImagePlus imp;	

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		ByteProcessor bp = (ByteProcessor) ip;
		int w = ip.getWidth();
		int h = ip.getHeight();
		
		if (!showDialog()) //dialog canceled or error
			return; 

		//compute the Hough Transform
		LinearHT ht = new LinearHT(bp, N_Angle, N_Radius);

		if (ShowAccumulator){
			FloatProcessor accIp = ht.getAccumulatorImage();
			accIp.flipHorizontal(); //flip because angle runs reverse (negative y)
			(new ImagePlus("HT of " + imp.getTitle(), accIp)).show();
		}
		
		if (ShowAccumulatorPeaks) {
			FloatProcessor maxIp = ht.getLocalMaxImage();
			maxIp.flipHorizontal(); //flip because angle runs reverse (negative y)
			(new ImagePlus("Maxima of " + imp.getTitle(), maxIp)).show();
		}
		
		List<HoughLine> lines = ht.getMaxLines(MaxLines, MinPointsOnLine);
		
		if (lines.isEmpty()) {
			IJ.log("No lines detected - check the input image and parameters!");
		}

		if (ListStrongestLines) {
			int i = 0;
			for (HoughLine hl : lines){
				double angle =  hl.getAngle();
				double radius = hl.getRadius();
				int count = hl.getCount();
				i = i + 1;
				String msg = String.format(Locale.US, "%d: angle=%.2f, radius=%.2f, points=%d", i, angle, radius, count);
				//IJ.log(i + ": angle=" + angle + ", radius=" + radius + ", points" + count);
				IJ.log(msg);
			}
		}
		
		if (ShowResults) {
			ImageProcessor lineIp = new ColorProcessor(w, h);
			lineIp.setColor(Color.white);
			lineIp.fill();
			
			// copy the original foreground points to lineIp
			lineIp.setColor(Color.gray);
			for (int v = 0; v < h; v++) {
				for (int u = 0; u < w; u++) {
					if (ht.pixelIsForeground(u, v))
						lineIp.drawPixel(u, v); // draw a black pixel
				}
			}
			
			if (DrawWithPickedColor)
				lineIp.setColor(Toolbar.getForegroundColor());
			else
				lineIp.setColor(LineColor);
			for (HoughLine hl : lines){
				drawLine(lineIp, hl);
			}
			
			if (MarkImageCenter) {
				lineIp.setColor(Color.black);
				int uu = (int) Math.round(ht.getXc());
				int vv = (int) Math.round(ht.getYc());
				drawCross(lineIp, uu, vv, 2);
			}
			
			(new ImagePlus("Lines of " + imp.getTitle(), lineIp)).show();
		}
	}
	
	
	private boolean showDialog() {
		// display dialog , return false if canceled or on error.
		GenericDialog dlg = new GenericDialog("Hough Transform (lines)");
		dlg.addNumericField("Angle steps", N_Angle, 0);
		dlg.addNumericField("Radius steps", N_Radius, 0);
		dlg.addNumericField("Max. number of lines to show", MaxLines, 0);
		dlg.addNumericField("Min. number of points per line", MinPointsOnLine, 0);
		dlg.addCheckbox("Show accumulator", ShowAccumulator);
		dlg.addCheckbox("Show accumulator", ShowAccumulatorPeaks);
		dlg.addCheckbox("List strongest lines", ListStrongestLines);
		dlg.addCheckbox("Show results", ShowResults);
		dlg.addCheckbox("Draw with picked color", DrawWithPickedColor);
		dlg.addNumericField("Line width", LineWidth, 1);

		dlg.showDialog();
		if(dlg.wasCanceled())
			return false;
		N_Angle = (int) dlg.getNextNumber();
		N_Radius = (int) dlg.getNextNumber();
		MaxLines = (int) dlg.getNextNumber();
		MinPointsOnLine = (int) dlg.getNextNumber();
		ShowAccumulator = dlg.getNextBoolean();
		ShowAccumulatorPeaks = dlg.getNextBoolean();
		ListStrongestLines = dlg.getNextBoolean();
		ShowResults = dlg.getNextBoolean();
		DrawWithPickedColor = dlg.getNextBoolean();
		LineWidth = dlg.getNextNumber();
		if(dlg.invalidNumber()) {
			IJ.showMessage("Error", "Invalid input number");
			return false;
		}
		return true;
	}
	
	/*
	 * This is a brute-force drawing function which simply marks all
	 * image pixels that are sufficiently close to the HoughLine hl. 
	 */
	private void drawLine(ImageProcessor ip, HoughLine hl) {
		final int w = ip.getWidth();
		final int h = ip.getHeight();
		double dmax = LineWidth / 2;
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				// get the distance between (u,v) and the line hl:
				double d = Math.abs(hl.getDistance(u, v));
				if (d < dmax) {
					ip.drawPixel(u, v);
				}
			}
		}
		
	}
	
	private void drawCross(ImageProcessor ip, int uu, int vv, int size) {
		ip.drawLine(uu - size, vv, uu + size, vv);
		ip.drawLine(uu, vv - size, uu, vv + size);
	}

}





	
	
