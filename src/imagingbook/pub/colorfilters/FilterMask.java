/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.colorfilters;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class FilterMask {
	
	private final int center;			// mask center position
	private final int count;			// number of nonzero mask elements
	private final int[][] mask;			// mask[x][y]  specifies the support region
		
	public FilterMask(double radius) {
		center = Math.max((int) Math.ceil(radius), 1);
		//IJ.log("mask radius = " + center);
		int mWidth = 2 * center + 1;		// width/height of mask array
		mask = new int[mWidth][mWidth];			// initialized to zero
		int cnt = 0;
		double r2 = radius * radius + 1; 		// add 1 to get mask shape similar to ImageJ
		for (int u = 0; u < mWidth; u++) {
			int x = u - center;
			for (int v = 0; v < mWidth; v++) {
				int y = v - center;
				if (x*x + y*y <= r2) {
					mask[u][v] = 1;
					cnt = cnt + 1;
				}
			}
		}
		count = cnt;
	}
	
	public int getCenter() {
		return center;
	}
	
	public int getCount() {
		return count;
	}
	
	public int[][] getMask() {
		return mask;
	}
	
	public ImagePlus getImagePlus(String title) {
		FloatProcessor fp = new FloatProcessor(mask);
		ImageProcessor bp = fp.convertToByte(false);
		bp.threshold(0);
		//bp.multiply(255);
		return new ImagePlus(title, bp);
	}
	
	public void show(String title) {
		getImagePlus(title).show();
	}

}
