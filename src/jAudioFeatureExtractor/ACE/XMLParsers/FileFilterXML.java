/*
 * @(#)FileFilterXML.java	1.0	April 5, 2005.
 *
 * McGill University
 */

package jAudioFeatureExtractor.ACE.XMLParsers;

import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 * A file filter for the JFileChooser class.
 * Implements the two methods of the FileFilter
 * abstract class.
 *
 * <p>Filters all files except directories and files that
 * end with .xml (case is ignored).
 * 
 * @author	Cory McKay
 * @see		FileFilter
 */
public class FileFilterXML
	extends FileFilter
{
	public boolean accept(File f)
	{
		return f.getName().toLowerCase().endsWith(".xml") || f.isDirectory();
	}

	public String getDescription()
	{
		return "XML File";
	}
}
