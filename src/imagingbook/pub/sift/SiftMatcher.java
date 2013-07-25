/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.sift;

import imagingbook.lib.math.VectorNorm;
import imagingbook.lib.math.VectorNorm.NormType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SiftMatcher {
	
	public static class Parameters {
		public NormType norm = NormType.L2;
		public double rmMax = 0.8;	// = \bar{\rho}_{match}
		public boolean sort = true;
	}
	
	private final Parameters params;
	private final SiftDescriptor[] fA;
	private final VectorNorm am;

	// constructor - using default parameters
	public SiftMatcher(List<SiftDescriptor> sfA) {
		this(sfA, new Parameters());
	}
	
	// constructor - using specific parameters
	public SiftMatcher(List<SiftDescriptor> sfA, Parameters params) {
		this.fA = sfA.toArray(new SiftDescriptor[0]);
		this.params = params;
		am = params.norm.create();
	}
	
	public List<SiftMatch> matchDescriptors(List<SiftDescriptor> sfB) {
		SiftDescriptor[] fB = sfB.toArray(new SiftDescriptor[0]);
		List<SiftMatch> matches = new ArrayList<SiftMatch>(fA.length);
				
		for (int i = 0; i < fA.length; i++) {
			SiftDescriptor si = fA[i];
			int i1 = -1;
			int i2 = -1;
			double d1 = Double.MAX_VALUE;
			double d2 = Double.MAX_VALUE;
			
			for (int j = 0; j < fB.length; j++) {
				double d = dist(si, fB[j]);
				if (d < d1) {	// new absolute minimum distance
					i2 = i1;	// old best becomes second-best
					d2 = d1;
					i1 = j;
					d1 = d;
				}
				else // not a new absolute min., but possible second-best
					if (d < d2) { // new second-best
						i2 = j;
						d2 = d;
					}
			}
			if (i2 >= 0 && d2 > 0.001 && d1/d2 < params.rmMax) {
				SiftDescriptor s1 = fB[i1];
				SiftMatch m = new SiftMatch(si, s1, d1);
				matches.add(m);
			}
		}
		if (params.sort) Collections.sort(matches);  // sort matches to ascending distance d1
		return matches;
	}
	
	double dist(SiftDescriptor d1, SiftDescriptor d2) {
		//final ArrayMatcher matcher = params.norm.matcher;
		return am.distance(d1.getFeatures(), d2.getFeatures());
	}

}
