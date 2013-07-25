package C04_ColorEdges;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.coloredge.CannyEdgeDetector;
import imagingbook.pub.coloredge.CannyEdgeDetector.Parameters;

import java.awt.Color;
import java.awt.Point;
import java.util.List;

/**
 * This plugin implements the Canny edge detector for all types of images.
 * @author W. Burger
 * @version 2013/05/30
 */
public class Canny_Edges implements PlugInFilter {
	
	static boolean showEdgeMagnitude = true;
	static boolean showEdgeOrientation = true;
	static boolean showBinaryEdges = true;
	static boolean listEdgeTraces = true;
	
	ImagePlus imp = null;
	
	public int setup(String arg0, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		
		Parameters params = new Parameters();
		if (!setParameters(params)) return;
		
		CannyEdgeDetector detector = new CannyEdgeDetector(ip, params);
		
		if (showEdgeMagnitude) {
			ImageProcessor eMag = detector.getEdgeMagnitude();
			(new ImagePlus("Canny Edge Magnitude sigma=" + params.gSigma, eMag)).show();
		}
		
		if (showEdgeOrientation) {
			ImageProcessor eOrt = detector.getEdgeOrientation();
			(new ImagePlus("Canny Edge Orientation sigma=" + params.gSigma, eOrt)).show();
		}
		
		if (showBinaryEdges) {
			ImageProcessor eBin = detector.getEdgeBinary();
			(new ImagePlus("Canny Binary Edges sigma=" + params.gSigma, eBin)).show();
		}
		
		if(listEdgeTraces) {
			List<List<Point>> edgeTraces = detector.getEdgeTraces();
			IJ.log("number of edge traces: " + edgeTraces.size());
		}
	}

	boolean setParameters(Parameters params) {
		GenericDialog gd = new GenericDialog("Canny Detector");
		// Canny parameters:
		gd.addNumericField("Sigma (0.5 - 20)", params.gSigma, 1);
		gd.addNumericField("Low Threshold", params.loThr, 2);
		gd.addNumericField("High Threshold", params.hiThr, 2);
		gd.addCheckbox("Normalize gradient magnitude", params.normGradMag);
		// plugin parameters:
		gd.addMessage("Plugin parameters:");
		gd.addCheckbox("Show edge magnitude", showEdgeMagnitude);
		gd.addCheckbox("Show edge orientation", showEdgeOrientation); 
		gd.addCheckbox("Show binary edges", showBinaryEdges);
		gd.addCheckbox("List edge traces", listEdgeTraces);
		// display
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}	
		// retrieve Canny parameters:
		params.gSigma = (float) gd.getNextNumber();
		if (params.gSigma < 0.5f) params.gSigma = 0.5f;
		if (params.gSigma > 20) params.gSigma = 20;
		params.loThr = (float) gd.getNextNumber();
		params.hiThr = (float) gd.getNextNumber();
		params.normGradMag = gd.getNextBoolean();
		// retrieve plugin parameters:
		showEdgeMagnitude = gd.getNextBoolean();
		showEdgeOrientation = gd.getNextBoolean();
		showBinaryEdges = gd.getNextBoolean();
		listEdgeTraces = gd.getNextBoolean();
		return true;
	}
	
	ColorProcessor combineEdgeChannels(ImageProcessor[] edgeProcessors) {
		int w = edgeProcessors[0].getWidth();
		int h = edgeProcessors[0].getHeight();
		
		Color[] colors = //http://www.w3schools.com/tags/ref_colormixer.asp
		{
				new Color(0xFFFFFF), // 0 black
				new Color(0x0000CC), // 1 blue
				new Color(0x009900), // 2 green
				new Color(0x00AAAA), // 3 cyan
				new Color(0xCC0000), // 4 red
				new Color(0x9900CC), // 5 magenta
				new Color(0xFFCC00), // 6 yellow
				new Color(0x000000)  // 7 white
		};
		
		ColorProcessor cEdges = new ColorProcessor(w, h);
		cEdges.fill();
		int[] RGB = new int[3];
		for (int u=0; u<w; u++) {
			for (int v=0; v<h; v++) {
				int r = edgeProcessors[0].getPixel(u, v);
				int g = edgeProcessors[1].getPixel(u, v);
				int b = edgeProcessors[2].getPixel(u, v);
				r = r>0 ? 1 : 0;
				g = g>0 ? 1 : 0;
				b = b>0 ? 1 : 0;
				int cindx = 4 * r + 2 * g + b;
				Color c = colors[cindx];
//				if (r>0 || g>0 || b>0) {
//					if (useWhiteBackground && r>0 && g>0 && b>0) {
//						r = g = b = 0;	// set full edges to black
//					}
					RGB[0] = c.getRed();
					RGB[1] = c.getGreen();
					RGB[2] = c.getBlue();
					cEdges.putPixel(u, v, RGB);
//				}
			}
		}
		
		for (int i=0; i<8; i++) {
			Color c = colors[i];
			RGB[0] = c.getRed();
			RGB[1] = c.getGreen();
			RGB[2] = c.getBlue();
			cEdges.putPixel(0+i, cEdges.getHeight()-1, RGB);
		}
		return cEdges;
	}
	
	
	// --------------------------------------------------------------------------
	
    void showInvertedEdgeStrength(FloatProcessor fp) {
    	// fp assumed to have only positive values
    	fp.resetMinAndMax();
    	// convert to byte processor
    	ByteProcessor bp = (ByteProcessor) fp.convertToByte(false);
    	bp.invert();
    	(new ImagePlus("Edge Magnitude", bp)).show();
    }
    
	void showComposite(FloatProcessor magnitude, ImageProcessor edgesBinary) {
		IJ.log("showComposite");
		int width = magnitude.getWidth();
		int height = magnitude.getHeight();
		ColorProcessor comp = new ColorProcessor(width, height);

		magnitude.findMinAndMax();
		double emax = (float) magnitude.getMax();
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				float mf = magnitude.getPixelValue(u, v);
				int mi = 255 - (int) (mf * 255 / emax) * 2;
				if (mi < 0)
					mi = 0;
				// int mi = (int) (mf * 255 / emax);
				comp.putPixel(u, v, new int[] { mi, mi, mi });

				if (edgesBinary.getPixel(u, v) != 0) {
					// comp.putPixel(u, v, new int[] {0, 0, 255});
					comp.putPixel(u, v, edgesBinary.getPixel(u, v));
				}
			}
		}
		(new ImagePlus("Composite", comp)).show();
	}
	
	void replaceBlack(ImageProcessor edgesBinary) {
		IJ.log("replaceBlack");
		if (edgesBinary instanceof ColorProcessor) {
			IJ.log("replacing black...");
			int width = edgesBinary.getWidth();
			int height = edgesBinary.getHeight();
			int[] RGB = new int[3];
			for (int u = 0; u < width; u++) {
				for (int v = 0; v < height; v++) {
					edgesBinary.getPixel(u, v, RGB);
					if (RGB[0] == 0 && RGB[1] == 0 && RGB[2] == 0) {
						RGB[0] = 255;
						RGB[1] = 255;
						RGB[2] = 255;
						edgesBinary.putPixel(u, v, RGB);
					}

				}
			}
		}
	}
	
}
