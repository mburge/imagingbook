/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.util;


import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This utility class implements a basic iterator for arbitrary arrays.
 */
public class ArrayIterator<T> implements Iterator<T> {

	private int next;
	private T[] data;

	public ArrayIterator(T[] data) {
		this.data = data;
		next = 0;
	}

	public boolean hasNext() {
		return next < data.length;
	}

	public T next() {
		if (hasNext())
			return data[next++];
		else
			throw new NoSuchElementException();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
