/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.histogram;

import imagingbook.pub.histogram.PiecewiseLinearCdf;
import imagingbook.pub.histogram.Util;

public class HistogramMatcher {
	
	public HistogramMatcher() {
	}
	
	/**
	 * @param hA histogram of target image
	 * @param hR reference histogram
	 * @return the mapping function F() to be applied to the target image as an int table.
	 */
	public int[] matchHistograms (int[] hA, int[] hR) {
		int K = hA.length;
		double[] PA = Util.Cdf(hA); // get CDF of histogram hA
		double[] PR = Util.Cdf(hR); // get CDF of histogram hR
		int[] F = new int[K]; // pixel mapping function f()

		// compute pixel mapping function f():
		for (int a = 0; a < K; a++) {
			int j = K - 1;
			do {
				F[a] = j;
				j--;
			} while (j >= 0 && PA[a] <= PR[j]);
		}
		return F;
	}
	
	public int[] matchHistograms(int[] hA, PiecewiseLinearCdf PR) {
		int K = hA.length;
		double[] PA = Util.Cdf(hA); // get p.d.f. of histogram Ha
		int[] F = new int[K]; // pixel mapping function f()

		// compute pixel mapping function f():
		for (int a = 0; a < K; a++) {
			double b = PA[a];
			F[a] = PR.getInverseCdf(b);
		}
		return F;
	}
}
