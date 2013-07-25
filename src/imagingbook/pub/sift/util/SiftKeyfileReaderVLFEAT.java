/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.sift.util;

import ij.IJ;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class SiftKeyfileReaderVLFEAT {
	
	private int descriptorLength = 128;
	
	public List<SiftKeypoint> readKeypointFile(String filename) {
		List<SiftKeypoint> keypoints = new ArrayList<SiftKeypoint>();
		File keyfile = new File(filename);

		Scanner sc = null;
		try {
			FileReader fr = new FileReader(keyfile);
			sc = new Scanner(fr);
		} catch (FileNotFoundException e) {
			return null;
		}

		sc.useLocale(Locale.US);

		try {
//			int nKeypoints = sc.nextInt();
//			int descriptorLength = sc.nextInt();

//			IJ.log("keypoints: " + nKeypoints);
//			IJ.log("descriptorLength: " + descriptorLength);

			while(sc.hasNext()) {
				// read one keypoint:
				double xpos = sc.nextDouble(); //NOTE: x/y not sapped
				double ypos = sc.nextDouble();
				double scale = sc.nextDouble(); //NOTE: orientation OK
				double orientation = sc.nextDouble();
				int[] descriptor = new int[descriptorLength];
				for (int i=0; i<descriptorLength; i++) {
					descriptor[i] = sc.nextInt();
				}
				keypoints.add(new SiftKeypoint(xpos, ypos, scale, orientation, descriptor));
			}
		}
		catch (Exception e) {
			IJ.log("Exception: " + e);
		}
		
		sc.close();
		return keypoints;
	}
}


/* Lowe's file format for SIFT keys:
The file format starts with 2 integers giving the total number of
keypoints and the length of the descriptor vector for each keypoint
(128). Then the location of each keypoint in the image is specified by
4 floating point numbers giving subpixel row and column location,
scale, and orientation (in radians from -PI to PI).  Obviously, these
numbers are not invariant to viewpoint, but can be used in later
stages of processing to check for geometric consistency among matches.
Finally, the invariant descriptor vector for the keypoint is given as
a list of 128 integers in range [0,255].  Keypoints from a new image
can be matched to those from previous images by simply looking for the
descriptor vector with closest Euclidean distance among all vectors
from previous images.
*/
	
