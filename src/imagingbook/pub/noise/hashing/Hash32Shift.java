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
 * Hash function described in Thomas Wang, "Integer Hash Function"
 * http://www.concentric.net/~Ttwang/tech/inthash.htm (Jan. 2007)
 */

public class Hash32Shift extends Hash32 {

	public Hash32Shift() {
		super();
	}
	
	public Hash32Shift(int seed) {
		super(seed);
	}
	
	@Override
	int hashInt(int key) {
		return hashIntShift(key);
	}
	
	int hashIntShift(int key) {
		key = ~key + (key << 15); // key = (key << 15) - key - 1;
		key = key ^ (key >>> 12);
		key = key + (key << 2);
		key = key ^ (key >>> 4);
		key = key * 2057; // key = (key + (key << 3)) + (key << 11);
		key = key ^ (key >>> 16);
		return key;
	}

}
