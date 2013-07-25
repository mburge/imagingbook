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

import java.awt.geom.Point2D;


/*
 * 2007: Changed to use the JAMA numerical math package 
 * (http://math.nist.gov/javanumerics/jama/) instead of JAMPACK.
 * 2013: changed to use methods from the local matrix library
 * (based on Apache Common Maths) to solve linear systems of 
 * equations.
 */

public class BilinearMapping extends Mapping { 
	double a1, a2, a3, a4;
	double b1, b2, b3, b4;
	
	BilinearMapping(
					double a1, double a2, double a3, double a4,
					double b1, double b2, double b3, double b4, 
					boolean inv) {
		this.a1 = a1;   this.a2 = a2;   this.a3 = a3;   this.a4 = a4;
		this.b1 = b1;   this.b2 = b2;   this.b3 = b3;   this.b4 = b4;	
		isInverse = inv;			
	}
	
	//map between arbitrary quadrilaterals
	public static BilinearMapping makeInverseMapping(
			Point2D P1, Point2D P2, Point2D P3, Point2D P4,	// source quad
			Point2D Q1, Point2D Q2, Point2D Q3, Point2D Q4)	// target quad
		{	
		//define column vectors X, Y
		double[] x = {Q1.getX(), Q2.getX(), Q3.getX(), Q4.getX()};
		double[] y = {Q1.getY(), Q2.getY(), Q3.getY(), Q4.getY()};
		
		//define matrix M
		double[][] M = new double[][]
			{{P1.getX(), P1.getY(), P1.getX() * P1.getY(), 1},
			 {P2.getX(), P2.getY(), P2.getX() * P2.getY(), 1},
			 {P3.getX(), P3.getY(), P3.getX() * P3.getY(), 1},
			 {P4.getX(), P4.getY(), P4.getX() * P4.getY(), 1}};

		double[] a = Matrix.solve(M, x);		// solve x = M * a = x (a is unknown)
		double[] b = Matrix.solve(M, y);		// solve y = M * b = y (b is unknown)
		
		double a1 = a[0];		double b1 = b[0];
		double a2 = a[1];		double b2 = b[1];
		double a3 = a[2];		double b3 = b[2];
		double a4 = a[3];		double b4 = b[3];
		   
		return new BilinearMapping(a1, a2, a3, a4, b1, b2, b3, b4, true);
	}
	
	public double[] applyTo (double[] xy){
		double x0 = xy[0];
		double y0 = xy[1];
		double x1 = a1 * x0 + a2 * y0 + a3 * x0 * y0 + a4;
		double y1 = b1 * x0 + b2 * y0 + b3 * x0 * y0 + b4;
		//pnt.setLocation(x1, y1);
		return new double[] {x1, y1};
	}	
						
//	public Point2D applyTo (Point2D pnt){
//		double x0 = pnt.getX();
//		double y0 = pnt.getY();
//		double x1 = a1 * x0 + a2 * y0 + a3 * x0 * y0 + a4;
//		double y1 = b1 * x0 + b2 * y0 + b3 * x0 * y0 + b4;
//		//pnt.setLocation(x1, y1);
//		return new Point2D.Double(x1, y1);
//	}	
	
	public String toString() {
		return 
			"A=(" + a1 + "," + a2 + "," + a3 + "," + a4 + ")" + 
			" / " +
			"B=(" + b1 + "," + b2 + "," + b3 + "," + b4 + ")" ;
	}

}
