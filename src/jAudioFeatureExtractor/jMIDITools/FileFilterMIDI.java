/*
 * @(#)FileFilterMIDI.java	1.0	April 5, 2005.
 *
 * McGill University
 */

package jAudioFeatureExtractor.jMIDITools;

import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 * A file filter. Implements the two methods of the FileFilter
 * abstract class.
 *
 * <p>Filters all files except directories and files that
 * end with .mid or .midi (case is ignored).
 *
 * @author	Cory McKay
 * @see		FileFilter
 */
public class FileFilterMIDI
	extends FileFilter
{
	public boolean accept(File f)
	{
		boolean acceptable_extension = false;
		String lowercase_file_name = f.getName().toLowerCase();
		if ( f.isDirectory() ||
		     lowercase_file_name.endsWith(".mid") ||
		     lowercase_file_name.endsWith(".midi") )
			acceptable_extension = true;
		return acceptable_extension;
	}

	public String getDescription()
	{
		return "MIDI (mid, midi) files";
	}
}