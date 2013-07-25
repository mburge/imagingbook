/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.colorimage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;

import java.awt.color.ColorSpace;

/**
 * This class contains only static methods for handling color images represented as 
 * multi-plane image stacks.
 * The 3 components in all these images are assumed to be in [0,1].
 */
public abstract class ColorStack { //extends ImageStack {
	
	public enum ColorStackType {
		RGB("R", "G", "B"), 
		sRGB("sR", "sG", "sB"), 
		Lab("L", "a", "b"), 
		Luv("L", "u", "v"),
//		XYZ("X", "Y", "Z"), 		// not currently implemented
//		YCbCr("Y", "Cb", "Cr"), 	// not currently implemented
		;

		protected final String[] componentLabels;

		ColorStackType(String... labels) {
			this.componentLabels = labels;
		}
	}
	
//	public static final String ColorStackTypeName = ColorStackType.class.getSimpleName();
	
	/**
	 *This static method creates a 3-slice float-stack from a RGB image
	 * @param imp the source (RGB) image.
	 * @return an image stack consisting of 3 slices of type float.
	 */
	public static ImagePlus createFrom(ImagePlus imp) {
		if (imp.getType() != ImagePlus.COLOR_RGB)
			return null;
		ColorProcessor cp = (ColorProcessor) imp.getProcessor();
		int width = cp.getWidth();
		int height = cp.getHeight();
		ImageStack stack = new ImageStack(width, height);

		FloatProcessor rp = new FloatProcessor(width, height);
		FloatProcessor gp = new FloatProcessor(width, height);
		FloatProcessor bp = new FloatProcessor(width, height);
			
		int[] pixels = (int[]) cp.getPixels(); 
		float[] rf = (float[]) rp.getPixels();
		float[] gf = (float[]) gp.getPixels();
		float[] bf = (float[]) bp.getPixels();
    	for (int i=0; i<pixels.length; i++) {
            int c = pixels[i];
            int r = (c&0xff0000)>>16;
    		int g = (c&0xff00)>>8;
    		int b = c&0xff;
    		rf[i] = r / 255f;
    		gf[i] = g / 255f;
    		bf[i] = b / 255f;
    	}
		stack.addSlice(rp);
		stack.addSlice(gp);
		stack.addSlice(bp);
    	ImagePlus cimp = new ImagePlus(imp.getTitle(), stack);
    	//cimp.setProperty(ColorStackType.class.getSimpleName(), ColorStackType.sRGB);
    	setType(cimp, ColorStackType.sRGB);
		return cimp;
	}
	
	public static void setType(ImagePlus imp, ColorStackType type) {
		ImageStack stack = imp.getImageStack();
		if (stack == null)
			return;
		for (int i = 1; i <= 3; i++) {
			stack.setSliceLabel(type.componentLabels[i - 1], i);
		}
	}
	
	public static ColorStackType getType(ImagePlus imp) {
		if (!isColorStack(imp)) return null;
		for (ColorStackType types : ColorStackType.values()) {
			if (isType(imp, types)) {
				return types;
			}
		}
		return null;
	}
	
	public static boolean isType(ImagePlus imp, ColorStackType type) {
		if (!isColorStack(imp)) return false;
		String[] labels = imp.getImageStack().getSliceLabels();
		return 
			type.componentLabels[0].equals(labels[0]) &&
			type.componentLabels[1].equals(labels[1]) &&
			type.componentLabels[2].equals(labels[2]) ;
//		Object prop = imp.getProperty(ColorStackTypeName);
//		return (prop.toString().equals(type.toString()));
	}
	
	public static boolean isColorStack(ImagePlus imp) {	
		// calling only getStackSize() avoids a call to getImageStack()
		return (imp != null && imp.getImageStackSize() == 3 
				&&	(imp.getImageStack().getProcessor(1) instanceof FloatProcessor)
//				&& 	(imp.getImageStack().getPixels(1) instanceof float[])
				);
	}
	
