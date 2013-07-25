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
import imagingbook.lib.filters.GenericFilter;
import imagingbook.lib.image.ImageAccessor;

import java.util.Formatter;
import java.util.Locale;


/*
 * Generic linear convolution filter implemented
 * by extending the GenericFilter class.
 */

public class LinearFilter extends GenericFilter {
	private float[][] kernel2d = null;

	private int kernelWidth, kernelHeight;	// width/height of the kernel
	private int kernelCtrX, kernelCtrY;	// center coordinates of the kernel
	
	public LinearFilter(float[][] kernel2d) {
//		super(kernel2d[0].length / 2, kernel2d.length / 2);
		this.kernel2d = kernel2d;
		this.kernelWidth = kernel2d.length;
		this.kernelHeight = kernel2d[0].length;
		kernelCtrX = kernelWidth / 2;
		kernelCtrY = kernelHeight / 2;
	}
	
	// --------------------------------------------------------------
	

	public float filterPixel(ImageAccessor.Gray ia, int u, int v) {
		float sum = 0;
		for (int j=0; j<kernelHeight; j++) {
			int vj = v+j-kernelCtrY;
			for (int i=0; i<kernelWidth; i++) {
				int ui = u+i-kernelCtrX;
				sum = sum + ia.getp(ui,vj) * kernel2d[i][j];
			}
		}
 		return sum;
	}
	
	final float[] rgb = {0,0,0};
	
	public float[] filterPixel(ImageAccessor.Color ia, int u, int v) {
		if (u==0 && v ==0) {
			IJ.log("filterPixel(ImageAccessor.Rgb");
		}
		float sumR = 0;	// sum of weighted red
		float sumG = 0;	// sum of weighted green
		float sumB = 0;	// sum of weighted blue
		int[] Iij = new int[3];
		for (int j=0; j<kernelHeight; j++) {
			int vj = v+j-kernelCtrY;
			for (int i=0; i<kernelWidth; i++) {
				int ui = u+i-kernelCtrX;
				ia.getp(ui,vj,Iij);
				float w = kernel2d[i][j];
				sumR = sumR + Iij[0] * w;
				sumG = sumG + Iij[1] * w;
				sumB = sumB + Iij[2] * w;
			}
		}
		rgb[0] = sumR;
		rgb[1] = sumG;
		rgb[2] = sumB;
//		int r = Math.round(sumR);
//		int g = Math.round(sumG);
//		int b = Math.round(sumB);
// 		return ((r & 0xFF)<<16) | ((g & 0xFF)<<8) | b & 0xFF;
		return rgb;
 	}
	
//	public int filterPixel(ByteProcessor image, int u, int v) {
//	float sum = 0;
//	for (int j=0; j<kernelHeight; j++) {
//		int vj = v+j-kernelCtrY;
//		for (int i=0; i<kernelWidth; i++) {
//			int ui = u+i-kernelCtrX;
//			sum = sum + image.getPixel(ui,vj) * kernel[j][i];
//		}
//	}
//		return (int)Math.rint(sum);
//	}
	
	// --------------------------------------------------------------
	
//	public int filterPixel(ShortProcessor image, int u, int v) {
//		// same as for ByteProcessor
//		float sum = 0;
//		for (int j=0; j<kernelHeight; j++) {
//			int vj = v+j-kernelCtrY;
//			for (int i=0; i<kernelWidth; i++) {
//				int ui = u+i-kernelCtrX;
//				sum = sum + image.getPixel(ui,vj) * kernel2d[i][j];
//			}
//		}
// 		return (int)Math.rint(sum);
// 	}
	
	// --------------------------------------------------------------
	
//	public float filterPixel(FloatProcessor image, int u, int v) {
//		float sum = 0;
//		for (int j=0; j<kernelHeight; j++) {
//			int vj = v+j-kernelCtrY;
//			for (int i=0; i<kernelWidth; i++) {
//				int ui = u+i-kernelCtrX;
//				sum = sum + image.getPixelValue(ui,vj) * kernel2d[i][j];
//			}
//		}
// 		return sum;
// 	}
	
	// --------------------------------------------------------------
	
//	public int filterPixel(ColorProcessor image, int u, int v) {
//		float sumR = 0;	// sum of weighted red
//		float sumG = 0;	// sum of weighted green
//		float sumB = 0;	// sum of weighted blue
//		int[] Iij = new int[3];
//		for (int j=0; j<kernelHeight; j++) {
//			int vj = v+j-kernelCtrY;
//			for (int i=0; i<kernelWidth; i++) {
//				int ui = u+i-kernelCtrX;
//				image.getPixel(ui,vj,Iij);
//				float w = kernel2d[i][j];
//				sumR = sumR + Iij[0] * w;
//				sumG = sumG + Iij[1] * w;
//				sumB = sumB + Iij[2] * w;
//			}
//		}
//		int r = Math.round(sumR);
//		int g = Math.round(sumG);
//		int b = Math.round(sumB);
// 		return ((r & 0xFF)<<16) | ((g & 0xFF)<<8) | b & 0xFF;
// 	}
	


	// --------------------------------------------------------------

	public void listKernel() {
		for (int j = 0; j < kernelHeight; j++) {
			StringBuilder sb = new StringBuilder();
			Formatter fm = new Formatter(sb, Locale.US);
			for (int i = 0; i < kernelWidth; i++) {
				fm.format(" %.5f | ", kernel2d[i][j]);
			}
			fm.format("\n");
			IJ.log(fm.toString());
			fm.close();
		}
	}
}
