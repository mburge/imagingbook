package Ch17_Bildvergleich;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.matching.ChamferMatcher;

public class Chamfer_Matching implements PlugInFilter {
	ImagePlus img1;
	
	private static ImagePlus img2;


    public int setup(String arg, ImagePlus imp) {
    	this.img1 = imp;
        return DOES_8G + NO_CHANGES;
    }
    
    //--------------------------------------------------------------------

    public void run(ImageProcessor ip) {
		if (!showDialog()) return;

    	ByteProcessor subimage = (ByteProcessor) img2.getProcessor(); 
		
		FloatProcessor dtf = ChamferMatcher.distanceTransform(ip);
		ImagePlus distwin = new ImagePlus("Distance Transform of " + img1.getTitle(),dtf); 
		distwin.show();
		
		FloatProcessor matchip = ChamferMatcher.chamferMatch(dtf, subimage);
		ImagePlus matchwin = new ImagePlus("Match of " + img1.getTitle(),matchip); 
		matchwin.show();
    }
 
    private boolean showDialog() {
		int[] wList = WindowManager.getIDList();
		if (wList==null) {
			IJ.error("No windows are open.");
			return false;
		}

		String[] titles = new String[wList.length];
		for (int i=0; i<wList.length; i++) {
			ImagePlus imp = WindowManager.getImage(wList[i]);
			if (imp!=null)
				titles[i] = imp.getTitle();
			else
				titles[i] = "";
		}
		GenericDialog gd = new GenericDialog("Chamfer Matching", IJ.getInstance());
		String title2;
		if (img2 == null)
			title2 = titles[0];
		else
			title2 = img2.getTitle();
		gd.addChoice("Image2:", titles, title2);
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		else {
			int index2 = gd.getNextChoiceIndex();
			title2 = titles[index2];
			img2 = WindowManager.getImage(wList[index2]);
			return true;
		}
    }
		
}
