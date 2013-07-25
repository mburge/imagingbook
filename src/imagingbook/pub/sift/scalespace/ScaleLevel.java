/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.sift.scalespace;

import ij.process.FloatProcessor;
import imagingbook.pub.sift.filters.GaussianFilter;

/**
 * Represents a single scale level. Just a special kind of FloatProcessor
 * with some extra fields and methods.
 */
public class ScaleLevel extends FloatProcessor {	// TODO: make IJ independent, use only float arrays
	
	private double absoluteScale;	// really needed?
	
	// ------------------------------
	
	public ScaleLevel(int width, int height, float[] data, double absoluteScale) {
		super(width, height, data, null);	// constructor of FloatProcessor
		this.absoluteScale = absoluteScale;
	}
	
	public ScaleLevel(FloatProcessor fp, double absoluteScale) {
		this(fp.getWidth(), fp.getHeight(), ((float[]) fp.getPixels()).clone(), absoluteScale);
	}
	
	public ScaleLevel(ScaleLevel level) {
		this(level.getWidth(), level.getHeight(), ((float[]) level.getPixels()).clone(), level.absoluteScale);
	}
	
	// ------------------------------

	public void filterGaussian(double sigma) {
		GaussianFilter gf = new GaussianFilter(sigma);
		gf.applyTo(this);
	}
	
	public ScaleLevel duplicate() {
		return new ScaleLevel(this);
	}

	public ScaleLevel decimate() {	// returns a 2:1 subsampled copy of this ScaleLevel
		//IJ.log(" reducing ...");
		int width1 = this.getWidth();
		int height1 = this.getHeight();
		int width2 = width1 / 2;
		int height2 = height1 / 2;
		
		float[] pixels1 = (float[]) this.getPixels();
		float[] pixels2 = new float[width2*height2];		
		for (int v2 = 0 ; v2 < height2; v2++) {
			int v1 = 2 * v2;
			for (int u2 = 0 ; u2 < width2; u2++) {
				int u1 = 2 * u2;
				pixels2[v2 * width2 + u2] = pixels1[v1 * width1 + u1];
			}
		}
		return new ScaleLevel(width2, height2, pixels2, absoluteScale);
	}
	
	public ScaleLevel subtract(FloatProcessor B) {
		// A <-- A-B
		ScaleLevel A = this.duplicate();
		float[] pixelsA = (float[]) A.getPixels();
		float[] pixelsB = (float[]) B.getPixels();
		for (int i=0; i<pixelsA.length; i++) {
			pixelsA[i] = pixelsA[i] - pixelsB[i];
		}
		A.setAbsoluteScale(0);
		return A;
	}
	
	public void setAbsoluteScale(double sigma) {
		this.absoluteScale = sigma;
	}
	
	public double getAbsoluteScale() {
		return absoluteScale;
	}
	
	/*
	 * Collects and returns the 3x3 neighborhood values at this scale level 
	 * at center position u,v. The result is stored in the given 3x3 array nh.
	 */
	public void get3x3Neighborhood(final int u, final int v, final float[][] nh) {
		for (int i = 0, x = u - 1; i < 3; i++, x++) {
			for (int j = 0, y = v - 1; j < 3; j++, y++) {
				nh[i][j] = this.getf(x, y);
			}
		}
	}
	
	public void getGradientPolar(int u, int v, double[] grad) {
		final double grad_x = this.getf(u+1, v) - this.getf(u-1, v);	// x-component of local gradient
		final double grad_y = this.getf(u, v+1) - this.getf(u, v-1);	// y-component of local gradient
		grad[0] = Math.sqrt(grad_x * grad_x + grad_y * grad_y);		// local gradient magnitude (E)
		grad[1] = Math.atan2(grad_y, grad_x);						// local gradient direction (phi)
	}
}
