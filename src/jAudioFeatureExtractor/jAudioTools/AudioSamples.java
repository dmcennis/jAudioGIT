/*
 * @(#)AudioSamples.java	1.02	April 27, 2005.
 *
 * Cory McKay
 * McGill Univarsity
 */

package jAudioFeatureExtractor.jAudioTools;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


/**
 * A class for holding audio samples and associated audio formatting information.
 * Samples are stored as arrays of doubles, generally for use in analysis and
 * signal processing. Samples are stored maintaining any channel segregation as
 * well as mixed down to a single channel. Values can vary between -1 and +1.
 *
 * <p>Each object of this class is assigned a string at instantiation that
 * can be used externally as a reference. Although not used internally by the
 * methods of this class, it will generally be best to assign a unique value
 * to this string, such as a filename.
 *
 * <p>Includes constructors for generating and storing the samples from
 * AudioInputStreams, audio files or existing arrays of sample values.
 *
 * <p>Includes methods for accessing samples as a whole or as segments, and also
 * for returning the samples after having been broken into equally sized windows
 * of specified sizes. A method is also present for saving the samples to a File.
 * Methods are also available for checking if any sample values fall out of the
 * allowable ranges (i.e. between -1 and +1) and for normalizing samples so that
 * the maximum amplitude will have an absolute value of 1. Methods are also
 * available for getting copies of objects of this class as well as for getting
 * formatted information about them. The samples can also be changed externally.
 *
 * @author	Cory McKay
 */
public class AudioSamples 
{
	/* FIELDS ******************************************************************/


	/**
	 * A unique identifier that external objects can use to identify each individual
	 * object of this class. Not used internally by objects of this class.
	 */
	protected String					unique_ID;


	/**
	 * Audio samles, with a minimum value of -1 and a maximum value of +1. If
	 * audio is multi-channel, all channels are mixed down into this one channel.
	 */
	protected double[]					samples;


	/**
	 * Audio samles, with a minimum value of -1 and a maximum value of +1. Is
	 * set to null if only one channel of audio is present. First indice 
	 * corresponds to channel and second indice corresponds to sample number.
	 */
	protected double[][]				channel_samples;


	/**
	 * The AudioFormat used to encode the samples field. Will always involve
	 * big-endian signed linear PCM encoding and a bit depth of eith 8 or 16 bits.
	 */
	protected AudioFormat				audio_format;


	/* CONSTRUCTORS ************************************************************/

	
	/**
	 * Store the given audio file as samples and the corresponding
	 * AudioFormat.
	 *
	 * <p><b>IMPORTANT:</b> Note that, regardless of the AudioFormat in the given file,
	 * it will be converted and stored using big-endian signed linear PCM encoding. 
	 * Sampling rate and number of channels is maintained, but bit depth will be
	 * changed to 16 bits if it is not either 8 or 16 bits.
	 *
	 * @param	audio_file				A reference to an audio file from which to extract
	 *									and store samples as double values.
	 * @param	unique_identifier		The string that will be used by external
	 *									objects to uniquely identify the instantiated
	 *									AudioSamples object.
	 * @param	normalize_if_clipped	If set to true, then normalizes audio so the
	 *									absolute value of the highest amplitude sample is
	 *									1. Does this if and only if one or more of the samples
	 *									is outside the allowable range of sample values
	 *									(-1 to 1). If set to false, then does not
	 *									normalize, regardless of sample values.
	 * @throws	Exception				Throws an informative exception if the samples
	 *									cannot be extracted from the file.
	 */
	public AudioSamples( File audio_file, 
	                     String unique_identifier,
		                 boolean normalize_if_clipped )
		throws Exception
	{
		if ( !audio_file.exists() )
			throw new Exception("File " + audio_file.getName() + " does not exist.");
		if ( audio_file.isDirectory() )
			throw new Exception("File " + audio_file.getName() + " is a directory.");

		AudioInputStream audio_input_stream = null;
		
		try
		{
			audio_input_stream = AudioSystem.getAudioInputStream(audio_file);
		}
		catch (UnsupportedAudioFileException ex)
		{
			throw new Exception("File " + audio_file.getName() + " has an unsupported audio format.");
		}
		catch (IOException ex)
		{
			throw new Exception("File " + audio_file.getName() + " is not readable.");
		}
		
		AudioInputStream converted_audio = AudioMethods.getConvertedAudioStream(audio_input_stream);
		
		channel_samples = AudioMethods.extractSampleValues(converted_audio);

		samples = DSPMethods.getSamplesMixedDownIntoOneChannel(channel_samples);
		
		if (channel_samples.length == 1)
			channel_samples = null;

		audio_format = converted_audio.getFormat();

		unique_ID = unique_identifier;

		if (normalize_if_clipped)
			normalizeIfClipped();
		
		converted_audio.close();
	}


	/**
	 * Store the given AudioInputStream as samples and the corresponding
	 * AudioFormat.
	 *
	 * <p><b>IMPORTANT:</b> Note that the AudioFormat in the AudioInputStream will
	 * be converted and stored as big-endian signed linear PCM encoding with. Sampling
	 * rate and number of channels is maintained, but bit depth will be changed
	 * to 16 bits if it is not either 8 or 16 bits.
	 *
	 * @param	audio_input_stream		An AudioInputStream from which to extract
	 *									and store samples as double values.
	 * @param	unique_identifier		The string that will be used by external
	 *									objects to uniquely identify the instantiated
	 *									AudioSamples object.
	 * @param	normalize_if_clipped	If set to true, then normalizes audio so the
	 *									absolute value of the highest amplitude sample is
	 *									1. Does this if and only if one or more of the samples
	 *									is outside the allowable range of sample values
	 *									(-1 to 1). If set to false, then does not
	 *									normalize, regardless of sample values.
	 * @throws	Exception				Throws an informative exception if the samples
	 *									cannot be extracted from the AudioInputStream.
	 */
	public AudioSamples( AudioInputStream audio_input_stream,
	                     String unique_identifier,
	                     boolean normalize_if_clipped )
		throws Exception
	{
		if (audio_input_stream == null)
			throw new Exception("Given AudioInputStream is empty.");

		unique_ID = unique_identifier;

		AudioInputStream converted_audio = AudioMethods.getConvertedAudioStream(audio_input_stream);
		
		channel_samples = AudioMethods.extractSampleValues(converted_audio);

		samples = DSPMethods.getSamplesMixedDownIntoOneChannel(channel_samples);
		
		if (channel_samples.length == 1)
			channel_samples = null;

		audio_format = converted_audio.getFormat();

		if (normalize_if_clipped)
			normalizeIfClipped();
		
		converted_audio.close();
	}


