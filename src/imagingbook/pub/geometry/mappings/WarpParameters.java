/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.geometry.mappings;


/**
 * This interfaces prescribes methods for the parameterization of mappings
 * with 1D double vectors (used inLucas-Kanade matcher etc.).
 * Currently only used with LinearMapping.
 *  @author WB
 *
 */
public interface WarpParameters {
	
	public int getParameterCount();	
	public double[] getParameters();
	public void setParameters(double[] p);
	public double[][] getJacobian(double[] x);
	
	
}
