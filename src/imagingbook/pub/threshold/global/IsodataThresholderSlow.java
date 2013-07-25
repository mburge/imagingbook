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
 * Slow version using explicit recomputation of background and foreground means 
 * in every iteration.
 */
public class IsodataThresholderSlow extends GlobalThresholder {
	
	private int MAX_ITERATIONS = 100;
	
	public IsodataThresholderSlow() {
		super();
	}

	public int getThreshold(int[] h) {
		int K = h.length;
		int q = (int) mean(h, 0, K-1); 	// start with the total mean
		int q_;
		
		int i = 0;	// iteration counter
		do {
			i++;
			int nB = count(h, 0, q);
			int nF = count(h, q+1, K-1);
			if (nB == 0 || nF == 0)
				return -1;
			double meanB = mean(h, 0, q);
			double meanF = mean(h, q+1, K-1);
			q_ = q;				
			q = (int)((meanB + meanF)/2);
		} while (q != q_ && i < MAX_ITERATIONS);
		
		return q;
	}

}
