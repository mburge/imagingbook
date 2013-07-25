/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.geometry.mappings;

import ij.process.ImageProcessor;
import imagingbook.pub.geometry.interpolators.ImageInterpolator;
import imagingbook.pub.geometry.interpolators.PixelInterpolator.Method;

import java.awt.Point;
import java.awt.geom.Point2D;

/*
 * 2013-02-02: changed to use the returned new point of applyTo(Point2D),
 * not relying on side effect.
 * 
 */

public abstract class Mapping implements Cloneable {
	protected boolean isInverse = false;

	// all subclasses must implement this method
	public abstract double[] applyTo(double[] xy);

	public Mapping getInverse() {
		if (isInverse)
			return this;
		else {
			return this.invert(); // only linear mappings invert
		}
	}
	
	protected Mapping invert() {
		throw new UnsupportedOperationException("mapping cannot be inverted");
	}
	
	public Point2D applyTo(Point2D pnt) {
		double[] xy = applyTo(new double[] {pnt.getX(), pnt.getY()});
		return new Point2D.Double(xy[0], xy[1]);
	}

	/**
	 * Destructively transforms the image in "target" using this geometric
	 * mapping and the specified pixel interpolation method.
	 */
	public void applyTo(ImageProcessor target, Method interpolMethod) {
		// make a temporary copy of the image:
		ImageProcessor source = target.duplicate();
		applyTo(source, target, interpolMethod);
		source = null;
	}

	/**
	 * Transforms the "source" image to the "target" image using this geometric
	 * mapping and the specified pixel interpolation method. Source and target
	 * must be different images!
	 */
	public void applyTo(ImageProcessor source, ImageProcessor target, Method interpolMethod) {
		if (target == source) {
			throw new IllegalArgumentException("source and target image must not be the same");
		}
		ImageInterpolator intrPltr = ImageInterpolator.create(source, interpolMethod);
		applyTo(target, intrPltr);
		intrPltr = null;
	}

	/**
	 * Transforms the source image (contained in "srcInterpol") to the "target"
	 * image using this geometric mapping and the specified pixel interpolator.
	 */
	public void applyTo(ImageProcessor target, ImageInterpolator srcInterpol) {
		if (target == srcInterpol.getIp()) {
			throw new IllegalArgumentException("source and target image must not be the same");
		}
		Mapping invMap = this.getInverse(); // get inverse mapping
		int w = target.getWidth();
		int h = target.getHeight();
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				Point2D sourcePt = invMap.applyTo(new Point(u, v));
				int val = srcInterpol.getInterpolatedPixel(sourcePt);
				target.putPixel(u, v, val);
			}
		}
	}

	public Mapping duplicate() { // duplicates any mapping, overwrite
		return this.clone();
	}
	
	protected Mapping clone() {
		Mapping copy = null;
		try {
			copy = (Mapping) super.clone();
		} 
		catch (CloneNotSupportedException e) { }
		return copy;
	}

}
