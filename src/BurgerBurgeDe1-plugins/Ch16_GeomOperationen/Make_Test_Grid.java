package Ch16_GeomOperationen;
/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;

public class Make_Test_Grid implements PlugIn {
	
	static int w = 400;
	static int h = 400;
	static int xStep = 20;
	static int yStep = 20;
	static int xStart = 100;
	static int yStart = 100;
	static int xN = 10;
	static int yN = 10;
	
	int foreground = 0;
	int background = 255;
	
    public void run(String arg) {
    	ByteProcessor ip = new ByteProcessor(w,h);
    	ip.setValue(background);
    	ip.fill();
    	
		ip.setValue(foreground);
    	int y = yStart;
    	int x1 = xStart;
    	int x2 = xStart+xN*xStep;
    	for (int j = 0; j <= yN; j++) {
			ip.drawLine(x1,y,x2,y);
			y = y + yStep;
    	}
    	
		int x = xStart;
		int y1 = yStart;
		int y2 = yStart+yN*yStep;
		for (int i = 0; i <= xN; i++) {
			ip.drawLine(x,y1,x,y2);
			x = x + xStep;
		}
		
		ip.drawLine(0,0,w-1,h-1);
		ip.drawOval(xStart, yStart, 200, 200);
    	
        ImagePlus im = new ImagePlus("Grid",ip);
        im.show();
        //im.updateAndDraw();
    }
}
