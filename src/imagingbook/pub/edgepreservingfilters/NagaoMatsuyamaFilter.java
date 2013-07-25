/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.edgepreservingfilters;

import imagingbook.lib.filters.GenericFilter;
import imagingbook.lib.image.ImageAccessor;

/**
 * This class implements a 5x5 Nagao-Matsuyama filter, as described in
 * NagaoMatsuyama (1979).
 * 
 * @author W. Burger
 * @version 2013/05/30
 */

public class NagaoMatsuyamaFilter extends GenericFilter {
	
	public static class Parameters {
		public double varThreshold = 0.0;	// 0,...,10
	}
	
	private final Parameters params;
	
	private static final int[][] R1 =
	{{-1,-1}, {0,-1}, {1,-1},
	 {-1, 0}, {0, 0}, {1, 0},
	 {-1, 1}, {0, 1}, {1, 1}};
	
	private static final int[][] R2 =
	{{-2,-1}, {-1,-1},
	 {-2, 0}, {-1, 0}, {0, 0},
	 {-2, 1}, {-1, 1}};
	
	private static final int[][] R3 =
	{{-2,-2}, {-1,-2},
	 {-2,-1}, {-1,-1}, {0,-1},
	          {-1, 0}, {0, 0}};
	
	private static final int[][] R4 =
	{{-1,-2}, {0,-2}, {1,-2}, 
	 {-1,-1}, {0,-1}, {1,-1},
	          {0, 0}};

	private static final int[][] R5 =
	{        {1,-2}, {2,-2},
	 {0,-1}, {1,-1}, {2,-1},
	 {0, 0}, {1, 0}};
	
	private static final int[][] R6 =
	{        {1,-1}, {2,-1},
	 {0, 0}, {1, 0}, {2, 0},
	         {1, 1}, {2, 1}};
	
	private static final int[][] R7 =
	{{0,0}, {1,0},
	 {0,1}, {1,1}, {2,1},
	        {1,2}, {2,2}};

	private static final int[][] R8 =
	{        {0,0},
	 {-1,1}, {0,1}, {1,1},
	 {-1,2}, {0,2}, {1,2}};
	
	private static final int[][] R9 =
	{        {-1,0}, {0,0},
	 {-2,1}, {-1,1}, {0,1},
	 {-2,2}, {-1,2}};
	
	private static final int[][][] subRegions =
		{R2, R3, R4, R5, R6, R7, R8, R9};
	
	// ------------------------------------------------------
	
	public NagaoMatsuyamaFilter() {
		this(new Parameters());
	}
	
	public NagaoMatsuyamaFilter(Parameters params) {
		this.params = params;
	}
	
	private float minVariance;
	private float minMean;
	private float minMeanR;
	private float minMeanG;
	private float minMeanB;
	
	// ------------------------------------------------------

	public float filterPixel(ImageAccessor.Gray image, int u, int v) {
		minVariance = Float.MAX_VALUE;
		evalSubregion(image, R1, u, v);
		minVariance = minVariance - (float) params.varThreshold;
		for (int[][] Rk : subRegions) {
			evalSubregion(image, Rk, u, v);
		}
 		return minMean;
 	}
	
	void evalSubregion(ImageAccessor.Gray ia, int[][] R, int u, int v) {
		float sum1 = 0; 
		float sum2 = 0;
		int n = 0;
		for (int[] p : R) {
			float a = ia.getp(u+p[0], v+p[1]);
			sum1 = sum1 + a;
			sum2 = sum2 + a * a;
			n = n + 1;
		}
		float nr = n;
		float var = (sum2 - sum1 * sum1 / nr) / nr;	// = sigma^2
		if (var < minVariance) {
			minVariance = var;
			minMean = sum1 / nr; // mean
		}
	}
	
	// ------------------------------------------------------
	
	final float[] rgb = {0,0,0};
	
	public float[] filterPixel(ImageAccessor.Color ia, int u, int v) {
		minVariance = Float.MAX_VALUE;
		evalSubregionColor(ia, R1, u, v);
		minVariance = minVariance - (3 * (float) params.varThreshold);
		for (int[][] Rk : subRegions) {
			evalSubregionColor(ia, Rk, u, v);
		}
		rgb[0] = (int) Math.rint(minMeanR);
		rgb[1] = (int) Math.rint(minMeanG);
		rgb[2] = (int) Math.rint(minMeanB);
 		return rgb;
 	}
	
	void evalSubregionColor(ImageAccessor.Color ia, int[][] R, int u, int v) {
		final int[] cpix = {0,0,0};
		int sum1R = 0; int sum2R = 0;
		int sum1G = 0; int sum2G = 0;
		int sum1B = 0; int sum2B = 0;
		int n = 0;
		for (int[] p : R) {
			ia.getp(u + p[0], v + p[1], cpix);
			int red = cpix[0];
			int grn = cpix[1];
			int blu = cpix[2];
			sum1R = sum1R + red;
			sum1G = sum1G + grn;
			sum1B = sum1B + blu;
			sum2R = sum2R + red * red;
			sum2G = sum2G + grn * grn;
			sum2B = sum2B + blu * blu;
			n = n + 1;
		}
		float nr = n;
		// calculate variance for this subregion:
		float varR = (sum2R - sum1R * sum1R / nr) / nr;
		float varG = (sum2G - sum1G * sum1G / nr) / nr;
		float varB = (sum2B - sum1B * sum1B / nr) / nr;
		// total variance:
		float totalVar = varR + varG + varB;	
		if (totalVar < minVariance) {
			minVariance = totalVar;
			minMeanR = sum1R / nr;
			minMeanG = sum1G / nr;
			minMeanB = sum1B / nr;
		}
	}

}
