/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.color3;

public abstract class ChromaticAdaptation {
	
	protected float[] white1 = null;
	protected float[] white2 = null;

	// actual transformation of color coordinates.
	// XYZ1 are interpreted relative to white point W1.
	// Returns a new color adapted to white point W2.
	public abstract float[] apply (float[] XYZ1);
	
	protected ChromaticAdaptation() {
	}

	protected ChromaticAdaptation (float[] white1, float[] white2) {
		this.white1 = white1.clone();
		this.white2 = white2.clone();
	}
	
	protected ChromaticAdaptation(Illuminant illum1, Illuminant illum2) {
		this(illum1.getXyzFloat(), illum2.getXyzFloat());
	}

	public float[] getSourceWhite() {
		return white1;
	}
	
	public float[] getTargetWhite() {
		return white2;
	}

}
