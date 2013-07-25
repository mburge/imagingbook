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

import java.util.Arrays;

/**
 * Ordinary (scalar) median filter for color images implemented
 * by extending the GenericFilter class.
 * Color images are filtered individually in all channels.
 * @author W. Burger
 * @version 2013/05/30
 */
public class ScalarMedianFilter extends GenericFilter {
	
	public static class Parameters {
		public double radius = 3.0;
	}
	
	final Parameters params;
	FilterMask mask;

	
	public ScalarMedianFilter() {
		params = new Parameters();
		initialize();
	}
	
	public ScalarMedianFilter(Parameters params) {
		this.params = params;
		initialize();
	}

	public ScalarMedianFilter(double radius) {
		params = new Parameters();
		params.radius = radius;
		initialize();
	}
	
	void initialize() {
		mask = new FilterMask(params.radius);
	}

	public float filterPixel(ImageAccessor.Gray source, int u, int v) {
		final int maskCount = mask.getCount();
		final float[] p = new float[maskCount];
		final int medianIndex = maskCount/2;
		final int maskCenter = mask.getCenter();
		final int[][] maskArray = mask.getMask();
		int k = 0;
		for (int i = 0; i < maskArray.length; i++) {
			int ui = u + i - maskCenter;
			for (int j = 0; j < maskArray[0].length; j++) {
				if (maskArray[i][j] > 0) {
					int vj = v + j - maskCenter;
					p[k] = source.getp(ui, vj);
					k = k + 1;
				}
			}
		}
		Arrays.sort(p);
		return p[medianIndex];
	}

	public float[] filterPixel(ImageAccessor.Color source, int u, int v) {
		final int maskCount = mask.getCount();
		final int[] pR = new int[maskCount];
		final int[] pG = new int[maskCount];
		final int[] pB = new int[maskCount];
		final int[] pctr = new int[3];
		final float[] pF = new float[3];
		final int medianIndex = maskCount/2;
		final int maskCenter = mask.getCenter();
		final int[][] maskArray = mask.getMask();
		int k = 0;
		for (int i=0; i<maskArray.length; i++) {
			int ui = u + i - maskCenter;
			for (int j=0; j<maskArray[0].length; j++) {
				if (maskArray[i][j] > 0) {
					int vj = v + j - maskCenter;
					source.getp(ui,vj,pctr);
					pR[k] = pctr[0];
					pG[k] = pctr[1];
					pB[k] = pctr[2];
					k = k + 1;
				}
			}
		}
		Arrays.sort(pR); pF[0] = pR[medianIndex];
		Arrays.sort(pG); pF[1] = pG[medianIndex];
		Arrays.sort(pB); pF[2] = pB[medianIndex];
		return pF;
 	}
}
