/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.colorimage;

import java.awt.color.ColorSpace;

import imagingbook.pub.color3.Illuminant;

/*
 * This class implements a D65-based L*a*b* color space without performing
 * chromatic adaptation between D50 and D65, as required by Java's profile 
 * connection space. Everything is D65.
 */

public class LabColorSpace2 extends ColorSpace {
	private static final long serialVersionUID = 1L;
	
	// D65 reference white point
	public static final double Xref = Illuminant.D65.X; 	// 0.950456; 
	public static final double Yref = Illuminant.D65.Y; 	// 1.000000;
	public static final double Zref = Illuminant.D65.Z;		// 1.088754;
	
	public static final double epsilon = 216.0/24389;
	public static final double kappa = 841.0/108;
	
	// create 2 chromatic adaptation objects
//	ChromaticAdaptation catD65toD50 = new BradfordAdaptation(Illuminant.D65, Illuminant.D50);
//	ChromaticAdaptation catD50toD65 = new BradfordAdaptation(Illuminant.D50, Illuminant.D65);
	
	// sRGB color space used only in methods toRGB() and fromRGB()
//	static final ColorSpace sRGBcs = ColorSpace.getInstance(CS_sRGB);
	
	public LabColorSpace2(){
		super(TYPE_Lab,3);
	}

	// XYZ->CIELab: returns CIE Lab values (relative to D65) 
	// from Java XYZ values (relative to D65)
	public float[] fromCIEXYZ(float[] XYZ65) {	
		// NO chromatic adaptation D50->D65
		// float[] XYZ65 = catD50toD65.apply(XYZ50);	
		double xx = f1(XYZ65[0] / Xref);	
		double yy = f1(XYZ65[1] / Yref);
		double zz = f1(XYZ65[2] / Zref);
		float L = (float)(116.0 * yy - 16.0);
		float a = (float)(500.0 * (xx - yy));
		float b = (float)(200.0 * (yy - zz));
		return new float[] {L, a, b};
	}
	
	// CIELab->XYZ: returns D65-related XYZ values from 
	// D65-related Lab values
	public float[] toCIEXYZ(float[] Lab) {
		double ll = ( Lab[0] + 16.0 ) / 116.0;
		float Y65 = (float) (Yref * f2(ll));
		float X65 = (float) (Xref * f2(ll + Lab[1] / 500.0));
		float Z65 = (float) (Zref * f2(ll - Lab[2] / 200.0));
		return new float[] {X65, Y65, Z65};
		// return catD65toD50.apply(XYZ65);
	}
	
	// Gamma correction for L* (forward)
	double f1 (double c) {
		if (c > epsilon) // 0.008856
			return Math.cbrt(c);
		else
			return (kappa * c) + (16.0 / 116);
	}
	
	// Gamma correction for L* (inverse)
	double f2 (double c) {
		double c3 = c * c * c; //Math.pow(c, 3.0);
		if (c3 > epsilon)
			return c3;
		else
			return (c - 16.0 / 116) / kappa;
	}

	//sRGB->CIELab
	public float[] fromRGB(float[] srgb) {
		// get linear rgb components:
		double r = sRgbUtil.gammaInv(srgb[0]);
		double g = sRgbUtil.gammaInv(srgb[1]);
		double b = sRgbUtil.gammaInv(srgb[2]);
		
		// convert to XYZ (Poynton / ITU 709) 
		float X = (float) (0.412453 * r + 0.357580 * g + 0.180423 * b);
		float Y = (float) (0.212671 * r + 0.715160 * g + 0.072169 * b);
		float Z = (float) (0.019334 * r + 0.119193 * g + 0.950227 * b);
		
		float[] XYZ65 = new float[] {X, Y, Z}; 
		return this.fromCIEXYZ(XYZ65);
	}
	
	//CIELab->sRGB
	public float[] toRGB(float[] Lab) {
		float[] XYZ65 = this.toCIEXYZ(Lab);
		double X = XYZ65[0];
		double Y = XYZ65[1];
		double Z = XYZ65[2];
		// XYZ -> RGB (linear components)
		double r = (3.240479 * X + -1.537150 * Y + -0.498535 * Z);
		double g = (-0.969256 * X + 1.875992 * Y + 0.041556 * Z);
		double b = (0.055648 * X + -0.204043 * Y + 1.057311 * Z);
		
//		if (RgbGamutChecker.checkOutOfGamut(r, g, b) && RgbGamutChecker.markOutOfGamutColors) { // REMOVE ************************** !!	
//			r = RgbGamutChecker.oGred; 
//			g = RgbGamutChecker.oGgrn; 
//			b = RgbGamutChecker.oGblu;
//		}
		
		// RGB -> sRGB (nonlinear components)
		float rr = (float) sRgbUtil.gammaFwd(r);
		float gg = (float) sRgbUtil.gammaFwd(g);
		float bb = (float) sRgbUtil.gammaFwd(b);
				
		return new float[] {rr,gg,bb} ; //sRGBcs.fromCIEXYZ(XYZ50);
	}
	
	 //---------------------------------------------------------------------
	
	// double versions of Gamma correction
	
//    double gammaFwd(double lc) {	// input: linear component value
//		return (lc > 0.0031308) ?
//			(1.055 * Math.pow(lc, 1/2.4) - 0.055) :
//			(lc * 12.92);
//    }
//    
//    double gammaInv(double nc) {	// input: nonlinear component value
//    	return (nc > 0.03928) ?
//			Math.pow((nc + 0.055)/1.055, 2.4) :
//			(nc / 12.92);
//    }
    
    public static void main(String[] args) {
       	int sr = 128;
    	int sg = 1;
    	int sb = 128;
    	System.out.format("Input (sRGB) = %d, %d, %d\n", sr, sg, sb);
    	System.out.format("XYZref = %10g, %10g, %10g\n", Xref,Yref,Zref);
    	
    	ColorSpace cs = new LabColorSpace2();
    	//float[] luv = cs.fromCIEXYZ(new float[] {.1f,.5f,.9f});
    	float[] lab = cs.fromRGB(new float[] {sr/255f, sg/255f, sb/255f});

    	System.out.format("Lab = %8f, %8f, %8f\n", lab[0],lab[2],lab[2]);
    	//float[] xyz = cs.toCIEXYZ(luv);
    	float[] srgb = cs.toRGB(lab);
    	System.out.format("sRGB = %8f, %8f, %8f\n", 
    			Math.rint(255*srgb[0]), Math.rint(255*srgb[1]), Math.rint(255*srgb[2]));

    }
}
