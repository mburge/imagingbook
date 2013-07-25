/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.geometry.interpolators;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.geometry.interpolators.PixelInterpolator.Method;


import java.awt.geom.Point2D;

public abstract class ImageInterpolator {
	
	public static Method defaultInterpolationMethod = Method.Bicubic; 
	
	protected ImageProcessor ip;
	
	public ImageProcessor getIp() {
		return ip;
	}
	
	protected ImageInterpolator(ImageProcessor ip) {
		this.ip = ip;
	}
	
	/**
	 * Factory method: creates an image interpolator for the specified
	 * image "ip" using the default interpolation method.
	 */
	public static ImageInterpolator create(ImageProcessor ip) {
		return create(ip, defaultInterpolationMethod);
	}
	
	/*
	 * Factory method: creates an image interpolator for the specified
	 * image "ip" and interpolation method "method".
	 */
	public static ImageInterpolator create(ImageProcessor ip, Method method) {
		if (ip instanceof ColorProcessor)
			return new ImageInterpolator.Rgb(ip, method);
		else // ip can be any gray image
			return new ImageInterpolator.Gray(ip, method);
	}
	
	// The only method you will want to use (implemented by subclasses).
	public abstract int getInterpolatedPixel(Point2D pnt);
	
	// Real subclass "ImageInterpolator.Gray" for interpolating SCALAR images (byte, short, float)
	public static class Gray extends ImageInterpolator {
		PixelInterpolator sInterpolator;
		
		// Constructor is private (use static factory methods above)
		private Gray(ImageProcessor ip, Method type) {
			super(ip);
			sInterpolator = PixelInterpolator.create(ip, type);
		}

		public int getInterpolatedPixel(Point2D pnt) {
			return sInterpolator.getInterpolatedPixel(pnt);
		}	
	}
	
	// Real subclass "ImageInterpolator.Rgb" for interpolating RGB color images
	public static class Rgb extends ImageInterpolator {
		// interpolators for red, green, blue channels:
		PixelInterpolator rInterpolator, gInterpolator, bInterpolator; 

		// Constructor is private (use static factory methods above)
		private Rgb(ImageProcessor ip, Method type) {
			super(ip);
			ColorProcessor cp = (ColorProcessor) ip;
			int w = cp.getWidth();
			int h = cp.getHeight();
			// retrieve each color plane as a ByteProcessor
			ByteProcessor rp = new ByteProcessor(w, h);
			ByteProcessor gp = new ByteProcessor(w, h);
			ByteProcessor bp = new ByteProcessor(w, h);
			byte[] rpix = (byte[]) rp.getPixels();
			byte[] gpix = (byte[]) gp.getPixels();
			byte[] bpix = (byte[]) bp.getPixels();
			cp.getRGB(rpix, gpix, bpix);
			// create one interpolator for each color plane
			rInterpolator = PixelInterpolator.create(rp, type);
			gInterpolator = PixelInterpolator.create(gp, type);
			bInterpolator = PixelInterpolator.create(bp, type);
		}

		public int getInterpolatedPixel(Point2D pnt) {
			int red = clamp(rInterpolator.getInterpolatedPixel(pnt));
			int grn = clamp(gInterpolator.getInterpolatedPixel(pnt));
			int blu = clamp(bInterpolator.getInterpolatedPixel(pnt));
			int rgb = ((red & 0xff) << 16) | ((grn & 0xff) << 8) | blu & 0xff;
			return rgb;
		}
		
		private final int clamp(int val) {
			if (val < 0) return 0;
			if (val > 255) return 255;
			return val;
		}
	}

}
