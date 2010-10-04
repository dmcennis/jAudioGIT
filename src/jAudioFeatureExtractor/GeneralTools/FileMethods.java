/*
 * @(#)FileMethods.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.GeneralTools;

import java.io.*;


/**
 * A holder class for static methods relating to files.
 *
 * @author	Cory McKay
 */
public class FileMethods 
{
	/**
	 * Copies the contents of one file to another.
	 * Throws an exception if the destination file already exists
	 * or if the original file does not exist.
	 *
	 * @param	original	The name of the file to be copied.
	 * @param	destination	The name of the file to be copied to.
	 */
	public static void copyFile(String original, String destination)
		throws Exception
	{
		File original_file = new File(original);
		File destination_file = new File(destination);
		if (!original_file.exists())
			throw new Exception("File with path " + original + " does not exist.");
		if (destination_file.exists())
			throw new Exception("File with path " + destination + " already exists.");
		FileReader in = new FileReader(original_file);
		FileWriter out = new FileWriter(destination_file);
		int c;
		while ((c = in.read()) != -1)
			out.write(c);
		in.close();
		out.close();
    }
}