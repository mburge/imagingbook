package Ch16_GeomOperationen;
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
import imagingbook.pub.geometry.mappings.nonlinear.RippleMapping;

public class Transform_Ripple implements PlugInFilter {

    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
		double xWavel = 120;
		double xAmpl = 10;
		double yWavel = 250;
		double yAmpl = 10;

		RippleMapping map = RippleMapping.makeInverseMapping(xWavel,xAmpl,yWavel,yAmpl);
		
		map.applyTo(ip, PixelInterpolator.Method.Bicubic);
    }

}
