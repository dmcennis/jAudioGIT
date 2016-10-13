/*
Copyright ï¿½ 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package cern.colt.matrix;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.impl.AbstractMatrix1D;
/**
Abstract base class for 1-d matrices (aka <i>vectors</i>) holding <tt>double</tt> elements.
First see the <a href="package-summary.html">package summary</a> and javadoc <a href="package-tree.html">tree view</a> to get the broad picture.
<p>
A matrix has a number of cells (its <i>size</i>), which are assigned upon instance construction.
Elements are accessed via zero based indexes.
Legal indexes are of the form <tt>[0..size()-1]</tt>.
Any attempt to access an element at a coordinate <tt>index&lt;0 || index&gt;=size()</tt> will throw an <tt>IndexOutOfBoundsException</tt>.

@author wolfgang.hoschek@cern.ch
@version 1.0, 09/24/99
*/
public abstract class DoubleMatrix1D extends AbstractMatrix1D {
/**
 * Makes this class non instantiable, but still let's others inherit from it.
 */
protected DoubleMatrix1D() {}
/**
Applies a function to each cell and aggregates the results.
Returns a value <tt>v</tt> such that <tt>v==a(size())</tt> where <tt>a(i) == aggr( a(i-1), f(get(i)) )</tt> and terminators are <tt>a(1) == f(get(0)), a(0)==Double.NaN</tt>.
<p>
<b>Example:</b>
<pre>
cern.jet.math.Functions F = cern.jet.math.Functions.functions;
matrix = 0 1 2 3 

// Sum( x[i]*x[i] ) 
matrix.aggregate(F.plus,F.square);
--> 14
</pre>
For further examples, see the <a href="package-summary.html#FunctionObjects">package doc</a>.

@param aggr an aggregation function taking as first argument the current aggregation and as second argument the transformed current cell value.
@param f a function transforming the current cell value.
@return the aggregated measure.
@see cern.jet.math.Functions
*/
public double aggregate(cern.colt.function.DoubleDoubleFunction aggr, cern.colt.function.DoubleFunction f) {
	if (size==0) return Double.NaN;
	double a = f.apply(getQuick(size-1));
	for (int i=size-1; --i >= 0; ) {
		a = aggr.apply(a, f.apply(getQuick(i)));
	}
	return a;
}
/**
Applies a function to each corresponding cell of two matrices and aggregates the results.
Returns a value <tt>v</tt> such that <tt>v==a(size())</tt> where <tt>a(i) == aggr( a(i-1), f(get(i),other.get(i)) )</tt> and terminators are <tt>a(1) == f(get(0),other.get(0)), a(0)==Double.NaN</tt>.
<p>
<b>Example:</b>
<pre>
cern.jet.math.Functions F = cern.jet.math.Functions.functions;
x = 0 1 2 3 
y = 0 1 2 3 

// Sum( x[i]*y[i] )
x.aggregate(y, F.plus, F.mult);
--> 14

// Sum( (x[i]+y[i])^2 )
x.aggregate(y, F.plus, F.chain(F.square,F.plus));
--> 56
</pre>
For further examples, see the <a href="package-summary.html#FunctionObjects">package doc</a>.

@param aggr an aggregation function taking as first argument the current aggregation and as second argument the transformed current cell values.
@param f a function transforming the current cell values.
@return the aggregated measure.
@throws	IllegalArgumentException if <tt>size() != other.size()</tt>.
@see cern.jet.math.Functions
*/
public double aggregate(DoubleMatrix1D other, cern.colt.function.DoubleDoubleFunction aggr, cern.colt.function.DoubleDoubleFunction f) {
	checkSize(other);
	if (size==0) return Double.NaN;
	double a = f.apply(getQuick(size-1),other.getQuick(size-1));
	for (int i=size-1; --i >= 0; ) {
		a = aggr.apply(a, f.apply(getQuick(i),other.getQuick(i)));
	}
	return a;
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
	if (values.length != size) throw new IllegalArgumentException("Must have same number of cells: length="+values.length+"size()="+size());
	for (int i=size; --i >= 0;) {
		setQuick(i,values[i]);
	}
	return this;
}
/**
 * Sets all cells to the state specified by <tt>value</tt>.
 * @param    value the value to be filled into the cells.
 * @return <tt>this</tt> (for convenience only).
 */
public DoubleMatrix1D assign(double value) {
	for (int i=size; --i >= 0;) {
		setQuick(i,value);
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
	for (int i=size; --i >= 0; ) {
		setQuick(i, function.apply(getQuick(i)));
	}
	return this;
}
/**
 * Replaces all cell values of the receiver with the values of another matrix.
 * Both matrices must have the same size.
 * If both matrices share the same cells (as is the case if they are views derived from the same matrix) and intersect in an ambiguous way, then replaces <i>as if</i> using an intermediate auxiliary deep copy of <tt>other</tt>.
 *
 * @param     other   the source matrix to copy from (may be identical to the receiver).
 * @return <tt>this</tt> (for convenience only).
 * @throws	IllegalArgumentException if <tt>size() != other.size()</tt>.
 */
public DoubleMatrix1D assign(DoubleMatrix1D other) {
	if (other==this) return this;
	checkSize(other);
	if (haveSharedCells(other)) other = other.copy();
	
	for (int i=size; --i >= 0;) {
		setQuick(i,other.getQuick(i));
	}
	return this;
}
/**
Assigns the result of a function to each cell; <tt>x[i] = function(x[i],y[i])</tt>.
<p>
<b>Example:</b>
<pre>
// assign x[i] = x[i]<sup>y[i]</sup>
m1 = 0 1 2 3;
m2 = 0 2 4 6;
m1.assign(m2, cern.jet.math.Functions.pow);
-->
m1 == 1 1 16 729
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
	checkSize(y);
	for (int i=size; --i >= 0; ) {
		setQuick(i, function.apply(getQuick(i), y.getQuick(i)));
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
public DoubleMatrix1D assign(DoubleMatrix1D y, cern.colt.function.DoubleDoubleFunction function, cern.colt.list.IntArrayList nonZeroIndexes) {
	checkSize(y);
	int[] nonZeroElements = nonZeroIndexes.elements();

	// specialized for speed
	if (function==cern.jet.math.Functions.mult) {  // x[i] = x[i] * y[i]
	    int j = 0;
		for (int index=nonZeroIndexes.size(); --index >= 0; ) {
			int i = nonZeroElements[index];
			for (; j<i; j++) setQuick(j,0); // x[i] = 0 for all zeros
			setQuick(i, getQuick(i) * y.getQuick(i));  // x[i] * y[i] for all nonZeros
			j++;
		}
	}
	else if (function instanceof cern.jet.math.PlusMult) {
		double multiplicator = ((cern.jet.math.PlusMult) function).multiplicator;
		if (multiplicator == 0) { // x[i] = x[i] + 0*y[i]
			return this;
		}
		else if (multiplicator == 1) { // x[i] = x[i] + y[i]
			for (int index=nonZeroIndexes.size(); --index >= 0; ) {
				int i = nonZeroElements[index];
				setQuick(i, getQuick(i) + y.getQuick(i));
			}
		}
		else if (multiplicator == -1) { // x[i] = x[i] - y[i]
			for (int index=nonZeroIndexes.size(); --index >= 0; ) {
				int i = nonZeroElements[index];
				setQuick(i, getQuick(i) - y.getQuick(i));
			}
		}
		else { // the general case x[i] = x[i] + mult*y[i]
			for (int index=nonZeroIndexes.size(); --index >= 0; ) {
				int i = nonZeroElements[index];
				setQuick(i, getQuick(i) + multiplicator*y.getQuick(i));
			}
		}
	}
	else { // the general case x[i] = f(x[i],y[i])
		return assign(y,function);
	}
	return this;
}
/**
 * Returns the number of cells having non-zero values; ignores tolerance.
 */
public int cardinality() {
	int cardinality = 0;
	for (int i=size; --i >= 0;) {
		if (getQuick(i) != 0) cardinality++;
	}
	return cardinality;
}
/**
 * Returns the number of cells having non-zero values, but at most maxCardinality; ignores tolerance.
 */
protected int cardinality(int maxCardinality) {
	int cardinality = 0;
	int i=size; 
	while (--i >= 0 && cardinality < maxCardinality) {
		if (getQuick(i) != 0) cardinality++;
	}
	return cardinality;
}
/**
 * Constructs and returns a deep copy of the receiver.
 * <p>
 * <b>Note that the returned matrix is an independent deep copy.</b>
 * The returned matrix is not backed by this matrix, so changes in the returned matrix are not reflected in this matrix, and vice-versa. 
 *
 * @return  a deep copy of the receiver.
 */
public DoubleMatrix1D copy() {
	DoubleMatrix1D copy = like();
	copy.assign(this);
	return copy;
}
/**
 * Returns whether all cells are equal to the given value.
 *
 * @param     value the value to test against.
 * @return    <tt>true</tt> if all cells are equal to the given value, <tt>false</tt> otherwise.
 */
public boolean equals(double value) {
	return cern.colt.matrix.linalg.Property.DEFAULT.equals(this,value);
}
/**
 * Compares this object against the specified object.
 * The result is <code>true</code> if and only if the argument is 
 * not <code>null</code> and is at least a <code>DoubleMatrix1D</code> object
 * that has the same sizes as the receiver and 
 * has exactly the same values at the same indexes.
 * @param   obj   the object to compare with.
 * @return  <code>true</code> if the objects are the same;
 *          <code>false</code> otherwise.
 */
public boolean equals(Object obj) {
	if (this == obj) return true;
	if (obj == null) return false;
	if (!(obj instanceof DoubleMatrix1D)) return false;

	return cern.colt.matrix.linalg.Property.DEFAULT.equals(this,(DoubleMatrix1D) obj);
}
/**
 * Returns the matrix cell value at coordinate <tt>index</tt>.
 *
 * @param     index   the index of the cell.
 * @return    the value of the specified cell.
 * @throws	IndexOutOfBoundsException if <tt>index&lt;0 || index&gt;=size()</tt>.
 */
public double get(int index) {
	if (index<0 || index>=size) checkIndex(index);
	return getQuick(index);
}
/**
 * Returns the content of this matrix if it is a wrapper; or <tt>this</tt> otherwise.
 * Override this method in wrappers.
 */
protected DoubleMatrix1D getContent() {
	return this;
}
/**
Fills the coordinates and values of cells having non-zero values into the specified lists.
Fills into the lists, starting at index 0.
After this call returns the specified lists all have a new size, the number of non-zero values.
<p>
In general, fill order is <i>unspecified</i>.
This implementation fills like: <tt>for (index = 0..size()-1)  do ... </tt>.
However, subclasses are free to us any other order, even an order that may change over time as cell values are changed.
(Of course, result lists indexes are guaranteed to correspond to the same cell).
<p>
<b>Example:</b>
<br>
<pre>
0, 0, 8, 0, 7
-->
indexList  = (2,4)
valueList  = (8,7)
</pre>
In other words, <tt>get(2)==8, get(4)==7</tt>.

@param indexList the list to be filled with indexes, can have any size.
@param valueList the list to be filled with values, can have any size.
*/
public void getNonZeros(IntArrayList indexList, DoubleArrayList valueList) {
	boolean fillIndexList = indexList != null;
	boolean fillValueList = valueList != null;
	if (fillIndexList) indexList.clear(); 
	if (fillValueList) valueList.clear();
	int s = size;
	for (int i=0; i < s; i++) {
		double value = getQuick(i);
		if (value != 0) {
			if (fillIndexList) indexList.add(i);
			if (fillValueList) valueList.add(value);
		}
	}
}
/**
Fills the coordinates and values of cells having non-zero values into the specified lists.
Fills into the lists, starting at index 0.
After this call returns the specified lists all have a new size, the number of non-zero values.
<p>
In general, fill order is <i>unspecified</i>.
This implementation fills like: <tt>for (index = 0..size()-1)  do ... </tt>.
However, subclasses are free to us any other order, even an order that may change over time as cell values are changed.
(Of course, result lists indexes are guaranteed to correspond to the same cell).
<p>
<b>Example:</b>
<br>
<pre>
0, 0, 8, 0, 7
-->
indexList  = (2,4)
valueList  = (8,7)
</pre>
In other words, <tt>get(2)==8, get(4)==7</tt>.

@param indexList the list to be filled with indexes, can have any size.
@param valueList the list to be filled with values, can have any size.
*/
public void getNonZeros(IntArrayList indexList, DoubleArrayList valueList, int maxCardinality) {
	boolean fillIndexList = indexList != null;
	boolean fillValueList = valueList != null;
	int card = cardinality(maxCardinality);
	if (fillIndexList) indexList.setSize(card);
	if (fillValueList) valueList.setSize(card);
	if (!(card<maxCardinality)) return;

	if (fillIndexList) indexList.setSize(0);
	if (fillValueList) valueList.setSize(0);
	int s = size;
	for (int i=0; i < s; i++) {
		double value = getQuick(i);
		if (value != 0) {
			if (fillIndexList) indexList.add(i);
			if (fillValueList) valueList.add(value);
		}
	}
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
public abstract double getQuick(int index);
/**
 * Returns <tt>true</tt> if both matrices share at least one identical cell.
 */
protected boolean haveSharedCells(DoubleMatrix1D other) {
	if (other==null) return false;
	if (this==other) return true;
	return getContent().haveSharedCellsRaw(other.getContent());
}	
/**
 * Returns <tt>true</tt> if both matrices share at least one identical cell.
 */
protected boolean haveSharedCellsRaw(DoubleMatrix1D other) {
	return false;
}	
/**
 * Construct and returns a new empty matrix <i>of the same dynamic type</i> as the receiver, having the same size.
 * For example, if the receiver is an instance of type <tt>DenseDoubleMatrix1D</tt> the new matrix must also be of type <tt>DenseDoubleMatrix1D</tt>,
 * if the receiver is an instance of type <tt>SparseDoubleMatrix1D</tt> the new matrix must also be of type <tt>SparseDoubleMatrix1D</tt>, etc.
 * In general, the new matrix should have internal parametrization as similar as possible.
 *
 * @return  a new empty matrix of the same dynamic type.
 */
public DoubleMatrix1D like() {
	return like(size);
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
public abstract DoubleMatrix1D like(int size);
/**
 * Construct and returns a new 2-d matrix <i>of the corresponding dynamic type</i>, entirelly independent of the receiver.
 * For example, if the receiver is an instance of type <tt>DenseDoubleMatrix1D</tt> the new matrix must be of type <tt>DenseDoubleMatrix2D</tt>,
 * if the receiver is an instance of type <tt>SparseDoubleMatrix1D</tt> the new matrix must be of type <tt>SparseDoubleMatrix2D</tt>, etc.
 *
 * @param rows the number of rows the matrix shall have.
 * @param columns the number of columns the matrix shall have.
 * @return  a new matrix of the corresponding dynamic type.
 */
public abstract DoubleMatrix2D like2D(int rows, int columns);
/**
 * Sets the matrix cell at coordinate <tt>index</tt> to the specified value.
 *
 * @param     index   the index of the cell.
 * @param    value the value to be filled into the specified cell.
 * @throws	IndexOutOfBoundsException if <tt>index&lt;0 || index&gt;=size()</tt>.
 */
public void set(int index, double value) {
	if (index<0 || index>=size) checkIndex(index);
	setQuick(index,value);
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
public abstract void setQuick(int index, double value);
/**
Swaps each element <tt>this[i]</tt> with <tt>other[i]</tt>.
@throws IllegalArgumentException if <tt>size() != other.size()</tt>.
*/
public void swap(DoubleMatrix1D other) {
	checkSize(other);
	for (int i=size; --i >= 0; ) {
		double tmp = getQuick(i);
		setQuick(i, other.getQuick(i));
		other.setQuick(i, tmp);
	}
	return;
}
/**
Constructs and returns a 1-dimensional array containing the cell values.
The values are copied. So subsequent changes in <tt>values</tt> are not reflected in the matrix, and vice-versa.
The returned array <tt>values</tt> has the form 
<br>
<tt>for (int i=0; i < size(); i++) values[i] = get(i);</tt>

@return an array filled with the values of the cells.
*/
public double[] toArray() {
	double[] values = new double[size];
	toArray(values);
	return values;
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
	for (int i=size; --i >= 0; ) {
		values[i] = getQuick(i);
	}
}
/**
 * Returns a string representation using default formatting.
 * @see cern.colt.matrix.doublealgo.Formatter
 */
public String toString() {
	return new cern.colt.matrix.doublealgo.Formatter().toString(this);
}
/**
 * Constructs and returns a new view equal to the receiver.
 * The view is a shallow clone. Calls <code>clone()</code> and casts the result.
 * <p>
 * <b>Note that the view is not a deep copy.</b>
 * The returned matrix is backed by this matrix, so changes in the returned matrix are reflected in this matrix, and vice-versa. 
 * <p>
 * Use {@link #copy()} to construct an independent deep copy rather than a new view.
 *
 * @return  a new view of the receiver.
 */
protected DoubleMatrix1D view() {
	return (DoubleMatrix1D) clone();
}
/**
Constructs and returns a new <i>flip view</i>.
What used to be index <tt>0</tt> is now index <tt>size()-1</tt>, ..., what used to be index <tt>size()-1</tt> is now index <tt>0</tt>.
The returned view is backed by this matrix, so changes in the returned view are reflected in this matrix, and vice-versa.

@return a new flip view.
*/
public DoubleMatrix1D viewFlip() {
	return (DoubleMatrix1D) (view().vFlip());
}
/**
Constructs and returns a new <i>sub-range view</i> that is a <tt>width</tt> sub matrix starting at <tt>index</tt>.

Operations on the returned view can only be applied to the restricted range.
Any attempt to access coordinates not contained in the view will throw an <tt>IndexOutOfBoundsException</tt>.
<p>
<b>Note that the view is really just a range restriction:</b> 
The returned matrix is backed by this matrix, so changes in the returned matrix are reflected in this matrix, and vice-versa. 
<p>
The view contains the cells from <tt>index..index+width-1</tt>.
and has <tt>view.size() == width</tt>.
A view's legal coordinates are again zero based, as usual.
In other words, legal coordinates of the view are <tt>0 .. view.size()-1==width-1</tt>.
As usual, any attempt to access a cell at other coordinates will throw an <tt>IndexOutOfBoundsException</tt>.

@param     index   The index of the first cell.
@param     width   The width of the range.
@throws	IndexOutOfBoundsException if <tt>index<0 || width<0 || index+width>size()</tt>.
@return the new view.
		
*/
public DoubleMatrix1D viewPart(int index, int width) {
	return (DoubleMatrix1D) (view().vPart(index,width));
}
/**
Constructs and returns a new <i>selection view</i> that is a matrix holding the indicated cells.
There holds <tt>view.size() == indexes.length</tt> and <tt>view.get(i) == this.get(indexes[i])</tt>.
Indexes can occur multiple times and can be in arbitrary order.
<p>
<b>Example:</b>
<br>
<pre>
this     = (0,0,8,0,7)
indexes  = (0,2,4,2)
-->
view     = (0,8,7,8)
</pre>
Note that modifying <tt>indexes</tt> after this call has returned has no effect on the view.
The returned view is backed by this matrix, so changes in the returned view are reflected in this matrix, and vice-versa. 

@param  indexes   The indexes of the cells that shall be visible in the new view. To indicate that <i>all</i> cells shall be visible, simply set this parameter to <tt>null</tt>.
@return the new view.
@throws IndexOutOfBoundsException if <tt>!(0 <= indexes[i] < size())</tt> for any <tt>i=0..indexes.length()-1</tt>.
*/
public DoubleMatrix1D viewSelection(int[] indexes) {
	// check for "all"
	if (indexes==null) {
		indexes = new int[size];
		for (int i=size; --i >= 0; ) indexes[i] = i;
	}
	
	checkIndexes(indexes);
	int[] offsets = new int[indexes.length];
	for (int i=indexes.length; --i >= 0; ) {
		offsets[i] = index(indexes[i]);
	}
	return viewSelectionLike(offsets);
}
/**
Constructs and returns a new <i>selection view</i> that is a matrix holding the cells matching the given condition.
Applies the condition to each cell and takes only those cells where <tt>condition.apply(get(i))</tt> yields <tt>true</tt>.
<p>
<b>Example:</b>
<br>
<pre>
// extract and view all cells with even value
matrix = 0 1 2 3 
matrix.viewSelection( 
&nbsp;&nbsp;&nbsp;new DoubleProcedure() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;public final boolean apply(double a) { return a % 2 == 0; }
&nbsp;&nbsp;&nbsp;}
);
-->
matrix ==  0 2
</pre>
For further examples, see the <a href="package-summary.html#FunctionObjects">package doc</a>.
The returned view is backed by this matrix, so changes in the returned view are reflected in this matrix, and vice-versa. 

@param  condition The condition to be matched.
@return the new view.
*/
public DoubleMatrix1D viewSelection(cern.colt.function.DoubleProcedure condition) {
	IntArrayList matches = new IntArrayList();
	for (int i=0; i < size; i++) {
		if (condition.apply(getQuick(i))) matches.add(i);
	}
	matches.trimToSize();
	return viewSelection(matches.elements());
}
/**
 * Construct and returns a new selection view.
 *
 * @param offsets the offsets of the visible elements.
 * @return  a new view.
 */
protected abstract DoubleMatrix1D viewSelectionLike(int[] offsets);
/**
Sorts the vector into ascending order, according to the <i>natural ordering</i>.
This sort is guaranteed to be <i>stable</i>.
For further information, see {@link cern.colt.matrix.doublealgo.Sorting#sort(DoubleMatrix1D)}.
For more advanced sorting functionality, see {@link cern.colt.matrix.doublealgo.Sorting}.
@return a new sorted vector (matrix) view.
*/
public DoubleMatrix1D viewSorted() {
	return cern.colt.matrix.doublealgo.Sorting.mergeSort.sort(this);
}
/**
Constructs and returns a new <i>stride view</i> which is a sub matrix consisting of every i-th cell.
More specifically, the view has size <tt>this.size()/stride</tt> holding cells <tt>this.get(i*stride)</tt> for all <tt>i = 0..size()/stride - 1</tt>.

@param  stride  the step factor.
@throws	IndexOutOfBoundsException if <tt>stride <= 0</tt>.
@return the new view.
		
*/
public DoubleMatrix1D viewStrides(int stride) {
	return (DoubleMatrix1D) (view().vStrides(stride));
}
/**
 * Applies a procedure to each cell's value.
 * Iterates downwards from <tt>[size()-1]</tt> to <tt>[0]</tt>,
 * as demonstrated by this snippet:
 * <pre>
 * for (int i=size(); --i >=0;) {
 *    if (!procedure.apply(getQuick(i))) return false;
 * }
 * return true;
 * </pre>
 * Note that an implementation may use more efficient techniques, but must not use any other order.
 *
 * @param procedure a procedure object taking as argument the current cell's value. Stops iteration if the procedure returns <tt>false</tt>, otherwise continues. 
 * @return <tt>false</tt> if the procedure stopped before all elements where iterated over, <tt>true</tt> otherwise. 
 */
private boolean xforEach(final cern.colt.function.DoubleProcedure procedure) {
	for (int i=size; --i >= 0;) {
		if (!procedure.apply(getQuick(i))) return false;
	}
	return true;
}
/**
 * Returns the dot product of two vectors x and y, which is <tt>Sum(x[i]*y[i])</tt>.
 * Where <tt>x == this</tt>.
 * Operates on cells at indexes <tt>0 .. Math.min(size(),y.size())</tt>.
 * @param y the second vector.
 * @return the sum of products.
 */
public double zDotProduct(DoubleMatrix1D y) {
	return zDotProduct(y,0,size);
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
	if (from<0 || length<=0) return 0;
	
	int tail = from+length;
	if (size < tail) tail = size;
	if (y.size < tail) tail = y.size;
	length = tail-from;
	
	double sum = 0;
	int i = tail-1;
	for (int k=length; --k >= 0; i--) {
		sum += getQuick(i) * y.getQuick(i);
	}
	return sum;
}
/**
 * Returns the dot product of two vectors x and y, which is <tt>Sum(x[i]*y[i])</tt>.
 * Where <tt>x == this</tt>.
 * @param y the second vector.
 * @param nonZeroIndexes the indexes of cells in <tt>y</tt>having a non-zero value.
 * @return the sum of products.
 */
public double zDotProduct(DoubleMatrix1D y, int from, int length, IntArrayList nonZeroIndexes) {
	// determine minimum length
	if (from<0 || length<=0) return 0;
	
	int tail = from+length;
	if (size < tail) tail = size;
	if (y.size < tail) tail = y.size;
	length = tail-from;
	if (length<=0) return 0;

	// setup
	int[] nonZeroIndexElements = nonZeroIndexes.elements();
	int index = 0;
	int s = nonZeroIndexes.size();
	
	// skip to start	
	while ((index < s) && nonZeroIndexElements[index]<from) index++; 

	// now the sparse dot product
	int i;
	double sum = 0;
	while ((--length >= 0) && (index < s) && ((i=nonZeroIndexElements[index]) < tail)) {
		sum += getQuick(i) * y.getQuick(i);
		index++;
	}
	
	return sum;
}
/**
 * Returns the dot product of two vectors x and y, which is <tt>Sum(x[i]*y[i])</tt>.
 * Where <tt>x == this</tt>.
 * @param y the second vector.
 * @param nonZeroIndexes the indexes of cells in <tt>y</tt>having a non-zero value.
 * @return the sum of products.
 */
protected double zDotProduct(DoubleMatrix1D y, IntArrayList nonZeroIndexes) {
	return zDotProduct(y,0,size,nonZeroIndexes);
	/*
	double sum = 0;
	int[] nonZeroIndexElements = nonZeroIndexes.elements();
	for (int index=nonZeroIndexes.size(); --index >= 0; ) {
		int i = nonZeroIndexElements[index];
		sum += getQuick(i) * y.getQuick(i);
	}
	return sum;
	*/
}
/**
 * Returns the sum of all cells; <tt>Sum( x[i] )</tt>.
 * @return the sum.
 */
public double zSum() {
	if (size()==0) return 0;
	return aggregate(cern.jet.math.Functions.plus, cern.jet.math.Functions.identity);
}
}
