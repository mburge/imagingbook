/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.math;

import ij.IJ;
import imagingbook.lib.math.Complex;

import java.awt.geom.Point2D;
import java.util.Locale;

public class Complex {

	public double re;
	public double im;

	public Complex(double re, double im) {
		this.re = re;
		this.im = im;
	}

	public Complex(Point2D p) {
		this.re = p.getX();
		this.im = p.getY();
	}

	public Complex(Complex c) {
		this.re = c.re;
		this.im = c.im;
	}

	/*
	 * Create a complex quantity on the unit circle with angle 'phi'.
	 * e^{\i \phi} = \cos(\phi) + \i \cdot \sin(\phi)
	 */
	public Complex(double phi) {
		this.re = Math.cos(phi);
		this.im = Math.sin(phi);
	}

	public static Complex[] makeComplexVector(int M) {
		Complex[] g = new Complex[M];
		for (int i = 0; i < M; i++) {
			g[i] = new Complex(0,0);
		}
		return g;
	}

	public static Complex[] duplicate(Complex[] g1) {
		Complex[] g2 = new Complex[g1.length];
		for (int i=0; i<g1.length; i++) {
			g2[i] = new Complex(g1[i].re, g1[i].im);
		}
		return g2;
	}


	public static Complex[] makeComplexVector(double[] signal) {
		int M = signal.length;
		Complex[] g = new Complex[M];
		for (int i = 0; i < M; i++) {
			g[i] = new Complex(signal[i], 0);
		}
		return g;
	}

	public static Complex[] makeComplexVector(double[] real, double[] imag) {
		int M = real.length;
		Complex[] g = new Complex[M];
		for (int i = 0; i < M; i++) {
			g[i] = new Complex(real[i], imag[i]);
		}
		return g;
	}

	// radius
	public double abs() {
		return Math.sqrt(this.abs2());
	}

	// squared radius
	public double abs2() {
		return re*re + im*im;
	}

	// angle 
	public double arg() {
		return Math.atan2(im, re);
	}


	/*
	 *  ---- complex arithmetic ---------------
	 */

	// complex conjugate
	public Complex conjugate() {
		return new Complex(this.re, -this.im);
	}

	public Complex add(Complex c2) {
		return new Complex(this.re + c2.re,  this.im + c2.im);
	}

	public Complex mult(double s) {
		return new Complex(this.re * s, this.im * s);
	}

	public Complex mult(Complex c2) {
		// (x1 + i y1)(x2 + i y2) = (x1 x2 – y1 y2) + i (x1 y2 + y1 x2)
		Complex c1 = this;
		double x = c1.re * c2.re - c1.im * c2.im;
		double y = c1.re * c2.im + c1.im * c2.re;
		return new Complex(x, y);
	}

	public Complex rotate(double phi) {
		return this.mult(new Complex(phi));
	}

	public double distance(Complex c2) {
		Complex c1 = this;
		double dRe = c1.re - c2.re;
		double dIm = c1.im - c2.im;
		return Math.sqrt(dRe*dRe + dIm*dIm);
	}

	public double crossProduct(Complex c2) {
		Complex c1 = this;
		return c1.re * c2.im - c1.im * c2.re;
	}

	public double dotProduct(Complex c2) {
		Complex c1 = this;
		return c1.re * c2.re + c1.im * c2.im;
	}

	public String toString() {
		if (this.im >= 0) {
			return String.format(Locale.US, "(%.4f + %.4f i)", this.re, this.im);
		}
		else {
			return String.format(Locale.US, "(%.4f - %.4f i)", this.re, Math.abs(this.im));
		}
	}
	
	// -------------------------------------------------------
	
	public static void printComplexVector(Complex[] g, String title) {
		IJ.log("Printing " + title);
		for (int i = 0; i < g.length; i++) {
			if (g[i] == null)
				IJ.log(String.format("%d: ********", i));
			else {
				double gr = g[i].re;
				double gi = g[i].im;
				if (gi >= 0) {
					IJ.log(String.format(Locale.US, "%d: %6.2f + %6.2fi", i, gr, Math.abs(gi)));
				}
				else {
					IJ.log(String.format(Locale.US, "%d: %6.2f - %6.2fi", i, gr, Math.abs(gi)));
				}
			}
		}
	}


	//------------ TESTING only ------------------------------

	public static void main(String[] args) {
		Complex z1 = new Complex(0.3, 0.6);
		Complex z2 = new Complex(-1, 0.2);
		System.out.println("z1 = " + z1);
		System.out.println("z2 = " + z2);
		Complex z3 = z1.mult(z2);
		System.out.println("z3 = " + z3);
		Complex z4 = z2.mult(z1);
		System.out.println("z4 = " + z4);
	}

}
