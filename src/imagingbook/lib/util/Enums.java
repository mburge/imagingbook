/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.util;

public class Enums {

	/**
	 * This static method returns an array of all constant names (strings) 
	 * for a given enumeration class.
	 * Assume the enum definition: enum MyEnum {A, B, C};
	 * Usage: String[] names = getEnumNames(MyEnum.class);
	 */
	public static String[] getEnumNames(Class<? extends Enum<?>> enumclass) {
		Enum<?>[] eConstants = (Enum<?>[]) enumclass.getEnumConstants();
		if (eConstants == null) {
			return new String[0];
		}
		else {
			int n = eConstants.length;
			String[] eNames = new String[n];
			for (int i = 0; i < n; i++) {
				eNames[i] = eConstants[i].name();
			}
			return eNames;
		}
	}

}
