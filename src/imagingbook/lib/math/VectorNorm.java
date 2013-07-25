/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.math;

public abstract class VectorNorm {
	
	public abstract double magnitude(double[] x);
	public abstract double magnitude(int[] x);
	
	/**
	 * @return the distance between vectors a and b
	 */
	public abstract double distance(double[] a, double[] b);
	public abstract double distance(int[] a, int[] b);
	
	/**
	 * @return the squared distance between vectors a and b
	 */
	public abstract double distance2(double[] a, double[] b);
	public abstract double distance2(int[] a, int[] b);
	
	/**
	 * Returns a factor to scale magnitude and distance values 
	 * to the range of the vector components of dimensionality
	 * n. This is prim. used for scaling color distances (n = 3).
	 * E.g., if components are distributed in [0,255], the distances
	 * multiplied by this factor should again be in [0,255].
	 */
	public abstract double getScale(int n);
	
	private interface Creator {
		public VectorNorm create();
	}
	
	public enum NormType implements Creator {
		L1 	 {public VectorNorm create() {return new VectorNorm.L1();}},
		L2 	 {public VectorNorm create() {return new VectorNorm.L2();}}, 
		Linf {public VectorNorm create() {return new VectorNorm.Linf();}
		};
	}

	static String wrongLengthMessage = "feature vectors must be of same length";

	// ------------------------------------------------------------------------------
	
	public static class L1 extends VectorNorm {

		public double magnitude(double[] X) {
			double sum = 0.0;
			for (int i = 0; i < X.length; i++) {
				sum = sum + Math.abs(X[i]);
			}
			return sum;
		}

		public double magnitude(int[] X) {
			long sum = 0;
			for (int i = 0; i < X.length; i++) {
				sum = sum + Math.abs(X[i]);
			}
			return (double) sum;
		}
		
		public double distance(final double[] X, final double[] Y) {
			if (X.length != Y.length) {
				throw new IllegalArgumentException(wrongLengthMessage);
			}
			double sum = 0.0;
			for (int i = 0; i < X.length; i++) {
				double d = X[i] - Y[i];
				sum = sum + Math.abs(d);
			}
			return sum;
		}

		public double distance(final int[] X, final int[] Y) {
			if (X.length != Y.length) {
				throw new IllegalArgumentException(wrongLengthMessage);
			}
			int sum = 0;
			for (int i = 0; i < X.length; i++) {
				sum = sum + Math.abs(X[i] - Y[i]);
			}
			return sum;
		}

		public double distance2(double[] a, double[] b) {
			double d = distance(a, b);
			return d * d;
		}

		public double distance2(int[] a, int[] b) {
			double d = distance(a, b);
			return d * d;
		}

		public double getScale(int n) {
			return 1.0 / n;
		}
	}

	// ------------------------------------------------------------------------------
	
	public static class L2 extends VectorNorm {

		public double magnitude(double[] X) {
			double sum = 0.0;
			for (int i = 0; i < X.length; i++) {
				sum = sum + X[i] * X[i];
			}
			return Math.sqrt(sum);
		}

		public double magnitude(int[] X) {
			long sum = 0;
			for (int i = 0; i < X.length; i++) {
				sum = sum + X[i] * X[i];
			}
			return Math.sqrt(sum);
		}
		
		public double distance(double[] a, double[] b) {
			return Math.sqrt(distance2(a, b));
		}
		
		public double distance2(final double[] X, final double[] Y) {
			if (X.length != Y.length) {
				throw new IllegalArgumentException(wrongLengthMessage);
			}
			double sum = 0.0;
			for (int i = 0; i < X.length; i++) {
				double d = X[i] - Y[i];
				sum = sum + d * d;
			}
			return sum;
		}
		
		public double distance(int[] a, int[] b) {
			return Math.sqrt(distance2(a, b));
		}

		public double distance2(final int[] X, final int[] Y) {
			if (X.length != Y.length) {
				throw new IllegalArgumentException(wrongLengthMessage);
			}
			int sum = 0;
			for (int i = 0; i < X.length; i++) {
				int d = X[i] - Y[i];
				sum = sum + d * d;
			}
			return sum;
		}

		public double getScale(int n) {
			return Math.sqrt(1.0 / n);
		}
	}

	// ------------------------------------------------------------------------------
	
	public static class Linf extends VectorNorm {

		public double magnitude(double[] X) {
			double dmax = 0.0;
			for (int i = 0; i < X.length; i++) {
				dmax = Math.max(dmax, Math.abs(X[i]));
			}
			return dmax;
		}

		public double magnitude(int[] X) {
			int dmax = 0;
			for (int i = 0; i < X.length; i++) {
				dmax = Math.max(dmax, Math.abs(X[i]));
			}
			return dmax;
		}

		public double distance(final double[] X, final double[] Y) {
			if (X.length != Y.length) {
				throw new IllegalArgumentException(wrongLengthMessage);
			}
			double dmax = 0.0;
			for (int i = 0; i < X.length; i++) {
				double d = X[i] - Y[i];
				dmax = Math.max(dmax, Math.abs(d));
			}
			return dmax;
		}

		public double distance(final int[] X, final int[] Y) {
			if (X.length != Y.length) {
				throw new IllegalArgumentException(wrongLengthMessage);
			}
			int dmax = 0;
			for (int i = 0; i < X.length; i++) {
				int d = Math.abs(X[i] - Y[i]);
				dmax = Math.max(dmax, d);
			}
			return dmax;
		}

		public double distance2(double[] a, double[] b) {
			double d = distance(a, b);
			return d * d;
		}

		public double distance2(int[] a, int[] b) {
			double d = distance(a, b);
			return d * d;
		}

		public double getScale(int n) {
			return 1.0;
		}
	}

}
