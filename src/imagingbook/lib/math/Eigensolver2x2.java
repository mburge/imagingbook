/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.math;


/**
 * The this class computes the eigenvalues/eigenvectors for a 2x2
 * matrix
 *   | A B |
 *   | C D |
 * The implementation was inspired by Blinn, Jim: Consider the lowly 2x2 matrix. 
 * IEEE Computer Graphics and Applications, 16(2):82-88, 1996.
 */
public class Eigensolver2x2 {
	
	private final double A, B, C, D;
	
	public Eigensolver2x2(double[][] A) {
		this(A[0][0], A[0][1], A[1][0], A[1][1]);
	}
	
	public Eigensolver2x2(double A, double B, double C, double D) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.D = D;
	}

	public EigenPair[] realEigenValues2x2() {
		final double R = (A + D) / 2;
		final double S = (A - D) / 2;
		if ((S * S + B * C) < 0) // no real eigenvalues
			return null;
		else {
			double T = Math.sqrt(S * S + B * C);
			double eVal1 = R + T;
			double eVal2 = R - T;
			double[] eVec1, eVec2;
			if ((A - D) >= 0) {
				eVec1 = new double[] {S + T, C};
				eVec2 = new double[] {B, -S - T};
			} else {
				eVec1 = new double[] {B, -S + T};
				eVec2 = new double[] {S - T, C};
			}

			EigenPair[] e = new EigenPair[2];
			// put eigenpair with larger eigenvalue up front
			if (Math.abs(eVal1) >= Math.abs(eVal2)) {
				e[0] = new EigenPair(eVal1, eVec1);
				e[1] = new EigenPair(eVal2, eVec2);
			} else {
				e[1] = new EigenPair(eVal1, eVec1);
				e[0] = new EigenPair(eVal2, eVec2);
			}
			return e;
		}
	}

	/**
	 * EigenPair is a tuple <eigenvalue, eigenvector> and represents
	 * the solution to an eigen problem.
	 */
	public static class EigenPair {
		final double eival;
		final double[] eivec;

		public EigenPair(double eival, double[] eivec) {
			this.eival = eival;
			this.eivec = eivec.clone();
		}

		public double getEigenvalue() {
			return this.eival;
		}

		public double[] getEigenvector() {
			return this.eivec;
		}
		
		public String toString() {
			if (eivec == null)
				return "no eigenvalue / eigenvector";
			else {
				return String.format("eigenvalue: %.5f | eigenvector: %s", eival, Matrix.toString(eivec)) ;
			}
		}
	}
	
	
	// for Testing:
//	public static void main(String[] args) {
//		
//		double[][] A = {
//				{-0.009562, 0.011933}, 
//				{0.011933, -0.021158}
//				};
//		/*
//		 * eigenvalue: -0.02863 | eigenvector: {0.012, -0.019}
//		 * eigenvalue: -0.00209 | eigenvector: {0.019, 0.012}
//		 */
//		
//		double[][] B = {
//				{-0.004710, -0.006970},
//				{-0.006970, -0.029195}};
//		/*
//		 * eigenvalue: -0.03104 | eigenvector: {-0.007, -0.026}
//		 * eigenvalue: -0.00286 | eigenvector: {0.026, -0.007}
//		 */
//		
//		EigenPair[] eigenvals; 
//		eigenvals = new Eigensolver2x2(A).realEigenValues2x2();
//		System.out.println(eigenvals[0].toString());
//		System.out.println(eigenvals[1].toString());
//		
//		eigenvals = new Eigensolver2x2(B).realEigenValues2x2();
//		System.out.println(eigenvals[0].toString());
//		System.out.println(eigenvals[1].toString());
//	}
	

}
