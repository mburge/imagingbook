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
import java.util.Stack;

public class DepthFirstLabeling extends RegionLabeling {
	
	public DepthFirstLabeling(ByteProcessor ip) {
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

	void floodFill(int x, int y, int label) {
		//stack contains pixel coordinates
		Stack<Point> stack = new Stack<Point>();	// Note: Stack should be replaced by Deque interface (Java 1.6)
		stack.push(new Point(x,y));
		while (!stack.isEmpty()){
			Point p = stack.pop();
			int up = p.x;
			int vp = p.y;
			if ((up>=0) && (up<width) && (vp>=0) && (vp<height) && getLabel(up, vp)==FOREGROUND) {
				setLabel(up, vp, label);
				stack.push(new Point(up+1, vp));
				stack.push(new Point(up, vp+1));
				stack.push(new Point(up, vp-1));
				stack.push(new Point(up-1, vp));
			}
		}
	}

}
