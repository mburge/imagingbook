/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.color2;


import ij.process.ByteProcessor;
import ij.process.ColorProcessor;

import java.awt.image.IndexColorModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import imagingbook.pub.color1.ColorHistogram;

/*
 * This is an implementation of Heckbert's median-cut color quantization algorithm 
 * (Heckbert P., "Color Image Quantization for Frame Buffer Display", ACM Transactions
 * on Computer Graphics (SIGGRAPH), pp. 297-307, 1982).
 * Unlike in the original algorithm, no initial uniform (scalar) quantization is used to
 * for reducing the number of image colors. Instead, all colors contained in the original
 * image are considered in the quantization process. After the set of representative
 * colors has been found, each image color is mapped to the closest representative
 * in RGB color space using the Euclidean distance.
 * The quantization process has two steps: first a ColorQuantizer object is created from
 * a given image using one of the constructor methods provided. Then this ColorQuantizer
 * can be used to quantize the original image or any other image using the same set of 
 * representative colors (color table).
 */

public class MedianCutQuantizer implements ColorQuantizer {
	
	private ColorNode[] imageColors = null;	// original (unique) image colors
	private ColorNode[] quantColors = null;	// quantized colors

	public MedianCutQuantizer(ColorProcessor ip, int Kmax) {
		this((int[]) ip.getPixels(), Kmax);
	}
	
	public MedianCutQuantizer(int[] pixels, int Kmax) {
		quantColors = findRepresentativeColors(pixels, Kmax);
	}
	
	public int countQuantizedColors() {
		return quantColors.length;
	}
	
	public ColorNode[] getQuantizedColors() {
		return quantColors;
	}

	ColorNode[] findRepresentativeColors(int[] pixels, int Kmax) {
		ColorHistogram colorHist = new ColorHistogram(pixels);
		int K = colorHist.getNumberOfColors();
		ColorNode[] rCols = null;

		imageColors = new ColorNode[K];
		for (int i = 0; i < K; i++) {
			int rgb = colorHist.getColor(i);
			int cnt = colorHist.getCount(i);
			imageColors[i] = new ColorNode(rgb, cnt);
		}

		if (K <= Kmax) // image has fewer colors than Kmax
			rCols = imageColors;
		else {
			ColorBox initialBox = new ColorBox(0, K-1, 0);
			List<ColorBox> colorSet = new ArrayList<ColorBox>();
			colorSet.add(initialBox);
			int k = 1;
			boolean done = false;
			while (k < Kmax && !done) {
				ColorBox nextBox = findBoxToSplit(colorSet);
				if (nextBox != null) {
					ColorBox newBox = nextBox.splitBox();
					colorSet.add(newBox);
					k = k + 1;
				} else {
					done = true;
				}
			}
			rCols = averageColors(colorSet);
		}
		return rCols;
	}
	
	public int[] quantizeImage(int[] origPixels) {
		int[] qantPixels = origPixels.clone();
		for (int i = 0; i < origPixels.length; i++) {
			ColorNode color = findClosestColor(origPixels[i]);
			qantPixels[i] = color.rgb;
		}
		return qantPixels;
	}
	
	public ByteProcessor quantizeImage(ColorProcessor cp) {
		if (countQuantizedColors() > 256) 
			throw new Error("cannot index to more than 256 colors");
		int w = cp.getWidth();
		int h = cp.getHeight();
		int[] origPixels = (int[]) cp.getPixels();
		byte[] idxPixels = new byte[origPixels.length];

		for (int i = 0; i < origPixels.length; i++) {
			idxPixels[i] = (byte) findClosestColorIndex(origPixels[i]);
		}

		IndexColorModel idxCm = makeIndexColorModel();
		return new ByteProcessor(w, h, idxPixels, idxCm);
	}
	
	IndexColorModel makeIndexColorModel() {
		int nColors = countQuantizedColors();
		byte[] rMap = new byte[nColors];
		byte[] gMap = new byte[nColors];
		byte[] bMap = new byte[nColors];
		for (int i=0; i<nColors; i++) {
			rMap[i] = (byte) quantColors[i].red;
			gMap[i] = (byte) quantColors[i].grn;
			bMap[i] = (byte) quantColors[i].blu;
		}
		return new IndexColorModel(8, nColors, rMap, gMap, bMap);
	}
	
	ColorNode findClosestColor (int rgb) {
		int idx = findClosestColorIndex(rgb);
		return quantColors[idx];
	}
	
