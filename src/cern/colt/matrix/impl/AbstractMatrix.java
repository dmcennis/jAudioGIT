/*
Copyright © 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package cern.colt.matrix.impl;

/**
Abstract base class for arbitrary-dimensional matrices holding objects or primitive data types such as <code>int</code>, <code>float</code>, etc.
First see the <a href="package-summary.html">package summary</a> and javadoc <a href="package-tree.html">tree view</a> to get the broad picture.
<p>
<b>Note that this implementation is not synchronized.</b>

@author wolfgang.hoschek@cern.ch
@version 1.0, 09/24/99
*/
public abstract class AbstractMatrix extends cern.colt.PersistentObject {
	protected boolean isNoView = true;
	//public static boolean debug = true;
/**
 * Makes this class non instantiable, but still let's others inherit from it.
 */
protected AbstractMatrix() {}
/**
 * Ensures that the receiver can hold at least the specified number of non-zero (non-null) cells without needing to allocate new internal memory.
 * If necessary, allocates new internal memory and increases the capacity of the receiver.
 * <p>
 * This default implementation does nothing. Override this method if necessary.
 *
 * @param   minNonZeros   the desired minimum number of non-zero (non-null) cells.
 */
public void ensureCapacity(int minNonZeros) {}
/**
 * Returns whether the receiver is a view or not.
 */
protected boolean isView() {
	return ! this.isNoView;
}
/**
 * Returns the number of cells.
 */
public abstract int size();
/**
 * Releases any superfluous internal memory. An application can use this operation to minimize the 
 * storage of the receiver.
 * <p>
 * This default implementation does nothing. Override this method if necessary.
 */
public void trimToSize() {}
}
