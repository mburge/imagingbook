/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.geometry.mappings.linear;

import java.awt.geom.Point2D;

public class AffineMapping extends LinearMapping {
	
	// creates the identity mapping:
	public AffineMapping() {
		super();
	}
   
	public AffineMapping (
			double a11, double a12, double a13, 
			double a21, double a22, double a23, 
			boolean inv) {
		super(a11, a12, a13, a21, a22, a23, 0, 0, 1, inv);
	}
	
	public AffineMapping(LinearMapping lm) {
		super(lm);
		a31 = 0;
		a32 = 0;
		a33 = 1;
	}
	
	public AffineMapping(Point2D A1, Point2D A2, Point2D A3, Point2D B1, Point2D B2, Point2D B3) {
		super();
		double ax1 = A1.getX(), ax2 = A2.getX(), ax3 = A3.getX();
		double ay1 = A1.getY(), ay2 = A2.getY(), ay3 = A3.getY();
		double bx1 = B1.getX(), bx2 = B2.getX(), bx3 = B3.getX();
		double by1 = B1.getY(), by2 = B2.getY(), by3 = B3.getY();
		
		double S = ax1*(ay3-ay2) + ax2*(ay1-ay3) + ax3*(ay2-ay1); // TODO: check S for zero value and throw exception!
		a11 = (ay1*(bx2-bx3) + ay2*(bx3-bx1) + ay3*(bx1-bx2)) / S;
		a12 = (ax1*(bx3-bx2) + ax2*(bx1-bx3) + ax3*(bx2-bx1)) / S;
		a21 = (ay1*(by2-by3) + ay2*(by3-by1) + ay3*(by1-by2)) / S;
		a22 = (ax1*(by3-by2) + ax2*(by1-by3) + ax3*(by2-by1)) / S;
		a13 = 
				(ax1*(ay3*bx2-ay2*bx3) + ax2*(ay1*bx3-ay3*bx1) + ax3*(ay2*bx1-ay1*bx2)) / S;
		a23 = 
				(ax1*(ay3*by2-ay2*by3) + ax2*(ay1*by3-ay3*by1) + ax3*(ay2*by1-ay1*by2)) / S;
	}
	
	public AffineMapping(double[] p) {
		//super(p[0] + 1, p[1], p[2], p[3] + 1, p[4], p[5], false);
		super();
		this.setParameters(p);
	}
	
	public AffineMapping concat(LinearMapping B) {
		AffineMapping A = new AffineMapping(this);
		A.concatDestructive(B);
		return A;
	}
	
	public AffineMapping invert() {
		AffineMapping pm = new AffineMapping(this);
		pm.invertDestructive();
		return pm;
	}
	
	// warp parameter support -------------------------------------
	
	@Override
	public int getParameterCount() {
		return 6;
	}
	
	@Override
	public double[] getParameters() {
		double[] p = new double[] {
			a11 - 1, 
			a12,
			a21,
			a22 - 1,
			a13,
			a23 };
		return p;
	}

	@Override
	public void setParameters(double[] p) {
		a11 = p[0] + 1;
		a12 = p[1];
		a21 = p[2];
		a22 = p[3] + 1;
		a13 = p[4];
		a23 = p[5];
	}
	
	@Override
	public double[][] getJacobian(double[] xy) {
		final double x = xy[0];
		final double y = xy[1];
		return new double[][]
				{{x, y, 0, 0, 1, 0},
				 {0, 0, x, y, 0, 1}};
	}
	
	@Override
	public AffineMapping duplicate() {
		//return new AffineMapping(this);
		return (AffineMapping) this.clone();
	}
	
	@Override
	public AffineMapping duplicate(double[] p) {
		AffineMapping copy = this.duplicate();
		copy.setParameters(p);
		return copy;
	}
	
	// ---------------------------------------
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Forward mapping:");
		AffineMapping t = new AffineMapping(1, 3, 5, -1, 3, 4, false);
		System.out.println(t.toString());
		AffineMapping t2 = t.invert();
		System.out.println("Inverse mapping:");
		System.out.println(t2.toString());
	}
	
	
}