	/**
	 * Store the given samples with the associated AudioFormat.
	 *
	 * <p><b>IMPORTANT:</b> Note that, regardless of the given AudioFormat, it will be
	 * converted and samples will be stored as big-endian signed linear PCM encoding.
	 * Sampling rate and number of channels is maintained, but bit depth will be changed
	 * to 16 bits if it is not either 8 or 16 bits.
	 *
	 * @param	audio_samples			Audio samles to store, with a minimum value of
	 *									-1 and a maximum value of +1. The first indice 
	 *									corresponds to the channel and the second indice
	 *									corresponds to the sample number.
	 * @param	audio_format			The AudioFormat to use for interpereting the
	 *									given samples.
	 * @param	unique_identifier		The string that will be used by external
	 *									objects to uniquely identify the instantiated
	 *									AudioSamples object.
	 * @param	normalize_if_clipped	If set to true, then normalizes audio so the
	 *									absolute value of the highest amplitude sample is
	 *									1. Does this if and only if one or more of the samples
	 *									is outside the allowable range of sample values
	 *									(-1 to 1). If set to false, then does not
	 *									normalize, regardless of sample values.
	 * @throws	Exception				Throws an informative exception if the samples
	 *									cannot be extracted from the audio_samples.
	 */
	public AudioSamples( double[][] audio_samples, 
					     AudioFormat audio_format, 
			             String unique_identifier,
		                 boolean normalize_if_clipped )
		throws Exception
	{
		if (audio_samples == null)
			throw new Exception("Given audio samples array is empty.");
		for (int chan = 0; chan < audio_samples.length; chan++)
			if (audio_samples[chan] == null)
				throw new Exception("One or more channels of given audio samples array is empty.");
		int number_samples = audio_samples[0].length;
		for (int chan = 0; chan < audio_samples.length; chan++)
			if (audio_samples[chan].length != number_samples)
				throw new Exception( "Different channels of given audio samples array have a\n" +
		                             "different number of samples." );
		if (audio_format == null)
			throw new Exception("Null audio format specified for samples.");
		if (audio_format.getChannels() != audio_samples.length)
			throw new Exception( "The specified samples have " + audio_samples.length + " channels but\n" +
			                     "the specified audio format has " + audio_format.getChannels() + " channels.\n" +
			                     "These must be the same." );

		unique_ID = unique_identifier;

		samples = DSPMethods.getSamplesMixedDownIntoOneChannel(audio_samples);
		
		if (audio_samples.length == 1)
			channel_samples = null;
		else
			channel_samples = DSPMethods.getCopyOfSamples(audio_samples);

		this.audio_format = AudioMethods.getConvertedAudioFormat(audio_format);

		if (normalize_if_clipped)
			normalizeIfClipped();
	}
	

	/**
	 * Store the given samples with the associated sampling rate.
	 * A default AudioFormat is generated.
	 *
	 * @param	audio_samples			Audio samles to store, with a minimum value of
	 *									-1 and a maximum value of +1. The first indice 
	 *									corresponds to the channel and the second indice
	 *									corresponds to the sample number.
	 * @param	sampling_rate			The sampling rate to associate with the given
	 *									samples.
	 * @param	unique_identifier		The string that will be used by external
	 *									objects to uniquely identify the instantiated
	 *									AudioSamples object.
	 * @param	normalize_if_clipped	If set to true, then normalizes audio so the
	 *									absolute value of the highest amplitude sample is
	 *									1. Does this if and only if one or more of the samples
	 *									is outside the allowable range of sample values
	 *									(-1 to 1). If set to false, then does not
	 *									normalize, regardless of sample values.
	 * @throws	Exception				Throws an informative exception if the samples
	 *									cannot be extracted from the audio_samples.
	 */
	public AudioSamples( double[][] audio_samples,
		                 float sampling_rate,
			             String unique_identifier,
		                 boolean normalize_if_clipped )
		throws Exception
	{
		if (audio_samples == null)
			throw new Exception("Given audio samples array is empty.");
		for (int chan = 0; chan < audio_samples.length; chan++)
			if (audio_samples[chan] == null)
				throw new Exception("One or more channels of given audio samples array is empty.");
		int number_samples = audio_samples[0].length;
		for (int chan = 0; chan < audio_samples.length; chan++)
			if (audio_samples[chan].length != number_samples)
				throw new Exception( "Different channels of given audio samples array have a\n" +
		                             "different number of samples." );

		unique_ID = unique_identifier;

		samples = DSPMethods.getSamplesMixedDownIntoOneChannel(audio_samples);
		
		if (audio_samples.length == 1)
			channel_samples = null;
		else
			channel_samples = DSPMethods.getCopyOfSamples(audio_samples);

		audio_format = getDefaultAudioFormat(sampling_rate);

		if (normalize_if_clipped)
			normalizeIfClipped();
	}


	/* PUBLIC METHODS ***********************************************************/


