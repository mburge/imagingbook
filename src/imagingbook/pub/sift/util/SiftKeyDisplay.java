/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.sift.util;

import java.awt.Color;
import java.util.List;

import ij.process.ImageProcessor;


/*
 * This class might be obsolete!?
 */

public class SiftKeyDisplay {
	
	private List<SiftKeypoint> keyset;
	private double magnification = 6.0;
	private Color boxColor = Color.green;
	private Color lineColor = Color.red;
	
	
	public SiftKeyDisplay(List<SiftKeypoint> keys) {
		this.keyset = keys;
	}
	
	public void draw(ImageProcessor ip) {
		for (SiftKeypoint k : keyset) {
			drawOneKey(k,ip);
		}
	}
	
	void drawOneKey(SiftKeypoint key, ImageProcessor ip) {	
		double x = (int) Math.rint(key.x);
		double y = (int) Math.rint(key.y);
		int u0 = (int) x;
		int v0 = (int) y;
		double phi = key.orientation;
		double scale = key.scale;
		double dx = magnification * scale * Math.cos(phi);	
		double dy = magnification * scale * Math.sin(phi); // NOTE: angle runs reversed
		int u1 = (int) Math.rint(x + dx);
		int v1 = (int) Math.rint(y + dy);
		
		ip.setColor(boxColor);
		ip.drawRect(u0-1, v0-1, 3, 3);
		ip.setColor(lineColor);
		ip.drawLine(u0,v0,u1,v1);

	}

}
