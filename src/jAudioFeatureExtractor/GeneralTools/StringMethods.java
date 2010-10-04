/*
 * @(#)StringMethods.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.GeneralTools;

import java.io.File;
import java.text.DecimalFormat;


/**
 * A holder class for static methods relating to manipulating strings.
 *
 * @author	Cory McKay
 * @see		String
 */
public class StringMethods 
{
	/**
	 * Returns the name of the file referred to by the given path.
	 *
	 * @param	file_path The file path from which the file name is to be extracted.
	 * @return	The name of the file referred to in the parameter.
	 */
	public static String convertFilePathToFileName(String file_path)
	{
		return file_path.substring(file_path.lastIndexOf(File.separator) + 1, file_path.length());
	}


	/**
	 * Returns the name of the directory that the given filename
	 * is found in. Throws an exception if no valid directory
	 * separator is present.
	 */
	 public static String getDirectoryName(String file_path)
		 throws Exception
	{
		int index_of_last_separator = file_path.lastIndexOf(File.separatorChar);
		if (index_of_last_separator == -1)
			throw new Exception(file_path + " does not contain a valid directory separator.");
		return new String(file_path.substring(0, file_path.lastIndexOf(File.separator)) + File.separator);
	}


	/**
	 * Returns a copy of the given string with the extension removed. Returns null
	 * if there is no extension or if there are less than five characters
	 * in the string.
	 *
	 * <p><b>IMPORTANT:</b> <i>filename</i> should consist of at least four characters.
	 *
	 * @param	filename The name of the file from which the extension is to be removed.
	 * @return	The name of the file with the extension removed.
	 */
	public static String removeExtension(String filename)
	{
		if (filename.length() < 5)
			return null;
		if (filename.charAt(filename.length() - 4) != '.')
		{
			if (filename.charAt(filename.length() - 5) == '.')
				return filename.substring(0, (filename.length() - 5));
			else if (filename.charAt(filename.length() - 3) == '.')
				return filename.substring(0, (filename.length() - 3));
			else return null;
		}
		return filename.substring(0, (filename.length() - 4));
	}


	/**
	 * Returns the 2, 3 or 4 letter extension of the given file name. Returns null
	 * if there is no extension or if there are less than 4 characters in the string.
	 *
	 * <p><b>IMPORTANT:</b> <i>filename</i> should consist of at least four characters.
	 *
	 * @param	filename The name of the file from which the extension is to be returned.
	 * @return	The extension of the file.
	 */
	public static String getExtension(String filename)
	{
		if (filename.length() < 5)
			return null;
		if (filename.charAt(filename.length() - 4) != '.')
		{
			if (filename.charAt(filename.length() - 5) == '.')
				return filename.substring((filename.length() - 5), filename.length());
			else if (filename.charAt(filename.length() - 3) == '.')
				return filename.substring((filename.length() - 3), filename.length());
			else return null;
		}
		return filename.substring((filename.length() - 4), filename.length());
	}


	/**
	 * Returns a copy of the given string with all but the first <i>number_characters</i> eliminated.
	 * If the given string is shorter than <i>number_characters</i>, then blank spaces are
	 * added to the end of the string in order to make it the full length.
	 *
	 * @param	string_to_shorten	The string to be shortened or have spaces added to its end.
	 * @param	number_characters	Number of characters in the new string.
	 * @return	The shortened string.
	 */
	public static String getBeginningOfString(String string_to_shorten, int number_characters)
	{
		String copy = new String(string_to_shorten);
		if (string_to_shorten.length() < number_characters)
		{
			int difference = number_characters - string_to_shorten.length();
			for (int i = 0; i < difference; i++)
				copy += " ";
			return copy;
		}
		else if (string_to_shorten.length() > number_characters)
			return string_to_shorten.substring(0, number_characters);
		else return copy;
	}


	/**
	 * Returns a copy of the given string with all but the first <i>number_characters</i> eliminated.
	 * If the given string is shorter than <i>number_characters</i>, then hyphens are are added
	 * to the end of the string in order to make it the full length (with two blank spaces on each side.
	 *
	 * @param	string_to_shorten	The string to be shortened or have spaces added to its end.
	 * @param	number_characters	Number of characters in the new string.
	 * @return	The shortened string.
	 */
	public static String getBeginningOfStringWithHyphenFiller(String string_to_shorten, int number_characters)
	{
		String copy = new String(string_to_shorten);
		if (string_to_shorten.length() < number_characters)
		{
			int difference = number_characters - string_to_shorten.length();
			for (int i = 0; i < difference; i++)
			{
				if (i == 0 || i == 1 || i == (difference - 2) || i == (difference - 1))
					copy += " ";
				else
					copy += "-";
			}
			return copy;
		}
		else if (string_to_shorten.length() > number_characters)
			return string_to_shorten.substring(0, number_characters);
		else return copy;
	}


