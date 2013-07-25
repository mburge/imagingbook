/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.matching;
import ij.process.*;

public class ChamferMatcher {
	
	public static FloatProcessor distanceTransform(ImageProcessor ip){
		FloatProcessor dp = (FloatProcessor) ip.convertToFloat();
		int w = dp.getWidth();
		int h = dp.getHeight();
		float[] dpix = (float[]) dp.getPixels();
		final float K1 = 1;
		final float K2 = (float)Math.sqrt(2); 
		
		//Initialization: 
		//foreground pixels (>0) -> 0, background -> infinity
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int i = v * w + u;
				if (dpix[i] > 0) // this is a foreground pixel
					dpix[i] = 0; // zero distance to foregorund
				else
					dpix[i] = Float.POSITIVE_INFINITY;
			}
		}
		
		//L->R pass:
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int i = v * w + u;
				if (dpix[i]>0) { //not a foreground pixel
					//compute distances via neighboring pixels
					float d1 = Float.POSITIVE_INFINITY;
					float d2 = Float.POSITIVE_INFINITY;
					float d3 = Float.POSITIVE_INFINITY;
					float d4 = Float.POSITIVE_INFINITY;
					if (u>0) 			d1 = K1 + dpix[v*w+u-1];
					if (u>0 && v>0) 	d2 = K2 + dpix[(v-1)*w+u-1];
					if (v>0)			d3 = K1 + dpix[(v-1)*w+u];
					if (v>0 && u<w-1)	d4 = K2 + dpix[(v-1)*w+u+1];
					
					float dmin = dpix[i];
					if (d1<dmin) dmin = d1;
					if (d2<dmin) dmin = d2;
					if (d3<dmin) dmin = d3;
					if (d4<dmin) dmin = d4;
					dpix[i] = dmin;
				}
			}
		}
		
		//R->L pass:
		for (int v = h - 1; v >= 0; v--) {
			for (int u = w - 1; u >= 0; u--) {
				int i = v * w + u;
				if (dpix[i] > 0) { //not a foreground pixel
					//compute distances via neighboring pixels
					float d1 = Float.POSITIVE_INFINITY;
					float d2 = Float.POSITIVE_INFINITY;
					float d3 = Float.POSITIVE_INFINITY;
					float d4 = Float.POSITIVE_INFINITY;
					if (u<w-1) 			d1 = K1 + dpix[v*w+u+1];
					if (u<w-1 && v<h-1)	d2 = K2 + dpix[(v+1)*w+u+1];
					if (v<h-1)			d3 = K1 + dpix[(v+1)*w+u];
					if (v<h-1 && u>0)	d4 = K2 + dpix[(v+1)*w+u-1];
					
					float dmin = dpix[i];
					if (d1<dmin) dmin = d1;
					if (d2<dmin) dmin = d2;
					if (d3<dmin) dmin = d3;
					if (d4<dmin) dmin = d4;
					dpix[i] = dmin;
				}
			}
		}
		
		//eliminate all infinite values (set to maxim real value)
		//works??
		float maxval = getRealMaxValue(dp);
		for (int i=0; i<dpix.length; i++){
			if (dpix[i]>maxval)
				dpix[i] = maxval;
		}
		
		return dp;
	}
    
	public static FloatProcessor chamferMatch(FloatProcessor distImg, ByteProcessor subImg){
		int w1 = distImg.getWidth();
		int h1 = distImg.getHeight();
		int w2 = subImg.getWidth();
		int h2 = subImg.getHeight();
		FloatProcessor match = new FloatProcessor(w1,h1);
		match.setValue(Float.POSITIVE_INFINITY);
		match.fill();
		
		int umax = w1 - w2;
		int vmax = h1 - h2;
		int uc = w2 / 2;
		int vc = h2 / 2;
		
		for (int u = 0; u <= umax; u++) {
			for (int v = 0; v <= vmax; v++) {
				double q = getMatchValue(distImg,subImg,u,v);
				match.putPixelValue(uc+u,vc+v,q); //insert the result at the center of sumImg
			}	
		}	
		return match;
	}
    
	static double getMatchValue(FloatProcessor distImg, ByteProcessor subImg, int u0, int v0) {
		int w1 = distImg.getWidth();
		int h1 = distImg.getHeight();
		int w2 = subImg.getWidth();
		int h2 = subImg.getHeight();
		double q = 0.0;
		float[] distPix = (float[]) distImg.getPixels();
		byte[]  subPix =  (byte[]) subImg.getPixels();
		for (int v1 = v0, v2 = 0; v1 < h1 && v2 < h2; v1++, v2++) {
			int i1 = v1 * w1; // start index of line v1
			int i2 = v2 * w2; // start index of line v2
			for (int u1 = u0, u2 = 0; u1 < w1 && u2 < w2; u1++, u2++) {
				if ((0xFF & subPix[i2+u2])>0){	//foreground pixel in template!
					q = q + distPix[i1+u1];
				}
			}
		}
		return q;
	}
	
	static float getRealMinValue(FloatProcessor fp){
		float[] pix = (float[]) fp.getPixels();
		float minval = Float.POSITIVE_INFINITY;
		for (int i = 0; i < pix.length; i++) {
			if (pix[i] > Float.NEGATIVE_INFINITY && pix[i] < minval)
				minval = pix[i];
		}
		return minval;
	}
	
	static float getRealMaxValue(FloatProcessor fp){
		float[] pix = (float[]) fp.getPixels();
		float maxval = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < pix.length; i++) {
			if (pix[i] < Float.POSITIVE_INFINITY && pix[i] > maxval)
				maxval = pix[i];
		}
		return maxval;
	}
    	
	

}
