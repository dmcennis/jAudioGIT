/*
 * @(#)RecordingInfo.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.DataTypes;

import java.io.Serializable;

import jAudioFeatureExtractor.jAudioTools.AudioSamples;


/**
 * A class for holding references to audio recordings.
 *
 * @author Cory McKay
 */
public class RecordingInfo implements Serializable
{
	/* FIELDS ******************************************************************/
	
	
	/**
	 * A name used internally to refer to the referenced recording.
	 */
	public	String			identifier;
	
	
	/**
	 * The path of the audio file referred to by objects of this class.
	 * Should be unique.
	 */
	public	String			file_path;
	
	
	/**
	 * Audio samples stored as arrays of doubles. May sometimes be set to
	 * null in order to store space, in which case they can be extracted
	 * from the file referred to in the file_path field.
	 */
	transient public	AudioSamples	samples;
	
	
	/**
	 * Whether or not a feature extractor receiving	an object of this class
	 * should extract features from the referenced file.
	 */
	public	boolean			should_extract_features;
	
	
	/* CONSTRUCTOR *************************************************************/


	/**
	 * Basic constructor for filling fields.
	 * 
	 * @param	identifier				A name used internally to refer to the
	 *									referenced recording.
	 * @param	file_path				The path of the audio file referred to by
	 *									objects of this class. Should be unique.
	 * @param	samples					Audio samples stored as arrays of doubles.
	 *									May sometimes be set to null in order to
	 *									store space.
	 * @param	should_extract_features	Whether or not a feature extractor receiving
	 *									an object of this class should extract
	 *									features from the referenced file.
	 */
	public RecordingInfo( String identifier,
	                      String file_path,
	                      AudioSamples samples,
	                      boolean should_extract_features )
	{
		this.identifier = identifier;
		this.file_path = file_path;
		this.samples = samples;
		this.should_extract_features = should_extract_features;
	}


	/**
	 * Basic constructor for filling fields. Sets the identifier to the
	 * file name extracted from the given file_path, and sets the
	 * should_extract_features field to false and the samples field to null.
	 * 
	 * @param	file_path				The path of the audio file referred to by
	 *									objects of this class. Should be unique.
	 */
	public RecordingInfo(String file_path)
	{
		identifier = jAudioFeatureExtractor.GeneralTools.StringMethods.convertFilePathToFileName(file_path);
		this.file_path = file_path;
		samples = null;
		should_extract_features = false;
	}
}