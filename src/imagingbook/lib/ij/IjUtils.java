/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.ij;

import ij.ImagePlus;
import ij.WindowManager;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


public abstract class IjUtils {
	
	/* 
	 * Returns a (possibly empty) array of ImagePlus objects that are
	 * sorted by their titles if the sortByTitle flag is set.
	 */
	public static ImagePlus[] getOpenImages(boolean sortByTitle) {
		return getOpenImages(sortByTitle, null) ;
	}
	
	/* 
	 * Returns a (possibly empty) array of ImagePlus objects that are
	 * sorted by their titles if the sortByTitle flag is set.
	 * The image "exclude" (typically the current image) is not included 
	 * in the returned array (pass null to exclude no image).
	 */
	public static ImagePlus[] getOpenImages(boolean sortByTitle, ImagePlus exclude) {
		List<ImagePlus> imgList = new LinkedList<ImagePlus>();
		int[] wList = WindowManager.getIDList();
        if (wList != null) {
	    	for (int i : wList) {
	            ImagePlus imp = WindowManager.getImage(i);
	            if (imp != null && imp != exclude) {
	            	imgList.add(imp);
	            }
	    	}
        }
        ImagePlus[] impArr = imgList.toArray(new ImagePlus[0]);
        if (sortByTitle) {
        	Comparator<ImagePlus> cmp = new Comparator<ImagePlus>() {
        		public int compare(ImagePlus impA, ImagePlus impB) {
        			return impA.getTitle().compareTo(impB.getTitle());
        		}
        	};
        	Arrays.sort(impArr, cmp);
        }
		return impArr;
	}
		
	public static void showProcessor(ImageProcessor ip, String title) {
		ImagePlus win = new ImagePlus(title, ip);
		win.show();
	}
	
	public static void showProcessor(FloatProcessor ip, String title) {
		ImagePlus win = new ImagePlus(title, ip);
		ip.resetMinAndMax();
		win.show();
	}
	
	public static void showProcessor(FloatProcessor ip, String title, double min, double max) {
		ImagePlus win = new ImagePlus(title, ip);
		ip.setMinAndMax(min, max);
		win.show();
	}
	
	public static void normalize(FloatProcessor fp) {	// TODO: make this return a new FloatProcesor, make more efficient
		double minVal = fp.getMin();
		double maxVal = fp.getMax();
		fp.add(-minVal);
		fp.multiply(1.0 / (maxVal - minVal));
	}


}
