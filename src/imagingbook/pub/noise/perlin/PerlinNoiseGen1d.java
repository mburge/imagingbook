/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.noise.perlin;

import imagingbook.pub.noise.hashing.HashFun;


/**
 * Gradient (Perlin) noise implementation. 
 * This class implements a 1D Perlin noise generator.
 */

public class PerlinNoiseGen1d extends PerlinNoiseGen {
	
	public PerlinNoiseGen1d(double f_min, double f_max, double persistence, HashFun hf) {
		super(f_min, f_max, persistence, hf);
	}
	
	/**
	 * 1D combined (multi-frequency) Perlin noise function. 
	 * @param x Interpolation position.
	 * @return The value of the combined Perlin
	 * noise function for the one-dimensional position x.
	 */
	public double NOISE(double x) {
		double sum = 0;
		for (int i=0; i<F.length; i++) {
			sum = sum + A[i] * noise(F[i] * x);
		}
		return sum;
	}
	
	/**
	 * 1D elementary (single-frequency) Perlin noise function. 
	 * @param x Interpolation position.
	 * @return The value of the elementary Perlin
	 * noise function for the one-dimensional position x.
	 */
	public double noise(double x) {
		int p0 = ffloor(x);
		double g0 = gradient(p0);
		double g1 = gradient(p0 + 1);
		double x01 = x - p0;
		double w0 = g0 * x01;
		double w1 = g1 * (x01 - 1);
		return interpolate(x01, w0, w1);
	}
	
	/**
	 * @param p discrete position.
	 * @return A pseudo-random gradient value for the discrete 
	 * position p.
	 */
	double gradient(int p) {
		return 2.0 * hashFun.hash(p) - 1;
	}
	
	/**
	 * Local interpolation function.
	 * @param x01 The interpolation position in [0,1]
	 * @param w0 Tangent value for x=0.
	 * @param w1 Tangent value for x=1.
	 * @return The interpolated noise value at position x01.
	 */
	double interpolate(double x01, double w0, double w1) { // local interpolation function (x01 is in [0,1])
		double s = this.s(x01); 
		return (1 - s) * w0 + s * w1;
	}
}
