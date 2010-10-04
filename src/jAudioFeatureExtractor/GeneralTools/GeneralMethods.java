/*
 * @(#)GeneralMethods.java	1.0	April 5, 2005.
 *
 * Cory McKay
 * McGill Univarsity
 */

package jAudioFeatureExtractor.GeneralTools;


/**
 * A holder class for general static methods that can be used for a wide
 * variety of purposes.
 *
 * @author	Cory McKay
 */
public class GeneralMethods 
{
	/**
	 * Returns a shortened array, with all entries that were set to null
	 * removed. Returns null if the resulting array has no valid entries
	 * or if the given array has no valid entries.
	 *
	 * @param	array	The array to remove null entries from.
	 * @return			A shortened array with all null entries removed,
	 *					or null.
	 */
	public static Object[] removeNullEntriesFromArray(Object[] array)
	{
		if (array == null)
			return null;

		int number_null_entries = 0;
		for (int i = 0; i < array.length; i++)
			if (array[i] == null)
				number_null_entries++;

		int number_valid_entries = array.length - number_null_entries;
		if (number_valid_entries == 0)
			return null;

		Object[] new_array = new Object[number_valid_entries];
		int current_index = 0;
		for (int i = 0; i < array.length; i++)
			if (array[i] != null)
			{
				new_array[current_index] = array[i];
				current_index++;
			}
		
		return new_array;
	}


	/**
	 * Returns a copy of the given array. Note that the entries
	 * are copied by reference.
	 *
	 * @param	given_array	The array to copy
	 * @return				A copy of the given array.
	 */
	public static Object[] getCopyOfArray(Object[] given_array)
	{
		Object[] new_array = new Object[given_array.length];
		for (int i = 0; i < new_array.length; i++)
			new_array[i] = given_array[i];
		return new_array;
	}


	/**
	 * Returns a new array whose first part consists of the elements of
	 * array_1 and whose second part consists of the elements of array_2.
	 *
	 * @param	array_1 The first array to concatenate.
	 * @param	array_2 The second array to concatenate.
	 * @return			array_1 and array_2 combined into 1 array.
	 */
	public static Object[] concatenateArray(Object[] array_1, Object[] array_2)
	{
		int length_1 = array_1.length;
		int length_2 = array_2.length;
		Object[] new_array = new Object[length_1 + length_2];
		for (int i = 0; i < length_1; i++)
			new_array[i] = array_1[i];
		for (int j = 0; j < length_2; j++)
			new_array[length_1 + j] = array_2[j];
		return new_array;
	}
}