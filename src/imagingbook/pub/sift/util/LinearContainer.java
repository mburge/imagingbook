/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.pub.sift.util;

import imagingbook.lib.util.ArrayIterator;

import java.util.Iterator;

//import java.util.Iterator;

/*
 * This class implements an array with flexible bottom and top index,
 * similar to an array in Pascal.
 */
public class LinearContainer<T> implements Iterable<T> {
	
	private int botIndex, topIndex;
	private T[] data;
	
	@SuppressWarnings("unchecked")
	public LinearContainer(int n) {
		this.botIndex = 0;
		topIndex = n-1;
		data = (T[]) new Object[n];
	}
	
	@SuppressWarnings("unchecked")
	public LinearContainer(int botIndex, int topIndex) {
		this.botIndex = botIndex;
		this.topIndex = topIndex;
		data = (T[]) new Object[topIndex - botIndex + 1];
	}
	
	public T getElement(int k) {
		return data[k-getBotIndex()];
	}
	
	public void setElement(int k, T elem) {
		data[k-getBotIndex()] = elem;
	}

	public int getBotIndex() {
		return botIndex;
	}
	
	public int getTopIndex() {
		return topIndex;
	}

	public Iterator<T> iterator() {
		return new ArrayIterator<T>(data);
	}

}
