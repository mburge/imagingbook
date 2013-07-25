/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.sift.scalespace;

public class DogOctave extends ScaleOctave {

	// TODO: check correctness of bottom and top levels!!
	DogOctave(ScaleOctave Gp) {
		//super(0,0,0,0);
		super(Gp.p, Gp.Q, Gp.width, Gp.height, Gp.botLevelIndex, Gp.topLevelIndex-1);
		// create DoG octave
		for (int q = botLevelIndex; q <= topLevelIndex; q++) {
			ScaleLevel Dpq = differenceOfGaussians(Gp.getLevel(q+1), Gp.getLevel(q));
			this.setLevel(q, Dpq);
		}
	}
	
	public ScaleLevel differenceOfGaussians(ScaleLevel A, ScaleLevel B) {
		// A: Gaussian at level q+1
		// B: Gaussian at level q
		// C <-- A - B (scale the same as B)
		ScaleLevel C = B.duplicate();
		final float[] pixelsA = (float[]) A.getPixels();
		final float[] pixelsB = (float[]) B.getPixels();
		final float[] pixelsC = (float[]) C.getPixels();
		for (int i=0; i<pixelsA.length; i++) {
			pixelsC[i] = pixelsA[i] - pixelsB[i];
		}
		C.setAbsoluteScale(B.getAbsoluteScale());
		return C;
	}
	


}
