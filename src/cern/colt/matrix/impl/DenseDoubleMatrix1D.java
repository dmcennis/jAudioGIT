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
Dense 1-d matrix (aka <i>vector</i>) holding <tt>double</tt> elements.
First see the <a href="package-summary.html">package summary</a> and javadoc <a href="package-tree.html">tree view</a> to get the broad picture.
<p>
<b>Implementation:</b>
<p>
Internally holds one single contigous one-dimensional array. 
Note that this implementation is not synchronized.
<p>
<b>Memory requirements:</b>
<p>
<tt>memory [bytes] = 8*size()</tt>.
Thus, a 1000000 matrix uses 8 MB.
<p>
<b>Time complexity:</b>
<p>
<tt>O(1)</tt> (i.e. constant time) for the basic operations
<tt>get</tt>, <tt>getQuick</tt>, <tt>set</tt>, <tt>setQuick</tt> and <tt>size</tt>,
<p>
@author wolfgang.hoschek@cern.ch
@version 1.0, 09/24/99
*/
public class DenseDoubleMatrix1D extends DoubleMatrix1D {
	/**
	  * The elements of this matrix.
	  */
	protected double[] elements;
/**
 * Constructs a matrix with a copy of the given values.
 * The values are copied. So subsequent changes in <tt>values</tt> are not reflected in the matrix, and vice-versa.
 *
 * @param values The values to be filled into the new matrix.
 */
public DenseDoubleMatrix1D(double[] values) {
	this(values.length);
	assign(values);
}
/**
 * Constructs a matrix with a given number of cells.
 * All entries are initially <tt>0</tt>.
 * @param size the number of cells the matrix shall have.
 * @throws IllegalArgumentException if <tt>size<0</tt>.
 */
public DenseDoubleMatrix1D(int size) {
	setUp(size);
	this.elements = new double[size];
}
/**
 * Constructs a matrix view with the given parameters.
 * @param size the number of cells the matrix shall have.
 * @param elements the cells.
 * @param zero the index of the first element.
 * @param stride the number of indexes between any two elements, i.e. <tt>index(i+1)-index(i)</tt>.
 * @throws IllegalArgumentException if <tt>size<0</tt>.
 */
protected DenseDoubleMatrix1D(int size, double[] elements, int zero, int stride) {
	setUp(size,zero,stride);
	this.elements = elements;
	this.isNoView = false;
}
/**
 * Sets all cells to the state specified by <tt>values</tt>.
 * <tt>values</tt> is required to have the same number of cells as the receiver.
 * <p>
 * The values are copied. So subsequent changes in <tt>values</tt> are not reflected in the matrix, and vice-versa.
 *
 * @param    values the values to be filled into the cells.
 * @return <tt>this</tt> (for convenience only).
 * @throws IllegalArgumentException if <tt>values.length != size()</tt>.
 */
public DoubleMatrix1D assign(double[] values) {
	if (isNoView) {
		if (values.length != size) throw new IllegalArgumentException("Must have same number of cells: length="+values.length+"size()="+size());
		System.arraycopy(values, 0, this.elements, 0, values.length);
	}
	else {
		super.assign(values);
	}
	return this;
}
/**
 * Sets all cells to the state specified by <tt>value</tt>.
 * @param    value the value to be filled into the cells.
 * @return <tt>this</tt> (for convenience only).
 */
public DoubleMatrix1D assign(double value) {
	int index = index(0);
	int s = this.stride;
	double[] elems = this.elements;
	for (int i=size; --i >= 0; ) {
		elems[index] = value;
		index += s;
	}
	return this;
}
/**
Assigns the result of a function to each cell; <tt>x[i] = function(x[i])</tt>.
(Iterates downwards from <tt>[size()-1]</tt> to <tt>[0]</tt>).
<p>
<b>Example:</b>
<pre>
// change each cell to its sine
matrix =   0.5      1.5      2.5       3.5 
matrix.assign(cern.jet.math.Functions.sin);
-->
matrix ==  0.479426 0.997495 0.598472 -0.350783
</pre>
For further examples, see the <a href="package-summary.html#FunctionObjects">package doc</a>.

@param function a function object taking as argument the current cell's value.
@return <tt>this</tt> (for convenience only).
@see cern.jet.math.Functions
*/
public DoubleMatrix1D assign(cern.colt.function.DoubleFunction function) {
	int s=stride;
	int i=index(0);
	double[] elems = this.elements;
	if (elems==null) throw new InternalError();

	// specialization for speed
	if (function instanceof cern.jet.math.Mult) { // x[i] = mult*x[i]
		double multiplicator = ((cern.jet.math.Mult)function).multiplicator;
		if (multiplicator==1) return this;
		for (int k=size; --k >= 0; ) {
			elems[i] *= multiplicator;
			i += s;
		}
	}
	else { // the general case x[i] = f(x[i])
		for (int k=size; --k >= 0; ) {
			elems[i] = function.apply(elems[i]);
			i += s;
		}
	}
	return this;
}
/**
 * Replaces all cell values of the receiver with the values of another matrix.
 * Both matrices must have the same size.
 * If both matrices share the same cells (as is the case if they are views derived from the same matrix) and intersect in an ambiguous way, then replaces <i>as if</i> using an intermediate auxiliary deep copy of <tt>other</tt>.
 *
 * @param     source   the source matrix to copy from (may be identical to the receiver).
 * @return <tt>this</tt> (for convenience only).
 * @throws	IllegalArgumentException if <tt>size() != other.size()</tt>.
 */
public DoubleMatrix1D assign(DoubleMatrix1D source) {
	// overriden for performance only
	if (! (source instanceof DenseDoubleMatrix1D)) {
		return super.assign(source);
	}
	DenseDoubleMatrix1D other = (DenseDoubleMatrix1D) source;
	if (other==this) return this;
	checkSize(other);
	if (isNoView && other.isNoView) { // quickest
		System.arraycopy(other.elements, 0, this.elements, 0, this.elements.length);
		return this;
	}
	if (haveSharedCells(other)) {
		DoubleMatrix1D c = other.copy();
		if (! (c instanceof DenseDoubleMatrix1D)) { // should not happen
			return super.assign(source);
		}
		other = (DenseDoubleMatrix1D) c;
	}

	final double[] elems = this.elements;
	final double[] otherElems = other.elements;
	if (elements==null || otherElems==null) throw new InternalError();
	int s = this.stride;
	int ys = other.stride;

	int index = index(0);
	int otherIndex = other.index(0);
	for (int k=size; --k >= 0; ) {
		elems[index] = otherElems[otherIndex];
		index += s;
		otherIndex += ys;
	}
	return this;
}
/**
Assigns the result of a function to each cell; <tt>x[i] = function(x[i],y[i])</tt>.
(Iterates downwards from <tt>[size()-1]</tt> to <tt>[0]</tt>).
<p>
<b>Example:</b>
<pre>
// assign x[i] = x[i]<sup>y[i]</sup>
m1 = 0 1 2 3;
m2 = 0 2 4 6;
m1.assign(m2, cern.jet.math.Functions.pow);
-->
m1 == 1 1 16 729

// for non-standard functions there is no shortcut: 
m1.assign(m2,
&nbsp;&nbsp;&nbsp;new DoubleDoubleFunction() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;public double apply(double x, double y) { return Math.pow(x,y); }
&nbsp;&nbsp;&nbsp;}
);
</pre>
For further examples, see the <a href="package-summary.html#FunctionObjects">package doc</a>.

@param y the secondary matrix to operate on.
@param function a function object taking as first argument the current cell's value of <tt>this</tt>,
and as second argument the current cell's value of <tt>y</tt>,
@return <tt>this</tt> (for convenience only).
@throws	IllegalArgumentException if <tt>size() != y.size()</tt>.
@see cern.jet.math.Functions
*/
public DoubleMatrix1D assign(DoubleMatrix1D y, cern.colt.function.DoubleDoubleFunction function) {
	// overriden for performance only
	if (! (y instanceof DenseDoubleMatrix1D)) {
		return super.assign(y,function);
	}
	DenseDoubleMatrix1D other = (DenseDoubleMatrix1D) y;
	checkSize(y);
	final double[] elems = this.elements;
	final double[] otherElems = other.elements;
	if (elems==null || otherElems==null) throw new InternalError();
	int s = this.stride;
	int ys = other.stride;

	int index = index(0);
	int otherIndex = other.index(0);

	// specialized for speed
	if (function==cern.jet.math.Functions.mult) {  // x[i] = x[i] * y[i]
		for (int k=size; --k >= 0; ) {
			elems[index] *= otherElems[otherIndex];
			index += s;
			otherIndex += ys;
		}
	}
	else if (function==cern.jet.math.Functions.div) { // x[i] = x[i] / y[i]
		for (int k=size; --k >= 0; ) {
			elems[index] /= otherElems[otherIndex];
			index += s;
			otherIndex += ys;
		}
	}
	else if (function instanceof cern.jet.math.PlusMult) {
		double multiplicator = ((cern.jet.math.PlusMult) function).multiplicator;
		if (multiplicator == 0) { // x[i] = x[i] + 0*y[i]
			return this;
		}
		else if (multiplicator == 1) { // x[i] = x[i] + y[i]
			for (int k=size; --k >= 0; ) {
				elems[index] += otherElems[otherIndex];
				index += s;
				otherIndex += ys;
			}
		}
		else if (multiplicator == -1) { // x[i] = x[i] - y[i]
			for (int k=size; --k >= 0; ) {
				elems[index] -= otherElems[otherIndex];
				index += s;
				otherIndex += ys;
			}
		}
		else { // the general case x[i] = x[i] + mult*y[i]		
			for (int k=size; --k >= 0; ) {
				elems[index] += multiplicator*otherElems[otherIndex];
				index += s;
				otherIndex += ys;
			}
		}
	}
	else { // the general case x[i] = f(x[i],y[i])		
		for (int k=size; --k >= 0; ) {
			elems[index] = function.apply(elems[index], otherElems[otherIndex]);
			index += s;
			otherIndex += ys;
		}
	}
	return this;
}
/**
 * Returns the number of cells having non-zero values, but at most maxCardinality; ignores tolerance.
 */
protected int cardinality(int maxCardinality) {
	int cardinality = 0;
	int index = index(0);
	int s = this.stride;
	double[] elems = this.elements;
	int i=size; 
	while (--i >= 0 && cardinality < maxCardinality) {
		if (elems[index] != 0) cardinality++;
		index += s;
	}
	return cardinality;
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
	// manually inlined:
	return elements[zero + index*stride];
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
	// overriden for manual inlining only
	//return _offset(_rank(rank));
	return zero + rank*stride;
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
	// manually inlined:
	elements[zero + index*stride] = value;
}
/**
Swaps each element <tt>this[i]</tt> with <tt>other[i]</tt>.
@throws IllegalArgumentException if <tt>size() != other.size()</tt>.
*/
public void swap(DoubleMatrix1D other) {
	// overriden for performance only
	if (! (other instanceof DenseDoubleMatrix1D)) {
		super.swap(other);
	}
	DenseDoubleMatrix1D y = (DenseDoubleMatrix1D) other;
	if (y==this) return;
	checkSize(y);
	
	final double[] elems = this.elements;
	final double[] otherElems = y.elements;
	if (elements==null || otherElems==null) throw new InternalError();
	int s = this.stride;
	int ys = y.stride;

	int index = index(0);
	int otherIndex = y.index(0);
	for (int k=size; --k >= 0; ) {
		double tmp = elems[index];
		elems[index] = otherElems[otherIndex];
		otherElems[otherIndex] = tmp;
		index += s;
		otherIndex += ys;
	}
	return;
}
/**
Fills the cell values into the specified 1-dimensional array.
The values are copied. So subsequent changes in <tt>values</tt> are not reflected in the matrix, and vice-versa.
After this call returns the array <tt>values</tt> has the form 
<br>
<tt>for (int i=0; i < size(); i++) values[i] = get(i);</tt>

@throws IllegalArgumentException if <tt>values.length < size()</tt>.
*/
public void toArray(double[] values) {
	if (values.length < size) throw new IllegalArgumentException("values too small");
	if (this.isNoView) System.arraycopy(this.elements,0,values,0,this.elements.length);
	else super.toArray(values);
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
/**
 * Returns the dot product of two vectors x and y, which is <tt>Sum(x[i]*y[i])</tt>.
 * Where <tt>x == this</tt>.
 * Operates on cells at indexes <tt>from .. Min(size(),y.size(),from+length)-1</tt>. 
 * @param y the second vector.
 * @param from the first index to be considered.
 * @param length the number of cells to be considered.
 * @return the sum of products; zero if <tt>from<0 || length<0</tt>.
 */
public double zDotProduct(DoubleMatrix1D y, int from, int length) {
	if (!(y instanceof DenseDoubleMatrix1D)) {
		return super.zDotProduct(y, from, length);
	}
	DenseDoubleMatrix1D yy = (DenseDoubleMatrix1D) y;
	
	int tail = from + length;
	if (from < 0 || length < 0) return 0;
	if (size < tail) tail = size;
	if (y.size < tail) tail = y.size;
	int min = tail-from;
	
	int i = index(from);
	int j = yy.index(from);
	int s = stride;
	int ys = yy.stride;
	final double[] elems = this.elements;
	final double[] yElems = yy.elements;
	if (elems==null || yElems==null) throw new InternalError();
	
	double sum = 0;
	/*
	// unoptimized
	for (int k = min; --k >= 0;) {
		sum += elems[i] * yElems[j];
		i += s;
		j += ys;
	}
	*/
	
	// optimized
	// loop unrolling
	i -= s;
	j -= ys;
	for (int k=min/4; --k >= 0; ) { 
		sum += elems[i += s] * yElems[j += ys] + 
			elems[i += s] * yElems[j += ys] +
			elems[i += s] * yElems[j += ys] +
			elems[i += s] * yElems[j += ys];
	}		
	for (int k=min%4; --k >= 0; ) {
		sum += elems[i += s] * yElems[j += ys];
	}
	return sum;
}
/**
 * Returns the sum of all cells; <tt>Sum( x[i] )</tt>.
 * @return the sum.
 */
public double zSum() {
	double sum = 0;
	int s=stride;
	int i=index(0);
	final double[] elems = this.elements;
	if (elems==null) throw new InternalError();
	for (int k=size; --k >= 0; ) {
		sum += elems[i];
		i += s;
	}
	return sum;
}
}
