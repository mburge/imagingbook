/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.moments;
import ij.process.ImageProcessor;

public class Moments {
	static final int BACKGROUND = 0;

	public static double moment(ImageProcessor ip, int p, int q) {
		double Mpq = 0.0;
		for (int v = 0; v < ip.getHeight(); v++) { 
			for (int u = 0; u < ip.getWidth(); u++) { 
				if (ip.getPixel(u,v) != BACKGROUND) {
					Mpq+= Math.pow(u, p) * Math.pow(v, q);
				}
			}
		}
		return Mpq;
	}
	
	public static double centralMoment(ImageProcessor ip, int p, int q) {
		double m00  = moment(ip, 0, 0);	// region area
		double xCtr = moment(ip, 1, 0) / m00;
		double yCtr = moment(ip, 0, 1) / m00;
		double cMpq = 0.0;
		for (int v = 0; v < ip.getHeight(); v++) { 
			for (int u = 0; u < ip.getWidth(); u++) {
				if (ip.getPixel(u,v) != BACKGROUND) { 
					cMpq+= Math.pow(u - xCtr, p) * Math.pow(v - yCtr, q);
				}
			}
		}
		return cMpq;
	}
	
	public static double normalCentralMoment(ImageProcessor ip, int p, int q) {
		double m00 = moment(ip, 0, 0);
		double norm = Math.pow(m00, (double)(p + q + 2) / 2);
		return centralMoment(ip, p, q) / norm;
	}
}

