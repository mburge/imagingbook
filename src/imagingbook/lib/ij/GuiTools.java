/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.ij;

import java.util.ArrayList;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;

public class GuiTools {
	
	/*
	 * Queries the user to select one of the currently open images.
	 * String parameter 'title' can be used to specify the title
	 * of the dialog (if null, the default title is used).
	 * Parameter 'excludeIm' can be used to specify an image to exclude
	 * from being selected (typically the current image).
	 * Returns a reference to the chosen image (ImagePlus) or
	 * null, if the dialog was canceled.
	 * 
	 * Wilbur, Feb 2012
	 */
	
	static String defaultTitle = "Choose image";
	
    public static ImagePlus chooseOpenImage(String title, ImagePlus excludeIm) {
		if (title == null) {
			title = defaultTitle;
		}
    	
		int[] imgIdsAll = WindowManager.getIDList();
		if (imgIdsAll==null) {
			IJ.error("No images are open.");
			return null;
		}

		List<Integer> imgIdList   = new ArrayList<Integer>(imgIdsAll.length);	// use a Map instead?
		List<String>  imgNameList = new ArrayList<String>(imgIdsAll.length);
		
		for (int id : imgIdsAll) {
			ImagePlus img = WindowManager.getImage(id);
			if (img!=null && img != excludeIm && img.isProcessor()) {
				imgIdList.add(id);
				imgNameList.add(img.getShortTitle());
			}
		}
		
		if (imgIdList.size() < 1) {
			IJ.error("No other images found.");
			return null;
		}
		
		Integer[] imgIds   = imgIdList.toArray(new Integer[0]);
		String[]  imgNames = imgNameList.toArray(new String[0]);
		GenericDialog gd = new GenericDialog(title, null);
		gd.addChoice("Image:", imgNames, imgNames[0]);	
		gd.showDialog();
		if (gd.wasCanceled())
			return null;
		else {
			int idx = gd.getNextChoiceIndex();
			return WindowManager.getImage(imgIds[idx]);
		}
    }
    
    /* 
     * Convenience methods for the above.
     */
    
	public static ImagePlus chooseOpenImage(String title) {
		return chooseOpenImage(title, null);
	}
	
    public static ImagePlus chooseOpenImage() {
    	return chooseOpenImage(null, null);
    }

}
