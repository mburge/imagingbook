/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.threshold.adaptive;

import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.RankFilters;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import imagingbook.pub.threshold.BackgroundMode;

/**
 * This version of Niblack's thresholder uses a circular support region, implemented 
 * with IJ's rank-filter methods.
 */
public abstract class NiblackThresholder extends AdaptiveThresholder {
	
	public static class Parameters {
		public int radius = 15;
		public double kappa =  0.30;
		public int dMin = 5;
		public BackgroundMode bgMode = BackgroundMode.DARK;
	}
	
	private final Parameters params;
	protected FloatProcessor Imean;
	protected FloatProcessor Isigma;

	public NiblackThresholder () {
		super();
		this.params = new Parameters();
	}

	public NiblackThresholder (Parameters params) {
		super();
		this.params = params;
	}
	
	protected abstract void makeMeanAndVariance(ByteProcessor I, Parameters params);
	
	@Override
	public ByteProcessor getThreshold(ByteProcessor I) {
		int w = I.getWidth();
		int h = I.getHeight();
		makeMeanAndVariance(I, params);
		ByteProcessor Q = new ByteProcessor(w, h);
		final double kappa = params.kappa;
		final int dMin = params.dMin;
		final boolean darkBg = (params.bgMode == BackgroundMode.DARK);
		
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				double sigma = Isigma.getf(u, v);
				double mu = Imean.getf(u, v);
				double diff = kappa * sigma + dMin;
				int q = (int) Math.rint((darkBg) ? mu + diff : mu - diff);
				if (q < 0)	 q = 0;
				if (q > 255) q = 255;
				Q.set(u, v, q);
			}
		}
		return Q;
	}
	
	// -----------------------------------------------------------------------
	
	public static class Box extends NiblackThresholder {

		public Box() {
			super();
		}
		
		public Box(Parameters params) {
			super(params);
		}

		@Override
		protected void makeMeanAndVariance(ByteProcessor I, Parameters params) {
			int width = I.getWidth();
			int height = I.getHeight();
			Imean =  new FloatProcessor(width, height);
			Isigma =  new FloatProcessor(width, height);
			final int radius = params.radius;
			final int n = (radius + 1 + radius) * (radius + 1 + radius);

			for (int v = 0; v < height; v++) {
				for (int u = 0; u < width; u++) {
					long A = 0;	// sum of image values in support region
					long B = 0;	// sum of squared image values in support region
					for (int j = -radius; j <= radius; j++) {
						for (int i = -radius; i <= radius; i++) {
							int p = getPaddedPixel(I, u + i, v + j); // this is slow!
							A = A + p;
							B = B + p * p;
						}
					}
					Imean.setf(u, v, (float) A / n);
					Isigma.setf(u, v, (float) Math.sqrt((B - (double) (A * A) / n) / n));
				}
			}
		}
		
	}
	
	// -----------------------------------------------------------------------
	
	public static class Disk extends NiblackThresholder {
		
		public Disk() {
			super();
		}
		
		public Disk(Parameters params) {
			super(params);
		}

		@Override
		protected void makeMeanAndVariance(ByteProcessor I, Parameters params) {
			FloatProcessor mean = (FloatProcessor) I.convertToFloat();
			FloatProcessor var =  (FloatProcessor) mean.duplicate();
			
			RankFilters rf = new RankFilters();
			rf.rank(mean, params.radius, RankFilters.MEAN);
			Imean = mean;
			
			rf.rank(var, params.radius, RankFilters.VARIANCE);
			var.sqrt();
			Isigma = var;
		}
	}
	
	// -----------------------------------------------------------------------
	
	public static class Gauss extends NiblackThresholder {
		
		public Gauss() {
			super();
		}
		
		public Gauss(Parameters params) {
			super(params);
		}
		
		@Override
		protected void makeMeanAndVariance(ByteProcessor I, Parameters params) {
			// //uses ImageJ's GaussianBlur
			// local variance over square of size (size + 1 + size)^2
			int width = I.getWidth();
			int height = I.getHeight();
			
			Imean = new FloatProcessor(width,height);
			Isigma = new FloatProcessor(width,height);

			FloatProcessor A = (FloatProcessor) I.convertToFloat();
			FloatProcessor B = (FloatProcessor) A.duplicate();
			B.sqr();
			
			GaussianBlur gb = new GaussianBlur();
			double sigma = params.radius * 0.6;	// sigma of Gaussian filter should be approx. 0.6 of the disk's radius
			gb.blurFloat(A, sigma, sigma, 0.002);
			gb.blurFloat(B, sigma, sigma, 0.002);

			for (int v = 0; v < height; v++) {
				for (int u = 0; u < width; u++) {
					float a = A.getf(u, v);
					float b = B.getf(u, v);
					Imean.setf(u, v, a);
					Isigma.setf(u, v, (float) Math.sqrt(b - a * a));
				}
			}
		}
	}

}
