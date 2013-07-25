/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.sift;

/*
 * Make dynamic such that the match can contain an arbitrary sequence of features.
 */

public class SiftMatch3 implements Comparable<SiftMatch3> {
	
	final SiftDescriptor descriptor1, descriptor2, descriptor3;
	final double distance;
	
	public SiftMatch3(SiftDescriptor descriptor1, SiftDescriptor descriptor2, SiftDescriptor descriptor3, double distance) {
		this.descriptor1 = descriptor1;
		this.descriptor2 = descriptor2;
		this.descriptor3 = descriptor3;
		this.distance = distance;
	}
	
	public SiftDescriptor getDescriptor1() {
		return descriptor1;
	}
	
	public SiftDescriptor getDescriptor2() {
		return descriptor2;
	}
	
	public SiftDescriptor getDescriptor3() {
		return descriptor3;
	}
	
	public double getDistance() {
		return distance;
	}

	@Override
	public int compareTo(SiftMatch3 match2) {
		if (this.distance < match2.distance)
			return -1;
		else if (this.distance > match2.distance)
			return 1;
		else
			return 0;
	}
	
	public String toString() {
		return String.format("match %.2f", this.distance);
	}

}
