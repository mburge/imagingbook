package xC_Appendix;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

public class Convert_ImagePlus_To_Gray8 implements PlugInFilter {
	ImagePlus imp = null;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL; 	// this plugin accepts any image
	}

	public void run(ImageProcessor ip) {
		ImageConverter iConv = new ImageConverter(imp);
		iConv.convertToGray8();
		ip = imp.getProcessor();	// ip is now of type ByteProcessor
		// process grayscale image ...
		ip.sharpen();
	}
}
