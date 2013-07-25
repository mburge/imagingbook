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
public class Threshold_Adaptive_All implements PlugInFilter {
	
	enum Mode {
		Bernsen(Threshold_Adaptive_Bernsen.class), 
		Interpolating(Threshold_Adaptive_Interpolating.class),
//		NiblackBox(Threshold_Adaptive_NiblackBox.class),
//		NiblackDisk(Threshold_Adaptive_NiblackDisk.class),
//		NiblackGauss(Threshold_Adaptive_NiblackGauss.class),
		Niblack(Threshold_Adaptive_Niblack.class),
		Sauvola(Threshold_Adaptive_Sauvola.class);
		
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
