/*
Copyright © 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package cern.colt.buffer;

import cern.colt.list.DoubleArrayList;
/**
 * Target of a streaming <tt>DoubleBuffer3D</tt> into which data is flushed upon buffer overflow.
 *
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public interface DoubleBuffer3DConsumer {
/**
 * Adds all specified (x,y,z) points to the receiver.
 * @param x the x-coordinates of the points to be added.
 * @param y the y-coordinates of the points to be added. 
 * @param z the z-coordinates of the points to be added.
 */
public void addAllOf(DoubleArrayList x, DoubleArrayList y, DoubleArrayList z);
}
