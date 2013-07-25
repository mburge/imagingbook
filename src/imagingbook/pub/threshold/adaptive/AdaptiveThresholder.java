/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.threshold.adaptive;

import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import imagingbook.pub.threshold.Thresholder;

public abstract class AdaptiveThresholder extends Thresholder {
	
	public abstract ByteProcessor getThreshold(ByteProcessor bp);
	
	public void threshold(ByteProcessor bp, ByteProcessor Q) {
		final int w = bp.getWidth();
		final int h = bp.getHeight();
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int p = bp.get(u, v);
				int q = Q.get(u, v);
				bp.set(u, v, (p <= q) ? 0 : 255);
			}
		}
	}
	
	public void threshold(ByteProcessor ip) {
		ByteProcessor Q = getThreshold(ip); 
		threshold(ip, Q);
	}
	
	// change to use an ImageAccessor!
	protected int getPaddedPixel(ByteProcessor bp, int u, int v) {
		final int w = bp.getWidth();
		final int h = bp.getHeight();
		if (u < 0)
			u = 0;
		else if (u >= w)
			u = w - 1;
		if (v < 0)
			v = 0;
		else if (v >= h)
			v = h - 1;
		return bp.get(u, v);
	}
	
	// used for logging/testing only
	protected double[] getLine(ByteProcessor bp, int v) {
		double[] line = new double[bp.getWidth()];
		for (int u = 0; u < line.length; u++) {
			line[u] = bp.get(u, v);
		}
		return line;
	}
	
	// used for logging/testing only
	protected double[] getLine(FloatProcessor fp, int v) {
		double[] line = new double[fp.getWidth()];
		for (int u = 0; u < line.length; u++) {
			line[u] = fp.getf(u, v);
		}
		return line;
	}
}
