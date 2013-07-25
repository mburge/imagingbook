/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.ij;

import ij.IJ;
import ij.process.ImageProcessor;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

public class LookupTables {
	
	public static void listCurrentLut(ImageProcessor ip) {
        ColorModel cm = ip.getCurrentColorModel();
        IndexColorModel icm = (IndexColorModel)cm;
        int mapSize = icm.getMapSize();
        byte[] reds = new byte[mapSize];
        byte[] grns = new byte[mapSize];
        byte[] blus = new byte[mapSize];
        icm.getReds(reds); 
        icm.getGreens(grns); 
        icm.getBlues(blus);
        for (int i=0; i<mapSize; i++) {
            IJ.log(String.format("%3d: %3d %3d %3d", i, reds[i] & 0xFF, grns[i] & 0xFF, blus[i] & 0xFF));
        }
	}
	
	/* 
	 * Modifies the lookup table to display a bright image with gray values
	 * in the range minGray ... 255. Does nothing if ip is a ColorProcessor.
	 */
	public static void brightLut(ImageProcessor ip, int minGray) {
		if (minGray < 0 || minGray >= 255) 
			return;
        ColorModel cm = ip.getColorModel();
        if (!(cm instanceof IndexColorModel))
        	return;
        IndexColorModel icm = (IndexColorModel)cm;
        int mapSize = icm.getMapSize();
        byte[] reds = new byte[mapSize];
        byte[] grns = new byte[mapSize];
        byte[] blus = new byte[mapSize];
        float scale = (255 - minGray) / 255f;
        for (int i=0; i<mapSize; i++) {
        	byte g = (byte) (Math.round(minGray + scale * i) & 0xFF);
            reds[i] = g;
            grns[i] = g;
            blus[i] = g;
        }
        ip.setColorModel(new IndexColorModel(8, mapSize, reds, grns, blus));
	}

}