	/**
	 * Returns a copy of this AudioSamples object. All fields are copies, not
	 * references to the original fields, so no changes made to the copies
	 * will change the original
	 *
	 * @return				A copy of this object.
	 * @throws	Exception	Throws an informative exception if the copy cannot
	 *						be made.
	 */
	public AudioSamples getCopyOfAudioSamples()
		throws Exception
	{
		String new_unique_ID = null;
		if (new_unique_ID != null)
			new_unique_ID = new String(unique_ID);

		double[][] new_channel_samples = null;
		if (channel_samples != null)
		{
			new_channel_samples = new double[channel_samples.length][];
			for (int i = 0; i < new_channel_samples.length; i++)
			{
				new_channel_samples[i] = new double[channel_samples[i].length];
				for (int j = 0; j < new_channel_samples[i].length; j++)
					new_channel_samples[i][j] = channel_samples[i][j];
			}
		}
		else
		{
			new_channel_samples = new double[1][samples.length];
			for (int i = 0; i < samples.length; i++)
				new_channel_samples[0][i] = samples[i];
		}

		AudioFormat new_audio_format = null;
		if (audio_format != null)
		{
			new_audio_format = new AudioFormat( audio_format.getEncoding(),
												audio_format.getSampleRate(),
				                                audio_format.getSampleSizeInBits(),
				                                audio_format.getChannels(),
				                                audio_format.getFrameSize(),
				                                audio_format.getFrameRate(),
				                                audio_format.isBigEndian() );
		}

		return new AudioSamples (new_channel_samples, new_audio_format, new_unique_ID, false);
	}


	/**
	 * Returns a formatted description of the AudioFormat of the samples
	 * as well as the number of samples per channel, the duration in
	 * seconds of the recording and the maximum sample amplitude.
	 *
	 * @return A formatted description of the recording.
	 */
	public String getRecordingInfo()
	{
		String return_string = AudioMethods.getAudioFormatData(audio_format);

		String number_samples = getNumberSamplesPerChannel() + " samples\n";
		String duration = getDuration() + " seconds\n";
		String max_sample_value = getMaximumAmplitude() + "\n";

		return_string +=  new String("SAMPLES PER CHANNEL: " + number_samples);
		return_string +=  new String("DURATION: " + duration);
		return_string +=  new String("MAX SIGNAL AMPLITUDE: " + max_sample_value);
		return return_string;
	}


	/**
	 * Returns the identifier assigned to an object of this class at instantiation.
	 *
	 * @return	The identifier of this object.
	 */
	public String getUniqueIdentifier()
	{
		return unique_ID;
	}


	/**
	 * Returns the AudioFormat associated with the stored samples.
	 *
	 * @return	The AudioFormat associated with the stored samples.
	 */
	public AudioFormat getAudioFormat()
	{
		return audio_format;
	}


	/**
	 * Returns the sampling rate that is associated with the stored samples.
	 *
	 * @return	The sampling rate associated with the stored samples in the
	 *			form of a float.
	 */
	public float getSamplingRate()
	{
		return audio_format.getSampleRate();
	}


	/**
	 * Returns the sampling rate that is associated with the stored samples.
	 *
	 * @return	The sampling rate associated with the stored samples in the
	 *			form of a double.
	 */
	public double getSamplingRateAsDouble()
	{
		return (new Float(audio_format.getSampleRate())).doubleValue();
	}


	/**
	 * Returns the total number of samples per channel in the stored audio.
	 *
	 * @return	The total number of samples per channel in the stored audio.
	 */
	public int getNumberSamplesPerChannel()
	{
		return samples.length;
	}


	/**
	 * Returns the total length of the stored audio in seconds.
	 *
	 * @return	The length of the stored audio in seconds.
	 */
	public double getDuration()
	{
		return convertSampleIndexToTime(samples.length - 1);
	}


	/**
	 * Returns the number of channels of stored audio.
	 *
	 * @return	The number of channels of stored audio.
	 */
	public int getNumberChannels()
	{
		if (channel_samples == null)
			return 1;
		else
			return channel_samples.length;
	}


	/**
	 * Returns the stored audio samples. If the audio data originally consisted
	 * of multiple channels, then the returned samples represent the audio
	 * after mixing down into a single channel.
	 *
	 * @return	The audio samples stored in this object. These have a minimum
	 *          value of -1 and a maximum value of +1.
	 */
	public double[] getSamplesMixedDown()
	{
		return samples;
	}


	/**
	 * Returns the stored audio samples between the given sample indices.
	 * If the audio data originally consisted of multiple channels, then
	 * the returned samples represent the audio after mixing down into a
	 * single channel.
	 *
	 * @param	start_sample	The sample indice of the first sample to return.
	 *                          Must be 0 or higher and must be less than
	 *                          end_sample.
	 * @param	end_sample		The sample indice of the last sample to return.
	 *                          Must be less than the total number of samples.
	 * @return					The audio samples stored in this object. These
	 *							have a minimum	value of -1 and a maximum value
	 *							of +1. Only the samples within (and including)
	 *                          the given sample indices are returned. Only one
	 *                          (potentially combined) channel of audio is
	 *                          returned.
	 * @throws	Exception		Throws an informative exception if the given
	 *							start sample or end sample are outside acceptable
	 *                          ranges.
	 */
	public double[] getSamplesMixedDown(int start_sample, int end_sample)
		throws Exception
	{
		if (start_sample < 0)
			throw new Exception( "Requested audio starting at sample " + start_sample +
			                     "\nStart sample indice must be 0 or greater." );
		if (end_sample >= samples.length)
			throw new Exception( "Requested audio ending at sample " + end_sample +
			                     "\nA total of " + samples.length + "samples are present." +
			                     "\nRequested ending sample indice must be less than this." );
		if (start_sample >= end_sample)
			throw new Exception( "Requested audio starting at sample " + start_sample +
			                     " and ending at sample " + end_sample + ".\n" +
			                     "Requested start sample indice must be less than requested" +
			                     "\nend sample indice." );
		
		int number_samples = end_sample - start_sample;
		double[] sample_segment = new double[number_samples];
		for (int samp = start_sample; samp <= end_sample; samp++)
			sample_segment[samp - start_sample] = samples[samp];
		return sample_segment;
	}


