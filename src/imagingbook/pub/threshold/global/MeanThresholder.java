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

public class MeanThresholder extends GlobalThresholder {
	
	public MeanThresholder() {
		super();
	}

	public int getThreshold(int[] h) {
		// calculate mean of entire image:
		int K = h.length;
		int cnt = 0;
		long sum = 0;
		for (int i=0; i<K; i++) {
			cnt += h[i];
			sum += i * h[i];
		}
		
		int mean = (int) Math.rint((double)sum / cnt);

		// count resulting background pixels:
		int n0 = 0;
		for (int i = 0; i <= mean; i++) {
			n0 += h[i];
		}
		
		// determine if background or foreground is empty:
		int q = (n0 < cnt) ? mean : -1; 
		return q;
	}
}
