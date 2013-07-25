package xB_Anhang;

import java.lang.reflect.Array;

public class Util {

	/**
	 * The result of the "modulus" operator (a mod b) is defined as the
	 * remainder of the integer division (a / b). If BOTH a, b are positive,
	 * then Java's % (remainder) operator gives the correct results. If at least
	 * one of the operands could be negative, the following method can be used
	 * to implement the mathematical definition of the mod operator:
	 * Examples:
	 *     13 mod  4 =  1
	 *    -13 mod  4 =  3
	 *     13 mod -4 = -3
	 *    -13 mod -4 = -1
	 *      0 mod  4 =  0
	 *      4 mod  0 =  4
	 */
	public static int Mod(int a, int b) {
		if (b == 0)
			return a;
		if (a * b >= 0)
			return a - b * (a / b);	
		else
			return a - b * (a / b - 1);
	}

	/**
	 * Utility method for recursive cloning of arrays of any element type 
	 * and dimensionality. Objects inside the array are not duplicated.
	 */
	public static Object duplicateArray(Object orig) {
		Class<?> origClass = orig.getClass();
		if (!origClass.isArray())
			return null; // no array to duplicate
		Class<?> compType = origClass.getComponentType();
		int n = Array.getLength(orig);
		Object dup = Array.newInstance(compType, n);
		if (compType.isArray()) // array elements are arrays again:
			for (int i = 0; i < n; i++)
				Array.set(dup, i, duplicateArray(Array.get(orig, i)));
		else // array elements are objects or primitives:
			System.arraycopy(orig, 0, dup, 0, n);
		return dup;
	}

}
