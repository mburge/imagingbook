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

/**
 * This thresholder implements the algorithm proposed by Ridler and Calvard (1978),
 * T.W. Ridler, S. Calvard, Picture thresholding using an iterative selection method,
 * IEEE Trans. System, Man and Cybernetics, SMC-8 (August 1978) 630-632.
 * described in Glasbey/Horgan: "Image Analysis for the Biological Sciences" (Ch. 4).
 * 
 * Fast version using tables of background and foreground means.
 */
public class IsodataThresholder extends GlobalThresholder {
	
	private int MAX_ITERATIONS = 100;

	private double[] M0 = null;		// table of background means
	private double[] M1 = null;		// table of foreground means
	
	public IsodataThresholder() {
		super();
	}

	public int getThreshold(int[] h) {
		makeMeanTables(h);
		int K = h.length;
		int q = (int) M0[K-1]; 	// start with total mean
		int q_;
		
		int i = 0;
		do {
			i++; 
			if (M0[q]<0 || M1[q]<0)  // background or foreground is empty
				return -1;
			q_ = q;				
			q = (int) ((M0[q] + M1[q])/2);
		} while (q != q_ && i < MAX_ITERATIONS);
		
		return q;
	}
	
	private int makeMeanTables(int[] h) {
		int K = h.length;
		M0 = new double[K];
		M1 = new double[K];
		int n0 = 0;
		long s0 = 0;
		for (int q = 0; q < K; q++) {
			n0 = n0 + h[q];
			s0 = s0 + q * h[q];
			M0[q] = (n0 > 0) ? ((double) s0)/n0 : -1;
		}
		int N = n0;
		
		int n1 = 0;
		long s1 = 0;
		M1[K-1] = 0;
		for (int q = h.length-2; q >= 0; q--) {
			n1 = n1 + h[q+1];
			s1 = s1 + (q+1) * h[q+1];
			M1[q] = (n1 > 0) ? ((double) s1)/n1 : -1;
		}
		
		return N;
	}
	
}
