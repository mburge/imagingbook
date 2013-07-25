package Ch11_BinaereRegionen;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Make_Projections implements PlugInFilter {
	
	ImagePlus origImage = null;
	String origTitle = null;
	static boolean verbose = true;
	static final int PSIZE = 150;	// size of projection bars
	static final int FOREGROUND = 0;
	static final int BACKGROUND = 255;
	
	public int setup(String arg, ImagePlus im) { 
    origImage = im;
		origTitle = im.getTitle();
		return DOES_8G + NO_CHANGES; 
	}
	
	public void run(ImageProcessor ip) {
		int M = ip.getWidth();
		int N = ip.getHeight();
		int[] horProj = new int[N];
		int[] verProj = new int[M];
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				int p = ip.getPixel(u, v);
				horProj[v] +=  p;
				verProj[u] +=  p;
			}
		}
		
		// make the horizontal projection:
		ByteProcessor hP = new ByteProcessor(PSIZE,N);
		hP.setValue(BACKGROUND); hP.fill();
		hP.setValue(FOREGROUND);
		double maxWhite = M * 255;
		for (int row = 0; row < N; row++) {
			double black = (1 - horProj[row] / maxWhite);	// percentage of black in line 'row'
			int k = (int) (black * PSIZE);
			if (k > 0) {
				hP.drawLine(0, row, k, row);
			}
		}
		ImagePlus hImg = new ImagePlus("Horiz. Proj", hP);
		hImg.show();
				
		// make the vertical projection:
		ByteProcessor vP = new ByteProcessor(M,PSIZE);
		vP.setValue(BACKGROUND); vP.fill();
		vP.setValue(FOREGROUND);
		maxWhite = N * 255;
		for (int col = 0; col < M; col++) {
			double black = (1 - verProj[col] / maxWhite);	// percentage of black in column 'col'
			int k = (int) (black * PSIZE);
			if (k > 0) {
				vP.drawLine(col, 0, col, k);
			}
		}
		ImagePlus vImg = new ImagePlus("Vert. Proj", vP);
		vImg.show();	
	}

}