	int findClosestColorIndex (int rgb) {
		int red = ((rgb & 0xFF0000) >> 16);
		int grn = ((rgb & 0xFF00) >> 8);
		int blu =  (rgb & 0xFF);
		int minIdx = 0;
		int minDistance = Integer.MAX_VALUE;
		for (int i=0; i<quantColors.length; i++) {
			ColorNode color = quantColors[i];
			int d2 = color.distance2(red, grn, blu);
			if (d2 < minDistance) {
				minDistance = d2;
				minIdx = i;
			}
		}
		return minIdx;
	}
	
	private ColorBox findBoxToSplit(List<ColorBox> colorBoxes) {
		ColorBox boxToSplit = null;
		// from the set of splitable color boxes
		// select the one with the minimum level
		int minLevel = Integer.MAX_VALUE;
		for (ColorBox box : colorBoxes) {
			if (box.colorCount() >= 2) {	// box can be split
				if (box.level < minLevel) {
					boxToSplit = box;
					minLevel = box.level;
				}
			}
		}
		return boxToSplit;
	}
	
	private ColorNode[] averageColors(List<ColorBox> colorBoxes) {
		int n = colorBoxes.size();
		ColorNode[] avgColors = new ColorNode[n];
		int i = 0;
		for (ColorBox box : colorBoxes) {
			avgColors[i] = box.getAverageColor();
			i = i + 1;
		}
		return avgColors;
	}
	
	// -------------- class ColorNode -------------------------------------------

	class ColorNode {
		private int rgb;
		private int red, grn, blu;
		private int cnt;
		
		ColorNode (int rgb, int cnt) {
			this.rgb = (rgb & 0xFFFFFF);
			this.red = (rgb & 0xFF0000) >> 16;
			this.grn = (rgb & 0xFF00) >> 8;
			this.blu = (rgb & 0xFF);
			this.cnt = cnt;
		}
		
		ColorNode (int red, int grn, int blu, int cnt) {
			this.rgb = ((red & 0xff) << 16) | ((grn & 0xff) << 8) | blu & 0xff;
			this.red = red;
			this.grn = grn;
			this.blu = blu;
			this.cnt = cnt;
		}
		
		int distance2 (int red, int grn, int blu) {
			// returns the squared distance between (red, grn, blu)
			// and this color
			int dr = this.red - red;
			int dg = this.grn - grn;
			int db = this.blu - blu;
			return dr*dr + dg*dg + db*db;
		}
		
		public String toString() {
			String s = this.getClass().getSimpleName();
			s = s + " red=" + red + " green=" + grn + " blue=" + blu + " count=" + cnt;
			return s;
		}
	}
	
	// -------------- class ColorBox -------------------------------------------

	class ColorBox { 
		int lower = 0; 	// lower index into 'imageColors'
		int upper = -1; // upper index into 'imageColors'
		int level; 		// split level o this color box
		int count = 0; 	// number of pixels represented by thos color box
		int rmin, rmax;	// range of contained colors in red dimension
		int gmin, gmax;	// range of contained colors in green dimension
		int bmin, bmax;	// range of contained colors in blue dimension
		
		ColorBox(int lower, int upper, int level) {
			this.lower = lower;
			this.upper = upper;
			this.level = level;
			this.trim();
		}
		
		int colorCount() {
			return upper - lower;
		}
		
		void trim() {
			// recompute the boundaries of this color box
			rmin = 255;	rmax = 0;
			gmin = 255;	gmax = 0;
			bmin = 255;	bmax = 0;
			count = 0;		
			for (int i = lower; i <= upper; i++) {
				ColorNode color = imageColors[i];
				count = count + color.cnt;
				int r = color.red;
				int g = color.grn;
				int b = color.blu;
				if (r > rmax) rmax = r;
				if (r < rmin) rmin = r;
				if (g > gmax) gmax = g;
				if (g < gmin) gmin = g;
				if (b > bmax) bmax = b;
				if (b < bmin) bmin = b;
			}
		}
		
		// Split this color box at the median point along its 
		// longest color dimension
		ColorBox splitBox() {	
			if (this.colorCount() < 2)	// this box cannot be split
				return null;
			else {
				// find longest dimension of this box:
				ColorDimension dim = getLongestColorDimension();

				// find median along dim
				int med = findMedian(dim);

				// now split this box at the median return the resulting new
				// box.
				int nextLevel = level + 1;
				ColorBox newBox = new ColorBox(med + 1, upper, nextLevel);
				this.upper = med;
				this.level = nextLevel;
				this.trim();
				return newBox;
			}
		}
		
