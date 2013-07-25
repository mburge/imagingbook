/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.edgepreservingfilters;

import ij.IJ;
import ij.plugin.filter.Convolver;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

// TODO: convert to subclass of GenericFilter using ImageAccessor (see BilateralFilter)

/**
 * This class implements the Anisotropic Diffusion filter proposed by David Tschumperle 
 * in D. Tschumperle and R. Deriche, "Diffusion PDEs on vector-valued images", 
 * IEEE Signal Processing Magazine, vol. 19, no. 5, pp. 16-25 (Sep. 2002). It is based 
 * on an earlier C++ (CImg) implementation (pde_TschumperleDeriche2d.cpp) by the original
 * author, made available under the CeCILL v2.0 license 
 * (http://www.cecill.info/licences/Licence_CeCILL_V2-en.html).
 * 
 * This class is based on the ImageJ API and intended to be used in ImageJ plugins.
 * How to use: consult the source code of the related ImageJ plugins for examples.
 * 
 * @author W. Burger
 * @version 2013/05/30
 */

public class TschumperleDericheFilter {
	
	public static class Parameters {
		public int iterations = 20;	// Number of smoothing iterations
		public double dt = 20.0;  		// Adapting time step
		public double sigmaG  = 0.5;	// Gradient smoothing (sigma of Gaussian)
		public double sigmaS  = 0.5;	// Structure tensor smoothing (sigma of Gaussian)
		public float a1 = 0.25f;  		// Diff. limiter along minimal var. (small val = strong smoothing)
		public float a2 = 0.90f;  		// Diff. limiter along maximal var. (small val = strong smoothing)
		public boolean useLinearRgb = false;
	}
	
	private final Parameters params;
	private final int T;			// number of iterations
	
	private int M;	// image width
	private int N;	// image height
	private int K;	// number of color channels, k = 0,...,K-1

	private float[][][] I;		// float image data: 		I[k][u][v] for color channel k
	private float[][][] Dx; 	// image x-gradient: 		Dx[k][u][v] for color channel k
	private float[][][] Dy; 	// image y-gradient: 		Dx[k][u][v] for color channel k
	private float[][][] G; 		// 2x2 structure tensor: 	G[i][u][v], i=0,1,2 (only 3 elements because of symmetry)
	private float[][][] A;		// 2x2 tensor field: 	 	A[i][u][v], i=0,1,2 (only 3 elements because of symmetry)
	private float[][][] B;		// scalar local velocity:   B[k][u][v]  for channel k (== beta_k)
//	private float[][][] Hk;  	// Hessian matrix for channel k: Hk[i][u][v], i=0,1,2
	FloatProcessor tmpFp;		// used as temporary storage for blurring
	
	private float initial_max;
	private float initial_min;
	
	// constructor - uses only default settings:
	public TschumperleDericheFilter() {
		this(new Parameters());
	}
	
	// constructor - use for setting individual parameters:
	public TschumperleDericheFilter(Parameters params) {
		this.params = params;
		T = params.iterations;
	}
	
	/* This method applies the filter to the given image (ip). 
	 * Note that ip is destructively modified.
	 */
	public void applyTo(ImageProcessor ip) {	
		initialize(ip);
		// main iteration loop
		for (int n = 1; n <= T; n++) {
			IJ.showProgress(n, T);
			
			// Step 1:
			calculateGradients(I, Dx, Dy);
			
			// Step 2:
			smoothGradients(Dx, Dy);
			
			// Step 3: Hessian matrix is only calculated locally as part of Step 8.
			
			// Step 4:
			calculateStructureMatrix(Dx, Dy, G);
			// Step 5:
			smoothStructureMatrix(G);

			// Step 6-7:
			calculateGeometryMatrix(G, A);
			
			// Step 8:
			float maxVelocity = calculateVelocities(I, A, B);
			
			double alpha = params.dt / maxVelocity;
			updateImage(I, B, alpha);
		}
		copyResultToImage(ip);
		cleanUp();
	} 
	
	// -------------------------------------------------------------------------
	
