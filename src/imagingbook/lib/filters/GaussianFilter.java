/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.filters;

public class GaussianFilter extends LinearFilter {

	public GaussianFilter(double sigma) {
		super(makeGaussKernel2d(sigma));
	}
	
	public GaussianFilter(double sigmaX, double sigmaY) {
		super(makeGaussKernel2d(sigmaX, sigmaY));
	}
	
	public static float[] makeGaussKernel1d(double sigma){
		// make 1D Gauss filter kernel large enough
		// to avoid truncation effects (too small in ImageJ!) 
		int rad = (int) (3.5 * sigma);
		float[] kernel = new float[rad + 1 + rad]; // odd size
		double sigma2 = sigma * sigma;
		
		for (int i = 0; i < kernel.length; i++) {
			double r = rad - i;
			kernel[i] =  (float) Math.exp(-0.5 * (r*r) / sigma2);
		}
		return kernel;
	}

	
	public static float[][] makeGaussKernel2d(double sigma){
		int rad = (int) (3.5 * sigma);
		int size = rad+rad+1;
		float[][] kernel = new float[size][size]; //center cell = kernel[rad][rad]
		double sigma2 = sigma * sigma;
		double scale = 1.0 / (2 * Math.PI * sigma * sigma);
		for (int i = 0; i < size; i++) {
			double x = rad - i;
			for (int j = 0; j < size; j++) {
				double y = rad - j;
				kernel[i][j] = (float) (scale * Math.exp(-0.5 * (x * x + y * y)	/ sigma2));
			}
		}
		return kernel;
	}
	
	public static float[][] makeGaussKernel2d(double sigmaX, double sigmaY){
		int radX = (int) (3.5 * sigmaX);
		int radY = (int) (3.5 * sigmaY);
		int sizeX = radX + radX + 1;
		int sizeY = radY + radY + 1;

		float[][] kernel = new float[sizeX][sizeY]; //center cell = kernel[rad][rad]
		double sigmaX2 = (sigmaX > 0.1) ? sigmaX * sigmaX : 0.1;
		double sigmaY2 = (sigmaY > 0.1) ? sigmaY * sigmaY : 0.1;
		
		double sum = 0;
		for (int i = 0; i < sizeX; i++) {
			double x = radX - i;
			for (int j = 0; j < sizeY; j++) {
				double y = radY - j;
				// IJ.log("x = " + x + " / " + "y = " + y);
				double g = (float) Math.exp(-((x * x) / (2 * sigmaX2) + (y * y) / (2 * sigmaY2)));
				// IJ.log("g = " + g);
				kernel[i][j] = (float) g;
				sum = sum + g;
			}
		}

		// normalize the kernel to sum 1
		double scale = 1 / sum;
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				kernel[i][j] = (float) (kernel[i][j] * scale);
			}
		}
		return kernel;
	}

}
