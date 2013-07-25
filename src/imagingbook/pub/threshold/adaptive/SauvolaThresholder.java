/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.threshold.adaptive;

import ij.plugin.filter.RankFilters;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import imagingbook.pub.threshold.BackgroundMode;
import imagingbook.pub.threshold.adaptive.AdaptiveThresholder;

/**
 * Adaptive thresholder as proposed in J. Sauvola and M. Pietikäinen, 
 * "Adaptive document image binarization", Pattern Recognition 33(2), 
 * 1135–1143 (2000).
 */
public class SauvolaThresholder extends AdaptiveThresholder {
	
	public static class Parameters {
		public int radius = 15;
		public double kappa =  0.5;
		public double sigmaMax =  128;
		public BackgroundMode bgMode = BackgroundMode.DARK;
	}
		
	private FloatProcessor Imean;
	private FloatProcessor Isigma;
	private final Parameters params;
	
	public SauvolaThresholder() {
		super();
		this.params = new Parameters();
	}
	
	public SauvolaThresholder(Parameters params) {
		super();
		this.params = params;
	}
	
	@Override
	public ByteProcessor getThreshold(ByteProcessor I) {
		FloatProcessor mean = (FloatProcessor) I.convertToFloat();
		FloatProcessor var = (FloatProcessor) mean.duplicate();
		
		RankFilters rf = new RankFilters();
		rf.rank(mean, params.radius, RankFilters.MEAN);
		Imean = mean;
		
		rf.rank(var, params.radius, RankFilters.VARIANCE);
		var.sqrt();
		Isigma = var;
		
		int width = I.getWidth();
		int height = I.getHeight();
		final double kappa = params.kappa;
		final double sigmaMax = params.sigmaMax;
		final boolean darkBg = (params.bgMode == BackgroundMode.DARK);
		
		ByteProcessor Q = new ByteProcessor(width, height);
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				final double sigma = Isigma.getf(u, v);
				final double mu = Imean.getf(u, v);
				final double diff = kappa * (sigma / sigmaMax - 1);
				int q = (int) Math.rint((darkBg) ? mu * (1 - diff) : mu	* (1 + diff));
				if (q < 0)
					q = 0;
				if (q > 255)
					q = 255;
				Q.set(u, v, q);
			}
		}
		return Q;
	}
	
}
