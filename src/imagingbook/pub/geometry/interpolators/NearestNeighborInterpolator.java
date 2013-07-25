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


public class NearestNeighborInterpolator extends PixelInterpolator {
	
	public NearestNeighborInterpolator(ImageProcessor ip) {
		super(ip);
	}
	
	public int getInterpolatedPixel(Point2D pnt) {
		int u = (int) Math.rint(pnt.getX());
		int v = (int) Math.rint(pnt.getY());
		return ip.getPixel(u,v);
	}
	
}
