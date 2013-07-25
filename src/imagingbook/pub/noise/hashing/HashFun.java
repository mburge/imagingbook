/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.noise.hashing;

import java.util.Random;

/**
 * This class is the abstract superclass of various hash functions. 
 * It cannot be instantiated directly, but its subclasses can. Two static 
 * methods <tt>create()</tt> and <tt>create(seed)</tt> are provided for convenience.
 * Typical use: <br><pre>
 *   HashFun hf = new HashFun.create(); // or, alternatively,
 *   HashFun hf = new Hash32Ward(seed);
 *   double g = hf.hash(u); // g is in [-1,+1]
 *   double g = hf.hash(u,v);
 *   double[] g = hf.hash(u,v,w); </pre>
 * Omit seed in the constructor call to get a random seed 
 * hash function of the specified type.
 */

public abstract class HashFun {
	
	static final Random rand = new Random();
	int seed;
	
	protected HashFun() {
		this.seed = makeRandomSeed();
	}
	
	protected HashFun(int seed){
		this.seed = seed;
	}
	
	/**
	 * @return A new HashFun object.
	 * Has32Shift is used as the default type.
	 */
	public static HashFun create() {
		return new Hash32Shift();
	}
	
	/**
	 * @param seed
	 * @return A new HashFun object initialized with seed. 
	 * Has32Shift is used as the default type.
	 */
	public static HashFun create(int seed) {
		return new Hash32Shift(seed);
	}
	
	protected int makeRandomSeed() {
		return 0x000fffff & rand.nextInt();
	}
	
	// these hash functions return either a single value or a vector 
	// with elements in [0,1]
	public abstract double hash(int u);					// 1D hash function
	public abstract double[] hash(int u, int v);		// 2D hash function
	public abstract double[] hash(int u, int v, int w);	// 3D hash function
	public abstract double[] hash(int[] p);				// ND hash function

}


