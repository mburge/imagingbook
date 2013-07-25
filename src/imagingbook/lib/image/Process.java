package imagingbook.lib.image;

import ij.plugin.filter.Convolver;
import ij.process.Blitter;
import ij.process.FloatProcessor;


/**
 * This class provides some static utility methods for ImageJ' ImageProcessors.
 */
public class Process {
	
	public static FloatProcessor convolve1h (FloatProcessor fp, float[] h) {
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		conv.convolve(fp, h, 1, h.length);
		return fp;
	}
	
	public static FloatProcessor convolve1v (FloatProcessor fp, float[] h) {
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		conv.convolve(fp, h, h.length, 1);
		return fp;
	}
	
	public static FloatProcessor convolve2 (FloatProcessor fp, float[] h) {
		convolve1h(fp, h);
		convolve1v(fp, h);
		return fp;
	}
	
	public static FloatProcessor sqr (FloatProcessor fp) {
		fp.sqr();
		return fp;
	}
	
	public static FloatProcessor mult (FloatProcessor fp1, FloatProcessor fp2) {
		fp1.copyBits(fp2, 0, 0, Blitter.MULTIPLY);
		return fp1;
	}

}
