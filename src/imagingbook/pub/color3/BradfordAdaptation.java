/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.color3;

/*
 * This class represents a linear chromatic adaptation transform.
 * The transformation is specified by the matrix 'Mfwd' and its
 * inverse 'Minv'.
 */

public class BradfordAdaptation extends ChromaticAdaptation {
	
	// CAT transform matrices (forward and inverse)
	static protected double[][] Mfwd = new double[][] {
	    { 0.8951,  0.2664, -0.1614},
	    {-0.7502,  1.7135,  0.0367},
	    { 0.0389, -0.0685,  1.0296}};
	static protected double[][] Minv = new double[][] {	
		{ 0.9869929055, -0.1470542564, 0.1599626517}, 
		{ 0.4323052697,  0.5183602715, 0.0492912282},
		{-0.0085286646,  0.0400428217, 0.9684866958}};
	
	//	the complete color adaptation transformation matrix
	protected double[][] Mcat = null;
	
	public BradfordAdaptation(float[] white1, float[] white2) {
		super(white1, white2);
		double[] rgb1 = mult(Mfwd, white1);
		double[] rgb2 = mult(Mfwd, white2);
		double[][] Mrgb = rgbMatrix(rgb1, rgb2);
		Mcat = mult(Minv, mult(Mrgb,Mfwd));
	}
	
	public BradfordAdaptation(Illuminant illum1, Illuminant illum2) {
		this(illum1.getXyzFloat(), illum2.getXyzFloat());
	}
	
	// transformation of color coordinates
	public float[] apply (float[] XYZ1) {
		float[] XYZ2 = new float[3];
		for (int i=0; i<3; i++) {
			XYZ2[i] = (float) (Mcat[i][0] * XYZ1[0] + Mcat[i][1] * XYZ1[1] + Mcat[i][2] * XYZ1[2]);
		}
		return XYZ2;
	}
	
	// matrix utility methods:
	
	// multiply matrices: (m1 * m2)
	static double[][] mult (double[][] m1, double[][] m2) {
		// m1 is of size (p,q)
		// m2 is of size (q,r)
		int p = m1.length;		// m1 has p rows
		int q = m1[0].length;	// m1 has q colums
		int r = m2[0].length;	// m2 has q rows, r columns
		if (q != m2.length) throw new IllegalArgumentException();
		double[][] result = new double[p][r];
		for (int i=0; i<p; i++) {
			for (int j=0; j<r; j++) {
				double s = 0.0;
				for (int k=0; k<q; k++) {
					s = s + m1[i][k] * m2[k][j];
				}
				result[i][j] = s;
			}
		}
		return result;
	}
	
	// multiply matrix M with float vector x
	static double[] mult (double[][] M, float[] x) {
		int p = M.length;
		int q = M[0].length;
		if (x.length != q) throw new IllegalArgumentException();
		double[] y = new double[p];
		for (int i=0; i<p; i++) {
			for (int k=0; k<q; k++) {
				y[i] = y[i] + M[i][k] * x[k];
			}
		}
		return y;
	}
	
	// returns a diagonal matrix with the ratios of the rgb components
	// obtained by transforming the two white points
	double[][] rgbMatrix(double[] rgb1, double[] rgb2) {
		if (rgb1.length != rgb2.length) throw new IllegalArgumentException();
		int n = rgb1.length;
		double[][] Madapt = new double[n][n];
		for (int i=0; i<n; i++) {
			Madapt[i][i] = rgb2[i] / rgb1[i];
		}
		return Madapt;
	}

	// prints the composite transformation matrix
	public void printCAT () {
		for (int i=0; i<Mcat.length; i++) {
			for (int j=0; j<Mcat[0].length; j++) {
				System.out.printf(java.util.Locale.US, "%8.6f ", Mcat[i][j]);
			}
			System.out.println();
		}
	}
}
