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

public class LanczosInterpolator extends PixelInterpolator {
	int order = 2;	// order (tap count) of this interpolator
	
	public LanczosInterpolator(ImageProcessor ip) {
		super(ip);
	}
	
	public LanczosInterpolator(ImageProcessor ip, int order) {
		super(ip);
		// order >= 2
		this.order = order;
	}
	
	public int getInterpolatedPixel(Point2D pnt) {
		double x0 = pnt.getX();
		double y0 = pnt.getY();
		int u0 = (int) Math.floor(x0);	//use floor to handle negative coordinates too
		int v0 = (int) Math.floor(y0);

		double q = 0;
		for (int j = 0; j <= 2*order-1; j++) {
			int v = v0 + j - order + 1;
			double p = 0;
			for (int i = 0; i <= 2*order-1; i++) {
				int u = u0 + i - order + 1;
				float pixval = ip.getPixelValue(u, v);
				p = p + pixval * w_Ln(x0 - u);
			}
			q = q + p * w_Ln(y0 - v);
		}
		return toIntResult(q);
	}
	
	static final double pi = Math.PI;
	static final double pi2 = pi*pi;
	
	double w_Ln(double x) { // 1D Lanczos interpolator of order n
		x = Math.abs(x);
		if (x < 0.001) return 1.0;
		if (x < order) {
			return order * (Math.sin(pi*x/order) * Math.sin(pi*x)) / (pi2*x*x);
		}
		else return 0.0;
	}	
	
}