	/**
	 * Returns the stored audio samples between the given times.
	 * If the audio data originally consisted of multiple channels, then
	 * the returned samples represent the audio after mixing down into a
	 * single channel.
	 *
	 * @param	start_time		The time, in seconds, of the first sample to
	 *                          return. Must be 0 or higher and must be less than
	 *                          end_time.
	 * @param	end_time		The time, in seconds, of the last sample to return.
	 *                          return. Must be less than or equal to the total 
	 *                          duration.
	 * @return					The audio samples stored in this object. These
	 *							have a minimum	value of -1 and a maximum value
	 *							of +1. Only the samples within (and including)
	 *                          the given times are returned. Only one (potentially 
	 *                          combined) channel of audio is returned.
	 * @throws	Exception		Throws an informative exception if the given
	 *							times are outside acceptable ranges.
	 */
	public double[] getSamplesMixedDown(double start_time, double end_time)
		throws Exception
	{
		int start_sample = convertTimeToSampleIndex(start_time);
		int end_sample = convertTimeToSampleIndex(end_time);
		return getSamplesMixedDown(start_sample, end_sample);
	}


	/**
	 * Returns the stored audio samples divided into windows of equal lengths.
	 * The last window is extended past the length of the stored samples if 
	 * necessary and is zero padded. If the audio data originally consisted of
	 * multiple channels, then the returned samples represent the audio after
	 * mixing down into a single channel.
	 *
	 * @param	window_size		The length in samples of the windows that the
	 *							samples are to divided into.
	 * @return					The audio samples stored in this object after
	 *							being broken into equally sized windows of the
	 *							specified size. The last window is zero-padded
	 *							if necessary. The samples have a minimum value
	 *							of -1 and a maximum value of +1. Only one
	 *                          (potentially combined) channel of audio is
	 *                          returned. The first indice specifies the window
	 *							and the second indice specifies the sample index
	 *							within the window.
	 * @throws	Exception		Throws an exception if a negative or 0 window size
	 *							is specified.
	 */
	public double[][] getSampleWindowsMixedDown(int window_size)
		throws Exception
	{
		if (window_size < 1)
			throw new Exception( "Window size of " + window_size + " specified.\n" +
			                     "This value must be above 0." );

		int number_windows = samples.length / window_size;
		if (samples.length % window_size != 0)
			number_windows++;

		double[][] windowed_samples = new double[number_windows][window_size];
		for (int win = 0; win < number_windows; win++)
		{
			if (win != number_windows - 1)
			{
				for (int samp = 0; samp < window_size; samp++)
					windowed_samples[win][samp] = samples[win * window_size + samp];
			}
			else
			{
				for (int samp = 0; samp < window_size; samp++)
				{
					if (win * window_size + samp < samples.length)
						windowed_samples[win][samp] = samples[win * window_size + samp];
					else
						windowed_samples[win][samp] = 0;
				}
			}
		}

		return windowed_samples;
	}


	/**
	 * Returns the stored audio samples divided into windows of equal lengths.
	 * The last window is extended past the length of the stored samples if 
	 * necessary and is zero padded. If the audio data originally consisted of
	 * multiple channels, then the returned samples represent the audio after
	 * mixing down into a single channel.
	 *
	 * @param	window_duration	The duration in seconds of the windows that the
	 *							samples are divided into.
	 * @return					The audio samples stored in this object after
	 *							being broken into equally sized windows of the
	 *							specified size. The last window is zero-padded
	 *							if necessary. The samples have a minimum value
	 *							of -1 and a maximum value of +1. Only one
	 *                          (potentially combined) channel of audio is
	 *                          returned. The first indice specifies the window
	 *							and the second indice specifies the sample index
	 *							within the window.
	 * @throws	Exception		Throws an exception if a negative or 0 window size
	 *							is specified.
	 */
	public double[][] getSampleWindowsMixedDown(double window_duration)
		throws Exception
	{
		int window_size = convertTimeToSampleIndex(window_duration);
		return getSampleWindowsMixedDown(window_size);
	}


	/**
	 * Returns the stored audio samples in the form of an AudioInputStream.
	 * If the audio data originally consisted of multiple channels, then the
	 * returned samples represent the audio after mixing down into a single
	 * channel.
	 *
	 * @return					The entire set of samples mixed down into one channel.
	 *							Returned in the form of an AudioInputStream.
	 * @throws	Exception		Throws an exception if a a problem occurs 
	 *							during conversion.
	 */
	public AudioInputStream getAudioInputStreamMixedDown()
		throws Exception
	{
		// Specify that only one channel is used
		AudioFormat mixed_down_audio_format = new AudioFormat( audio_format.getSampleRate(),
								                               audio_format.getSampleSizeInBits(),
														       1,
														       true,
														       audio_format.isBigEndian() );
		
		// Convert samples to 2-D array
		double[][] samples_to_convert = new double[1][];
		samples_to_convert[0] = samples;

		// Convert to an AudioInputStream
		AudioInputStream audio_input_stream
			= AudioMethods.convertToAudioInputStream(samples_to_convert, mixed_down_audio_format);
		
		// Return the resuls
		return audio_input_stream;
	}


