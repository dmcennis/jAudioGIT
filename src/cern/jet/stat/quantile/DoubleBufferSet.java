/*
Copyright © 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package cern.jet.stat.quantile;

/**
 * A set of buffers holding <tt>double</tt> elements; internally used for computing approximate quantiles.
 */
class DoubleBufferSet extends BufferSet {
	protected DoubleBuffer[] buffers;
	private boolean nextTriggerCalculationState; //tmp var only
/**
 * Constructs a buffer set with b buffers, each having k elements
 * @param b the number of buffers
 * @param k the number of elements per buffer
 */
public DoubleBufferSet(int b, int k) {
	this.buffers = new DoubleBuffer[b];
	this.clear(k);
}
/**
 * Returns an empty buffer if at least one exists.
 * Preferably returns a buffer which has already been used,
 * i.e. a buffer which has already been allocated.
 */
public DoubleBuffer _getFirstEmptyBuffer() {
	DoubleBuffer emptyBuffer = null;
	for (int i=buffers.length; --i >= 0;) {
		if (buffers[i].isEmpty()) {
			if (buffers[i].isAllocated()) return buffers[i];
			emptyBuffer = buffers[i];
		}
	}
		
	return emptyBuffer;
}
/**
 * Returns all full or partial buffers.
 */
public DoubleBuffer[] _getFullOrPartialBuffers() {
	//count buffers
	int count = 0;
	for (int i=buffers.length; --i >= 0;) {
		if (! buffers[i].isEmpty()) count++;
	}

	//collect buffers
	DoubleBuffer[] collectedBuffers = new DoubleBuffer[count];
	int j=0;
	for (int i=buffers.length; --i >= 0;) {
		if (! buffers[i].isEmpty()) {
			collectedBuffers[j++]=buffers[i];
		}
	}

	return collectedBuffers;
}
/**
 * Determines all full buffers having the specified level.
 * @return all full buffers having the specified level
 */
public DoubleBuffer[] _getFullOrPartialBuffersWithLevel(int level) {
	//count buffers
	int count = 0;
	for (int i=buffers.length; --i >= 0;) {
		if ((! buffers[i].isEmpty()) && buffers[i].level()==level) count++;
	}

	//collect buffers
	DoubleBuffer[] collectedBuffers = new DoubleBuffer[count];
	int j=0;
	for (int i=buffers.length; --i >= 0;) {
		if ((! buffers[i].isEmpty()) && buffers[i].level()==level) {
			collectedBuffers[j++]=buffers[i];
		}
	}

	return collectedBuffers;
}
/**
 * @return The minimum level of all buffers which are full.
 */
public int _getMinLevelOfFullOrPartialBuffers() {
	int b=this.b();
	int minLevel=Integer.MAX_VALUE;
	DoubleBuffer buffer;
	
	for (int i=0; i<b; i++) {
		buffer = this.buffers[i];
		if ((! buffer.isEmpty()) && (buffer.level() < minLevel)) {
			minLevel = buffer.level();
		}
	}
	return minLevel;
}
/**
 * Returns the number of empty buffers.
 */
public int _getNumberOfEmptyBuffers() {
	int count = 0;
	for (int i=buffers.length; --i >= 0;) {
		if (buffers[i].isEmpty()) count++;
	}

	return count;
}
/**
 * Returns all empty buffers.
 */
public DoubleBuffer _getPartialBuffer() {
	for (int i=buffers.length; --i >= 0;) {
		if (buffers[i].isPartial()) return buffers[i];
	}
	return null;
}
/**
 * @return the number of buffers
 */
public int b() {
	return buffers.length;
}
/**
 * Removes all elements from the receiver.  The receiver will
 * be empty after this call returns, and its memory requirements will be close to zero.
 */
public void clear() {
	clear(k());
}
/**
 * Removes all elements from the receiver.  The receiver will
 * be empty after this call returns, and its memory requirements will be close to zero.
 */
protected void clear(int k) {
	for (int i=b(); --i >=0; ) this.buffers[i]=new DoubleBuffer(k);
	this.nextTriggerCalculationState = true;
}
/**
 * Returns a deep copy of the receiver.
 *
 * @return a deep copy of the receiver.
 */
public Object clone() {
	DoubleBufferSet copy = (DoubleBufferSet) super.clone();

	copy.buffers = (DoubleBuffer[]) copy.buffers.clone();
	for (int i=buffers.length; --i >= 0; ) {
		copy.buffers[i] = (DoubleBuffer) copy.buffers[i].clone();
	}
	return copy;
}
/**
 * Collapses the specified full buffers (must not include partial buffer).
 * @return a full buffer containing the collapsed values. The buffer has accumulated weight.
 * @param buffers the buffers to be collapsed (all of them must be full or partially full)
 */
public DoubleBuffer collapse(DoubleBuffer[] buffers) {
	//determine W
	int W=0;								//sum of all weights
	for (int i=0; i<buffers.length; i++) { W += buffers[i].weight(); }

	//determine outputTriggerPositions
	int k=this.k();
	long[] triggerPositions = new long[k]; 
	for (int j=0; j<k; j++) { triggerPositions[j]=this.nextTriggerPosition(j,W); }

	//do the main work: determine values at given positions in sorted sequence
	double[] outputValues = this.getValuesAtPositions(buffers, triggerPositions);

	//mark all full buffers as empty, except the first, which will contain the output
	for (int b=1; b<buffers.length; b++) buffers[b].clear(); 

	DoubleBuffer outputBuffer = buffers[0];
	outputBuffer.values.elements(outputValues);
	outputBuffer.weight(W);
	
	return outputBuffer;
}
/**
 * Returns whether the specified element is contained in the receiver.
 */
public boolean contains(double element) {
	for (int i=buffers.length; --i >= 0;) {
		if ((! buffers[i].isEmpty()) && buffers[i].contains(element)) {
			return true;
		}
	}

	return false;
}
/**
 * Applies a procedure to each element of the receiver, if any.
 * Iterates over the receiver in no particular order.
 * @param procedure    the procedure to be applied. Stops iteration if the procedure returns <tt>false</tt>, otherwise continues. 
 */
public boolean forEach(cern.colt.function.DoubleProcedure procedure) {
	for (int i=buffers.length; --i >=0; ) {
		for (int w=buffers[i].weight(); --w >=0; ) {
			if (! (buffers[i].values.forEach(procedure))) return false;
		}
	}
	return true;
}
/**
 * Determines all values of the specified buffers positioned at the specified triggerPositions within the sorted sequence and fills them into outputValues.
 * @param buffers the buffers to be searched (all must be full or partial) 
 * @param triggerPositions the positions of elements within the sorted sequence to be retrieved
 * @return outputValues a list filled with the values at triggerPositions
 */
protected double[] getValuesAtPositions(DoubleBuffer[] buffers, long[] triggerPositions) {
	//if (buffers.length==0) 
	//{
	//	throw new IllegalArgumentException("Oops! buffer.length==0.");
	//}

	//System.out.println("triggers="+cern.it.util.Arrays.toString(positions));

	//new DoubleArrayList(outputValues).fillFromToWith(0, outputValues.length-1, 0.0f);
	//delte the above line, it is only for testing
	
	//cern.it.util.Log.println("\nEntering getValuesAtPositions...");
	//cern.it.util.Log.println("hitPositions="+cern.it.util.Arrays.toString(positions));

	// sort buffers.
	for (int i=buffers.length; --i >= 0; ) {
		buffers[i].sort();
	}

	// collect some infos into fast cache; for tuning purposes only.
	int[] bufferSizes = new int[buffers.length];		
	double[][] bufferValues = new double[buffers.length][];
	int totalBuffersSize = 0;
	for (int i=buffers.length; --i >= 0; ) {
		bufferSizes[i] = buffers[i].size();
		bufferValues[i] = buffers[i].values.elements();
		totalBuffersSize += bufferSizes[i];
		//cern.it.util.Log.println("buffer["+i+"]="+buffers[i].values);
	}
	
	// prepare merge of equi-distant elements within buffers into output values
	
	// first collect some infos into fast cache; for tuning purposes only.
	final int buffersSize = buffers.length;		
	final int triggerPositionsLength = triggerPositions.length; 

	// now prepare the important things.
	int j=0; 								//current position in collapsed values
	int[] cursors = new int[buffers.length];	//current position in each buffer; init with zeroes
	long counter = 0; 						//current position in sorted sequence
	long nextHit = triggerPositions[j];		//next position in sorted sequence to trigger output population
	double[] outputValues = new double[triggerPositionsLength];

	if (totalBuffersSize==0) {
		// nothing to output, because no elements have been filled (we are empty).
		// return meaningless values
		for (int i=0; i<triggerPositions.length; i++) {
			outputValues[i] = Double.NaN;
		}
		return outputValues;
	}
	
	// fill all output values with equi-distant elements.
	while (j<triggerPositionsLength) { 
		//System.out.println("\nj="+j);
		//System.out.println("counter="+counter);
		//System.out.println("nextHit="+nextHit);

		// determine buffer with smallest value at cursor position.
		double minValue = Double.POSITIVE_INFINITY;
		int minBufferIndex = -1;

		for (int b=buffersSize; --b >= 0; ) {
			//DoubleBuffer buffer = buffers[b];
			//if (cursors[b] < buffer.length) { 
			if (cursors[b] < bufferSizes[b]) { 
				///double value = buffer.values[cursors[b]];
				double value = bufferValues[b][cursors[b]];
				if (value <= minValue) {
					minValue = value;
					minBufferIndex = b;
				}
			}
		}

		DoubleBuffer minBuffer=buffers[minBufferIndex];

		// trigger copies into output sequence, if necessary.
		counter += minBuffer.weight();
		while (counter>nextHit && j<triggerPositionsLength) { 
			outputValues[j++]=minValue;
			//System.out.println("adding to output="+minValue);
			if (j<triggerPositionsLength) nextHit=triggerPositions[j];
		}


		// that element has now been treated, move further.
		cursors[minBufferIndex]++;
		//System.out.println("cursors="+cern.it.util.Arrays.toString(cursors));
	
	} //end while (j<k)
	
	//cern.it.util.Log.println("returning output="+cern.it.util.Arrays.toString(outputValues));
	return outputValues;
}
/**
 * @return the number of elements within a buffer.
 */
public int k() {
	return buffers[0].k;
}
/**
 * Returns the number of elements currently needed to store all contained elements.
 */
public long memory() {
	long memory=0;
	for (int i=buffers.length; --i >=0;) {
		memory = memory + buffers[i].memory();
	}
	return memory;
}
/**
 * Computes the next triggerPosition for collapse
 * @return the next triggerPosition for collapse
 * @param j specifies that the j-th trigger position is to be computed
 * @param W the accumulated weights
 */
protected long nextTriggerPosition(int j, long W) {
	long nextTriggerPosition;
	
	if (W%2L != 0) { //is W odd?
		nextTriggerPosition=j*W + (W+1)/2;
	}
	
	else { //W is even
		//alternate between both possible next hit positions upon successive invocations
		if (nextTriggerCalculationState) {
			nextTriggerPosition=j*W + W/2;
		}
		else {
			nextTriggerPosition=j*W + (W+2)/2;
		}
	}
			
	return nextTriggerPosition;
}
/**
 * Returns how many percent of the elements contained in the receiver are <tt>&lt;= element</tt>.
 * Does linear interpolation if the element is not contained but lies in between two contained elements.
 *
 * @param the element to search for.
 * @return the percentage <tt>p</tt> of elements <tt>&lt;= element</tt> (<tt>0.0 &lt;= p &lt;=1.0)</tt>.
 */
public double phi(double element) {
	double elementsLessThanOrEqualToElement=0.0;							
	for (int i=buffers.length; --i >= 0; ) {
		if (! buffers[i].isEmpty()) {
			elementsLessThanOrEqualToElement += buffers[i].weight * buffers[i].rank(element);
		}
	}

	return elementsLessThanOrEqualToElement / totalSize();
}
/**
 * @return a String representation of the receiver
 */
public String toString() {
	StringBuffer buf = new StringBuffer();
	for (int b=0; b<this.b(); b++) {
		if (! buffers[b].isEmpty()) {
			buf.append("buffer#"+b+" = ");
			buf.append(buffers[b].toString()+"\n");
		}
	}
	return buf.toString();
}
/**
 * Returns the number of elements in all buffers.
 */
public long totalSize() {
	DoubleBuffer[] fullBuffers = _getFullOrPartialBuffers();
	long totalSize=0;							
	for (int i=fullBuffers.length; --i >= 0; ) {
		totalSize += fullBuffers[i].size() * fullBuffers[i].weight();
	}

	return totalSize;
}
}
