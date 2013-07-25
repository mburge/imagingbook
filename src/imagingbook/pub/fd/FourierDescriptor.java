/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.fd;

import static imagingbook.lib.math.Arithmetic.EPSILON_DOUBLE;
import static imagingbook.lib.math.Arithmetic.sqr;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import ij.gui.Roi;
import imagingbook.lib.math.Arithmetic;
import imagingbook.lib.math.Complex;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariateOptimizer;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;

/**
 * This is the abstract super-class for Fourier descriptors. It cannot
 * be instantiated.
 * @author W. Burger
 * @version 2013/05/30
 */
public abstract class FourierDescriptor implements Cloneable {
	
	static int minReconstructionSamples = 50;
	
	protected Complex[] g;	// complex-valued samples (used only for display purposes)
	protected Complex[] G;	// complex-valued DFT spectrum
	protected double reconstructionScale = 1.0;		// remembers original scale after normalization
	
	// ----------------------------------------------------------------
	
	public double getReconstructionScale() {
		return reconstructionScale;
	}

	/*
	 * Truncates this Fourier descriptor to P coefficients.
	 * Example:
	 * 0 1 2 3 4 5 6 7 8 9
	 * a b c d e f g h i j
	 * becomes (with Mp = 3)
	 * 0 1 2 3 4 5 6
	 * a b c d h i j
	 */
	public void truncate(int P) {
		int M = G.length;
		if (P > 0 && P < M) {
			Complex[] Gnew = new Complex[P];
			for (int m = 0; m < P; m++) {
				if (m <= P/2)
					Gnew[m] = G[m];
				else
					Gnew[m] = G[M-P+m];
			}
			G = Gnew;
		}
	}
	
