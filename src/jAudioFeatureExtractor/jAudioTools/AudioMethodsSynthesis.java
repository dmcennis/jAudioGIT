/*
 * @(#)AudioMethodsSynthesis.java	1.0	April 5, 2005.
 *
 * Cory McKay
 * McGill Univarsity
 */

package jAudioFeatureExtractor.jAudioTools;

import java.nio.*;
import java.util.Date;
import java.util.Random;
import javax.sound.sampled.AudioFormat;


/**
 * A holder class for general static methods relating to sampled audio. Although
 * all methods can be used individually, the <code>synthesizeAndWriteToBuffer</code>
 * method provides a single way of accessing all of the functionality of the other
 * methods.
 *
 * <p>The methods whose names beginning with "generateSamples" generate synthesized
 * samples in the form of 2-D arrays of doubles, with dimensions of channel and time.
 *
 * <p>Arrays of this type can be turned into arrays of bytes in standard audio formats
 * using the <code>writeSamplesToBuffer</code> method, or indirectly using the
 * <code>synthesizeAndWriteToBuffer</code> method.
 *
 * <p>See the descriptions below for information on other available methods.
 *
 * @author	Cory McKay
 */
public class AudioMethodsSynthesis
{
	/* FIELDS ******************************************************************/


	// Codes for use in the synthesis_type parameter of the synthesizeAndWriteToBuffer method.
	// The getSynthesisTypeCode method can be used to get these codes externally.
	private static final int SINE_WAVE = 1;
	private static final int BASIC_TONE = 2;
	private static final int STEREO_PANNING = 3;
	private static final int STEREO_PINPONG = 4;
	private static final int FM_SWEEP = 5;
	private static final int DECAY_PULSE = 6;
	private static final int WHITE_NOISE = 7;
	

	/* STATIC METHODS ***********************************************************/


	/**
	 * Populates the given buffer with synthesized sound samples using the given
	 * <code>AudioFormat</code>. Alternatively, returns the sample values in the form
	 * of a 2-D array of doubles if the <i>buffer</i> parameter is  null. If the
	 * <i>buffer</i> parameter is not null, then null is returned and the <i>buffer</i>
	 * is filled.
	 *
	 * Uses the provided synthesis type, overall gain, panning and (sometimes) fudamental
	 * to synthesize the audiofrequency.
	 *
	 * <p>The size of the <i>buffer</i> parameter along with the <code>AudioFormat</code>
	 * used determines the duration of the sound to be generated if the <i>buffer</i>
	 * parameter is not null. It is otherwise determined by the <i>duration</i> parameter.
	 *
	 * @param	buffer					The buffer of bytes to write synthesized samples to.
	 *                                  May be null if prefer samples as a 2-D array of 
	 *                                  doubles rather than an array of bytes.
	 * @param	duration				The time in seconds to generate. Is ignored if
	 *                                  the <i>buffer</i> parameter is not null.
	 * @param	audio_format			The <code>AudioFormat</code> to use for encoding
	 *									samples to the <i>buffer</i> parameter. Only
	 *									audio_formats consisting of bit depths of 8
	 *									or 16 bits are accepted, and encoding must consist
	 *									of big endian signed PCM samples.
	 * @param	synthesis_type			The code indicating what type of synthesis is to
	 *									be used to fill the  <i>buffer</i> parameter. These
	 *									codes can be accessed using the 
	 *									<code>getSynthesisTypeCode</code> method.
	 * @param	gain					The overall loudness of the samples.
	 *									This value must be between 0.0 and 1.0, with
	 *									0.0 being silence and 1.0 being maximum amplitude.
	 * @param	panning					The relative strength of the two stereo channels.
	 *									This parameter is ignored in non-stereo cases.
	 *									Value must be between -1.0 and +1.0, with -1.0
	 *									corresponding to full amplitude on the left
	 *									channel and silence on the right, and +1.0
	 *									corresponding to the reverse. A value of 0
	 *									indicates equal balance.
	 * @param	fundamental_frequency	The fundamental frequency of the sound to be 
	 *									synthesiszed. Is ignored for some types of
	 *									synthesis.
	 * @param	max_frac_samp_rate		Used to determine the maximum allowable frequency
	 *									partial that will be permitted to be synthesized.
	 *									This is to avoid aliasing, and generally a value
	 *									of 0.45 is best, with values below 0.5 always
	 *									needed to ensure protection against aliasing.
	 *									The maximum allowable frequency is determined
	 *									by multiplying this value by the sampling rate
	 *									contained in the <i>audio_format</i> paramter.
	 * @param	click_avoid_env_length	The duration in seconds of an envelope applied
	 *                                  at the beginning and end of the synthesized audio
	 *									in order to avoid clicks. Linear attenuation is 
	 *									used for this amount of time on each end.
	 * @return							If the <i>buffer</i> parameter is not null, then
	 *									null is returned. If the <i>buffer</i> parameter
	 *									is null, then something is returned: 
	 *									A 2-D array of doubles whose first indice
	 *									indicates channel and whose second indice
	 *									indicates sample value. In stereo, indice
	 *									0 corresponds to left and 1 to right. All
	 *									samples should fall between -1 and +1.
	 * @throws	Exception				Throws an informative exception if an invalid
	 *									parameter is specified.
	 */
	public static double[][] synthesizeAndWriteToBuffer( byte[] buffer,
	                                                     double duration,
	                                                     AudioFormat audio_format,
	                                                     int synthesis_type,
	                                                     double gain,
	                                                     double panning,
									                     double fundamental_frequency,
	                                                     double max_frac_samp_rate,
	                                                     double click_avoid_env_length )
		throws Exception
	{
		// Throw exception if a null audio_format is specified
		if (audio_format == null)
			throw new Exception("Null audio format provided.");

		// Throw exception if incompatible AudioFormat is given
		if ( (audio_format.getSampleSizeInBits() != 16 && audio_format.getSampleSizeInBits() != 8 )||
		     !audio_format.isBigEndian() ||
		     audio_format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED )
			throw new Exception( "Only 8 or 16 bit signed PCM samples with a big-endian\n" +
			                     "byte order can be generated currently." );

		// Obtain information about the audio encoding to use
		int number_of_channels = audio_format.getChannels();
		float sample_rate = audio_format.getSampleRate();
		int bit_depth = audio_format.getSampleSizeInBits();

		// Obtain information about the number of bytes needed per channel
		int total_number_of_samples_per_channel = 0;
		if (buffer != null)
		{
			int bytes_per_sample = bit_depth / 8;
			int total_number_of_bytes = buffer.length;
			int total_number_of_samples = total_number_of_bytes / bytes_per_sample;
			total_number_of_samples_per_channel = total_number_of_samples / number_of_channels;
		}
		else
			total_number_of_samples_per_channel = (int) (sample_rate * duration);

		// Generate the appropriate sample values for the given synthesis_type,
		// and throw an exception if an invalid type is given.
		double[][] sample_values = null;
		if (synthesis_type == SINE_WAVE)
		{
			sample_values = generateSamplesSineWave( fundamental_frequency,
													 number_of_channels,
													 sample_rate,
			                                         max_frac_samp_rate,
													 total_number_of_samples_per_channel );
		}
		else if (synthesis_type == BASIC_TONE)
		{
			sample_values = generateSamplesBasicTone( fundamental_frequency,
													  number_of_channels,
													  sample_rate,
			                                          max_frac_samp_rate,
													  total_number_of_samples_per_channel );
		}
		else if (synthesis_type == STEREO_PANNING)
		{
			sample_values = generateSamplesStereoPanning( fundamental_frequency,
													      number_of_channels,
												     	  sample_rate,
			                                              max_frac_samp_rate,
													      total_number_of_samples_per_channel );
		}
		else if (synthesis_type == STEREO_PINPONG)
		{
			sample_values = generateSamplesStereoPingpong( fundamental_frequency,
													       number_of_channels,
													       sample_rate,
			                                               max_frac_samp_rate,
													       total_number_of_samples_per_channel );
		}
		else if (synthesis_type == FM_SWEEP)
		{
			sample_values = generateSamplesFMSweep( fundamental_frequency,
											 number_of_channels,
											 sample_rate,
			                                 max_frac_samp_rate,
											 total_number_of_samples_per_channel );
		}
		else if (synthesis_type == DECAY_PULSE)
		{
			sample_values = generateSamplesDecayPulse( fundamental_frequency,
											           number_of_channels,
											           sample_rate,
			                                           max_frac_samp_rate,
												       total_number_of_samples_per_channel );
		}
		else if (synthesis_type == WHITE_NOISE)
		{
			sample_values = generateWhiteNoise( number_of_channels,
											    total_number_of_samples_per_channel );
		}
		else
			throw new Exception("Invalid synthesis type specified.");

		// Apply gain and panning
		applyGainAndPanning(sample_values, gain, panning);

		// Apply click avoidance attenuation envelope
		applyClickAvoidanceAttenuationEnvelope( sample_values, 
		                                        click_avoid_env_length,
		                                        sample_rate );

		// Ensure that all channels have equal numbers of samples. Throw an
		// exception if they do not
		int samples_per_channel = sample_values[0].length;
		for (int chan = 0; chan < sample_values.length; chan++)
			if (sample_values[chan].length != samples_per_channel)
				throw new Exception("Channels do not have equal number of samples.");

		// Write the samples to the buffer using the correct encoding and return
		// null if appropriate
		if (buffer != null)
		{
			writeSamplesToBuffer(sample_values, bit_depth, buffer);
			return null;
		}
		else
			return sample_values;
	}