		// Find longest dimension of this color box (RED, GREEN, or BLUE)
		ColorDimension getLongestColorDimension() {
			int rLength = rmax - rmin;
			int gLength = gmax - gmin;
			int bLength = bmax - bmin;
			if (bLength >= rLength && bLength >= gLength)
				return ColorDimension.BLUE;
			else if (gLength >= rLength && gLength >= bLength)
				return ColorDimension.GREEN;
			else return ColorDimension.RED;
		}
				
		// Find the position of the median in RGB space along
		// the red, green or blue dimension, respectively.
		int findMedian(ColorDimension dim) {
			// sort color in this box along dimension dim:
			Arrays.sort(imageColors, lower, upper+1, dim.comparator);
			// find the median point:
			int half = count / 2;
			int nPixels, median;
			for (median = lower, nPixels = 0; median < upper; median++) {
				nPixels = nPixels + imageColors[median].cnt;
				if (nPixels >= half)
					break;
			}			
			return median;
		}
		
		ColorNode getAverageColor() {
			int rSum = 0;
			int gSum = 0;
			int bSum = 0;
			int n = 0;
			for (int i = lower; i <= upper; i++) {
				ColorNode ci = imageColors[i];
				int cnt = ci.cnt;
				rSum = rSum + cnt * ci.red;
				gSum = gSum + cnt * ci.grn;
				bSum = bSum + cnt * ci.blu;
				n = n + cnt;
			}
			double nd = n;
			int avgRed = (int) (0.5 + rSum / nd);
			int avgGrn = (int) (0.5 + gSum / nd);
			int avgBlu = (int) (0.5 + bSum / nd);
			return new ColorNode(avgRed, avgGrn, avgBlu, n);
		}

		public String toString() {
			String s = this.getClass().getSimpleName();
			s = s + " lower=" + lower + " upper=" + upper;
			s = s + " count=" + count + " level=" + level;
			s = s + " rmin=" + rmin + " rmax=" + rmax;
			s = s + " gmin=" + gmin + " gmax=" + gmax;
			s = s + " bmin=" + bmin + " bmax=" + bmax;
			s = s + " bmin=" + bmin + " bmax=" + bmax;
			return s;
		}
	}
	
	//	 ---  color dimensions ------------------------
	
	// The main purpose of this enumeration class is to associate the
	// color dimensions RED, GREEN, BLUE with the corresponding comparators.
	// Implementation uses anonymous classes.
	enum ColorDimension {
		RED (new Comparator<ColorNode>() {
			public int compare(ColorNode colA, ColorNode colB) {
				return colA.red - colB.red;
			}}), 
		GREEN (new Comparator<ColorNode>() {
			public int compare(ColorNode colA, ColorNode colB) {
				return colA.grn - colB.grn;
			}}), 
		BLUE (new Comparator<ColorNode>() {
			public int compare(ColorNode colA, ColorNode colB) {
				return colA.blu - colB.blu;
			}});

		public final Comparator<ColorNode> comparator;

		ColorDimension(Comparator<ColorNode> cmp) {
			this.comparator = cmp;
		}
	}
	
//	enum ColorDimension { 
//	RED   (new redComparator()), 
//	GREEN (new grnComparator()), 
//	BLUE  (new bluComparator());
//	
//	public final Comparator<ColorNode> comparator;
//	
//	ColorDimension (Comparator<ColorNode> cmp) {
//		this.comparator = cmp;
//	}
//}
//
//
//
//// --- color comparators used for sorting colors along different dimensions
//// ---
//
//static class redComparator implements Comparator<ColorNode> {
//	public int compare(ColorNode colA, ColorNode colB) {
//		return colA.red - colB.red;
//	}
//}
//
//static class grnComparator implements Comparator<ColorNode> {
//	public int compare(ColorNode colA, ColorNode colB) {
//		return colA.grn - colB.grn;
//	}
//}
//
//static class bluComparator implements Comparator<ColorNode> {
//	public int compare(ColorNode colA, ColorNode colB) {
//		return colA.blu - colB.blu;
//	}
//}
	
//	//-------- utility methods -----------
//	
//	void listColorNodes(){
//		int i = 0;
//		for (ColorNode color : quantColors) {
//			IJ.write("   color " + i + ": " + color.toString());
//			i++;
//		}
//	}
	
} //class MedianCut

