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
 * Hash function described in G. Ward, "A recursive implementation 
 * of the Perlin noise function", Graphics Gems II, 1991
 */
public class Hash32Ward extends Hash32 {
	
	public Hash32Ward() {
		super();
	}
	
	public Hash32Ward(int seed) {
		super(seed);
	}
	
	int hashInt(int key) {
		return hashIntWard(key);
	}
	
	//  lower 16 bits are highly repetitive and perfectly uniform!!!!
	int hashIntWard(int key) {
		key = (key << 13) ^ key; // ^ denotes bitwise XOR operation
		key = (key * (key * key * 15731 + 789221) + 1376312589);
		return key;
	}
}
