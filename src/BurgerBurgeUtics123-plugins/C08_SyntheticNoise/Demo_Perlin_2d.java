package C08_SyntheticNoise;

import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.noise.hashing.HashFun;
import imagingbook.pub.noise.hashing.HashPermute;
import imagingbook.pub.noise.perlin.PerlinNoiseGen2d;

/**
 * This ImageJ plugin creates a new noise image using a 2D gradient noise 
 * generator. Several parameters can be adjusted. For details see
 * Ch. 8 of W. Burger and M. J. Burge. "Principles of Digital Image Processing –
 * Advanced Methods" (Vol. 3). Undergraduate Topics in Computer Science.
 * Springer-Verlag, London (2013). http://www.imagingbook.com
 * 
 * @author W. Burger
 * @version 2013/05/28
 */

public class Demo_Perlin_2d implements PlugIn {
	int w = 300;				// image width
	int h = 300;				// image height
	int K = 3;					// number of noise frequencies
	double f_min = 0.01; 		// min. frequency (in cycles per pixel unit) 
	double f_max = f_min * Math.pow(2, K);
	double persistence = 0.5;	// persistence
	int seed = 2;				// hash seed 
	HashFun hf = new HashPermute(seed); 
//	HashFun hf = new Hash32Ward(seed);
//	HashFun hf = new Hash32Shift(seed);
//	HashFun hf = new Hash32ShiftMult(seed);
//	HashFun hf = HashFun.create(seed);

	public void run(String arg0) {
		// create the noise generator:
		PerlinNoiseGen2d ng = new PerlinNoiseGen2d(f_min, f_max, persistence, hf);
		// create a new image and fill with noise:
		ImageProcessor fp = new FloatProcessor(w, h);
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				double val = ng.NOISE(u, v);
				fp.putPixelValue(u, v, val);
			}
		}
		// display the new image:
		(new ImagePlus("Perlin Noise Image", fp)).show();
	}
}