	/**
	 * Apply an overall gain and panning to the provided set of samples. This set
	 * of samples will be modified by this method. All samples in the provided
	 * set of samples should be between -1 and +1 before processing, and the same
	 * should be true after processing.
	 *
	 * <p>It should be noted that gain and panning do not amplify samples, but
	 * rather attenuate them. The provided samples should already take advantage
	 * of the full available dynamic range (-1 to +1), and a gain of 1 and and a
	 * panning of 0 will maintain this. Other values of gain or panning will
	 * cause attenuation.
	 *
	 * @param	samples_to_modify	A 2-D array of doubles whose first indice
	 *								indicates channel and whose second indice
	 *								indicates sample value. In stereo, indice
	 *								0 corresponds to left and 1 to right. All
	 *								samples should fall between -1 and +1.
	 * @param	gain				The overall gain to apply to the samples.
	 *								This value must be between 0 and 1, with
	 *								0 being silence and 1 being maximum amplitude.
	 * @param	panning				The relative strength of the two stereo channels.
	 *								This parameter is ignored in non-stereo cases.
	 *								Value must be between -1 and +1, with -1
	 *								corresponding to full amplitude on the left
	 *								channel and silence on the right, and +1
	 *								corresponding to the reverse. A value of 0
	 *								indicates a balance, and no attenuation is
	 *								applied to either channel.
	 * @throws	Exception			Throws an exception if an invalid <i>gain</i>
	 *								or <i>panning</i> value is specified, of if
	 *								the <i>samples_to_modify</i> parameter is null
	 *								or contains empty channels.
	 */
	public static void applyGainAndPanning( double[][] samples_to_modify,
	                                        double gain,
	                                        double panning )
		throws Exception
	{
		// Throw exceptions if invalid parameters provided
		if (gain < 0.0 || gain > 1.0)
			throw new Exception( "Gain of " + gain + " specified.\n" +
			                     "This value must be between 0.0 and 1.0." );
		if (panning < -1.0 || panning > 1.0)
			throw new Exception( "Panning of " + panning + " specified.\n" +
			                     "This value must be between -1.0 and 1.0." );
		if (samples_to_modify == null)
			throw new Exception( "Empty set of samples provided." );
		for (int chan = 0; chan < samples_to_modify.length; chan++)
			if (samples_to_modify[chan] == null)
				throw new Exception ("Channel " + chan + " is empty.");

		// Apply gain to all samples equally accross all channels
		for (int chan = 0; chan < samples_to_modify.length; chan++)
			for (int samp = 0; samp < samples_to_modify[chan].length; samp++)
				samples_to_modify[chan][samp] *= gain;

		// Apply panning in the stereo case
		if (samples_to_modify.length == 2 && panning != 0.0)
		{
			// Adjust left channel if panning is to the right
			if (panning > 0.0)
			{
				double left_multiplier = 1.0 - panning;
				for (int samp = 0; samp < samples_to_modify[0].length; samp++)
					samples_to_modify[0][samp] *= left_multiplier;
			}

			// Adjust right channel if panning is to the left
			if (panning < 0.0)
			{
				double right_multiplier = panning + 1.0;
				for (int samp = 0; samp < samples_to_modify[1].length; samp++)
					samples_to_modify[1][samp] *= right_multiplier;
			}
		}
	}