	/**
	 * Returns the stored audio samples between the given samples in the form of an
	 * AudioInputStream. If the audio data originally consisted of multiple channels,
	 * then the returned samples represent the audio after mixing down into a single
	 * channel.
	 *
	 * @param	start_sample	The sample indice of the first sample to return.
	 *                          Must be 0 or higher and must be less than
	 *                          end_sample.
	 * @param	end_sample		The sample indice of the last sample to return.
	 *                          Must be less than the total number of samples.
	 * @return					The specified set of samples mixed down into one channel.
	 *							Returned in the form of an AudioInputStream.
	 * @throws	Exception		Throws an informative exception if the given
	 *							start sample or end sample are outside acceptable
	 *                          ranges or if error occurs during conversion.
	 */
	public AudioInputStream getAudioInputStreamMixedDown(int start_sample, int end_sample)
		throws Exception
	{
		// Specify that only one channel is used
		AudioFormat mixed_down_audio_format = new AudioFormat( audio_format.getSampleRate(),
								                               audio_format.getSampleSizeInBits(),
														       1,
														       true,
														       audio_format.isBigEndian() );
		
		// Find the particular samples to convert
		double[] sample_portion = getSamplesMixedDown(start_sample, end_sample);

		// Convert the samples to 2-D array
		double[][] samples_to_convert = new double[1][];
		samples_to_convert[0] = sample_portion;

		// Convert to an AudioInputStream
		AudioInputStream audio_input_stream
			= AudioMethods.convertToAudioInputStream(samples_to_convert, mixed_down_audio_format);
		
		// Return the resuls
		return audio_input_stream;
	}


	/**
	 * Returns the stored audio samples  between the given times in the form of an
	 * AudioInputStream. If the audio data originally consisted of multiple channels,
	 * then the returned samples represent the audio after mixing down into a single
	 * channel.
	 *
	 * @param	start_time		The time, in seconds, of the first sample to
	 *                          return. Must be 0 or higher and must be less than
	 *                          end_time.
	 * @param	end_time		The time, in seconds, of the last sample to return.
	 *                          return. Must be less than or equal to the total 
	 *                          duration.
	 * @return					The specified set of samples mixed down into one channel.
	 *							Returned in the form of an AudioInputStream.
	 * @throws	Exception		Throws an informative exception if the given
	 *							start time or end time are outside acceptable
	 *                          ranges or if error occurs during conversion.
	 */
	public AudioInputStream getAudioInputStreamMixedDown(double start_time, double end_time)
		throws Exception
	{
		// Specify that only one channel is used
		AudioFormat mixed_down_audio_format = new AudioFormat( audio_format.getSampleRate(),
								                               audio_format.getSampleSizeInBits(),
														       1,
														       true,
														       audio_format.isBigEndian() );
		
		// Find the particular samples to convert
		double[] sample_portion = getSamplesMixedDown(start_time, end_time);

		// Convert the samples to 2-D array
		double[][] samples_to_convert = new double[1][];
		samples_to_convert[0] = sample_portion;

		// Convert to an AudioInputStream
		AudioInputStream audio_input_stream
			= AudioMethods.convertToAudioInputStream(samples_to_convert, mixed_down_audio_format);
		
		// Return the resuls
		return audio_input_stream;
	}


	/**
	 * Returns the stored audio samples.
	 *
	 * @return	The audio samples stored in this object. These have a minimum
	 *          value of -1 and a maximum value of +1. The first indice corresponds
	 *          to the channel and the second indice corresponds to the sample
	 *          number.
	 */
	public double[][] getSamplesChannelSegregated()
	{
		if (channel_samples == null)
		{
			double[][] formatted_samples = new double[1][];
			formatted_samples[0] = samples;
			return formatted_samples;
		}
		else
			return channel_samples;
	}


	/**
	 * Returns the stored audio samples between the given sample indices.
	 *
	 * @param	start_sample	The sample indice of the first sample to return.
	 *                          Must be 0 or higher and must be less than
	 *                          end_sample.
	 * @param	end_sample		The sample indice of the last sample to return.
	 *                          Must be less than the total number of samples.
	 * @return					The audio samples stored in this object. These
	 *							have a minimum	value of -1 and a maximum value
	 *							of +1. The first indice corresponds to the channel
	 *							and the second indice corresponds to the sample
	 *					        number.
	 * @throws	Exception		Throws an informative exception if the given
	 *							start sample or end sample are outside acceptable
	 *                          ranges.
	 */
	public double[][] getSamplesChannelSegregated(int start_sample, int end_sample)
		throws Exception
	{
		if (start_sample < 0)
			throw new Exception( "Requested audio starting at sample " + start_sample +
			                     "\nStart sample indice must be 0 or greater." );
		if (end_sample >= samples.length)
			throw new Exception( "Requested audio ending at sample " + end_sample +
			                     "\nA total of " + samples.length + "samples are present." +
			                     "\nRequested ending sample indice must be less than this." );
		if (start_sample >= end_sample)
			throw new Exception( "Requested audio starting at sample " + start_sample +
			                     " and ending at sample " + end_sample + ".\n" +
			                     "Requested start sample indice must be less than requested" +
			                     "\nend sample indice." );
		
		int number_samples = end_sample - start_sample + 1;

		// Case where only one channel of audio is present
		if (channel_samples == null)
		{
			double[][] sample_segment = new double[1][number_samples];
			for (int samp = start_sample; samp <= end_sample; samp++)
				sample_segment[0][samp - start_sample] = samples[samp];
			return sample_segment;
		}

		// Case where multiple channels of audio are present
		else
		{
			double[][] sample_segment = new double[channel_samples.length][number_samples];
			for (int chan = 0; chan < channel_samples.length; chan++)
				for (int samp = start_sample; samp <= end_sample; samp++)
					sample_segment[chan][samp - start_sample] = channel_samples[chan][samp];
			return sample_segment;
		}
	}


