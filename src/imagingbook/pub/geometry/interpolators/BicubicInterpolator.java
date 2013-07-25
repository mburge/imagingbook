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

public class BicubicInterpolator extends PixelInterpolator {
	
	double a = 0.5;	// equiv. to Catmull-Rom interpolator
	
	public BicubicInterpolator(ImageProcessor ip) {
		super(ip);
	}
	
	public BicubicInterpolator(ImageProcessor ip, double a) {
		super(ip);
		this.a = a;
	}

	private final double cubic(double x) {
		if (x < 0)
			x = -x;
		double z = 0;
		if (x < 1)
			z = (-a + 2) * x * x * x + (a - 3) * x * x + 1;
		else if (x < 2)
			z = -a * x * x * x + 5 * a * x * x - 8 * a * x + 4 * a;
		return z;
	}
	
	public int getInterpolatedPixel(Point2D pnt) {
		double x0 = pnt.getX();
		double y0 = pnt.getY();
		int u0 = (int) Math.floor(x0);	//use floor to handle negative coordinates too
		int v0 = (int) Math.floor(y0);

		double q = 0;
		for (int j = 0; j <= 3; j++) {
			int v = v0 - 1 + j;
			double p = 0;
			for (int i = 0; i <= 3; i++) {
				int u = u0 - 1 + i;
				float pixval = ip.getPixelValue(u, v);
				p = p + pixval * cubic(x0 - u);
			}
			q = q + p * cubic(y0 - v);
		}
		return toIntResult(q);
	}

}
