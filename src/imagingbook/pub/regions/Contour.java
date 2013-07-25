/*******************************************************************************
 * This source code is made available as supplement to the printed textbooks on 
 * 'Digital Image Processing', authored by Wilhelm Burger and Mark J. Burge and 
 * published by Springer-Verlag. Note that this code comes with absolutely no 
 * warranty of any kind and the authors reserve the right to make changes to 
 * the code without notice at any time. See http://www.imagingbook.com for 
 * details and licensing conditions. Last update: 2013.
 ******************************************************************************/

package imagingbook.pub.regions;

import ij.IJ;

import java.awt.Point;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class Contour implements Comparable<Contour> {
	
	static int INITIAL_SIZE = 50;
	
	private int label;
	private List<Point> points;
	
	public Contour (int label) {
		this.label = label;
		points = new ArrayList<Point>(INITIAL_SIZE);
	}
	
	public void addPoint (Point p) {
		points.add(p);
	}
	
	//--------------------- drawing ------------	
		
	Path2D makePolygon() {
		return makePolygon(0.0, 0.0);
	}
	
	public Path2D makePolygon(double xOffset, double yOffset) {
		Path2D path = new Path2D.Float();
		if (points!=null || points.size()>0) {
			Point[] pnts = points.toArray(new Point[0]);
			if (pnts.length > 1){
				path.moveTo(pnts[0].x + xOffset, pnts[0].y + yOffset);
				for (int i=1; i<pnts.length; i++) {
					path.lineTo(pnts[i].x + xOffset,  pnts[i].y + yOffset);
				}
				path.closePath();
			}
			else {	// mark single pixel region "X"
				double x = pnts[0].x;
				double y = pnts[0].y;
				path.moveTo(x + xOffset - 0.5, y + yOffset - 0.5);
				path.lineTo(x + xOffset + 0.5, y + yOffset + 0.5);
				path.moveTo(x + xOffset - 0.5, y + yOffset + 0.5);
				path.lineTo(x + xOffset + 0.5, y + yOffset - 0.5);
			}
		}
		return path;
	}

	//--------------------- chain code ------------	

	/*
	byte[] makeChainCode8() {
		int m = points.size();
		if (m>1){
			int[] xPoints = new int[m];
			int[] yPoints = new int[m];
			int k = 0;
			Iterator<Point> itr = points.iterator();
			while (itr.hasNext() && k < m) {
				Point cn = itr.next();
				xPoints[k] = cn.x;
				yPoints[k] = cn.y;
				k = k + 1;
			}
			return null;
		}
		else {	// use circles for isolated pixels
			//Point cn = 
				points.get(0);
			return null;
		}
	}
	*/
	
	//--------------------- retrieve contour points -------
	
	public List<Point> getPointList() {
		return points;
	}
	
	public Point[] getPointArray() {
		return points.toArray(new Point[0]);
	}
		
	//--------------------- contour statistics ------------
	
	public int getLength() {
		return points.size();
	}
	
	public int getLabel() {
		return label;
	}
	
	//--------------------- debug methods ------------------
	
	public void printPoints (){
		for (Point pt: points) {
			IJ.log(pt.toString());
		}
	}
	
	public String toString(){
		return
			"Contour " + label + ": " + this.getLength() + " points";
	}
	
	//--------------------- compare method for sorting ------------------
	
	// Compare method for sorting ontours by length (longer contours at front)
	public int compareTo(Contour c2) {
		return c2.points.size() - this.points.size();
	}

}
