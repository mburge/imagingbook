/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.threshold;

public abstract class Thresholder {
	
	// compute the sum of a histogram array
	protected int sum(int[] h) {
		int cnt = 0;
		for (int i=0; i<h.length; i++) {
			cnt += h[i];
		}
		return cnt;
	}
	
	protected int count(int[] h) {
		return count(h,0,h.length-1);
	}
	
	// compute the population of a histogram from index lo...hi
	protected int count(int[] h, int lo, int hi) {
		if (lo < 0) lo = 0;
		if (hi >= h.length) hi = h.length-1;
		int cnt = 0;
		for (int i = lo; i <= hi; i++) {
			cnt += h[i];
		}
		return cnt;
	}
	
	protected double mean(int[] h) {
		return mean(h,0,h.length-1);
	}
	
	protected double mean(int[] h, int lo, int hi) {
		if (lo < 0) lo = 0;
		if (hi >= h.length) hi = h.length-1;
		long cnt = 0;
		long sum = 0;
		for (int i=lo; i<=hi; i++) {
			cnt = cnt + h[i];
			sum = sum + i * h[i];
		}
		if (cnt > 0)
			return ((double)sum) / cnt;
		else 
			return 0;
	}
	
	protected double sigma2(int[] h) {
		return sigma2(h, 0, h.length-1);
	}
			
	// this is a slow version, only for testing:
//	protected double sigma2(int[] h, int lo, int hi) {
//		if (lo < 0) lo = 0;
//		if (hi >= h.length) hi = h.length-1;
//		double mu = mean(h,lo,hi);
//		long cnt = 0;
//		double sum = 0;
//		for (int i=lo; i<=hi; i++) {
//			cnt = cnt + h[i];
//			sum = sum + (i - mu) * (i - mu) * h[i];
//		}
//		if (cnt > 0)
//			return ((double)sum) / cnt;
//		else 
//			return 0;
//	}
	
	// fast version
	protected double sigma2(int[] h, int lo, int hi) {
		if (lo < 0) lo = 0;
		if (hi >= h.length) hi = h.length-1;
		long A = 0;
		long B = 0;
		long N = 0;
		for (int i=lo; i<=hi; i++) {
			int ni = h[i];
			A = A + i * ni;
			B = B + i * i * ni;
			N = N + ni;
		}
		if (N > 0)
			return ((double)B - (double)A*A / N) / N;
		else 
			return 0;
	}
	
	
	// ---  utility methods -----------------------------
	
	// compute the median of a histogram 
	public int median(int[] h) {
		int K = h.length;
		int N = sum(h);
		int m = N / 2;
		int i = 0;
		int sum = h[0];
		while (sum <= m && i < K) {
			i++;
			sum += h[i];
		}
		return i;
	}
	
	public double[] normalize(int[] h) {
		int K = h.length;
		int N = count(h);
		double[] nh = new double[K];
		for (int i=0; i<K; i++) {
			nh[i] = ((double) h[i]) / N;
		}
		return nh;
	}
	
	public double[] normalize(double[] h) {
		double[] nh = new double[h.length];
		double hmax = max(h);
		for (int i=0; i<h.length; i++) {
			nh[i] =  (double) h[i] / hmax;
		}
		return nh;
	}
	
	public double max(double[] h) {
		double hmax = Double.NEGATIVE_INFINITY;
		for (int i=0; i<h.length; i++) {
			double n = h[i];
			if (n>hmax) hmax = n;
		}
		return hmax;
	
	}
	
}
