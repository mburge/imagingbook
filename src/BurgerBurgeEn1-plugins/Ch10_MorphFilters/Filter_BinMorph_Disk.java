package Ch10_MorphFilters;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.morphology.BinMorpher;
import imagingbook.pub.morphology.BinMorpher.Operation;
import imagingbook.pub.morphology.BinMorpherDisk;

/** 
 * This plugin implements a binary morphology filter using a disk-shaped
 * structuring element whose radius can be specified.
 */
public class Filter_BinMorph_Disk implements PlugInFilter {
	
	static double  radius = 1.0;
	static boolean showfilter = false;
	static String  opstring = null;
	

	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	public void run(ImageProcessor orig) {
		if (showDialog()) { //sets RADIUS and OPSTRING			
			BinMorpher bm = new BinMorpherDisk(radius);
			Operation op = Operation.valueOf(opstring);
			bm.apply(orig,op);
			if (showfilter)
				bm.showFilter();
		}
	}
    
	boolean showDialog() {
		GenericDialog gd = new GenericDialog("Structuring Element (Disk)");
		gd.addNumericField("Radius", 1.0, 1, 5,"pixels");
		String[] ops = BinMorpher.getOpNames();
		gd.addChoice("Operation", ops, ops[0]);
		gd.addCheckbox("Display filter", false);
		
		gd.showDialog();
		if (gd.wasCanceled()) 
			return false;
		else {
			radius = gd.getNextNumber();
			showfilter = gd.getNextBoolean();
			opstring = gd.getNextChoice();
			return true;
		}
	}

}




