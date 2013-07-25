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
import imagingbook.pub.threshold.BackgroundMode;


/**
 * This implementation of Bernsen's thresholder uses a circular support region,
 * implemented with ImageJ's built-in rank-filter methods.
 */
public class BernsenThresholder extends AdaptiveThresholder {
	
	public static class Parameters {
		public int radius = 15;
		public int cmin = 15;
		public BackgroundMode bgMode = BackgroundMode.DARK;
	}
	
	private final Parameters params;
	
	public BernsenThresholder() {
		this.params = new Parameters();
	}
	
	public BernsenThresholder(Parameters params) {
		this.params = params;
	}

	@Override
	public ByteProcessor getThreshold(ByteProcessor I) {
		int width = I.getWidth();
		int height = I.getHeight();
		ByteProcessor Imin = (ByteProcessor) I.duplicate();
		ByteProcessor Imax = (ByteProcessor) I.duplicate();

		RankFilters rf = new RankFilters();
		rf.rank(Imin, params.radius, RankFilters.MIN);
		rf.rank(Imax, params.radius, RankFilters.MAX);

		int q = (params.bgMode == BackgroundMode.DARK) ? 256 : 0;
		ByteProcessor Q = new ByteProcessor(width, height);

		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int gMin = Imin.get(u, v);
				int gMax = Imax.get(u, v);
				int c = gMax - gMin;
				if (c >= params.cmin)
					Q.set(u, v, (gMin + gMax) / 2);
				else
					Q.set(u, v, q);
			}
		}
		return Q;
	}
	
}
