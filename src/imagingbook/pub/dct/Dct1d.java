/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.dct;

public class Dct1d{

	double[] gG;
	
	Dct1d(int M){ //Constructor
		gG = new double[M];	//this is done only ONCE when object is created!
	}
	
	public double[] DCT(double[] g) {
		int M = g.length;
		double s = Math.sqrt(2.0 / M); //common scale factor
		double[] G = gG;
		for (int m = 0; m < M; m++) {
			double cm;
			if (m == 0)
				cm = 1.0 / Math.sqrt(2);
			else
				cm = 1.0;
			double sum = 0;
			for (int u = 0; u < M; u++) {
				double Phi = (Math.PI * (2 * u + 1) * m) / (2.0 * M);
				sum += g[u] * cm * Math.cos(Phi);
			}
			G[m] = s * sum;
		}
		return G;
	}
	
	public double[] iDCT(double[] G) {
		int M = G.length;
		double s = Math.sqrt(2.0 / M); //common scale factor
		double[] g = gG;
		for (int u = 0; u < M; u++) {
			double sum = 0;
			for (int m = 0; m < M; m++) {
				double cm;
				if (m == 0) cm = 1.0/Math.sqrt(2);
				else		cm = 1.0;
				double Phi = (Math.PI * (2 * u + 1) * m) / (2.0 * M);
				double cosPhi = Math.cos(Phi);
				sum += cm * G[m] * cosPhi;
			}
			g[u] = s * sum;
		}
		return g;
	}
	
}
