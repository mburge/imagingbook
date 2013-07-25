package Ch13_Spektraltechniken;

import imagingbook.lib.math.Complex;

/** 
 * Direct implementation of the one-dimensional DFT for arbitrary signal lengths.
 * This DFT uses the same definition as Mathematica. Example:
	> Fourier[{1, 2, 3, 4, 5, 6, 7, 8}, FourierParameters -> {0, -1}]
	{12.7279 + 0. i, 
	-1.41421 + 3.41421 i, 
	-1.41421 + 1.41421 i, 
	-1.41421 + 0.585786 i, 
	-1.41421 + 0. i, 
	-1.41421 - 0.585786 i, 
	-1.41421 - 1.41421 i, 
	-1.41421 - 3.41421 i}
*/
class DirectDft1D {

	//test example
	public static void main(String[] args) {
		double[] signal = { 1, 2, 3, 4, 5, 6, 7, 8 };
		Complex[] g = Complex.makeComplexVector(signal);

		Complex.printComplexVector(g, "Signal");
		
		//compute forward DFT
		Complex[] G = DFT(g, true);
		Complex.printComplexVector(G, "Spectrum");

		//compute inverse DFT
		Complex[] iG = DFT(G, false);
		Complex.printComplexVector(iG, "Reconstructed signal");
	}

	//direct DFT implementation 
	static Complex[] DFT(Complex[] g, boolean forward) {
		int M = g.length;
		double s = 1 / Math.sqrt(M); //common scale factor
		Complex[] G = new Complex[M];
		for (int m = 0; m < M; m++) {
			double sumRe = 0;
			double sumIm = 0;
			double phim = 2 * Math.PI * m / M;
			for (int u = 0; u < M; u++) {
				double gRe = g[u].re;
				double gIm = g[u].im;
				double cosw = Math.cos(phim * u);
				double sinw = Math.sin(phim * u);
				if (!forward) // inverse transform
					sinw = -sinw;
				//complex mult: (gRe + i gIm) * (cosw + i sinw)
				sumRe += gRe * cosw + gIm * sinw;
				sumIm += gIm * cosw - gRe * sinw;
			}
			G[m] = new Complex(s * sumRe, s * sumIm);
		}
		return G;
	}
}

