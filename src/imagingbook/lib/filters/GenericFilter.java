/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.filters;

import ij.IJ;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.image.ImageAccessor;


public abstract class GenericFilter {
	
	// PASS THE IMAGE PROCESSOR of the original image and
	// set up width/height, accessors etc.
	// then use apply without processor argument??
	// Allow source/target to be of different types?
	// Implement using interfaces (for gray/color)?
	
	protected GenericFilter() {
	}
 	
 	public abstract float filterPixel(ImageAccessor.Gray source, int u, int v);
 	public abstract float[] filterPixel(ImageAccessor.Color source, int u, int v);
 	
 	/* Dispatch work depending on actual (runtime) type of processor.
 	 * This is ugly but I want to avoid generic types (which would
 	 * not be of much help in this case anyway).
 	 */
 	public void applyTo(ImageProcessor ip) {	// check for target == null?
		int w = ip.getWidth();
		int h = ip.getHeight();
 		ImageProcessor ipCopy = ip.duplicate();
 
 		if (ip instanceof ColorProcessor) {
 	 		ImageAccessor.Color iaOrig = ImageAccessor.Color.create(ip);
 	 		ImageAccessor.Color iaCopy = ImageAccessor.Color.create(ipCopy);
 	        for (int v=0; v<h; v++) {
 	            for (int u = 0; u < w; u++) {
 	            	//int p = (int) filterPixel(iaCopy, u, v);
 	            	float[] rgb = filterPixel(iaCopy, u, v);
 	            	iaOrig.setp(u, v, rgb);
 	            }
 	            IJ.showProgress(v, h);
 	        }
 		}
 		else {
 			ImageAccessor.Gray iaOrig = ImageAccessor.Gray.create(ip);
 	 		ImageAccessor.Gray iaCopy = ImageAccessor.Gray.create(ipCopy);
			for (int v = 0; v < h; v++) {
				for (int u = 0; u < w; u++) {
					float p = filterPixel(iaCopy, u, v);
					iaOrig.setp(u, v, p);
				}
				IJ.showProgress(v, h);
			}
 		}
 	}
 	
// 	private void filterImageGray(ImageProcessor ipOrig) {
// 		IJ.log("filterImageGray(ImageProcessor)");
//		int w = ipOrig.getWidth();
//		int h = ipOrig.getHeight();
// 		ImageProcessor ipCopy = ipOrig.duplicate();
// 		ImageAccessor.Gray iaOrig = ImageAccessor.Gray.create(ipOrig);	// DANGEROUS CAST?
// 		ImageAccessor.Gray iaCopy = ImageAccessor.Gray.create(ipCopy);
//        for (int v=0; v<h; v++) {
//            for (int u=0; u<w; u++) {
//            	int p = Math.round(filterPixel(iaCopy, u, v));
//            	iaOrig.setp(u, v, p);
//            }
//            IJ.showProgress(v, h);
//        }
// 	}
 	
// 	private void filterImageColor(ColorProcessor ipOrig) {
// 		IJ.log("filterImageRgb(ColorProcessor)");
//		int w = ipOrig.getWidth();
//		int h = ipOrig.getHeight();
// 		ImageProcessor ipCopy = ipOrig.duplicate();
// 		ImageAccessor.Color iaOrig = ImageAccessor.Color.create(ipOrig);
// 		ImageAccessor.Color iaCopy = ImageAccessor.Color.create(ipCopy);
//        for (int v=0; v<h; v++) {
//            for (int u=0; u<w; u++) {
//            	//int p = (int) filterPixel(iaCopy, u, v);
//            	float[] rgb = filterPixel(iaCopy, u, v);
//            	iaOrig.setp(u, v, rgb);
//            }
//            IJ.showProgress(v, h);
//        }
// 	}
 	

}
