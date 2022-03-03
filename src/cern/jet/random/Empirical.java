/*
Copyright © 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package cern.jet.random;

import cern.jet.random.engine.RandomEngine;
/**
 * Empirical distribution.
 * <p>
 * The probability distribution function (pdf) must be provided by the user as an array of positive real numbers. 
 * The pdf does not need to be provided in the form of relative probabilities, absolute probabilities are also accepted.
 * <p>
 * If <tt>interpolationType == LINEAR_INTERPOLATION</tt> a linear interpolation within the bin is computed, resulting in a constant density within each bin.
 * <dt>
 * If <tt>interpolationType == NO_INTERPOLATION</tt> no interpolation is performed and the result is a discrete distribution.  
 * <p>
 * Instance methods operate on a user supplied uniform random number generator; they are unsynchronized.
 * <dt>
 * Static methods operate on a default uniform random number generator; they are synchronized.
 * <p>
 * <b>Implementation:</b>
 * A uniform random number is generated using a user supplied generator.
 * The uniform number is then transformed to the user's distribution using the cumulative probability distribution constructed from the pdf.
 * The cumulative distribution is inverted using a binary search for the nearest bin boundary. 
 * <p>
 * This is a port of <A HREF="http://wwwinfo.cern.ch/asd/lhc++/clhep/manual/RefGuide/Random/RandGeneral.html">RandGeneral</A> used in <A HREF="http://wwwinfo.cern.ch/asd/lhc++/clhep">CLHEP 1.4.0</A> (C++).
 *
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class Empirical extends AbstractContinousDistribution {
	protected double[] cdf; // cumulative distribution function
	protected int interpolationType;
	
	public static final int LINEAR_INTERPOLATION = 0;
	public static final int NO_INTERPOLATION = 1;
/**
 * Constructs an Empirical distribution.
 * The probability distribution function (pdf) is an array of positive real numbers. 
 * It need not be provided in the form of relative probabilities, absolute probabilities are also accepted.
 * The <tt>pdf</tt> must satisfy both of the following conditions
 * <ul>
 * <li><tt>0.0 &lt;= pdf[i] : 0&lt;=i&lt;=pdf.length-1</tt>
 * <li><tt>0.0 &lt; Sum(pdf[i]) : 0&lt;=i&lt;=pdf.length-1</tt>
 * </ul>
 * @param pdf the probability distribution function.
 * @param interpolationType can be either <tt>Empirical.NO_INTERPOLATION</tt> or <tt>Empirical.LINEAR_INTERPOLATION</tt>.
 * @param randomGenerator a uniform random number generator.
 * @throws IllegalArgumentException if at least one of the three conditions above is violated.
 */
public Empirical(double[] pdf, int interpolationType, RandomEngine randomGenerator) {
	setRandomGenerator(randomGenerator);
	setState(pdf, interpolationType);	
}
/**
 * Returns the cumulative distribution function.
 */
public double cdf(int k) {
	if (k < 0) return 0.0;
	if (k >= cdf.length-1) return 1.0;
	return cdf[k];
}
/**
 * Returns a deep copy of the receiver; the copy will produce identical sequences.
 * After this call has returned, the copy and the receiver have equal but separate state.
 *
 * @return a copy of the receiver.
 */
public Object clone() {
	Empirical copy = (Empirical) super.clone();
	if (this.cdf != null) copy.cdf = (double[]) this.cdf.clone();
	return copy;
}
/**
 * Returns a random number from the distribution.
 */
