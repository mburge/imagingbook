/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.noise.hashing;

/**
 * Gradient (Perlin) noise implementation. 
 */
public abstract class Hash32 extends HashFun {
	
	static final int maxInt = 0x7fffffff;
	
	static final int[] smallPrimes = {	// used for N-dimensional hashing
		73, 79, 83, 89, 97, 101, 103, 107, 109, 113,
		127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 
	    179, 181, 191, 193, 197, 199, 211, 223, 227, 229,
	    233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 
	    283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 
	    353, 359, 367, 373, 379, 383, 389, 397, 401, 409 
	};

	protected Hash32() {
		super();
	}
	
	protected Hash32(int seed) {
		super(seed);
	}
	
	/**
	 * "Hashes" an <tt>int</tt> key to a "pseudo-random" <tt>int</tt> value 
	 * in [-2147483648, 2147483647].
	 * This method is supposed to be overridden by subclasses if needed.
	 * @param key
	 * @return A integer value in [-2147483648, 2147483647].
	 */
	abstract int hashInt(int key);
	
	public double hash(int u) {
		int h = hashInt(73*u + seed) & maxInt;
		return (double) h / maxInt;
	}
	
//	public double[] hash(int u, int v) {
//		int hx = hashInt(59*u + 67*v + seed) & maxInt;	
//		int hy = hashInt(73*u + 79*v + seed) & maxInt;	
//		return new double[] {(double) hx / maxInt, (double) hy / maxInt};
//	}
	
	// call 1 hash function and extract 12-bit blocks
	public double[] hash(int u, int v) {
		final int M = 0x00000FFF;
		int h = hashInt(59*u + 67*v + seed);
		int hx =  h & M;		// extract bits  0..11
		int hy = (h >> 12) & M;	// extract bits 12..23
		return new double[] {(double) hx / M, (double) hy / M};
	}
	
	// call 3 different hash functions for 3 dimensions
//	public double[] hash(int u, int v, int w) {
//		int M = 0x7FFFFFFF;
//		int hx = hashInt(59*u + 67*v + 71*w + seed) & M;
//		int hy = hashInt(73*u + 79*v + 83*w + seed) & M;
//		int hz = hashInt(89*u + 97*v + 101*w + seed) & M;
//		return new double[] {(double) hx/M, (double) hy/M, (double) hz/M};
//	}
	
	
	// call 1 hash function and extract bit blocks
	public double[] hash(int u, int v, int w) {
		final int M = 0x000000FF;
		int h = hashInt(59*u + 67*v + 71*w + seed);
		int hx =  h & M;			// extract bits 0..7
		int hy = (h >> 8) & M;	// extract bits 8..15
		int hz = (h >> 16) & M;	// extract bits 16..23
		return new double[] {(double) hx / M, (double) hy / M, (double) hz / M};
	}
	
	/*
	 * N-dimensional permutation hash; this version does not use
	 * any bit splitting. Instead, the hashInt() function is
	 * applied repeatedly for every gradient dimension by 
	 * using the dimension number (k) as a local seed - 
	 * in addition to the global seed (seed).
	 */
	public double[] hash(int[] p) {	
		final int N = p.length;
		double[] g = new double[N];
		for (int k = 0; k < N; k++) { // dimension k
			int sum = seed;
			for (int l = 0; l < N; l++) { // dimension k
				sum = sum + smallPrimes[l + k] * p[l];
			}
			int h = hashInt(sum + k) & maxInt;
			g[k] = (double) h / maxInt;
		}
		return g;
	}

}