	public FourierDescriptor clone() {
		FourierDescriptor fd2 = null;
		try {
			fd2 = (FourierDescriptor) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		fd2.g = Complex.duplicate(this.g);
		fd2.G = Complex.duplicate(this.G);
		return fd2;
	}
	
	public int getMaxNegHarmonic() {
		return -(G.length - 1)/2;
	}
	
	public int getMaxPosHarmonic() {
		return G.length/2;
	}
	
	public int getMaxCoefficientPairs() {
		return (G.length - 1)/2;
	}
	
	// ----------------------------------------------------------------
	
	protected static Complex[] makeComplex(Point2D[] points) {
    	int N = points.length;
		Complex[] samples = new Complex[N];
		for (int i=0; i<N; i++) {
			samples[i] = new Complex(points[i]);
		}
		return samples;
	}
    
	// ------------------------------------------------------------------
	
	public Complex[] getSamples() {
		return g;
	}
	
	public Complex[] getCoefficients() {
		return G;
	}
	
	public int size() {
		return G.length;	// = M
	}
	
	public int getCoefficientIndex(int m) {
		return Arithmetic.mod(m, G.length);
	}
	
	public Complex getCoefficient(int m) {
		int mm = Arithmetic.mod(m, G.length);
		return new Complex(G[mm]);
	}
	
	public void setCoefficient(int m, Complex z) {
		int mm = Arithmetic.mod(m, G.length);
		G[mm] = new Complex(z);
	}
	
	public void setCoefficient(int m, double a, double b) {
		int mm = Arithmetic.mod(m, G.length);
		G[mm] = new Complex(a, b);
	}
	
	// ------------------------------------------------------------------
	
	/**
	 * Calculate a reconstruction from the full DFT spectrum with N samples.
	 */
	public Complex[] getReconstruction(int N) {
		Complex[] S = new Complex[N];
		for (int i = 0; i < N; i++) {
			double t = (double) i / N;
			S[i] = getReconstructionPoint(t);
		}
		return S;
	}
	
	/**
	 * Calculate a reconstruction from the partial DFT spectrum with N sample
	 * points and using Mp coefficient pairs.
	 */
	public Complex[] getReconstruction(int N, int Mp) {
		Complex[] S = new Complex[N];
		Mp = Math.min(Mp, -getMaxNegHarmonic());
		for (int i = 0; i < N; i++) {
			double t = (double) i / N;
			S[i] = getReconstructionPoint(t, -Mp, +Mp);
		}
		return S;
	}
	
	/**
	 * Reconstructs a single spatial point from the complete FD
	 * at the fractional path position t in [0,1].
	 */
	public Complex getReconstructionPoint(double t) {
		int mm = getMaxNegHarmonic();
		int mp = getMaxPosHarmonic();
		return getReconstructionPoint(t, mm, mp);
	}
	
	public Complex getReconstructionPoint(double t, int Mp) {
		return getReconstructionPoint(t, -Mp, Mp);
	}
	
	/**
	 * Reconstructs a single spatial point from this FD using
	 * coefficients [mm,...,mp] = [m-,...,m+] at the fractional path position t in [0,1].
	 */
	private Complex getReconstructionPoint(double t, int mm, int mp) {
		double x = G[0].re;
		double y = G[0].im;
		for (int m = mm; m <= mp; m++) {
			if (m != 0) {
				Complex Gm = getCoefficient(m);
				double A = reconstructionScale * Gm.re;
				double B = reconstructionScale * Gm.im;
				double phi = 2 * Math.PI * m * t;
				double sinPhi = Math.sin(phi);
				double cosPhi = Math.cos(phi);
				x = x + A * cosPhi - B * sinPhi;
				y = y + A * sinPhi + B * cosPhi;
			}
		}
		return new Complex(x, y);
	}
	
	// -----------------------------------------------------------------------
	
	public Path2D makeEllipse(Complex G1, Complex G2, int m, double xOffset, double yOffset) {
		Path2D path = new Path2D.Float();
		int recPoints = Math.max(minReconstructionSamples, G.length * 3);
		for (int i = 0; i < recPoints; i++) {
			double t = (double) i / recPoints;
			Complex p1 = this.getEllipsePoint(G1, G2, m, t);
			double xt = p1.re;
			double yt = p1.im;
			if (i == 0) {
				path.moveTo(xt + xOffset, yt + yOffset);
			}
			else {
				path.lineTo(xt + xOffset, yt + yOffset);
			}
		}
		path.closePath();
		return path;
	}
	
	/**
	 * Get the reconstructed point for two DFT coefficients G1, G2 at a given
	 * position t.
	 */
	public Complex getEllipsePoint(Complex G1, Complex G2, int m, double t) {
		Complex p1 = getReconstructionPoint(G1, -m, t);
		Complex p2 = getReconstructionPoint(G2, m, t);
		return p1.add(p2);
	}
	
	/**
	 * Returns the spatial point reconstructed from a single
	 * DFT coefficient 'Gm' with frequency 'm' at 
	 * position 't' in [0,1].
	 */
	private Complex getReconstructionPoint(Complex Gm, int m, double t) {
		double wm = 2 * Math.PI * m;
		double Am = Gm.re;
		double Bm = Gm.im;
		double cost = Math.cos(wm * t);
		double sint = Math.sin(wm * t);
		double xt = Am * cost - Bm * sint;
		double yt = Bm * cost + Am * sint;
		return new Complex(xt, yt);
	}
	
	/**
	 * Reconstructs the shape using all FD pairs.
	 */
	public Path2D makeFourierPairsReconstruction() {
		int M = G.length;
		return  makeFourierPairsReconstruction(M/2);
	}
	
	/**
	 * Reconstructs the shape obtained from FD-pairs 0,...,Mp as a polygon (path).
	 */
	public Path2D makeFourierPairsReconstruction(int Mp) {
		int M = G.length;
		Mp = Math.min(Mp, M/2);
		int recPoints = Math.max(minReconstructionSamples, G.length * 3);
		Path2D path = new Path2D.Float();
		for (int i = 0; i < recPoints; i++) {
			double t = (double) i / recPoints;
			Complex pt = new Complex(getCoefficient(0));	// assumes that coefficient 0 is never scaled
			// calculate a particular reconstruction point 
			for (int m = 1; m <= Mp; m++) {
				Complex ep = getEllipsePoint(getCoefficient(-m), getCoefficient(m), m, t);
				pt = pt.add(ep.mult(reconstructionScale));
			}
			double xt = pt.re; 
			double yt = pt.im; 
			if (i == 0) {
				path.moveTo(xt, yt);
			}
			else {
				path.lineTo(xt, yt);
			}
		}
		path.closePath();
		return path;
	}
	
	public int getMaxDftMagnitudeIndex() {
		double maxMag = -1;
		int maxIdx = -1;
		for (int i=0; i<G.length; i++) {
			double mag = G[i].abs();
			if (mag > maxMag) {
				maxMag = mag;
				maxIdx = i;
			}
		}
		return maxIdx;
	}
	
	public double getMaxDftMagnitude() {
		int maxIdx = getMaxDftMagnitudeIndex();
		return G[maxIdx].abs();
	}
	
	// Invariance -----------------------------------------------------
	
	public FourierDescriptor[] makeInvariant() {
		int Mp = getMaxCoefficientPairs();
		return makeInvariant(Mp);
	}
	
	public FourierDescriptor[] makeInvariant(int Mp) {
		makeScaleInvariant(Mp);
		FourierDescriptor[] fdAB = makeStartPointInvariant(Mp);	// = [fdA, fdB]
		fdAB[0].makeRotationInvariant(Mp);	// works destructively!
		fdAB[1].makeRotationInvariant(Mp);
		return fdAB;
	}
	
	public FourierDescriptor[] makeStartPointInvariant() {
		int Mp = getMaxCoefficientPairs();
		return makeStartPointInvariant(Mp);
	}
	
	private FourierDescriptor[] makeStartPointInvariant(int Mp) {
		double phiA = getStartPointPhase(Mp);
		double phiB = phiA + Math.PI;
		FourierDescriptor fdA = clone();
		FourierDescriptor fdB = clone();
		fdA.shiftStartPointPhase(phiA, Mp);
		fdB.shiftStartPointPhase(phiB, Mp);
		return new FourierDescriptor[] {fdA, fdB};
	}
	
	// -----------------------------------------------------------------
	
	/**
	 * Sets the zero (DC) coefficient to zero.
	 */
	public void makeTranslationInvariant() {
		G[0] = new Complex(0,0);
	}
	
	/**
	 * Normalizes this descriptor destructively to the L2 norm of G, 
	 * keeps G_0 untouched.
	 */
	public double makeScaleInvariant() {
		double s = 0;
		for (int m = 1; m < G.length; m++) {
			s = s + G[m].abs2();
		}
		// scale coefficients
		double norm = Math.sqrt(s);
		reconstructionScale = norm;		// keep for later reconstruction
		double scale = 1 / norm;
		for (int m = 1; m < G.length; m++) {
			G[m] =  G[m].mult(scale);
		}
		return scale;
	}
	
	/**
	 * Normalizes the L2 norm of the sub-vector (G_{-Mp}, ..., G_{Mp}),
	 * keeps G_0 untouched.
	 */
	private double makeScaleInvariant(int Mp) {
		double s = 0;
		for (int m = 1; m <= Mp; m++) {
			s = s + getCoefficient(-m).abs2() + getCoefficient(m).abs2();
		}
		// scale Fourier coefficients:
		double norm = Math.sqrt(s);
		reconstructionScale = norm;		// keep for later reconstruction
		double scale = 1 / norm;
		for (int m = 1; m <= Mp; m++) {
			setCoefficient(-m, getCoefficient(-m).mult(scale));
			setCoefficient( m, getCoefficient( m).mult(scale));
		}
		return scale;
	}
	
	/**
	 * Works destructively.
	 */
	public double makeRotationInvariant() {
		int Mp = getMaxCoefficientPairs();
		return makeRotationInvariant(Mp);
	}

	private double makeRotationInvariant(int Mp) {
		Complex z = new Complex(0,0);
		for (int m = 1; m <= Mp; m++) {
			Complex Gm = getCoefficient(-m);
			Complex Gp = getCoefficient(+m);
			double w = 1.0 / m;
			z = z.add(Gm.mult(w));
			z = z.add(Gp.mult(w));
		}
		double beta = z.arg();
		for (int m = 1; m <= Mp; m++) {
			setCoefficient(-m, getCoefficient(-m).rotate(-beta));
			setCoefficient( m, getCoefficient( m).rotate(-beta));
		}
		return beta;
	}
	
	/**
	 * For testing: apply shape rotation to this FourierDescriptor (phi in radians)
	 * @param phi
	 */
	public void rotate(double phi) {
		rotate(G, phi);
	}
	
	/**
	 * For testing: apply shape rotation to this FourierDescriptor (phi in radians)
	 * @param C
	 * @param phi
	 */
	private void rotate(Complex[] C, double phi) {
		Complex rot = new Complex(phi);
		for (int m = 1; m < G.length; m++) {
			C[m] = C[m].mult(rot);
		}
	}
	
	/**
	 * Apply a particular start-point phase shift
	 * @param phi
	 * @param Mp
	 */
	private void shiftStartPointPhase(double phi, int Mp) {
		Mp = Math.min(Mp, G.length/2);
		for (int m = -Mp; m <= Mp; m++) {
			if (m != 0) {
				setCoefficient(m, getCoefficient(m).rotate(m * phi));
			}
		}
	}

	
	/** 
	 * Calculates the 'canonical' start point. This version uses 
	 * (a) a coarse search for a global maximum of fp() and subsequently 
	 * (b) a numerical optimization using Brent's method
	 * (implemented with Apache Commons Math).
	 */
	public double getStartPointPhase(int Mp) {
		Mp = Math.min(Mp, (G.length-1)/2);
		UnivariateFunction fp =  new TargetFunction(Mp);
		// search for the global maximum in coarse steps
		double cmax = Double.NEGATIVE_INFINITY;
		int kmax = -1;
		int K = 25;	// number of steps over 180 degrees
		for (int k = 0; k < K; k++) {
			final double phi = Math.PI * k / K; 	// phase to evaluate
			final double c = fp.value(phi);
			if (c > cmax) {
				cmax = c;
				kmax = k;
			}
		}
		// optimize using previous and next point as the bracket.
		double minPhi = Math.PI * (kmax - 1) / K;
		double maxPhi = Math.PI * (kmax + 1) / K;	
		
		UnivariateOptimizer optimizer = new BrentOptimizer(1E-4, 1E-6);
		int maxIter = 20;
		UnivariatePointValuePair result = optimizer.optimize(
				new MaxEval(maxIter),
				new UnivariateObjectiveFunction(fp),
				GoalType.MAXIMIZE,
				new SearchInterval(minPhi, maxPhi)
				);
		double phi0 = result.getPoint();
		return phi0;	// the canonical start point phase
	}

	/**
	 * This inner class defines the target function for calculating the
	 * canonical start point phase. UnivariateFunction is defined in the
	 * Apache Common Maths framework.
	 */
	private class TargetFunction implements UnivariateFunction {
		final int Mp;
		
		TargetFunction(int Mp) {
			this.Mp = Mp;
		}
		
		/** 
		 * The value returned is the sum of the cross products of the FD pairs,
		 * with all coefficients rotated to the given start point phase phi.
		 * TODO: This could be made a lot more efficient!
		 */
		public double value(double phi) {
			double sum = 0;
			for (int m = 1; m <= Mp; m++) {
				Complex Gm = getCoefficient(-m).rotate(-m * phi);
				Complex Gp = getCoefficient( m).rotate( m * phi);
				sum = sum + Gp.crossProduct(Gm);
			}
			return sum;
		}
	}
	
	public double distanceComplex(FourierDescriptor fd2) {
		return distanceComplex(fd2, G.length/2);
	}
	
	public double distanceComplex(FourierDescriptor fd2, int Mp) {
		FourierDescriptor fd1 = this;
		Mp = Math.min(Mp, G.length/2);
		double sum = 0;
		for (int m = -Mp; m <= Mp; m++) {
			if (m != 0) {
				Complex G1m = fd1.getCoefficient(m);
				Complex G2m = fd2.getCoefficient(m);
				double dRe = G1m.re - G2m.re;
				double dIm = G1m.im - G2m.im;
				sum = sum + dRe * dRe + dIm * dIm;
			}
		}
		return Math.sqrt(sum);
	}
	
	public double distanceMagnitude(FourierDescriptor fd2) {
		int Mp = getMaxCoefficientPairs();
		return distanceMagnitude(fd2, Mp);
	}
	
	public double distanceMagnitude(FourierDescriptor fd2, int Mp) {
		FourierDescriptor fd1 = this;
		Mp = Math.min(Mp, G.length/2);
		double sum = 0;
		for (int m = -Mp; m <= Mp; m++) {
			if (m != 0) {
				double mag1 = fd1.getCoefficient(m).abs();
				double mag2 = fd2.getCoefficient(m).abs();
				double dmag = mag2 - mag1;
				sum = sum + (dmag * dmag);
			}
		}
		return Math.sqrt(sum);
	}
	
	// -----------------------------------------------------------------------------
	// Non-abstract inner classes --------------------------------------------------
	// -----------------------------------------------------------------------------
	
	/**
	 * Subclass of FourierDescriptor whose constructors assume
	 * that input polygons are uniformly sampled.
	 */
	public static class Uniform extends FourierDescriptor {
		/** 
		 * Create a Fourier descriptor from a uniformly sampled polygon V
		 * with the maximum number of Fourier coefficient pairs.
		 */
		public Uniform(Point2D[] V) {
			g = makeComplex(V);
			G = DFT(g);
		}
		
		/** 
		 * Create a Fourier descriptor from a uniformly sampled polygon V
		 * with Mp coefficient pairs.
		 */
		public Uniform(Point2D[] V, int Mp) {
			g = makeComplex(V);
			G = DFT(g, 2 * Mp + 1);
		}
		
		// -------------------------------------------------------------------
		
		/**
		 * DFT with the resulting spectrum (signal, if inverse) of the same length
		 * as the input vector g. Not using sin/cos tables.
		 */
		private Complex[] DFT(Complex[] g) {
			int M = g.length;
//			double[] cosTable = makeCosTable(M);	// cosTable[m] == cos(2*pi*m/M)
//			double[] sinTable = makeSinTable(M);
			Complex[] G = new Complex[M];
			double s = 1.0/M; //common scale factor (fwd/inverse differ!)
			for (int m = 0; m < M; m++) {
				double Am = 0;
				double Bm = 0;
				for (int k = 0; k < M; k++) {
					double x = g[k].re;
					double y = g[k].im;
					double phi = 2 * Math.PI * m * k / M;
					double cosPhi = Math.cos(phi);
					double sinPhi = Math.sin(phi);
					Am = Am + x * cosPhi + y * sinPhi;
					Bm = Bm - x * sinPhi + y * cosPhi;
				}
				G[m] = new Complex(s * Am, s * Bm);
			}
			return G;
		}
		
		/** 
		 * As above, but the length P of the resulting spectrum (signal, if inverse) 
		 * is explicitly specified.
		 */
		private Complex[] DFT(Complex[] g, int P) {
			int M = g.length;
//			double[] cosTable = makeCosTable(M);	// cosTable[m] == cos(2*pi*m/M)
//			double[] sinTable = makeSinTable(M);
			Complex[] G = new Complex[P];
			double s = 1.0/M; //common scale factor (fwd/inverse differ!)
			for (int m = P/2-P+1; m <= P/2; m++) {
				double Am = 0;
				double Bm = 0;
				for (int k = 0; k < M; k++) {
					double x = g[k].re;
					double y = g[k].im;
					//int mk = (m * k) % M; double phi = 2 * Math.PI * mk / M;
					double phi = 2 * Math.PI * m * k / M;	
					double cosPhi = Math.cos(phi);
					double sinPhi = Math.sin(phi);
					Am = Am + x * cosPhi + y * sinPhi;
					Bm = Bm - x * sinPhi + y * cosPhi;
				}
				G[Arithmetic.mod(m, P)] = new Complex(s * Am, s * Bm);
			}
			return G;
		}

//		private double[] makeCosTable(int M) {
//			double[] cosTab = new double[M];
//			for (int m = 0; m < M; m++) {
//				cosTab[m] = Math.cos(2 * Math.PI * m / M);
//			}
//			return cosTab;
//		}

//		private double[] makeSinTable(int M) {
//			double[] sinTab = new double[M];
//			for (int m = 0; m < M; m++) {
//				sinTab[m] = Math.sin(2 * Math.PI * m / M);
//			}
//			return sinTab;
//		}
		
	}
	
	/**
	 * Subclass of FourierDescriptor whose constructors assume
	 * that input polygons are non-uniformly sampled.
	 */
	public static class Nonuniform extends FourierDescriptor {
		
		/**
		 * V: sequences of 2D points describing an arbitrary, closed polygon.
		 * Mp: the number of Fourier coefficient pairs (M = 2 * Mp + 1)
		 */
		public Nonuniform(Point2D[] V, int Mp) {
			g = makeComplex(V);
			makeDftSpectrumTrigonometric(Mp);
		}
		
		/**
		 * roi: a region of interest (ImageJ), not necessarily a polyline.
		 * Mp:  the number of Fourier coefficient pairs (M = 2 * Mp + 1)
		 */
		public Nonuniform(Roi roi, int Mp) {
			this(getRoiPoints(roi), Mp);
		}
		
		void makeDftSpectrumTrigonometric(int Mp) {
			final int N = g.length;				// number of polygon vertices
			final int M = 2 * Mp + 1;			// number of Fourier coefficients
	        double[] dx = new double[N];		// dx[k] is the delta-x for polygon segment <k,k+1>
	        double[] dy = new double[N];		// dy[k] is the delta-y for polygon segment <k,k+1>
	        double[] lambda = new double[N];	// lambda[k] is the length of the polygon segment <k,k+1>
	        double[] L  = new double[N + 1]; 	// T[k] is the cumulated path length at polygon vertex k in [0,K]
	        
	        G = new Complex[M];
	        
	        L[0] = 0;
	        for (int i = 0; i < N; i++) {	// compute Dx, Dy, Dt and t tables
	            dx[i] = g[(i + 1) % N].re - g[i].re;
	            dy[i] = g[(i + 1) % N].im - g[i].im;
	            lambda[i] = sqrt(sqr(dx[i]) + sqr(dy[i])); 
	            if (abs(lambda[i]) < EPSILON_DOUBLE) {
	        		throw new Error("Zero-length polygon segment!");
	        	}
	            L[i+1] = L[i] + lambda[i];
	        }
	        
	        double Ln = L[N];	// Ln is the closed polygon length
	               
	        // calculate DFT coefficient G[0]:
	        double x0 = g[0].re; // V[0].getX();
	        double y0 = g[0].im; // V[0].getY();
	        double a0 = 0;
	        double c0 = 0;
	        for (int i = 0; i < N; i++) {	// for each polygon vertex
	        	double s = (sqr(L[i+1]) - sqr(L[i])) / (2 * lambda[i]) - L[i];
	        	double xi = g[i].re; // V[i].getX();
	        	double yi = g[i].im; // V[i].getY();
	        	a0 = a0 + s * dx[i] + (xi - x0) * lambda[i];
	        	c0 = c0 + s * dy[i] + (yi - y0) * lambda[i];
	        }
	        //G[0] = new Complex(x0 + a0/Ln, y0 + c0/Ln);
	        this.setCoefficient(0, new Complex(x0 + a0/Ln, y0 + c0/Ln));
	        
	        // calculate remaining FD pairs G[-m], G[+m] for m = 1,...,Mp
	        for (int m = 1; m <= Mp; m++) {	// for each FD pair
	        	double w = 2 * PI * m / Ln;
	        	double a = 0, c = 0;
	        	double b = 0, d = 0;
	            for (int i = 0; i < N; i++) {	//	for each polygon vertex
	            	double w0 = w * L[i];				
	            	double w1 = w * L[(i + 1) % N];		
	                double dCos = cos(w1) - cos(w0);
	                a = a + dCos * (dx[i] / lambda[i]);
	                c = c + dCos * (dy[i] / lambda[i]);
	                double dSin = sin(w1) - sin(w0);
	                b = b + dSin * (dx[i] / lambda[i]);
	                d = d + dSin * (dy[i] / lambda[i]);
	            }
	            double s = Ln / sqr(2 * PI * m);
	            this.setCoefficient(+m, new Complex(s * (a + d), s * (c - b)));
	            this.setCoefficient(-m, new Complex(s * (a - d), s * (b + c)));
	        }
		}
		
		static Point2D[] getRoiPoints(Roi roi) {
			Polygon poly = roi.getPolygon();
			int[] xp = poly.xpoints;
			int[] yp = poly.ypoints;
			// copy vertices for all non-zero-length polygon segments:
			List<Point> points = new ArrayList<Point>(xp.length);
			points.add(new Point(xp[0], yp[0]));
			int last = 0;
			for (int i = 1; i < xp.length; i++) {
				if (xp[last] != xp[i] || yp[last] != yp[i]) {
					points.add(new Point(xp[i], yp[i]));
					last = i;
				}
			}
			// remove last point if the closing segment has zero length:
			if (xp[last] == xp[0] && yp[last] == yp[0]) {
				points.remove(last);
			}
			return points.toArray(new Point2D[0]);
		}
	}
	

}
