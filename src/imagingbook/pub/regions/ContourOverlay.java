/*******************************************************************************
 * This source code is made available as supplement to the printed textbooks on 
 * 'Digital Image Processing', authored by Wilhelm Burger and Mark J. Burge and 
 * published by Springer-Verlag. Note that this code comes with absolutely no 
 * warranty of any kind and the authors reserve the right to make changes to 
 * the code without notice at any time. See http://www.imagingbook.com for 
 * details and licensing conditions. Last update: 2013.
 ******************************************************************************/

package imagingbook.pub.regions;

import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.ShapeRoi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.util.List;

public class ContourOverlay extends Overlay {

	static float defaultStrokeWidth = 1.0f; //0.2f;
	static Color defaultColor = Color.red;

//	static int capsstyle = BasicStroke.CAP_ROUND;
//	static int joinstyle = BasicStroke.JOIN_ROUND;
//	static float[] outerDashing = {12, 4}; 
//	static float[] innerDashing = {12, 4}; 
//	static boolean DRAW_CONTOURS = true;
	
	
	public ContourOverlay() {
		super();
	}
	
	public void addContours(List<Contour> contours) {
		addContours(contours, defaultColor, defaultStrokeWidth);
	}
	
	public void addContours(List<Contour> contours, Color color, float strokeWidth) {
		BasicStroke stroke = new BasicStroke(strokeWidth);
		for (Contour c : contours) {
			Shape s = c.makePolygon(0.5, 0.5);
			Roi roi = new ShapeRoi(s);
			roi.setStrokeColor(color);
			roi.setStroke(stroke);
			add(roi);
		}
	}

}



