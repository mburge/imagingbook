/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.color3;
import java.awt.color.ColorSpace;

/*
 * This class implements a D65-based L*a*b* color space for the standard Java XYZ 
 * Profile Connection Space (PCS) which is relative to D50. The XYZ values returned 
 * by the method fromCIEXYZ() or passed to the method toCIEXYZ() are interpreted
 * relative to white point D50, so they can be used in the standard Java color
 * management workflow.
 */

@Deprecated // use LabColorSpace2
public class LabColorSpace extends ColorSpace {
	private static final long serialVersionUID = 1L;
	
	// D65 reference white point
	static final double Xref = Illuminant.D65.X; 	// 0.950456; 
	static final double Yref = Illuminant.D65.Y; 	// 1.000000;
	static final double Zref = Illuminant.D65.Z;	// 1.088754;
	
	// create 2 chromatic adaptation objects
	ChromaticAdaptation catD65toD50 = new BradfordAdaptation(Illuminant.D65, Illuminant.D50);
	ChromaticAdaptation catD50toD65 = new BradfordAdaptation(Illuminant.D50, Illuminant.D65);
	
	// sRGB color space used only in methods toRGB() and fromRGB()
	static final ColorSpace sRGBcs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	
	public LabColorSpace(){
		super(TYPE_Lab,3);
	}

	// XYZ->CIELab: returns CIE Lab values (relative to D65) 
	// from Java XYZ values (relative to D50)
	public float[] fromCIEXYZ(float[] XYZ50) {	
		// chromatic adaptation D50->D65
		
		float[] XYZ65 = catD50toD65.apply(XYZ50);	
		double xx = f1(XYZ65[0] / Xref);	
		double yy = f1(XYZ65[1] / Yref);
		double zz = f1(XYZ65[2] / Zref);
		
		float L = (float)(116 * yy - 16);
		float a = (float)(500 * (xx - yy));
		float b = (float)(200 * (yy - zz));
		return new float[] {L, a, b};
	}
	
	// CIELab->XYZ: returns D50-related XYZ values from 
	// D65-related Lab values
	public float[] toCIEXYZ(float[] Lab) {
		double yy = ( Lab[0] + 16 ) / 116;
		float X65 = (float) (Xref * f2(Lab[1] / 500 + yy));
		float Y65 = (float) (Yref * f2(yy));
		float Z65 = (float) (Zref * f2(yy - Lab[2] / 200));
		float[] XYZ65 = new float[] {X65, Y65, Z65};
		return catD65toD50.apply(XYZ65);
	}
	
	// Gamma correction (forward)
	double f1 (double c) {
		if (c > 0.008856)
			return Math.pow(c, 1.0 / 3);
		else
			return (7.787 * c) + (16.0 / 116);
	}
	
	// Gamma correction (inverse)
	double f2 (double c) {
		double c3 = Math.pow(c, 3.0);
		if (c3 > 0.008856)
			return c3;
		else
			return (c - 16.0 / 116) / 7.787;
	}

	//sRGB->CIELab
	public float[] fromRGB(float[] sRGB) {
		float[] XYZ50 = sRGBcs.toCIEXYZ(sRGB);
		return this.fromCIEXYZ(XYZ50);
	}
	
	//CIELab->sRGB
	public float[] toRGB(float[] Lab) {
		float[] XYZ50 = this.toCIEXYZ(Lab);
		return sRGBcs.fromCIEXYZ(XYZ50);
	}

}
