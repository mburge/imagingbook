package Ch17_Bildvergleich;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.matching.CorrCoeffMatcher;


/** 
 * Template matching plugin based on the local correlation coefficient.
 * Slow because it uses getPixelValue() for pixel access.
 */
public class CorrelCoeff_Matching implements PlugInFilter {

	private ImagePlus templateImg;

    public int setup(String arg, ImagePlus im) {
        return DOES_8G + NO_CHANGES;
    }
    
    //--------------------------------------------------------------------

    public void run(ImageProcessor imgIp) {
		if (!showDialog()) return;
		
    	FloatProcessor imgFp = (FloatProcessor) imgIp.convertToFloat();
		ImageProcessor refIp = templateImg.getProcessor();
		FloatProcessor refFp = (FloatProcessor) refIp.convertToFloat();
		
		CorrCoeffMatcher matcher = new CorrCoeffMatcher(imgFp, refFp);
		
		IJ.log("Starting match...");
		FloatProcessor matchIp = matcher.computeMatch();
		IJ.log("Done.");
		
		ImagePlus matchwin = new ImagePlus("Average Correlation Coefficient", matchIp); 
		matchwin.show();
    }
 
    //--------------------------------------------------------------------

    private boolean showDialog() {
		int[] wList = WindowManager.getIDList();
		if (wList==null) {
			IJ.error("No windows are open.");
			return false;
		}

		String[] titles = new String[wList.length];
		for (int i = 0; i < wList.length; i++) {
			ImagePlus imp = WindowManager.getImage(wList[i]);
			if (imp!=null)
				titles[i] = imp.getTitle();
			else
				titles[i] = "";
		}
		GenericDialog gd = new GenericDialog("Select Template Image", IJ.getInstance());
		String title2;
		if (templateImg == null)
			title2 = titles[0];
		else
			title2 = templateImg.getTitle();
		gd.addChoice("Template:", titles, title2);
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		else {
			int index2 = gd.getNextChoiceIndex();
			title2 = titles[index2];
			templateImg = WindowManager.getImage(wList[index2]);
			return true;
		}
    }
}

