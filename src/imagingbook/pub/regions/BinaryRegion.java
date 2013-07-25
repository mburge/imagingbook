/*******************************************************************************
 * This source code is made available as supplement to the printed textbooks on 
 * 'Digital Image Processing', authored by Wilhelm Burger and Mark J. Burge and 
 * published by Springer-Verlag. Note that this code comes with absolutely no 
 * warranty of any kind and the authors reserve the right to make changes to 
 * the code without notice at any time. See http://www.imagingbook.com for 
 * details and licensing conditions. Last update: 2013.
 ******************************************************************************/

package imagingbook.pub.regions;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


/**
 * This class is used to incrementally compute and maintain
 * the statistics of a binary region.
 * Updated: 2010-11-19
 */
public class BinaryRegion implements Comparable<BinaryRegion> {
	private int label = 0;				// the label of THIS region
	private int size = 0;
	private double xc = Double.NaN;
	private double yc = Double.NaN;
	private int left = Integer.MAX_VALUE;
	private int right = -1;
	private int top = Integer.MAX_VALUE;
	private int bottom = -1;
	
	private Contour outerContour;
	private List<Contour> innerContours;
	
	// auxiliary variables
	private int x_sum  = 0;
	private int y_sum  = 0;
	private int x2_sum = 0;
	private int y2_sum = 0;
	
	// ------- constructor --------------------------

	public BinaryRegion(int label){
		this.label = label;
		outerContour = null;
		innerContours = null;
	}

	
	// ------- public methods --------------------------
	
	public int getLabel() {
		return this.label;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public Rectangle getBoundingBox() {
		if (left == Integer.MAX_VALUE) 
			return null;
		else
			return new Rectangle(left, top, right-left+1, bottom-top+1);
	}
	
	public Point2D.Double getCenter(){
		if (Double.isNaN(xc))
			return null;
		else
			return new Point2D.Double(xc, yc);
	}
	
	/* Use this method to add a single pixel to this region. Updates summation
	 * and boundary variables used to calculate various region statistics.
	 */
	public void addPixel(int x, int y){
		size = size + 1;
		x_sum = x_sum + x;
		y_sum = y_sum + y;
		x2_sum = x2_sum + x*x;
		y2_sum = y2_sum + y*y;
		if (x<left) left = x;
		if (y<top)  top = y;
		if (x>right) right = x;
		if (y>bottom) bottom = y;
	}
	
	/* Call this method to update the region's statistics. For now only the 
	 * center coordinates (xc, yc) are updated. Add additional statements as
	 * needed to update your own region statistics.
	 */
	public void update(){
		if (size > 0){
			xc = (double) x_sum / size;
			yc = (double) y_sum / size;
		}
	}
	
	public Contour getOuterContour() {
		return outerContour;
	}
	
	void setOuterContour(Contour contr) {
		outerContour = contr;
	}
	
	public List<Contour> getInnerContours() {
		return innerContours;
	}
	
	void addInnerContour(Contour contr) {
		if (innerContours == null) {
			innerContours = new LinkedList<Contour>();
		}
		innerContours.add(contr);
	}
	
	public String toString(){
		Formatter fm = new Formatter(new StringBuilder(), Locale.US);
		fm.format("Region %d", label);
		fm.format(", area = %d", size);
		fm.format(", bounding box = (%d, %d, %d, %d)", left, top , right, bottom );
		fm.format(", centroid = (%.2f, %.2f)", xc, yc);
		fm.format(", holes = %d", (innerContours==null) ? 0 :innerContours.size());
		String s = fm.toString();
		fm.close();
		return s;
	}

	// Compare method for sorting by region size (larger regions at front)
	public int compareTo(BinaryRegion r2) {
		return r2.size - this.size;
	}

}
