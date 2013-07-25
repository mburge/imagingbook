/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.geometry.mappings.linear;


public class Rotation extends AffineMapping {
	
	public Rotation(double alpha) {
		super(
			 Math.cos(alpha), -Math.sin(alpha), 0,
			 Math.sin(alpha),  Math.cos(alpha), 0, false);
	}
}