public double nextDouble() {
	double rand = randomGenerator.raw();
	if (this.cdf==null) return rand; // Non-existing pdf

	// binary search in cumulative distribution function:
	int nBins = cdf.length-1;
	int nbelow = 0;     // largest k such that I[k] is known to be <= rand
	int nabove = nBins; // largest k such that I[k] is known to be >  rand
 
	while (nabove > nbelow+1) {
		int middle = (nabove + nbelow + 1) >> 1; // div 2
		if (rand >= cdf[middle]) nbelow = middle;
		else nabove = middle;
	}
	// after this binary search, nabove is always nbelow+1 and they straddle rand:

	if (this.interpolationType == NO_INTERPOLATION) {
		return ((double)nbelow) / nBins;
	} 
	else if (this.interpolationType == LINEAR_INTERPOLATION) {
		double binMeasure = cdf[nabove] - cdf[nbelow];
		// binMeasure is always aProbFunc[nbelow], 
		// but we don't have aProbFunc any more so we subtract.

		if ( binMeasure == 0.0 ) { 
			// rand lies right in a bin of measure 0.  Simply return the center
			// of the range of that bin.  (Any value between k/N and (k+1)/N is
			// equally good, in this rare case.)
			return (nbelow + 0.5) / nBins;
		}

		double binFraction = (rand - cdf[nbelow]) / binMeasure;
		return (nbelow + binFraction) / nBins;
	}
	else throw new InternalError(); // illegal interpolation type
}
/**
 * Returns the probability distribution function.
 */
public double pdf(double x) {
	throw new RuntimeException("not implemented");
	//if (x < 0 || x > cdf.length-2) return 0.0;
	//int k = (int) x;
	//return cdf[k-1] - cdf[k];
}
/**
 * Returns the probability distribution function.
 */
public double pdf(int k) {
	if (k < 0 || k >= cdf.length-1) return 0.0;
	return cdf[k-1] - cdf[k];
}
/**
 * Sets the distribution parameters.
 * The <tt>pdf</tt> must satisfy both of the following conditions
 * <ul>
 * <li><tt>0.0 &lt;= pdf[i] : 0 &lt; =i &lt;= pdf.length-1</tt>
 * <li><tt>0.0 &lt; Sum(pdf[i]) : 0 &lt;=i &lt;= pdf.length-1</tt>
 * </ul>
 * @param pdf probability distribution function.
 * @param interpolationType can be either <tt>Empirical.NO_INTERPOLATION</tt> or <tt>Empirical.LINEAR_INTERPOLATION</tt>.
 * @throws IllegalArgumentException if at least one of the three conditions above is violated.
 */
public void setState(double[] pdf, int interpolationType) {
	if (interpolationType != LINEAR_INTERPOLATION &&
		interpolationType != NO_INTERPOLATION) {
		throw new IllegalArgumentException("Illegal Interpolation Type");
	}
	this.interpolationType = interpolationType;
	
	if (pdf==null || pdf.length==0) {
		this.cdf = null;
		//throw new IllegalArgumentException("Non-existing pdf");
		return;
	}

	// compute cumulative distribution function (cdf) from probability distribution function (pdf)
	int nBins = pdf.length;
	this.cdf = new double[nBins+1];
	
	cdf[0] = 0;
	for (int ptn = 0; ptn<nBins; ++ptn ) {
		double prob = pdf[ptn];
		if (prob < 0.0) throw new IllegalArgumentException("Negative probability");
		cdf[ptn+1] = cdf[ptn] + prob;
	}
	if (cdf[nBins] <=0.0) throw new IllegalArgumentException("At leat one probability must be > 0.0");
	for (int ptn = 0; ptn < nBins+1; ++ptn ) {
		cdf[ptn] /= cdf[nBins];
	}
	// cdf is now cached...
}
/**
 * Returns a String representation of the receiver.
 */
public String toString() {
	String interpolation = null;
	if (interpolationType==NO_INTERPOLATION) interpolation = "NO_INTERPOLATION";
	if (interpolationType==LINEAR_INTERPOLATION) interpolation = "LINEAR_INTERPOLATION";
	return this.getClass().getName()+"("+ ((cdf!=null) ? cdf.length : 0) +","+interpolation+")";
}
/**
 * Not yet commented.
 * @return int
 */
private int xnBins() {
	return cdf.length-1;
}
}
