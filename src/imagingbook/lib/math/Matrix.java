/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.math;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;

import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;

/*
 * This class contains a collection of static methods for calculations
 * with vectors and matrices using native Java arrays without any enclosing 
 * objects structures. 
 * Matrices are simply two-dimensional array M[r][c], where r is the row index
 * and c is the column index (as common in linear algebra). This means that
 * matrices are really vectors of row vectors.
 * 
 * Author: W: Burger
 * Revised: 2013-02
 */

public class Matrix {
	
	static {
		Locale.setDefault(Locale.US);
	}
	
	// vector and matrix creation

	public static double[] createDoubleVector(int length) {
		return new double[length];
	}
	
	public static float[] createFloatVector(int length) {
		return new float[length];
	}
	
	public static double[][] createDoubleMatrix(int rows, int columns) {
		return new double[rows][columns];
	}
	
	public static float[][] createFloatMatrix(int rows, int columns) {
		return new float[rows][columns];
	}
	
	// matrix properties -------------------------------------

	public static int getNumberOfRows(double[][] A) {
		return A.length;
	}
	
	public static int getNumberOfColumns(double[][] A) {
		return A[0].length;
	}
	
	public static int getNumberOfRows(float[][] A) {
		return A.length;
	}
	
	public static int getNumberOfColumns(float[][] A) {
		return A[0].length;
	}

	// matrix and vector duplication

	public static double[] duplicate(final double[] A) {
		return A.clone();
	}
	
	public static float[] duplicate(final float[] A) {
		return A.clone();
	}

	public static double[][] duplicate(final double[][] A) {
		final int m = A.length;
		double[][] B = new double[m][];
		for (int i = 0; i < m; i++) {
			B[i] = A[i].clone();
		}
		return B;
	}
	
	public static float[][] duplicate(final float[][] A) {
		final int m = A.length;
		float[][] B = new float[m][];
		for (int i = 0; i < m; i++) {
			B[i] = A[i].clone();
		}
		return B;
	}
	
	// element-wise arithmetic -------------------------------
	
	public static int[] add(int[] a, int[] b) {
		int[] c = a.clone();
		for (int i = 0; i < a.length; i++) {
			c[i] = c[i] + b[i];
		}
		return c;
	}
	
	public static double[] add(double[] A, double[] B) {
		double[] C = new double[A.length];
		for (int i = 0; i < A.length; i++) {
			C[i] = A[i] + B[i];
		}
		return C;
	}

