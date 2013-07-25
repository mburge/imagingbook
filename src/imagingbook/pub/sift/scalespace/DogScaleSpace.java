/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.sift.scalespace;


public class DogScaleSpace extends HierarchicalScaleSpace {
	
	public DogScaleSpace(GaussianScaleSpace G) {
		super(G.P, G.Q, G.sigma_s, G.sigma_0, G.botLevel, G.topLevel-1);  //botLevel = -1, topLevel = K+1
		build(G);
	}	
	
	private final void build(GaussianScaleSpace G) {
		// build DoG octaves:
		for (int p = 0; p < P; p++) {
			ScaleOctave Gp = G.getOctave(p);
			octaves[p] = new DogOctave(Gp);
		}
	}

}
