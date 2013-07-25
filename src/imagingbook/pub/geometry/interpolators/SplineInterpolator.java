/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.geometry.interpolators;

import ij.process.ImageProcessor;
import java.awt.geom.Point2D;

public class SplineInterpolator extends PixelInterpolator {
	double a = 0.5;	// default is a Catmull-Rom spline
	double b = 0.0;

	
	public SplineInterpolator(ImageProcessor ip) {
		super(ip);
	}
	
	public SplineInterpolator(ImageProcessor ip, double a, double b) {
		super(ip);
		this.a = a;
		this.b = b;
	}
	
	public int getInterpolatedPixel(Point2D pnt) {
		double x0 = pnt.getX();
		double y0 = pnt.getY();
		int u0 = (int) Math.floor(x0);	//use floor to handle negative coordinates too
		int v0 = (int) Math.floor(y0);

		double q = 0;
		for (int j = 0; j <= 3; j++) {
			int v = v0 + j - 1;
			double p = 0;
			for (int i = 0; i <= 3; i++) {
				int u = u0 + i - 1;
				float pixval = ip.getPixelValue(u, v);
				p = p + pixval * w_cs(x0 - u);
			}
			q = q + p * w_cs(y0 - v);
		}
		return toIntResult(q);
	}
	
	double w_cs(double x) {
		if (x < 0) x = -x;
		double w = 0;
		if (x < 1) 
			w = (-6*a - 9*b + 12) * x*x*x + (6*a + 12*b - 18) * x*x - 2*b + 6;
		else if (x < 2) 
			w = (-6*a - b) * x*x*x + (30*a + 6*b) * x*x + (-48*a - 12*b) * x + 24*a + 8*b;
		return w/6;
	}	
}
