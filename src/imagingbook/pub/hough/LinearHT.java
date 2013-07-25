package imagingbook.pub.hough;

import ij.IJ;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LinearHT {
	final ByteProcessor ip;	// reference to original image
	final double xc, yc; 		// x/y-coordinate of image center
	final double rMax;			// maximum radius
	final int nAng;				// number of steps for the angle  (a = 0 ... PI)
	final int nRad; 			// number of steps for the radius (r = -r_max ... +r_max)
	final int cRad;				// array index for zero radius (r = 0)
	final double dAng;			// increment of angle
	final double dRad; 			// increment of radius
	final int[][] houghArray; 	// Hough accumulator array
	final int[][] localMaxArray;	// array of accumulator local maxima
	
	public double getXc() {
		return xc;
	}
	
	public double getYc() {
		return yc;
	}

	// --------------  public constructor(s) ------------------------
	
	public LinearHT(ByteProcessor ip, int nAng, int nRad) {
		this.ip = ip;
		this.nAng = nAng; 
		this.dAng = Math.PI / nAng;
		this.nRad = nRad;
		this.cRad = nRad / 2;
		this.xc = ip.getWidth() / 2; 
		this.yc = ip.getHeight() / 2;
		this.rMax = Math.sqrt(xc * xc + yc * yc);
		this.dRad = (2 * rMax) / nRad;
		this.houghArray = makeHoughArray(); 
		fillHoughArray();
		this.localMaxArray = findLocalMaxima();
	}
	
	// --------------  public methods ------------------------
	
	/**
	 * Find and return the parameters of the n strongest lines (with max. pixel counts).
	 * Could be implemented more efficiently with insert-sort.
	 */
	public List<HoughLine> getMaxLines(int maxCnt, int minPts) {
		HoughLine[] linArr = new HoughLine[maxCnt];
		// create an array of n blank HoughLine objects (with count = -1)
		for (int i = 0; i < linArr.length; i++) {
			linArr[i] = new HoughLine(0, 0, -1);
		}

		for (int ri = 0; ri < nRad; ri++) {
			for (int ai = 0; ai < nAng; ai++) {
				int hcount = localMaxArray[ai][ri];
				if (hcount >= minPts) {
					HoughLine last = linArr[linArr.length - 1];
					if (hcount > last.count) {
						last.angle = realAngle(ai);
						last.radius = realRadius(ri);
						last.count = hcount;
						Arrays.sort(linArr);	// this may be more efficient with insert sort
					}
				}
			}
		}
		// linArr is sorted by count (highest counts first)
		List<HoughLine> lineList = new ArrayList<HoughLine>();
		for (HoughLine hl : linArr) {
			if (hl.getCount() < minPts) break;
			lineList.add(hl);
		}
		return lineList;
	}
	
	public boolean pixelIsForeground(int u, int v) {
		return ip.get(u, v) > 0;
	}
	
	// We use a FloatProcessor since accumulator values may be large.
	public FloatProcessor getAccumulatorImage() {
		FloatProcessor fp = new FloatProcessor(nAng,nRad);
		for (int ri = 0; ri < nRad; ri++) {
			for (int ai = 0; ai < nAng; ai++) {
				fp.setf(ai, ri, houghArray[ai][ri]);
			}
		}
		fp.resetMinAndMax();
		return fp;
	}
	
	public FloatProcessor getLocalMaxImage() {
		FloatProcessor fp = new FloatProcessor(nAng,nRad);
		for (int ri = 0; ri < nRad; ri++) {
			for (int ai = 0; ai < nAng; ai++) {
				fp.setf(ai, ri, localMaxArray[ai][ri]);
			}
		}
		fp.resetMinAndMax();
		return fp;
	}
	
	// --------------  nonpublic methods ------------------------
	
	private void fillHoughArray() {
		IJ.showStatus("filling accumulator ...");
		int h = ip.getHeight();
		int w = ip.getWidth();
		for (int v = 0; v < h; v++) {
			IJ.showProgress(v, h);
			for (int u = 0; u < w; u++) {
				if (pixelIsForeground(u, v)) {		// this is a forground pixel
					doPixel(u, v);
				}
			}
		}
		IJ.showProgress(1, 1);
	}
	
	private int[][] makeHoughArray() {
		int[][] houghArray = new int[nAng][nRad]; // cells initialized to zero!
		return houghArray;
	}


	private void doPixel(int u, int v) {
		final double x = u - xc;
		final double y = v - yc;
		for (int ai = 0; ai < nAng; ai++) {
			double theta = dAng * ai;
			double r = x * Math.cos(theta) + y * Math.sin(theta);
			int ri =  cRad + (int) Math.rint(r / dRad);
			if (ri >= 0 && ri < nRad) {
				houghArray[ai][ri]++;
			}
		}
	}
	
	private int[][] findLocalMaxima() {
		IJ.showStatus("finding local maxima");
		int[][] lmA = new int[nAng][nRad]; //initialized to zero
		for (int ai = 0; ai < nAng; ai++) {
			// angle dimension is treated cyclically:
			int a1 = (ai > 0) ? ai-1 : nAng-1;
			int a2 = (ai < nAng-1) ? ai+1 : 0;
			for (int ri = 1; ri < nRad - 1; ri++) {
				int ha = houghArray[ai][ri];
				// this test is critical if 2 identical cell values 
				// appear next to each other!
				boolean ismax =
					ha > houghArray[a1][ri-1] &&
					ha > houghArray[a1][ri]   &&
					ha > houghArray[a1][ri+1] &&
					ha > houghArray[ai][ri-1] &&
					ha > houghArray[ai][ri+1] &&
					ha > houghArray[a2][ri-1] &&
					ha > houghArray[a2][ri]   &&
					ha > houghArray[a2][ri+1] ;
				if (ismax)
					lmA[ai][ri] = ha;
			}
		}
		return lmA;
	}
	
	//returns real angle for angle index ai
	private double realAngle(int ai) {	
		return ai * dAng;
	}
	
	//returns real radius for radius index ri (with respect to image center <uc, vc>)
	private double realRadius(int ri) {	
		return (ri - cRad) * dRad;
	}
	
	/*
	 * This class represents a straight line in Hessian normal form.
	 */
	public class HoughLine implements Comparable<HoughLine> {
		
		double angle;
		double radius;
		int count;

		// no public constructor
		private HoughLine(double angle, double radius, int count){
			this.angle  = angle;	
			this.radius = radius;	
			this.count  = count;	
		}
		
		public double getAngle() {
			return angle;
		}
		
		public double getRadius() {
			return radius;
		}
		
		public int getCount() {
			return count;
		}
		
		public double getXc() {
			return xc;
		}
		
		public double getYc() {
			return yc;
		}
		
		
		/**
		 * Returns the perpendicular distance between this line and the point (x, y).
		 * The result may be positive or negative, depending on which side of
		 * the line (x, y) is located.
		 */
		public double getDistance(double x, double y) {
			final double xs = x - xc;
			final double ys = y - yc;
			return Math.cos(angle) * xs + Math.sin(angle) * ys - radius;
		}
		
		public int compareTo (HoughLine hl){
			HoughLine hl1 = this;
			HoughLine hl2 = hl;
			if (hl1.count > hl2.count)
				return -1;
			else if (hl1.count < hl2.count)
				return 1;
			else
				return 0;
		}
		
		public String toString() {
			return String.format("%s <angle=%.3f, radius=%.3f, count=%d>", 
					HoughLine.class.getSimpleName(), angle, radius, count);
		}

	} // end of class HoughLine
	
} // end of class LinearHT







