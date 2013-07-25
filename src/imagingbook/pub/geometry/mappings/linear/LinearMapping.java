/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.geometry.mappings.linear;

import imagingbook.lib.math.Matrix;
import imagingbook.pub.geometry.mappings.Mapping;
import imagingbook.pub.geometry.mappings.WarpParameters;

import java.awt.geom.Point2D;

/*
 * 2013-02-02: 
 * Changed applyTo(Point2D) to return a new point (no reuse).
 * Changed LinearMapping invert() to return new mapping.
 * Changed LinearMapping concat() to return new mapping.
 */

public class LinearMapping extends Mapping implements WarpParameters {
	
	protected double 
		a11 = 1, a12 = 0, a13 = 0,
		a21 = 0, a22 = 1, a23 = 0,
		a31 = 0, a32 = 0, a33 = 1;
		   
	public LinearMapping() {
		// creates the identity mapping
	}
		   
	protected LinearMapping (
			double a11, double a12, double a13, 
			double a21, double a22, double a23,
			double a31, double a32, double a33, boolean inv) {
		this.a11 = a11;  this.a12 = a12;  this.a13 = a13;
		this.a21 = a21;  this.a22 = a22;  this.a23 = a23;
		this.a31 = a31;  this.a32 = a32;  this.a33 = a33;
		isInverse = inv;
	}
	
	protected LinearMapping (LinearMapping lm) {
		this.a11 = lm.a11;  this.a12 = lm.a12;  this.a13 = lm.a13;
		this.a21 = lm.a21;  this.a22 = lm.a22;  this.a23 = lm.a23;
		this.a31 = lm.a31;  this.a32 = lm.a32;  this.a33 = lm.a33;
		this.isInverse = lm.isInverse;
	}
	
	public double[] applyTo (double[] xy) {
		return applyTo(xy[0], xy[1]);
	}
	
	public double[] applyTo (double x, double y) {
		double h =  (a31 * x + a32 * y + a33);
		double x1 = (a11 * x + a12 * y + a13) / h;
		double y1 = (a21 * x + a22 * y + a23) / h;
		// pnt.setLocation(x1, y1);
		return new double[] {x1, y1};
	}
		   
	public Point2D applyTo (Point2D pnt) {
		double x = pnt.getX();
		double y = pnt.getY();
		double h =  (a31 * x + a32 * y + a33);
		double x1 = (a11 * x + a12 * y + a13) / h;
		double y1 = (a21 * x + a22 * y + a23) / h;
		// pnt.setLocation(x1, y1);
		return new Point2D.Double(x1, y1);
	}
	
	public LinearMapping invert() {
		LinearMapping lm = new LinearMapping(this);
		lm.invertDestructive();
		return lm;
	}
	
	public void invertDestructive() {
		double det = a11*a22*a33 + a12*a23*a31 + a13*a21*a32 - 
					 a11*a23*a32 - a12*a21*a33 - a13*a22*a31;
		double b11 = (a22*a33 - a23*a32) / det; 
		double b12 = (a13*a32 - a12*a33) / det; 
		double b13 = (a12*a23 - a13*a22) / det; 
		double b21 = (a23*a31 - a21*a33) / det; 
		double b22 = (a11*a33 - a13*a31) / det; 
		double b23 = (a13*a21 - a11*a23) / det;
		double b31 = (a21*a32 - a22*a31) / det; 
		double b32 = (a12*a31 - a11*a32) / det; 
		double b33 = (a11*a22 - a12*a21) / det;
		a11 = b11;		a12 = b12;		a13 = b13;
		a21 = b21;		a22 = b22;		a23 = b23;
		a31 = b31;		a32 = b32;		a33 = b33;
		isInverse = !isInverse;
	}
	
	public LinearMapping concat(LinearMapping B) {
		LinearMapping A = new LinearMapping(this);
		A.concatDestructive(B);
		return A;
	}
	
	// concatenates THIS transform matrix A with B: A-> B*A
	public void concatDestructive(LinearMapping B){
		//LinearMapping lm = (LinearMapping) duplicate();
		double b11 = B.a11*a11 + B.a12*a21 + B.a13*a31;
		double b12 = B.a11*a12 + B.a12*a22 + B.a13*a32;
		double b13 = B.a11*a13 + B.a12*a23 + B.a13*a33;
		
		double b21 = B.a21*a11 + B.a22*a21 + B.a23*a31;
		double b22 = B.a21*a12 + B.a22*a22 + B.a23*a32;
		double b23 = B.a21*a13 + B.a22*a23 + B.a23*a33;
		
		double b31 = B.a31*a11 + B.a32*a21 + B.a33*a31;
		double b32 = B.a31*a12 + B.a32*a22 + B.a33*a32;
		double b33 = B.a31*a13 + B.a32*a23 + B.a33*a33;
		a11 = b11;		a12 = b12;		a13 = b13;
		a21 = b21;		a22 = b22;		a23 = b23;
		a31 = b31;		a32 = b32;		a33 = b33;
	}
	
	public double[][] getTransformationMatrix () {
		return new double[][]
				{{a11, a12, a13},
				 {a21, a22, a23},
				 {a31, a32, a33}};
	}

	@Override
	public int getParameterCount() {
		return 9;
	}
	
	@Override
	public double[] getParameters() {
		throw new UnsupportedOperationException("method not implemented");
		//return null;
	}

	@Override
	public void setParameters(double[] p) {
		throw new UnsupportedOperationException("method not implemented");
	}

	@Override
	public double[][] getJacobian(double[] x) {
		throw new UnsupportedOperationException("method not implemented");
		//return null;
	}

	public LinearMapping duplicate() {
		return (LinearMapping) this.clone();
	}
	
	public LinearMapping duplicate(double[] p) {
		LinearMapping copy = this.duplicate();
		copy.setParameters(p);
		return copy;
	}
	
	public double[][] toArray() {
		double[][] A =
			{{a11, a12, a13},
			 {a21, a22, a23},
			 {a31, a32, a33}};
		return A;
	}
	
	public String toString() {
		double[][] A = this.toArray();
		return Matrix.toString(A);
	}
	
}




