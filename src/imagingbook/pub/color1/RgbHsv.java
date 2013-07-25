/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.color1;

/** Methods for converting between RGB and HSV color spaces.
*/

public class RgbHsv {

	public static float[] RGBtoHSV (int R, int G, int B, float[] HSV) {
		// R,G,B in [0,255]
		float H = 0, S = 0, V = 0;
		float cMax = 255.0f;
		int cHi = Math.max(R, Math.max(G, B)); 	// highest color value
		int cLo = Math.min(R, Math.min(G, B)); 	// lowest color value
		int cRng = cHi - cLo;				    // color range
		
		// compute value V
		V = cHi / cMax;
		
		// compute saturation S
		if (cHi > 0)
			S = (float) cRng / cHi;

		// compute hue H
		if (cRng > 0) {	// hue is defined only for color pixels
			float r = (float)(cHi - R) / cRng;
			float g = (float)(cHi - G) / cRng;
			float b = (float)(cHi - B) / cRng;
			float h;
			if (R == cHi)                      // r is highest color value
				h = b - g;
			else if (G == cHi)                 // g is highest color value
				h = r - b + 2;
			else                               // b is highest color value
				h = g - r + 4;
			if (h < 0)
				h= h + 6;
			H = h / 6;
		}
		
		if (HSV == null)	// create a new HSV array if needed
			HSV = new float[3];
		HSV[0] = H; HSV[1] = S; HSV[2] = V;
		return HSV;
	}
	
	public static int HSVtoRGB (float h, float s, float v) {
		// h,s,v in [0,1]
		float r = 0, g = 0, b = 0;
		float hh = (6 * h) % 6;                 
		int   c1 = (int) hh;                     
		float c2 = hh - c1;
		float x = (1 - s) * v;
		float y = (1 - (s * c2)) * v;
		float z = (1 - (s * (1 - c2))) * v;	
		switch (c1) {
			case 0: r = v; g = z; b = x; break;
			case 1: r = y; g = v; b = x; break;
			case 2: r = x; g = v; b = z; break;
			case 3: r = x; g = y; b = v; break;
			case 4: r = z; g = x; b = v; break;
			case 5: r = v; g = x; b = y; break;
		}
		
		int R = Math.min((int)(r * 255), 255);
		int G = Math.min((int)(g * 255), 255);
		int B = Math.min((int)(b * 255), 255);
		
		// create int-packed RGB-color:
		int rgb = ((R & 0xff) << 16) | ((G & 0xff) << 8) | B & 0xff;
		return rgb;
	}

	// new:
	public static int[] HSVtoRGB (float[] HSV, int[] RGB) {
		float h = HSV[0]; 
		float s = HSV[1]; 
		float v = HSV[2];
		// h,s,v in [0,1]
		float r = 0, g = 0, b = 0;
		float hh = (6 * h) % 6;                 
		int   c1 = (int) hh;                     
		float c2 = hh - c1;
		float x = (1 - s) * v;
		float y = (1 - (s * c2)) * v;
		float z = (1 - (s * (1 - c2))) * v;	
		switch (c1) {
			case 0: r = v; g = z; b = x; break;
			case 1: r = y; g = v; b = x; break;
			case 2: r = x; g = v; b = z; break;
			case 3: r = x; g = y; b = v; break;
			case 4: r = z; g = x; b = v; break;
			case 5: r = v; g = x; b = y; break;
		}
		
		int R = Math.min((int)(r * 255), 255);
		int G = Math.min((int)(g * 255), 255);
		int B = Math.min((int)(b * 255), 255);
		if (RGB == null)	// create a new RGB array if needed
			RGB = new int[3];
		RGB[0] = R; RGB[1] = G; RGB[2] = B;
		return RGB;
	}



}
