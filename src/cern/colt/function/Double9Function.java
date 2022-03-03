package cern.colt.function;

/*
Copyright © 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
/**
 * Interface that represents a function object: a function that takes 
 * 9 arguments and returns a single value.
 */
public interface Double9Function {
/**
 * Applies a function to nine arguments.
 * 
 * @return the result of the function.
 */
abstract public double apply(
	double a00, double a01, double a02,
	double a10, double a11, double a12,
	double a20, double a21, double a22
	);
}
