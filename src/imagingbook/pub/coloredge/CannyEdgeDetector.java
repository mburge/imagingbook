/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.coloredge;

import ij.plugin.filter.Convolver;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * This class implements a Canny edge detector for grayscale and RGB images.
 * @author W. Burger
 * @version 2013/05/30
 */
public class CannyEdgeDetector extends ColorEdgeDetector {
	
	public static class Parameters {	
		public float gSigma = 2.0f;			// Gaussian sigma (scale)
		public float hiThr  = 20.0f;		// 20% of max. edge magnitude
		public float loThr  = 5.0f;			//  5% of max. edge magnitude
		public boolean normGradMag = true;	// normalize gradient magnitude
		
		public boolean isInValid () { // returns true if any invalid condition is found
			return gSigma < 0.1f || loThr >hiThr;
		}
	}
	
	Parameters params;
	ImageProcessor I;				// original image
	int M, N;						// width and height of I
	FloatProcessor Emag;			// gradient magnitude
	FloatProcessor Enms;			// non-max suppressed gradient magnitude
	FloatProcessor Ex, Ey;			// edge normal vectors
	ByteProcessor Ebin;				// final (binary) edge image
	List<List<Point>> traceList;	// list of edge traces
	
	// Constructor with default parameters:
	public CannyEdgeDetector(ImageProcessor ip) {
		this(ip, new Parameters());
	}
	
	// Constructor with parameter object:
	public CannyEdgeDetector(ImageProcessor ip, Parameters params) {
		if (params.isInValid()) throw new IllegalArgumentException();
		this.params = params;
		this.I = ip;
		findEdges();
	}
	
	// do the work ...
	void findEdges() {
		M = I.getWidth();
		N = I.getHeight();
		if (I instanceof ColorProcessor) 
			makeGradientsAndMagnitudeColor();
		else
			makeGradientsAndMagnitudeGray();
		nonMaxSuppression();
		detectAndTraceEdges();
	}
	
	//---------------------------------------------------------------------------
	
