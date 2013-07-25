/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.threshold.adaptive;

import ij.process.ByteProcessor;
import imagingbook.pub.threshold.BackgroundMode;
import imagingbook.pub.threshold.global.OtsuThresholder;

/**
 * This thresholder splits the image into non-overlapping square
 * sub-images, computes the optimal threshold within each sub-image 
 * (using an Otsu thresholder) and interpolates linearly between 
 * these local thresholds.
 */
public class InterpolatingThresholder extends AdaptiveThresholder {
	
	public static class Parameters {
		public int tileSize = 32;
		public BackgroundMode bgMode = BackgroundMode.DARK;
	}
	
	private final Parameters params;
	
	public InterpolatingThresholder() {
		this.params = new Parameters();
	}
	
	public InterpolatingThresholder(Parameters params) {
		this.params = params;
	}

	@Override
	public ByteProcessor getThreshold(ByteProcessor ip) {
		final int W = ip.getWidth();
		final int H = ip.getHeight();
		final int tileSize = params.tileSize;
		
		// determine number of tiles
		int nW = (W % tileSize == 0) ? (W / tileSize + 1) : (W / tileSize + 2);
		int nH = (H % tileSize == 0) ? (H / tileSize + 1) : (H / tileSize + 2);
		
		int[][] tiles = new int[nW][nH];
		int s0 = tileSize / 2;	// center of title (s0 + s1 = tileSize)
//		int s1 = tileSize - s0;

		// compute threshold for each tile
		int[] h = new int[256];
		OtsuThresholder thr = new OtsuThresholder();

		int q_ = (params.bgMode == BackgroundMode.DARK) ? 256 : 0;

		for (int j = 0, v0 = 0; j < nH; j++, v0 += tileSize) {
			for (int i = 0, u0 = 0; i < nW; i++, u0 += tileSize) {
				getSubimageHistogram(ip, u0 - s0, v0 - s0, tileSize, h);
				int q = thr.getThreshold(h);
				if (q < 0) q = q_; // no threshold found in this tile
				tiles[i][j] = q;
				//IJ.log(i + "/" + j + ": " + q);
			}
		}
		
		ByteProcessor thrIp = new ByteProcessor(W, H);
		
		for (int j = 0, v0 = 0; j < nH; j++, v0 += tileSize) {
			for (int i = 0, u0 = 0; i < nW; i++, u0 += tileSize) {
				// Rectangle re = new Rectangle(u0-s0, v0-s0, u0-s0+tileSize, v0-s0+tileSize);
				for (int v = v0 - s0; v < v0 - s0 + tileSize; v++) {
					for (int u = u0 - s0; u < u0 - s0 + tileSize; u++) {
						thrIp.putPixel(u, v, tiles[i][j]);
					}
				}
			}
		}
		
		// linearly interpolate
		for (int j = 0, v0 = 0; j < nH - 1; j++, v0 += tileSize) {
			for (int i = 0, u0 = 0; i < nW - 1; i++, u0 += tileSize) {
				int A = tiles[i][j];
				int B = tiles[i + 1][j];
				int C = tiles[i][j + 1];
				int D = tiles[i + 1][j + 1];

				// interpolate within [u0, v0, u0 + tileSize, v0 + tileSize]
				for (int v = v0; v < v0 + tileSize; v++) {
					double dy = (double) (v - v0) / tileSize;
					double AC = A + dy * (C - A);
					double BD = B + dy * (D - B);
					for (int u = u0; u < u0 + tileSize; u++) {
						double dx = (double) (u - u0) / tileSize;
						double ABCD = AC + dx * (BD - AC);
						// thrIp.putPixel(u,v,tiles[i][j]);
						thrIp.putPixel(u, v, (int) Math.rint(ABCD));
					}
				}

			}
		}
			
		return thrIp;
	}
	
	private void getSubimageHistogram(ByteProcessor ip, int u0, int v0, int size, int[] h) {
		for (int i = 0; i < h.length; i++) {
			h[i] = 0;
		}
		for (int v = v0; v < v0 + size; v++) {
			for (int u = u0; u < u0 + size; u++) {
				int p = getPaddedPixel(ip, u, v);
				h[p]++;
			}
		}
	}

}
