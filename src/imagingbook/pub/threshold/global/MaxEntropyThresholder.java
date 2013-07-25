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
 * Maximum entropy thresholder modeled after Kapur et al. (1985).
 */
public class MaxEntropyThresholder extends GlobalThresholder {
	
	static final double EPSILON = 1E-12;
	
	private double[] H0array = new double[256]; 	// only used for reporting
	private double[] H1array = new double[256]; 	// only used for reporting
	private double[] H01array = new double[256]; // only used for reporting
	
	private double[] S0 = null;
	private double[] S1 = null;
	
	public MaxEntropyThresholder() {
		super();
	}
	
	public int getThreshold(int[] h) {
		int K = h.length;	
		double[] p = normalize(h);		// normalized histogram (probabilities)
		makeTables(p);	// initialize S0, S1
		
		double P0 = 0, P1;
		int qMax = -1;
		double Hmax = Double.NEGATIVE_INFINITY;
		
		for (int q = 0; q <= K-1; q++) { // one more step for logging
			P0 = P0 + p[q];	
			P1 = 1 - P0;	
			double H0 = (P0 > EPSILON) ? -S0[q]/P0 + Math.log(P0) : 0;				
			double H1 = (P1 > EPSILON) ? -S1[q]/P1 + Math.log(P1) : 0;			
			double H01 = H0 + H1;
			
			H0array[q] = H0;	// logging only
			H1array[q] = H1;	// logging only
			H01array[q] = H01;	// logging only
			
			if (H01 > Hmax) {
				Hmax = H01;
				qMax = q;
			}
		}
		return qMax;
	}
	
	private void makeTables(double[] p) {
		int K = p.length;

		// make tables S0[], S1[]
		S0 = new double[K];
		S1 = new double[K];

		double s0 = 0;
		for (int i = 0; i < K; i++) {
			if (p[i] > EPSILON) {
				s0 = s0 + p[i] * Math.log(p[i]);
			}
			S0[i] = s0;
		}

		double s1 = 0;
		for (int i = K-1; i >= 0; i--) {
			S1[i] = s1;
			if (p[i] > EPSILON) {
				s1 = s1 + p[i] * Math.log(p[i]);
			}
		}
	}
}
