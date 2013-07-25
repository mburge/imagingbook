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

/**
 * Basic vector median filter for color images implemented
 * by extending the GenericFilter class.
 * @author W. Burger
 * @version 2013/05/30
 */
public class VectorMedianFilter extends GenericFilter {
	
	public static class Parameters {
		public double radius = 3.0;
		public NormType distanceNorm = NormType.L1;
		public boolean markModifiedPixels = false;
		public boolean showMask = false;
	}
	
	final Parameters params;
	
	public static Color modifiedColor = Color.black;
	final int[] modColor = {modifiedColor.getRed(), modifiedColor.getGreen(), modifiedColor.getBlue()};
	public int modifiedCount = 0;
	
	final FilterMask mask;
	final int[][] supportRegion;		// supportRegion[i][c] with index i, color component c
	final VectorNorm vNorm;
	
	// uses default parameters:
	public VectorMedianFilter() {	
		this(new Parameters());
	}
	
	// accepts parameter object:
	public VectorMedianFilter(Parameters params) {
		this.params = params;
		mask = new FilterMask(params.radius);
		supportRegion = new int[mask.getCount()][3];
		vNorm = params.distanceNorm.create();
		initialize();
	}
	
	void initialize() {
		if (params.showMask) mask.show("Mask");
	}
	
	public float filterPixel(Gray source, int u, int v) {
		throw new IllegalArgumentException("no filter for gray images");
	}
	
	// vector median filter for RGB color image
	public float[] filterPixel(ImageAccessor.Color ia, int u, int v) {
		final int[] pCtr = new int[3];		// center pixel
		ia.getp(u, v, pCtr);
		getSupportRegion(ia, u, v);
		double dCtr = aggregateDistance(pCtr, supportRegion); 
		double dMin = Double.MAX_VALUE;
		int jMin = -1;
		for (int j = 0; j < supportRegion.length; j++) {
			int[] p = supportRegion[j];
			double d = aggregateDistance(p, supportRegion);
			if (d < dMin) {
				jMin = j;
				dMin = d;
			}
		}
		int[] pmin = supportRegion[jMin];
		// modify this pixel only if the min aggregate distance of some
		// other pixel in the filter region is smaller
		// than the aggregate distance of the original center pixel:
		final float[] pF = new float[3];	// the returned color tupel
		if (dMin < dCtr) {	// modify this pixel
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

}
