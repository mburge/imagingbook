package C02_Thresholding;

import java.util.LinkedList;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * Demo plugin making available a selection of global thresholders.
 * @author W. Burger
 * @version 2013/05/30
 */
public class Threshold_Global_All implements PlugInFilter {
	
	enum Mode {
		IsoData(Threshold_Global_Isodata.class), 
		MaxEntropy(Threshold_Global_MaxEntropy.class),
		Mean(Threshold_Global_Mean.class),
		Median(Threshold_Global_Median.class),
		MinError(Threshold_Global_MinError.class),
		MinMax(Threshold_Global_MinMax.class),
		Otsu(Threshold_Global_Otsu.class);
		
		Class<? extends PlugInFilter> pluginClass;
		
		Mode(Class<? extends PlugInFilter> cls) {
			this.pluginClass = cls;
		}
		
		static String[] getNames() {
			List<String> names = new LinkedList<String>();
			for(Mode val : Mode.values()) {
				names.add(val.name());
			}
			return names.toArray(new String[0]);
		}
	}
	
	ImagePlus imp = null;
	
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		String[] choices = Mode.getNames();
		GenericDialog gd = new GenericDialog("Global Thresholder");
		gd.addMessage("Select an algorithm:");
		gd.addChoice("Algorithm:", choices, choices[0]);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		
		int mi = gd.getNextChoiceIndex();
		Mode mode = Mode.values()[mi];
		String pluginName = mode.pluginClass.getCanonicalName();
		
		imp.unlock();
		IJ.runPlugIn(imp, pluginName, null);
	}

}