	public static FloatProcessor[] getProcessors(ImagePlus imp) {
		if (isColorStack(imp)) {
			ImageStack stack = imp.getImageStack();
			int n = stack.getSize();
			FloatProcessor[] processors = new FloatProcessor[n];
			for (int i = 0; i < n; i++) {
				processors[i] = (FloatProcessor) stack.getProcessor(i + 1);
			}
			return processors;
		}
		else {
			return null;
		}
	}
	
	//---------------------------------------------------------
	
	public static void toSrgb(ImagePlus colstck) {
		 ColorStackType cst = getType(colstck);
		 if (cst == null) {
			 IJ.error("Color stack is in unknown color space.");
			 return;
		 }
		 switch (cst) {
		 	case Lab : 	ColorStack.labToSrgb(colstck); break;
		 	case Luv: 	ColorStack.luvToSrgb(colstck); break;
			case RGB: 	ColorStack.rgbToSrgb(colstck); break;
			case sRGB: 	break;	// colstck is in sRGB already, nothing to do
		 }
	}
	
	public static ImagePlus toColorImage(ImagePlus colstckimg) {
		assert isColorStack(colstckimg);
		ImageStack stack = colstckimg.getImageStack();
		if (stack == null) 
			return null;
		float[] rPix = (float[]) stack.getPixels(1);
		float[] gPix = (float[]) stack.getPixels(2);
		float[] bPix = (float[]) stack.getPixels(3);
		ColorProcessor cp = new ColorProcessor(stack.getWidth(), stack.getHeight());
		int[] srgbPix = (int[]) cp.getPixels();
		for (int i = 0; i < srgbPix.length; i++) {
            int r = Math.round(rPix[i] * 255);
    		int g = Math.round(gPix[i] * 255);
    		int b = Math.round(bPix[i] * 255);
    		if (r < 0) r = 0; else if (r > 255) r = 255;
    		if (g < 0) g = 0; else if (g > 255) g = 255;
    		if (b < 0) b = 0; else if (b > 255) b = 255;
    		srgbPix[i] = ((r & 0xff)<<16) | ((g & 0xff)<<8) | b & 0xff;
    	}
		return new ImagePlus(colstckimg.getTitle(), cp);
	}
	
	// sRGB <-> RGB --------------------------------------------------------
	
	public static void srgbToRgb(ImagePlus srgbImg) {
		assert isColorStack(srgbImg);
		ImageStack stack = srgbImg.getImageStack();
		for (int k = 1; k <= 3; k++) {
			float[] pixels = (float[]) stack.getPixels(k);		
			for (int i=0; i<pixels.length; i++) {
				float p = pixels[i];
				if (p < 0) p = 0f; else if (p > 1) p = 1f;
				pixels[i] = gammaInv(p);
			}
		}
		//srgbImg.setProperty(ColorStackTypeName, ColorStackType.RGB);
		setType(srgbImg, ColorStackType.RGB);
	}
	
	public static void rgbToSrgb(ImagePlus rgbImg) {
		assert isColorStack(rgbImg);
		ImageStack stack = rgbImg.getImageStack();
		for (int k = 1; k <= 3; k++) {
			float[] pixels = (float[]) stack.getPixels(k);		
			for (int i=0; i<pixels.length; i++) {
				float p = pixels[i];
				if (p < 0) p = 0f; else if (p > 1) p = 1f;
				pixels[i] = gammaFwd(p);
			}
		}
		//rgbImg.setProperty(ColorStackTypeName, ColorStackType.sRGB);
		setType(rgbImg, ColorStackType.sRGB);
	}
	
	// ---------------- numeric operations ---------------------------
	
	public static void multiply(ImagePlus rgbImg, double value) {
		assert isColorStack(rgbImg);
		ImageStack stack = rgbImg.getImageStack();
		for (int k = 1; k <= stack.getSize(); k++)  {
			stack.getProcessor(k).multiply(value);
		}
	}
	
