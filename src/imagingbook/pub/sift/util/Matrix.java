/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.sift.util;

// TODO: change to methods defined in imagingbook.lib.math.Matrix
public class Matrix {
	
	static final float EPSILON_FLOAT 	= 1e-35f;	// smallest possible float denominator is ~ 1e-38f
	static final double EPSILON_DOUBLE 	= 1e-300;	// smallest possible float denominator is ~ 1e-308f
	
	public static void multiply(final float[] V, float s) {
		for (int i=0; i<V.length; i++) {
				V[i] = V[i] * s;
		}
	}
	
	public static void multiply(final float[][] A, float s) {
		for (int i=0; i<A.length; i++) {
			for (int j=0; j<A[i].length; j++) {
				A[i][j] = A[i][j] * s;
			}
		}
	}
	/*
	 * Y <- A . X
	 */
	public static void multiply(final float[][] A, float[] X, float[] Y) {
		for (int i=0; i<A.length; i++) {
			float s = 0;
			for (int j=0; j<A[i].length; j++) {
				s = s + A[i][j] * X[j];
			}
			Y[i] = s;
		}
	}
	
	public static double determinant2x2(final float[][] A) {
		return	A[0][0] * A[1][1] - A[0][1] * A[1][0];
	}
	
	public static float[][] invert2x2(final float[][] A) {
		final double det = determinant2x2(A);
		if (Math.abs(det) < EPSILON_DOUBLE)
			return null;
		else {
			final double a00 = A[0][0];
			final double a01 = A[0][1];
			final double a10 = A[1][0];
			final double a11 = A[1][1];
			A[0][0] = (float) ( a11 / det);
			A[0][1] = (float) (-a01 / det);
			A[1][0] = (float) (-a10 / det);
			A[1][1] = (float) ( a00 / det);
			//multiply(A, 1/det);
			return A;
		}
	}
	
	public static double determinant3x3(final float[][] A) {
		return
			A[0][0] * A[1][1] * A[2][2] +
			A[0][1] * A[1][2] * A[2][0] +
			A[0][2] * A[1][0] * A[2][1] -
			A[2][0] * A[1][1] * A[0][2] -
			A[2][1] * A[1][2] * A[0][0] -
			A[2][2] * A[1][0] * A[0][1] ;
	}
	
	// Note: this works by side-effect (destructively)!!
	public static float[][] invert3x3(final float[][] A) {
		final double det = determinant3x3(A);
		//IJ.log("   determinant = " + det);
		if (Math.abs(det) < EPSILON_DOUBLE)
			return null;
		else {
			final double a00 = A[0][0];
			final double a01 = A[0][1];
			final double a02 = A[0][2];
			final double a10 = A[1][0];
			final double a11 = A[1][1];
			final double a12 = A[1][2];
			final double a20 = A[2][0];
			final double a21 = A[2][1];
			final double a22 = A[2][2];
			A[0][0] =  (float) ((a11 * a22 - a12 * a21) / det);
			A[0][1] =  (float) ((a02 * a21 - a01 * a22) / det);
			A[0][2] =  (float) ((a01 * a12 - a02 * a11) / det);
			
			A[1][0] =  (float) ((a12 * a20 - a10 * a22) / det);
			A[1][1] =  (float) ((a00 * a22 - a02 * a20) / det);
			A[1][2] =  (float) ((a02 * a10 - a00 * a12) / det);
			
			A[2][0] =  (float) ((a10 * a21 - a11 * a20) / det);
			A[2][1] =  (float) ((a01 * a20 - a00 * a21) / det);
			A[2][2] =  (float) ((a00 * a11 - a01 * a10) / det);
			//multiply(A, 1/det);
			return A;
		}
	}
	
	public static double normL1(float[] X) {
		double sum = 0;
		for (float x : X) {
			sum = sum + Math.abs(x);
		}
		return sum;
	}
	
	public static double normL2(float[] X) {
		double sum = 0;
		for (float x : X) {
			sum = sum + (x * x);
		}
		return Math.sqrt(sum);
	}
	
	public static void printMatrix(float[][] A) {
		for (int i=0; i<A.length; i++) {
			for (int j=0; j<A[i].length; j++) {
				System.out.format("%10.4f  ", A[i][j]);
			}
			System.out.format("\n");
		}
	}
	
}
