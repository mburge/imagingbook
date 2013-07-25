/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.sift;

import ij.IJ;
import ij.process.FloatProcessor;
import imagingbook.lib.ij.IjUtils;
import imagingbook.pub.sift.scalespace.DogScaleSpace;
import imagingbook.pub.sift.scalespace.GaussianScaleSpace;
import imagingbook.pub.sift.scalespace.ScaleLevel;
import imagingbook.pub.sift.scalespace.ScaleOctave;
import imagingbook.pub.sift.util.Matrix;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class SiftDetector {

	/* Default parameters; a (usually modified) instance of this class
	 * may be passed to constructor of the main class.
	 */
	public static class Parameters {
		public boolean DEBUG = false;
		public NhType nhType = NhType.NH18;	// type of neigborhood used for peak detection in 3D scale space
		public double sigma_s = 0.5;	// sampling scale (nominal smoothing level of the input image)
		public double sigma_0 = 1.6;	// base scale of level 0 (base smoothing)
		public int P = 4; 				// number of octaves
		public int Q = 3;				// scale steps (levels) per octave
		public double t_Mag = 0.01;		// min. magnitude required in DoG peak detection (abs. value)
		public double t_Peak = t_Mag;	// min. DoG magnitude required for extrapolated peaks (abs. value)
		public double t_Extrm = 0.0;	// min. difference to all neighbors in DoG peak detection (max. 0.0005)
		public int n_Refine = 5;		// max. number of iterations for refining a key point
		public double reMax = 10.0;		// max. principal curvature ratio used to eliminate line-like structures (3..10)
		public int n_Orient = 36;		// number of orientation bins in the feature descriptor (angular resolution)
		public int n_Smooth = 2;		// number of smoothing steps applied to the orientation histogram
		public double t_DomOr = 0.8;	// min. value in orientation histogram for dominant orientations (rel. to max. entry)
		public double s_Desc = 10.0;	// spatial size factor of descriptor (relative to feature scale)
		public int n_Spat = 4;			// number of spatial descriptor bins along each x/y axis
		public int n_Angl = 8;			// number of angular descriptor bins
		public double t_Fclip = 0.2;	// max. value in normalized feature vector (0.2 recommended by Lowe)
		public double s_Fscale = 512.0;	// scale factor for converting normalized features to byte values in [0,255]
	}
	
	private final Parameters params;

	/* Types of 3D neighborhoods used in min/max detection */

	public enum NhType {
		NH8(8),	NH10(10), NH18(18), NH26(26);
		private final int size;
		private NhType(int size) {
			this.size = size;
		}
	}
	
	static final float EPSILON_F = 1e-35f;
	static final double PI2 = 2 * Math.PI;

	/* non-static fields */

	private GaussianScaleSpace G;
	private DogScaleSpace D;
	private int nhSize;


	/* Constructors */

	public SiftDetector(FloatProcessor fp) {
		this(fp, new Parameters());	// uses default parameters
	}

	public SiftDetector(FloatProcessor fp, Parameters params) {
		IjUtils.normalize(fp);
		this.params = params;
		nhSize = params.nhType.size;
		G = new GaussianScaleSpace(fp, params.sigma_s, params.sigma_0, params.P, params.Q, -1, params.Q+1);
		D = new DogScaleSpace(G);
	}

	/*
	 * This is only for debugging: produces key points with orientation
	 * histograms attached for display purposes.
	 */
	public List<KeyPoint> makeRichKeypoints(List<KeyPoint> keypoints) {
		if (params.DEBUG) {IJ.log("makeSiftDescriptors...");}
		//int cnt = 0;
		List<KeyPoint> richKeyPoints = new ArrayList<KeyPoint>();
		for (KeyPoint kp : keypoints) {
			//IJ.log("   " + (cnt++));
			float[] oh = getOrientationHistogram(kp);
			smoothCircular(oh,params.n_Smooth);
			kp.orientation_histogram = oh;	// TODO: remove, for testing only!!
			List<Double> peakOrientations = findPeakOrientationIndices(oh);
			if (params.DEBUG && peakOrientations.size() == 0) {
				IJ.log("insufficient orientations at " + kp.u + "/" + kp.v);
			}
			for (double km : peakOrientations) {
				//for (int i=0; i<Math.min(1, peakOrientations.length); i++) {	// use only 1 descriptor!
				float phi = (float) (km * 2 * Math.PI / oh.length);	// 0 <= phi < 2 PI. Should be in range +/-PI?
				KeyPoint rkp = (KeyPoint) kp.clone();
				rkp.orientation = phi;
				richKeyPoints.add(rkp);
			}
		}
		if (params.DEBUG) {IJ.log("makeSiftDescriptors...done");}
		return richKeyPoints;
	}

	/*
	 * for debugging/illustrations only!
	 */
	public List<Double> findPeakOrientationIndices(float[] oh) {
		int nb = oh.length;
		List<Double> orientIndexes = new ArrayList<Double>(nb);
		// find the maximum entry in the orientation histogram 'oh'
		float maxh = oh[0];
		for (int k = 1; k < nb; k++) {
			if (oh[k] > maxh)
				maxh = oh[k];
		}

		if (maxh > 0.01f) { // ascertain minimum (non-zero) gradient energy
			// collect all peaks > 80% of the maximum entry in 'oh'
			float minh = maxh * (float) params.t_DomOr;
			for (int k = 0; k < nb; k++) { // hp ~ hc ~ ha
				// angles[k] = Float.NaN;
				float hc = oh[k]; // center value
				if (oh[k] > minh) { // value is min. 80% of global peak
					float hp = oh[(k - 1 + nb) % nb]; // previous histogram
					// value
					float hn = oh[(k + 1) % nb]; // next histogram value
					if (hc > hp && hc > hn) { // check if 'hc' is a local peak
						// interpolate orientation by a quadratic function
						// (parabola):
						double delta = interpolateQuadratic(hp, hc, hn);
						double k_max = (k + delta + nb) % nb; // interpolated
						// bin index, 0
						// <= km < nPhi
						// double phi_max = k_max * 2 * Math.PI / nb; // 0 <=
						// phi < 2 PI. Should be in range +/-PI?
						orientIndexes.add(k_max);
					}
				}
			}
		}
		return orientIndexes;
	}

	// THE REAL STUFF: Creating the SIFT Descriptors

	public List<SiftDescriptor> getSiftFeatures() {
		List<KeyPoint> keyPoints = getKeyPoints();
		List<SiftDescriptor> siftDescriptors = new ArrayList<SiftDescriptor>();
		for (KeyPoint c : keyPoints) {
			for (double phi_d : getDominantOrientations(c)) {
				SiftDescriptor sd = makeSiftDescriptor(c, phi_d);
				if (sd != null) {
					siftDescriptors.add(sd);
				}
			}
		}
		return siftDescriptors;
	}

	public List<KeyPoint> getKeyPoints() {
		List<KeyPoint> keyPts = new ArrayList<KeyPoint>();
		final int P = params.P;
		final int K = params.Q;
		for (int p = 0; p <= P-1; p++) {	// for every octave p
			for (int q = 0; q <= K-1; q++) {	// for every scale level q
				List<KeyPoint> extrema = findExtrema(p, q);
				for (KeyPoint e : extrema) {
					KeyPoint c = refineKeyPosition(D, e);
					if (c != null) {
						keyPts.add(c);
					}
				}
			}
		}
		return keyPts;
	}

	private List<KeyPoint> findExtrema(int p, int q) {
		final float tMag = (float) params.t_Mag;
		final float tExtrm = (float) params.t_Extrm;
		ScaleOctave Dp = D.getOctave(p);
		ScaleLevel Dpq = D.getScaleLevel(p, q);
		int M = Dpq.getWidth();
		int N = Dpq.getHeight();
		List<KeyPoint> E = new ArrayList<KeyPoint>();
		float scale = (float) D.getAbsoluteScale(p, q); //D.getScaleIndexFloat(p, q); needed?

		final float[][][] nh = new float[3][3][3];	// 3x3x3 neighborhood [q][u][v]
		for (int u = 1; u <= M-2; u++) {
			float x_real = (float) D.getRealX(p, u);	// for display purposes only
			for (int v = 1; v <= N-2; v++) {
				float y_real = (float) D.getRealY(p, v);	// for display purposes only
				if (Math.abs(Dpq.getf(u, v)) > tMag) {
					Dp.getNeighborhood(q, u, v, nh);	// CHANGE to use D not Dp!!
					if (isExtremum(nh, tExtrm)) {
						KeyPoint e = new KeyPoint(p, q, u, v, u, v, x_real, y_real, scale);
						E.add(e);
					}
				}
			}
		}
		return E;
	}

	private KeyPoint refineKeyPosition(DogScaleSpace D, KeyPoint e) { 
		//IJ.log("++Processing " + c.toString());
		int p = e.p, q = e.q, u = e.u, v = e.v;
		ScaleOctave Dp = D.getOctave(p);
		//ScaleLevel Dpq = Dp.getLevel(q);

		final double rMax = params.reMax;

		final float[][][] nh = new float[3][3][3];	// 3x3x3 neighborhood nh[q][u][v]
		final float[] grad = new float[3];			// gradient (dx, dy, ds)	
		final float[] d = new float[3];				// relocation displacement (dx, dy, ds)
		final float[][] hess = new float[3][3];		// 3D Hessian matrix:
		// | dxx dxy dxs |
		// | dxy dyy dys |
		// | dxs dys dss |
		final double tPeak = params.t_Peak;
		final int n_max = params.n_Refine;

		final float alphaMax = (float) (sqr(rMax + 1) / rMax);
		KeyPoint c = null;
		boolean done = false;
		int n = 1;
		while (!done && n <= n_max && Dp.isInside(q, u, v)) {	// CHANGE to use D instead of Dp!
			Dp.getNeighborhood(q, u, v, nh);	// CHANGE to use D instead of Dp!
			gradient(nh, grad);		// result in grad
			hessian(nh, hess);		// result in hess
			double detH = Matrix.determinant3x3(hess);
			if (Math.abs(detH) < EPSILON_F) {	// Hessian matrix has zero determinant?
				done = true;	// ignore this point and finish
			}
			else {
				float dxx = hess[0][0];	// extract 2x2 Hessian for calculating curvature ratio below
				float dxy = hess[0][1];
				float dyy = hess[1][1];
				float[][] invHess = Matrix.invert3x3(hess);	// note: inversion works by side effect!
				Matrix.multiply(invHess, grad, d);	// d <-- invHess . grad
				Matrix.multiply(d, -1);			// d <-- - d
				float xx = d[0];	// = x'
				float yy = d[1]; 	// = y'
				//				float ss = d[2];	// = sigma'
				if (Math.abs(xx) < 0.5f && Math.abs(yy) < 0.5f) {	// stay at this lattice point
					done = true;
					float Dpeak = nh[1][1][1] + 0.5f * (grad[0]*xx + grad[1]*yy + grad[2]*d[2]);
					float detHxy = dxx * dyy - dxy * dxy;
					if (Math.abs(Dpeak) > tPeak && detHxy > 0) {	// check peak magnitude
						float alpha = sqr(dxx + dyy) / detHxy;
						if (alpha <= alphaMax) { 					// check curvature ratio (edgeness)
							if (params.DEBUG) IJ.log(e.toString() + String.format(": added after %d tries, alpha = %f", n, alpha));
							c = e;	//  the passed keypoint is reused for the refined keypoint, with p,q unchanged
							c.x = u + xx;
							c.y = v + yy;
							c.x_real = (float) D.getRealX(p, c.x);
							c.y_real = (float) D.getRealY(p, c.y);
							c.scale  = (float) D.getAbsoluteScale(p, q);
						}
					}
				}
				else { // large displacement, move to a neighboring DoG cell at same level q by at most one unit
					u = u + Math.min(1, Math.max(-1, Math.round(xx)));	// move u by max +/-1
					v = v + Math.min(1, Math.max(-1, Math.round(yy)));	// move v by max +/-1
					// Note: we don't move along the scale axis!
				}
			}
			n = n + 1;
		}
		return c;
	}

	private boolean isExtremum(float[][][] nh, float tExtrm) {
		return isMin(nh, tExtrm) || isMax(nh, tExtrm);
	}

	boolean isMin(final float[][][] neighborhood, float tExtrm) {
		final float vct = neighborhood[1][1][1] + tExtrm; // center value + threshold
		// check 8 neighbors in scale plane q
		if (vct >= neighborhood[1][0][0]) return false;
		if (vct >= neighborhood[1][1][0]) return false;
		if (vct >= neighborhood[1][2][0]) return false;
		if (vct >= neighborhood[1][0][1]) return false;
		if (vct >= neighborhood[1][2][1]) return false;
		if (vct >= neighborhood[1][0][2]) return false;
		if (vct >= neighborhood[1][1][2]) return false;
		if (vct >= neighborhood[1][2][2]) return false;
		if (nhSize >= 10) {
			if (vct >= neighborhood[0][1][1]) return false;
			if (vct >= neighborhood[2][1][1]) return false;
			if (nhSize >= 18) {
				// check 4 more neighbors in scale plane q-1
				if (vct >= neighborhood[0][0][1]) return false;
				if (vct >= neighborhood[0][2][1]) return false;
				if (vct >= neighborhood[0][1][0]) return false;
				if (vct >= neighborhood[0][1][2]) return false;
				// check 4 more neighbors in scale plane q+1
				if (vct >= neighborhood[2][0][1]) return false;
				if (vct >= neighborhood[2][2][1]) return false;
				if (vct >= neighborhood[2][1][0]) return false;
				if (vct >= neighborhood[2][1][2]) return false;
				// optionally check 8 remaining neighbors (corners of cube):
				if (nhSize >= 26) {
					if (vct >= neighborhood[0][0][0]) return false;
					if (vct >= neighborhood[0][2][0]) return false;
					if (vct >= neighborhood[0][0][2]) return false;
					if (vct >= neighborhood[0][2][2]) return false;
					if (vct >= neighborhood[2][0][0]) return false;
					if (vct >= neighborhood[2][2][0]) return false;
					if (vct >= neighborhood[2][0][2]) return false;
					if (vct >= neighborhood[2][2][2]) return false;
				}
			}
		}
		return true;
	}

	boolean isMax(final float[][][] neighborhood, float tExtrm) {
		final float vct = neighborhood[1][1][1] - tExtrm; 	// center value - threshold
		// check 8 neighbors in scale plane q
		if (vct <= neighborhood[1][0][0]) return false;
		if (vct <= neighborhood[1][1][0]) return false;
		if (vct <= neighborhood[1][2][0]) return false;
		if (vct <= neighborhood[1][0][1]) return false;
		if (vct <= neighborhood[1][2][1]) return false;
		if (vct <= neighborhood[1][0][2]) return false;
		if (vct <= neighborhood[1][1][2]) return false;
		if (vct <= neighborhood[1][2][2]) return false;
		if (nhSize >= 10) {
			if (vct <= neighborhood[0][1][1]) return false;
			if (vct <= neighborhood[2][1][1]) return false;
			if (nhSize >= 18) {
				// check 4 more neighbors in scale plane q-1
				if (vct <= neighborhood[0][0][1]) return false;
				if (vct <= neighborhood[0][2][1]) return false;
				if (vct <= neighborhood[0][1][0]) return false;
				if (vct <= neighborhood[0][1][2]) return false;
				// check 4 more neighbors in scale plane q+1
				if (vct <= neighborhood[2][0][1]) return false;
				if (vct <= neighborhood[2][2][1]) return false;
				if (vct <= neighborhood[2][1][0]) return false;
				if (vct <= neighborhood[2][1][2]) return false;
				// optionally check 8 remaining neighbors (corners of cube):
				if (nhSize >= 26) {
					if (vct <= neighborhood[0][0][0]) return false;
					if (vct <= neighborhood[0][2][0]) return false;
					if (vct <= neighborhood[0][0][2]) return false;
					if (vct <= neighborhood[0][2][2]) return false;
					if (vct <= neighborhood[2][0][0]) return false;
					if (vct <= neighborhood[2][2][0]) return false;
					if (vct <= neighborhood[2][0][2]) return false;
					if (vct <= neighborhood[2][2][2]) return false;
				}
			}
		}
		return true;
	}

	/*
	 * Calculates the 3-dimensional gradient vector for the 3x3x3 neighborhood
	 * nh[s][x][y]. The result is stored in the supplied vector grad, to which a reference is 
	 * returned.
	 */
	float[] gradient(final float[][][] nh, final float[] grad) {
		// Note: factor 0.5f not needed, kept for clarity.
		grad[0] = 0.5f * (nh[1][2][1] - nh[1][0][1]);	// = dx
		grad[1] = 0.5f * (nh[1][1][2] - nh[1][1][0]);	// = dy
		grad[2] = 0.5f * (nh[2][1][1] - nh[0][1][1]);	// = ds
		return grad;
	}

	/* 
	 * Calculates the 3x3 Hessian matrix for the 3x3x3 neighborhood nh[s][i][j],
	 * with scale index s and spatial indices i, j.
	 * The result is stored in the supplied array hess, to which a reference is 
	 * returned.
	 */
	float[][] hessian(final float[][][] nh, final float[][] hess) {
		final float nh_111 = 2 * nh[1][1][1];
		final float dxx = nh[1][0][1] - nh_111 + nh[1][2][1];
		final float dyy = nh[1][1][0] - nh_111 + nh[1][1][2];
		final float dss = nh[0][1][1] - nh_111 + nh[2][1][1];

		final float dxy = (nh[1][2][2] - nh[1][0][2] - nh[1][2][0] + nh[1][0][0]) * 0.25f;
		final float dxs = (nh[2][2][1] - nh[2][0][1] - nh[0][2][1] + nh[0][0][1]) * 0.25f;
		final float dys = (nh[2][1][2] - nh[2][1][0] - nh[0][1][2] + nh[0][1][0]) * 0.25f;

		hess[0][0] = dxx;  hess[0][1] = dxy;  hess[0][2] = dxs;
		hess[1][0] = dxy;  hess[1][1] = dyy;  hess[1][2] = dys;
		hess[2][0] = dxs;  hess[2][1] = dys;  hess[2][2] = dss;
		return hess;
	}

	void printMatrix3x3(float[][] A) {
		IJ.log(String.format(Locale.US, "{{%.6f, %.6f, %.6f},", A[0][0], A[0][1], A[0][2]));
		IJ.log(String.format(Locale.US, " {%.6f, %.6f, %.6f},", A[1][0], A[1][1], A[1][2]));
		IJ.log(String.format(Locale.US, " {%.6f, %.6f, %.6f}}", A[2][0], A[2][1], A[2][2]));
	}

	/*
	 * Returns a list of orientations (angles) for the keypoint c.
	 */
	List<Double> getDominantOrientations(KeyPoint c) {
		float[] h_phi = getOrientationHistogram(c);
		smoothCircular(h_phi, params.n_Smooth);
		return findPeakOrientations(h_phi);
	}

	// Smoothes the array A in-place (i.e., destructively) in 'iterations' passes.
	void smoothCircular(float[] X, int n_iter) {
		final float[] H = {0.25f, 0.5f, 0.25f};	// filter kernel
		final int n = X.length;
		for (int i=0; i<n_iter; i++) {
			float s = X[0];
			float p = X[n-1];
			for (int j = 0; j <= n-2; j++) {
				float c = X[j];
				X[j] = H[0] * p + H[1] * X[j] + H[2] * X[j+1];
				p = c;
			}
			X[n-1] = H[0] * p + H[1] * X[n-1] + H[2] * s;
		}
	}

	/* 
	 * Extracts the peaks in the orientation histogram 'h_phi'
	 * and returns the corresponding angles in a (possibly empty) list. 
	 */
	List<Double> findPeakOrientations(float[] h_phi) {
		int n = h_phi.length;
		List<Double> angles = new ArrayList<Double>(n);
		// find the maximum entry in the orientation histogram 'h_phi'
		float h_max = h_phi[0];
		for (int k = 1; k < n; k++) {
			if (h_phi[k] > h_max)
				h_max = h_phi[k];
		}
		if (h_max > 0.01f) {	// ascertain minimum (non-zero) gradient energy 
			// collect all peaks > 80% of the maximum entry in 'oh'
			float h_min = h_max * 0.8f;
			for (int k = 0; k < n; k++) {	// hp ~ hc ~ ha
				float hc = h_phi[k];						// center value
				if (hc > h_min) {		// value is min. 80% of global peak
					float hp = h_phi[(k - 1 + n) % n];	// previous histogram value
					float hn = h_phi[(k + 1) % n];		// next histogram value
					if (hc > hp && hc > hn) {			// check if 'hc' is a local peak
						// interpolate orientation by a quadratic function (parabola):
						double delta = interpolateQuadratic(hp, hc, hn);
						double k_max = (k + delta + n) % n;	// interpolated bin index, 0 <= km < nPhi
						double phi_max = k_max * 2 * Math.PI / n;	// 0 <= phi < 2 PI. Should be in range +/-PI?
						angles.add(phi_max);
					}
				}
			}
		}
		return angles;
	}

	float[] getOrientationHistogram(KeyPoint c) {
		final int n_phi = params.n_Orient;
		final int K = params.Q;

		ScaleLevel Gpq = G.getScaleLevel(c.p, c.q);
		final float[] h_phi = new float[n_phi];	// automatically initialized to zero
		final int M = Gpq.getWidth(), N = Gpq.getHeight();
		final double x = c.x, y = c.y;

		final double sigma_w = 1.5 * params.sigma_0 * Math.pow(2,(double)c.q/K);
		final double sigma_w22 = 2 * sigma_w * sigma_w;
		final double r_w = Math.max(1, 2.5 * sigma_w);
		final double r_w2 = r_w * r_w;

		final int u_min = Math.max((int)Math.floor(x - r_w), 1);
		final int u_max = Math.min((int)Math.ceil(x + r_w), M-2);
		final int v_min = Math.max((int)Math.floor(y - r_w), 1);
		final int v_max = Math.min((int)Math.ceil(y + r_w), N-2);

		double[] gradPol = new double[2]; // gradient magnitude/orientation

		for (int u = u_min; u <= u_max; u++) {
			double dx = u - x;											// distance to feature's center
			for (int v = v_min; v <= v_max; v++) {
				double dy = v - y;
				double r2 = dx * dx + dy * dy;	// squared distance from center
				if (r2 < r_w2) {					// inside limiting circle
					Gpq.getGradientPolar(u, v, gradPol);
					double E = gradPol[0], phi = gradPol[1];						// gradient magnitude/orientation
					double wG = Math.exp(-(dx*dx + dy*dy)/sigma_w22);		// Gaussian weight
					double z = E * wG;
					double k_phi = n_phi * phi / (2 * Math.PI);				// continuous histogram bin index
					double alpha = k_phi - Math.floor(k_phi);				// weight alpha
					int k_0 = mod((int)Math.floor(k_phi), n_phi);			// lower histogram index
					int k_1 = mod(k_0 + 1, n_phi);							// upper histogram index
					h_phi[k_0] = (float) (h_phi[k_0] + (1 - alpha) * z);	// distribute z into bins k_0, k_1
					h_phi[k_1] = (float) (h_phi[k_1] + alpha * z);
				}
			}
		}
		return h_phi;
	}

	SiftDescriptor makeSiftDescriptor(KeyPoint c, double phi_d) {
		final int p = c.p, q = c.q;
		final double x = c.x, y = c.y;

		ScaleLevel Gpq = G.getScaleLevel(p, q);
		final int M = Gpq.getWidth(), N = Gpq.getHeight(), K = G.getQ();

		double sigma_q = G.getSigma_0() * Math.pow(2, (double) q / K);	// = Gpq.getAbsoluteScale() ?? decimated scale
		double w_d = params.s_Desc * sigma_q;		// descriptor width
		double sigma_d = 0.25 * w_d;	// width of Gaussian weighting function
		double sigma_d2 = 2 * sigma_d * sigma_d;
		double r_d = 2.5 * sigma_d;		// limiting radius
		double r_d2 = r_d * r_d;		// squared limiting radius

		double sc = 1.0/w_d;					// scale to canonical frame
		double sin_phi_d = Math.sin(-phi_d);	// precalculated sine
		double cos_phi_d = Math.cos(-phi_d);	// precalculated cosine

		//		Debug(logvar(p,"p") + logvar(q,"q") + logvar(w_d,"w_d"));

		int u_min = Math.max((int)Math.floor(x - r_d), 1);
		int u_max = Math.min((int)Math.ceil(x + r_d), M - 2);
		//		Debug(logvar(x,"x") + logvar(u_min,"u_min") + logvar(u_max,"u_max"));

		int v_min = Math.max((int)Math.floor(y - r_d), 1);
		int v_max = Math.min((int)Math.ceil(y + r_d), N - 2);
		//		Debug(logvar(y,"y") + logvar(v_min,"v_min") + logvar(v_max,"v_max"));

		// create the 3D orientation histogram, initialize to zero:
		final int n_Spat = params.n_Spat;
		final int n_Angl = params.n_Angl;
		double[][][] h_grad = new double[n_Spat][n_Spat][n_Angl];
		double[] gmo = new double[2];	// gradient magnitude/orientation

		for (int u = u_min; u <= u_max; u++) {
			double dx = u - x;
			for (int v = v_min; v <= v_max; v++) {
				double dy = v - y;
				double r2 = sqr(dx) + sqr(dy);	// squared distance from center
				if (r2 < r_d2) {					// inside limiting circle
					// map to the canonical coordinate frame, ii,jj \in [-1/2, +1/2]:
					double uu = sc * (cos_phi_d * dx - sin_phi_d * dy);
					double vv = sc * (sin_phi_d * dx + cos_phi_d * dy);
					// calculate the gradient of Gaussian scale level (p,q) at position (u,v):
					Gpq.getGradientPolar(u, v, gmo);
					double E = gmo[0];			// gradient magnitude
					double phi = gmo[1];		// gradient orientation
					double phi_norm = mod(phi - phi_d, PI2);// normalized gradient orientation	
					double w_G = Math.exp(-r2/sigma_d2);	// Gaussian weight
					double z = E * w_G;						// quantity to accumulate
					updateGradientHistogram(h_grad, uu, vv, phi_norm, z);
				}
			}
		}
		int[] f_int = makeFeatureVector(h_grad);
		double sigma_pq = G.getAbsoluteScale(p, q);
		double x_real = G.getRealX(p, x);
		double y_real = G.getRealY(p, y);
		return new SiftDescriptor(x_real, y_real, sigma_pq, phi_d, f_int);
	}

	private void updateGradientHistogram(double[][][] h_grad, double uu, double vv, double phi_norm, double z) {
		final int n_Spat = params.n_Spat;
		final int n_Angl = params.n_Angl;

		double ii = n_Spat * uu + 0.5 * (n_Spat - 1);	// continuous spatial histogram index i'
		double jj = n_Spat * vv + 0.5 * (n_Spat - 1);	// continuous spatial histogram index j'
		double kk = phi_norm * (n_Angl / PI2);			// continuous orientation histogram index k'

		int i0 = (int) Math.floor(ii);
		double alpha0 = 1.0 - (ii - i0);
		int i1 = i0 + 1;
		double alpha1 = 1.0 - alpha0;

		int j0 = (int) Math.floor(jj);
		double beta0 = 1.0 - (jj - j0);
		int j1 = j0 + 1;
		double beta1 = 1.0 - beta0;

		int k0 = mod((int)Math.floor(kk), n_Angl);	
		double gamma0 = 1.0 - (kk - Math.floor(kk));
		int k1 = (k0 + 1) % n_Angl;			// k0 >= 0
		double gamma1 = 1.0 - gamma0;

		final int[] iA = {i0, i1};	// index arrays used in loops below
		final int[] jA = {j0, j1};
		final int[] kA = {k0, k1};

		final double[] alphaA = {alpha0, alpha1};
		final double[] betaA  = {beta0, beta1};
		final double[] gammaA = {gamma0, gamma1};

		// distribute z over 8 adjacent spatial/angular bins:
		for (int a = 0; a <= 1; a++) {
			int i = iA[a];
			if (i >= 0 && i < n_Spat) {
				double alpha = alphaA[a];
				for (int b = 0; b <= 1; b++) {
					int j = jA[b];
					if (j >= 0 && j < n_Spat) {
						double beta = betaA[b];
						for (int c = 0; c <= 1; c++) {
							int k = kA[c];
							double gamma = gammaA[c];
							h_grad[i][j][k] = h_grad[i][j][k] + z * alpha * beta * gamma;

						}
					}
				}
			}
		}
	}

	private int[] makeFeatureVector(double[][][] h_grad) {
		final int n_Spat = params.n_Spat;
		final int n_Angl = params.n_Angl;
		float[] f = new float[n_Spat * n_Spat * n_Angl];
		// flatten 3D histogram to a 1D vector
		// the histogram is vectorized such that k (phi) is the fastest
		// varying index, followed by j and i (which is the slowest index).
		// Note: j (v) is the slowest in VLFEAT (i,j swapped)
		int m = 0;
		for (int i = 0; i < n_Spat; i++) {
			for (int j = 0; j < n_Spat; j++) {
				for (int k = 0; k < n_Angl; k++) {
					f[m] = (float) h_grad[i][j][k];
					m = m + 1;
				}
			}
		}
		normalize(f);
		clipPeaks(f, (float) params.t_Fclip);	
		normalize(f);
		return mapToIntegers(f, (float) params.s_Fscale);
	}

	private void normalize(float[] x) {
		final double norm = normL2(x);
		if (norm > EPSILON_F) {
			final float s = (float) (1.0 / norm);
			for (int i=0; i<x.length; i++) {
				x[i] = s * x[i];
			}
		}
	}

	private void clipPeaks(float[] x, float xmax) {
		for (int i=0; i<x.length; i++) {
			if (x[i] > xmax) {
				x[i] = xmax;
			}
		}
	}

	private int[] mapToIntegers(float[] x, float s) {
		int[] ivec = new int[x.length];
		for (int i=0; i<x.length; i++) {
			ivec[i] = Math.round(s * x[i]);
		}
		return ivec;
	}

	//  auxiliary methods -------------------------

	// also in imagingbook.math.Arithmetic.java
	private float sqr(float x) {
		return x*x;
	}

	// also in imagingbook.math.Arithmetic.java
	private double sqr(double x) {
		return x*x;
	}

	// also in imagingbook.math.Arithmetic.java
	private int mod(int a, int b) {
		if (b == 0)
			return a;
		if (a * b >= 0)
			return a - b * (a / b);	
		else
			return a - b * (a / b - 1);
	}

	// also in imagingbook.math.Arithmetic.java
	private double mod(double a, double n) {
		return a - n * Math.floor(a / n);
	}

	// also in imagingbook.math.Matrix.java
	private double normL2(float[] vec) {
		double sum = 0;
		for (float x : vec) {
			sum = sum + (x * x);
		}
		return Math.sqrt(sum);
	}

	/*
	 * Calculate the extremal position from 3 discrete function values 
	 * at x = -1, 0, +1: f(-1) = 'y1', f(0) = 'y2', f(+1) = 'y3'.
	 */
	private float interpolateQuadratic(float y1, float y2, float y3) {
		float a = (y1 - 2*y2 + y3) / 2;
		if (Math.abs(a) < 0.00001f) {
			throw new IllegalArgumentException("quadratic interpolation failed " 
					+ a + " " + y1 + " " + y2 + " " + y3);
		}
		float b = (y3 - y1) / 2;
		//		float c = y2;
		float x_extrm = -b / (2*a);		// extremal position
		//		float y_extrm = a*x*x + b*x + c;	// extremal value (not needed)
		return x_extrm;	// x is in [-1,+1]
	}

	// -------------------------------------------

	void print(float[] arr) {
		int linelength = 16;
		StringBuilder sb = new StringBuilder();
		Formatter fm = new Formatter(sb, Locale.US);
		for (int i = 0; i < arr.length; i++) {
			if (i > 0 && i % linelength == 0) {
				IJ.log(sb.toString());
				sb.setLength(0);
			}
			fm.format(" %.2f", arr[i]);
		}
		IJ.log(sb.toString());
		fm.close();
	}

	String logvar(float x, String name) {
		return name + " = " + String.format("%.3f ", x);
	}

	String logvar(double x, String name) {
		return name + " = " + String.format("%.3f ", x);
	}

	String logvar(int x, String name) {
		return name + " = " + x + " ";
	}

	void Debug(String s) {
		IJ.log(s);
	}

	void Stop() {
		throw new IllegalArgumentException("HALTED");
	}
	
	public void printGaussianScaleSpace() {
		G.print();
	}
}
