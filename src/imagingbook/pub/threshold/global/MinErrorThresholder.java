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
 * Minimum Error thresholder after Kittler and Illingworth (1986).
 */
public class MinErrorThresholder extends GlobalThresholder {
	
	static final double EPSILON = 1E-12;
	
	private double[] S2_0, S2_1;
	private int N;
	
	public MinErrorThresholder() {
		super();
	}
	
	public int getThreshold(int[] h) {
		int K = h.length;
		makeSigmaTables(h);	// set up S2_0, S2_1, N

		int n0 = 0, n1;
		int qMin = -1;
		double eMin = Double.POSITIVE_INFINITY;
		for (int q = 0; q <= K-2; q++) {
			n0 = n0 + h[q];
			n1 = N - n0;
			if (n0 > 0 && n1 > 0) { 
				double P0 = (double) n0 / N;	// could use n0, n1 instead
				double P1 = (double) n1 / N;
				double e = // 1.0 + 
					P0 * Math.log(S2_0[q]) + P1 * Math.log(S2_1[q])
					- 2 * (P0 * Math.log(P0) + P1 * Math.log(P1));
				if (e < eMin) {		// minimize e;
					eMin = e;
					qMin = q;
				}
			}
		}
		return qMin; 
	}
	
	private void makeSigmaTables(int[] h) {
		int K = h.length;
		final double unitVar = 1d/12;	// variance of a uniform distribution in unit interval
		S2_0 = new double[K];
		S2_1 = new double[K];
		
		int n0 = 0;
		long A0 = 0;
		long B0 = 0; 
		for (int q = 0; q < K; q++) {
			long ql = q;	// need a long type to avoid overflows
			n0 = n0 + h[q];
			A0 = A0 + h[q] * ql;
			B0 = B0 + h[q] * ql*ql;
			S2_0[q] = (n0 > 0) ? 
					unitVar + ((double)B0 - (double)A0*A0/n0)/n0 : 0;	
		}
		
		N = n0;
		
		int n1 = 0;
		long A1 = 0;
		long B1 = 0; 
		S2_1[K-1] = 0;
		for (int q = K-2; q >= 0; q--) {
			long qp1 = q+1;
			n1 = n1 + h[q+1];
			A1 = A1 + h[q+1] * qp1;
			B1 = B1 + h[q+1] * qp1*qp1;
			S2_1[q] = (n1 > 0) ? 
					unitVar + ((double)B1 - (double)A1*A1/n1)/n1 : 0;
		}
	}
	
}
