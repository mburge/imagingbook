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


public class Translation extends AffineMapping {
	
	public Translation() {
		super();
	}

	public Translation(double dx, double dy){
		super();
		a13 = dx;
		a23 = dy;
	}
	
	public Translation(double[] p) {
		this(p[0], p[1]);
	}
	
	public Translation(Point2D p1, Point2D p2) {
		this(p2.getX() - p1.getX(), p2.getY() - p1.getY());
	}

	public Translation(LinearMapping t) {
		super();
		this.a13 = t.a13;
		this.a23 = t.a23;
	}

	public Translation invert() {
		Translation t2 = new Translation();
		t2.a13 = -this.a13;
		t2.a23 = -this.a23;
		//return (Translation) super.invert();
		return t2;
	}
	
	// warp parameter support -------------------------------------
	
	@Override
	public int getParameterCount() {
		return 2;
	}
	
	@Override
	public double[] getParameters() {
		double[] p = new double[] {a13,	a23};
		return p;
	}

	@Override
	public void setParameters(double[] p) {
		a13 = p[0];
		a23 = p[1];
	}
	
	private final double[][] J =	// this transformation has a constant Jacobian
		{{1, 0},
		 {0, 1}};
	
	@Override
	public double[][] getJacobian(double[] X) {
		return J;
	}
	
	@Override
	public Translation duplicate() {
		return new Translation(this);
	}
	
	@Override
	public Translation duplicate(double[] p) {
		Translation copy = new Translation();
		copy.setParameters(p);
		return copy;
	}


//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		System.out.println("hello");
//		Translation t = new Translation(1, 3);
//		System.out.println("hello" + t.toString());
//		Translation t2 = t.invert();
//		System.out.println("hello" + t2.toString());
//	}
}
