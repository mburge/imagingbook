/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.image;

import ij.process.FloatProcessor;

public class Statistics {
	
	
	public static double getMean(FloatProcessor ip) {
		final int W = ip.getWidth();
		final int H = ip.getHeight();
		final int N = W * H;

		double sum = 0;
		for (int j = 0; j < H; j++) {
			for (int i = 0; i < W; i++) {
				sum = sum + ip.getf(i, j);
			}
		}
		return sum / N;
	}
	
	public static double getVariance(FloatProcessor ip) {
		final int W = ip.getWidth();
		final int H = ip.getHeight();
		final int N = W * H;
		
		final double mean = getMean(ip);

		double sum = 0;
		for (int j = 0; j < H; j++) {
			for (int i = 0; i < W; i++) {
				double d = ip.getf(i, j) - mean;
				sum = sum + d * d;
			}
		}
		return sum / N;
	}
	
	
	public static double getVariance2(FloatProcessor ip) {
		final int W = ip.getWidth();
		final int H = ip.getHeight();
		final int N = W * H;

		double sumX = 0;
		double sumX2 = 0;
		
		for (int j = 0; j < H; j++) {
			for (int i = 0; i < W; i++) {
				double x = ip.getf(i, j);
				sumX = sumX + x;
				sumX2 = sumX2 + x * x;
			}
		}
		
		double var = (sumX2 - sumX * sumX / N) / N ;
		return var;
	}

}
