/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.sift.filters;

/*
 * TODO: Should use the GenericFilter class!
 */

import ij.IJ;
import ij.plugin.filter.Convolver;
import ij.process.FloatProcessor;

public class GaussianFilter {
	
	static final double kernelSizeFactor = 3.5;   // times sigma
	private float[] kernel1D;
	
	public GaussianFilter(double sigma) {
		kernel1D = makeGaussKernel1d(sigma);
	}
	
	public void applyTo(FloatProcessor fp) {
		Convolver Conv = new Convolver();
		Conv.setNormalize(true);	// this is important!
		Conv.convolve(fp, kernel1D, 1, kernel1D.length);
		Conv.convolve(fp, kernel1D, kernel1D.length, 1);
	}
	
	public static float[] makeGaussKernel1d(double sigma){
		//make 1D Gauss filter kernel large enough
		//to avoid truncation effects (too small in ImageJ) 
		int rad = (int) (kernelSizeFactor * sigma);
		if (rad < 1) rad = 1;
		int size = rad+rad+1;
		float[] kernel = new float[size]; //center cell = kernel[rad]
		double sigma2 = sigma * sigma;
		double scale = 1.0 / (Math.sqrt(2 * Math.PI) * sigma);
		
		for (int i=0; i<size; i++){
			double x = rad - i;
			kernel[i] =  (float) (scale * Math.exp(-0.5 * (x*x) / sigma2));
		}
		
		return kernel;
	}
	
	static float[][] makeGaussKernel2d(double sigma){

		int rad = (int) (kernelSizeFactor * sigma);
		int size = rad+rad+1;
		float[][] kernel = new float[size][size]; //center cell = kernel[rad][rad]
		double sigma2 = sigma * sigma;
		double scale = 1.0 / (2 * Math.PI * sigma * sigma);
		
		for (int i=0; i<size; i++){
			double x = rad - i;
			for (int j=0; j<size; j++){ 
				double y = rad - j;
				kernel[i][j] =  (float) (scale * Math.exp(-0.5 * (x*x + y*y) / sigma2));
			}
		}
		
		return kernel;
	}
	
	void printKernel(float[] kernel) {
		System.out.println("****** Gaussian kernel ******* ");
		for (int i=0; i<kernel.length; i++) {
			System.out.println(i+": "+kernel[i]);
		}
	}
	
	void printKernel(float[][] kernel) {
		System.out.println("****** Gaussian kernel ******* ");
		for (int i=0; i<kernel.length; i++) {
			for (int j=0; j<kernel[i].length; j++) {
				System.out.print(" "+kernel[i][j]);
			}
			System.out.println();
		}
	}
	
	void printKernel(float[] kernel, String title) {
		IJ.log("****** " + title + " ******* ");
		for (int i=0; i<kernel.length; i++) {
			IJ.log(i+": "+kernel[i]);
		}
	}
	
	static float sumKernel(float[] kernel) {
		float sum = 0;
		for (int i=0; i<kernel.length; i++) {
			sum = sum +kernel[i];
		}
		return sum;
	}
	
	static float sumKernel(float[][] kernel) {
		float sum = 0;
		for (int i=0; i<kernel.length; i++) {
			for (int j=0; j<kernel[i].length; j++) {
				sum = sum +kernel[i][j];
			}
		}
		return sum;
	}

}