	/**
	 * Returns a formatted version of <i>number_to_round</i> that has been
	 * converted to scientific notation and includes the given number
	 * of <i>significant_digits</i>.
	 *
	 * <p>Values of not a number, negative infinity and positive infinity will
	 * be returned as NaN, -Infinity and Infinity respectively.
	 *
	 * @param	number_to_round		The number that is to be formatted.
	 * @param	significant_digits	The number of significant digits to use.
	 */
	public static String getDoubleInScientificNotation(double number_to_round, int significant_digits)
	{
		if (Double.isNaN(number_to_round))
			return new String("NaN");
		if (Double.isInfinite(number_to_round))
			return new String("Infinity");
//		if (number_to_round == Double.POSITIVE_INFINITY)
//			return new String("Infinity");

		String format_pattern = "0.";
		for (int i = 0; i < significant_digits - 1; i++)
			format_pattern += "#";
		format_pattern += "E0";
		java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(java.util.Locale.ENGLISH);
		DecimalFormat decimal_formatter = (DecimalFormat)formatter;
		decimal_formatter.applyPattern(format_pattern);
		

		return decimal_formatter.format(number_to_round);
	}
	
	
	/**
	 * Returns a formatted version of <i>number_to_round</i>. It will always
	 * show a 0 before numbers less than one, and will only show up to
	 * <i>decimal_places</i> decimal places.
	 *
	 * <p>Values of not a number, negative infinity and positive infinity will
	 * be returned as NaN, -Infinity and Infinity respectively.
	 *
	 * @param	number_to_round	The number that is to be rounded.
	 * @param	decimal_places	The maximum number of decimal places that will be displayed.
	 */
	public static String getRoundedDouble(double number_to_round, int decimal_places)
	{
		if (number_to_round == Double.NaN)
			return new String("NaN");
		if (number_to_round == Double.NEGATIVE_INFINITY)
			return new String("-Infinity");
		if (number_to_round == Double.POSITIVE_INFINITY)
			return new String("Infinity");

		String format_pattern = "#0.";
		for (int i = 0; i < decimal_places; i++)
			format_pattern += "#";
		DecimalFormat formatter = new DecimalFormat(format_pattern);

		return formatter.format(number_to_round);
	}


	/**
	 * Returns the index in the possible_names array where the given_name parameter
	 * occurs. Throws an exception if it is not there.
	 *
	 * @param	given_name		The string to search for in possible_names.
	 * @param	possible_names	The array to search for given_names in.
	 * @return	The index in possible_names that contains given_name.
	 */
	public static int getIndexOfString(String given_name, String[] possible_names)
		throws Exception
	{
		for (int i = 0; i < possible_names.length; i++)
			if (given_name.equals(possible_names[i]))
				return i;
		throw new Exception("Unable to find " + given_name + ".");
	}


	/**
	 * Returns a shorened copy of the given array of strings with all duplicate 
	 * entries removed. The original array of strings is not changed.
	 *
	 * @param	strings	The array of strings to remove duplicate entries from.
	 * @return			A shortened copy of the given strings with duplicates
	 *					removed.
	 */
	public static String[] removeDoubles(String[] strings)
	{
		String[] editable_strings = new String[strings.length];
		for (int i = 0; i < editable_strings.length; i++)
			editable_strings[i] = strings[i];

		for (int i = 0; i < editable_strings.length - 1; i++)
			for (int j = i + 1; j < editable_strings.length; j++)
				if (editable_strings[i] != null && editable_strings[j] != null)
					if (editable_strings[i].equals(editable_strings[j]))
						editable_strings[j] = null;

		Object[] cleaned_obj = jAudioFeatureExtractor.GeneralTools.GeneralMethods.removeNullEntriesFromArray(editable_strings);
		String[] cleaned_strings = new String[cleaned_obj.length];
		for (int i = 0; i < cleaned_strings.length; i++)
			cleaned_strings[i] = (String) cleaned_obj[i];

		return cleaned_strings;
	}
}