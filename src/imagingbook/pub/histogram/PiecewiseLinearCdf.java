/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.histogram;

public class PiecewiseLinearCdf {
	private int K;
	private int[] iArr;
	private double[] pArr;
	
	public PiecewiseLinearCdf(int K, int[] ik, double[] Pk) {
		this.K = K; // number of intensity values (typ. 256)
		int N = ik.length;
		iArr = new int[N + 2];		// array of intensity values
		pArr = new double[N + 2];	// array of cum. distribution values
		iArr[0] = -1; 
		pArr[0] = 0;
		for (int i = 0; i < N; i++) {
			iArr[i + 1] = ik[i];
			pArr[i + 1] = Pk[i];
		}
		iArr[N + 1] = K - 1;
		pArr[N + 1] = 1;
	}
	
	double getCdf(int i) {
		if (i < 0)
			return 0;
		else if (i >= K - 1)
			return 1;
		else {
			int s = 0, N = iArr.length - 1;
			for (int j = 0; j <= N; j++) { // find s (segment index)
				if (iArr[j] <= i)
					s = j;
				else
					break;
			}
			return pArr[s] + (i - iArr[s])
					* ((pArr[s + 1] - pArr[s]) / (iArr[s + 1] - iArr[s]));
		}
	}
	
	int getInverseCdf(double z) {
		if (z < getCdf(0))
			return 0;
		else if (z >= 1)
			return K - 1;
		else {
			int r = 0, N = iArr.length - 1;
			for (int j = 0; j <= N; j++) { // find r (segment index)
				if (pArr[j] <= z)
					r = j;
				else
					break;
			}
			return (int) Math.round(iArr[r] + (z - pArr[r])
					* ((iArr[r + 1] - iArr[r]) / (pArr[r + 1] - pArr[r])));
		}
	}
	
	// for testing only:
	public double[] getPdf() {	
		double[] prob = new double[K];
		prob[0] =  getCdf(0);
		for (int i = 1; i < K; i++) {
			prob[i] =  getCdf(i) - getCdf(i-1);
		}
		return prob;
	}
	
}