	/*
	 * Create temporary arrays, copy image data and calculate
	 * initial image statistics (all in one pass).
	 */
	private void initialize(ImageProcessor ip) {
		M = ip.getWidth(); 
		N = ip.getHeight(); 
		K = (ip instanceof ColorProcessor) ? 3 : 1;
		I  = new float[K][M][N];
		Dx  = new float[K][M][N];
		Dy  = new float[K][M][N];
		G = new float[3][M][N];
		A = new float[3][M][N];
		B = new float[K][M][N];
//		Hk = new float[3][M][N];

		if (ip instanceof ColorProcessor) {
			final int[] pixel = new int[K];	
			for (int u = 0; u < M; u++) {
				for (int v = 0; v < N; v++) {
					ip.getPixel(u, v, pixel);
					for (int k = 0; k < K; k++) {
						float c = pixel[k];
						I[k][u][v] = params.useLinearRgb ? srgbToRgb(c) : c;
					}
				}
			}
		}
		else {	// 8-bit, 16-bit or 32-bit (float) processor
			for (int u = 0; u < M; u++) {
				for (int v = 0; v < N; v++) {
					I[0][u][v] = ip.getf(u,v);
				}
			}
		}
		getImageMinMax();
	}
	
	void getImageMinMax() {
		float max = Float.MIN_VALUE;
		float min = Float.MAX_VALUE;
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				for (int k = 0; k < K; k++) {
					float p = I[k][u][v];
					if (p>max) max = p;
					if (p<min) min = p;
				}
			}
		}
		initial_max = max;
		initial_min = min;
	}
	
	void cleanUp() {
		I  = null;		Dx = null;		Dy = null;
		G = null;		A = null;		B = null;
//		Hk = null;		
		tmpFp = null;
	}
	
	void calculateGradients(float[][][] I, float[][][] Dx, float[][][] Dy) {
		// these Gradient kernels produce reduced artifacts
		final float c1 = (float) (2 - Math.sqrt(2.0)) / 4;
		final float c2 = (float) (Math.sqrt(2.0) - 1) / 2;
		
		final float[][] Hdx = 
			{{-c1, 0, c1},
			 {-c2, 0, c2},
			 {-c1, 0, c1}};
		
		final float[][] Hdy = 
			{{-c1, -c2, -c1},
			 {  0,   0,   0},
			 { c1,  c2,  c1}};

		for (int k = 0; k < K; k++) {
			convolve2dArray(I[k], Dx[k], Hdx);
			convolve2dArray(I[k], Dy[k], Hdy);
		}
	}
	
	void smoothGradients(float[][][] Dx, float[][][] Dy) {
		for (int k = 0; k < Dx.length; k++) {
			gaussianBlur(Dx[k], params.sigmaG);
		}
		for (int k = 0; k < Dy.length; k++) {
			gaussianBlur(Dy[k], params.sigmaG);
		}
	}
	
	void calculateStructureMatrix(float[][][] Dx, float[][][] Dy, float[][][] G) {
		// compute structure tensor field G
		// G = new float[width][height][3]; // must be clean for each slice
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				G[0][u][v]= 0.0f;
				G[1][u][v]= 0.0f;
				G[2][u][v]= 0.0f;
				for (int k = 0; k < K; k++) {
					//version 0.2 normalization
					float fx = Dx[k][u][v];
					float fy = Dy[k][u][v];
					G[0][u][v] += fx * fx;
					G[1][u][v] += fx * fy;
					G[2][u][v] += fy * fy;
				}
			}
		}
	}
	
	void smoothStructureMatrix(float[][][] G) {
		for (int i = 0; i < G.length; i++) {
			gaussianBlur(G[i], params.sigmaS);
		}
	}
	
	/*
	 * Compute the local geometry matrix A (used to drive the diffusion process)
	 * from the structure matrix G.
	 */
	void calculateGeometryMatrix(float[][][] G, float[][][] A) {
		final double[] lambda12 = new double[2]; 	// eigenvalues
		final double[] e1 = new double[2];			// eigenvectors
		final double[] e2 = new double[2];
		final double a1 = params.a1;
		final double a2 = params.a2;
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				final double G0 = G[0][u][v];	// elements of local geometry matrix (2x2)
				final double G1 = G[1][u][v];
				final double G2 = G[2][u][v];
				// calculate eigenvalues:
				if (!realEigenValues2x2(G0, G1, G1, G2, lambda12, e1, e2)) {
					throw new RuntimeException("eigenvalues undefined in " + 
								TschumperleDericheFilter.class.getSimpleName());
				}
				final double val1 = lambda12[0];
				final double val2 = lambda12[1];
				final double arg = 1.0 + val1 + val2;
				final float c1 = (float) Math.pow(arg, -a1);
				final float c2 = (float) Math.pow(arg, -a2);
				
				// calculate eigenvectors:
				normalize(e1);
				final float ex = (float) e1[0];
				final float ey = (float) e1[1];
				final float exx = ex * ex;
				final float exy = ex * ey;
				final float eyy = ey * ey;
				A[0][u][v] = c1 * eyy + c2 * exx;
				A[1][u][v] = (c2 - c1)* exy;
				A[2][u][v] = c1 * exx + c2 * eyy;
			}
		}
	}
	
	// Calculate the Hessian matrix Hk for a single position (u,v) in image Ik.
	void calculateHessianMatrix(float[][] Ik, int u, int v, float[] Hk) {
		final int pu = (u > 0) ? u-1 : 0; 
		final int nu = (u < M-1) ? u+1 : M-1;
		final int pv = (v > 0) ? v-1 : 0; 
		final int nv = (v < N-1) ? v+1 : N-1;
		float icc = Ik[u][v];
		Hk[0] = Ik[pu][v] + Ik[nu][v] - 2 * icc;								// = H_xx(u,v)
		Hk[1] = 0.25f * (Ik[pu][pv] + Ik[nu][nv] - Ik[pu][nv] - Ik[nu][pv]);	// = H_xy(u,v)
		Hk[2] = Ik[u][nv] + Ik[u][pv] - 2 * icc;								// = H_yy(u,v)
	}
	
	/*
	 * Calculate the local image velocity B(k,u,v) from the geometry matrix A(i,u,v)
	 * and the Hessian matrix Hkuv.
	 */
	float calculateVelocities(float[][][] I, float[][][] A, float[][][] B) {
		float maxV = Float.MIN_VALUE;
		float minV = Float.MAX_VALUE;
		final float[] Hkuv = new float[3];
		for (int k = 0; k < K; k++) {
			for (int u = 0; u < M; u++) {
				for (int v = 0; v < N; v++) {
					calculateHessianMatrix(I[k], u, v, Hkuv);
					final float a = A[0][u][v];
					final float b = A[1][u][v];
					final float c = A[2][u][v];					
					final float ixx = Hkuv[0]; 
					final float ixy = Hkuv[1]; 
					final float iyy = Hkuv[2];
					final float vel = a * ixx + 2 * b * ixy + c * iyy; 
					// find min/max velocity for time-step adaptation
					if (vel > maxV) maxV = vel;
					if (vel < minV) minV = vel;
					B[k][u][v] = vel;
				}
			}
		}
		return Math.max(Math.abs(maxV), Math.abs(minV));
	}

	// Calculate the Hessian matrix Hk for the whole (single-channel) image Ik.