	public static void convolve(ImagePlus rgbImg, float[] kernel, int w, int h) {
		assert isColorStack(rgbImg);
		ImageStack stack = rgbImg.getImageStack();
		for (int k = 1; k <= stack.getSize(); k++)  {
			stack.getProcessor(k).convolve(kernel, w, h);
		}
	}
	
	public static FloatProcessor max(ImagePlus rgbImg) { 
		assert isColorStack(rgbImg);
		ImageStack stack = rgbImg.getImageStack();
		FloatProcessor rp = (FloatProcessor) stack.getProcessor(1).duplicate();
		float[] rpix = (float[]) rp.getPixels();		
		for (int k = 2; k <= 3; k++) {
			float[] pixels = (float[]) stack.getPixels(k);		
			for (int i=0; i<pixels.length; i++) {
				float p = pixels[i];
				if (p > rpix[i]) rpix[i] = p;
			}
		}
		return rp;
	}
	
	public static FloatProcessor magL2(ImagePlus rgbImg) { 
		assert isColorStack(rgbImg);
		//final double scale = 1/Math.sqrt(3);
		ImageStack stack = rgbImg.getImageStack();
		FloatProcessor rp = (FloatProcessor) stack.getProcessor(1).duplicate();
		float[] rpix = (float[]) rp.getPixels();	
		float[] pixels1 = (float[]) stack.getPixels(1);
		float[] pixels2 = (float[]) stack.getPixels(2);
		float[] pixels3 = (float[]) stack.getPixels(3);
		
		for (int i=0; i<pixels1.length; i++) {
			double p1 = pixels1[i];
			double p2 = pixels2[i];
			double p3 = pixels3[i];
			//rpix[i] = (float) (scale * Math.sqrt(p1*p1 + p2*p2 + p3*p3));
			rpix[i] = (float) Math.sqrt(p1*p1 + p2*p2 + p3*p3);
		}
		return rp;
	}
	
	public static FloatProcessor magL1(ImagePlus rgbImg) { 
		assert isColorStack(rgbImg);
		//final float scale = 1;
		ImageStack stack = rgbImg.getImageStack();
		FloatProcessor rp = (FloatProcessor) stack.getProcessor(1).duplicate();
		float[] rpix = (float[]) rp.getPixels();	
		float[] pixels1 = (float[]) stack.getPixels(1);
		float[] pixels2 = (float[]) stack.getPixels(2);
		float[] pixels3 = (float[]) stack.getPixels(3);
		
		for (int i = 0; i < pixels1.length; i++) {
			double p1 = pixels1[i];
			double p2 = pixels2[i];
			double p3 = pixels3[i];
			rpix[i] = (float) (Math.abs(p1) + Math.abs(p2) + Math.abs(p3));
		}
		return rp;
	}
	
    static float gammaFwd(float lc) {	// input: linear component value
		return (lc > 0.0031308) ?
			(float) (1.055 * Math.pow(lc, 1/2.4f) - 0.055) :
			(lc * 12.92f);
    }
    
    static float gammaInv(float nc) {	// input: nonlinear component value
    	return (nc > 0.03928) ?
			(float) Math.pow((nc + 0.055)/1.055, 2.4) :
			(nc / 12.92f);
    }
    
    // sRGB <-> Lab -----------------------------------------------------
    
	public static void srgbToLab(ImagePlus srgbImg) {
		assert isColorStack(srgbImg);
		ColorSpace lcs = new LabColorSpace2();
		ImageStack stack = srgbImg.getImageStack();
		
		float[] rPix = (float[]) stack.getPixels(1);
		float[] gPix = (float[]) stack.getPixels(2);
		float[] bPix = (float[]) stack.getPixels(3);
		
		float[] srgb = new float[3];
		for (int i = 0; i < rPix.length; i++) {
			float rp = rPix[i];
			float gp = gPix[i];
			float bp = bPix[i];
			if (rp < 0) rp = 0; else if (rp > 1) rp = 1;
			if (gp < 0) gp = 0; else if (gp > 1) gp = 1;
			if (bp < 0) bp = 0; else if (bp > 1) bp = 1;
			srgb[0] = rp; srgb[1] = gp; srgb[2] = bp; 
			float[] lab = lcs.fromRGB(srgb);
			rPix[i] = lab[0];
			gPix[i] = lab[1];
			bPix[i] = lab[2];
		}
		setType(srgbImg, ColorStackType.Lab);
	}
	
