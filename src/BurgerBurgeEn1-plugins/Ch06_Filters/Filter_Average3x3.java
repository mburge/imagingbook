package Ch06_Filters;

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

public class Filter_Average3x3 implements PlugInFilter {

    public int setup(String arg, ImagePlus imp) {
        return DOES_8G;
    }

    public void run(ImageProcessor orig) {
        int w = orig.getWidth();
        int h = orig.getHeight(); 
        ImageProcessor copy = orig.duplicate();

		for (int v = 1; v <= h - 2; v++) {
			for (int u = 1; u <= w - 2; u++) {
                //compute filter result for position (u,v)
                int sum = 0;
				for (int j = -1; j <= 1; j++) {
					for (int i = -1; i <= 1; i++) {
						int p = copy.getPixel(u + i, v + j);
                        sum = sum + p;
                    }
                }
                int q = (int) (sum / 9.0);
				orig.putPixel(u, v, q);
            }
        }
    }

}
