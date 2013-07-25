/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.edgepreservingfilters;

import imagingbook.lib.filters.GenericFilter;
import imagingbook.lib.image.ImageAccessor;
import imagingbook.lib.math.Arithmetic;
import imagingbook.lib.math.VectorNorm;
import imagingbook.lib.math.VectorNorm.NormType;

/**
 * This class implements a bilateral filter as proposed in
 * C. Tomasi and R. Manduchi, "Bilateral Filtering for Gray and Color Images",
 * Proceedings of the 1998 IEEE International Conference on Computer Vision,
 * Bombay, India.
 * The filter uses Gaussian domain and range kernels and can be applied to all 
 * image types.
 * @author W. Burger
 * @version 2013/05/30
 */
public class BilateralFilter extends GenericFilter {
	
	public static class Parameters {
		public double sigmaD = 2; 		// sigma (width) of domain filter
		public double sigmaR = 50; 		// sigma (width) of range filter
		public NormType colorNormType = NormType.L2;
		
		static Parameters create(double sigmaD, double sigmaR) {
			Parameters p = new Parameters();
			p.sigmaD = sigmaD;
			p.sigmaR = sigmaR;
			return p;
		}
	}
	
	protected final Parameters params;
	
	private float[][] Hd;	// the domain kernel
	protected final int K;
	protected final float[] rgb = {0,0,0};
	protected final double sigmaR2;
	protected final VectorNorm colorNorm;
	protected final double colorScale;
	
	public BilateralFilter() {
		this(new Parameters());
	}
	
	// only for convenience / book compatibility:
	public BilateralFilter(double sigmaD, double sigmaR) {
		this(Parameters.create(sigmaD, sigmaR));
	}
	
	public BilateralFilter(Parameters params) {
		this.params = params;
		K = (int) Math.max(1, 3.5 * params.sigmaD);
		sigmaR2 = params.sigmaR * params.sigmaR;
		colorNorm = params.colorNormType.create();
		colorScale = Arithmetic.sqr(colorNorm.getScale(3));
		initialize();
	}
	
	private void initialize() {
		Hd = makeDomainKernel2D(params.sigmaD, K);
	}
	
	public float filterPixel(ImageAccessor.Gray I, int u, int v) {
		float S = 0;			// sum of weighted pixel values
		float W = 0;			// sum of weights
		
		float a = I.getp(u, v); // value of the current center pixel
		
		for (int m = -K; m <= K; m++) {
			for (int n = -K; n <= K; n++) {
				float b = I.getp(u + m, v + n);
				float wd = Hd[m + K][n + K];
				float wr = similarityGauss(a, b);
				float w = wd * wr;
				S = S + w * b;
				W = W + w;
			}
		}
		return S / W;
	}
	
	public float[] filterPixel(ImageAccessor.Color I, int u, int v) {
		float[] S = new float[3]; 	// sum of weighted RGB values
		float W = 0;				// sum of weights
		int[] a = new int[3];
		int[] b = new int[3];
		
		I.getp(u, v, a);			// value of the current center pixel
		
		for (int m = -K; m <= K; m++) {
			for (int n = -K; n <= K; n++) {
				I.getp(u + m, v + n, b);
				float wd = Hd[m + K][n + K];
				float wr = similarityGauss(a, b);
				float w = wd * wr;
				S[0] = S[0] + w * b[0];
				S[1] = S[1] + w * b[1];
				S[2] = S[2] + w * b[2];
				W = W + w;
			}
		}
		rgb[0] = Math.round(S[0] / W);
		rgb[1] = Math.round(S[1] / W);
		rgb[2] = Math.round(S[2] / W);
 		return rgb;
 	}
	
	// ------------------------------------------------------
	// This returns the weights for a Gaussian range kernel (scalar version):
	protected float similarityGauss(float a, float b) {
		double dI = a - b;
		return (float) Math.exp(-(dI * dI) / (2 * sigmaR2));
	}
	
	// This returns the weights for a Gaussian range kernel (color vector version):
	protected float similarityGauss(int[] a, int[] b) {
		double d2 = colorScale * colorNorm.distance2(a, b);
		return (float) Math.exp(-d2 / (2 * sigmaR2));
	}
	
	// Color distances need to be scaled to yield the same range of
	// values. This method returns the scale factor for squared 
	// distances, since this is what we use in the Gaussian.
//	static double getColorDistanceScale(NormType norm) {
//		double s = 1.0;
//		switch (norm) {
//		case L1 : s = 1/3.0; break;				// L1-dist is in [0,...,3*255*3]
//		case L2:  s = Math.sqrt(1/3.0); break;	// L2-dist is in [0,...,sqrt(3*255^2)] = [0,...,sqrt(3)*255]
//		case Linf: s = 1.0; break;				// Linf-dist is in [0,...,255]
//		default: break;
//		}
//		return s * s;	// scale factor for the squared distance
//	}

	// ------------------------------------------------------

	protected float[][] makeDomainKernel2D(double sigma, int K) {
		int size = K + 1 + K;
		float[][] domainKernel = new float[size][size]; //center cell = kernel[K][K]
		double sigma2 = sigma * sigma;
		double scale = 1.0 / (2 * Math.PI * sigma2);
		for (int i = 0; i < size; i++) {
			double x = K - i;
			for (int j = 0; j < size; j++) {
				double y = K - j;
				domainKernel[i][j] =  (float) (scale * Math.exp(-0.5 * (x*x + y*y) / sigma2));
			}
		}
		return domainKernel;
	}
	
	protected float[] makeRangeKernel(double sigma, int K) {
		int size = K + 1 + K;
		float[] rangeKernel = new float[size]; //center cell = kernel[K]
		double sigma2 = sigma * sigma;
		for (int i = 0; i < size; i++) {
			double x = K - i;
			rangeKernel[i] =  (float) Math.exp(-0.5 * (x*x) / sigma2);
		}
		return rangeKernel;
	}

}
