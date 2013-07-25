/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.colorfilters;

import imagingbook.lib.filters.GenericFilter;
import imagingbook.lib.image.ImageAccessor;
import imagingbook.lib.image.ImageAccessor.Gray;
import imagingbook.lib.math.VectorNorm;
import imagingbook.lib.math.VectorNorm.NormType;

import java.awt.Color;
import java.util.Arrays;

/**
 * Sharpening vector median filter for color images implemented
 * by extending the GenericFilter class.
 * @author W. Burger
 * @version 2013/05/30
 */
public class VectorMedianFilterSharpen extends GenericFilter {
	
	public static class Parameters {
		public double radius = 3.0;
		public double sharpen = 0.5;  		// sharpening parameter (0=none, 1 = max.)
		public double threshold = 0.0;		// threshold for replacing the current center pixel
		public NormType distanceNorm = NormType.L1;
		// for testing only:
		public boolean showMask = false;
		public boolean markModifiedPixels = false;
		public Color modifiedColor = Color.black;
	}
	
	final FilterMask mask;
	final int[][] supportRegion;		// supportRegion[i][c] with index i, color component c
	final VectorNorm vNorm;
	final int a;						// a = 2,...,n
	final Parameters params;
	
	int[] modColor;
	public int modifiedCount = 0;
	
	// uses default parameters:
	public VectorMedianFilterSharpen() {	
		this(new Parameters());
	}
	
	// accepts parameter object:
	public VectorMedianFilterSharpen(Parameters params) {
		this.params = params;
		mask = new FilterMask(params.radius);
		int maskCount = mask.getCount();
		supportRegion = new int[maskCount][3];
		a = (int) Math.round(maskCount - params.sharpen * (maskCount - 2));
		vNorm = params.distanceNorm.create();
		initialize();
	}
	
	void initialize() {
		modColor = new int[] {params.modifiedColor.getRed(), params.modifiedColor.getGreen(), params.modifiedColor.getBlue()};
		if (params.showMask) 
			mask.show("Mask");
	}
	
	public float filterPixel(Gray source, int u, int v) {
		throw new IllegalArgumentException("no filter for gray images");
	}
	
	// vector median filter for RGB color image
	public float[] filterPixel(ImageAccessor.Color ia, int u, int v) {
		final int[] pCtr = new int[3];		// center pixel
		ia.getp(u, v, pCtr);
		getSupportRegion(ia, u, v);
		double dCtr = trimmedAggregateDistance(pCtr, supportRegion, a); 
		double dMin = Double.MAX_VALUE;
		int jMin = -1;
		for (int j = 0; j < supportRegion.length; j++) {
			int[] p = supportRegion[j];
			double d = trimmedAggregateDistance(p, supportRegion, a);
			if (d < dMin) {
				jMin = j;
				dMin = d;
			}
		}
		int[] pmin = supportRegion[jMin];
		// modify this pixel only if the min aggregate distance of some
		// other pixel in the filter region is smaller
		// than the aggregate distance of the original center pixel:
		final float[] pF = new float[3];			// the returned color tupel
		if (dCtr - dMin > params.threshold * a) {	// modify this pixel
			if (params.markModifiedPixels) {
				copyRgb(modColor, pF);
				modifiedCount++;
			}
			else {
				copyRgb(pmin, pF);
			}
		}
		else {	// keep the original pixel value
			copyRgb(pCtr, pF);
		}
		return pF;
 	}
	
	int[][] getSupportRegion(ImageAccessor.Color ia, int u, int v) {
		final int[] p = new int[3];
		// fill 'supportRegion' for current mask position
		int n = 0;
		final int[][] maskArray = mask.getMask();
		final int maskCenter = mask.getCenter();
		for (int i = 0; i < maskArray.length; i++) {
			int ui = u + i - maskCenter;
			for (int j = 0; j < maskArray[0].length; j++) {
				if (maskArray[i][j] > 0) {
					int vj = v + j - maskCenter;
					ia.getp(ui, vj, p);
					copyRgb(p, supportRegion[n]);
					n = n + 1;
				}
			}
		}
		return supportRegion;
	}
	
	void copyRgb(int[] source, int[] target) {
		target[0] = source[0];
		target[1] = source[1];
		target[2] = source[2];
	}
	
	void copyRgb(int[] source, float[] target) {
		target[0] = source[0];
		target[1] = source[1];
		target[2] = source[2];
	}
	
	// find the color with the smallest summed distance to all others.
	// brute force and thus slow
	double aggregateDistance(int[] p, int[][] P) {
		double d = 0;
		for (int i = 0; i < P.length; i++) {
			d = d + vNorm.distance(p, P[i]);
		}
		return d;
	}
	
	double trimmedAggregateDistance(int[] p, int[][] P, int a) {
		if (a <= 1) {
			return 0;	// aggregate distance of rank 1 is always zero
		}
		int N = P.length;
		final double[] R = new double[N];
		for (int i = 0; i < N; i++) {
			R[i] = vNorm.distance(p, P[i]);
		}
		if (a < N) {
			Arrays.sort(R);	// only sort if the rank is less than N
		}
		double d = 0;
		for (int i = 1; i < a; i++) {
			d = d + R[i];
		}
		return d;
	}
	
	final int rgbToInt (int r, int g, int b) {
		return ((r & 0xFF)<<16) | ((g & 0xFF)<<8) | b & 0xFF;
	}

}
