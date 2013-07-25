/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.geometry.mappings.nonlinear;

import imagingbook.pub.geometry.mappings.Mapping;

public class RippleMapping extends Mapping {
	double xWavel = 20;
	double yWavel = 100;
	double xAmpl = 0;
	double yAmpl = 10;
   
	public RippleMapping (
			double xWavel, double xAmpl, 
			double yWavel, double yAmpl, 
			boolean inv) {
		this.xWavel = xWavel / (2 * Math.PI);
		this.yWavel = yWavel / (2 * Math.PI);
		this.xAmpl = xAmpl;
		this.yAmpl = yAmpl;
		this.isInverse = inv;
	}
	
	public static RippleMapping makeInverseMapping(
			double xWavel, double xAmpl, double yWavel, double yAmpl){
		return new RippleMapping(xWavel, xAmpl, yWavel, yAmpl, true);
	}

	public double[] applyTo (double[] xy){
		double x0 = xy[0];
		double y0 = xy[1];	
		double x1 = x0 + xAmpl * Math.sin(y0 / xWavel);
		double y1 = y0 + yAmpl * Math.sin(x0 / yWavel);
		//pnt.setLocation(x1, y1);
		return new double[] {x1, y1};
	}
}