	/**
	 * Applies linear attenuation to either end of the given samples. This is done
	 * in order to eliminate clicks. The attenuation on each side is determined by
	 * the <i>click_avoid_env_length</i> parameter.
	 *
	 * @param	sample_values			A 2-D array of doubles whose first indice
	 *									indicates channel and whose second indice
	 *									indicates sample value. In stereo, indice
	 *									0 corresponds to left and 1 to right. All
	 *									samples should fall between -1 and +1.
	 * @param	click_avoid_env_length	The duration in seconds of the envelope applied
	 *                                  at the beginning and end of the synthesized audio
	 *									in order to avoid clicks.
	 * @param	sample_rate				The sampling rate that was used to encode the
	 *									<i>sample_values</i>.
	 * @throws	Exception				Throws an exception if an invalid parameter
	 *									is passed.
	 */
	 public static void applyClickAvoidanceAttenuationEnvelope( double[][] sample_values, 
	                                                            double click_avoid_env_length,
		                                                        float sample_rate )
		throws Exception
	 {
		// Throw exceptions if parameters are invalid
		if (sample_values == null)
			throw new Exception( "Empty set of samples provided." );
		if (sample_rate <= 0.0F)
			throw new Exception( "Given sample rate is " + sample_rate + " Hz.\n" +
			                     "This value should be greater than zero." );
		if (click_avoid_env_length < 0.0)
			throw new Exception( "Click avoidance envelope length is " + click_avoid_env_length + " seconds.\n" +
			                     "This value should be 0.0 seconds or higher." );
		double duration_of_audio = sample_values[0].length / sample_rate;
		if ( (2.0 * click_avoid_env_length) >= duration_of_audio )
			throw new Exception( "Click avoidance envelope length is " + click_avoid_env_length + " seconds.\n" +
			                     "This would lead to combined envelope lengths longer than the provided audio." );

		// Find the duration in samples of each envelope
		int sample_duration = (int) (click_avoid_env_length * sample_rate);
		
		// Find sample to start and stop envelopes
		int start_sample_1 = 0;
		int end_sample_1 = sample_duration - 1;
		int start_sample_2 = sample_values[0].length - 1 - sample_duration;
		int end_sample_2 = sample_values[0].length - 1;

		// Apply first envelope
		for (int samp = start_sample_1; samp <= end_sample_1; samp++)
		{
			double amplitude_multipler = (double) samp / (double) end_sample_1;
			for (int chan = 0; chan < sample_values.length; chan++)
				sample_values[chan][samp] *= amplitude_multipler;
		}

		// Apply second envelope
		for (int samp = start_sample_2; samp <= end_sample_2; samp++)
		{
			double amplitude_multipler = 1.0 - ((double) (samp - start_sample_2) / (double) (end_sample_2 - start_sample_2));
			for (int chan = 0; chan < sample_values.length; chan++)
				sample_values[chan][samp] *= amplitude_multipler;
		}
	}


	/**
	 * Writes the samples in the <i>sample_values</i> parameter to the <i>buffer</i>
	 * parameter. It is implicit that the caller knows what the sampling rate is
	 * and will be able to use it to correctly interperet the samples stored in the
	 * buffer after writing. Encoding is done using big endian signed PCM samples.
	 *
	 * @param	sample_values		A 2-D array of doubles whose first indice
	 *								indicates channel and whose second indice
	 *								indicates sample value. In stereo, indice
	 *								0 corresponds to left and 1 to right. All
	 *								samples should fall between -1 and +1.
	 * @param	bit_depth			The bit depth to use for encoding the doubles
	 *								stored in <i>samples_to_modify</i>. Only bit
	 *								depths of 8 or 16 bits are accepted.
	 * @param	buffer				The buffer of bytes to write synthesized samples to.
	 */
	public static void writeSamplesToBuffer( double[][] sample_values,
	                                         int bit_depth,
	                                         byte[] buffer )
		throws Exception
	{
		// Throw exceptions for invalid parameters
		if (sample_values == null)
			throw new Exception( "Empty set of samples to write provided." );
		if (bit_depth != 8 && bit_depth != 16)
			throw new Exception( "Bit depth of " + bit_depth + " specified." +
			                     "Only bit depths of 8 or 16 currently accepted." );
		if (buffer == null)
			throw new Exception("Null buffer for storing samples provided.");
		
		// Find the maximum value a sample may have under the current bit depth
		// (assuming signed samples)
		double max_sample_value = AudioMethods.findMaximumSampleValue(bit_depth);

		// Prepare buffer of audio samples to be written to by wrapping it in
		// a ByteBuffer so that bytes may easily be written to it
		ByteBuffer byte_buffer = ByteBuffer.wrap(buffer);

		// Write samples to buffer (by way of byte_buffer)
		// Only works for bit depths of 8 or 16 bits and big endian signed samples
		if (bit_depth == 8)
		{
			for (int samp = 0; samp < sample_values[0].length; samp++)
				for (int chan = 0; chan < sample_values.length; chan++)
				{
					double sample_value = sample_values[chan][samp] * max_sample_value;
					byte_buffer.put( (byte) sample_value );
				}
		}
		else if (bit_depth == 16)
		{
			ShortBuffer short_buffer = byte_buffer.asShortBuffer();
			for (int samp = 0; samp < sample_values[0].length; samp++)
				for (int chan = 0; chan < sample_values.length; chan++)
				{
					double sample_value = sample_values[chan][samp] * max_sample_value;
					short_buffer.put( (short) sample_value );
				}
		}
	}

	
	/**
	 * Returns the code for use in the <i>synthesis_type</i> parameter of the
	 * <code>synthesizeAndWriteToBuffer</code> method that specifies to the given
	 * type of synthesis.
	 *
	 * @param	synthesis_type_name	The name of the type of synthesis to use to
	 *								generate audio.
	 * @return						The code corresponding to the given type of
	 *								synthesis.
	 */
	public static int getSynthesisTypeCode(String synthesis_type_name)
		throws Exception
	{
		if (synthesis_type_name.equals("Sine Wave"))
			return SINE_WAVE;
		else if (synthesis_type_name.equals("Basic Tone"))
			return BASIC_TONE;
		else if (synthesis_type_name.equals("Stereo Panning"))
			return STEREO_PANNING;
		else if (synthesis_type_name.equals("Stereo Pingpong"))
			return STEREO_PINPONG;
		else if (synthesis_type_name.equals("FM Sweep"))
			return FM_SWEEP;
		else if (synthesis_type_name.equals("Decay Pulse"))
			return DECAY_PULSE;
		else if (synthesis_type_name.equals("White Noise"))
			return WHITE_NOISE;
		else
			throw new Exception( "Unknown type of synthesis specified: " + synthesis_type_name + ".\n" +
			                     "Known types of synthesis are:\n" +
			                     "   Sine Wave, Basic Tone, Stereo Panning, Stereo Pingpong\n" +
			                     "   FM Sweep, White Noise and Decay Pulse." );
	}


