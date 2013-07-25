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

public class BinMorpherBox extends BinMorpher {
	
	BinMorpherBox(){
		makeBox();
	}
	
	private void makeBox(){
		se = new int[3][3];
		for (int v = 0; v < 3; v++) {
			for (int u = 0; u < 3; u++) {
				se[v][u] = 1;
			}
		}
	}
	
}
