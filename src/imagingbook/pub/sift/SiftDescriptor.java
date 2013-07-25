/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.sift;


import java.util.Locale;

public class SiftDescriptor {
	
	private final double x;	// image position
	private final double y;
	private final double scale;
	private final double orientation;
	private final int[] features;
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getScale() {
		return scale;
	}

	public double getOrientation() {
		return orientation;
	}
	public int[] getFeatures() {
		return features;
	}

	public SiftDescriptor(double x, double y, double scale, double orientation, int[] fi) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.orientation = orientation;
		this.features = fi;
	}
	
	// -----------------------------
	
	public double getDistanceL1(SiftDescriptor other) {
		int[] f1 = this.features;
		int[] f2 = other.features;
		int sum = 0;
		for (int i=0; i<f1.length; i++) {
			sum = sum + Math.abs(f1[i] - f2[i]);
		}
		return sum;
	}
	
	public double getDistanceL2(SiftDescriptor other) {
		int[] f1 = this.features;
		int[] f2 = other.features;
		int sum = 0;
		for (int i=0; i<f1.length; i++) {
			int d = f1[i] - f2[i];
			sum = sum + d * d;
		}
		return Math.sqrt(sum);
	}
	
	public double getDistanceLinf(SiftDescriptor other) {
		int[] f1 = this.features;
		int[] f2 = other.features;
		int dmax = 0;
		for (int i=0; i<f1.length; i++) {
			int d = Math.abs(f1[i] - f2[i]);
			dmax = Math.max(dmax, d);
		}
		return dmax;
	}
	
	// -----------------------------
	
	public String toString() {
		return String.format(Locale.US, "%.2f %.2f %.2f %.2f", x, y, scale, orientation);
	}

}
