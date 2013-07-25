/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.morphology;

import imagingbook.pub.morphology.BinMorpher;

public class BinMorpherDisk extends BinMorpher {
	
	BinMorpherDisk(){
		makeDisk(1);
	}
	
	public BinMorpherDisk(double radius) {
		makeDisk(radius);
		
	}
	
	private void makeDisk(double radius){
		int r = (int) Math.rint(radius);
		if (r <= 1) r = 1;
		int size = r + r + 1;
		se = new int[size][size];
		double r2 = radius * radius;

		for (int v = -r; v <= r; v++) {
			for (int u = -r; u <= r; u++) {
				if (u * u + v * v <= r2)
					se[v + r][u + r] = 1;
			}
		}
	}
	
}