	/**
	 * Returns the stored audio samples between the given times in secondes.
	 *
	 * @param	start_time		The time, in seconds, of the first sample to
	 *                          return. Must be 0 or higher and must be less than
	 *                          end_time.
	 * @param	end_time		The time, in seconds, of the last sample to return.
	 *                          return. Must be less than or equal to the total 
	 *                          duration.
	 * @return					The audio samples stored in this object. These
	 *							have a minimum	value of -1 and a maximum value
	 *							of +1. The first indice corresponds to the channel
	 *							and the second indice corresponds to the sample
	 *					        number. Only the samples within (and including)
	 *                          the given times are returned. 
	 * @throws	Exception		Throws an informative exception if the given
	 *							times are outside acceptable ranges.
	 */
	public double[][] getSamplesChannelSegregated(double start_time, double end_time)
		throws Exception
	{
		int start_sample = convertTimeToSampleIndex(start_time);
		int end_sample = convertTimeToSampleIndex(end_time);
		return getSamplesChannelSegregated(start_sample, end_sample);
	}


	/**
	 * Returns the stored audio samples divided into windows of equal lengths.
	 * The last window is extended past the length of the stored samples if 
	 * necessary and is zero padded.
	 *
	 * @param	window_size		The length in samples of the windows that the
	 *							samples are to divided into.
	 * @return					The audio samples stored in this object after
	 *							being broken into equally sized windows of the
	 *							specified size. The last window is zero-padded
	 *							if necessary. The samples have a minimum value
	 *							of -1 and a maximum value of +1. The first indice
	 *							specifies the channel, the second indice specifies
	 *							the window and the third indice specifies the 
	 *							sample index within the window.
	 * @throws	Exception		Throws an exception if a negative or 0 window size
	 *							is specified.
	 */
	public double[][][] getSampleWindowsChannelSegregated(int window_size)
		throws Exception
	{
		if (channel_samples == null)
		{
			double[][][] windowed_samples = new double[1][][];
			windowed_samples[0] = getSampleWindowsMixedDown(window_size);
			return windowed_samples;
		}

		if (window_size < 1)
			throw new Exception( "Window size of " + window_size + " specified.\n" +
			                     "This value must be above 0." );

		int number_windows = samples.length / window_size;
		if (samples.length % window_size != 0)
			number_windows++;

		double[][][] windowed_samples = new double[channel_samples.length][number_windows][window_size];
		for (int chan = 0; chan < channel_samples.length; chan++)
			for (int win = 0; win < number_windows; win++)
			{
				if (win != number_windows - 1)
				{
					for (int samp = 0; samp < window_size; samp++)
						windowed_samples[chan][win][samp] = channel_samples[chan][win * window_size + samp];
				}
				else
				{
					for (int samp = 0; samp < window_size; samp++)
					{
						if (win * window_size + samp < samples.length)
							windowed_samples[chan][win][samp] = channel_samples[chan][win * window_size + samp];
						else
							windowed_samples[chan][win][samp] = 0;
					}
				}
			}

		return windowed_samples;
	}


	/**
	 * Returns the stored audio samples divided into windows of equal lengths.
	 * The last window is extended past the length of the stored samples if 
	 * necessary and is zero padded.
	 *
	 * @param	window_duration	The duration in seconds of the windows that the
	 *							samples are divided into.
	 * @return					The audio samples stored in this object after
	 *							being broken into equally sized windows of the
	 *							specified size. The last window is zero-padded
	 *							if necessary. The samples have a minimum value
	 *							of -1 and a maximum value of +1. The first indice
	 *							specifies the channel, the second indice specifies
	 *							the window and the third indice specifies the 
	 *							sample index within the window.
	 * @throws	Exception		Throws an exception if a negative or 0 window size
	 *							is specified.
	 */
	public double[][][] getSampleWindowsChannelSegregated(double window_duration)
		throws Exception
	{
		int window_size = convertTimeToSampleIndex(window_duration);
		return getSampleWindowsChannelSegregated(window_size);
	}


	/**
	 * Returns the stored audio samples in the form of an AudioInputStream.
	 *
	 * @return					The entire set of samples returned in the
	 *							form of an AudioInputStream.
	 * @throws	Exception		Throws an exception if a a problem occurs 
	 *							during conversion.
	 */
	public AudioInputStream getAudioInputStreamChannelSegregated()
		throws Exception
	{
		// Extract samples from samples field if only one channel present
		double[][] samples_to_convert = getSamplesChannelSegregated();

		// Convert to an AudioInputStream
		AudioInputStream audio_input_stream
			= AudioMethods.convertToAudioInputStream(samples_to_convert, audio_format);
		
		// Return the resuls
		return audio_input_stream;
	}


	/**
	 * Returns the stored audio samples between the given samples in the form of
	 * an AudioInputStream.
	 *
	 * @param	start_sample	The sample indice of the first sample to return.
	 *                          Must be 0 or higher and must be less than
	 *                          end_sample.
	 * @param	end_sample		The sample indice of the last sample to return.
	 *                          Must be less than the total number of samples.
	 * @return					The specified set of samples returned in the form of an
	 *							of an AudioInputStream.
	 * @throws	Exception		Throws an informative exception if the given
	 *							start sample or end sample are outside acceptable
	 *                          ranges or if error occurs during conversion.
	 */
	public AudioInputStream getAudioInputStreamChannelSegregated(int start_sample, int end_sample)
		throws Exception
	{
		// Extract samples from samples field if only one channel present
		double[][] samples_to_convert = getSamplesChannelSegregated(start_sample, end_sample);

		// Convert to an AudioInputStream
		AudioInputStream audio_input_stream
			= AudioMethods.convertToAudioInputStream(samples_to_convert, audio_format);
		
		// Return the resuls
		return audio_input_stream;
	}


