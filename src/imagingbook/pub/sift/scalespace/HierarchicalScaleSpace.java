/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.sift.scalespace;

import ij.IJ;

public abstract class HierarchicalScaleSpace {
	
	final int P;	// number of octaves
	final int Q; 	// number of levels per octave
	final double sigma_s;	// absolute scale of original image
	final double sigma_0;	// absolute base scale of first octave (level 0,0)
	final int botLevel, topLevel;  // bottom and top level index in each octave
	final ScaleOctave[] octaves;

	HierarchicalScaleSpace(int P, int Q, double sigma_s, double sigma_0, int botLevel, int topLevel) {
		this.Q = Q;
		this.P = P;
		this.sigma_s = sigma_s;
		this.sigma_0 = sigma_0;
		this.botLevel = botLevel;
		this.topLevel = topLevel;
		octaves = new ScaleOctave[P];	
	}
	
	public int getP() {
		return P;
	}
	
	public int getQ() {
		return Q;
	}
	
	public double getSigma_s() {
		return sigma_s;
	}
	
	public double getSigma_0() {
		return sigma_0;
	}
	
	public ScaleOctave getOctave(int p) {
		return octaves[p];
	}
	
	public ScaleLevel getScaleLevel(int p, int q) {
		return getOctave(p).getLevel(q);
	}
	
//	public float getValue(int p, int q, int u, int v) {
//		ScaleLevel level = getLevel(p,q);
//		return level.getf(u, v);
//	}
	
	public int getScaleIndex(int p, int q) {
		int m = Q * p + q; 
		return m;
	}
	
	public float getScaleIndexFloat(float p, float q) {
		float m = Q * p + q; 
		return m;
	}
	
	public double getAbsoluteScale(int p, float q) {
		double m = Q * p + q;
		return sigma_0 * Math.pow(2, m/Q);
	}
	
	public double getRelativeScale(double scaleA, double scaleB) {	// scaleA <= scaleB
		return Math.sqrt(scaleB*scaleB - scaleA*scaleA);
	}
	
	public double getRealX(int p, double xp) {
		return Math.pow(2, p) * xp;	// optimize (precalculate Math.pow(p, 2))
	}
	
	public double getRealY(int p, double yp) {
		return Math.pow(2, p) * yp;
	}
	
	public void show() {
		show("");
	}
	
	public void show(String title) {
		if (!title.isEmpty()) {
			title = title + ": ";
		}
		for (int p = 0; p < P; p++) {
			octaves[p].showAsStack(title + "Octave " + p);
		}
	}
	
	public void print() {
		IJ.log("Hierarchical Scale Space (" + this.getClass().toString() + ")");
		for (ScaleOctave oct : octaves) {
			oct.print();
		}
		
	}
}
