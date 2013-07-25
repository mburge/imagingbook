/*******************************************************************************
 * This source code is made available as supplement to the printed textbooks on 
 * 'Digital Image Processing', authored by Wilhelm Burger and Mark J. Burge and 
 * published by Springer-Verlag. Note that this code comes with absolutely no 
 * warranty of any kind and the authors reserve the right to make changes to 
 * the code without notice at any time. See http://www.imagingbook.com for 
 * details and licensing conditions. Last update: 2013.
 ******************************************************************************/

package imagingbook.pub.regions;

import ij.process.ByteProcessor;

public class RecursiveLabeling extends RegionLabeling {

	public RecursiveLabeling(ByteProcessor ip) {
		super(ip);
	}
	
	void applyLabeling() {
		resetLabel();
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				if (getLabel(u, v) >= START_LABEL) {
					// start a new region
					int label = getNextLabel();
					floodFill(u, v, label);
				}
			}
		}
	}

	public void floodFill(int up, int vp, int label) {
		if ((up>=0) && (up<width) && (vp>=0) && (vp<height) && getLabel(up, vp)>=START_LABEL) {
			setLabel(up, vp, label);
			floodFill(up + 1, vp, label);
			floodFill(up, vp + 1, label);
			floodFill(up, vp - 1, label);
			floodFill(up - 1, vp, label);
		}
	}

}