	/**
	 * Returns the names of the types of synthesis that can be performed.
	 *
	 * @return	An array of strings consisting of the names of available types of
	 *			synthesis.
	 */
	public static String[] getSynthesisNames()
	{
		String[] names = { "Sine Wave", 
		                   "Basic Tone", 
		                   "Stereo Panning",
		                   "Stereo Pingpong",
		                   "FM Sweep",
		                   "Decay Pulse",
		                   "White Noise" };
		return names;
	}


	/**
	 * Generates sample values for a tone consisting of a single sinusoid.
	 * An identical signal is provided to each channel.
	 *
	 * <p>This is an example of a standard sample generating method. All
	 * of the methods of this type implemented in this class produce a 2-D
	 * array of doubles whose first indice corresponds to channel and
	 * whose second incice corresponds to sample value. In mono, there
	 * is only one channel, and in stereo indice 0 indicates left and
	 * indice 1 indicates right. All samples generated by this type of
	 * method should fall between -1 and +1.
	 *
	 * @param	fund_freq				The fundamental frequency of the tone to 
	 *									be generated. 
	 * @param	number_of_channels		The number of channels to generate samples for.
	 * @param	sample_rate				The sampling rate to use for generating samples.
	 * @param	max_frac_samp_rate		Used to determine the maximum allowable frequency
	 *									partial that will be permitted to be synthesized.
	 *									This is to avoid aliasing, and generally a value
	 *									of 0.45 is best, with values below 0.5 always
	 *									needed to ensure protection against aliasing.
	 *									The maximum allowable frequency is determined
	 *									by multiplying this value by the sampling rate
	 *									contained in the <i>sample_rate</i> paramter.
	 * @param	total_samples_per_chan	The total number of samples to generate per channel.
	 *									Combined with <i>sample_rate</i>, this can
	 *									be used to find the time duration of the
	 *									sound to be generated.
	 * @return							A 2-D array of doubles whose first indice
	 *									indicates channel and whose second indice
	 *									indicates sample value. In stereo, indice
	 *									0 corresponds to left and 1 to right. All
	 *									samples fall between -1 and +1.
	 * @throws	Exception				Throws an exception if invalid parameters provided.
	 */
	public static double[][] generateSamplesSineWave( double fund_freq,
	                                                  int number_of_channels,
	                                                  float sample_rate,
	                                                  double max_frac_samp_rate,
	                                                  int total_samples_per_chan )
		throws Exception
	{
		// Throw an exception if an invalid max_frac_samp_rate is passed
		if (max_frac_samp_rate <= 0.0)
			throw new Exception( "Invalid maximum allowable fraction of sampling rate of " + max_frac_samp_rate + " specified.\n" +
			                     "This value must be above 0." );

		// Throw exceptions for invalid fundamental frequencies (avoid aliasing)
		if (fund_freq <= 0.0)
			throw new Exception( "Invalid fundamental frequence of " + fund_freq + " Hz specified.\n" +
			                     "Frequency must be above 0 Hz." );
		if (fund_freq >= (max_frac_samp_rate * sample_rate) )
			throw new Exception( "Invalid fundamental frequency of " + fund_freq + " Hz specified.\n" +
			                     "Frequency must be below " + (max_frac_samp_rate * sample_rate) + " Hz\n" +
			                     "under current settings. This is done in order to avoid aliasing at this\n" +
			                     "sampling rate of " + sample_rate + " Hz for this type of synthesis." );

		// Throw exceptions for invalid number_of_channels, sample_rate or total_samples_per_chan
		if (number_of_channels < 1)
			throw new Exception("There must be 1 or more channels. You specified " + number_of_channels + ".");
		if (sample_rate <= 0.0F)
			throw new Exception( "Invalid sampling rate of " + sample_rate + " Hz specified.\n" +
			                     "Must be greater than 0.");
		if (total_samples_per_chan <= 0)
			throw new Exception( "Invalid total number of samples per channel of " + total_samples_per_chan + " specified.\n" +
			                     "Must be greater than 0.");

		// Prepare the array to hold the samples for each channel
		double[][] samples = new double[number_of_channels][total_samples_per_chan];

		// Generate the samples one by one
		for(int samp = 0; samp < total_samples_per_chan; samp++)
		{
			// Find the time of the current sample
			double time = samp / sample_rate;

			// Add sinusoids to get basic sample value
			double sample_value = ( Math.sin( 2 * Math.PI * fund_freq * time ) );
			
			// Store identical samples on each channel
			for (int chan = 0; chan < samples.length; chan++)
				samples[chan][samp] = sample_value;
		}

		// Return the generated samples
		return samples;
	}

	
	/**
	 * Generates sample values for a tone consisting of a sinusoid at a
	 * fundamental frequency and additional sinusoids at  1.2 and 1.8 times
	 * this frequency. All three sinusoids have equal amplitudes. An
	 * identical signal is provided to each channel.
	 *
	 * <p>This is an example of a standard sample generating method. All
	 * of the methods of this type implemented in this class produce a 2-D
	 * array of doubles whose first indice corresponds to channel and
	 * whose second incice corresponds to sample value. In mono, there
	 * is only one channel, and in stereo indice 0 indicates left and
	 * indice 1 indicates right. All samples generated by this type of
	 * method should fall between -1 and +1.
	 *
	 * @param	fund_freq				The fundamental frequency of the tone to 
	 *									be generated.
	 * @param	number_of_channels		The number of channels to generate samples for.
	 * @param	sample_rate				The sampling rate to use for generating samples.
	 * @param	max_frac_samp_rate		Used to determine the maximum allowable frequency
	 *									partial that will be permitted to be synthesized.
	 *									This is to avoid aliasing, and generally a value
	 *									of 0.45 is best, with values below 0.5 always
	 *									needed to ensure protection against aliasing.
	 *									The maximum allowable frequency is determined
	 *									by multiplying this value by the sampling rate
	 *									contained in the <i>sample_rate</i> paramter.
	 * @param	total_samples_per_chan	The total number of samples to generate per channel.
	 *									Combined with <i>sample_rate</i>, this can
	 *									be used to find the time duration of the
	 *									sound to be generated.
	 * @return							A 2-D array of doubles whose first indice
	 *									indicates channel and whose second indice
	 *									indicates sample value. In stereo, indice
	 *									0 corresponds to left and 1 to right. All
	 *									samples fall between -1 and +1.
	 * @throws	Exception				Throws an exception if invalid parameters provided.
	 */
	public static double[][] generateSamplesBasicTone( double fund_freq,
	                                                   int number_of_channels,
	                                                   float sample_rate,
	                                                   double max_frac_samp_rate,
	                                                   int total_samples_per_chan )
		throws Exception
	{
		// Throw an exception if an invalid max_frac_samp_rate is passed
		if (max_frac_samp_rate <= 0.0)
			throw new Exception( "Invalid maximum allowable fraction of sampling rate of " + max_frac_samp_rate + " specified.\n" +
			                     "This value must be above 0." );

		// Throw exceptions for invalid fundamental frequencies (avoid aliasing)
		if (fund_freq <= 0.0)
			throw new Exception( "Invalid fundamental frequence of " + fund_freq + " Hz specified.\n" +
			                     "Frequency must be above 0 Hz." );
		if (fund_freq >= (max_frac_samp_rate * sample_rate / 1.8) )
			throw new Exception( "Invalid fundamental frequency of " + fund_freq + " Hz specified.\n" +
			                     "Frequency must be below " + (max_frac_samp_rate * sample_rate / 1.8) + " Hz\n" +
			                     "under current settings. This is done in order to avoid aliasing at this\n" +
			                     "sampling rate of " + sample_rate + " Hz for this type of synthesis." );

		// Throw exceptions for invalid number_of_channels, sample_rate or total_samples_per_chan
		if (number_of_channels < 1)
			throw new Exception("There must be 1 or more channels. You specified " + number_of_channels + ".");
		if (sample_rate <= 0.0F)
			throw new Exception( "Invalid sampling rate of " + sample_rate + " Hz specified.\n" +
			                     "Must be greater than 0.");
		if (total_samples_per_chan <= 0)
			throw new Exception( "Invalid total number of samples per channel of " + total_samples_per_chan + " specified.\n" +
			                     "Must be greater than 0.");

		// Prepare the array to hold the samples for each channel
		double[][] samples = new double[number_of_channels][total_samples_per_chan];

		// Generate the samples one by one
		for (int samp = 0; samp < total_samples_per_chan; samp++)
		{
			// Find the time of the current sample
			double time = samp / sample_rate;

			// Add sinusoids to get basic sample value
			double sample_value = ( Math.sin( 2 * Math.PI * fund_freq * time ) +
			                        Math.sin( 2 * Math.PI * (1.2) * fund_freq * time ) +
			                        Math.sin( 2 * Math.PI * (1.8) * fund_freq * time ) );
			
			// Normalize sample values so that they fall between -1 and +1
			sample_value = sample_value / 3.0;

			// Store identical samples on each channel
			for (int chan = 0; chan < samples.length; chan++)
				samples[chan][samp] = sample_value;
		}

		// Return the generated samples
		return samples;
	}


