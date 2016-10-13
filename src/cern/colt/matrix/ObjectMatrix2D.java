/*
Copyright ï¿½ 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package cern.colt.matrix;

import cern.colt.list.IntArrayList;
import cern.colt.list.ObjectArrayList;
import cern.colt.matrix.impl.AbstractMatrix2D;
/**
Abstract base class for 2-d matrices holding <tt>Object</tt> elements.
First see the <a href="package-summary.html">package summary</a> and javadoc <a href="package-tree.html">tree view</a> to get the broad picture.
<p>
A matrix has a number of rows and columns, which are assigned upon instance construction - The matrix's size is then <tt>rows()*columns()</tt>.
Elements are accessed via <tt>[row,column]</tt> coordinates.
Legal coordinates range from <tt>[0,0]</tt> to <tt>[rows()-1,columns()-1]</tt>.
Any attempt to access an element at a coordinate <tt>column&lt;0 || column&gt;=columns() || row&lt;0 || row&gt;=rows()</tt> will throw an <tt>IndexOutOfBoundsException</tt>.
<p>
<b>Note</b> that this implementation is not synchronized.

@author wolfgang.hoschek@cern.ch
@version 1.0, 09/24/99
*/
public abstract class ObjectMatrix2D extends AbstractMatrix2D {
/**
 * Makes this class non instantiable, but still let's others inherit from it.
 */
protected ObjectMatrix2D() {}
/**
Applies a function to each cell and aggregates the results.
Returns a value <tt>v</tt> such that <tt>v==a(size())</tt> where <tt>a(i) == aggr( a(i-1), f(get(row,column)) )</tt> and terminators are <tt>a(1) == f(get(0,0)), a(0)==null</tt>.
<p>
<b>Example:</b>
<pre>
cern.jet.math.Functions F = cern.jet.math.Functions.functions;
2 x 2 matrix
0 1
2 3

// Sum( x[row,col]*x[row,col] ) 
matrix.aggregate(F.plus,F.square);
--> 14
</pre>
For further examples, see the <a href="package-summary.html#FunctionObjects">package doc</a>.

@param aggr an aggregation function taking as first argument the current aggregation and as second argument the transformed current cell value.
@param f a function transforming the current cell value.
@return the aggregated measure.
@see cern.jet.math.Functions
*/
public Object aggregate(cern.colt.function.ObjectObjectFunction aggr, cern.colt.function.ObjectFunction f) {
	if (size()==0) return null;
	Object a = f.apply(getQuick(rows-1,columns-1));
	int d = 1; // last cell already done
	for (int row=rows; --row >= 0; ) {
		for (int column=columns-d; --column >= 0; ) {
			a = aggr.apply(a, f.apply(getQuick(row,column)));
		}
		d = 0;
	}
	return a;
}
/**
Applies a function to each corresponding cell of two matrices and aggregates the results.
Returns a value <tt>v</tt> such that <tt>v==a(size())</tt> where <tt>a(i) == aggr( a(i-1), f(get(row,column),other.get(row,column)) )</tt> and terminators are <tt>a(1) == f(get(0,0),other.get(0,0)), a(0)==null</tt>.
<p>
<b>Example:</b>
<pre>
cern.jet.math.Functions F = cern.jet.math.Functions.functions;
x == 2 x 2 matrix
0 1
2 3

y == 2 x 2 matrix
0 1
2 3

// Sum( x[row,col] * y[row,col] ) 
x.aggregate(y, F.plus, F.mult);
--> 14

// Sum( (x[row,col] + y[row,col])^2 )
x.aggregate(y, F.plus, F.chain(F.square,F.plus));
--> 56
</pre>
For further examples, see the <a href="package-summary.html#FunctionObjects">package doc</a>.

@param aggr an aggregation function taking as first argument the current aggregation and as second argument the transformed current cell values.
@param f a function transforming the current cell values.
@return the aggregated measure.
@throws	IllegalArgumentException if <tt>columns() != other.columns() || rows() != other.rows()</tt>
@see cern.jet.math.Functions
*/
public Object aggregate(ObjectMatrix2D other, cern.colt.function.ObjectObjectFunction aggr, cern.colt.function.ObjectObjectFunction f) {
	checkShape(other);
	if (size()==0) return null;
	Object a = f.apply(getQuick(rows-1,columns-1),other.getQuick(rows-1,columns-1));
	int d = 1; // last cell already done
	for (int row=rows; --row >= 0; ) {
		for (int column=columns-d; --column >= 0; ) {
			a = aggr.apply(a, f.apply(getQuick(row,column), other.getQuick(row,column)));
		}
		d = 0;
	}
	return a;
}
/**
 * Sets all cells to the state specified by <tt>values</tt>.
 * <tt>values</tt> is required to have the form <tt>values[row][column]</tt>
 * and have exactly the same number of rows and columns as the receiver.
 * <p>
 * The values are copied. So subsequent changes in <tt>values</tt> are not reflected in the matrix, and vice-versa.
 *
 * @param    values the values to be filled into the cells.
 * @return <tt>this</tt> (for convenience only).
 * @throws IllegalArgumentException if <tt>values.length != rows() || for any 0 &lt;= row &lt; rows(): values[row].length != columns()</tt>.
 */
public ObjectMatrix2D assign(Object[][] values) {
	if (values.length != rows) throw new IllegalArgumentException("Must have same number of rows: rows="+values.length+"rows()="+rows());
	for (int row=rows; --row >= 0;) {
		Object[] currentRow = values[row];
		if (currentRow.length != columns) throw new IllegalArgumentException("Must have same number of columns in every row: columns="+currentRow.length+"columns()="+columns());
		for (int column=columns; --column >= 0;) {
			setQuick(row,column,currentRow[column]);
		}
	}
	return this;
}
/**
Assigns the result of a function to each cell; <tt>x[row,col] = function(x[row,col])</tt>.
<p>
<b>Example:</b>
<pre>
matrix = 2 x 2 matrix 
0.5 1.5      
2.5 3.5

// change each cell to its sine
matrix.assign(cern.jet.math.Functions.sin);
-->
2 x 2 matrix
0.479426  0.997495 
0.598472 -0.350783
</pre>
For further examples, see the <a href="package-summary.html#FunctionObjects">package doc</a>.

@param function a function object taking as argument the current cell's value.
@return <tt>this</tt> (for convenience only).
@see cern.jet.math.Functions
*/
public ObjectMatrix2D assign(cern.colt.function.ObjectFunction function) {
	for (int row=rows; --row >= 0; ) {
		for (int column=columns; --column >= 0; ) {
			setQuick(row,column, function.apply(getQuick(row,column)));
		}
	}
	return this;
}
/**
 * Replaces all cell values of the receiver with the values of another matrix.
 * Both matrices must have the same number of rows and columns.
 * If both matrices share the same cells (as is the case if they are views derived from the same matrix) and intersect in an ambiguous way, then replaces <i>as if</i> using an intermediate auxiliary deep copy of <tt>other</tt>.
 *
 * @param     other   the source matrix to copy from (may be identical to the receiver).
 * @return <tt>this</tt> (for convenience only).
 * @throws	IllegalArgumentException if <tt>columns() != other.columns() || rows() != other.rows()</tt>
 */
public ObjectMatrix2D assign(ObjectMatrix2D other) {
	if (other==this) return this;
	checkShape(other);
	if (haveSharedCells(other)) other = other.copy();
	
	for (int row=rows; --row >= 0;) {
		for (int column=columns; --column >= 0;) {
			setQuick(row,column,other.getQuick(row,column));
		}
	}
	return this;
}
/**
Assigns the result of a function to each cell; <tt>x[row,col] = function(x[row,col],y[row,col])</tt>.
<p>
<b>Example:</b>
<pre>
// assign x[row,col] = x[row,col]<sup>y[row,col]</sup>
m1 = 2 x 2 matrix 
0 1 
2 3

m2 = 2 x 2 matrix 
0 2 
4 6

m1.assign(m2, cern.jet.math.Functions.pow);
-->
m1 == 2 x 2 matrix
 1   1 
16 729
</pre>
For further examples, see the <a href="package-summary.html#FunctionObjects">package doc</a>.

@param y the secondary matrix to operate on.
@param function a function object taking as first argument the current cell's value of <tt>this</tt>,
and as second argument the current cell's value of <tt>y</tt>,
@return <tt>this</tt> (for convenience only).
@throws	IllegalArgumentException if <tt>columns() != other.columns() || rows() != other.rows()</tt>
@see cern.jet.math.Functions
*/
public ObjectMatrix2D assign(ObjectMatrix2D y, cern.colt.function.ObjectObjectFunction function) {
	checkShape(y);
	for (int row=rows; --row >= 0; ) {
		for (int column=columns; --column >= 0; ) {
			setQuick(row,column, function.apply(getQuick(row,column), y.getQuick(row,column)));
		}
	}
	return this;
}
/**
 * Sets all cells to the state specified by <tt>value</tt>.
 * @param    value the value to be filled into the cells.
 * @return <tt>this</tt> (for convenience only).
 */
public ObjectMatrix2D assign(Object value) {
	for (int row=rows; --row >= 0;) {
		for (int column=columns; --column >= 0;) {
			setQuick(row,column,value);
		}
	}
	return this;
}
/**
 * Returns the number of cells having non-zero values; ignores tolerance.
 */
public int cardinality() {
	int cardinality = 0;
	for (int row=rows; --row >= 0;) {
		for (int column=columns; --column >= 0;) {
			if (getQuick(row,column) != null) cardinality++;
		}
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
public ObjectMatrix2D copy() {
	return like().assign(this);
}
/**
* Compares the specified Object with the receiver for equality.
* Equivalent to <tt>equals(otherObj,true)</tt>.  
*
* @param otherObj the Object to be compared for equality with the receiver.
* @return true if the specified Object is equal to the receiver.
*/
public boolean equals(Object otherObj) { //delta
	return equals(otherObj, true);
}
/**
* Compares the specified Object with the receiver for equality.
* Returns true if and only if the specified Object is also at least an ObjectMatrix2D, both matrices have the
* same size, and all corresponding pairs of cells in the two matrices are the same.
* In other words, two matrices are defined to be equal if they contain the
* same cell values in the same order.
* Tests elements for equality or identity as specified by <tt>testForEquality</tt>.
* When testing for equality, two elements <tt>e1</tt> and
* <tt>e2</tt> are <i>equal</i> if <tt>(e1==null ? e2==null :
* e1.equals(e2))</tt>.)  
*
* @param otherObj the Object to be compared for equality with the receiver.
* @param testForEquality if true -> tests for equality, otherwise for identity.
* @return true if the specified Object is equal to the receiver.
*/
public boolean equals(Object otherObj, boolean testForEquality) { //delta
	if (! (otherObj instanceof ObjectMatrix2D)) {return false;}
	if (this==otherObj) return true;
	if (otherObj==null) return false;
	ObjectMatrix2D other = (ObjectMatrix2D) otherObj;
	if (rows!=other.rows()) return false;
	if (columns!=other.columns()) return false;

	if (! testForEquality) {
		for (int row=rows; --row >= 0; ) {
			for (int column=columns; --column >= 0; ) {
				if (getQuick(row,column) != other.getQuick(row,column)) return false;
			}
		}
	}
	else {
		for (int row=rows; --row >= 0; ) {
			for (int column=columns; --column >= 0; ) {
				if (!(getQuick(row,column)==null ? other.getQuick(row,column)==null : getQuick(row,column).equals(other.getQuick(row,column)))) return false;
			}
		}
	}

	return true;

}
/**
 * Returns the matrix cell value at coordinate <tt>[row,column]</tt>.
 *
 * @param     row   the index of the row-coordinate.
 * @param     column   the index of the column-coordinate.
 * @return    the value of the specified cell.
 * @throws	IndexOutOfBoundsException if <tt>column&lt;0 || column&gt;=columns() || row&lt;0 || row&gt;=rows()</tt>
 */
public Object get(int row, int column) {
	if (column<0 || column>=columns || row<0 || row>=rows) throw new IndexOutOfBoundsException("row:"+row+", column:"+column);
	return getQuick(row,column);
}
/**
 * Returns the content of this matrix if it is a wrapper; or <tt>this</tt> otherwise.
 * Override this method in wrappers.
 */
protected ObjectMatrix2D getContent() {
	return this;
}
/**
Fills the coordinates and values of cells having non-zero values into the specified lists.
Fills into the lists, starting at index 0.
After this call returns the specified lists all have a new size, the number of non-zero values.
<p>
In general, fill order is <i>unspecified</i>.
This implementation fills like <tt>for (row = 0..rows-1) for (column = 0..columns-1) do ... </tt>.
However, subclasses are free to us any other order, even an order that may change over time as cell values are changed.
(Of course, result lists indexes are guaranteed to correspond to the same cell).
<p>
<b>Example:</b>
<br>
<pre>
2 x 3 matrix:
0, 0, 8
0, 7, 0
-->
rowList    = (0,1)
columnList = (2,1)
valueList  = (8,7)
</pre>
In other words, <tt>get(0,2)==8, get(1,1)==7</tt>.

@param rowList the list to be filled with row indexes, can have any size.
@param columnList the list to be filled with column indexes, can have any size.
@param valueList the list to be filled with values, can have any size.
*/
public void getNonZeros(IntArrayList rowList, IntArrayList columnList, ObjectArrayList valueList) {
	rowList.clear(); 
	columnList.clear(); 
	valueList.clear();
	int r = rows;
	int c = columns;
	for (int row=0; row < r; row++) {
		for (int column=0; column < c; column++) {
			Object value = getQuick(row,column);
			if (value != null) {
				rowList.add(row);
				columnList.add(column);
				valueList.add(value);
			}
		}
	}
}
/**
 * Returns the matrix cell value at coordinate <tt>[row,column]</tt>.
 *
 * <p>Provided with invalid parameters this method may return invalid objects without throwing any exception.
 * <b>You should only use this method when you are absolutely sure that the coordinate is within bounds.</b>
 * Precondition (unchecked): <tt>0 &lt;= column &lt; columns() && 0 &lt;= row &lt; rows()</tt>.
 *
 * @param     row   the index of the row-coordinate.
 * @param     column   the index of the column-coordinate.
 * @return    the value at the specified coordinate.
 */
public abstract Object getQuick(int row, int column);
/**
 * Returns <tt>true</tt> if both matrices share at least one identical cell.
 */
protected boolean haveSharedCells(ObjectMatrix2D other) {
	if (other==null) return false;
	if (this==other) return true;
	return getContent().haveSharedCellsRaw(other.getContent());
}	
/**
 * Returns <tt>true</tt> if both matrices share at least one identical cell.
 */
protected boolean haveSharedCellsRaw(ObjectMatrix2D other) {
	return false;
}	
/**
 * Construct and returns a new empty matrix <i>of the same dynamic type</i> as the receiver, having the same number of rows and columns.
 * For example, if the receiver is an instance of type <tt>DenseObjectMatrix2D</tt> the new matrix must also be of type <tt>DenseObjectMatrix2D</tt>,
 * if the receiver is an instance of type <tt>SparseObjectMatrix2D</tt> the new matrix must also be of type <tt>SparseObjectMatrix2D</tt>, etc.
 * In general, the new matrix should have internal parametrization as similar as possible.
 *
 * @return  a new empty matrix of the same dynamic type.
 */
public ObjectMatrix2D like() {
	return like(rows,columns);
}
/**
 * Construct and returns a new empty matrix <i>of the same dynamic type</i> as the receiver, having the specified number of rows and columns.
 * For example, if the receiver is an instance of type <tt>DenseObjectMatrix2D</tt> the new matrix must also be of type <tt>DenseObjectMatrix2D</tt>,
 * if the receiver is an instance of type <tt>SparseObjectMatrix2D</tt> the new matrix must also be of type <tt>SparseObjectMatrix2D</tt>, etc.
 * In general, the new matrix should have internal parametrization as similar as possible.
 *
 * @param rows the number of rows the matrix shall have.
 * @param columns the number of columns the matrix shall have.
 * @return  a new empty matrix of the same dynamic type.
 */
public abstract ObjectMatrix2D like(int rows, int columns);
/**
 * Construct and returns a new 1-d matrix <i>of the corresponding dynamic type</i>, entirelly independent of the receiver.
 * For example, if the receiver is an instance of type <tt>DenseObjectMatrix2D</tt> the new matrix must be of type <tt>DenseObjectMatrix1D</tt>,
 * if the receiver is an instance of type <tt>SparseObjectMatrix2D</tt> the new matrix must be of type <tt>SparseObjectMatrix1D</tt>, etc.
 *
 * @param size the number of cells the matrix shall have.
 * @return  a new matrix of the corresponding dynamic type.
 */
public abstract ObjectMatrix1D like1D(int size);
/**
 * Construct and returns a new 1-d matrix <i>of the corresponding dynamic type</i>, sharing the same cells.
 * For example, if the receiver is an instance of type <tt>DenseObjectMatrix2D</tt> the new matrix must be of type <tt>DenseObjectMatrix1D</tt>,
 * if the receiver is an instance of type <tt>SparseObjectMatrix2D</tt> the new matrix must be of type <tt>SparseObjectMatrix1D</tt>, etc.
 *
 * @param size the number of cells the matrix shall have.
 * @param zero the index of the first element.
 * @param stride the number of indexes between any two elements, i.e. <tt>index(i+1)-index(i)</tt>.
 * @return  a new matrix of the corresponding dynamic type.
 */
protected abstract ObjectMatrix1D like1D(int size, int zero, int stride);
/**
 * Sets the matrix cell at coordinate <tt>[row,column]</tt> to the specified value.
 *
 * @param     row   the index of the row-coordinate.
 * @param     column   the index of the column-coordinate.
 * @param    value the value to be filled into the specified cell.
 * @throws	IndexOutOfBoundsException if <tt>column&lt;0 || column&gt;=columns() || row&lt;0 || row&gt;=rows()</tt>
 */
public void set(int row, int column, Object value) {
	if (column<0 || column>=columns || row<0 || row>=rows) throw new IndexOutOfBoundsException("row:"+row+", column:"+column);
	setQuick(row,column,value);
}
/**
 * Sets the matrix cell at coordinate <tt>[row,column]</tt> to the specified value.
 *
 * <p>Provided with invalid parameters this method may access illegal indexes without throwing any exception.
 * <b>You should only use this method when you are absolutely sure that the coordinate is within bounds.</b>
 * Precondition (unchecked): <tt>0 &lt;= column &lt; columns() && 0 &lt;= row &lt; rows()</tt>.
 *
 * @param     row   the index of the row-coordinate.
 * @param     column   the index of the column-coordinate.
 * @param    value the value to be filled into the specified cell.
 */
public abstract void setQuick(int row, int column, Object value);
/**
 * Constructs and returns a 2-dimensional array containing the cell values.
 * The returned array <tt>values</tt> has the form <tt>values[row][column]</tt>
 * and has the same number of rows and columns as the receiver.
 * <p>
 * The values are copied. So subsequent changes in <tt>values</tt> are not reflected in the matrix, and vice-versa.
 *
 * @return an array filled with the values of the cells.
 */
public Object[][] toArray() {
	Object[][] values = new Object[rows][columns];
	for (int row=rows; --row >= 0;) {
		Object[] currentRow = values[row];
		for (int column=columns; --column >= 0;) {
			currentRow[column] = getQuick(row,column);
		}
	}
	return values;
}
/**
 * Returns a string representation using default formatting.
 * @see cern.colt.matrix.objectalgo.Formatter
 */
public String toString() {
	return new cern.colt.matrix.objectalgo.Formatter().toString(this);
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
protected ObjectMatrix2D view() {
	return (ObjectMatrix2D) clone();
}
/**
Constructs and returns a new <i>slice view</i> representing the rows of the given column.
The returned view is backed by this matrix, so changes in the returned view are reflected in this matrix, and vice-versa.
To obtain a slice view on subranges, construct a sub-ranging view (<tt>viewPart(...)</tt>), then apply this method to the sub-range view.
<p> 
<b>Example:</b> 
<table border="0">
  <tr nowrap> 
	<td valign="top">2 x 3 matrix: <br>
	  1, 2, 3<br>
	  4, 5, 6 </td>
	<td>viewColumn(0) ==></td>
	<td valign="top">Matrix1D of size 2:<br>
	  1, 4</td>
   </tr>
</table>

@param column the column to fix.
@return a new slice view.
@throws IndexOutOfBoundsException if <tt>column < 0 || column >= columns()</tt>.
@see #viewRow(int)
*/
public ObjectMatrix1D viewColumn(int column) {
	checkColumn(column);
	int viewSize = this.rows;
	int viewZero = index(0,column);
	int viewStride = this.rowStride;
	return like1D(viewSize,viewZero,viewStride);
}
/**
Constructs and returns a new <i>flip view</i> along the column axis.
What used to be column <tt>0</tt> is now column <tt>columns()-1</tt>, ..., what used to be column <tt>columns()-1</tt> is now column <tt>0</tt>.
The returned view is backed by this matrix, so changes in the returned view are reflected in this matrix, and vice-versa.
<p> 
<b>Example:</b> 
<table border="0">
  <tr nowrap> 
	<td valign="top">2 x 3 matrix: <br>
	  1, 2, 3<br>
	  4, 5, 6 </td>
	<td>columnFlip ==></td>
	<td valign="top">2 x 3 matrix:<br>
	  3, 2, 1 <br>
	  6, 5, 4</td>
	<td>columnFlip ==></td>
	<td valign="top">2 x 3 matrix: <br>
	  1, 2, 3<br>
	  4, 5, 6 </td>
  </tr>
</table>

@return a new flip view.
@see #viewRowFlip()
*/
public ObjectMatrix2D viewColumnFlip() {
	return (ObjectMatrix2D) (view().vColumnFlip());
}
/**
Constructs and returns a new <i>dice (transposition) view</i>; Swaps axes; example: 3 x 4 matrix --> 4 x 3 matrix.
The view has both dimensions exchanged; what used to be columns become rows, what used to be rows become columns.
In other words: <tt>view.get(row,column)==this.get(column,row)</tt>.
This is a zero-copy transposition, taking O(1), i.e. constant time.
The returned view is backed by this matrix, so changes in the returned view are reflected in this matrix, and vice-versa. 
Use idioms like <tt>result = viewDice(A).copy()</tt> to generate an independent transposed matrix.
<p> 
<b>Example:</b> 
<table border="0">
  <tr nowrap> 
	<td valign="top">2 x 3 matrix: <br>
	  1, 2, 3<br>
	  4, 5, 6 </td>
	<td>transpose ==></td>
	<td valign="top">3 x 2 matrix:<br>
	  1, 4 <br>
	  2, 5 <br>
	  3, 6</td>
	<td>transpose ==></td>
	<td valign="top">2 x 3 matrix: <br>
	  1, 2, 3<br>
	  4, 5, 6 </td>
  </tr>
</table>

@return a new dice view.
*/
public ObjectMatrix2D viewDice() {
	return (ObjectMatrix2D) (view().vDice());
}
/**
Constructs and returns a new <i>sub-range view</i> that is a <tt>height x width</tt> sub matrix starting at <tt>[row,column]</tt>.

Operations on the returned view can only be applied to the restricted range.
Any attempt to access coordinates not contained in the view will throw an <tt>IndexOutOfBoundsException</tt>.
<p>
<b>Note that the view is really just a range restriction:</b> 
The returned matrix is backed by this matrix, so changes in the returned matrix are reflected in this matrix, and vice-versa. 
<p>
The view contains the cells from <tt>[row,column]</tt> to <tt>[row+height-1,column+width-1]</tt>, all inclusive.
and has <tt>view.rows() == height; view.columns() == width;</tt>.
A view's legal coordinates are again zero based, as usual.
In other words, legal coordinates of the view range from <tt>[0,0]</tt> to <tt>[view.rows()-1==height-1,view.columns()-1==width-1]</tt>.
As usual, any attempt to access a cell at a coordinate <tt>column&lt;0 || column&gt;=view.columns() || row&lt;0 || row&gt;=view.rows()</tt> will throw an <tt>IndexOutOfBoundsException</tt>.

@param     row   The index of the row-coordinate.
@param     column   The index of the column-coordinate.
@param     height   The height of the box.
@param     width   The width of the box.
@throws	IndexOutOfBoundsException if <tt>column<0 || width<0 || column+width>columns() || row<0 || height<0 || row+height>rows()</tt>
@return the new view.
		
*/
public ObjectMatrix2D viewPart(int row, int column, int height, int width) {
	return (ObjectMatrix2D) (view().vPart(row,column,height,width));
}
/**
Constructs and returns a new <i>slice view</i> representing the columns of the given row.
The returned view is backed by this matrix, so changes in the returned view are reflected in this matrix, and vice-versa.
To obtain a slice view on subranges, construct a sub-ranging view (<tt>viewPart(...)</tt>), then apply this method to the sub-range view.
<p> 
<b>Example:</b> 
<table border="0">
  <tr nowrap> 
	<td valign="top">2 x 3 matrix: <br>
	  1, 2, 3<br>
	  4, 5, 6 </td>
	<td>viewRow(0) ==></td>
	<td valign="top">Matrix1D of size 3:<br>
	  1, 2, 3</td>
   </tr>
</table>

@param row the row to fix.
@return a new slice view.
@throws IndexOutOfBoundsException if <tt>row < 0 || row >= rows()</tt>.
@see #viewColumn(int)
*/
public ObjectMatrix1D viewRow(int row) {
	checkRow(row);
	int viewSize = this.columns;
	int viewZero = index(row,0);
	int viewStride = this.columnStride;
	return like1D(viewSize,viewZero,viewStride);
}
/**
Constructs and returns a new <i>flip view</i> along the row axis.
What used to be row <tt>0</tt> is now row <tt>rows()-1</tt>, ..., what used to be row <tt>rows()-1</tt> is now row <tt>0</tt>.
The returned view is backed by this matrix, so changes in the returned view are reflected in this matrix, and vice-versa.
<p> 
<b>Example:</b> 
<table border="0">
  <tr nowrap> 
	<td valign="top">2 x 3 matrix: <br>
	  1, 2, 3<br>
	  4, 5, 6 </td>
	<td>rowFlip ==></td>
	<td valign="top">2 x 3 matrix:<br>
	  4, 5, 6 <br>
	  1, 2, 3</td>
	<td>rowFlip ==></td>
	<td valign="top">2 x 3 matrix: <br>
	  1, 2, 3<br>
	  4, 5, 6 </td>
  </tr>
</table>

@return a new flip view.
@see #viewColumnFlip()
*/
public ObjectMatrix2D viewRowFlip() {
	return (ObjectMatrix2D) (view().vRowFlip());
}
/**
Constructs and returns a new <i>selection view</i> that is a matrix holding the indicated cells.
There holds <tt>view.rows() == rowIndexes.length, view.columns() == columnIndexes.length</tt> and <tt>view.get(i,j) == this.get(rowIndexes[i],columnIndexes[j])</tt>.
Indexes can occur multiple times and can be in arbitrary order.
<p>
<b>Example:</b>
<pre>
this = 2 x 3 matrix:
1, 2, 3
4, 5, 6
rowIndexes     = (0,1)
columnIndexes  = (1,0,1,0)
-->
view = 2 x 4 matrix:
2, 1, 2, 1
5, 4, 5, 4
</pre>
Note that modifying the index arguments after this call has returned has no effect on the view.
The returned view is backed by this matrix, so changes in the returned view are reflected in this matrix, and vice-versa. 
<p>
To indicate "all" rows or "all columns", simply set the respective parameter
@param  rowIndexes   The rows of the cells that shall be visible in the new view. To indicate that <i>all</i> rows shall be visible, simply set this parameter to <tt>null</tt>.
@param  columnIndexes   The columns of the cells that shall be visible in the new view. To indicate that <i>all</i> columns shall be visible, simply set this parameter to <tt>null</tt>.
@return the new view.
@throws IndexOutOfBoundsException if <tt>!(0 <= rowIndexes[i] < rows())</tt> for any <tt>i=0..rowIndexes.length()-1</tt>.
@throws IndexOutOfBoundsException if <tt>!(0 <= columnIndexes[i] < columns())</tt> for any <tt>i=0..columnIndexes.length()-1</tt>.
*/
public ObjectMatrix2D viewSelection(int[] rowIndexes, int[] columnIndexes) {
	// check for "all"
	if (rowIndexes==null) {
		rowIndexes = new int[rows];
		for (int i=rows; --i >= 0; ) rowIndexes[i] = i;
	}
	if (columnIndexes==null) {
		columnIndexes = new int[columns];
		for (int i=columns; --i >= 0; ) columnIndexes[i] = i;
	}
	
	checkRowIndexes(rowIndexes);
	checkColumnIndexes(columnIndexes);
	int[] rowOffsets = new int[rowIndexes.length];
	int[] columnOffsets = new int[columnIndexes.length];
	for (int i=rowIndexes.length; --i >= 0; ) {
		rowOffsets[i] = _rowOffset(_rowRank(rowIndexes[i]));
	}
	for (int i=columnIndexes.length; --i >= 0; ) {
		columnOffsets[i] = _columnOffset(_columnRank(columnIndexes[i]));
	}
	return viewSelectionLike(rowOffsets,columnOffsets);
}
/**
Constructs and returns a new <i>selection view</i> that is a matrix holding all <b>rows</b> matching the given condition.
Applies the condition to each row and takes only those row where <tt>condition.apply(viewRow(i))</tt> yields <tt>true</tt>.
To match columns, use a dice view.
<p>
<b>Example:</b>
<br>
<pre>
// extract and view all rows which have a value < threshold in the first column (representing "age")
final Object threshold = 16;
matrix.viewSelection( 
&nbsp;&nbsp;&nbsp;new ObjectMatrix1DProcedure() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;public final boolean apply(ObjectMatrix1D m) { return m.get(0) < threshold; }
&nbsp;&nbsp;&nbsp;}
);

// extract and view all rows with RMS < threshold
// The RMS (Root-Mean-Square) is a measure of the average "size" of the elements of a data sequence.
matrix = 0 1 2 3
final Object threshold = 0.5;
matrix.viewSelection( 
&nbsp;&nbsp;&nbsp;new ObjectMatrix1DProcedure() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;public final boolean apply(ObjectMatrix1D m) { return Math.sqrt(m.aggregate(F.plus,F.square) / m.size()) < threshold; }
&nbsp;&nbsp;&nbsp;}
);
</pre>
For further examples, see the <a href="package-summary.html#FunctionObjects">package doc</a>.
The returned view is backed by this matrix, so changes in the returned view are reflected in this matrix, and vice-versa. 

@param  condition The condition to be matched.
@return the new view.
*/
public ObjectMatrix2D viewSelection(ObjectMatrix1DProcedure condition) {
	IntArrayList matches = new IntArrayList();
	for (int i=0; i < rows; i++) {
		if (condition.apply(viewRow(i))) matches.add(i);
	}
	
	matches.trimToSize();
	return viewSelection(matches.elements(), null); // take all columns
}
/**
 * Construct and returns a new selection view.
 *
 * @param rowOffsets the offsets of the visible elements.
 * @param columnOffsets the offsets of the visible elements.
 * @return  a new view.
 */
protected abstract ObjectMatrix2D viewSelectionLike(int[] rowOffsets, int[] columnOffsets);
/**
Sorts the matrix rows into ascending order, according to the <i>natural ordering</i> of the matrix values in the given column.
This sort is guaranteed to be <i>stable</i>.
For further information, see {@link cern.colt.matrix.objectalgo.Sorting#sort(ObjectMatrix2D,int)}.
For more advanced sorting functionality, see {@link cern.colt.matrix.objectalgo.Sorting}.
@return a new sorted vector (matrix) view.
@throws IndexOutOfBoundsException if <tt>column < 0 || column >= columns()</tt>.
*/
public ObjectMatrix2D viewSorted(int column) {
	return cern.colt.matrix.objectalgo.Sorting.mergeSort.sort(this,column); 
}
/**
Constructs and returns a new <i>stride view</i> which is a sub matrix consisting of every i-th cell.
More specifically, the view has <tt>this.rows()/rowStride</tt> rows and <tt>this.columns()/columnStride</tt> columns holding cells <tt>this.get(i*rowStride,j*columnStride)</tt> for all <tt>i = 0..rows()/rowStride - 1, j = 0..columns()/columnStride - 1</tt>.
The returned view is backed by this matrix, so changes in the returned view are reflected in this matrix, and vice-versa.

@param rowStride the row step factor.
@param columnStride the column step factor.
@return a new view.
@throws	IndexOutOfBoundsException if <tt>rowStride<=0 || columnStride<=0</tt>.
*/
public ObjectMatrix2D viewStrides(int rowStride, int columnStride) {
	return (ObjectMatrix2D) (view().vStrides(rowStride, columnStride));
}
/**
 * Applies a procedure to each cell's value.
 * Iterates downwards from <tt>[rows()-1,columns()-1]</tt> to <tt>[0,0]</tt>,
 * as demonstrated by this snippet:
 * <pre>
 * for (int row=rows; --row >=0;) {
 *    for (int column=columns; --column >= 0;) {
 *        if (!procedure.apply(getQuick(row,column))) return false;
 *    }
 * }
 * return true;
 * </pre>
 * Note that an implementation may use more efficient techniques, but must not use any other order.
 *
 * @param procedure a procedure object taking as argument the current cell's value. Stops iteration if the procedure returns <tt>false</tt>, otherwise continues. 
 * @return <tt>false</tt> if the procedure stopped before all elements where iterated over, <tt>true</tt> otherwise. 
 */
private boolean xforEach(final cern.colt.function.ObjectProcedure procedure) {
	for (int row=rows; --row >= 0;) {
		for (int column=columns; --column >= 0;) {
			if (!procedure.apply(getQuick(row,column))) return false;
		}
	}
	return true;
}
}
