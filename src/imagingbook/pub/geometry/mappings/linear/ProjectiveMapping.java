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

public class ProjectiveMapping extends LinearMapping { 
	
	// creates the identity mapping:
	public ProjectiveMapping() {
		super();
	}
	
	public ProjectiveMapping(double a11, double a12, double a13, double a21,
					  double a22, double a23, double a31, double a32, boolean inv) {
		super(a11, a12, a13, a21, a22, a23, a31, a32, 1, inv);
	}
	
	public ProjectiveMapping(LinearMapping lm) {
		super(lm);
		this.normalize();
	}
	
	// creates the projective mapping from the unit square S to
	// the arbitrary quadrilateral Q given by points P1 ... P4:
	public ProjectiveMapping(Point2D A1, Point2D A2, Point2D A3, Point2D A4) {
		super();
		double x1 = A1.getX(), x2 = A2.getX(), x3 = A3.getX(), x4 = A4.getX(); 
		double y1 = A1.getY(), y2 = A2.getY(), y3 = A3.getY(), y4 = A4.getY();
		double S = (x2-x3)*(y4-y3) - (x4-x3)*(y2-y3);
		// TODO: check S for zero value and throw exception
		a31 = ((x1-x2+x3-x4)*(y4-y3)-(y1-y2+y3-y4)*(x4-x3)) / S;
		a32 = ((y1-y2+y3-y4)*(x2-x3)-(x1-x2+x3-x4)*(y2-y3)) / S;
		a11 = x2 - x1 + a31*x2;
		a12 = x4 - x1 + a32*x4;
		a13 = x1;
		a21 = y2 - y1 + a31*y2;
		a22 = y4 - y1 + a32*y4;
		a23 = y1;
	}
	
	@Deprecated
	public static ProjectiveMapping makeMapping(Point2D A1, Point2D A2, Point2D A3, Point2D A4) {
		double x1 = A1.getX(), x2 = A2.getX(), x3 = A3.getX(), x4 = A4.getX(); 
		double y1 = A1.getY(), y2 = A2.getY(), y3 = A3.getY(), y4 = A4.getY();
		double S = (x2-x3)*(y4-y3) - (x4-x3)*(y2-y3);
		double a31 = ((x1-x2+x3-x4)*(y4-y3)-(y1-y2+y3-y4)*(x4-x3)) / S;
		double a32 = ((y1-y2+y3-y4)*(x2-x3)-(x1-x2+x3-x4)*(y2-y3)) / S;
		double a11 = x2 - x1 + a31*x2;
		double a12 = x4 - x1 + a32*x4;
		double a13 = x1;
		double a21 = y2 - y1 + a31*y2;
		double a22 = y4 - y1 + a32*y4;
		double a23 = y1;
		return new ProjectiveMapping(a11, a12, a13, a21, a22, a23, a31, a32, false);
	}
	
	// creates the projective mapping between arbitrary quadrilaterals Qa, Qb
	// via the unit square: Qa -> S -> Qb
	public ProjectiveMapping(
			Point2D A1, Point2D A2, Point2D A3, Point2D A4, 
			Point2D B1, Point2D B2, Point2D B3, Point2D B4)	{
		super();	// initialized to identity
		ProjectiveMapping T1 = new ProjectiveMapping(A1, A2, A3, A4);
		ProjectiveMapping T2 = new ProjectiveMapping(B1, B2, B3, B4);
		ProjectiveMapping T1i = T1.invert();
		ProjectiveMapping T12 = T1i.concat(T2);
		this.concatDestructive(T12);	// transfer T12 -> this
	}
	
	public ProjectiveMapping(double[] p) {
		super();
		this.setParameters(p);
	}
	
	@Deprecated
	public static ProjectiveMapping makeMapping (
			Point2D A1, Point2D A2, Point2D A3, Point2D A4, 
			Point2D B1, Point2D B2, Point2D B3, Point2D B4)	{
		ProjectiveMapping T1 = makeMapping(A1, A2, A3, A4);
		ProjectiveMapping T2 = makeMapping(B1, B2, B3, B4);
		ProjectiveMapping T1i = T1.invert();
		ProjectiveMapping T12 = T1i.concat(T2);
		T12.normalize();
		T12.isInverse = false;
		return T12;
	}
	
