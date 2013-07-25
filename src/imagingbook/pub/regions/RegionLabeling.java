/*******************************************************************************
 * This source code is made available as supplement to the printed textbooks on 
 * 'Digital Image Processing', authored by Wilhelm Burger and Mark J. Burge and 
 * published by Springer-Verlag. Note that this code comes with absolutely no 
 * warranty of any kind and the authors reserve the right to make changes to 
 * the code without notice at any time. See http://www.imagingbook.com for 
 * details and licensing conditions. Last update: 2013.
 ******************************************************************************/

package imagingbook.pub.regions;

import ij.IJ;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class does the complete region labeling for a given image.
 * It is abstract, because the implementation of some parts depend
 * upon the region labeling algorithm being used.
 * Updated: 2010-11-19
 */

public abstract class RegionLabeling {
	
	static final int BACKGROUND = 0;
	static final int FOREGROUND = 1;
	static final int START_LABEL = 2;

	protected ImageProcessor ip;
	protected int width;
	protected int height;
	protected int currentLabel;
	protected int maxLabel;	// the maximum label in the labels array
	
	protected int[][] labelArray;
	// label values in labelArray can be:
	//  0 ... unlabeled
	// -1 ... previously visited background pixel
	// >0 ... valid label
	
	protected List<BinaryRegion> regions;
	
	RegionLabeling(ByteProcessor ip) {
		this.ip = ip;
		width  = ip.getWidth();
		height = ip.getHeight();
		initialize();
		applyLabeling();
		collectRegions();
	}
	
	void initialize() {
		// set all pixels to either FOREGROUND or BACKGROUND (by thresholding)
		labelArray = new int[width][height];
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				labelArray[u][v] = (ip.getPixel(u, v) > 0) ? FOREGROUND : BACKGROUND;
			}
		}
	}
	
	public List<BinaryRegion> getRegions() {
		return getRegions(false);	// unsorted
	}
	
	public List<BinaryRegion> getRegions(boolean sort) {
		if (regions == null) 
			return Collections.emptyList();
		else {
			List<BinaryRegion> rns = new ArrayList<BinaryRegion>(regions);
			if (sort) {
				Collections.sort(rns);
			}
			return rns;
		}
	}
	
	// This method must be implemented by any real sub-class:
	abstract void applyLabeling();
	
	// creates a container of BinaryRegion objects
	// collects the region pixels from the label image
	// and computes the statistics for each region
	void collectRegions() {
		BinaryRegion[] regionArray = new BinaryRegion[maxLabel + 1];
		for (int i = START_LABEL; i <= maxLabel; i++) {
			regionArray[i] = new BinaryRegion(i);
		}
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int label = getLabel(u, v);
				if (label >= START_LABEL && label <= maxLabel && regionArray[label] != null) {
					regionArray[label].addPixel(u, v);
				}
			}
		}
		// create a list of regions to return, collect nonempty regions
		List<BinaryRegion> regionList = new LinkedList<BinaryRegion>();
		for (BinaryRegion r: regionArray) {
			if (r != null && r.getSize() > 0) {
				r.update();	// compute the statistics for this region
				regionList.add(r);
			}
		}
		regions = regionList;
	}
	
	public int getLabel(int u, int v) {
		if (u >= 0 && u < width && v >= 0 && v < height)
			return labelArray[u][v];
		else
			return BACKGROUND;
	}
	
	void setLabel(int u, int v, int label) {
		if (u >= 0 && u < width && v >= 0 && v < height)
			labelArray[u][v] = label;
	}
	
	void resetLabel() {
		currentLabel = -1;
		maxLabel = -1;
	}
	
	int getNextLabel() {
		if (currentLabel < 1)
			currentLabel = START_LABEL;
		else
			currentLabel = currentLabel + 1;
		maxLabel = currentLabel;
		return currentLabel;
	}
	
	int getMaxLabel() {
		return maxLabel;
	}
	
	// --------------------------------------------------
	
	public ImageProcessor makeLabelImage(boolean color) {
		return (color) ?  makeLabelImageColor() : makeLabelImageGray();
	}

	ColorProcessor makeLabelImageColor() {
		int[] colorLUT = new int[maxLabel+1];
		for (int i = START_LABEL; i <= maxLabel; i++) {
			colorLUT[i] = makeRandomColor();
		}
		ColorProcessor cp = new ColorProcessor(width, height);
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int lb = getLabel(u, v);
				if (lb >= 0 && lb < colorLUT.length) {
					cp.putPixel(u, v, colorLUT[lb]);
				}
			}
		}
		return cp;
	}
	
	ShortProcessor makeLabelImageGray() {
		ShortProcessor sp = new ShortProcessor(width, height);
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int lb = getLabel(u, v);
				sp.set(u, v, (lb >= 0) ? lb : 0);
			}
		}
		sp.resetMinAndMax();
		return sp;
	}
	
	// Find the region object with the given label:
	public BinaryRegion findRegion(int label) {
		if (regions == null) return null;
		BinaryRegion regn = null;
		for (BinaryRegion r : regions) {
			if (r.getLabel() == label) {
				regn = r;
				break;
			}
		}
		return regn;
	}
	
	
	/*
	 * utility methods
	 */

	int makeRandomColor() {
		double saturation = 0.2;
		double brightness = 0.2;
		float h = (float) Math.random();
		float s = (float) (saturation * Math.random() + 1 - saturation);
		float b = (float) (brightness * Math.random() + 1 - brightness);
		return Color.HSBtoRGB(h, s, b);
	}
	
	void printSummary() {
		if (regions != null) {
			IJ.log("Number of regions detected: " + regions.size());
			for (BinaryRegion r : regions) {
				IJ.log(r.toString());
			}
		} else
			IJ.log("No regions found.");
	}

}
