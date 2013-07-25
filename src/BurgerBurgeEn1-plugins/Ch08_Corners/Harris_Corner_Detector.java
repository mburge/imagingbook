package Ch08_Corners;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.corners.HarrisCornerDetector;

/**
 * This plugin implements the Harris corner detector.
 * @version 2013/07/09
 */
public class Harris_Corner_Detector implements PlugInFilter {
	
	ImagePlus im;
	static int nmax = 0;	//points to show

    public int setup(String arg, ImagePlus im) {
    	this.im = im;
        return DOES_ALL + NO_CHANGES;
    }
    
    public void run(ImageProcessor ip) {
    	HarrisCornerDetector.Parameters params = new HarrisCornerDetector.Parameters();
		if (!showDialog(params)) //dialog canceled or error
			return; 
		HarrisCornerDetector hcd = new HarrisCornerDetector(ip, params);
		hcd.findCorners();
		ImageProcessor result = hcd.showCornerPoints(ip, nmax);
		(new ImagePlus("Corners from " + im.getTitle(), result)).show();
    }
    
	private boolean showDialog(HarrisCornerDetector.Parameters params) {
		// display dialog , return false if canceled or on error.
		GenericDialog dlg = new GenericDialog("Harris Corner Detector");
		dlg.addNumericField("Alpha", params.alpha, 3);
		dlg.addNumericField("Threshold", params.threshold, 0);
		dlg.addCheckbox("Clean up corners", params.doCleanUp);
		dlg.addNumericField("Corners to show (0 = show all)", nmax, 0);
		dlg.showDialog();
		if(dlg.wasCanceled())
			return false;	
		params.alpha = dlg.getNextNumber();
		params.threshold = (int) dlg.getNextNumber();
		params.doCleanUp = dlg.getNextBoolean();
		nmax = (int) dlg.getNextNumber();
		if(dlg.invalidNumber()) {
			IJ.showMessage("Error", "Invalid input number");
			return false;
		}	
		return true;
	}
}
