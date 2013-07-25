/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.coloredge;

import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import imagingbook.lib.math.Matrix;
import imagingbook.lib.math.VectorNorm;
import imagingbook.lib.math.VectorNorm.NormType;

/**
 * Monochromatic color edge detector, as described in UTICS Vol. 3, Alg. 4.1.
 * @author W. Burger
 * @version 2013/05/30
 */
public class MonochromaticEdgeDetector extends ColorEdgeDetector {
	
	int M;	// image width
	int N;	// image height
	ColorProcessor I;
	FloatProcessor Emag;	// edge magnitude map
	FloatProcessor Eort;	// edge orientation map

	public static class Parameters {
		public NormType norm = NormType.L2;
	}
	
	final Parameters params;
	
	// Sobel-kernels for x/y-derivatives:
    final float[] HxS = Matrix.multiply(1.0f/8, new float[] {
		-1, 0, 1,
        -2, 0, 2,
        -1, 0, 1
        });
    
    final float[] HyS = Matrix.multiply(1.0f/8, new float[] {
		-1, -2, -1,
		 0,  0,  0,
		 1,  2,  1
		 });
    
    final int R = 0, G = 1, B = 2;		// RGB channel indexes
	
    FloatProcessor[] Ix;
    FloatProcessor[] Iy;
 
	public MonochromaticEdgeDetector(ColorProcessor originalImage) {
		this(originalImage, new Parameters());
	}
	
	public MonochromaticEdgeDetector(ColorProcessor originalImage, Parameters params) {
		this.params = params;
		this.I = originalImage;
		setup();
		findEdges();
	}
	
	protected void setup() {
		M = this.I.getWidth();
		N = this.I.getHeight();
		Emag = new FloatProcessor(M, N);
		Eort = new FloatProcessor(M, N);
		Ix = new FloatProcessor[3];
		Iy = new FloatProcessor[3];
	}

	void findEdges() {
		for (int c = R; c <= B; c++) {
			Ix[c] =  getRgbFloatChannel(I, c);
			Iy[c] =  getRgbFloatChannel(I, c);
			Ix[c].convolve(HxS, 3, 3);
			Iy[c].convolve(HyS, 3, 3);
		}
		
		//VectorNorm vNorm = VectorNorm.create(params.norm);
		VectorNorm vNorm = params.norm.create();
		final double[] Ergb = new double[3];
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				float Rx = Ix[R].getf(u, v);	float Ry = Iy[R].getf(u, v);
				float Gx = Ix[G].getf(u, v);	float Gy = Iy[G].getf(u, v);
				float Bx = Ix[B].getf(u, v);	float By = Iy[B].getf(u, v);
				
				// calculate local edge magnitude:
				float Er2 = Rx * Rx + Ry * Ry;
				float Eg2 = Gx * Gx + Gy * Gy;
				float Eb2 = Bx * Bx + By * By;
				Ergb[0] = Er2;
				Ergb[1] = Eg2;
				Ergb[2] = Eb2;
				//float eMag = mag(Er2, Eg2, Eb2, vecNorm);
				float eMag = (float) vNorm.magnitude(Ergb);	// consider a magnitude() method for float[]
				Emag.setf(u, v, eMag);
				
				// find the maximum gradient channel:
				float e2max = Er2, cx = Rx, cy = Ry;
				if (Eg2 > e2max) {
					e2max = Eg2; cx = Gx; cy = Gy;
				}
				if (Eb2 > e2max) {
					e2max = Eb2; cx = Bx; cy = By;
				}
				
				// calculate edge orientation for the maximum channel:
				float eOrt = (float) Math.atan2(cy, cx);
				Eort.setf(u, v, eOrt);
			}
		}
	}

	
//	float mag(float er2, float eg2, float eb2, ColorDistanceNorm norm) {
//		double dist = 0;
//		switch (norm) {
//			case L1 : dist = Math.sqrt(er2) + Math.sqrt(eg2) + Math.sqrt(eb2); break;
//			case L2 : dist = Math.sqrt(er2*er2 + eg2*eg2 + eb2*eb2); break;
//			case Lmax : dist = Math.sqrt(Math.max(er2, (Math.max(eg2, eb2)))); break;
//		}
//		return (float) dist;
//	}
	
	public FloatProcessor getEdgeMagnitude() {
		return Emag;
	}

	public FloatProcessor getEdgeOrientation() {
		return Eort;
	}
	
}
