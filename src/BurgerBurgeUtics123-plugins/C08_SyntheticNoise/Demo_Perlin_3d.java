package C08_SyntheticNoise;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import imagingbook.pub.noise.hashing.Hash32Shift;
import imagingbook.pub.noise.hashing.HashFun;
import imagingbook.pub.noise.perlin.PerlinNoiseGenNd;

/**
 * This ImageJ plugin creates a new noise image using a 3D gradient noise 
 * generator. Several parameters can be adjusted. For details see
 * Ch. 8 of W. Burger and M. J. Burge. "Principles of Digital Image Processing –
 * Advanced Methods" (Vol. 3). Undergraduate Topics in Computer Science.
 * Springer-Verlag, London (2013). http://www.imagingbook.com
 * 
 * @author W. Burger
 * @version 2013/05/28
 */

public class Demo_Perlin_3d  implements PlugIn {
	
	static int wx = 300;
	static int wy = 300;
	static int wz = 10;		int stepZ = 5;
	
	int dim = 3;
	
	static int octaves = 6;
	static int seed = 2;

	static double f_min = 0.01; // f_max / (1 << (octaves-1));
	static double f_max = f_min * (1 << (octaves-1));
	
	static double persistence = 0.5;

	String title = this.getClass().getSimpleName() + " Seed=" + seed;
	
	public void run(String arg0) {
		
		// choose hash function:
//		HashFun hf = HashFun.create(seed);
		HashFun hf = new Hash32Shift(seed);
//		HashFun hf = new Hash32ShiftMult(seed);
//		HashFun hf = new Hash32Ward(seed);
//		HashFun hf = new HashPermute(seed); 

		// create the noise generator:
		PerlinNoiseGenNd png = new PerlinNoiseGenNd(dim, f_min, f_max, persistence, hf);
		createNoiseImage(wx, wy, wz, png).show();
	}
	
	ImagePlus createNoiseImage(int wx, int wy, int wz, PerlinNoiseGenNd ng) {
		ImagePlus stackImg = 
			NewImage.createFloatImage(title, wx, wy, wz, NewImage.FILL_BLACK);
		ImageStack stack = stackImg.getStack();
		double[] X = new double[dim];
		IJ.showStatus("creating noise volume ...");
		for (int z=1; z<=wz; z++) {
			IJ.showProgress(z, wz);
			ImageProcessor fp = stack.getProcessor(z);
			for (int v=0; v<wy; v++){
				for (int u=0; u<wx; u++){
					X[0] = u;
					X[1] = v;
					X[2] = z * stepZ;
					fp.putPixelValue(u, v, ng.NOISE(X));
				}
			}
			//fp.setMinAndMax(-0.6,0.6); 	//fp.resetMinAndMax();
		}
		IJ.showStatus("");
		stackImg.setDisplayRange(-0.6,0.6);
		return stackImg;
	}

}