	public static double[][] add(double[][] A, double[][] B) {
		final int m = A.length;
		final int n = A[0].length;
		double[][] C = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j] + B[i][j];
			}
		}
		return C;
	}
	
	public static double[] sub(double[] A, double[] B) {
		double[] c = A.clone();
		for (int i = 0; i < A.length; i++) {
			c[i] = c[i] - B[i];
		}
		return c;
	}
	
	public static double[] sub(double[] A, int[] B) {
		double[] c = A.clone();
		for (int i = 0; i < A.length; i++) {
			c[i] = c[i] - B[i];
		}
		return c;
	}
	
	public static int[] floor(double[] A) {
		int[] B = new int[A.length];
		for (int i = 0; i < A.length; i++) {
			B[i] = (int) Math.floor(A[i]);
		}
		return B;
	}

	// scalar multiplications -------------------------------

	public static double[] multiply(final double[] A, final double s) {
		double[] B = new double[A.length];
		for (int i = 0; i < A.length; i++) {
			B[i] = A[i] * s;
		}
		return B;
	}
	
	public static double[] multiply(final double s, final double[] A) {
		return multiply(A, s);
	}

	public static double[][] multiply(final double[][] A, final double s) {
		double[][] B = duplicate(A);
		for (int i = 0; i < B.length; i++) {
			for (int j = 0; j < B[i].length; j++) {
				B[i][j] = B[i][j] * s;
			}
		}
		return B;
	}
	
	public static double[][] multiply(final double s, final double[][] A) {
		return multiply(A, s);
	}
	
	public static float[] multiply(final float[] A, final float s) {
		float[] B = duplicate(A);
		for (int i = 0; i < B.length; i++) {
			B[i] = B[i] * s;
		}
		return B;
	}
	
	public static float[] multiply(final float s, final float[] A) {
		return multiply(A, s);
	}

	public static float[][] multiply(final float[][] A, final float s) {
		float[][] B = duplicate(A);
		for (int i = 0; i < B.length; i++) {
			for (int j = 0; j < B[i].length; j++) {
				B[i][j] = B[i][j] * s;
			}
		}
		return B;
	}
	
	public static float[][] multiply(final float s, final float[][] A) {
		return multiply(A, s);
	}
	
	// matrix-vector multiplications ----------------------------------------

	/*  
	 * Implements a right (post-) matrix-vector multiplication: Y <- X . A
	 * X is treated as a row vector of length m, matrix A is of size (m,n).
	 * Y (a row vector of length n) is modified.
	 */
	public static double[] multiply(final double[] X, final double[][] A, double[] Y) {
		final int m = getNumberOfRows(A);
		final int n = getNumberOfColumns(A);
		if (X.length != m || Y.length != n) 
			throw new IllegalArgumentException("incompatible vector-matrix dimensions");
		for (int i = 0; i < n; i++) {
			double s = 0;
			for (int j = 0; j < m; j++) {
				s = s + X[j] * A[j][i];
			}
			Y[i] = s;
		}
		return Y;
	}
	
	public static double[] multiply(final double[] X, final double[][] A) {
		double[] Y = new double[getNumberOfColumns(A)];
		return multiply(X, A, Y);
	}

	/*  
	 * implements a left (pre-) matrix-vector multiplication: Y <- A . X
	 * Matrix A is of size (m,n), column vector X is of length n.
	 * The result Y is a column vector of length m.
	 */
	
	public static double[] multiply(final double[][] A, final double[] X) {
		double[] Y = new double[A.length];
		return multiply(A, X, Y);
	}
	
	public static double[] multiply(final double[][] A, final double[] X, double[] Y) {
		final int m = A.length;
		final int n = A[0].length;
		if (X.length != n || Y.length != m) 
			throw new IllegalArgumentException("incompatible matrix-vector dimensions");
		for (int i = 0; i < m; i++) {
			double s = 0;
			for (int j = 0; j < n; j++) {
				s = s + A[i][j] * X[j];
			}
			Y[i] = s;
		}
		return Y;
	}
	
	/*
	 * Y <- A . X
	 */
	public static float[] multiply(final float[][] A, final float[] X) {
		float[] Y = new float[A.length];
		return multiply(A, X, Y);
	}
	
	public static float[] multiply(final float[][] A, final float[] X, float[] Y) {
		for (int i = 0; i < A.length; i++) {
			float s = 0;
			for (int j = 0; j < A[i].length; j++) {
				s = s + A[i][j] * X[j];
			}
			Y[i] = s;
		}
		return Y;
	}
	
	// matrix-matrix products ---------------------------------------
	
	// C <- A * B
	public static double[][] multiply(final double[][] A, final double[][] B) {
		int m = getNumberOfRows(A);
		int q = getNumberOfColumns(B);
		double[][] C = createDoubleMatrix(m, q);
		return multiply(A, B, C);
	}
	
	public static double[][] multiply(final double[][] A, final double[][] B, double[][] C) {
		int m = getNumberOfRows(A);
		int n = getNumberOfColumns(A);
		int q = getNumberOfColumns(B);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < q; j++) {
				double s = 0;
				for (int k = 0; k < n; k++) {
					s = s + A[i][k] * B[k][j];
				}
				C[i][j] = s;
			}
		}
		return C;
	}
	
	public static float[][] multiply(final float[][] A, final float[][] B) {
		int m = getNumberOfRows(A);
		int q = getNumberOfColumns(B);
		float[][] C = createFloatMatrix(m, q);
		return multiply(A, B, C);
	}

	public static float[][] multiply(final float[][] A, final float[][] B, float[][] C) {
		int m = getNumberOfRows(A);
		int n = getNumberOfColumns(A);
		int q = getNumberOfColumns(B);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < q; j++) {
				float s = 0;
				for (int k = 0; k < n; k++) {
					s = s + A[i][k] * B[k][j];
				}
				C[i][j] = s;
			}
		}
		return C;
	}
	
	// vector-vector products ---------------------------------------
	
	// A is considered a row vector, B is a column vector, both of length n.
	// returns a scalar vale.
	public static double dotProduct(final double[] A, final double[] B) {
		double sum = 0;
		for (int i = 0; i < A.length; i++) {
			sum = sum + A[i] * B[i];
		}
		return sum;
	}
	
	// A is considered a column vector, B is a row vector, of length m, n, resp.
	// returns a matrix M of size (m,n).
	public static double[][] outerProduct(final double[] A, final double[] B) {
		final int m = A.length;
		final int n = B.length;
		double[][] M = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				M[i][j] = A[i] * B[j];
			}
		}
		return M;
	}

	//  vector norms ---------------------------------------------------

	public static double normL1(final double[] A) {
		double sum = 0;
		for (double x : A) {
			sum = sum + Math.abs(x);
		}
		return sum;
	}

	public static double normL2(final double[] A) {
		return Math.sqrt(normL2squared(A));
	}

	public static double normL2squared(final double[] A) {
		double sum = 0;
		for (double x : A) {
			sum = sum + (x * x);
		}
		return sum;
	}
	
	public static float normL1(final float[] A) {
		float sum = 0;
		for (float x : A) {
			sum = sum + Math.abs(x);
		}
		return sum;
	}

	public static float normL2(final float[] A) {
		return (float) Math.sqrt(normL2squared(A));
	}

	public static float normL2squared(final float[] A) {
		float sum = 0;
		for (float x : A) {
			sum = sum + (x * x);
		}
		return sum;
	}

	// summation --------------------------------------------------

	public static double sum(final double[] A) {
		double sum = 0;
		for (int i = 0; i < A.length; i++) {
			sum = sum + A[i];
		}
		return sum;
	}
	
	public static float sum(final float[] A) {
		float sum = 0;
		for (int i = 0; i < A.length; i++) {
			sum = sum + A[i];
		}
		return sum;
	}
	
	// determinants --------------------------------------------
	
	public static double determinant2x2(final double[][] A) {
		return A[0][0] * A[1][1] - A[0][1] * A[1][0];
	}

	public static double determinant3x3(final double[][] A) {
		return A[0][0] * A[1][1] * A[2][2] + A[0][1] * A[1][2] * A[2][0]
				+ A[0][2] * A[1][0] * A[2][1] - A[2][0] * A[1][1] * A[0][2]
				- A[2][1] * A[1][2] * A[0][0] - A[2][2] * A[1][0] * A[0][1];
	}
	
	public static float determinant2x2(final float[][] A) {
		return A[0][0] * A[1][1] - A[0][1] * A[1][0];
	}

	public static float determinant3x3(final float[][] A) {
		return A[0][0] * A[1][1] * A[2][2] + A[0][1] * A[1][2] * A[2][0]
				+ A[0][2] * A[1][0] * A[2][1] - A[2][0] * A[1][1] * A[0][2]
				- A[2][1] * A[1][2] * A[0][0] - A[2][2] * A[1][0] * A[0][1];
	}
	
	// matrix inversion ---------------------------------------
	
	
	/**
	 * @param A a square matrix.
	 * @return the inverse of A or null if A is non-square or singular.
	 */
	public static double[][] inverse(final double[][] A) {
		RealMatrix M = MatrixUtils.createRealMatrix(A);
		if (!M.isSquare()) 
			return null;
		else {
			double[][] Ai = null;
			try {
				RealMatrix Mi = new LUDecomposition(M).getSolver().getInverse();
				Ai = Mi.getData();
			} catch (SingularMatrixException e) {}
			return Ai;
		}
	}

	public static double[][] inverse2x2(final double[][] A) {
		double[][] B = duplicate(A);
		final double det = determinant2x2(B);
		if (Math.abs(det) < Arithmetic.EPSILON_DOUBLE)
			return null;
		else {
			final double a00 = B[0][0];
			final double a01 = B[0][1];
			final double a10 = B[1][0];
			final double a11 = B[1][1];
			B[0][0] =  a11 / det;
			B[0][1] = -a01 / det;
			B[1][0] = -a10 / det;
			B[1][1] =  a00 / det;
			return B;
		}
	}

	public static double[][] inverse3x3(final double[][] A) {
		double[][] B = duplicate(A);
		final double det = determinant3x3(B);
		if (Math.abs(det) < Arithmetic.EPSILON_DOUBLE)
			return null;
		else {
			final double a00 = B[0][0];
			final double a01 = B[0][1];
			final double a02 = B[0][2];
			final double a10 = B[1][0];
			final double a11 = B[1][1];
			final double a12 = B[1][2];
			final double a20 = B[2][0];
			final double a21 = B[2][1];
			final double a22 = B[2][2];
			B[0][0] = (a11 * a22 - a12 * a21) / det;
			B[0][1] = (a02 * a21 - a01 * a22) / det;
			B[0][2] = (a01 * a12 - a02 * a11) / det;

			B[1][0] = (a12 * a20 - a10 * a22) / det;
			B[1][1] = (a00 * a22 - a02 * a20) / det;
			B[1][2] = (a02 * a10 - a00 * a12) / det;

			B[2][0] = (a10 * a21 - a11 * a20) / det;
			B[2][1] = (a01 * a20 - a00 * a21) / det;
			B[2][2] = (a00 * a11 - a01 * a10) / det;
			return B;
		}
	}

	public static float[][] inverse2x2(final float[][] A) {
		float[][] B = duplicate(A);
		final double det = determinant2x2(B);
		if (Math.abs(det) < Arithmetic.EPSILON_DOUBLE)
			return null;
		else {
			final double a00 = B[0][0];
			final double a01 = B[0][1];
			final double a10 = B[1][0];
			final double a11 = B[1][1];
			B[0][0] = (float) ( a11 / det);
			B[0][1] = (float) (-a01 / det);
			B[1][0] = (float) (-a10 / det);
			B[1][1] = (float) ( a00 / det);
			return B;
		}
	}

	// Note: this works by side-effect (destructively)!!
	public static float[][] inverse3x3(final float[][] A) {
		float[][] B = duplicate(A);
		final double det = determinant3x3(B);
		// IJ.log("   determinant = " + det);
		if (Math.abs(det) < Arithmetic.EPSILON_DOUBLE)
			return null;
		else {
			final double a00 = B[0][0];
			final double a01 = B[0][1];
			final double a02 = B[0][2];
			final double a10 = B[1][0];
			final double a11 = B[1][1];
			final double a12 = B[1][2];
			final double a20 = B[2][0];
			final double a21 = B[2][1];
			final double a22 = B[2][2];
			B[0][0] = (float) ((a11 * a22 - a12 * a21) / det);
			B[0][1] = (float) ((a02 * a21 - a01 * a22) / det);
			B[0][2] = (float) ((a01 * a12 - a02 * a11) / det);

			B[1][0] = (float) ((a12 * a20 - a10 * a22) / det);
			B[1][1] = (float) ((a00 * a22 - a02 * a20) / det);
			B[1][2] = (float) ((a02 * a10 - a00 * a12) / det);

			B[2][0] = (float) ((a10 * a21 - a11 * a20) / det);
			B[2][1] = (float) ((a01 * a20 - a00 * a21) / det);
			B[2][2] = (float) ((a00 * a11 - a01 * a10) / det);
			return B;
		}
	}
	
	// ------------------------------------------------------------------------
	
	// Finds a solution x for A.x = b
	public static double[] solve(final double[][] A, double[] b) {
		RealMatrix AA = MatrixUtils.createRealMatrix(A);
		RealVector bb = MatrixUtils.createRealVector(b);
		DecompositionSolver solver = new LUDecomposition(AA).getSolver();
		double[] x = null;
		try {
			x = solver.solve(bb).toArray();
		} catch (SingularMatrixException e) {}
		return x;
	}
	
	// Output to streams and strings ------------------------------------------
	
	static int defaultPrintPrecision = 3;
	static int printPrecision = defaultPrintPrecision;
	static String fStr;
	static {
		 resetPrintPrecision();
	}
	
	public static void resetPrintPrecision() {
		setPrintPrecision(defaultPrintPrecision);
	}
	
	public static void setPrintPrecision(int nDigits) {
		printPrecision = Math.max(nDigits, 0);
		if (nDigits > 0) {
			fStr = "%." + printPrecision + "f"; // e.g. "%.5f"
		}
		else {
			fStr = "%e";	// use scientific format - OK?
		}
	}
	
	public static int getPrintPrecision() {
		return printPrecision;
	}
	
	public static String toString(double[] A) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(A, strm);
		return bas.toString();
	}
	
	public static void printToStream(double[] A, PrintStream strm) {
		strm.format("{");
		for (int i = 0; i < A.length; i++) {
			if (i > 0)
				strm.format(", ");
			strm.format(fStr, A[i]);
		}
		strm.format("}");
		strm.flush();
	}
	
	public static String toString(double[][] A) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(A, strm);
		return bas.toString();
	}
	
	public static void printToStream(double[][] A, PrintStream strm) {
		strm.format("{");
		for (int i=0; i< A.length; i++) {
			if (i == 0)
				strm.format("{");
			else
				strm.format(", \n{");
			for (int j=0; j< A[i].length; j++) {
				if (j == 0) 
					strm.format(fStr, A[i][j]);
				else
					strm.format(", " + fStr, A[i][j]);
			}
			strm.format("}");
		}
		strm.format("}");
		strm.flush();
	}
	
	public static String toString(float[] A) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(A, strm);
		return bas.toString();
	}
	
	public static void printToStream(float[] A, PrintStream strm) {
		strm.format("{");
		for (int i = 0; i < A.length; i++) {
			if (i > 0)
				strm.format(", ");
			strm.format(fStr, A[i]);
		}
		strm.format("}");
		strm.flush();
	}

	public static String toString(float[][] A) {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream strm = new PrintStream(bas);
		printToStream(A, strm);
		return bas.toString();
	}
	
	public static void printToStream(float[][] A, PrintStream strm) {
		strm.format("{");
		for (int i=0; i< A.length; i++) {
			if (i == 0)
				strm.format("{");
			else
				strm.format(", \n{");
			for (int j=0; j< A[i].length; j++) {
				if (j == 0) 
					strm.format(fStr, A[i][j]);
				else
					strm.format(", " + fStr, A[i][j]);
			}
			strm.format("}");
		}
		strm.format("}");
		strm.flush();
	}

	//--------------------------------------------------------------------------

//	public static void main(String[] args) {
//		float[][] A = {{ -1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }};
//
//		toString(A);
//		int row = 1;
//		int column = 2;
//		System.out.println("A[1][2] = " + A[row][column]);
//
//		System.out.println("det=" + determinant3x3(A));
//		float[][] Ai = inverse3x3(A);
//		toString(Ai);
//
//		double[][] B = {{ -1, 2, 3 }, { 4, 5, 6 }};
//		System.out.println("B rows = " + B.length);
//		System.out.println("B columns = " + B[0].length);
//		
//		Matrix.setPrintPrecision(5);
//
//		double[][] C = new double[2][3];
//		System.out.println("C rows = " + C.length);
//		System.out.println("C columns = " + C[0].length);
//
//
//		RealMatrix Ba = new Array2DRowRealMatrix(B);
//		System.out.println("Ba = " + Ba.toString());
//	}
}
