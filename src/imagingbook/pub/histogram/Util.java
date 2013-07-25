/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.histogram;

public class Util {

	static int[] makeGaussianHistogram () {
		return makeGaussianHistogram(128, 50);
	}

	public static int[] makeGaussianHistogram (double mean, double sigma) {
		int[] h = new int[256];
		double sigma2 = 2 * sigma * sigma;
		for (int i = 0; i < h.length; i++) {
			double x = mean - i;
			double g = Math.exp(-(x * x) / sigma2) / sigma;
			h[i] = (int) Math.round(10000 * g);
		}
		return h;
	}

	public static double[] normalizeHistogram (double[] h) {
		// find max histogram entry
		double max = h[0];
		for (int i = 0; i < h.length; i++) {
			if (h[i] > max)
				max = h[i];
		}
		if (max == 0) return null;
		// normalize
		double[] hn = new double[h.length];
		double s = 1.0/max;
		for (int i = 0; i < h.length; i++) {
			hn[i] = s * h[i];
		}
		return hn;
	}

	//------------------------------------------------------
	
	public static double[] normalizeHistogram (int[] h) {
		// find the max histogram entry
		int max = h[0];
		for (int i = 0; i < h.length; i++) {
			if (h[i] > max)
				max = h[i];
		}
		if (max == 0) return null;
		// normalize
		double[] hn = new double[h.length];
		double s = 1.0/max;
		for (int i = 0; i < h.length; i++) {
			hn[i] = s * h[i];
		}
		return hn;
	}

	public static double[] Cdf (int[] h) {
		// returns the cumul. probability distribution function (cdf) for histogram h
		int K = h.length;
		int n = 0;		// sum all histogram values		
		for (int i=0; i<K; i++)	{ 	
			n = n + h[i]; 
		}
		double[] P = new double[K];
		int c = h[0];
		P[0] = (double) c / n;
		for (int i = 1; i < K; i++) {
	    	c = c + h[i];
	        P[i] = (double) c / n;
	    }
	    return P;
	}

	static double[] Pdf (int[] h) {
		// returns the probability distribution function (pdf) for histogram h
		int K = h.length;
		int n = 0;			// sum all histogram values	
		for (int i = 0; i < K; i++) {
			n = n + h[i]; 
		}
		double[] p = new double[K];
		for (int i = 0; i < h.length; i++) {
			p[i] =  (double) h[i] / n;
		}
		return p;
	}

}
