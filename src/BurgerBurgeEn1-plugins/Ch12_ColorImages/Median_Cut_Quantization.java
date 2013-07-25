package Ch12_ColorImages;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.color2.ColorQuantizer;
import imagingbook.pub.color2.MedianCutQuantizer;

public class Median_Cut_Quantization implements PlugInFilter {
	static int NCOLORS = 32;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB + NO_CHANGES;
	}
	
	public void run(ImageProcessor ip) {
		ColorProcessor cp = (ColorProcessor) ip.convertToRGB();
		
		// create a quantizer object
		ColorQuantizer quantizer = new MedianCutQuantizer(cp, NCOLORS);
		int qColors = quantizer.countQuantizedColors();
		
		// quantize to an indexed image
		ByteProcessor idxIp = quantizer.quantizeImage(cp);
		ImagePlus idxIm = new ImagePlus("Quantized Index Image (" + qColors + " colors)", idxIp);
		idxIm.show();
		
		// quantize to an RGB image
		int[] rgbPixels = quantizer.quantizeImage((int[]) cp.getPixels());
		ImageProcessor rgbIp = 
			new ColorProcessor(cp.getWidth(), cp.getHeight(), rgbPixels);
		(new ImagePlus("Quantized RGB Image (" + qColors + " colors)" , rgbIp)).show();
		
	}
}