	/**
	 * Generates sample values for a sound consisting of a stereo sweep, starting
	 * with a relatively high frequency sinusoid on the left speaker and moving across 
	 * to a sinusoid one octave lower on the right speaker.
	 *
	 * <p>This is an example of a standard sample generating method. All
	 * of the methods of this type implemented in this class produce a 2-D
	 * array of doubles whose first indice corresponds to channel and
	 * whose second incice corresponds to sample value. In mono, there
	 * is only one channel, and in stereo indice 0 indicates left and
	 * indice 1 indicates right. All samples generated by this type of
	 * method should fall between -1 and +1.
	 *
	 * @param	fund_freq				The frequency of the tone on the left and
	 *									twice the frequency of the tone on the right.
	 * @param	number_of_channels		The number of channels to generate samples for.
	 * @param	sample_rate				The sampling rate to use for generating samples.
	 * @param	max_frac_samp_rate		Used to determine the maximum allowable frequency
	 *									partial that will be permitted to be synthesized.
	 *									This is to avoid aliasing, and generally a value
	 *									of 0.45 is best, with values below 0.5 always
	 *									needed to ensure protection against aliasing.
	 *									The maximum allowable frequency is determined
	 *									by multiplying this value by the sampling rate
	 *									contained in the <i>sample_rate</i> paramter.
	 * @param	total_samples_per_chan	The total number of samples to generate per channel.
	 *									Combined with <i>sample_rate</i>, this can
	 *									be used to find the time duration of the
	 *									sound to be generated.
	 * @return							A 2-D array of doubles whose first indice
	 *									indicates channel and whose second indice
	 *									indicates sample value. In stereo, indice
	 *									0 corresponds to left and 1 to right. All
	 *									samples fall between -1 and +1.
	 * @throws	Exception				Throws an exception if invalid parameters provided.
	 */
	public static double[][] generateSamplesStereoPanning( double fund_freq,
	                                                       int number_of_channels,
	                                                       float sample_rate,
	                                                       double max_frac_samp_rate,
	                                                       int total_samples_per_chan )
		throws Exception
	{
		// Throw an exception if an invalid max_frac_samp_rate is passed
		if (max_frac_samp_rate <= 0.0)
			throw new Exception( "Invalid maximum allowable fraction of sampling rate of " + max_frac_samp_rate + " specified.\n" +
			                     "This value must be above 0." );

		// Throw exceptions for invalid fundamental frequencies (avoid aliasing)
		if (fund_freq <= 0.0)
			throw new Exception( "Invalid fundamental frequence of " + fund_freq + " Hz specified.\n" +
			                     "Frequency must be above 0 Hz." );
		if (fund_freq >= (max_frac_samp_rate * sample_rate / 1.8) )
			throw new Exception( "Invalid fundamental frequency of " + fund_freq + " Hz specified.\n" +
			                     "Frequency must be below " + (max_frac_samp_rate * sample_rate) + " Hz\n" +
			                     "under current settings. This is done in order to avoid aliasing at this\n" +
			                     "sampling rate of " + sample_rate + " Hz for this type of synthesis." );

		// Throw exceptions for invalid number_of_channels, sample_rate or total_samples_per_chan
		if (number_of_channels != 2)
			throw new Exception("There must be 2 channels. You specified " + number_of_channels + ".");
		if (sample_rate <= 0.0F)
			throw new Exception( "Invalid sampling rate of " + sample_rate + " Hz specified.\n" +
			                     "Must be greater than 0.");
		if (total_samples_per_chan <= 0)
			throw new Exception( "Invalid total number of samples per channel of " + total_samples_per_chan + " specified.\n" +
			                     "Must be greater than 0.");

		// Prepare the array to hold the samples for each channel
		double[][] samples = new double[number_of_channels][total_samples_per_chan];

		// Generate the samples one by one
		for (int samp = 0; samp < total_samples_per_chan; samp++)
		{
			// Find the time of the current sample
			double time = samp / sample_rate;

			// Find the time dependant gain for each channel
			double right_gain = (double) samp / (double) total_samples_per_chan;
			double left_gain = 1.0 - right_gain;

			// Add sinusoids to get basic sample value
			double original_left_sample_value = Math.sin( 2 * Math.PI * fund_freq * time );
			double original_right_sample_value = Math.sin( 2 * Math.PI * fund_freq / 2 * time );
			
			// Store the samples on each channel
			// Store identical samples on each channel
			samples[0][samp] = left_gain * original_left_sample_value;
			samples[1][samp] = right_gain * original_right_sample_value;
		}

		// Return the generated samples
		return samples;
	}


