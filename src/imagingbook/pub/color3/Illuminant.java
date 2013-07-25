/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.color3;

public enum Illuminant {
	E	(1/3.0, 1/3.0),						// 5400K, Equal energy
	D50	(0.964296, 1.00000, 0.825100),		// 5000K
//	D50	(0.34773, 0.35952 ),				// 5000K
	D55 (0.33411, 0.34877),					// 5500K
//	D65 (0.31382, 0.33100),					// 6500K, Television, sRGB color space (correct?
//	D65 (0.31271, 0.32902),					// 6500K, CIE!
	D65 (0.950456, 1.00000, 1.088754),		// 6500K, Television, sRGB color space
	D75 (0.29968, 0.31740),					// 7500K
	A	(0.45117, 0.40594),					// 2856K, Incandescent tungsten
	B	(0.3498, 0.3527),					// 4874K, Obsolete, direct sunlight at noon 
	C	(0.31039, 0.31905),					// 6774K, Obsolete, north sky daylight 
	F2	(0.37928, 0.36723),					// 4200K, Cool White Fluorescent (CWF)
	F7	(0.31565, 0.32951),					// 6500K,  Broad-Band Daylight Fluorescent 
	F11	(0.38543, 0.37110)					// 4000K,  Narrow Band White Fluorescent 
	;
	
	public final double X, Y, Z;

	private Illuminant(double X, double Y, double Z) {
		this.X = X; this.Y = Y; this.Z = Z;
	}
	
	private Illuminant(double x, double y) {
		Y = 1.0;
		X = x * Y / y; 
		Z = (1.0 - x - y) * Y / y;
	}
	
	public float[] getXyzFloat() {
		return new float[] {(float)X, (float)Y, (float)Z};
	}
	
}
