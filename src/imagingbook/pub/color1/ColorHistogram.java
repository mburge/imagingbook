/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.color1;

import ij.process.ColorProcessor;
import java.util.Arrays;

public class ColorHistogram {
	int colorArray[] = null;
	int countArray[] = null;
	
	public ColorHistogram(ColorProcessor ip) {
		this((int[]) ip.getPixels());
	}
	
	public ColorHistogram(int[] pixelsOrig) {
		int N = pixelsOrig.length;
		int[] pixelsCpy = new int[N];
		for (int i = 0; i < N; i++) {
			// remove possible alpha components
			pixelsCpy[i] = 0xFFFFFF & pixelsOrig[i];
		}
		Arrays.sort(pixelsCpy);
		
		// count unique colors:
		int k = -1; // current color index
		int curColor = -1;
		for (int i = 0; i < pixelsCpy.length; i++) {
			if (pixelsCpy[i] != curColor) {
				k++;
				curColor = pixelsCpy[i];
			}
		}
		int nColors = k+1;
		
		// tabulate and count unique colors:
		colorArray = new int[nColors];
		countArray = new int[nColors];
		k = -1;	// current color index
		curColor = -1;
		for (int i = 0; i < pixelsCpy.length; i++) {
			if (pixelsCpy[i] != curColor) {	// new color
				k++;
				curColor = pixelsCpy[i];
				colorArray[k] = curColor;
				countArray[k] = 1;
			}
			else {
				countArray[k]++;
			}
		}
	}
	
	public int[] getColorArray() {
		return colorArray;
	}
	
	public int[] getCountArray() {
		return countArray;
	}
	
	public int getNumberOfColors() {
		if (colorArray == null)
			return 0;
		else
			return colorArray.length;
	}
	
	public int getColor(int index) {
		return this.colorArray[index];
	}
	
	public int getCount(int index) {
		return this.countArray[index];
	}
}
