/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.edgepreservingfilters;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.colorimage.sRgbUtil;

//TODO: work over to use GenericFilter (as in BilateralFilter)

/**
 * This code is based on the Anisotropic Diffusion filter proposed by Perona and Malik,
 * as proposed in Pietro Perona and Jitendra Malik, "Scale-space and edge detection 
 * using anisotropic diffusion", IEEE Transactions on Pattern Analysis 
 * and Machine Intelligence, vol. 12, no. 4, pp. 629-639 (July 1990).
 * 
 * The filter operates on all types of grayscale (scalar) and RGB color images.
 * This class is based on the ImageJ API and intended to be used in ImageJ plugins.
 * How to use: consult the source code of the related ImageJ plugins for examples.
 * 
 * @author W. Burger
 * @version 2013/05/30
 */
public class PeronaMalikFilter {
	
	public static enum ColorMode  {
		SeparateChannels, 
		BrightnessGradient, 
		ColorGradient;
	}
	
	public static class Parameters {
		public int iterations = 10;
		public float alpha = 0.20f; 			// update rate
		public float kappa = 25; 				// smoothness parameter (kappa)
		public boolean smoothRegions = true;	// selects conductivity function c()
		public ColorMode colorMode = ColorMode.SeparateChannels;
		public boolean useLinearRgb = false;
	}
	
	private final Parameters params;
	private final int T; // number of iterations
	private final ConductanceFunction g;
	
	private int M;		// image width
	private int N;		// image height
	
	// constructor - using default parameters
	public PeronaMalikFilter () {
		this(new Parameters());
	}
	
	// constructor - use this version to set all parameters
	public PeronaMalikFilter (Parameters params) {
		this.params = params;
		T = params.iterations;
		g = (params.smoothRegions) ? g2 : g1;
	}
	
	public void applyTo(ImageProcessor ip) {
		M = ip.getWidth();
		N = ip.getHeight();
		FilterOperator fm = null;
		if (ip instanceof ColorProcessor) {
			switch (params.colorMode) {
			case SeparateChannels : 	fm = new FilterColorSeparate(); break;
			case BrightnessGradient : 	fm = new FilterColorBrightnessGradient(); break;
			case ColorGradient : 	  	fm = new FilterColorColorGradient(); break;
			}
		}
		else {
			fm = new FilterScalar();
		}
 		fm.filter(ip);
	}
	
	// ------------------------------------------------------
	
	private interface ConductanceFunction {
		float eval(float d);
	}
	
	// ConductanceFunction objects g1, g2 implemented with anonymous classes:
	
	// = g_K^{(1)} (d)	// for not so smooth regions
	private final ConductanceFunction g1 = new ConductanceFunction() {
		public float eval(float d) {
			float gK = d/params.kappa;
			return (float) Math.exp(-gK*gK);
		}
	};
	
	// = g_K^{(2)} (d)	// for smoother regions
	private final ConductanceFunction g2 = new ConductanceFunction() {
		public float eval(float d) {
			float gK = d / params.kappa;
			return (1.0f / (1.0f + gK*gK));
		}
	};
	
	// ------------------------------------------------------
	
	/*
	 * Interface for different types of operators. The implementing
	 * classes (below) do the actual work, depending on the image type
	 * and (in case of color images) the specified color mode.
	 */
	// TODO: change to use GenericFilter
	private interface FilterOperator {
		void filter(ImageProcessor ip);
	}

	/* ----------------------------------------------------------------------
	 * FilterOperator for scalar images (8 bit, 16 bit, float)
	 * -------------------------------------------------------------------- */
	
	private class FilterScalar implements FilterOperator {
		private float[][] I = null;			// I[u][v]
		private float[][] Dx = null;		// Dx[u][v] = I[u+1][v] - I[u][v]
		private float[][] Dy = null;		// Dy[u][v] = I[u][v+1] - I[u][v]
		
		public void filter(ImageProcessor ip) {	
			// create temporary data structures
			I = ip.getFloatArray();
			Dx = new float[M][N];
			Dy = new float[M][N];			
			if (params.useLinearRgb) srgbToRgb(I);
			
			// perform actual filter operation
			for (int n = 1; n <= T; n++) {
				IJ.showProgress(n, T);
				iterateOnce();
			}		
			if (params.useLinearRgb) rgbToSrgb(I);
			copyResultToImage(I, ip);
			
			I = null; 
			Dx = null; 
			Dy = null;
		}
		
