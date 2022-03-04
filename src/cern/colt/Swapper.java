/*
Copyright © 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package cern.colt;

/**
 * Interface for an object that knows how to swap elements at two positions (a,b).
 *
 * @see cern.colt.GenericSorting
 *
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 03-Jul-99
 */
public interface Swapper {
/**
 * Swaps the generic data g[a] with g[b].
 */
public abstract void swap(int a, int b);
}
