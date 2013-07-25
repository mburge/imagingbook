/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.colorimage;

/*
 * This is only a utility class to hold static methods used by CIELAB and CIELUV color spaces.
 * Should be modified to implement a a subclass of ColorSpace!
 */

public abstract class sRgbUtil {
	
	// double versions of Gamma correction
	
    public static double gammaFwd(double lc) {	// input: linear component value
		return (lc > 0.0031308) ?
			(1.055 * Math.pow(lc, 1/2.4) - 0.055) :
			(lc * 12.92);
    }
    
    public static double gammaInv(double nc) {	// input: nonlinear component value
    	return (nc > 0.03928) ?
			Math.pow((nc + 0.055)/1.055, 2.4) :
			(nc / 12.92);
    }
    
    public static float[] sRgbToRgb(float[] srgb) {	// all components in [0,1]
		float R = (float) sRgbUtil.gammaInv(srgb[0]);
		float G = (float) sRgbUtil.gammaInv(srgb[1]);
		float B = (float) sRgbUtil.gammaInv(srgb[2]);
    	return new float[] {R,G,B};
    }

    public static float[] rgbToSrgb(float[] rgb) {	// all components in [0,1]
		float sR = (float) sRgbUtil.gammaFwd(rgb[0]);
		float sG = (float) sRgbUtil.gammaFwd(rgb[1]);
		float sB = (float) sRgbUtil.gammaFwd(rgb[2]);
		return new float[] {sR,sG,sB};
    }
    
}
