/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.corners;

import ij.IJ;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.image.Process;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class HarrisCornerDetector {
	
	/* Default parameters; a (usually modified) instance of this class
	 * may be passed to constructor of the main class.
	 */
	public static class Parameters {
		public double alpha = 0.050;
		public int threshold = 20000;
		public double dmin = 10;
		public int border = 20;
		public boolean doCleanUp = true;
	}
	
	private final Parameters params;
	
	//filter kernels (one-dim. part of separable 2D filters)
	private final float[] pfilt = {0.223755f, 0.552490f, 0.223755f};
	private final float[] dfilt = {0.453014f, 0.0f, -0.453014f};
	private final float[] bfilt = {0.01563f, 0.09375f, 0.234375f, 0.3125f, 0.234375f, 0.09375f, 0.01563f};
						  // = {1,6,15,20,15,6,1}/64
	
	private ImageProcessor ipOrig;
	private FloatProcessor A;							
	private FloatProcessor B;
	private FloatProcessor C;
	private FloatProcessor Q;
	private List<Corner> corners;
	
	public HarrisCornerDetector(ImageProcessor ip){
		this(ip, new Parameters());
	}
	
	public HarrisCornerDetector(ImageProcessor ip, Parameters params) {
		this.ipOrig = ip;
		this.params = params;
	}
	
	public void findCorners(){
		makeDerivatives();
		makeCrf();	//corner response function (CRF)
		corners = collectCorners(params.border);
		if (params.doCleanUp) {
			corners = cleanupCorners(corners);
		}
	}
	
	private void makeDerivatives(){
//		FloatProcessor Ix = (FloatProcessor) ipOrig.convertToFloat();
//		FloatProcessor Iy = (FloatProcessor) ipOrig.convertToFloat();
		FloatProcessor Ix, Iy;
		if (ipOrig instanceof FloatProcessor) {
			Ix = (FloatProcessor) ipOrig.duplicate();
			Iy = (FloatProcessor) ipOrig.duplicate();
		}
		else {
			Ix = (FloatProcessor) ipOrig.convertToFloat();
			Iy = (FloatProcessor) ipOrig.convertToFloat();
		}
		
		Ix = Process.convolve1h(Process.convolve1h(Ix, pfilt), dfilt);
		Iy = Process.convolve1v(Process.convolve1v(Iy, pfilt), dfilt);

		A = Process.sqr((FloatProcessor) Ix.duplicate()); 
		A = Process.convolve2(A, bfilt);

		B = Process.sqr((FloatProcessor) Iy.duplicate()); 
		B = Process.convolve2(B, bfilt);

		C = Process.mult((FloatProcessor)Ix.duplicate(), Iy);
		C = Process.convolve2(C, bfilt);
	}
	
	private void makeCrf() { //corner response function (CRF)
		int w = ipOrig.getWidth();
		int h = ipOrig.getHeight();
		final float alpha = (float) params.alpha;
		Q = new FloatProcessor(w, h);
		float[] Apix = (float[]) A.getPixels();
		float[] Bpix = (float[]) B.getPixels();
		float[] Cpix = (float[]) C.getPixels();
		float[] Qpix = (float[]) Q.getPixels();
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int i = v * w + u;
				float a = Apix[i], b = Bpix[i], c = Cpix[i];
				float det = a * b - c * c;
				float trace = a + b;
				Qpix[i] = det - alpha * (trace * trace);
			}
		}
	}
	
	private List<Corner> collectCorners(int border) {
		List<Corner> cornerList = new Vector<Corner>(1000);
		final int w = Q.getWidth();
		final int h = Q.getHeight();
		final int threshold = params.threshold;
		float[] Qpix = (float[]) Q.getPixels();
		for (int v = border; v < h - border; v++) {
			for (int u = border; u < w - border; u++) {
				float q = Qpix[v * w + u];
				if (q > threshold && isLocalMax(Q, u, v)) {
					Corner c = new Corner(u, v, q);
					cornerList.add(c);
				}
			}
		}
		Collections.sort(cornerList);	// sort corners by descending q-value
		return cornerList;
	}
	
	private List<Corner> cleanupCorners(List<Corner> corners){
		// corners are assumed to be sorted by descending q-value
		double dmin2 = params.dmin * params.dmin;
		Corner[] cornerArray = new Corner[corners.size()];
		cornerArray = corners.toArray(cornerArray);
		List<Corner> goodCorners = new Vector<Corner>(corners.size());
		for (int i = 0; i < cornerArray.length; i++) {
			if (cornerArray[i] != null){
				Corner c1 = cornerArray[i];
				goodCorners.add(c1);
				//delete all remaining corners close to c
				for (int j = i + 1; j < cornerArray.length; j++) {
					if (cornerArray[j] != null){
						Corner c2 = cornerArray[j];
						if (c1.dist2(c2) < dmin2)
							cornerArray[j] = null;   //delete corner c2
					}
				}
			}
		}
		return goodCorners;
	}
	
	void printCornerPoints(List<Corner> crf){
		int i = 0;
		for (Corner ipt: crf){
			IJ.log((i++) + ": " + (int)ipt.q + " " + ipt.u + " " + ipt.v);
		}
	}
	
	
	private boolean isLocalMax (FloatProcessor fp, int u, int v) {
		int w = fp.getWidth();
		int h = fp.getHeight();
		if (u <= 0 || u >= w-1 || v <= 0 || v >= h-1)
			return false;
		else {
			float[] pix = (float[]) fp.getPixels();
			final int i0 = (v - 1) * w + u;
			final int i1 = v * w + u;
			final int i2 = (v + 1) * w + u;
			float cp = pix[i1];
			return
				cp >= pix[i0 - 1] && cp >= pix[i0] && cp >= pix[i0 + 1] &&
				cp >= pix[i1 - 1] &&                  cp >= pix[i1 + 1] && 
				cp >= pix[i2 - 1] && cp >= pix[i2] && cp >= pix[i2 + 1] ;
		}
	}
	
	//-----------------------------------------------------------
	
	public ImageProcessor showCornerPoints(ImageProcessor ip, int nmax){
		ByteProcessor ipResult = (ip instanceof ByteProcessor) ? 
						(ByteProcessor) ip.duplicate() : 
						(ByteProcessor) ip.convertToByte(true);
		//change background image contrast and brightness
		int[] lookupTable = new int[256];
		for (int i = 0; i < 256; i++){
			lookupTable[i] = 128 + (i/2);
		}
		ipResult.applyTable(lookupTable);  
		//draw corners:
		int n = 0;
		for (Corner c: corners) {
			drawCorner(ipResult, c);
			n = n + 1;
			if (nmax > 0 && n >= nmax) 
				break;
		}
		return ipResult;
	}
	
	private void drawCorner(ImageProcessor ip, Corner c){
		//draw corner c as a black cross into ip
		int paintvalue = 0; //black
		int size = 2;
		ip.setValue(paintvalue);
		int uu = Math.round(c.u);
		int vv = Math.round(c.v);
		ip.drawLine(uu - size, vv, uu + size, vv);
		ip.drawLine(uu, vv - size, uu, vv + size);
	}

}