	/**
	 * Generates sample values for a sound consisting of a stereo pinpong, where
	 * the signal switches between the four channels four times per second. The
	 * signal on the right is 0.8 times the frequency of the signal on the right.
	 *
	 * <p>This is an example of a standard sample generating method. All
	 * of the methods of this type implemented in this class produce a 2-D
	 * array of doubles whose first indice corresponds to channel and
	 * whose second incice corresponds to sample value. In mono, there
	 * is only one channel, and in stereo indice 0 indicates left and
	 * indice 1 indicates right. All samples generated by this type of
	 * method should fall between -1 and +1.
	 *
	 * @param	fund_freq				The frequency of the tone on the left and
	 *									1.25 times the frequency of the tone on the right.
	 * @param	number_of_channels		The number of channels to generate samples for.
	 * @param	sample_rate				The sampling rate to use for generating samples.
	 * @param	max_frac_samp_rate		Used to determine the maximum allowable frequency
	 *									partial that will be permitted to be synthesized.
	 *									This is to avoid aliasing, and generally a value
	 *									of 0.45 is best, with values below 0.5 always
	 *									needed to ensure protection against aliasing.
	 *									The maximum allowable frequency is determined
	 *									by multiplying this value by the sampling rate
	 *									contained in the <i>sample_rate</i> paramter.
	 * @param	total_samples_per_chan	The total number of samples to generate per channel.
	 *									Combined with <i>sample_rate</i>, this can
	 *									be used to find the time duration of the
	 *									sound to be generated.
	 * @return							A 2-D array of doubles whose first indice
	 *									indicates channel and whose second indice
	 *									indicates sample value. In stereo, indice
	 *									0 corresponds to left and 1 to right. All
	 *									samples fall between -1 and +1.
	 * @throws	Exception				Throws an exception if invalid parameters provided.
	 */
	public static double[][] generateSamplesStereoPingpong( double fund_freq,
	                                                        int number_of_channels,
	                                                        float sample_rate,
	                                                        double max_frac_samp_rate,
	                                                        int total_samples_per_chan )
		throws Exception
	{
		// Throw an exception if an invalid max_frac_samp_rate is passed
		if (max_frac_samp_rate <= 0.0)
			throw new Exception( "Invalid maximum allowable fraction of sampling rate of " + max_frac_samp_rate + " specified.\n" +
			                     "This value must be above 0." );

		// Throw exceptions for invalid fundamental frequencies (avoid aliasing)
		if (fund_freq <= 0.0)
			throw new Exception( "Invalid fundamental frequence of " + fund_freq + " Hz specified.\n" +
			                     "Frequency must be above 0 Hz." );
		if (fund_freq >= (max_frac_samp_rate * sample_rate / 1.8) )
			throw new Exception( "Invalid fundamental frequency of " + fund_freq + " Hz specified.\n" +
			                     "Frequency must be below " + (max_frac_samp_rate * sample_rate) + " Hz\n" +
			                     "under current settings. This is done in order to avoid aliasing at this\n" +
			                     "sampling rate of " + sample_rate + " Hz for this type of synthesis." );

		// Throw exceptions for invalid number_of_channels, sample_rate or total_samples_per_chan
		if (number_of_channels != 2)
			throw new Exception("There must be 2 channels. You specified " + number_of_channels + ".");
		if (sample_rate <= 0.0F)
			throw new Exception( "Invalid sampling rate of " + sample_rate + " Hz specified.\n" +
			                     "Must be greater than 0.");
		if (total_samples_per_chan <= 0)
			throw new Exception( "Invalid total number of samples per channel of " + total_samples_per_chan + " specified.\n" +
			                     "Must be greater than 0.");

		// Prepare the array to hold the samples for each channel
		double[][] samples = new double[number_of_channels][total_samples_per_chan];

		// Generate the samples one by one
		double number_of_times_a_sec_switches_occur = 4.0;
		double switch_time_interval = 1 / number_of_times_a_sec_switches_occur;
		double time_of_last_switch = 0.0;
		double right_gain = 0.0;
		double left_gain = 1.0;
		for (int samp = 0; samp < total_samples_per_chan; samp++)
		{
			// Find the time of the current sample
			double time = samp / sample_rate;

			// Find the time dependant gain for each channel
			if (time - time_of_last_switch > switch_time_interval)
			{
				double temp = left_gain;
				left_gain = right_gain;
				right_gain = temp;
				time_of_last_switch = time;
			}

			// Add sinusoids to get basic sample value
			double original_left_sample_value = Math.sin( 2 * Math.PI * fund_freq * time );
			double original_right_sample_value = Math.sin( 2 * Math.PI * fund_freq * 0.8 * time );
			
			// Store the samples on each channel
			// Store identical samples on each channel
			samples[0][samp] = left_gain * original_left_sample_value;
			samples[1][samp] = right_gain * original_right_sample_value;
		}

		// Return the generated samples
		return samples;
	}