//	void calculateHessianMatrix(float[][] Ik, float[][][] Hk) {
//		for (int u = 0; u < M; u++) {
//			final int pu = (u > 0) ? u-1 : 0; 
//			final int nu = (u < M-1) ? u+1 : M-1;
//			for (int v = 0; v < N; v++) {
//				final int pv = (v > 0) ? v-1 : 0; 
//				final int nv = (v < N-1) ? v+1 : N-1;
//				float icc = Ik[u][v];
//				Hk[0][u][v] = Ik[pu][v] + Ik[nu][v] - 2 * icc;								// = H_xx(u,v)
//				Hk[1][u][v] = 0.25f * (Ik[pu][pv] + Ik[nu][nv] - Ik[pu][nv] - Ik[nu][pv]);	// = H_xy(u,v)
//				Hk[2][u][v] = Ik[u][nv] + Ik[u][pv] - 2 * icc;								// = H_yy(u,v)
//			}
//		}
//	}
	
//	float calculateVelocity(float[][][] I, float[][][] A, float[][][] B) {
//		float maxV = Float.MIN_VALUE;
//		float minV = Float.MAX_VALUE;
//		for (int k = 0; k < K; k++) {
//			// calculate the Hessian matrix for channel k:
//			calculateHessianMatrix(I[k], Hk);
//			for (int u = 0; u < M; u++) {
//				for (int v = 0; v < N; v++) {
//					float a = A[0][u][v];
//					float b = A[1][u][v];
//					float c = A[2][u][v];					
//					float ixx = Hk[0][u][v]; 
//					float ixy = Hk[1][u][v]; 
//					float iyy = Hk[2][u][v];
//					float vel = a * ixx + 2 * b * ixy + c * iyy; 
//					// find min/max velocity for time-step adaptation
//					if (vel > maxV) maxV = vel;
//					if (vel < minV) minV = vel;
//					B[k][u][v] = vel;
//				}
//			}
//		}
//		return Math.max(Math.abs(maxV), Math.abs(minV));
//	}
	
	void updateImage(float[][][] I, float[][][] B, double alpha) {
		final float alphaF = (float) alpha;
		for (int k = 0; k < K; k++) {
			for (int u = 0; u < M; u++) {
				for (int v = 0; v < N; v++) {
					float inew = I[k][u][v] + alphaF * B[k][u][v];
					// clamp image to the original range (brute!)
					if (inew < initial_min) inew = initial_min;
					if (inew > initial_max) inew = initial_max;
					I[k][u][v] = inew;
				}
			}
		}
	}
	
	void copyResultToImage(ImageProcessor ip) {
		final int[] pixel = new int[K];
		if (ip instanceof ColorProcessor) {
			for (int u = 0; u < M; u++) {
				for (int v = 0; v < N; v++) {
					for (int k = 0; k < K; k++) {
						int c = params.useLinearRgb ? 
								Math.round(rgbToSrgb(I[k][u][v])) : 
								Math.round(I[k][u][v]);
						if (c < 0) c = 0;
						if (c > 255) c = 255;
						pixel[k] = c;
					}
					ip.putPixel(u,v,pixel);
				}
			}
		}
		else { 	// 8-bit, 16-bit or 32-bit (float) processor
			for (int u = 0; u < M; u++) {
				for (int v = 0; v < N; v++) {
					ip.setf(u, v, I[0][u][v]);
				}
			}
		}
	}
	
	// Utility methods -------------------------------------------------
	
	/*
	 * Blur the 2D array source with a Gaussian kernel of width sigma
	 * and store the result in target.
	 */
	void gaussianBlur(float[][] source, float[][] target, double sigma) {
		if (sigma < 0.1) return;
		if (source.length != target.length || source[0].length != target[0].length) {
			throw new Error("source/target arrays have different dimensions");
		}
		float[][] Hgx = makeGaussKernel1D(sigma, true);		// horizontal 1D kernel
		float[][] Hgy = makeGaussKernel1D(sigma, false);	// vertical 1D kernel
		convolve2dArray(source, target, Hgx, Hgy);
	}
	
	void gaussianBlur(float[][] source, double sigma) {	// source = target
		gaussianBlur(source, source, sigma);
	}
	
	// ----------------------------------------------------------------
	
	/*
	 * Convolve the 2D array source successively with a sequence of kernels
	 * and store the result in target.
	 * This should eventually be implemented without an ImageJ FloatProcessor!
	 */
	void convolve2dArray(float[][] source, float[][] target, float[][]... kernels) {
		if (source.length != target.length || source[0].length != target[0].length) {
			throw new Error("source/target arrays have different dimensions");
		}
		int w = source.length;
		int h = source[0].length;
		if (tmpFp == null || tmpFp.getWidth() != w || tmpFp.getHeight() != h) {
			tmpFp = new FloatProcessor(w, h);
		}
		// copy data to FloatProcessors		
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				tmpFp.setf(u, v, source[u][v]);
			}
		}
		
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		// convolve with all specified kernels
		for (float[][] H : kernels) {
			if (H == null) break;
			int wH = H.length;
			int hH = H[0].length;
			float[] H1 = flatten(H);
			conv.convolveFloat(tmpFp, H1, wH, hH);
		}
		// copy data back to array	
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				target[u][v] = tmpFp.getf(u, v);
			}
		}
	}
	
	/*
	 * Copy a 2D float  array into a 1D float array
	 */
	float[] flatten (float[][] arr2d) {
		int w = arr2d.length;
		int h = arr2d[0].length;
		float[] arr1d = new float[w*h];
		int k = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				arr1d[k] = arr2d[i][j];
				k++;
			}
		}
		return arr1d;
	}
	
	/*
	 * Construct a 2D Gaussian filter kernel large enough to avoid truncation effects.
	 * Returns a 1D kernel as a 2D array, so it can be used flexibly in horizontal
	 * or vertical direction.
	 */
	private float[][] makeGaussKernel1D(double sigma, boolean horizontal){
		// Construct a 2D Gaussian filter kernel large enough
		// to avoid truncation effects.
		final double sigma2 = sigma * sigma;
		final double scale = 1.0 / (Math.sqrt(2 * Math.PI) * sigma);	
		final int rad = Math.max((int) (3.5 * sigma), 1); 
		int size = rad + 1 +rad;	//center cell = kernel[rad]
		float[][] kernel = (horizontal) ?  
				new float[size][1] : 
				new float[1][size]; 
		double sum = 0;
		for (int i = 0; i < size; i++) {
			double x = rad - i;
			float val = (float) (scale * Math.exp(-0.5 * (x*x) / sigma2));
			if (horizontal) 
				kernel[i][0] =  val;
			else 
				kernel[0][i] =  val;
			sum = sum + val;
		}
		
		// normalize (just to be safe)
		for (int i = 0; i < kernel.length; i++) {
			for (int j = 0; j < kernel[i].length; j++) {
				kernel[i][j] = (float) (kernel[i][j] / sum);
			}
		}
		return kernel;
	}
	
	// ----------------------------------------------------------------
	
	void normalize(double[] vec) {
		double sum = 0;
		for (double v : vec) {
			sum = sum + v * v;
		}
		if (sum > 0.000001) {
			double s = 1 / Math.sqrt(sum);
			for (int i = 0; i < vec.length; i++) {
				vec[i] = vec[i] * s;
			}
		}
	}
	
	boolean realEigenValues2x2 (
			double A, double B, double C, double D, 
			double[] lam12, double[] x1, double[] x2) {
		final double R = (A + D) / 2;
		final double S = (A - D) / 2;
		final double V = S * S + B * C;
		if (V < 0) 
			return false; // matrix has no real eigenvalues
		else {
			double T = Math.sqrt(V);
			lam12[0] = R + T;	// lambda_1
			lam12[1] = R - T;	// lambda_2
			if ((A - D) >= 0) {
				x1[0] = S + T;	//e_1x
				x1[1] = C;		//e_1y			
				x2[0] = B;		//e_2x
				x2[1] = -S - T;	//e_2y		
			} 
			else {
				x1[0] = B;		//e_1x
				x1[1] = -S + T;	//e_1y	
				x2[0] = S - T;	//e_2x
				x2[1] = C;		//e_2y	
			}
			return true;
		}
	}
	
	//  RGB/sRGB conversion -----------------------------------
	// TODO: move this to lib.colorimage.sRgbUtil class
	
	float srgbToRgb(float nc) {
		float nc01 = nc/255;
		return (float)(gammaInv(nc01) * 255);
	}

	float rgbToSrgb(float lc) {
		float lc01 = lc/255;
		return (float) (gammaFwd(lc01) * 255);
	}
	
	
	double gammaFwd(double lc) {	// input: linear component value
		return (lc > 0.0031308) ?
			(1.055 * Math.pow(lc, 1/2.4) - 0.055) :
			(lc * 12.92);
    }
    
    double gammaInv(double nc) {	// input: nonlinear component value
    	return (nc > 0.03928) ?
			Math.pow((nc + 0.055)/1.055, 2.4) :
			(nc / 12.92);
    }

}

