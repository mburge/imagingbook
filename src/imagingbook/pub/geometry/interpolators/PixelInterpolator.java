/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.geometry.interpolators;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.geom.Point2D;

public abstract class PixelInterpolator {
	
	public enum Method {
		NearestNeighbor,
		Bilinear,
		Bicubic,
		BicubicSmooth,
		BicubicSharp,
		CatmullRom,
		CubicBSpline,
		MitchellNetravali,
		Lanzcos2,
		Lanzcos3,
		Lanzcos4;
		
//		public static String[] getNames() {
//			List<String> en = new LinkedList<String>();
//			for (Enum<?> m : Method.values()) {
//				en.add(m.name());
//			}
//			return en.toArray(new String[0]);
//		}
	}
	
	protected ImageProcessor ip;
	
	public static PixelInterpolator create(ImageProcessor ip, Method method) {
		if (ip instanceof ColorProcessor)
			throw new IllegalArgumentException("cannot create PixelInterpolator for ColorProcessor");
		switch (method) {
		case NearestNeighbor : 	return new NearestNeighborInterpolator(ip); 
		case Bilinear : 		return new BilinearInterpolator(ip);
		case Bicubic : 			return new BicubicInterpolator(ip, 1.00);
		case BicubicSmooth : 	return new BicubicInterpolator(ip, 0.25);
		case BicubicSharp : 	return new BicubicInterpolator(ip, 1.75);
		case CatmullRom: 		return new SplineInterpolator(ip, 0.5, 0.0);
		case CubicBSpline: 		return new SplineInterpolator(ip, 0.0, 1.0);
		case MitchellNetravali: return new SplineInterpolator(ip, 1.0/3, 1.0/3);
		case Lanzcos2 : 		return new LanczosInterpolator(ip, 2);
		case Lanzcos3 : 		return new LanczosInterpolator(ip, 3);
		case Lanzcos4 : 		return new LanczosInterpolator(ip, 4);
		default : throw new IllegalArgumentException("unhandled interpolator method: " + method.name());
		}
	}
	
	protected PixelInterpolator(ImageProcessor ip) {
		this.ip = ip;
	}
	
	public abstract int getInterpolatedPixel(Point2D pnt);
	
	protected int toIntResult(double result) {
		if (ip instanceof FloatProcessor)
			return Float.floatToIntBits((float)result);
		else
			return (int) Math.round(result);
	}
	
}
