/*
Copyright ï¿½ 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package cern.colt.matrix.linalg;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
/** 
For a symmetric, positive definite matrix <tt>A</tt>, the Cholesky decomposition
is a lower triangular matrix <tt>L</tt> so that <tt>A = L*L'</tt>;
If the matrix is not symmetric or positive definite, the constructor
returns a partial decomposition and sets an internal flag that may
be queried by the <tt>isSymmetricPositiveDefinite()</tt> method.
*/
public class CholeskyDecomposition implements java.io.Serializable {
	static final long serialVersionUID = 1020;
	/** Array for internal storage of decomposition.
	@serial internal array storage.
	*/
	//private double[][] L;
	private DoubleMatrix2D L;
	
	/** Row and column dimension (square matrix).
	@serial matrix dimension.
	*/
	private int n;
	
	/** Symmetric and positive definite flag.
	@serial is symmetric and positive definite flag.
	*/
	private boolean isSymmetricPositiveDefinite;
/** 
Constructs and returns a new Cholesky decomposition object for a symmetric and positive definite matrix; 
The decomposed matrices can be retrieved via instance methods of the returned decomposition object.

@param  A   Square, symmetric matrix.
@return     Structure to access <tt>L</tt> and <tt>isSymmetricPositiveDefinite</tt> flag.
@throws IllegalArgumentException if <tt>A</tt> is not square.
*/
public CholeskyDecomposition(DoubleMatrix2D A) {
	Property.DEFAULT.checkSquare(A); 
	// Initialize.
	//double[][] A = Arg.getArray();
	
	n = A.rows();
	//L = new double[n][n];
	L = A.like(n,n);
	isSymmetricPositiveDefinite = (A.columns() == n);
	
	//precompute and cache some views to avoid regenerating them time and again
	DoubleMatrix1D[] Lrows = new DoubleMatrix1D[n];
	for (int j = 0; j < n; j++) Lrows[j] = L.viewRow(j);
	
	// Main loop.
	for (int j = 0; j < n; j++) {
		//double[] Lrowj = L[j];
		//DoubleMatrix1D Lrowj = L.viewRow(j);
		double d = 0.0;
		for (int k = 0; k < j; k++) {
			//double[] Lrowk = L[k];
			double s = Lrows[k].zDotProduct(Lrows[j],0,k);
			/*
			DoubleMatrix1D Lrowk = L.viewRow(k);
			double s = 0.0;
			for (int i = 0; i < k; i++) {
			   s += Lrowk.getQuick(i)*Lrowj.getQuick(i);
			}
			*/
			s = (A.getQuick(j,k) - s) / L.getQuick(k,k);
			Lrows[j].setQuick(k,s);
			d = d + s*s;
			isSymmetricPositiveDefinite = isSymmetricPositiveDefinite && (A.getQuick(k,j) == A.getQuick(j,k)); 
		}
		d = A.getQuick(j,j) - d;
		isSymmetricPositiveDefinite = isSymmetricPositiveDefinite && (d > 0.0);
		L.setQuick(j,j, Math.sqrt(Math.max(d,0.0)));
		
		for (int k = j+1; k < n; k++) {
			L.setQuick(j,k, 0.0);
		}
	}
}
/** 
Returns the triangular factor, <tt>L</tt>.
@return     <tt>L</tt>
*/
public DoubleMatrix2D getL() {
	return L;
}
/** 
Returns whether the matrix <tt>A</tt> is symmetric and positive definite.
@return     true if <tt>A</tt> is symmetric and positive definite; false otherwise
*/
public boolean isSymmetricPositiveDefinite() {
	return isSymmetricPositiveDefinite;
}
/** 
Solves <tt>A*X = B</tt>; returns <tt>X</tt>.
@param  B   A Matrix with as many rows as <tt>A</tt> and any number of columns.
@return     <tt>X</tt> so that <tt>L*L'*X = B</tt>.
@exception  IllegalArgumentException  if <tt>B.rows() != A.rows()</tt>.
@exception  IllegalArgumentException  if <tt>!isSymmetricPositiveDefinite()</tt>.
*/
public DoubleMatrix2D solve(DoubleMatrix2D B) {
	// Copy right hand side.
	DoubleMatrix2D X = B.copy();
	int nx = B.columns();

	// fix by MG Ferreira <mgf@webmail.co.za>
	// old code is in method xxxSolveBuggy()
	for (int c = 0; c < nx; c++ ) {
		// Solve L*Y = B;
		for (int i = 0; i < n; i++)	{
			double sum = B.getQuick( i, c );
			for (int k = i-1; k >= 0; k--) {
				sum -= L.getQuick(i,k) * X.getQuick( k, c );
			}
			X.setQuick( i, c, sum / L.getQuick( i, i ) );
		}

		// Solve L'*X = Y;
		for (int i = n-1; i >= 0; i--) {
			double sum = X.getQuick( i, c );
			for (int k = i+1; k < n; k++) {
				sum -= L.getQuick(k,i) * X.getQuick( k, c );
			}
			X.setQuick( i, c, sum / L.getQuick( i, i ) );
		}
	}

	return X;
}
/** 
Solves <tt>A*X = B</tt>; returns <tt>X</tt>.
@param  B   A Matrix with as many rows as <tt>A</tt> and any number of columns.
@return     <tt>X</tt> so that <tt>L*L'*X = B</tt>.
@exception  IllegalArgumentException  if <tt>B.rows() != A.rows()</tt>.
@exception  IllegalArgumentException  if <tt>!isSymmetricPositiveDefinite()</tt>.
*/
private DoubleMatrix2D XXXsolveBuggy(DoubleMatrix2D B) {
	cern.jet.math.Functions F = cern.jet.math.Functions.functions;
	if (B.rows() != n) {
		throw new IllegalArgumentException("Matrix row dimensions must agree.");
	}
	if (!isSymmetricPositiveDefinite) {
		throw new IllegalArgumentException("Matrix is not symmetric positive definite.");
	}
	
	// Copy right hand side.
	DoubleMatrix2D X = B.copy();
	int nx = B.columns();
	
	// precompute and cache some views to avoid regenerating them time and again
	DoubleMatrix1D[] Xrows = new DoubleMatrix1D[n];
	for (int k = 0; k < n; k++) Xrows[k] = X.viewRow(k);
	
	// Solve L*Y = B;
	for (int k = 0; k < n; k++) {
		for (int i = k+1; i < n; i++) {
			// X[i,j] -= X[k,j]*L[i,k]
			Xrows[i].assign(Xrows[k], F.minusMult(L.getQuick(i,k)));
		}
		Xrows[k].assign(F.div(L.getQuick(k,k)));
	}
	
	// Solve L'*X = Y;
	for (int k = n-1; k >= 0; k--) {
		Xrows[k].assign(F.div(L.getQuick(k,k)));
		for (int i = 0; i < k; i++) {
			// X[i,j] -= X[k,j]*L[k,i]
			Xrows[i].assign(Xrows[k], F.minusMult(L.getQuick(k,i)));
		}
	}
	return X;
}

/**
Returns a String with (propertyName, propertyValue) pairs.
Useful for debugging or to quickly get the rough picture.
For example,
<pre>
rank          : 3
trace         : 0
</pre>
*/
public String toString() {
	StringBuffer buf = new StringBuffer();
	String unknown = "Illegal operation or error: ";

	buf.append("--------------------------------------------------------------------------\n");
	buf.append("CholeskyDecomposition(A) --> isSymmetricPositiveDefinite(A), L, inverse(A)\n");
	buf.append("--------------------------------------------------------------------------\n");

	buf.append("isSymmetricPositiveDefinite = ");
	try { buf.append(String.valueOf(this.isSymmetricPositiveDefinite()));} 
	catch (IllegalArgumentException exc) { buf.append(unknown+exc.getMessage()); }
		
	buf.append("\n\nL = ");
	try { buf.append(String.valueOf(this.getL()));} 
	catch (IllegalArgumentException exc) { buf.append(unknown+exc.getMessage()); }
	
	buf.append("\n\ninverse(A) = ");
	try { buf.append(String.valueOf(this.solve(cern.colt.matrix.DoubleFactory2D.dense.identity(L.rows()))));} 
	catch (IllegalArgumentException exc) { buf.append(unknown+exc.getMessage()); }
	
	return buf.toString();
}
}
