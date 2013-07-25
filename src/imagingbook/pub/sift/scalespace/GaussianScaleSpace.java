/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.sift.scalespace;

import ij.process.FloatProcessor;

public class GaussianScaleSpace extends HierarchicalScaleSpace {

	public GaussianScaleSpace(FloatProcessor fp, double sigma_s, double sigma_0, int P, int Q, int botLevel, int topLevel) {
		super(P, Q, sigma_s, sigma_0, botLevel, topLevel);	
		build(fp);
	}
	
	private final void build(FloatProcessor fp) {
		double scale_b = getAbsoluteScale(0, -1) ;	// absolute scale of level(0,-1)
		double sigma_b = getRelativeScale(sigma_s, scale_b);
		
		ScaleLevel Ginit = new ScaleLevel(fp, sigma_s);
		Ginit.filterGaussian(sigma_b);
		Ginit.setAbsoluteScale(scale_b);

		// build Gaussian octaves:
		octaves[0] = new GaussianOctave(0, Q, Ginit, botLevel, topLevel, sigma_0);
		for (int p = 1; p < P; p++) {
			ScaleLevel Gbase = octaves[p-1].getLevel(Q-1).decimate();
			octaves[p] = new GaussianOctave(p, Q, Gbase, botLevel, topLevel, sigma_0);
		}
	}
	
}
