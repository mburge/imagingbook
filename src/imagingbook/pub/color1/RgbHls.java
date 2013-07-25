/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.color1;

/** Methods for converting between RGB and HLS color spaces.
*/

public class RgbHls {

	@Deprecated
	public static float[] RGBtoHLS (float R, float G, float B) {
		// R,G,B assumed to be in [0,1]
		float cHi = Math.max(R, Math.max(G, B)); // highest color value
		float cLo = Math.min(R, Math.min(G, B)); // lowest color value
		float cRng = cHi - cLo;				    // color range
		
		// compute lightness L
		float L = (cHi + cLo)/2;
		
		// compute saturation S		
		float S = 0;
		if (0 < L && L < 1) {
			float d = (L <= 0.5f) ? L : (1 - L);
			S = 0.5f * cRng / d;
		}

		// compute hue H
		float H=0;
		if (cHi > 0 && cRng > 0) {        // a color pixel
			float r = (float)(cHi - R) / cRng;
			float g = (float)(cHi - G) / cRng;
			float b = (float)(cHi - B) / cRng;
			float h;
			if (R == cHi)                      // r is highest color value
				h = b - g;
			else if (G == cHi)                 // g is highest color value
				h = r - b + 2.0f;
			else                               // b is highest color value
				h = g - r + 4.0f;
			
			if (h < 0)
			  h= h + 6;
			H = h / 6;
		}

		return new float[] {H,L,S};
	}
	

	// added 2012-04-06 (check again if signatures make sense!)
	// new, replaces book version!
	public static float[] RGBtoHLS (int R, int G, int B) {
		// R, G, B assumed to be in [0,255]
		int cHi = Math.max(R, Math.max(G, B)); // highest color value
		int cLo = Math.min(R, Math.min(G, B)); // lowest color value
		int cRng = cHi - cLo;				    // color range
		
		// Calculate hue H (same as in HSV):
		float H = 0;
		
		if (cHi > 0 && cRng > 0) {        // a color pixel
			float r = (float)(cHi - R) / cRng;
			float g = (float)(cHi - G) / cRng;
			float b = (float)(cHi - B) / cRng;
			float h;
			if (R == cHi)                      // R is largest component
				h = b - g;
			else if (G == cHi)                 // G is largest component
				h = r - b + 2.0f;
			else                               // B is largest component
				h = g - r + 4.0f;
			if (h < 0)
			  h = h + 6;
			H = h / 6;
		}
		
		// Calculate lightness L
		float L = ((cHi + cLo) / 255f) / 2;
		
		// Calculate saturation S		
		float S = 0;
		if (0 < L && L < 1) {
			float d = (L <= 0.5f) ? L : (1 - L);
			S = 0.5f * (cRng / 255f) / d;
		}

		return new float[] { H, L, S };
	}
	
	// return value changed to int: 2012-04-06
	public static int HLStoRGB (float H, float L, float S) {
		// H,L,S assumed to be in [0,1]
		float r = 0, g = 0, b = 0; 
	
		if (L <= 0)				// black
			r = g = b = 0;
		else if (L >= 1)		// white
			r = g = b = 1;
		else {
			float hh = (6 * H) % 6;
			int   c1 = (int) hh;
			float c2 = hh - c1;
			float d = (L <= 0.5f) ? (S * L) : (S * (1 - L));
			float w = L + d, 	x = L - d;
			float y = w - (w - x) * c2;
			float z = x + (w - x) * c2;
			switch (c1) {
				case 0: r=w; g=z; b=x; break;
				case 1: r=y; g=w; b=x; break;
				case 2: r=x; g=w; b=z; break;
				case 3: r=x; g=y; b=w; break;
				case 4: r=z; g=x; b=w; break;
				case 5: r=w; g=x; b=y; break;
			}			
		}
		int R = Math.min(Math.round(r * 255), 255);
		int G = Math.min(Math.round(g * 255), 255);
		int B = Math.min(Math.round(b * 255), 255);
		// create int-packed RGB-color:
		int rgb = ((R&0xff)<<16) | ((G&0xff)<<8) | B&0xff; 
		return rgb;
	}
	
	
	// for testing only!
	
//	public static void main(String[] args) {
//		float[] hls;
//		hls = RGBtoHLS (255, 127, 127); // pink
//		//hls = RGBtoHLS (1.0f, 0.5f, 0.5f);	// pink
//		System.out.format("HSL = %.3f  %.3f  %.3f ", hls[0], hls[2], hls[1]);
//	}
	
}
