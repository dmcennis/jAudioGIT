/*
Copyright ï¿½ 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package cern.jet.stat.quantile;

/**
 * Read-only equi-depth histogram for selectivity estimation.
 * Assume you have collected statistics over a data set, among them a one-dimensional equi-depth histogram (quantiles).
 * Then an applications or DBMS might want to estimate the <i>selectivity</i> of some range query <tt>[from,to]</tt>, i.e. the percentage of data set elements contained in the query range.
 * This class does not collect equi-depth histograms but only space efficiently stores already produced histograms and provides operations for selectivity estimation.
 * Uses linear interpolation.
 * <p>
 * This class stores a list <tt>l</tt> of <tt>float</tt> values for which holds:
 * <li>Let <tt>v</tt> be a list of values (sorted ascending) an equi-depth histogram has been computed over.</li>
 * <li>Let <tt>s=l.length</tt>.</li>
 * <li>Let <tt>p=(0, 1/s-1), 2/s-1,..., s-1/s-1=1.0)</tt> be a list of the <tt>s</tt> percentages.</li>
 * <li>Then for each <tt>i=0..s-1: l[i] = e : v.contains(e) && v[0],..., v[p[i]*v.length] &lt;= e</tt>.</li>
 * <li>(In particular: <tt>l[0]=min(v)=v[0]</tt> and <tt>l[s-1]=max(v)=v[s-1]</tt>.)</li>
 * 
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class EquiDepthHistogram extends cern.colt.PersistentObject {
	protected float[] binBoundaries;
/**
 * Constructs an equi-depth histogram with the given quantile elements.
 * Quantile elements must be sorted ascending and have the form specified in the class documentation.
 */
public EquiDepthHistogram(float[] quantileElements) {
	this.binBoundaries = quantileElements;
}
/**
 * Returns the bin index of the given element.
 * In other words, returns a handle to the range the element falls into.
 *
 * @param element the element to search for.
 * @throws java.lang.IllegalArgumentException if the element is not contained in any bin.
 */
public int binOfElement(float element) {
	int index = java.util.Arrays.binarySearch(binBoundaries,element);
	if (index>=0) {
		// element found.
		if (index==binBoundaries.length-1) index--; // last bin is a closed interval.
	}
	else {
		// element not found.
		index -= -1; // index = -index-1; now index is the insertion point.
		if (index==0 || index==binBoundaries.length) {
			throw new IllegalArgumentException("Element="+element+" not contained in any bin.");
		}
		index--;
	}
	return index;
}
/**
 * Returns the number of bins. In other words, returns the number of subdomains partitioning the entire value domain.
 */
public int bins() {
	return binBoundaries.length-1;
}
/**
 * Returns the end of the range associated with the given bin.
 * @throws ArrayIndexOutOfBoundsException if <tt>binIndex &lt; 0 || binIndex &gt;= bins()</tt>.
 */
public float endOfBin(int binIndex) {
	return binBoundaries[binIndex+1];
}
/**
 * Returns the percentage of elements in the range (from,to]. Does linear interpolation.
 * @param from the start point (exclusive).
 * @param to the end point (inclusive).
 * @returns a number in the closed interval <tt>[0.0,1.0]</tt>.
 */
public double percentFromTo(float from, float to) {
	return phi(to)-phi(from);
}
/**
 * Returns how many percent of the elements contained in the receiver are <tt>&lt;= element</tt>.
 * Does linear interpolation.
 *
 * @param the element to search for.
 * @returns a number in the closed interval <tt>[0.0,1.0]</tt>.
 */
public double phi(float element) {
	int size = binBoundaries.length;
	if (element<=binBoundaries[0]) return 0.0;
	if (element>=binBoundaries[size-1]) return 1.0;

	double binWidth = 1.0/(size-1);
	int index = java.util.Arrays.binarySearch(binBoundaries, element);
	//int index = new FloatArrayList(binBoundaries).binarySearch(element);
	if (index>=0) { // found
		return binWidth*index;
	}

	// do linear interpolation
	int insertionPoint = -index-1;
	double from = binBoundaries[insertionPoint-1];
	double to = binBoundaries[insertionPoint]-from;
	double p = (element - from) / to;
	return binWidth * (p+(insertionPoint-1));
}
/**
 * @deprecated
 * Deprecated.
 * Returns the number of bin boundaries.
 */
public int size() {
	return binBoundaries.length;
}
/**
 * Returns the start of the range associated with the given bin.
 * @throws ArrayIndexOutOfBoundsException if <tt>binIndex &lt; 0 || binIndex &gt;= bins()</tt>.
 */
public float startOfBin(int binIndex) {
	return binBoundaries[binIndex];
}
/**
 * Not yet commented.
 */
public static void test(float element) {
	float[] quantileElements =
		{50.0f, 100.0f, 200.0f, 300.0f, 1400.0f, 1500.0f,  1600.0f, 1700.0f, 1800.0f, 1900.0f, 2000.0f};
	EquiDepthHistogram histo = new EquiDepthHistogram(quantileElements);
	System.out.println("elem="+element+", phi="+histo.phi(element));
}
}
