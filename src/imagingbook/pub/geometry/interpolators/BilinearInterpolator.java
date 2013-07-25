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

public class BilinearInterpolator extends PixelInterpolator {
	
	public BilinearInterpolator(ImageProcessor ip) {
		super(ip);
	}

	public int getInterpolatedPixel(Point2D pnt) {
		double x = pnt.getX();
		double y = pnt.getY();
		int u = (int) Math.floor(x);
		int v = (int) Math.floor(y);
		double a = x-u;
		double b = y-v;
		double A = ip.getPixelValue(u, v);
		double B = ip.getPixelValue(u+1, v);
		double C = ip.getPixelValue(u, v+1);
		double D = ip.getPixelValue(u+1, v+1);
		double E = A + a*(B-A);
		double F = C + a*(D-C);
		double G = E + b*(F-E);
		return toIntResult(G);
	}
	
}
