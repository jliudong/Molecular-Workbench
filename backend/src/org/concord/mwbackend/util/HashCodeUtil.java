package org.concord.mwbackend.util;

import java.lang.reflect.Array;

/**
 * The following utility class allows simple construction of an effective hashCode method. It is based on the
 * recommendations of Effective Java, by Joshua Bloch. Collected methods which allow easy implementation of
 * <code>hashCode</code>. Example use case:
 * 
 * <pre>
 * public int hashCode() {
 * 	int result = HashCodeUtil.SEED;
 * 	//collect the contributions of various fields
 * 	result = HashCodeUtil.hash(result, fPrimitive);
 * 	result = HashCodeUtil.hash(result, fObject);
 * 	result = HashCodeUtil.hash(result, fArray);
 * 	return result;
 * }
 * </pre>
 */

public final class HashCodeUtil {

	/**
	 * An initial value for a <code>hashCode</code>, to which is added contributions from fields. Using a
	 * non-zero value decreases collisons of <code>hashCode</code> values.
	 */
	public static final int SEED = 23;

	private static final int fODD_PRIME_NUMBER = 37;

	public static int hash(int aSeed, boolean aBoolean) {
		return firstTerm(aSeed) + (aBoolean ? 1 : 0);
	}

	public static int hash(int aSeed, char aChar) {
		return firstTerm(aSeed) + (int) aChar;
	}

	// Note that byte and short are handled by this method, through implicit conversion.
	public static int hash(int aSeed, int aInt) {
		return firstTerm(aSeed) + aInt;
	}

	public static int hash(int aSeed, long aLong) {
		return firstTerm(aSeed) + (int) (aLong ^ (aLong >>> 32));
	}

	public static int hash(int aSeed, float aFloat) {
		return hash(aSeed, Float.floatToIntBits(aFloat));
	}

	public static int hash(int aSeed, double aDouble) {
		return hash(aSeed, Double.doubleToLongBits(aDouble));
	}

	/**
	 * <code>aObject</code> is a possibly-null object field, and possibly an array. If <code>aObject</code> is
	 * an array, then each element may be a primitive or a possibly-null object.
	 */
	public static int hash(int aSeed, Object aObject) {
		int result = aSeed;
		if (aObject == null) {
			result = hash(result, 0);
		} else if (!isArray(aObject)) {
			result = hash(result, aObject.hashCode());
		} else {
			int length = Array.getLength(aObject);
			for (int idx = 0; idx < length; ++idx) {
				Object item = Array.get(aObject, idx);
				// recursive call!
				result = hash(result, item);
			}
		}
		return result;
	}

	private static int firstTerm(int aSeed) {
		return fODD_PRIME_NUMBER * aSeed;
	}

	private static boolean isArray(Object aObject) {
		return aObject.getClass().isArray();
	}

}