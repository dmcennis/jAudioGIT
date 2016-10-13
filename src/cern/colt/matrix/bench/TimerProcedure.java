/*
Copyright © 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package cern.colt.matrix.bench;

/**
 * Interface that represents a procedure object: a procedure that takes 
 * a single argument and does not return a value.
 */
interface TimerProcedure {
/**
 * Applies a procedure to an argument.
 * Optionally can return a boolean flag to inform the object calling the procedure.
 *
 * <p>Example: forEach() methods often use procedure objects.
 * To signal to a forEach() method whether iteration should continue normally or terminate (because for example a matching element has been found),
 * a procedure can return <tt>false</tt> to indicate termination and <tt>true</tt> to indicate continuation.
 *
 * @param element   element passed to the procedure.
 * @return a flag  to inform the object calling the procedure.
 */
public void apply(cern.colt.Timer element);
/**
 * Prepare parameters before calling apply()
 */
public void init();
}