		void iterateOnce() {
			// calculate local gradients in X and Y direction:
			for (int u = 0; u < M; u++) {
				for (int v = 0; v < N; v++) {	
					Dx[u][v] = (u < M-1) ? I[u+1][v] - I[u][v] : 0; // set last column to zero
					Dy[u][v] = (v < N-1) ? I[u][v+1] - I[u][v] : 0; // set last row to zero
				}
			}
			
			// update image data:
			for (int u = 0; u < M; u++) {
				for (int v = 0; v < N; v++) {
					float d0 = Dx[u][v];
					float d1 = Dy[u][v];
					float d2 = (u>0) ? -Dx[u-1][v] : 0;
					float d3 = (v>0) ? -Dy[u][v-1] : 0;
					I[u][v] = I[u][v] +
						params.alpha * (g.eval(d0)*d0 + g.eval(d1)*d1 + g.eval(d2)*d2 + g.eval(d3)*d3);
				}
			}
		}
		
		private void copyResultToImage(float[][] imgData, ImageProcessor ip) {
			if (ip instanceof FloatProcessor) {
				FloatProcessor cp = (FloatProcessor) ip;
				for (int v = 0; v < N; v++) {
					for (int u = 0; u < M; u++) {
						cp.putPixelValue(u, v, imgData[u][v]);
					}
				}
			}
			else {
				for (int v = 0; v < N; v++) {
					for (int u = 0; u < M; u++) {
						ip.putPixel(u, v, (int) Math.round(imgData[u][v]));
					}
				}
			}
		}

	}
	
	/* ----------------------------------------------------------------------
	 * FilterOperator for RGB color images - applies the (scalar) anisotropic 
	 * filter to each of the RGB color channels
	 * -------------------------------------------------------------------- */
	
	private class FilterColorSeparate implements FilterOperator {

		public void filter(ImageProcessor ip) {
			ColorProcessor I = (ColorProcessor) ip;
			
			// extract color channels as individual ByteProcessor images:
			ByteProcessor Ir = new ByteProcessor(M, N);
			ByteProcessor Ig = new ByteProcessor(M, N);
			ByteProcessor Ib = new ByteProcessor(M, N);
			byte[] pR = (byte[]) Ir.getPixels();
			byte[] pG = (byte[]) Ig.getPixels();
			byte[] pB = (byte[]) Ib.getPixels();
			I.getRGB(pR, pG, pB);
			
			FilterScalar fm = new FilterScalar();
			fm.filter(Ir);
			fm.filter(Ig);
			fm.filter(Ib);
			
			// copy back to color image
			I.setRGB(pR, pG, pB);
		}
	}
	
	/* ----------------------------------------------------------------------
	 * FilterOperator for RGB color images - uses the brightness gradient to
	 * control the local conductance.
	 * -------------------------------------------------------------------- */

	private class FilterColorBrightnessGradient implements FilterOperator {
		private float[][][] I = null;	// I[c][u][v] (RGB color image)
		private float[][] B = null;		// B[u][v] (brightness image)
		// color differences in X/Y
		private float[][][] Ix = null;	// Ix[c][u][v] = I[c][u+1][v] - I[c][u][v]
		private float[][][] Iy = null;	// Iy[c][u][v] = I[c][u][v+1] - I[c][u][v]
		// color gradient
		private float[][] Bx = null;	 
		private float[][] By = null;
		
		public void filter(ImageProcessor ip) {
			ColorProcessor cp = (ColorProcessor) ip;
			I = extractRgbData(cp);
			if (params.useLinearRgb) 
				srgbToRgb(I);
			B = new float[M][N];
			Ix = new float[3][M][N];	// local differences in R,G,B (x-direction)
			Iy = new float[3][M][N]; 	// local differences in R,G,B (y-direction)
			Bx = new float[M][N];		// local differences in brightness  (x-direction)
			By = new float[M][N];		// local differences in brightness  (y-direction)
			
			for (int t = 1; t <= T; t++) {
				IJ.showProgress(t, T);
				iterateOnce();
			}	
			if (params.useLinearRgb) 
				rgbToSrgb(I);
			copyResultToImage(I, cp);
			
			I = null; Ix = null; Iy = null;
			Bx = null; By = null;
		}
		
