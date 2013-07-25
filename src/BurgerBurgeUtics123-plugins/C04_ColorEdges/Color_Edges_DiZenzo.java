package C04_ColorEdges;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.util.File;
import imagingbook.pub.coloredge.ColorEdgeDetector;
import imagingbook.pub.coloredge.DiZenzoCumaniEdgeDetector;


/**
 * This plugin implements a multi-gradient (DiZenzo/Cumani-style) color 
 * edge detector.
 * @author W. Burger
 * @version 2013/05/30
 */
public class Color_Edges_DiZenzo implements PlugInFilter {
	
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
    	
    	if (!setParameters()) return;
    	
    	ColorProcessor cp = (ColorProcessor) ip;
    	ColorEdgeDetector ced = new DiZenzoCumaniEdgeDetector(cp);
    	
    	if (showEdgeMagnitude) {
    		FloatProcessor edgeMagnitude = ced.getEdgeMagnitude();
    		(new ImagePlus("Edge Magnitude (DiZenzo)", edgeMagnitude)).show();
    	}
		if (showEdgeOrientation) {
			FloatProcessor edgeOrientation = ced.getEdgeOrientation();
			(new ImagePlus("Edge Orientation (DiZenzo)", edgeOrientation)).show();
		}
    }
    
    boolean setParameters() {
		GenericDialog gd = new GenericDialog("Multi-Gradient Color Edges");
		gd.addCheckbox("Show edge magnitude", showEdgeMagnitude);
		gd.addCheckbox("Show edge orientation", showEdgeOrientation);
		gd.showDialog();
		if(gd.wasCanceled()) return false;
		showEdgeMagnitude = gd.getNextBoolean();
		showEdgeOrientation =  gd.getNextBoolean();
		return true;
    }
      
}