	/**
	 * Returns the stored audio samples between the given times in the form of
	 * an AudioInputStream.
	 *
	 * @param	start_time		The time, in seconds, of the first sample to
	 *                          return. Must be 0 or higher and must be less than
	 *                          end_time.
	 * @param	end_time		The time, in seconds, of the last sample to return.
	 *                          return. Must be less than or equal to the total 
	 *                          duration.
	 * @return					The specified set of samples returned in the form of an
	 *							of an AudioInputStream.
	 * @throws	Exception		Throws an informative exception if the given
	 *							start time or end time are outside acceptable
	 *                          ranges or if error occurs during conversion.
	 */
	public AudioInputStream getAudioInputStreamChannelSegregated(double start_time, double end_time)
		throws Exception
	{
		// Extract samples from samples field if only one channel present
		double[][] samples_to_convert = getSamplesChannelSegregated(start_time, end_time);

		// Convert to an AudioInputStream
		AudioInputStream audio_input_stream
			= AudioMethods.convertToAudioInputStream(samples_to_convert, audio_format);
		
		// Return the resuls
		return audio_input_stream;
	}


	/**
	 * Saves the currently stored samples to the specified file.
	 *
	 * <p><b>WARNING:</b> Will automatically overwrite given file if it already exists.
	 *
	 * @param	save_file				The File to save the audio samples to.
	 * @param	multi_channel			If this is true, then any separate channels are
	 *									saved on separate channels. If this is false,
	 *									then saved file has only one channel, onto which
	 *									all samples have been mixed down to.
	 * @param	save_file_type			The AudioFileFormat.Type to use for saving the audio.
	 *									A default value is used if this is null.
	 * @param	normalize_if_clipped	If set to true, then normalizes audio so the
	 *									absolute value of the highest amplitude sample is 1.
	 *									Does this if and only if one or more of the samples to
	 *									save is outside the allowable range of sample values
	 *									(-1 to 1). If set to false, then does not
	 *									normalize, regardless of sample values. <b>WARNING:</b>
	 *									This will normalize the samples stored in memory as well.
	 * @throws	Exception				Throws an informative exception if the samples cannot
	 *									be succesfully saved to the file.
	 */
	public void saveAudio( File save_file,
						   boolean multi_channel,
	                       AudioFileFormat.Type save_file_type,
						   boolean normalize_if_clipped )
		throws Exception
	{
		// Verify that a file is specified
		if (save_file == null)
			throw new Exception("No file provided to save to.");

		// Normalize the file if requested and if necessary
		if (normalize_if_clipped)
			normalizeIfClipped();

		// Convert samples to an AudioInputStream
		AudioInputStream audio_input_stream = null;
		if (multi_channel)
			audio_input_stream = getAudioInputStreamChannelSegregated();
		else
			audio_input_stream = getAudioInputStreamMixedDown();

		// Save as a wav file if file type not specified
		if (save_file_type == null)
			save_file_type = AudioFileFormat.Type.WAVE;

		// Delete any pre-existing file
		if (save_file.exists())
			save_file.delete();

		// Write the samples to the file
		AudioSystem.write(audio_input_stream, save_file_type, save_file);
	}
	
	
	/**
	 * Checks the mixed down and channel segregated samples separately to see if
	 * either one has values outside of the permitted range (-1 to +1). If so, 
	 * normalizes the appropriate samples so that the absolute value of the
	 * highest amplitude sample is 1.
	 */
	public void normalizeIfClipped()
	{
		if (checkMixedDownSamplesForClipping() > 0.0)
			normalizeMixedDownSamples();
		if (checkChannelSegregatedSamplesForClipping() > 0.0)
			normalizeChannelSegretatedSamples();
	}


	/**
	 * Returns the maximum amplitude value in all of the channels.
	 *
	 * @return	The maximum amplitude (absolute) value in any one channel.
	 */
	public double getMaximumAmplitude()
	{
		double max_amplitude = 0.0;
		if (channel_samples != null)
		{
			for (int chan = 0; chan < channel_samples.length; chan++)
				for (int samp = 0; samp < channel_samples[chan].length; samp++)
					if (Math.abs(channel_samples[chan][samp]) > max_amplitude)
						max_amplitude = Math.abs(channel_samples[chan][samp]);
		}
		else 
		{
			for (int samp = 0; samp < samples.length; samp++)
				if (Math.abs(samples[samp]) > max_amplitude)
					max_amplitude = Math.abs(samples[samp]);
		}
		return max_amplitude;
	}
	
	
	/**
	 * Returns the maximum deviation in the stored mixed down sample values outside 
	 * the permissible range of -1 to +1. Returns -1.0 if all samples fall withing
	 * the permissible range.
	 *
	 * @return	The maximum deviation from the permissible sample values. -1 if
	 *			all sample values fall within the allowable range.
	 */
	public double checkMixedDownSamplesForClipping()
	{
		double max_difference = -1.0;
		for (int samp = 0; samp < samples.length; samp++)
			if (Math.abs(samples[samp]) > 1.0)
			{
				double difference = Math.abs(samples[samp]) - 1.0;
				if (difference > max_difference)
					max_difference = difference;
			}
		return max_difference;
	}
	
	
	/**
	 * Returns the maximum deviation in the stored sample values outside the
	 * permissible range of -1 to +1. Returns -1.0 if all samples fall withing
	 * the permissible range. Does this dependantly over all channels, but not
	 * over the mixed down channels (unless only one channel is present).
	 *
	 * @return	The maximum deviation from the permissible sample values. -1 if
	 *			all sample values fall within the allowable range.
	 */
	public double checkChannelSegregatedSamplesForClipping()
	{
		double max_difference = -1.0;
		if (channel_samples != null)
		{
			for (int chan = 0; chan < channel_samples.length; chan++)
				for (int samp = 0; samp < channel_samples[chan].length; samp++)
					if (Math.abs(channel_samples[chan][samp]) > 1.0)
					{
						double difference = Math.abs(channel_samples[chan][samp]) - 1.0;
						if (difference > max_difference)
							max_difference = difference;
					}
		}
		else 
			max_difference = checkMixedDownSamplesForClipping();
		return max_difference;
	}


