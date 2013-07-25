/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.threshold.global;

import imagingbook.pub.threshold.global.GlobalThresholder;

public class QuantileThresholder extends GlobalThresholder {
	
	private double b = 0.5;	// quantile of expected background pixels

	public QuantileThresholder() {
		super();
	}
	
	public QuantileThresholder(double b) {
		super();
		this.b = b;
	}

	public int getThreshold(int[] h) {
		int K = h.length;
		int N = sum(h);
		
		double n = N * b;	
		int i = 0;
		int c = h[0];
		while (i < K && c < n) {
			i++;
			c+= h[i];
		}
		
		int q = (c < N) ? i : -1; 
		return q;
	}
}