	public ProjectiveMapping concat(LinearMapping B) {
		ProjectiveMapping A = new ProjectiveMapping(this);
		A.concatDestructive(B);
		return A;
	}
	
	public ProjectiveMapping invert() {
		ProjectiveMapping pm = new ProjectiveMapping(this);
		pm.invertDestructive();
		return pm;
	}
	
	void normalize() {
		// scales the matrix such that a33 becomes 1
		// TODO: check a33 for zero value and throw exception
		a11 = a11/a33;		a12 = a12/a33;		a13 = a13/a33;
		a21 = a21/a33;		a22 = a22/a33;		a23 = a23/a33;
		a31 = a31/a33;		a32 = a32/a33;		a33 = 1;
	}
	
/*	
	ProjectiveMapping(Point2D B1, Point2D B2, Point2D B3, Point2D B4) {
		double x1 = B1.x, x2 = B2.x, x3 = B3.x, x4 = B4.x; 
		double y1 = B1.y, y2 = B2.y, y3 = B3.y, y4 = B4.y;
		double S = (x2-x3)*(y4-y3) - (x4-x3)*(y2-y3);
		
		a31 = ((x1-x2+x3-x4)*(y4-y3) - (y1-y2+y3-y4)*(x4-x3)) / S;
		a32 = ((y1-y2+y3-y4)*(x2-x3) - (x1-x2+x3-x4)*(y2-y3)) / S;
		a11 = x2 - x1 + a31*x2;
		a12 = x4 - x1 + a32*x4;
		a13 = x1;
		a21 = y2 - y1 + a31*y2;
		a22 = y4 - y1 + a32*y4;
		a23 = y1;
	}
*/
	
	// warp parameter support -------------------------------------
	
	@Override
	public int getParameterCount() {
		return 8;
	}
	
	@Override
	public double[] getParameters() {
		double[] p = new double[] {
			a11 - 1, 
			a12,
			a21,
			a22 - 1,
			a31,
			a32,
			a13,
			a23,
			};
		return p;
	}
	
//	p[0] = M3x3[0][0] - 1;	// = a
//	p[1] = M3x3[0][1];		// = b
//	p[2] = M3x3[1][0];		// = c
//	p[3] = M3x3[1][1] - 1;	// = d
//	p[4] = M3x3[2][0];		// = e
//	p[5] = M3x3[2][1];		// = f
//	p[6] = M3x3[0][2];		// = tx
//	p[7] = M3x3[1][2];		// = ty

	@Override
	public void setParameters(double[] p) {
		a11 = p[0] + 1;   a12 = p[1];        a13 = p[6];
		a21 = p[2];       a22 = p[3] + 1;    a23 = p[7];
		a31 = p[4];       a32 = p[5];        a33 = 1;
	}
	
	@Override
	public double[][] getJacobian(double[] xy) {
		// see Baker 2003 "20 Years" Part 1, Eq. 99 (p. 46)
		final double x = xy[0];
		final double y = xy[1];
		double a = a11 * x + a12 * y + a13;	// = alpha
		double b = a21 * x + a22 * y + a23;	// = beta
		double c = a31 * x + a32 * y + 1;	// = gamma
		double cc = c * c;
		// TODO: check c for zero-value and throw exception, make more efficient
		return new double[][]
			{{x/c, y/c, 0,   0,   -(x*a)/cc, -(y*a)/cc, 1/c, 0  },
			 {0,   0,   x/c, y/c, -(x*b)/cc, -(y*b)/cc, 0,   1/c}};
	}

	@Override
	public ProjectiveMapping duplicate() {
		return (ProjectiveMapping) this.clone();
	}
	
	@Override
	public ProjectiveMapping duplicate(double[] p) {
		ProjectiveMapping copy = this.duplicate();
		copy.setParameters(p);
		return copy;
	}
}
