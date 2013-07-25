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

public class TwirlMapping extends Mapping {
	double xc, yc, angle, rad;
   
	public TwirlMapping (double xc, double yc, double angle, double rad, boolean inv) {
		this.xc = xc;
		this.yc = yc;
		this.angle = angle;
		this.rad = rad;
		this.isInverse = inv;
	}

	public static TwirlMapping makeInverseMapping(double xc, double yc, double angle, double rad){
		return new TwirlMapping(xc, yc, angle, rad, true);
	}

	public double[] applyTo (double[] xy){
		double x = xy[0];
		double y = xy[1];
		double dx = x - xc;
		double dy = y - yc;
		double d = Math.sqrt(dx*dx + dy*dy);
		if (d < rad) {
			double a = Math.atan2(dy,dx) + angle * (rad-d) / rad;
			double x1 = xc + d * Math.cos(a);
			double y1 = yc + d * Math.sin(a);
			//pnt.setLocation(x1, y1);
			return new double[] {x1, y1};
		}
		return xy.clone();
	}
}