		private void iterateOnce() {
			// re-calculate local brightness:
			for (int v = 0; v < N; v++) {	
				for (int u = 0; u < M; u++) {	
					B[u][v] = getBrightness(I[0][u][v], I[1][u][v], I[2][u][v]);
				}
			}
			// re-calculate local color differences and brightness gradient in X and Y direction:
			for (int v = 0; v < N; v++) {	
				for (int u = 0; u < M; u++) {
	    			if (u < M-1) {
	    				Ix[0][u][v] = I[0][u+1][v] - I[0][u][v];
	    				Ix[1][u][v] = I[1][u+1][v] - I[1][u][v];
	    				Ix[2][u][v] = I[2][u+1][v] - I[2][u][v];
	    				Bx[u][v] 	= B[u+1][v] - B[u][v];
	    			}
	    			else {
	    				Ix[0][u][v] = Ix[1][u][v] = Ix[2][u][v] = Bx[u][v] = 0;
	    			}    				
	    			if (v < N-1) {
	    				Iy[0][u][v] = I[0][u][v+1] - I[0][u][v];
	    				Iy[1][u][v] = I[1][u][v+1] - I[1][u][v];
	    				Iy[2][u][v] = I[2][u][v+1] - I[2][u][v];
	    				By[u][v] 	= B[u][v+1] - B[u][v];
	    			}
	    			else {
	    				Iy[0][u][v] = Iy[1][u][v] = Iy[2][u][v] = By[u][v] = 0;
	    			}
				}
			}	
			// update image data:
			for (int v = 0; v < N; v++) {
				for (int u = 0; u < M; u++) {
					// brightness gradients:
					float dw = (u>0) ? -Bx[u-1][v] : 0;
					float de = Bx[u][v];
					float dn = (v>0) ? -By[u][v-1] : 0;
					float ds = By[u][v];
					// update all color channels
					for (int i = 0; i<3; i++) {
						float dWrgb = (u>0) ? -Ix[i][u-1][v] : 0;			
						float dErgb = Ix[i][u][v];
						float dNrgb = (v>0) ? -Iy[i][u][v-1] : 0;
						float dSrgb = Iy[i][u][v];
						I[i][u][v] = I[i][u][v] +
								params.alpha * (g.eval(dn)*dNrgb + g.eval(ds)*dSrgb + g.eval(de)*dErgb + g.eval(dw)*dWrgb);
					}
				}
			}		
		}
		
		private final float getBrightness(float r, float g, float b) {
			return 0.299f * r + 0.587f * g + 0.114f * b;
		}
	}
	
	/* ----------------------------------------------------------------------
	 * FilterOperator for RGB color images - uses the DiZenzo color gradient 
	 * to control the local conductance.
	 * -------------------------------------------------------------------- */
	
	private class FilterColorColorGradient implements FilterOperator {
		private float[][][] I = null;	// I[c][u][v]
		// color differences in X/Y
		private float[][][] Ix = null;	// Ix[c][u][v] = I[c][u+1][v] - I[c][u][v][
		private float[][][] Iy = null;	// Iy[c][u][v][c] = I[c][u][v+1] - I[c][u][v]
		// color gradient
		private float[][] Sx = null;	// local color gradient (x-direction)
		private float[][] Sy = null;	// local color gradient (y-direction)

		public void filter(ImageProcessor ip) {
			ColorProcessor cp = (ColorProcessor) ip;
			I = extractRgbData(cp);
			if (params.useLinearRgb) srgbToRgb(I);
			Ix = new float[3][M][N];	
			Iy = new float[3][M][N];			
			Sx = new float[M][N];
			Sy = new float[M][N];
			
			for (int n = 1; n <= T; n++) {
				IJ.showProgress(n, T);
				iterateOnce();
			}
			
			if (params.useLinearRgb) rgbToSrgb(I);
			copyResultToImage(I, cp);
			I = null; Ix = null; Iy = null;
			Sx = null; Sy = null;
		}
		
		private void iterateOnce() {
			// recalculate gradients:
			for (int v = 0; v < N; v++) {	
				for (int u = 0; u < M; u++) {
					float Rx = 0, Gx = 0, Bx = 0;
	    			float Ry = 0, Gy = 0, By = 0;
	    			if (u < M-1) {
	    				Rx = I[0][u+1][v] - I[0][u][v];
	    				Gx = I[1][u+1][v] - I[1][u][v];
	    				Bx = I[2][u+1][v] - I[2][u][v];
	    			}
	    			if (v < N-1) {
	    				Ry = I[0][u][v+1] - I[0][u][v];
	    				Gy = I[1][u][v+1] - I[1][u][v];
	    				By = I[2][u][v+1] - I[2][u][v];
	    			}    			
	    			Ix[0][u][v] = Rx; Ix[1][u][v] = Gx; Ix[2][u][v] = Bx;
	    			Iy[0][u][v] = Ry; Iy[1][u][v] = Gy; Iy[2][u][v] = By;
	    			// Di Zenzo color contrast along X/Y-axes
    				Sx[u][v] = (float) Math.sqrt(Rx * Rx + Gx * Gx + Bx * Bx);
    				Sy[u][v] = (float) Math.sqrt(Ry * Ry + Gy * Gy + By * By);
				}
			}
			
			// update image data:
			for (int v = 0; v < N; v++) {
				for (int u = 0; u < M; u++) {
					float s0 = Sx[u][v];  
					float s1 = Sy[u][v];  
					float s2 = (u>0) ? Sx[u-1][v] : 0;
					float s3 = (v>0) ? Sy[u][v-1] : 0;
					// calculate neighborhood conductance
					float c0 = g.eval(s0);
					float c1 = g.eval(s1);
					float c2 = g.eval(s2);
					float c3 = g.eval(s3);
					// update all color channels using the same neighborhood conductance
					for (int i = 0; i<3; i++) {
						// differences in color channel i
						float d0 = Ix[i][u][v];
						float d1 = Iy[i][u][v];
						float d2 = (u>0) ? -Ix[i][u-1][v] : 0;			
						float d3 = (v>0) ? -Iy[i][u][v-1] : 0;				
						I[i][u][v] = I[i][u][v] +
								params.alpha * (c0*d0 + c1*d1 + c2*d2 + c3*d3);
					}
				}
			}
		}
		
