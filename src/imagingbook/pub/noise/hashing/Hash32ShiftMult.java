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
public class Hash32ShiftMult extends Hash32 {
	
	public Hash32ShiftMult() {
		super();
	}
	
	public Hash32ShiftMult(int seed) {
		super(seed);
	}
	
	int hashInt(int key) {
		return hashIntShiftMult(key) ;
	}
	
	int hashIntShiftMult(int key) {
		int c2 = 668265261; //=  0x27d4eb2d, which is not a prime, closest prime is 668265263
		key = (key ^ 61) ^ (key >>> 16);
		key = key + (key << 3);
		key = key ^ (key >>> 4);
		key = key * c2;
		key = key ^ (key >>> 15);
		return key;
	}
}
