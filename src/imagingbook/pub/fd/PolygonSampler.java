/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.fd;

import java.awt.geom.Point2D;

public class PolygonSampler {
	
	public PolygonSampler() {
	}
	
	/**
	 * Samples the closed polygon path specified by V at M
	 * equi-distant positions. 
	 * @param V the vertices of the (closed) polygon.
	 * @param M the number of sample points.
	 * @return the sample points as an array of Point2D objects.
	 */
	public Point2D[] samplePolygonUniformly(Point2D[] V, int M) {
		int N = V.length;
		double Delta = pathLength(V) / M;	// constant segment length in Q
		// distribute N points along polygon path P
		Point2D[] S = new Point2D[M];
		S[0] = (Point2D) V[0].clone();	// q_0 = p_0 (duplicate p_0)
		int i = 0;			// lower index of segment (i,i+1) in P
		int j = 1;			// index of next point to be added to Q
		double alpha = 0;	// lower boundary of current path segment in P
		double beta = Delta;	// path position of next point to be added to Q
		// for all M segments in P do:
		while (i < N && j < M) {
			Point2D vA = V[i];
			Point2D vB = V[(i + 1) % N];
			double delta = vA.distance(vB);
			// handle segment (i,i+1) with path boundaries (a,a+d), knowing a < b
			while (beta <= alpha + delta && j < M) {
				// a < b <= a+d
				S[j] = interpolate(vA, vB, (beta - alpha) / delta);
				j = j + 1;
				beta = beta + Delta;
			}
			alpha = alpha + delta;
			i = i + 1;
		}	
		return S;
	}
	
	/**
	 * This is for testing: allows to choose an arbitrary start point by
	 * setting 'startFrac' in [0,1].
	 * @param V the vertices of the (closed) polygon.
	 * @param M the number of sample points.
	 * @param startFrac the position of the first sample as a fraction of the 
	 * polggon's circumference in [0,1].
	 * @return the sample points as an array of Point2D objects.
	 */
	public Point2D[] uniformlySamplePolygon(Point2D[] V, int M, double startFrac) {
		int startPos = (int) Math.round(V.length * startFrac) % V.length;
		return samplePolygonUniformly(shiftLeft(V, startPos), M);
	}
	
	private Point2D[] shiftLeft(Point2D[] V, int startPos) {
		int polyLen = V.length;
		Point2D[] P2 = new Point2D[polyLen]; 
		for (int i = 0; i < P2.length; i++) {
			P2[i] = (Point2D) V[(i + startPos) % polyLen].clone();
		}
		return P2;
	}
	
	
	protected double pathLength(Point2D[] V) {
		double L = 0;
		final int N = V.length;
		for (int i = 0; i < N; i++) {
			Point2D p0 = V[i];
			Point2D p1 = V[(i + 1) % N];
			L = L + p0.distance(p1);
		}
		return L;
	}
	
	protected Point2D interpolate(Point2D p0, Point2D p1, double alpha) {
		// alpha is in [0,1]
		double x = p0.getX() + alpha * (p1.getX() - p0.getX());
		double y = p0.getY() + alpha * (p1.getY() - p0.getY());
		return new Point2D.Double(x, y);
	}
	
}