	/* ----------------------------------------------------------------------
	 * Various utility methods
	 * -------------------------------------------------------------------- */
	
	@SuppressWarnings("unused")
	private void showColorGradients() {
			(new ImagePlus("dX RED", new FloatProcessor(Ix[0]))).show();
			(new ImagePlus("dX GRN", new FloatProcessor(Ix[1]))).show();
			(new ImagePlus("dX BLU", new FloatProcessor(Ix[2]))).show();
			
			(new ImagePlus("dY RED", new FloatProcessor(Iy[0]))).show();
			(new ImagePlus("dY GRN", new FloatProcessor(Iy[1]))).show();
			(new ImagePlus("dY BLU", new FloatProcessor(Iy[2]))).show();
			
			(new ImagePlus("dX", new FloatProcessor(Sx))).show();
			(new ImagePlus("dY", new FloatProcessor(Sy))).show();
		}
	}
	
	@SuppressWarnings("unused")
	private float getAbsMaxValue(float[][] A) {
		float themax = 0;
		for (int u = 0; u < A.length; u++) {
			for (int v = 0; v < A[u].length; v++) {
				float a = Math.abs(A[u][v]);
				if (a > themax)
					themax = a;
			}
		}
		return themax;
	}
	
	// ---------------------------------------------------------------
	
	private float[][][] extractRgbData(ColorProcessor ip) {
		int w = ip.getWidth();
		int h = ip.getHeight();
		float[][][] rgbData = new float[3][w][h];
		int[] c = new int[3];
		
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				ip.getPixel(u, v, c);
				rgbData[0][u][v] = c[0];
				rgbData[1][u][v] = c[1];
				rgbData[2][u][v] = c[2];
			}
		}
		return rgbData;
	}
	
	private void copyResultToImage(float[][][] imgData, ColorProcessor ip) {
		int w = ip.getWidth();
		int h = ip.getHeight();
		int[] c = new int[3];
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				ip.getPixel(u, v, c);
				c[0] = (int) Math.round(imgData[0][u][v]);
				c[1] = (int) Math.round(imgData[1][u][v]);
				c[2] = (int) Math.round(imgData[2][u][v]);
				if (c[0] < 0) c[0] = 0;
				if (c[1] < 0) c[1] = 0;
				if (c[2] < 0) c[2] = 0;
				if (c[0] > 255) c[0] = 255;
				if (c[1] > 255) c[1] = 255;
				if (c[2] > 255) c[2] = 255;
				ip.putPixel(u, v, c);
			}
		}
	}
	
	// Conversion methods from sRGB to linear RGB -----------------------
	
	private void srgbToRgb(float[] srgb) {
		for (int i = 0; i < srgb.length; i++) {
			float srgb0 = srgb[i]/255;
			srgb[i] = (float) (sRgbUtil.gammaInv(srgb0) * 255);
		}
	}
	
	private void srgbToRgb(float[][] srgb) {
		for (int j = 0; j < srgb.length; j++) {
			srgbToRgb(srgb[j]);
		}
	}
	
	private void srgbToRgb(float[][][] srgb) {
		for (int k = 0; k < srgb.length; k++) {
			srgbToRgb(srgb[k]);
		}
	}

	// Conversion methods from linear RGB to sRGB -----------------------
	// TODO: this should be moved to class lib.colorImage.sRgbUtil
	
	private void rgbToSrgb(float[] rgb) {
		for (int i = 0; i < rgb.length; i++) {
			float rgb0 = rgb[i] / 255;
			rgb[i] = (float) (sRgbUtil.gammaFwd(rgb0) * 255);
		}
	}
	
	private void rgbToSrgb(float[][] rgb) {
		for (int j = 0; j < rgb.length; j++) {
			rgbToSrgb(rgb[j]);
		}
	}
	
	private void rgbToSrgb(float[][][] rgb) {
		for (int k=0; k<rgb.length; k++) {
			rgbToSrgb(rgb[k]);
		}
	}

}
