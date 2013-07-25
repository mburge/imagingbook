package Ch16_GeometricOps;
/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.geometry.interpolators.PixelInterpolator;
import imagingbook.pub.geometry.mappings.linear.Rotation;


public class Transform_Rotate implements PlugInFilter {
	static double angle = 15; // rotation angle (in degrees)

    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
    	
		Rotation map = new Rotation((2 * Math.PI * angle) / 360);
		map.applyTo(ip, PixelInterpolator.Method.Bicubic);
    }
}