	public static void labToSrgb(ImagePlus labImg) {
		assert isColorStack(labImg);
		LabColorSpace2 lcs = new LabColorSpace2();
		ImageStack stack = labImg.getImageStack();
		
		float[] lPix = (float[]) stack.getPixels(1);
		float[] aPix = (float[]) stack.getPixels(2);
		float[] bPix = (float[]) stack.getPixels(3);
		
		float[] lab = new float[3];
		for (int i = 0; i < lPix.length; i++) {
			lab[0] = lPix[i]; lab[1] = aPix[i]; lab[2] = bPix[i]; 
			float[] srgb = lcs.toRGB(lab);
			float rp = srgb[0];
			float gp = srgb[1];
			float bp = srgb[2];
			if (rp < 0) rp = 0; else if (rp > 1) rp = 1;
			if (gp < 0) gp = 0; else if (gp > 1) gp = 1;
			if (bp < 0) bp = 0; else if (bp > 1) bp = 1;
			lPix[i] = rp; // R
			aPix[i] = gp; // G
			bPix[i] = bp; // B
		}
		setType(labImg, ColorStackType.sRGB);
	}
   
    // sRGB <-> Luv -----------------------------------------------------
    
	public static void srgbToLuv(ImagePlus srgbImg) {
		assert isColorStack(srgbImg);
		LuvColorSpace lcs = new LuvColorSpace();
		ImageStack stack = srgbImg.getImageStack();
		
		float[] rPix = (float[]) stack.getPixels(1);
		float[] gPix = (float[]) stack.getPixels(2);
		float[] bPix = (float[]) stack.getPixels(3);
		
		float[] srgb = new float[3];
		for (int i = 0; i < rPix.length; i++) {
			float rp = rPix[i];
			float gp = gPix[i];
			float bp = bPix[i];
			if (rp < 0) rp = 0; else if (rp > 1) rp = 1;
			if (gp < 0) gp = 0; else if (gp > 1) gp = 1;
			if (bp < 0) bp = 0; else if (bp > 1) bp = 1;
			srgb[0] = rp; srgb[1] = gp; srgb[2] = bp; 
			float[] lab = lcs.fromRGB(srgb);
			rPix[i] = lab[0];
			gPix[i] = lab[1];
			bPix[i] = lab[2];
		}
		setType(srgbImg, ColorStackType.Luv);
	}
	
	public static void luvToSrgb(ImagePlus luvImg) {
		assert isColorStack(luvImg);
		ColorSpace lcs = new LuvColorSpace();
		ImageStack stack = luvImg.getImageStack();
		
		float[] lPix = (float[]) stack.getPixels(1);
		float[] aPix = (float[]) stack.getPixels(2);
		float[] bPix = (float[]) stack.getPixels(3);
		
		float[] lab = new float[3];
		for (int i = 0; i < lPix.length; i++) {
			lab[0] = lPix[i]; lab[1] = aPix[i]; lab[2] = bPix[i]; 
			float[] srgb = lcs.toRGB(lab);
			float rp = srgb[0];
			float gp = srgb[1];
			float bp = srgb[2];
			if (rp < 0) rp = 0; else if (rp > 1) rp = 1;
			if (gp < 0) gp = 0; else if (gp > 1) gp = 1;
			if (bp < 0) bp = 0; else if (bp > 1) bp = 1;
			lPix[i] = rp; // R
			aPix[i] = gp; // G
			bPix[i] = bp; // B
		}
		setType(luvImg, ColorStackType.sRGB);
	}
    
}