	/**
	 * Normalizes the samples mixed down into one channel so that the absolute value
	 * of the highest sample amplitude is 1. Does nothing if all samples are 0.
	 */
	public void normalizeMixedDownSamples()
	{
		samples = DSPMethods.normalizeSamples(samples);
	}


	/**
	 * Normalizes the channel segregated samples so that the absolute value
	 * of the highest sample amplitude is 1. Does nothing if all samples are 0.
	 */
	public void normalizeChannelSegretatedSamples()
	{
		if (channel_samples != null)
			channel_samples = DSPMethods.normalizeSamples(channel_samples);
		else
			normalizeMixedDownSamples();
	}


	/**
	 * Normalizes both the channel segregated samples and the samples mixed
	 * down into one channel so that the absolute value of the highest sample
	 * amplitude is 1. Does nothing if all samples are 0.
	 */
	public void normalize()
	{
		normalizeChannelSegretatedSamples();
		if (channel_samples != null)
			normalizeMixedDownSamples();
	}


	/**
	 * Updates the samples stored in an object of this class. The given new
	 * samples must have the same number of channels as the original data.
	 * The stored samples are a copy of the given samples, so changes to
	 * the passed array will not affect the information stored here.
	 *
	 * @param	new_samples			Audio samles to store, usually with a minimum value
	 *								of -1 and a maximum value of +1. The first indice 
	 *								corresponds to the channel and the second indice
	 *								corresponds to the sample number.
	 * @throws	Exception			Throws an exception if an invalid parameter is
	 *								passed (null entries, non-matching number of channels
	 *								or samples per channel.
	 */
	public void setSamples(double[][] new_samples)
		throws Exception
	{
		// Verify that the given samples are valid
		if (new_samples == null)
			throw new Exception("An empty set of samples provided.");
		int number_samples = -1;
		for (int chan = 0; chan < new_samples.length; chan++)
		{
			if (new_samples[chan] == null)
				throw new Exception("Channel " + chan + " of the given samples is empty.");
			if (number_samples != -1)
				if (number_samples != new_samples[chan].length)
					throw new Exception("Different channels have different numbers of samples.");
			number_samples = new_samples[chan].length;
		}
		
		// Update the samples and channel_samples fields
		if (channel_samples == null)
		{
			if (new_samples.length != 1)
			{
				throw new Exception( "Given samples have " + new_samples.length + " channels.\n" +
				                     "Only one channel should be present." );
			}
			samples = new double[number_samples];
			for (int samp = 0; samp < samples.length; samp++)
				samples[samp] = new_samples[0][samp];
		}
		else
		{
			if (new_samples.length != channel_samples.length)
			{
				throw new Exception( "Given samples have " + new_samples.length + " channels.\n" +
				                     channel_samples.length + " channel should be present." );
			}
			channel_samples = new double[new_samples.length][number_samples];
			for (int chan = 0; chan < channel_samples.length; chan++)
				for (int samp = 0; samp < channel_samples[chan].length; samp++)
					channel_samples[chan][samp] = new_samples[chan][samp];
			samples = DSPMethods.getSamplesMixedDownIntoOneChannel(channel_samples);

		}
	}


	/* PRIVATE METHODS *********************************************************/


	/**
	 * Returns a new <code>AudioFormat</code> with the given sampling rate. The
	 * number of channels is automatically set based on the channel_samples field.
	 *
	 * Big-endian signed linear PCM encoding is used by default, and the default
	 * bit-depth is set to 16 bits.
	 *
	 * @param	sampling_rate	The sampling rate, in Hz, to use for interpereting
	 *							samples.
	 * @return					The default AudioFormat with the specified sampling
	 *							rate.
	 */
	private AudioFormat getDefaultAudioFormat(float sampling_rate)
	{
		int bit_depth = 16;
		boolean signed = true;
		boolean big_endian = true;

		int channels = 1;
		if (channel_samples == null)
			channels = channel_samples.length;

		return new AudioFormat(sampling_rate, bit_depth, channels, signed, big_endian);
	}


	/** 
	 * Returns the given sample index converted into a time in seconds based
	 * on the sampling rate of the stored audio samples. If the given sample
	 * index is negative, then time 0 is returned. If the given sample index
	 * is greate thean the length of the audio, then the time of the last index
	 * is returned.
	 *
	 * @param	sample_index	The sample index to convert to a time value.
	 * @return					The time in seconds corresponding to the given
	 *							sample index, or the nearest time if the 
	 *							given sample index is outside the duration
	 *							of the audio.
	 */
	private double convertSampleIndexToTime(int sample_index)
	{
		if (sample_index < 0)
			sample_index = 0;
		else if (sample_index >= samples.length)
			sample_index = samples.length - 1;
		float time = samples.length / audio_format.getSampleRate();
		return (new Float(time)).doubleValue();
	}


	/** 
	 * Returns the given time in seconds converted into a sample index based
	 * on the sampling rate of the stored audio samples. If the given time
	 * would correspond to a negative sample, then the index of the first
	 * sample is returned. If the given time would correspond to a sample
	 * greater than the length of the audio, then the last index is returned.
	 *
	 * @param	time	The time in seconds to convert to a sample.
	 * @return			The sample corresponding to the given time, or the
	 *					nearest sample if the given time is outside the duration
	 *                  of the audio.
	 */
	private int convertTimeToSampleIndex(double time)
	{
		int sample_index = (int) (time * audio_format.getSampleRate());
		if (sample_index < 0)
			return 0;
		else if (sample_index >= samples.length)
			return samples.length - 1;
		return sample_index;
	}
}