	/**
	 * Generates sample values for a tone consisting of a single sinusoid.
	 * that undergoes a gradual linear frequency increase from 1/10 of the provided
	 * <i>fundamental_frequency</i> at the beginning of the sound to the
	 * <i>fundamental_frequency</i> at the end of the sound. An identical 
	 * signal is provided to each channel.
	 *
	 * <p>This is an example of a standard sample generating method. All
	 * of the methods of this type implemented in this class produce a 2-D
	 * array of doubles whose first indice corresponds to channel and
	 * whose second incice corresponds to sample value. In mono, there
	 * is only one channel, and in stereo indice 0 indicates left and
	 * indice 1 indicates right. All samples generated by this type of
	 * method should fall between -1 and +1.
	 *
	 * @param	fund_freq				The final frequency of the tone to 
	 *									be generated. 
	 * @param	number_of_channels		The number of channels to generate samples for.
	 * @param	sample_rate				The sampling rate to use for generating samples.
	 * @param	max_frac_samp_rate		Used to determine the maximum allowable frequency
	 *									partial that will be permitted to be synthesized.
	 *									This is to avoid aliasing, and generally a value
	 *									of 0.45 is best, with values below 0.5 always
	 *									needed to ensure protection against aliasing.
	 *									The maximum allowable frequency is determined
	 *									by multiplying this value by the sampling rate
	 *									contained in the <i>sample_rate</i> paramter.
	 * @param	total_samples_per_chan	The total number of samples to generate per channel.
	 *									Combined with <i>sample_rate</i>, this can
	 *									be used to find the time duration of the
	 *									sound to be generated.
	 * @return							A 2-D array of doubles whose first indice
	 *									indicates channel and whose second indice
	 *									indicates sample value. In stereo, indice
	 *									0 corresponds to left and 1 to right. All
	 *									samples fall between -1 and +1.
	 * @throws	Exception				Throws an exception if invalid parameters provided.
	 */
	public static double[][] generateSamplesFMSweep( double fund_freq,
	                                                 int number_of_channels,
	                                                 float sample_rate,
	                                                 double max_frac_samp_rate,
	                                                 int total_samples_per_chan )
		throws Exception
	{
		// Throw an exception if an invalid max_frac_samp_rate is passed
		if (max_frac_samp_rate <= 0.0)
			throw new Exception( "Invalid maximum allowable fraction of sampling rate of " + max_frac_samp_rate + " specified.\n" +
			                     "This value must be above 0." );

		// Throw exceptions for invalid fundamental frequencies (avoid aliasing)
		if (fund_freq <= 0.0)
			throw new Exception( "Invalid fundamental frequence of " + fund_freq + " Hz specified.\n" +
			                     "Frequency must be above 0 Hz." );
		if (fund_freq >= (max_frac_samp_rate * sample_rate) )
			throw new Exception( "Invalid fundamental frequency of " + fund_freq + " Hz specified.\n" +
			                     "Frequency must be below " + (max_frac_samp_rate * sample_rate) + " Hz\n" +
			                     "under current settings. This is done in order to avoid aliasing at this\n" +
			                     "sampling rate of " + sample_rate + " Hz for this type of synthesis." );

		// Throw exceptions for invalid number_of_channels, sample_rate or total_samples_per_chan
		if (number_of_channels < 1)
			throw new Exception("There must be 1 or more channels. You specified " + number_of_channels + ".");
		if (sample_rate <= 0.0F)
			throw new Exception( "Invalid sampling rate of " + sample_rate + " Hz specified.\n" +
			                     "Must be greater than 0.");
		if (total_samples_per_chan <= 0)
			throw new Exception( "Invalid total number of samples per channel of " + total_samples_per_chan + " specified.\n" +
			                     "Must be greater than 0.");

		// Prepare the array to hold the samples for each channel
		double[][] samples = new double[number_of_channels][total_samples_per_chan];

		// Set the frequencies
		double high_freq = fund_freq;
		double low_freq = high_freq / 10.0;

		// Generate the samples one by one
		for(int samp = 0; samp < total_samples_per_chan; samp++)
		{
			// Find the time of the current sample
			double time = samp / sample_rate;

			// Find the fraction of the synthesis that has been completed
			double fraction_done = (double) samp / (double) total_samples_per_chan;

			// Find the time dependant frequency
			double freq = low_freq + (high_freq - low_freq) * fraction_done;

			// Add sinusoids to get basic sample value
			double sample_value = ( Math.sin( 2 * Math.PI * freq * time ) );
			
			// Store identical samples on each channel
			for (int chan = 0; chan < samples.length; chan++)
				samples[chan][samp] = sample_value;
		}

		// Return the generated samples
		return samples;
	}

	
	/**
	 * Generates sample values for a tone consisting of a single sinusoid
	 * that decays linearly to arrive at silence at the end of the audio.
	 * An identical signal is provided to each channel.
	 *
	 * <p>This is an example of a standard sample generating method. All
	 * of the methods of this type implemented in this class produce a 2-D
	 * array of doubles whose first indice corresponds to channel and
	 * whose second incice corresponds to sample value. In mono, there
	 * is only one channel, and in stereo indice 0 indicates left and
	 * indice 1 indicates right. All samples generated by this type of
	 * method should fall between -1 and +1.
	 *
	 * @param	fund_freq				The fundamental frequency of the tone to 
	 *									be generated. 
	 * @param	number_of_channels		The number of channels to generate samples for.
	 * @param	sample_rate				The sampling rate to use for generating samples.
	 * @param	max_frac_samp_rate		Used to determine the maximum allowable frequency
	 *									partial that will be permitted to be synthesized.
	 *									This is to avoid aliasing, and generally a value
	 *									of 0.45 is best, with values below 0.5 always
	 *									needed to ensure protection against aliasing.
	 *									The maximum allowable frequency is determined
	 *									by multiplying this value by the sampling rate
	 *									contained in the <i>sample_rate</i> paramter.
	 * @param	total_samples_per_chan	The total number of samples to generate per channel.
	 *									Combined with <i>sample_rate</i>, this can
	 *									be used to find the time duration of the
	 *									sound to be generated.
	 * @return							A 2-D array of doubles whose first indice
	 *									indicates channel and whose second indice
	 *									indicates sample value. In stereo, indice
	 *									0 corresponds to left and 1 to right. All
	 *									samples fall between -1 and +1.
	 * @throws	Exception				Throws an exception if invalid parameters provided.
	 */
	public static double[][] generateSamplesDecayPulse( double fund_freq,
	                                                    int number_of_channels,
	                                                    float sample_rate,
	                                                    double max_frac_samp_rate,
	                                                    int total_samples_per_chan )
		throws Exception
	{
		// Throw an exception if an invalid max_frac_samp_rate is passed
		if (max_frac_samp_rate <= 0.0)
			throw new Exception( "Invalid maximum allowable fraction of sampling rate of " + max_frac_samp_rate + " specified.\n" +
			                     "This value must be above 0." );

		// Throw exceptions for invalid fundamental frequencies (avoid aliasing)
		if (fund_freq <= 0.0)
			throw new Exception( "Invalid fundamental frequence of " + fund_freq + " Hz specified.\n" +
			                     "Frequency must be above 0 Hz." );
		if (fund_freq >= (max_frac_samp_rate * sample_rate) )
			throw new Exception( "Invalid fundamental frequency of " + fund_freq + " Hz specified.\n" +
			                     "Frequency must be below " + (max_frac_samp_rate * sample_rate) + " Hz\n" +
			                     "under current settings. This is done in order to avoid aliasing at this\n" +
			                     "sampling rate of " + sample_rate + " Hz for this type of synthesis." );

		// Throw exceptions for invalid number_of_channels, sample_rate or total_samples_per_chan
		if (number_of_channels < 1)
			throw new Exception("There must be 1 or more channels. You specified " + number_of_channels + ".");
		if (sample_rate <= 0.0F)
			throw new Exception( "Invalid sampling rate of " + sample_rate + " Hz specified.\n" +
			                     "Must be greater than 0.");
		if (total_samples_per_chan <= 0)
			throw new Exception( "Invalid total number of samples per channel of " + total_samples_per_chan + " specified.\n" +
			                     "Must be greater than 0.");

		// Prepare the array to hold the samples for each channel
		double[][] samples = new double[number_of_channels][total_samples_per_chan];

		// Generate the samples one by one
		for(int samp = 0; samp < total_samples_per_chan; samp++)
		{
			// Find the time of the current sample
			double time = samp / sample_rate;

			// Find the amplitude coefficient
			double fraction_done = (double) samp / (double) total_samples_per_chan;
			double amplitude_coef = 1.0 - fraction_done;

			// Add sinusoids to get basic sample value
			double sample_value = amplitude_coef * ( Math.sin( 2 * Math.PI * fund_freq * time ) );
			
			// Store identical samples on each channel
			for (int chan = 0; chan < samples.length; chan++)
				samples[chan][samp] = sample_value;
		}

		// Return the generated samples
		return samples;
	}


