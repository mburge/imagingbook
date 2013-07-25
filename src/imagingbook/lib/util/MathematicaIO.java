/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.util;

import java.util.Formatter;
import java.util.Locale;


public class MathematicaIO {

	public static String listArray(double[] A, String name) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format(name + " = {");
		for (int i = 0; i < A.length; i++) {
			if (i > 0)
				formatter.format(", ");
			formatter.format("%.5f", A[i]);
		}
		formatter.format("};\n");
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	// CHECK i/j indices!!!
	
	public static String listArray(double[][] A, String name) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format(name + " = {");
		for (int i=0; i< A.length; i++) {
			if (i == 0)
				formatter.format("{");
			else
				formatter.format(", \n{");
			for (int j=0; j< A[i].length; j++) {
				if (j == 0) 
					formatter.format("%.3f", A[i][j]);
				else
					formatter.format(", %.3f", A[i][j]);
			}
			formatter.format("}");
		}
		formatter.format("};\n");
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	public static String listArray(float[] A) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format("{");
		for (int i = 0; i < A.length; i++) {
			if (i > 0)
				formatter.format(", ");
			formatter.format("%.5f", A[i]);
		}
		formatter.format("};\n");
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	public static String listArray(float[][] A, String name) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format(name + " = {");
		for (int i=0; i< A.length; i++) {
			if (i == 0)
				formatter.format("{");
			else
				formatter.format(", \n{");
			for (int j=0; j< A[i].length; j++) {
				if (j == 0) 
					formatter.format("%.3f", A[j][i]);
				else
					formatter.format(", %.3f", A[j][i]);
			}
			formatter.format("}");
		}
		formatter.format("};\n");
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	public static String listArray(String name, float[] A) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format(name + " = {");
		for (int i = 0; i < A.length; i++) {
			if (i > 0)
				formatter.format(", ");
			formatter.format("%.5f", A[i]);
		}
		formatter.format("};\n");
		String result = formatter.toString();
		formatter.close();
		return result;
	}
    
    // CHECK i/j indices!!!


}
