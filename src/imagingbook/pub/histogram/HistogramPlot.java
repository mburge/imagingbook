/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.histogram;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ImageProcessor;
import imagingbook.pub.histogram.HistogramPlot;
import imagingbook.pub.histogram.PiecewiseLinearCdf;
import imagingbook.pub.histogram.Util;

public class HistogramPlot {
	// TODO: make HistogramPlot extend ImagePlus
	
	static final int BACKGROUND = 255;

    int width =  256;
    int height = 128;
    int base = height-1;
    int paintValue = 0;
	ImagePlus hist_img;
	ImageProcessor ip;
	int[] H = new int[256];
	
	public HistogramPlot(int[] h, String title) {
		this(Util.normalizeHistogram(h), title);
	}
	
	public HistogramPlot(double[] nH, String title) {
		createHistogramImage(title);
		// nH must be a normalized histogram of length 256
		for (int i = 0; i < nH.length; i++) {
			H[i] = (int) Math.round(height * nH[i]);
		}
		draw();
		//show();
	}
	
	public HistogramPlot(PiecewiseLinearCdf cdf, String title) {
		// TODO: needed?
		createHistogramImage(title);
		// nH must be a normalized histogram of length 256
		for (int i = 0; i < 256; i++) {
			H[i] = (int) Math.round(height * cdf.getCdf(i));
		}
		draw();
		//show();
	}
	
	void createHistogramImage(String title) {
		if (title == null)
			title = "Histogram Plot";
		hist_img  = NewImage.createByteImage(title,width,height,1,0);
		ip = hist_img.getProcessor();
        ip.setValue(BACKGROUND);
        ip.fill();
	}
	
	void draw() {
		ip.setValue(0);
		ip.drawLine(0,base,width-1,base);
		ip.setValue(paintValue);
		int u = 0;
		for (int i=0; i<H.length; i++) {
			int k = H[i];
			if (k > 0){
			ip.drawLine(u, base-1, u, base-k);
			}
			u = u + 1;
		}
	}
	
	void update() {
		hist_img.updateAndDraw();
	}
	
	public void show() {
		hist_img.show();
        update();
	}
	
	void makeRamp() {
		for (int i = 0; i < H.length; i++) {
			H[i] = i;
		}
	}
	
	void makeRandom() {
		for (int i = 0; i < H.length; i++) {
			H[i] = (int)(Math.random() * height);
		}
	}
	
    //----- static methods ----------------------
	
//    public static void showHistogram(ImageProcessor ip, String title) {
//		int[] Ha = ip.getHistogram();
//		HistogramPlot hp = new HistogramPlot(Util.normalizeHistogram(Ha), title);
//		hp.show();
//	}

//	public static void showCumHistogram(ImageProcessor ip, String title) {
//		int[] Ha = ip.getHistogram();
//		HistogramPlot hp = new HistogramPlot(Util.Cdf(Ha), title);
//		hp.show();
//	}

}
