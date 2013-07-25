/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.dft;

import imagingbook.lib.math.Complex;

public class Dft1d {

	double[] cosTable;
	double[] sinTable;
	Complex[] G;
	
	Dft1d(int M){ //Constructor
		makeCosTable(M);
		makeSinTable(M);
		G = new Complex[M];	//this is done only ONCE when object is created!
		for (int i = 0; i < M; i++) {
			G[i] = new Complex(0,0);
		}
	}
	
	void makeCosTable(int M){
		cosTable = new double[M];
		for (int i=0; i<M; i++){
			cosTable[i]= Math.cos(2*Math.PI*i/M);
		}
	}
	
	void makeSinTable(int M){
		sinTable = new double[M];
		for (int i=0; i<M; i++){
			sinTable[i]= Math.sin(2*Math.PI*i/M);
		}		
	}
	
	public Complex[] DFT(Complex[] g, boolean forward) {
		int M = g.length;
		double s = 1 / Math.sqrt(M); //common scale factor
		for (int u = 0; u < M; u++) {
			double sumRe = 0;
			double sumIm = 0;
			for (int m = 0; m < M; m++) {
				double gRe = g[m].re;
				double gIm = g[m].im;
				int k = (u * m) % M;
				double cosPhi = cosTable[k];
				double sinPhi = sinTable[k];
				if (forward)
					sinPhi = -sinPhi;
				//complex multiplication: (gRe + i gIm) * (cosPhi + i sinPhi)
				sumRe += gRe * cosPhi - gIm * sinPhi;
				sumIm += gRe * sinPhi + gIm * cosPhi;
			}
			G[u].re = s * sumRe;	
			G[u].im = s * sumIm;
		}
		return G;
	}
}
