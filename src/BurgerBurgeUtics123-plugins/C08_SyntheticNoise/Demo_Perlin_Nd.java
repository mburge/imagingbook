package C08_SyntheticNoise;

import imagingbook.pub.noise.hashing.HashFun;
import imagingbook.pub.noise.perlin.PerlinNoiseGenNd;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

/**
 * This ImageJ plugin creates a new noise image using a N-dim. gradient noise 
 * generator. Several parameters can be adjusted. For details see
 * Ch. 8 of W. Burger and M. J. Burge. "Principles of Digital Image Processing –
 * Advanced Methods" (Vol. 3). Undergraduate Topics in Computer Science.
 * Springer-Verlag, London (2013). http://www.imagingbook.com
 * 
 * @author W. Burger
 * @version 2013/05/28
 */

public class Demo_Perlin_Nd  implements PlugIn {
	
	static int w = 300;
	static int h = 300;
	static int octaves = 3;
	static int seed = 2;

	static double f_min = 0.01; // f_max / (1 << (octaves-1));
	static double f_max = f_min * (1 << (octaves-1));
	
	static double persistence = 0.5;

	String title = this.getClass().getSimpleName() + " Seed=" + seed;
	
	public void run(String arg0) {
		int dim = 2;	// number of dimensions
		
		// choose hash function:
		HashFun hf = HashFun.create(seed);
//		HashFun hf = new Hash32Shift(seed);
//		HashFun hf = new Hash32ShiftMult(seed);
//		HashFun hf = new Hash32Ward(seed);
//		HashFun hf = new HashPermute(seed); 

		// create the noise generator:
		PerlinNoiseGenNd png = new PerlinNoiseGenNd(dim, f_min, f_max, persistence, hf);
		createNoiseImage(w, h, png).show();
	}
	
	ImagePlus createNoiseImage(int w, int h, PerlinNoiseGenNd ng) {
		ImageProcessor fp = new FloatProcessor(w,h);
		double[] X = new double[2];
		for (int v=0; v<h; v++){
			for (int u=0; u<w; u++){
				X[0] = u;
				X[1] = v;
				fp.putPixelValue(u, v, ng.NOISE(X));
			}
		}
		fp.setMinAndMax(-0.6,0.6); 	//fp.resetMinAndMax();
		ImagePlus nimg = new ImagePlus(title,fp);
		return nimg;
	}

}
