/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.math;

public class Arithmetic {
	
	public static final float EPSILON_FLOAT 	= 1e-35f;	// smallest possible float denominator is ~ 1e-38f
	public static final double EPSILON_DOUBLE 	= 1e-300;	// smallest possible float denominator is ~ 1e-308f
		
	public static int sqr(int x) {
		return x*x;
	}
	
	public static float sqr(float x) {
		return x*x;
	}
	
	public static double sqr(double x) {
		return x*x;
	}
	
	/*
	 * Book version (correct)
	 */
	public static int mod(int a, int b) {
		if (b == 0)
			return a;
		if (a * b >= 0)	// a,b are either both positive or negative
			return a - b * (a / b);	
		else
			return a - b * (a / b - 1);
	}
	
	// http://en.wikipedia.org/wiki/Modulo_operation: 
	// identical to results in Mathematica
	public static double mod(double a, double n) {
		return a - n * Math.floor(a / n);
	}

}
