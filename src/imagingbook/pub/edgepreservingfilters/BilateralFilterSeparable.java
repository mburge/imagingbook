/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.edgepreservingfilters;

import ij.process.ImageProcessor;
import imagingbook.lib.image.ImageAccessor;

/**
 * XY-Separated version of bilateral filter using Gaussian domain and 
 * range kernels. This filter can be applied to all image types.
 * @author W. Burger
 * @version 2013/05/30
 */
public class BilateralFilterSeparable extends BilateralFilter {
	
	enum Direction {
		Horizontal, Vertical
	}
	
	private Direction direction;
	private float[] Hr;			// domain kernel is one-dimensional here!
	
	public BilateralFilterSeparable() {
		super();
	}
	
	public BilateralFilterSeparable(double sigmaD, double sigmaR) {
		super(sigmaD, sigmaR);
	}
	
	public BilateralFilterSeparable(Parameters params) {
		super(params);
		initialize();
	}
	
	private void initialize() {
		Hr = makeDomainKernel1D(params.sigmaD, K);
	}
	
	// overrides the corresponding method in GenericFilter
	public void applyTo(ImageProcessor target) {
		// apply this filter twice, with 'direction' set to different values:
		for (Direction d : Direction.values()) {
			direction = d;
			//IJ.showStatus("filter direction: " + direction.name());
			super.applyTo(target);  // call original method
		}
	}

	// ------------------------------------------------------
	
	public float filterPixel(ImageAccessor.Gray I, int u, int v) {
		float a = I.getp(u, v);
		float S = 0;
		float W = 0;
		if (direction == Direction.Horizontal) {
			for (int m = -K; m <= K; m++) {
				float b = I.getp(u + m, v);
				float wd = Hr[m + K];				// domain weight
				float wr = similarityGauss(a, b);	// range weight
				float w = wd * wr;
				S = S + w * b;
				W = W + w;
			}
		}
		else { // (direction == Direction.Vertical)
			for (int n = 0; n <= K; n++) {
				float b = I.getp(u, v + n);
				float wd = Hr[n + K];				// domain weight
				float wr = similarityGauss(a, b);	// range weight
				float w = wd * wr;
				S = S + w * b;
				W = W + w;
			}
		}
 		return S / W;
 	}
	
	public float[] filterPixel(ImageAccessor.Color I, int u, int v) {
		final int[] a = new int[3];
		final int[] b = new int[3];
		final float[] S = new float[3]; 	// sum of weighted RGB (initialized to zero)
		float W = 0;						// sum of weights
		I.getp(u, v, a);
		
		if (direction == Direction.Horizontal) {
			for (int m = -K; m <= K; m++) {
				I.getp(u + m, v, b);
				float wd = Hr[m + K];
				float wr = similarityGauss(a, b);
				float w = wd * wr;
				S[0] = S[0] + w * b[0];
				S[1] = S[1] + w * b[1];
				S[2] = S[2] + w * b[2];
				W = W + w;
			}
		}
		else { // (direction == Direction.Vertical)
			for (int n = -K; n <= K; n++) {
				I.getp(u, v + n, b);
				float wd = Hr[n + K];
				float wr = similarityGauss(a, b);
				float w = wd * wr;
				S[0] = S[0] + b[0] * w;
				S[1] = S[1] + b[1] * w;
				S[2] = S[2] + b[2] * w;
				W = W + w;
			}
		}
		rgb[0] = Math.round(S[0] / W);
		rgb[1] = Math.round(S[1] / W);
		rgb[2] = Math.round(S[2] / W);
 		return rgb;
 	}
	
	// ------------------------------------------------------

	private float[] makeDomainKernel1D(double sigma, int K) {
		int size = K + 1 + K;
		float[] domainKernel = new float[size];
		double sigma2 = sigma * sigma;
		double scale = 1.0 / (Math.sqrt(2 * Math.PI) * sigma);
		
		for (int i = 0; i < size; i++) {
			double x = K - i;
			domainKernel[i] =  (float) (scale * Math.exp(-0.5 * (x*x) / sigma2));
		}
		return domainKernel;
	}
	
}
