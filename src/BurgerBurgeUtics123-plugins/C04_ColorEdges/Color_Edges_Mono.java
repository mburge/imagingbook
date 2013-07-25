package C04_ColorEdges;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.math.VectorNorm.NormType;
import imagingbook.lib.util.Enums;
import imagingbook.lib.util.File;
import imagingbook.pub.coloredge.ColorEdgeDetector;
import imagingbook.pub.coloredge.MonochromaticEdgeDetector;
import imagingbook.pub.coloredge.MonochromaticEdgeDetector.Parameters;

/**
 * This is a simple color edge detector based on monochromatic techniques.
 * @author W. Burger
 * @version 2013/05/30
 */
public class Color_Edges_Mono implements PlugInFilter {
	
	static boolean showEdgeMagnitude = true;
	static boolean showEdgeOrientation = true;
	
	ImagePlus imp = null;

    public int setup(String arg, ImagePlus imp) {
    	this.imp = imp;
        return DOES_RGB + NO_CHANGES;
    }

    public void run(ImageProcessor ip) {
    	String title = imp.getTitle();
    	title = File.stripFileExtension(title);
    	Parameters params = new Parameters();
    	
    	if (!setParameters(params)) return;
    	
    	ColorProcessor cp = (ColorProcessor) ip;
    	ColorEdgeDetector ced = new MonochromaticEdgeDetector(cp, params);
    	   	
    	if (showEdgeMagnitude) {
    		FloatProcessor edgeMagnitude = ced.getEdgeMagnitude();
    		(new ImagePlus("Edge Magnitude (Mono)", edgeMagnitude)).show();
    	}
    		
		if (showEdgeOrientation) {
			FloatProcessor edgeOrientation = ced.getEdgeOrientation();
			(new ImagePlus("Edge Orientation (Mono)", edgeOrientation)).show();
		}
    }
    
    boolean setParameters(Parameters params) {
		GenericDialog gd = new GenericDialog("Monochromatic Color Edges");
		gd.addChoice("Color norm", Enums.getEnumNames(NormType.class), params.norm.name());
		gd.addCheckbox("Show edge magnitude", showEdgeMagnitude);
		gd.addCheckbox("Show edge orientation", showEdgeOrientation);
		gd.showDialog();
		if(gd.wasCanceled()) 
			return false;
		params.norm = NormType.valueOf(gd.getNextChoice());
		showEdgeMagnitude = gd.getNextBoolean();
		showEdgeOrientation =  gd.getNextBoolean();
		return true;
    }
      
}

