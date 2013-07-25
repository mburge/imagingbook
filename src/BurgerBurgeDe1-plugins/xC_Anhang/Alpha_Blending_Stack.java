package xC_Anhang;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.Blitter;
import ij.process.ByteBlitter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Alpha_Blending_Stack implements PlugInFilter {
	static int nFrames = 10;

	ImagePlus fgIm; // foreground image (chosen interactively)
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	public void run(ImageProcessor bgIp) {
		// bgIp = background image
		if (runDialog()) { // open dialog box (returns false if cancelled)
			int w = bgIp.getWidth();
			int h = bgIp.getHeight();

			// prepare foreground image
			ImageProcessor fgIp = fgIm.getProcessor().convertToByte(false);
			ImageProcessor fgTmpIp = bgIp.duplicate();

			// create image stack
			ImagePlus movie = NewImage.createByteImage("Movie", w, h, nFrames, 0); 
			ImageStack stack = movie.getStack();

			// loop over stack frames
			for (int i = 0; i < nFrames; i++) {
				// transparency of foreground image
				double iAlpha = 1.0 - (double) i / (nFrames - 1);
				ImageProcessor iFrame = stack.getProcessor(i + 1); 
				// copy background image to frame i
				iFrame.insert(bgIp, 0, 0);
				iFrame.multiply(iAlpha);

				// copy foreground image and make transparent
				fgTmpIp.insert(fgIp, 0, 0);
				fgTmpIp.multiply(1 - iAlpha);

				// add foreground image frame \Code{i}
				ByteBlitter blitter = new ByteBlitter((ByteProcessor) iFrame);
				blitter.copyBits(fgTmpIp, 0, 0, Blitter.ADD);
			}

			// display movie (image stack)
			movie.show();
		}
	}
	
	boolean runDialog() {
		// get list of open images
		int[] windowList = WindowManager.getIDList(); 
		if (windowList == null) {
			IJ.noImage(); 
			return false;
		}
		String[] windowTitles = new String[windowList.length];
		for (int i = 0; i < windowList.length; i++) {
			ImagePlus imp = WindowManager.getImage(windowList[i]); 
			if (imp != null)
				windowTitles[i] = imp.getShortTitle(); 
			else
				windowTitles[i] = "untitled";
		}
		GenericDialog gd = new GenericDialog("Alpha Blending"); 
		gd.addChoice("Foreground image:", 
		windowTitles, windowTitles[0]);
		gd.addNumericField("Frames:", nFrames, 0); 
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		else {
			int img2Index = gd.getNextChoiceIndex(); 
			fgIm = WindowManager.getImage(windowList[img2Index]);
			nFrames = (int) gd.getNextNumber(); 
			if (nFrames < 2)
				nFrames = 2;
			return true;
		}
	}

}
