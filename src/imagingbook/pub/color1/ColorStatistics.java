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

public class ColorStatistics {
	
	//determine how many different colors are contained in the 24 bit full-color image cp
	public static int countColors (ColorProcessor cp) { 
		// duplicate pixel array and sort
		int[] pixels = ((int[]) cp.getPixels()).clone();
		Arrays.sort(pixels);  
		
		int k = 1;	// image contains at least one color
		for (int i = 0; i < pixels.length-1; i++) {
			if (pixels[i] != pixels[i+1])
				k = k + 1;
		}
		return k;
	}
	
	//computes the combined color histogram for color components (c1,c2)
	static int[][] get2dHistogram (ColorProcessor cp, int c1, int c2) 
	{	// c1, c2:  R = 0, G = 1, B = 2
		int[] RGB = new int[3];
		int[][] H = new int[256][256];	// histogram array H[c1][c2] 

		for (int v = 0; v < cp.getHeight(); v++) {
			for (int u = 0; u < cp.getWidth(); u++) {
				cp.getPixel(u, v, RGB); 
				int i = RGB[c1];	
				int j = RGB[c2];	
				// increment corresponding histogram cell
				H[j][i]++; // i runs horizontal, j runs vertical
			}
		}	
		return H;
	}
	

}
