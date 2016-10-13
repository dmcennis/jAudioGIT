/*
Copyright ï¿½ 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package cern.colt.matrix.impl;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
/**
Selection view on dense 1-d matrices holding <tt>double</tt> elements.
First see the <a href="package-summary.html">package summary</a> and javadoc <a href="package-tree.html">tree view</a> to get the broad picture.
<p>
<b>Implementation:</b>
<p>
Objects of this class are typically constructed via <tt>viewIndexes</tt> methods on some source matrix.
The interface introduced in abstract super classes defines everything a user can do.
From a user point of view there is nothing special about this class; it presents the same functionality with the same signatures and semantics as its abstract superclass(es) while introducing no additional functionality.
Thus, this class need not be visible to users.
By the way, the same principle applies to concrete DenseXXX, SparseXXX classes: they presents the same functionality with the same signatures and semantics as abstract superclass(es) while introducing no additional functionality.
Thus, they need not be visible to users, either. 
Factory methods could hide all these concrete types.
<p>
This class uses no delegation. 
Its instances point directly to the data. 
Cell addressing overhead is 1 additional array index access per get/set.
<p>
Note that this implementation is not synchronized.
<p>
<b>Memory requirements:</b>
<p>
<tt>memory [bytes] = 4*indexes.length</tt>.
Thus, an index view with 1000 indexes additionally uses 4 KB.
<p>
<b>Time complexity:</b>
<p>
Depends on the parent view holding cells.
<p>
@author wolfgang.hoschek@cern.ch
@version 1.0, 09/24/99
*/
class SelectedDenseDoubleMatrix1D extends DoubleMatrix1D {
	/**
	  * The elements of this matrix.
	  */
	protected double[] elements;
	
	/**
	  * The offsets of visible indexes of this matrix.
	  */
	protected int[] offsets;

	/**
	  * The offset.
	  */
	protected int offset;	
/**
 * Constructs a matrix view with the given parameters.
 * @param elements the cells.
 * @param  indexes   The indexes of the cells that shall be visible.
 */
protected SelectedDenseDoubleMatrix1D(double[] elements, int[] offsets) {
	this(offsets.length,elements,0,1,offsets,0);
}
/**
 * Constructs a matrix view with the given parameters.
 * @param size the number of cells the matrix shall have.
 * @param elements the cells.
 * @param zero the index of the first element.
 * @param stride the number of indexes between any two elements, i.e. <tt>index(i+1)-index(i)</tt>.
 * @param offsets   the offsets of the cells that shall be visible.
 * @param offset   
 */
protected SelectedDenseDoubleMatrix1D(int size, double[] elements, int zero, int stride, int[] offsets, int offset) {
	setUp(size,zero,stride);
	
	this.elements = elements;
	this.offsets = offsets;
	this.offset = offset;
	this.isNoView = false;
}
/**
 * Returns the position of the given absolute rank within the (virtual or non-virtual) internal 1-dimensional array. 
 * Default implementation. Override, if necessary.
 *
 * @param  rank   the absolute rank of the element.
 * @return the position.
 */
protected int _offset(int absRank) {
	return offsets[absRank];
}
/**
 * Returns the matrix cell value at coordinate <tt>index</tt>.
 *
 * <p>Provided with invalid parameters this method may return invalid objects without throwing any exception.
 * <b>You should only use this method when you are absolutely sure that the coordinate is within bounds.</b>
 * Precondition (unchecked): <tt>index&lt;0 || index&gt;=size()</tt>.
 *
 * @param     index   the index of the cell.
 * @return    the value of the specified cell.
 */
public double getQuick(int index) {
	//if (debug) if (index<0 || index>=size) checkIndex(index);
	//return elements[index(index)];
	//manually inlined:
	return elements[offset + offsets[zero + index*stride]];
}
/**
 * Returns <tt>true</tt> if both matrices share at least one identical cell.
 */
protected boolean haveSharedCellsRaw(DoubleMatrix1D other) {
	if (other instanceof SelectedDenseDoubleMatrix1D) {
		SelectedDenseDoubleMatrix1D otherMatrix = (SelectedDenseDoubleMatrix1D) other;
		return this.elements==otherMatrix.elements;
	}
	else if (other instanceof DenseDoubleMatrix1D) {
		DenseDoubleMatrix1D otherMatrix = (DenseDoubleMatrix1D) other;
		return this.elements==otherMatrix.elements;
	}
	return false;
}
/**
 * Returns the position of the element with the given relative rank within the (virtual or non-virtual) internal 1-dimensional array.
 * You may want to override this method for performance.
 *
 * @param     rank   the rank of the element.
 */
protected int index(int rank) {
	//return this.offset + super.index(rank);
	// manually inlined:
	return offset + offsets[zero + rank*stride];
}
/**
 * Construct and returns a new empty matrix <i>of the same dynamic type</i> as the receiver, having the specified size.
 * For example, if the receiver is an instance of type <tt>DenseDoubleMatrix1D</tt> the new matrix must also be of type <tt>DenseDoubleMatrix1D</tt>,
 * if the receiver is an instance of type <tt>SparseDoubleMatrix1D</tt> the new matrix must also be of type <tt>SparseDoubleMatrix1D</tt>, etc.
 * In general, the new matrix should have internal parametrization as similar as possible.
 *
 * @param size the number of cell the matrix shall have.
 * @return  a new empty matrix of the same dynamic type.
 */
public DoubleMatrix1D like(int size) {
	return new DenseDoubleMatrix1D(size);
}
/**
 * Construct and returns a new 2-d matrix <i>of the corresponding dynamic type</i>, entirelly independent of the receiver.
 * For example, if the receiver is an instance of type <tt>DenseDoubleMatrix1D</tt> the new matrix must be of type <tt>DenseDoubleMatrix2D</tt>,
 * if the receiver is an instance of type <tt>SparseDoubleMatrix1D</tt> the new matrix must be of type <tt>SparseDoubleMatrix2D</tt>, etc.
 *
 * @param rows the number of rows the matrix shall have.
 * @param columns the number of columns the matrix shall have.
 * @return  a new matrix of the corresponding dynamic type.
 */
public DoubleMatrix2D like2D(int rows, int columns) {
	return new DenseDoubleMatrix2D(rows,columns);
}
/**
 * Sets the matrix cell at coordinate <tt>index</tt> to the specified value.
 *
 * <p>Provided with invalid parameters this method may access illegal indexes without throwing any exception.
 * <b>You should only use this method when you are absolutely sure that the coordinate is within bounds.</b>
 * Precondition (unchecked): <tt>index&lt;0 || index&gt;=size()</tt>.
 *
 * @param     index   the index of the cell.
 * @param    value the value to be filled into the specified cell.
 */
public void setQuick(int index, double value) {
	//if (debug) if (index<0 || index>=size) checkIndex(index);
	//elements[index(index)] = value;
	//manually inlined:
	elements[offset + offsets[zero + index*stride]] = value;
}
/**
 * Sets up a matrix with a given number of cells.
 * @param size the number of cells the matrix shall have.
 */
protected void setUp(int size) {
	super.setUp(size);
	this.stride = 1;
	this.offset = 0;
}
/**
 * Construct and returns a new selection view.
 *
 * @param offsets the offsets of the visible elements.
 * @return  a new view.
 */
protected DoubleMatrix1D viewSelectionLike(int[] offsets) {
	return new SelectedDenseDoubleMatrix1D(this.elements,offsets);
}
}
