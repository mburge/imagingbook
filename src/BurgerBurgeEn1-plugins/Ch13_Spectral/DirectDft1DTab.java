package Ch13_Spectral;

import imagingbook.lib.math.Complex;


/** 
 * Direct implementation of the one-dimensional DFT for arbitrary signal lengths.
 * Uses tabulated sin/cos-values for efficiency.

This DFT uses the same definition as Mathematica. Example:
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
class DirectDft1DTab {

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

	//direct DFT implementation using tabulated sin/cos-values (more efficient)
	static Complex[] DFT(Complex[] g, boolean forward) {
		int M = g.length;
		double[] cosTable = makeCosTable(M);
		double[] sinTable = makeSinTable(M);
		double s = 1 / Math.sqrt(M); //common scale factor
		Complex[] G = new Complex[M];
		for (int u = 0; u < M; u++) {
			double sumRe = 0;
			double sumIm = 0;
			for (int m = 0; m < M; m++) {
				double gRe = g[m].re;
				double gIm = g[m].im;
				int k = (u * m) % M;
				double cosPhi = cosTable[k];
				double sinPhi = sinTable[k];
				if (forward)
					sinPhi = -sinPhi;
				//complex multiplication: (gRe + i gIm) * (cosPhi + i sinPhi)
				sumRe += gRe * cosPhi - gIm * sinPhi;
				sumIm += gRe * sinPhi + gIm * cosPhi;
			}
			G[u] = new Complex(s * sumRe, s * sumIm);
		}
		return G;
	}
	
	static double[] makeCosTable(int M){
		double[] table = new double[M];
		for (int i=0; i<M; i++){
			table[i]= Math.cos(2*Math.PI*i/M);
		}
		return table;
	}
	
	static double[] makeSinTable(int M){
		double[] table = new double[M];
		for (int i=0; i<M; i++){
			table[i]= Math.sin(2*Math.PI*i/M);
		}		
		return table;
	}
	
}


