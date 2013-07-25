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
 * This class implements a D65-based Luv color space without performing
 * chromatic adaptation between D50 and D65, as required by Java's profile 
 * connection space. Everything is D65.
 */

public class LuvColorSpace extends ColorSpace {
	private static final long serialVersionUID = 1L;
	
	// D65 reference white point
	public static final double Xref = Illuminant.D65.X; 	// 0.950456; 
	public static final double Yref = Illuminant.D65.Y; 	// 1.000000;
	public static final double Zref = Illuminant.D65.Z;		// 1.088754;
	
	public static final double epsilon = 216.0/24389;
	public static final double kappa = 841.0/108;
	
	static final double uuref = fu(Xref, Yref, Zref); // u'_n
	static final double vvref = fv(Xref, Yref, Zref); // v'_n
	
	// create 2 chromatic adaptation objects
//	ChromaticAdaptation catD65toD50 = new BradfordAdaptation(Illuminant.D65, Illuminant.D50);
//	ChromaticAdaptation catD50toD65 = new BradfordAdaptation(Illuminant.D50, Illuminant.D65);
	
	// sRGB color space used only in methods toRGB() and fromRGB()
	static final ColorSpace sRGBcs = ColorSpace.getInstance(CS_sRGB);
	
	public LuvColorSpace(){
		super(TYPE_Lab,3);
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
	
	static double fu (double x, double y, double z) { // x,y,z must all be positive
		if (x < 0.00001)	// fails if 0.001 is used!
			return 0;
		else
			return (4*x)/(x + 15*y + 3*z);
	}
	
	static double fv (double x, double y, double z) { // x,y,z must all be positive
		if (y < 0.00001)
			return 0;
		else
			return (9*y)/(x + 15*y + 3*z);
	}

	// XYZ->CIELuv: returns CIELuv values (relative to D65) 
	// from Java XYZ values (relative to D65)
	// http://en.wikipedia.org/wiki/CIELUV_color_space
	// http://de.wikipedia.org/wiki/CIELUV-Farbraumsystem - better!
	public float[] fromCIEXYZ(float[] XYZ65) {	
		// NO chromatic adaptation D50->D65
		// float[] XYZ65 = catD50toD65.apply(XYZ50);
		double X = XYZ65[0];
		double Y = XYZ65[1];	
		double Z = XYZ65[2];

		double YY = f1(Y / Yref);  	// Y'
		double uu = fu(X,Y,Z); 		// u'
		double vv = fv(X,Y,Z); 		// v'
		
		float L = (float)(116.0 * YY - 16.0); 		//L*
		float u = (float)(13 * L * (uu - uuref));  	//u*
		float v = (float)(13 * L * (vv - vvref));  	//v*
		return new float[] {L, u, v};
	}
	
	// CIELuv->XYZ: returns D65-related XYZ values from 
	// D65-related Luv values
	public float[] toCIEXYZ(float[] Luv) {
		double L = Luv[0];
		double u = Luv[1];
		double v = Luv[2];
		float Y = (float) (Yref * f2((L + 16) / 116.0));
		double uu = (L<0.00001) ? uuref : u / (13*L) + uuref; // u'
		double vv = (L<0.00001) ? vvref : v / (13*L) + vvref; // v'
		float X = (float) (Y * ((9*uu)/(4*vv)));
		float Z = (float) (Y * ((12 - 3*uu - 20*vv)/(4*vv)));
		float[] XYZ65 = new float[] {X, Y, Z};
		// return catD65toD50.apply(XYZ65);
		return XYZ65;
	}
	
	//sRGB->CIELuv
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
	
	//CIELuv->sRGB
	public float[] toRGB(float[] Luv) {
		float[] XYZ65 = this.toCIEXYZ(Luv);
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
	
    public static void main(String[] args) {
    	int sr = 128;
    	int sg = 1;
    	int sb = 128;
    	System.out.format("Input (sRGB) = %d, %d, %d\n", sr, sg, sb);
    	System.out.format("XYZref = %10g, %10g, %10g\n", Xref,Yref,Zref);
    	
    	LuvColorSpace cs = new LuvColorSpace();
    	//float[] luv = cs.fromCIEXYZ(new float[] {.1f,.5f,.9f});
    	float[] luv = cs.fromRGB(new float[] {sr/255f, sg/255f, sb/255f});

    	System.out.format("Luv = %8f, %8f, %8f\n", luv[0],luv[2],luv[2]);
    	//float[] xyz = cs.toCIEXYZ(luv);
    	float[] srgb = cs.toRGB(luv);
    	System.out.format("sRGB = %8f, %8f, %8f\n", 
    			Math.rint(255*srgb[0]), Math.rint(255*srgb[1]), Math.rint(255*srgb[2]));
    	
    }

}
