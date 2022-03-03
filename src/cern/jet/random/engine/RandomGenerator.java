/*
Copyright ï¿½ 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package cern.jet.random.engine;

/**
 * Interface for uniform pseudo-random number generators.
 */
public interface RandomGenerator  {

	/**
	 * Returns a 32 bit uniformly distributed random number in the open unit interval <code>(0.0,1.0)</code> (excluding 0.0 and 1.0).
	 */
	public double raw();

	/**
	 * Returns a 64 bit uniformly distributed random number in the open unit interval <code>(0.0,1.0)</code> (excluding 0.0 and 1.0).
	 */
	public double nextDouble();
	
	/**
	 * Returns a 32 bit uniformly distributed random number in the closed interval <tt>[Integer.MIN_VALUE,Integer.MAX_VALUE]</tt> (including <tt>Integer.MIN_VALUE</tt> and <tt>Integer.MAX_VALUE</tt>);
	 */
	public int nextInt();
	
	/**
	 * Returns a 64 bit uniformly distributed random number in the closed interval <tt>[Long.MIN_VALUE,Long.MAX_VALUE]</tt> (including <tt>Long.MIN_VALUE</tt> and <tt>Long.MAX_VALUE</tt>).
	 */
	public long nextLong();

	/**
	 * Returns a 32 bit uniformly distributed random number in the open unit interval <code>(0.0f,1.0f)</code> (excluding 0.0f and 1.0f).
	 */
	public float nextFloat();
}