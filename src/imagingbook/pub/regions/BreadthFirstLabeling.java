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

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

public class BreadthFirstLabeling extends RegionLabeling {
	
	public BreadthFirstLabeling(ByteProcessor ip) {
		super(ip);
	}
	
	void applyLabeling() {
		resetLabel();
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				if (getLabel(u, v) == FOREGROUND) {
					// start a new region
					int label = getNextLabel();
					floodFill(u, v, label);
				}
			}
		}
	}

	void floodFill(int u, int v, int label) {
		Queue<Point> queue = new LinkedList<Point>();
		queue.add(new Point(u, v));
		while (!queue.isEmpty()) {
			Point p = queue.remove();	// get the next point to process
			int up = p.x;
			int vp = p.y;
			if ((up>=0) && (up<width) && (vp>=0) && (vp<height) && getLabel(up, vp)==FOREGROUND) {
				setLabel(up, vp, label);
				queue.add(new Point(up+1, vp));
				queue.add(new Point(up, vp+1));
				queue.add(new Point(up, vp-1));
				queue.add(new Point(up-1, vp));
			}
		}
	}

}
