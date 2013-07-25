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
import imagingbook.pub.geometry.mappings.nonlinear.SphereMapping;

public class Transform_Spherical implements PlugInFilter {

    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
    	int w = ip.getWidth();
    	int h = ip.getHeight();
		
		SphereMapping map = 
				SphereMapping.makeInverseMapping(w / 2 + 10, h / 2, h / 2);
		
		map.applyTo(ip, PixelInterpolator.Method.Bicubic);
    }

}
