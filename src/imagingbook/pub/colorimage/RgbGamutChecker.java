/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.colorimage;

public class RgbGamutChecker {
	
	static final double lo = -0.01;
	static final double hi =  1.01;
	
	public static boolean markOutOfGamutColors = false;
	
	public static double oGred = 1;	// out of gamut replacement values
	public static double oGgrn = 1;
	public static double oGblu = 1;
	
	static int ctr = 0;
	
	public static void reset() {
		ctr = 0;
	}
	
	public static void increment() {
		ctr = ctr + 1;
	}
	
	public static int getCount() {
		return ctr;
	}
	
	public static boolean checkOutOfGamut(double r, double g, double b) {
		if (r < lo || r > hi || g < lo || g > hi || b < lo || b > hi) {
			ctr = ctr + 1;	
			return true;
		}
		else {
			return false;
		}
	}
}