	/**
	 * Generates sample values for an audio wave consisting of randomly
	 * generated white noise.
	 *
	 * <p>This is an example of a standard sample generating method. All
	 * of the methods of this type implemented in this class produce a 2-D
	 * array of doubles whose first indice corresponds to channel and
	 * whose second incice corresponds to sample value. In mono, there
	 * is only one channel, and in stereo indice 0 indicates left and
	 * indice 1 indicates right. All samples generated by this type of
	 * method should fall between -1 and +1.
	 *
	 * @param	number_of_channels		The number of channels to generate samples for.
	 * @param	total_samples_per_chan	The total number of samples to generate per channel.
	 * @return							A 2-D array of doubles whose first indice
	 *									indicates channel and whose second indice
	 *									indicates sample value. In stereo, indice
	 *									0 corresponds to left and 1 to right. All
	 *									samples fall between -1 and +1.
	 * @throws	Exception				Throws an exception if invalid parameters provided.
	 */
	public static double[][] generateWhiteNoise(int number_of_channels,
	                                            int total_samples_per_chan )
		throws Exception
	{
		// Throw exceptions for invalid number_of_channels or total_samples_per_chan
		if (number_of_channels < 1)
			throw new Exception("There must be 1 or more channels. You specified " + number_of_channels + ".");
		if (total_samples_per_chan <= 0)
			throw new Exception( "Invalid total number of samples per channel of " + total_samples_per_chan + " specified.\n" +
			                     "Must be greater than 0.");

		// Prepare the array to hold the samples for each channel
		double[][] samples = new double[number_of_channels][total_samples_per_chan];

		// Seed the random number generator
		Random generator = new Random(new Date().getTime());

		// Generate the samples one by one
		for (int samp = 0; samp < total_samples_per_chan; samp++)
			for (int chan = 0; chan < number_of_channels; chan++)
				samples[chan][samp] = (2.0 * generator.nextDouble()) - 1.0;

		// Return the generated samples
		return samples;
	}
}