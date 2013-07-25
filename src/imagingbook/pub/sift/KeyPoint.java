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

public class KeyPoint implements Cloneable {
	public final int p;	// octave index
	public final int q;	// level index
	
	public final int u;	// lattice x-position 
	public final int v;	// lattice y-position 
	public float x;		// interpolated lattice x-position 
	public float y;		// interpolated lattice y-position 
	
	public float x_real;	// real x-position (in image coordinates)		
	public float y_real;	// real y-position (in image coordinates)		
	public float scale;		// absolute scale
	
	public float[] orientation_histogram;	// for debugging only
	public double orientation;	// dominant orientation
	
	protected KeyPoint(int p, int q, int u, int v) {
		this.p = p;
		this.q = q;
		this.u = u;
		this.v = v;
		this.x = u;
		this.y = v;
	}
	
	protected KeyPoint(int p, int q, int u, int v, float x, float y, float x_real, float y_real, float scale) {
		this.p = p;
		this.q = q;
		this.u = u;
		this.v = v;
		this.x = x;
		this.y = y;
		this.x_real = x_real;
		this.y_real = y_real;
		this.scale = scale;
	}
	
	public String toString() {
		return String.format(Locale.US, "p=%d, q=%d, u=%d, v=%d, scale=%.2f", p, q, u, v, scale);
	}
	
	public KeyPoint clone() {
		try {
			return (KeyPoint) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
