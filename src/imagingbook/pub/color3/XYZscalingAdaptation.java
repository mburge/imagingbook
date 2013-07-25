/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.color3;

public class XYZscalingAdaptation extends ChromaticAdaptation {

	public XYZscalingAdaptation(float[] white1, float[] white2) {
		super(white1, white2);
	}
	
	public XYZscalingAdaptation(Illuminant illum1, Illuminant illum2) {
		this(illum1.getXyzFloat(), illum2.getXyzFloat());
	}

	public float[] apply (float[] XYZ1) {
		float[] W1 = this.white1;
		float[] W2 = this.white2;
		float[] XYZ2 = new float[3];
		XYZ2[0] = XYZ1[0] * W2[0] / W1[0];
		XYZ2[1] = XYZ1[1] * W2[1] / W1[1];
		XYZ2[2] = XYZ1[2] * W2[2] / W1[2];
		return XYZ2;
	}

}
