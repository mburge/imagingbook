/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.dct;
import ij.process.*;

public class Dct2d {
	int width;
	int height;
	float[] Data;	//original image data
	float[] Power;
	float PowerMax;
	boolean swapQu = true;
	int scaleValue = 255;	
	boolean forward = true;
	
	public Dct2d(FloatProcessor ip){
		width = ip.getWidth();
		height = ip.getHeight();
		Data = (float[]) ip.getPixels();
		doDct2d();
		makePowerSpectrum();
	}
	
	public Dct2d(FloatProcessor ip, boolean center){
		this(ip);
		swapQu = center;
	}
	
	//------------------------------------------------
	
	public void setForward(){
		forward = true;
	}
	
	public void setInverse(){
		forward = false;
	}
	
	public float[] getReal(){
		return Data;
	}
	
	public float[] getPower(){
		return Power;
	}
	
	//------------------------------------------------
	
	public void doDct2d () {
		// in-place 2D Dct
		// do the rows:
		double[] row = new double[width];
		Dct1d dftR = new Dct1d(width);
		for (int v=0; v<height; v++){
			getRow(v,row);
			double[] rowDct;
			if (forward)
				rowDct = dftR.DCT(row);
			else
				rowDct = dftR.iDCT(row);
			putRow(v,rowDct);
		}
		
		// do the columns:
		double[] col = new double[height];
		Dct1d dftC = new Dct1d(height);
		for (int u=0; u<width; u++){
			getCol(u,col);
			double[] colDft;
			if (forward)
				colDft= dftC.DCT(col);
			else
				colDft= dftC.iDCT(row);
			putCol(u,colDft);
		}
	}
	
	void getRow(int v, double[] rowC){
		int i = v*width; //start index of row v
		for (int u=0; u<width; u++){
			rowC[u] = Data[i+u];
		}
	}
	
	void putRow(int v, double[] rowC){
		int i = v*width; //start index of row v
		for (int u=0; u<width; u++){
			Data[i+u] = (float) rowC[u];
		}
	}
	
	void getCol(int u, double[] rowC){
		for (int v=0; v<height; v++){
			rowC[v] = Data[v*width+u];
		}
	}
	
	void putCol(int u, double[] rowC){
		for (int v=0; v<height; v++){
			Data[v*width+u] = (float) rowC[v];
		}
	}
	
	//----------------------------------------------------
	
	void makePowerSpectrum(){
		//computes the power spectrum 
		Power = new float[Data.length];
		PowerMax = 0.0f;
		for (int i=0; i<Data.length; i++){
			float p = Data[i];
			if (p < 0) p = -p;
			Power[i] = p;
			if (p > PowerMax) PowerMax = p;
		}
	}
	
	public ByteProcessor makePowerImage(){
		ByteProcessor ip = new ByteProcessor(width,height);
		byte[] pixels = (byte[]) ip.getPixels();
		//PowerMax must be set
		double max = Math.log(PowerMax+1.0);
		double scale = 1.0;
		if (scaleValue > 0)
			scale = scaleValue/max;
		for (int i=0; i<pixels.length; i++){
			double p = Power[i];
			//if (p<0) p = -p;
			double plog = Math.log(p+1.0);
			int pint = (int)(plog * scale);
			pixels[i] = (byte) (0xFF & pint);
		}
		if (swapQu) swapQuadrants(ip);
		return ip;
	}

	//	----------------------------------------------------

	public void swapQuadrants (ImageProcessor ip) {
		//swap quadrants Q1 <-> Q3, Q2 <-> Q4
		// Q2 Q1
		// Q3 Q4
		ImageProcessor t1, t2;
		int w = ip.getWidth();
		int h = ip.getHeight();
		int w2 = w/2;
		int h2 = h/2;
		
		ip.setRoi(w2,0,w-w2,h2); // Q1
		t1 = ip.crop();
		
		ip.setRoi(0,h2,w2,h-h2); //Q3
		t2 = ip.crop();
		
		ip.insert(t1,0,h2); //swap Q1 <-> Q3
		ip.insert(t2,w2,0);
		
		ip.setRoi(0,0,w2,h2); //Q2
		t1 = ip.crop();
		
		ip.setRoi(w2,h2,w-w2,h-h2); //Q4
		t2 = ip.crop();
		
		ip.insert(t1,w2,h2);
		ip.insert(t2,0,0);
	}


}


