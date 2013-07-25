package C08_SyntheticNoise;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import imagingbook.pub.noise.hashing.HashFun;
import imagingbook.pub.noise.hashing.HashPermute;


public class Test_Hash_Randomness  implements PlugIn {
	
	static int seed = 1;
	
	public void run(String arg0) {
		int w = 1500;
		int h = 1000;
		ImagePlus fim0 = NewImage.createFloatImage("Plane0", w, h, 1, NewImage.FILL_BLACK);
		ImageProcessor fp0 = fim0.getProcessor();
		ImagePlus fim1 = NewImage.createFloatImage("Plane1", w, h, 1, NewImage.FILL_BLACK);
		ImageProcessor fp1 = fim1.getProcessor();
		
		HashFun hf = new HashPermute(0);
//		HashFun hf = new Hash32Shift(0);
//		HashFun hf = new Hash32ShiftMult(0);
//		HashFun hf = new Hash32Ward(31);	// bad in channel 0 - don't use!
		
		int k = 0;
		for (int v=0; v<h; v++){
			for (int u=0; u<w; u++){
//				double val = hf.hash(k); fp.putPixelValue(u, v, val);
				double[] val = hf.hash(u,v);
				fp0.putPixelValue(u, v, val[0]);
				fp1.putPixelValue(u, v, val[1]);
				k = k + 1;
			}
		}
		
		fp0.resetMinAndMax(); fim0.show();
		fp1.resetMinAndMax(); fim1.show();
	}
	
//	void printHashVals(HashFun hf) {
//		int N = 20;
//		for (int i=0; i<N; i++) {
//			double h = hf.hash_x(i,0,0);
//			IJ.log(i + ": " + h);
//		}
//	}

}
