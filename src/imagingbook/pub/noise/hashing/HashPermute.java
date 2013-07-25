/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.noise.hashing;

public class HashPermute extends HashFun {
	
	public HashPermute() {
		super();
	}
	
    // seed is ignored by HashPermute
	public HashPermute(int seed) {
		super(seed);
	}
	
	@Override
	public double hash(int u) {
		int h = h8(u);
		return (double) (h & 0xFF) / 0xFF; // use bits 0..7 for d
	}
	
	// 6 bits per channel (overlapping blocks)
	@Override
	public double[] hash(int u, int v) {
		final int M = 0x3F; // 63;
		int h = h8(u, v);
		double hx = h & M; 			// use bits 0..5 for dx
		double hy = (h >> 2) & M; 	// use bits 2..7 for dy
		return new double[] {hx/M, hy/M};
	}
	
	@Override
	public double[] hash(int u, int v, int w) {
		final int M = 0x0F;
		int h = h8(u, v, w);
		double hx =  h & M; 			// use bits 0..3 for x
		double hy = ((h >> 2) & M); 	// use bits 2..5 for y
		double hz = ((h >> 4) & M); 	// use bits 4..7 for z
		return new double[] {hx/M, hy/M, hz/M};
	}

	// 2 dimensions
	private int h8 (int u) {
		u = (u + seed) & 0xFF;
		return P[u];
	}
	
	// 2 dimensions
	private int h8 (int u, int v) {
		u = (u + seed) & 0xFF;
		v = v & 0xFF;
		return P[P[v] + u];
	}
	
	// 3 dimensions
	private int h8 (int u, int v, int w) {
		u = (u + seed) & 0xFF;
		v = v & 0xFF;
		w = w & 0xFF;
		return P[P[P[w] + v] + u];
	}
	
	@Override
	/*
	 * N-dimensional permutation hash; this version does not use
	 * any bit splitting. Instead, the hash8() function is
	 * applied repeatedly for every gradient dimension by 
	 * using the dimension number (k) as a local seed (sd) - 
	 * in addition to the global seed (seed).
	 */
	public double[] hash(int[] p) {
		int N = p.length;
		double[] g = new double[N];
		for (int k=0; k<N; k++) {		// dimension k
			int h = h8(p, k+seed);
			g[k] = (double) (h & 0xFF) / 0xFF;
		}
		return g;
	}
	
	/*
	 * N-dimensional permutation hash function
	 */
	private int h8 (int[] p, int sd) {
		int h = sd & 0xFF;
		for (int k=0; k<p.length; k++) {
			h = P[h + p[k] & 0xFF];
		}
		return h;
	}

	/*
	 * Permutation table P[i], for i = 0..255. To avoid index wrapping, 
	 * table's length is doubled to 512.
	 */
	static final int P[] = new int[512];
	static {
		int[] perm = {
				151, 160, 137, 91, 90, 15, 131, 13, 
				201, 95, 96, 53, 194, 233, 7, 225, 
				140, 36, 103, 30, 69, 142, 8, 99, 
				37, 240, 21, 10, 23, 190, 6, 148, 
				247, 120, 234, 75, 0, 26, 197, 62,
				94,	252, 219, 203, 117, 35, 11, 32, 
				57, 177, 33, 88, 237, 149, 56, 87, 
				174, 20, 125, 136, 171, 168, 68, 175, 
				74, 165, 71, 134, 139, 48, 27, 166, 
				77, 146, 158, 231, 83, 111, 229, 122, 
				60, 211, 133, 230, 220, 105, 92, 41, 
				55, 46, 245, 40, 244, 102, 143, 54, 
				65, 25, 63, 161, 1, 216, 80, 73, 
				209, 76, 132, 187, 208, 89, 18, 169, 
				200, 196, 135, 130, 116, 188, 159, 86, 
				164, 100, 109, 198, 173, 186, 3, 64,
				52, 217, 226, 250, 124, 123, 5,	202, 
				38, 147, 118, 126, 255, 82, 85, 212, 
				207, 206, 59, 227, 47, 16, 58, 17, 
				182, 189, 28, 42, 223, 183, 170, 213, 
				119, 248, 152, 2, 44, 154, 163, 70, 
				221, 153, 101, 155, 167, 43, 172, 9,
				129, 22, 39, 253, 19, 98, 108, 110, 
				79, 113, 224, 232, 178,	185, 112, 104,
				218, 246, 97, 228, 251, 34, 242, 193, 
				238, 210, 144, 12, 191, 179, 162, 241, 
				81, 51, 145, 235, 249, 14, 239,	107, 
				49, 192, 214, 31, 181, 199, 106, 157, 
				184, 84, 204, 176, 115, 121, 50, 45, 
				127, 4, 150, 254, 138, 236, 205, 93, 
				222, 114, 67, 29, 24, 72, 243, 141, 
				128, 195, 78, 66, 215, 61, 156,	180 };
		for (int i = 0; i < 256; i++)
			P[256 + i] = P[i] = perm[i];
	}
	
	public static void main(String[] args) {
		HashPermute hf = new HashPermute();
		for (int k=0; k<256; k++) {
			System.out.format("%d : %10f\n", k, hf.hash(k));
			
		}
		
		System.out.println(-1 % 256);
	}
}