	void makeGradientsAndMagnitudeGray() {
		FloatProcessor If = (I instanceof FloatProcessor) ? 
				(FloatProcessor) I.duplicate() :
				(FloatProcessor) I.convertToFloat();
				
		// apply a separable Gaussian filter to I
		float[] gaussKernel = makeGaussKernel1d(params.gSigma);
		Convolver conv = new Convolver();
		conv.setNormalize(true);
		conv.convolve(If, gaussKernel, gaussKernel.length, 1);
		conv.convolve(If, gaussKernel, 1, gaussKernel.length);
		
		// calculate the gradients in X- and Y-direction
		Ex = If;
		Ey = (FloatProcessor) If.duplicate();
		float[] gradKernel = {-0.5f, 0, 0.5f};
		conv.setNormalize(false);
		conv.convolve(Ex, gradKernel, gradKernel.length, 1);
		conv.convolve(Ey, gradKernel, 1, gradKernel.length);
		
		Emag = new FloatProcessor(M, N);
		float emax = 0;
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				float dx = Ex.getf(u,v);
				float dy = Ey.getf(u,v);
				float mag = (float) Math.hypot(dx, dy);	// = (float) Math.sqrt(dx*dx + dy*dy);
				if (mag > emax) 
					emax = mag;
				Emag.setf(u, v, mag);
			}
		}
		//IJ.log("Gray emax = " + emax);
		
		// normalize gradient magnitude 
		if (params.normGradMag && emax > 0.001) 
			Emag.multiply(100.0/emax);
	}
	
	void makeGradientsAndMagnitudeColor() {
		FloatProcessor[] Irgb = rgbToFloatChannels((ColorProcessor)I);
		FloatProcessor[] Ixrgb = new FloatProcessor[3];
		FloatProcessor[] Iyrgb = new FloatProcessor[3];
		
		// apply a separable Gaussian filter to each RGB channel
		float[] gaussKernel = makeGaussKernel1d(params.gSigma);
		Convolver conv = new Convolver();
		conv.setNormalize(true);
		for (int i=0; i<Irgb.length; i++) {
			FloatProcessor I = Irgb[i];
			conv.convolve(I, gaussKernel, gaussKernel.length, 1);
			conv.convolve(I, gaussKernel, 1, gaussKernel.length);
			Ixrgb[i] = I;
			Iyrgb[i] = (FloatProcessor) I.duplicate();
		}
		
		// calculate the gradients in X- and Y-direction for each RGB channel
		float[] gradKernel = {-0.5f, 0, 0.5f};
		conv.setNormalize(false);
		for (int i=0; i<Irgb.length; i++) {
			FloatProcessor Ix = Ixrgb[i];
			FloatProcessor Iy = Iyrgb[i];
			conv.convolve(Ix, gradKernel, gradKernel.length, 1);
			conv.convolve(Iy, gradKernel, 1, gradKernel.length);
		}

		// calculate gradient magnitude
		Ex = new FloatProcessor(M, N);
		Ey = new FloatProcessor(M, N);
		Emag = new FloatProcessor(M, N);
		float emax = 0;
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				float rx = Ixrgb[0].getf(u,v), ry = Iyrgb[0].getf(u,v);
				float gx = Ixrgb[1].getf(u,v), gy = Iyrgb[1].getf(u,v);
				float bx = Ixrgb[2].getf(u,v), by = Iyrgb[2].getf(u,v);
				float A = rx*rx + gx*gx + bx*bx;
				float B = ry*ry + gy*gy + by*by;
				float C = rx*ry + gx*gy + bx*by;
				float D = (float) Math.sqrt((A - B)*(A - B) + 4*C*C);
				
				float mag = (float) Math.sqrt(0.5*(A+B+D));
				if (mag > emax)	emax = mag;
				Emag.setf(u, v, mag);
				Ex.setf(u, v, A - B + D);
				Ey.setf(u, v, 2*C);
			}
		}
		//IJ.log("RGB emax = " + emax);
		// normalize gradient magnitude 
		if (params.normGradMag && emax > 0.001) 
			Emag.multiply(100.0/emax);
	}
	
	
	//---------------------------------------------------------------------------
	
	void nonMaxSuppression() {
		// perform non-maximum suppression along gradient direction	
		Enms = new FloatProcessor(M, N);
		for (int v = 1; v < N-1; v++) {
			for (int u = 1; u < M-1; u++) {
				int s_theta = getOrientationSector(Ex.getf(u, v), Ey.getf(u, v));
				if (isLocalMaximum(Emag, u, v, s_theta, params.loThr)) {
					Enms.setf(u, v, Emag.getf(u, v));	// keep local maximum only
				}
			}
		}
	}
	
	void detectAndTraceEdges() {
		Ebin = new ByteProcessor(M, N);
		int color = 255;
		traceList = new LinkedList<List<Point>>();
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				if (Enms.getf(u, v) >= params.hiThr && Ebin.get(u, v) == 0) { // unmarked edge point
					List<Point> trace = traceAndThreshold(u, v, params.loThr, color);
					traceList.add(trace);
				}
			}
		}
	}
	
	// Determines if the gradient magnitude is a local maximum at position (u,v)
	// in direction s_theta.
	boolean isLocalMaximum(FloatProcessor gradMagnitude, int u, int v, int s_theta, float mMin) {
		float mC = gradMagnitude.getf(u, v);
		if (mC < mMin) {
			return false;
		}
		else {
			float mL = 0, mR = 0;
			switch (s_theta) {
			case 0 : 
				mL = gradMagnitude.getf(u-1, v);
				mR = gradMagnitude.getf(u+1, v);
				break;
			case 1 : 
				mL = gradMagnitude.getf(u-1, v-1);
				mR = gradMagnitude.getPixelValue(u+1, v+1);
				break;
			case 2 : 
				mL = gradMagnitude.getf(u, v-1);
				mR = gradMagnitude.getf(u, v+1);
				break;
			case 3 : 
				mL = gradMagnitude.getf(u-1, v+1);
				mR = gradMagnitude.getf(u+1, v-1);
				break;
			}
			return (mL <= mC && mC >= mR);
		}
	}
	
	///Recursively collect and mark all pixels of an edge that are 8-connected to 
	// (u0,v0) and have a gradient magnitude above loThr.
	/**
	 * Recursively collect and mark all pixels of an edge that are 8-connected to 
	 * (u0,v0) and have a gradient magnitude above loThr.
	 * @param u0 start coordinate (x)
	 * @param v0 start coordinate (y)
	 * @param loThr low threshold (min. edge magnitude to continue tracing)
	 * @param markColor color used for marking pixels on edge trace
	 * @return
	 */
	List<Point> traceAndThreshold(int u0, int v0, float loThr, int markColor) {
		//IJ.log("  working point " + u + " " + v);
		Stack<Point> pointStack = new Stack<Point>();
		List<Point> pointList = new LinkedList<Point>();
		pointStack.push(new Point(u0, v0));
		while (!pointStack.isEmpty()) {
			Point p = pointStack.pop();
			int up = p.x;
			int vp = p.y;
			Ebin.putPixel(up, vp, markColor);	// mark this edge point
			pointList.add(p);
				
			int uL = Math.max(up - 1, 0); 		// (up > 0) ? up-1 : 0;
			int uR = Math.min(up + 1, M - 1); 	// (up < M-1) ? up+1 : M-1;
			int vT = Math.max(vp - 1, 0); 		// (vp > 0) ? vp - 1 : 0;
			int vB = Math.min(vp + 1, N - 1); 	// (vp < N-1) ? vp+1 : N-1;
			
			for (int u = uL; u <= uR; u++) {
				for (int v = vT; v <= vB; v++) {
					if (Ebin.get(u, v) == 0 && Enms.getf(u, v) >= loThr) {
						pointStack.push(new Point(u,v));
					}
				}
			}
		}
		return pointList;
	}
	
	private final float cosPi8 = (float) Math.cos(Math.PI/8);
	private final float sinPi8 = (float) Math.sin(Math.PI/8);
	
	// returns the quantized orientation sector for vector (dx, dy)
	int getOrientationSector(float dx, float dy) {
		// rotate the gradient vector by PI/8
		float dxR = cosPi8 * dx - sinPi8 * dy;
		float dyR = sinPi8 * dx + cosPi8 * dy;	
		// mirror vector (dxR,dyR) to [0,PI]
		if (dyR < 0) {
			dxR = -dxR;
			dyR = -dyR;
		}
		// determine the octant for (dx, dy)
		int s_theta;
		if (dxR >= 0) { // dxR >= 0, dyR >= 0
			if (dxR >= dyR)
				s_theta = 0; // return 0;
			else
				s_theta = 1; // return 1;
		}
		else {  // dxR < 0, dyR >= 0
			if (-dxR < dyR)
				s_theta = 2; // return 2;
			else
				s_theta = 3; //return 3;
		}
		return s_theta;
	}
	
	public FloatProcessor getEdgeMagnitude() {
		return Emag;
	}

	public FloatProcessor getEdgeOrientation() {
		FloatProcessor E_theta = new FloatProcessor(M, N);
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				double ex = Ex.getf(u, v);
				double ey = Ey.getf(u, v);
				float theta = (float) Math.atan2(ey, ex);
				E_theta.setf(u, v, theta);
			}
		}
		return E_theta;
	}
	
	public ByteProcessor getEdgeBinary() {
		return Ebin;
	}
	
	public List<List<Point>> getEdgeTraces() {
		return traceList;
	}
	
	//---------------------------------------------------------------------------

	float[] makeGaussKernel1d(double sigma) {
		// make 1D Gauss filter kernel large enough
		int rad = (int) (3.5 * sigma);
		int size = rad + rad + 1;
		float[] kernel = new float[size]; // center cell = kernel[rad]
		double sigma2 = sigma * sigma;
		double scale = 1 / Math.sqrt(2 * Math.PI * sigma2);
		for (int i = 0; i < size; i++) {
			double x = rad - i;
			kernel[i] = (float) (
			scale * Math.exp(-0.5 * (x * x) / sigma2)
			);
		}
		return kernel;
	}
	
	// extract RGB channels of 'cp' as 3 float processors
	FloatProcessor[] rgbToFloatChannels(ColorProcessor cp) {
		int w = cp.getWidth();
		int h = cp.getHeight();
		FloatProcessor rp = new FloatProcessor(w, h);
		FloatProcessor gp = new FloatProcessor(w, h);
		FloatProcessor bp = new FloatProcessor(w, h);
		int[] pixels = (int[]) cp.getPixels(); 
		float[] rpix = (float[]) rp.getPixels();
		float[] gpix = (float[]) gp.getPixels();
		float[] bpix = (float[]) bp.getPixels();
    	for (int i=0; i<pixels.length; i++) {
            int c = pixels[i];
            rpix[i] = (c&0xff0000)>>16;
    		gpix[i] = (c&0xff00)>>8;
			bpix[i] =  c&0xff;
    	}
    	return new FloatProcessor[] {rp, gp, bp};
	}
	
}
