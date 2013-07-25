/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.noise.perlin;

import imagingbook.lib.math.Matrix;
import imagingbook.pub.noise.hashing.HashFun;

/**
 * Gradient (Perlin) noise implementation.
 * This class implements an N-dimensional Perlin noise generator.
 */
public class PerlinNoiseGenNd extends PerlinNoiseGen {
	
	final int N;		// dimensionality, default 1
	final int K;			// number of hypercube vertices, default 2
	final int[][] Q;			// vertex coordinates of the unit hypercube
	
	public PerlinNoiseGenNd(int N, double f_min, double f_max, double persistence, HashFun hf) {
		super(f_min, f_max, persistence, hf);
		this.N = N;
		this.K = (int) Math.pow(2, N);	// number of hypercube vertices
		this.Q = new int[K][N];			// vertices of the unit hypercube
		for (int j = 0; j < K; j++) {
			Q[j] = vertex(j, N);
		}
	}
	
	/**
	 * N-dim combined (multi-frequency) Perlin noise function. 
	 * @param X Interpolation position X (N-dimensional).
	 * @return The value of the combined Perlin
	 * noise function for the N-dimensional position X.
	 */
	public double NOISE(double[] X) {
		double sum = 0;
		for (int i = 0; i < F.length; i++) { // for all frequencies
			sum = sum + A[i] * noise(Matrix.multiply(X, F[i]));
		}
		return sum;
	}
	
	/**
	 * 2D elementary (single-frequency) Perlin noise function. 
	 * @param X Interpolation position X (N-dimensional).
	 * @return The value of the elementary Perlin
	 * noise function for the N-dimensional position X.
	 */
	public double noise(double[] X) {
		int[] P0 = Matrix.floor(X);		// origin of hypercube around X
		 
		// get the 2^N gradient vectors for all hypercube corners:
		double[][] G = new double[K][N];
		for(int j=0; j<K; j++) { 	
			G[j] = gradient(Matrix.add(P0,Q[j])); 			// gradient vector at cube corner j
		}
		
		double[] X01 = Matrix.sub(X,P0);					// X01[k] are in [0,1]
		
		// get the 2^N gradient values at all vertices for position X
		double[] W = new double[K];
		for (int j = 0; j < K; j++) { 	
			W[j] = Matrix.dotProduct(G[j], Matrix.sub(X01, Q[j]));
		}
		
		return interpolate(X01, W, 0);
	}
	
	/**
	 * @param p discrete position.
	 * @return A pseudo-random gradient vector for 
	 * the discrete lattice point p (N-dimensional).
	 */
	double[] gradient(int[] p) {	
		if (p.length == 2) {
			return gradient(p[0],p[1]);
		}
		// hash() always returns a new double[], g[i] in [0,1]
		double[] g = hashFun.hash(p);	// STILL TO BE DONE!!!
		for (int i=0; i<g.length; i++) {
			g[i] = 2.0 * g[i] - 1;
		}
		return g;
	}
	
	/**
	 * Local interpolation function.
	 * @param X01 Interpolation position in [0,1]^N
	 * @param WW  A vector of length 2^(N-d) with
	 * the tangent values for the hypercube corners.
	 * @param k The interpolation dimension (axis).
	 * @return  The interpolated noise value at position X01.
	 */
	double interpolate(double[] X01, double[] WW, int k) { 
		if (WW.length == 1) {			// (d == N)
			return WW[0];				// done, end of recursion
		}
		else {							// d < N
			double x01 = X01[k];		// select dimension d of vector X
			double s = this.s(x01);			// blending function
			int M = WW.length/2;
			double[] W = new double[M];		// W is half the length of WW
			for (int i=0; i<M; i++) {
				double wa = WW[2*i];
				double wb = WW[2*i+1];
				W[i] = (1-s)*wa + s*wb;		// the actual interpolation
			}
			return interpolate(X01, W, k+1);
		}
	}

	/**
	 * @param j Vertex number (0..2^N-1)
	 * @param N Dimension of the hypercube
	 * @return The coordinate vector for vertex j of the N-dimensional
	 * hypercube.
	 */
	private int[] vertex(int j, int N) { 
		int[] v = new int[N];
		// copy the bit representation of j into v,
		// v[0] is the most significant bit 
		for (int k = 0; k < N; k++) {
			 v[k] = j & 0x00000001;		// select least significant bit (bit 0)
			 j = j >>> 1;				// j <- j/2
		}
		return v;
	}
	


	
	// from 2D example
	double[] gradient(int i, int j) {
		double[] g = hashFun.hash(i,j);		// hash() always returns a new double[]
		g[0] = 2.0 * g[0] - 1;
		g[1] = 2.0 * g[1] - 1;
		return g;
	}

//	private int Power2(int k) {
//		return 1 << k;
//	}

}
