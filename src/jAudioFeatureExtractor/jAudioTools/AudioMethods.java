/*
 * @(#)AudioMethods.java	1.03	May 29, 2005.
 *
 * Cory McKay
 * McGill Univarsity
 */

package jAudioFeatureExtractor.jAudioTools;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;


/**
 * A holder class for general static methods relating to sampled audio involving
 * classes used in the javax.sound.sampled package. Also includes methods for use
 * in converting back an forth between audio stored using this package and audio
 * stored as samples in arrays of doubles.
 *
 * @author	Cory McKay
 */
public class AudioMethods 
{
	/**
	 * Returns a copy of the given AudioFormat.
	 *
	 * @param	old_audio_format	The AudioFormat to copy.
	 * @return						A copy of old_audio_format.
	 */
	public static AudioFormat getCopyOfAudioFormat(AudioFormat old_audio_format)
	{
		return new AudioFormat( old_audio_format.getEncoding(),
								old_audio_format.getSampleRate(),
								old_audio_format.getSampleSizeInBits(),
								old_audio_format.getChannels(),
								old_audio_format.getFrameSize(),
								old_audio_format.getFrameRate(),
								old_audio_format.isBigEndian() );
	}


	/**
	 * Returns information regarding a given <code>AudioFormat</code>.
	 * This information consists of mixer index, name, version, vendor and description, 
	 * in that order. A list of any additional property names are also included.
	 * The <code>getProperty</code> method of the <code>AudioFormat</code> class may
	 * then be used to access the values of these properties using the key listed here.
	 * Three common property keys are bitrate, vbr and quality (see the
	 * <code>AudioFormat</code> API). These may not be visible, depending on whether 
	 * they were encoded and on whether the file reader used can see them.
	 *
	 * @param	audio_format	The <code>AudioFormat</code> to return data about.
	 * @return					Data in string form about the specified <code>AudioFormat</code>.
	 */
	public static String getAudioFormatData(AudioFormat audio_format)
	{
		String encoding = audio_format.getEncoding().toString() + "\n";
		String endian;
		if (audio_format.isBigEndian())
			endian = "big-endian\n";
		else
			endian = "little-endian\n";
		String sampling_rate = (audio_format.getSampleRate() / 1000) + " kHz\n";
		String bit_depth = audio_format.getSampleSizeInBits() + " bits\n";
		String channels;
		if (audio_format.getChannels() == 1)
			channels = "mono\n";
		else if (audio_format.getChannels() == 2)
			channels = "stereo\n";
		else
			channels = audio_format.getChannels() + " channels\n";
		String frame_size = (8 * audio_format.getFrameSize()) + " bits\n";
		String frame_rate = audio_format.getFrameRate() + " frames per second\n";
		String additional_properties = audio_format.properties() + "\n";

		String data = new String();
		data += new String("SAMPLING RATE: " + sampling_rate);
		data += new String("BIT DEPTH: " + bit_depth);
		data += new String("CHANNELS: " + channels);
		data += new String("FRAME SIZE: " + frame_size);
		data += new String("FRAME RATE: " + frame_rate);
		data += new String("ENCODING: " + encoding);
		data += new String("BYTE ORDER: " + endian);
		data += new String("PROPERTIES: " + additional_properties);
		return data;
	}
	

	/**
	 * Returns information regarding a given audio file.
	 * This information consists of the file name, its type, its size and the length of its
	 * audio data in KB and in seconds. A list of any additional property names are also included.
	 * The <code>getProperty</code> method of the <code>AudioFileFormat</code> class may
	 * then be used to access the values of these properties using the key listed here.
	 * Six common property keys are duration, author, title, copyright, date and comment.
	 * These may not be visible, depending on whether they were encoded and on whether
	 * the file reader used can see them. The <code>AudioFormat</code> properties are also
	 * returned (see the <code>getAudioFormatData</code> method of this class).
	 *
	 * @param	file		The file to return data about.
	 * @return				Data in string form about the specified <code>AudioFormat</code>.
	 * @throws	Exception	Throws informative exceptions if the file is invalid or has
	 *						an unsupported format.
	 */
	public static String getAudioFileFormatData(File file)
		throws Exception
	{
		try
		{
			AudioFileFormat audio_file_format = AudioSystem.getAudioFileFormat(file);
			
			String file_name = file.getName() + "\n";
			String file_type = audio_file_format.getType().toString() + "\n";
			String file_size = (audio_file_format.getByteLength() / 1024) + " kilobytes\n";
			String length_of_audio_data = audio_file_format.getFrameLength() + " sample frames\n";
			String time_duration = audio_file_format.getFrameLength() / audio_file_format.getFormat().getFrameRate() + " seconds\n";
			String additional_properties = audio_file_format.properties() + "\n";

			String data = new String();
			data += new String("FILE NAME: " + file_name);
			data += new String("FILE TYPE: " + file_type);
			data += new String("FILE SIZE: " + file_size);
			data += new String("FRAMES OF AUDIO DATA: " + length_of_audio_data);
			data += new String("TIME DURATION: " + time_duration);
			data += new String("PROPERTIES: " + additional_properties);

			data += "\n" + getAudioFormatData(audio_file_format.getFormat());

			return data;
		}
		catch (UnsupportedAudioFileException ex)
		{
			throw new Exception("File " + file.getName() + " has an unsupported audio format.");
		}
		catch (IOException ex)
		{
			throw new Exception("File " + file.getName() + " is not readable.");
		}
	}


	/**
	 * Returns the names of file types for which file writing support is
	 * provided by the system. Provided in reverse order of how the
	 * <code>AudioSystem.getAudioFileTypes</code> method returns them.
	 *
	 * @return	The names of system supported audio file formats.
	 */
	public static String[] getAvailableFileFormatTypes()
	{
		AudioFileFormat.Type[] file_types = AudioSystem.getAudioFileTypes();
		String file_type_labels[] = new String[file_types.length];
		for (int i = 0; i < file_types.length; i++)
			file_type_labels[i] = file_types[file_types.length - 1 - i].toString();
		return file_type_labels;
	}

	
	/**
	 * Returns the appropriate <code>AudioFileFormat.Type</code> corresponding to
	 * the given <code>String</code>.
	 *
	 * <p><b>WARNING</b>: Future additions to the Java SDK may make further formats
	 * available that are not accounted for here.
	 *
	 * @param	file_type_name	The name of the format type desired.
	 * @return					The <code>AudioFileFormat.Type</code> corresponding to
	 *							the given <i>file_type_name</i>. Returns null if the
	 *							<i>file_type_name</i> does not correspond to any known
	 *							<code>AudioFileFormat.Type</code>.
	 */
	public static AudioFileFormat.Type getAudioFileFormatType(String file_type_name)
	{
		if (file_type_name.equals("WAVE"))
			return AudioFileFormat.Type.WAVE;
		else if (file_type_name.equals("AIFF"))
			return AudioFileFormat.Type.AIFF;
		else if (file_type_name.equals("AIFC"))
			return AudioFileFormat.Type.AIFC;
		else if (file_type_name.equals("AU"))
			return AudioFileFormat.Type.AU;
		else if (file_type_name.equals("SND"))
			return AudioFileFormat.Type.SND;
		else
			return null;
	}

	
	/**
	 * Returns information in <code>String</code> form regarding all available system mixers.
	 * This information consists of mixer index, name, version, vendor and description, 
	 * in that order.
	 */
	public static String getAvailableMixerData()
	{
		Mixer.Info[] mixer_info = AudioSystem.getMixerInfo();
		String data = new String();
		for(int i = 0; i < mixer_info.length; i++)
		{
			data += new String("INDEX: " + i + "\n");
			data += new String("NAME: " + mixer_info[i].getName()) + "\n";
      		data += new String("VERSION: " + mixer_info[i].getVersion()) + "\n";
      		data += new String("VENDOR: " + mixer_info[i].getVendor()) + "\n";
      		data += new String("DESCRIPTION: " + mixer_info[i].getDescription()) + "\n";
			data += new String("\n");
		}
		return data;
	}
	

	/**
	 * Returns the <code>Mixer</code> object with the specified index.
	 * The mixers corresponding to the given index may be obtained by
	 * calling the <code>printAvailableMixers</code> method.
	 *
	 * <p>If the <i>listener</i> parameter is not null, then open, start, stop and close
	 * events will be sent to the passed listener. If it is null, then the line is
	 * returned without any attached listener.
	 *
	 * <p><b>WARNING:</b> The <code>Mixer</code> correxponding to particular
	 * indices will vary from system to system.
	 *
	 * @param	mixer_index	The index (on the current system) of the mixer to be returned.
	 * @param	listener	Responds to open, start, stop and close events on the
	 *                      returned line. May be null.
	 * @return				The specified <code>Mixer</code>.
	 */
	public static Mixer getMixer( int mixer_index,
                                  AudioEventLineListener listener )
	{
		Mixer.Info[] mixer_info = AudioSystem.getMixerInfo();
		Mixer mixer = AudioSystem.getMixer( mixer_info[mixer_index] );
		if (listener != null)
			mixer.addLineListener(listener);
		return mixer;
	}


	/**
	 * Returns a <code>TargetDataLine</code> that can be used for purposes such as
	 * recording from a mic. This line will correspond to a system-specified mixer.
	 * This line will be opened and started, and will therefore be recording data when
	 * it is returned.
	 *
	 * <p>If the <i>listener</i> parameter is not null, then open, start, stop and close
	 * events will be sent to the passed listener. If it is null, then the line is
	 * returned without any attached listener.
	 *
	 * <b>WARNING:</b> The <code>TargetDataLine</code> opened here will begin capturing data
	 * and storing it in an internal a buffer. The user must be sure to take data from this line
	 * quickly enough that its buffer does not overflow, with the result that data is lost.
	 *
	 * @param	audio_format	The audio format to be used by the <code>TargetDataLine</code>
	 * @param	listener		Responds to open, start, stop and close events on the
	 *                          returned line. May be null.
	 * @return					A <code>TargetDataLine</code> that can be used for purposes
	 *                          such as recording from a mic.
	 * @throws	Exception		Throws an exception if cannot get a valid <code>TargetDataLine</code>.
	 */
	public static TargetDataLine getTargetDataLine( AudioFormat audio_format,
	                                                AudioEventLineListener listener )
		throws Exception
	{
		DataLine.Info data_line_info = new DataLine.Info(TargetDataLine.class, audio_format);
		TargetDataLine target_data_line = null;
		target_data_line = (TargetDataLine) AudioSystem.getLine(data_line_info);
		if (listener != null)
			target_data_line.addLineListener(listener);
		target_data_line.open(audio_format);
		target_data_line.start();
		return target_data_line;
	}

	
	/**
	 * Returns a <code>TargetDataLine</code> that can be used for purposes such as
	 * recording from a mic. This line will correspond to the specified <code>Mixer</code>.
	 * This line will be opened and started, and will therefore be recording data when
	 * it is returned.
	 *
	 * <p>If the <i>listener</i> parameter is not null, then open, start, stop and close
	 * events will be sent to the passed listener. If it is null, then the line is
	 * returned without any attached listener.
	 *
	 * <b>WARNING:</b> The <code>TargetDataLine</code> opened here will begin capturing data
	 * and storing it in an internal buffer. The user must be sure to take data from this line
	 * quickly enough that its buffer does not overflow, and data is lost.
	 *
	 * @param	audio_format	The audio format to be used by the <code>TargetDataLine</code>
	 * @param	mixer			The <code>Mixer</code> corresponding to the <code>TargetDataLine</code>.
	 * @param	listener		Responds to open, start, stop and close events on the
	 *                          returned line. May be null.
	 * @return					A <code>TargetDataLine</code> that can be used for purposes
	 *                          such as recording from a mic.
	 * @throws	Exception		Throws an exception if cannot get a valid <code>TargetDataLine</code>.
	 */
	public static TargetDataLine getTargetDataLine( AudioFormat audio_format,
	                                                Mixer mixer,
	                                                AudioEventLineListener listener )
		throws Exception
	{
		DataLine.Info data_line_info = new DataLine.Info(TargetDataLine.class, audio_format);
		TargetDataLine target_data_line = null;
		target_data_line = (TargetDataLine) mixer.getLine(data_line_info);
		if (listener != null)
			target_data_line.addLineListener(listener);
		target_data_line.open(audio_format);
		target_data_line.start();
		return target_data_line;
	}


	/**
	 * Gets a <code>SourceDataLine</code> that can be used for purposes such as
	 * writing directly to a speaker. This line will correspond to a system-specified mixer.
	 * This line will be opened and started, and will therefore be ready to write data when
	 * it is returned.
	 *
	 * <p>If the <i>listener</i> parameter is not null, then open, start, stop and close
	 * events will be sent to the passed listener. If it is null, then the line is
	 * returned without any attached listener.
	 *
	 * @param	audio_format	The audio format to be used by the <code>SourceDataLine</code>.
	 * @param	listener		Responds to open, start, stop and close events on the
	 *                          returned line. May be null.
	 * @return					A <code>SourceDataLine</code> that can be used for purposes
	 *                          such as writing directly to a speaker.
	 */
	public static SourceDataLine getSourceDataLine( AudioFormat audio_format,
	                                                AudioEventLineListener listener )
	{
		SourceDataLine source_data_line = null;
		DataLine.Info data_line_info = new DataLine.Info(SourceDataLine.class, audio_format);
		try
		{
			source_data_line = (SourceDataLine) AudioSystem.getLine(data_line_info);
			if (listener != null)
				source_data_line.addLineListener(listener);
			source_data_line.open(audio_format);
		}
		catch (LineUnavailableException e)
		{
			System.out.println(e);
			System.exit(0);
		}
		source_data_line.start();
		return source_data_line;
	}


	/**
	 * Generates an <code>AudioInputStream</code> based on the given
	 * <code>ByteArrayOutputStream</code> and the given <code>AudioFormat</code>.
	 *
	 * <p>It is important to note that the <code>ByteArrayOutputStream</code> is turned
	 * into a fixed size array before playback, so additional information should not be
	 * added to it once this mehtod is called. This method is not intended for 
	 * real-time streamed data, but rather pre-recorded data.
	 *
	 * <p>In general, the <code>AudioFormat</code> used for the <code>AudioInputStream</code>
	 * and the <code>ByteArrayOutputStream</code> should be the same.
	 *
	 * @param	byte_stream			The audio bytes to be played.
	 * @param	audio_format		Ther <code>AudioFormat</code> to use for encoding the
	 *                              <code>AudioInputStream</code>. Should also correspond
	 *                              to the <code>AudioFormat</code> of the bytes of the
	 *                              <code>ByteArrayOutputStream</code>.
	 * @return						The <code>AudioInputStream</code> corresponding to the
	 *                              given samples and the given <code>AudioFormat</code>.
	 */
	public static AudioInputStream getInputStream( ByteArrayOutputStream byte_stream, 
	                                               AudioFormat audio_format )
	{
		byte audio_bytes[] = byte_stream.toByteArray();
		InputStream input_byte_stream = new ByteArrayInputStream(audio_bytes);
		long number_of_sample_frames = audio_bytes.length / audio_format.getFrameSize();
		AudioInputStream audio_input_stream = new AudioInputStream( input_byte_stream,
		                                                            audio_format,
		                                                            number_of_sample_frames );
		return audio_input_stream;
	}


	/**
	 * Generates an <code>AudioInputStream</code> based on the given
	 * array of bytes and the given <code>AudioFormat</code>. Because of the fixed 
	 * size of the bytes array, this method is not intended for real-time streamed
	 * data, but rather pre-recorded data.
	 *
	 * <p>In general, the <code>AudioFormat</code> used for the <code>AudioInputStream</code>
	 * and the array of bytes should be the same.
	 *
	 * @param	audio_bytes			The audio bytes to be played.
	 * @param	audio_format		Ther <code>AudioFormat</code> to use for encoding the
	 *                              <code>AudioInputStream</code>. Should also correspond
	 *                              to the <code>AudioFormat</code> of <i>audio_bytes</i>.
	 * @return						The <code>AudioInputStream</code> corresponding to the
	 *                              given samples and the given <code>AudioFormat</code>.
	 */
	public static AudioInputStream getInputStream( byte[] audio_bytes, 
	                                               AudioFormat audio_format )
	{
		InputStream input_byte_stream = new ByteArrayInputStream(audio_bytes);
		long number_of_sample_frames = audio_bytes.length / audio_format.getFrameSize();
		AudioInputStream audio_input_stream = new AudioInputStream( input_byte_stream,
		                                                            audio_format,
		                                                            number_of_sample_frames );
		return audio_input_stream;
	}


	/**
	 * Generates an <code>AudioInputStream</code> based on the contents of the given
	 * <code>File</code>.
	 *
	 * @param	audio_file	The audio file to extract the <code>AudioInputStream</code> from.
	 * @return				The <code>AudioInputStream</code> extracted from the specified file.
	 * @throws	Exception	Throws informative exceptions if the file is invalid or has
	 *                      an unsupported file format.
	 */
	public static AudioInputStream getInputStream( File audio_file )
		throws Exception
	{
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
		return audio_input_stream;
	}


	/**
	 * Returns an AudioFormat with the same sampling rate and number of channels
	 * as the passed AudioFormat. If the bit depth is something other than 8 or 16
	 * bits, then it is converted to 16 bits. The returned AudioFormat, also,
	 * will use big-endian signed linear PCM encoding, regardless of the passed
	 * format.
	 *
	 * @param	original_format	The format from which to extract sampling rate,
	 *							bit depth and number of channels.
	 * @return					The new format using big-endian signed linear
	 *							PCM encoding and either 8 or 16 bit bit depth.
	 */
	public static AudioFormat getConvertedAudioFormat(AudioFormat original_format)
	{
		int bit_depth = original_format.getSampleSizeInBits();
		if (bit_depth != 8 && bit_depth !=16)
			bit_depth = 16;
		return new AudioFormat( AudioFormat.Encoding.PCM_SIGNED,
								original_format.getSampleRate(),
								bit_depth,
								original_format.getChannels(),
								original_format.getChannels() * (bit_depth / 8),
								original_format.getSampleRate(),
								true );
	}


	/**
	 * Returns a copy of the given AudioInputStream that uses big-endian signed
	 * linear PCM encoding, regardless of the original encoding. The bit depth is
	 * converted to 16 bits if it is not 8 or 16 bits.
	 *
	 * @param	audio_input_stream	The AudioInputStream to convert to a new encoding.
	 * @return						A copy of the passed AudioInputStream  with the
	 *								new encoding.
	 */
	public static AudioInputStream getConvertedAudioStream(AudioInputStream audio_input_stream)
	{
		audio_input_stream = convertUnsupportedFormat(audio_input_stream);
		AudioFormat original_audio_format = audio_input_stream.getFormat();
		AudioFormat new_audio_format = getConvertedAudioFormat(original_audio_format);
		if (!new_audio_format.matches(original_audio_format))
			audio_input_stream = AudioSystem.getAudioInputStream(new_audio_format, audio_input_stream);
		return audio_input_stream;
	}


	/**
	 * Takes the given AudioInputStream and tests if it is playable. If not, converts
	 * it to big-Endian signed 16 bit linear PCM with the original sampling rate
	 * and number of channels. Useful for dealing with formats such as MP3s.
	 *
	 * @param	audio_input_stream	The audio data to test and possibly convert.
	 * @return						The original AudioInputStream if it is playable,
	 *								and a converted AudioInputStream if it is not.
	 */
	public static AudioInputStream convertUnsupportedFormat(AudioInputStream audio_input_stream)
	{
		DataLine.Info info = new DataLine.Info( SourceDataLine.class,
												audio_input_stream.getFormat() );
		if (!AudioSystem.isLineSupported(info))
		{
			AudioFormat	original_format = audio_input_stream.getFormat();
			int bit_depth = 16;
			AudioFormat	new_audio_format = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED,
				                                            original_format.getSampleRate(),
															bit_depth,
															original_format.getChannels(),
				                                            original_format.getChannels() * (bit_depth / 8),
															original_format.getSampleRate(),
															true );
			audio_input_stream = AudioSystem.getAudioInputStream( new_audio_format,
																  audio_input_stream );
		}
		return audio_input_stream;
	}			


	/**
	 * Returns an array of doubles representing the samples for each channel
	 * in the given AudioInputStream.
	 *
	 * <p>This method is only compatible with audio with bit depths of 8 or 16 bits
	 * that is encoded using signed PCM with big endian byte order.
	 *
	 * @param	audio_input_stream	The AudioInputStream to convert to sample values.
	 * @return						A 2-D array of sample values whose first indice indicates
	 *								channel and whose second indice indicates sample number.
	 *								In stereo, indice 0 corresponds to left and 1 to right.
	 *								All samples should fall between -1 and +1.
	 * @throws	Exception			Throws an informative exception if an invalid paramter 
	 *								is provided.
	 */
	public static double[][] extractSampleValues(AudioInputStream audio_input_stream)
		throws Exception
	{
		// Converts the contents of audio_input_stream into an array of bytes 
		byte[] audio_bytes = getBytesFromAudioInputStream(audio_input_stream);
		int number_bytes = audio_bytes.length;

		// Note the AudioFormat
		AudioFormat this_audio_format = audio_input_stream.getFormat();

		// Extract information from this_audio_format
		int number_of_channels = this_audio_format.getChannels();
		int bit_depth = this_audio_format.getSampleSizeInBits();

		// Throw exception if incompatible this_audio_format provided
		if ( (bit_depth != 16 && bit_depth != 8 )||
		     !this_audio_format.isBigEndian() ||
		     this_audio_format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED )
			throw new Exception( "Only 8 or 16 bit signed PCM samples with a big-endian\n" +
			                     "byte order can be analyzed currently." );

		// Find the number of samples in the audio_bytes
		int number_of_bytes = audio_bytes.length;
		int bytes_per_sample = bit_depth / 8;
		int number_samples = number_of_bytes / bytes_per_sample / number_of_channels;

		// Throw exception if incorrect number of bytes given
		if ( ((number_samples == 2 || bytes_per_sample == 2) && (number_of_bytes % 2 != 0)) ||
		     ((number_samples == 2 && bytes_per_sample == 2) && (number_of_bytes % 4 != 0)) )
			throw new Exception("Uneven number of bytes for given bit depth and number of channels.");
		
		// Find the maximum possible value that a sample may have with the given
		// bit depth
		double max_sample_value = AudioMethods.findMaximumSampleValue(bit_depth) + 2.0;

		// Instantiate the sample value holder
		double[][] sample_values = new double[number_of_channels][number_samples];

		// Convert the bytes to double samples
		ByteBuffer byte_buffer = ByteBuffer.wrap(audio_bytes);
		if (bit_depth == 8)
		{
			for (int samp = 0; samp < number_samples; samp++)
				for (int chan = 0; chan < number_of_channels; chan++)
					sample_values[chan][samp] = (double) byte_buffer.get() / max_sample_value;
		}
		else if (bit_depth == 16)
		{
			ShortBuffer short_buffer = byte_buffer.asShortBuffer();
			for (int samp = 0; samp < number_samples; samp++)
				for (int chan = 0; chan < number_of_channels; chan++)
					sample_values[chan][samp] = (double) short_buffer.get() / max_sample_value;
		}

		// Return the samples
		return sample_values;
	}


	/**
	 * Generates an array of audio bytes based on the contents of the given
	 * <code>AudioInputStream</code>. Extracts all of the bytes available in the
	 * <code>AudioInputStream</code> at the moment that this method is called.
	 *
	 * @param	audio_input_stream	The <code>AudioInputStream</code> to extract the
	 *								bytes from.
	 * @return						The audio bytes extracted from the
	 *                              <code>AudioInputStream</code>. Has the same
	 *								<code>AudioFileFormat</code> as the specified 
	 *								<code>AudioInputStream</code>.
	 * @throws	Exception			Throws an exception if a problem occurs.
	 */
	public static byte[] getBytesFromAudioInputStream(AudioInputStream audio_input_stream)
		throws Exception
	{
		// Calculate the buffer size to use
		float buffer_duration_in_seconds = 0.25F;
		int buffer_size = AudioMethods.getNumberBytesNeeded( buffer_duration_in_seconds, 
		                                                     audio_input_stream.getFormat() );
		byte rw_buffer[] = new byte[buffer_size + 2];

		// Read the bytes into the rw_buffer and then into the ByteArrayOutputStream
		ByteArrayOutputStream byte_array_output_stream = new ByteArrayOutputStream();
		int position = audio_input_stream.read(rw_buffer, 0, rw_buffer.length);
		while (position > 0)
		{
			byte_array_output_stream.write(rw_buffer, 0, position);
			position = audio_input_stream.read(rw_buffer, 0, rw_buffer.length);
		}
		byte[] results = byte_array_output_stream.toByteArray();
		try
		{
			byte_array_output_stream.close();
		}
		catch (IOException e)
		{
			System.out.println(e);
			System.exit(0);
		}

		// Return the results
		return results;
	}


	/**
	 * Returns the number of bytes needed to store samples corresponding to audio
	 * of fixed duration.
	 *
	 * @param	duration_in_seconds	The duration, in seconds, of the audio that 
	 *								needs to be stored.
	 * @param	audio_format		The <code>AudioFormat</code> of the samples
	 *								to be stored.
	 * @return						The number of bytes needed to store the samples.
	 */
	public static int getNumberBytesNeeded( double duration_in_seconds,
	                                        AudioFormat audio_format )
	{
		int frame_size_in_bytes = audio_format.getFrameSize();
		float frame_rate = audio_format.getFrameRate();
		return (int) (frame_size_in_bytes * frame_rate * duration_in_seconds);
	}

	
	/**
	 * Returns the number of bytes needed to store samples corresponding to
	 * the given number of samples in a given <code>AudioFormat</code>.
	 *
	 * @param	number_samples		The number of samples to be encoded.
	 * @param	audio_format		The <code>AudioFormat</code> of the samples
	 *								to be stored.
	 * @return						The number of bytes needed to store the samples.
	 */
	public static int getNumberBytesNeeded( int number_samples,
	                                        AudioFormat audio_format )
	{
		int number_bytes_per_sample = audio_format.getSampleSizeInBits() / 8;
		int number_channels = audio_format.getChannels();
		return (number_samples * number_bytes_per_sample * number_channels);
	}

	
	/**
	 * Returns an AudioInputStream containing the given samples encoded using
	 * the given AudioFormat.
	 *
	 * @param	samples			The audio samples to convert. The first indice
	 *							indicates channel and the second indicates sample
	 *							number. Sample values vary from -1 to +1.
	 * @param	audio_format	The AudioFormat to use for encoding.
	 * @return					The AudioInputStream representing the samples.
	 * @throws	Exception		Throws an exception if an error occurs during
	 *							conversion.
	 */
	public static AudioInputStream convertToAudioInputStream( double[][] samples,
															  AudioFormat audio_format )
		throws Exception
	{
		int number_bytes_needed = getNumberBytesNeeded(samples[0].length, audio_format);
		byte[] audio_bytes = new byte[number_bytes_needed];
		writeSamplesToBuffer( samples,
			                  audio_format.getSampleSizeInBits(),
	                          audio_bytes );
		return getInputStream(audio_bytes, audio_format);
	}


	/**
	 * Writes the samples in the <i>sample_values</i> parameter to the <i>buffer</i>
	 * parameter. It is implicit that the caller knows what the sampling rate is
	 * and will be able to use it to correctly interperet the samples stored in the
	 * buffer after writing. Encoding is done using big endian signed PCM samples.
	 * Sample vaules greater than 1 or less than -1 are automatically clipped.
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
		
		// Clip values above +1 or below -1
		sample_values = clipSamples(sample_values);
		
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
	 * Clips the given samples so that all values below -1 are set to -1
	 * and all values above 1 are set to 1. The returned array is a copy
	 * so the original array is not altered.
	 *
	 * @param	original_samples	A 2-D array of doubles whose first indice
	 *								indicates channel and whose second indice
	 *								indicates sample value. In stereo, indice
	 *								0 corresponds to left and 1 to right.
	 * @return						A clipped copy of the original_samples
	 *								parameter.
	 * @throws	Exception			If a null parameter is passed.
	 */
	public static double[][] clipSamples(double[][] original_samples)
		throws Exception
	{
		// Throw exceptions for invalid parameters
		if (original_samples == null)
			throw new Exception( "Empty set of samples to provided." );

		// Perform clipping
		double[][] clipped_samples = new double[original_samples.length][];
		for (int chan = 0; chan < clipped_samples.length; chan++)
		{
			clipped_samples[chan] = new double[original_samples[chan].length];
			for (int samp = 0; samp < clipped_samples[chan].length; samp++)
			{
				if (original_samples[chan][samp] < -1.0)
					 clipped_samples[chan][samp] = -1.0;
				else if (original_samples[chan][samp] > 1.0)
					 clipped_samples[chan][samp] = 1.0;
				else
					 clipped_samples[chan][samp] = original_samples[chan][samp];
			}
		}
		return clipped_samples;
	}


	/**
	 * Returns the maximum possible value that a signed sample can have under
	 * the given bit depth. May be 1 or 2 values smaller than actual max,
	 * depending on specifics of encoding used.
	 *
	 * @param	bit_depth	The bit depth to examine.
	 * @return				The maximum possible positive sample value as a double.
	 */
	public static double findMaximumSampleValue(int bit_depth)
	{
		int max_sample_value_int = 1;
		for (int i = 0; i < (bit_depth - 1); i++)
			max_sample_value_int *= 2;
		max_sample_value_int--;
		double max_sample_value = ((double) max_sample_value_int) - 1.0;
		return max_sample_value;
	}


	/**
	 * Stores the samples on the <code>ByteArrayOutputStream</code> to
	 * the given file.
	 *
	 * @param	audio				The audio data to be recorded.
	 * @param	audio_format		The <i>AudioFormat</i> of the <i>audio</i>.
	 * @param	save_file			The file to save the audio to.
	 * @param	file_type			The type of audio file to save to.
	 * @throws	Exception			Throws an exception if an error writing to the file
	 *								occurs of if null parameters are passed.
	 */
	public static void saveByteArrayOutputStream( ByteArrayOutputStream audio, 
	                                              AudioFormat audio_format,
	                                              File save_file,
	                                              AudioFileFormat.Type file_type )
		throws Exception
	{
		// Throw exeptions for null parameters
		if (audio == null)
			throw new Exception("No audio data provided to save.");
		if (audio_format == null)
			throw new Exception("No audio format provided for saving.");
		if (save_file == null)
			throw new Exception("No file provided for saving.");
		if (file_type == null)
			throw new Exception("No audio file format provided for saving.");
		
		// Find the number of sample frames
		int number_bytes = audio.size();
		int bytes_per_frame = audio_format.getFrameSize();
		long number_frames = (long) (number_bytes / bytes_per_frame);

		// Convert audio_buffer to an AudioInputStream
		ByteArrayInputStream bais = new ByteArrayInputStream(audio.toByteArray());
		AudioInputStream audio_input_stream = new AudioInputStream(bais, audio_format, number_frames);

		// Save audio to disk
		saveToFile(audio_input_stream, save_file, file_type);
	}


	/**
	 * Stores the samples coming in on the given <code>AudioInputStream</code> to
	 * the given file. Just repeats the functionality of the 
	 * <code>AudioSystem.write</code> method, but with better documentation and exceptions.
	 * Useful for non-real-time recording, as well as possibly real-time recording.
	 *
	 * @param	audio_input_stream	The audio data to be recorded.
	 * @param	file_to_save_to		The file to save the audio to.
	 * @param	file_type			The type of audio file to save to.
	 * @throws	Exception			Throws an exception if an error writing to the file
	 *								occurs of if null parameters are passed.
	 */
	public static void saveToFile( AudioInputStream audio_input_stream,
	                               File file_to_save_to,
	                               AudioFileFormat.Type file_type )
		throws Exception
	{
		if (audio_input_stream == null)
			throw new Exception("No audio provided to save.");
		if (file_to_save_to == null)
			throw new Exception("No file provided to save to.");
		if (file_type == null)
			throw new Exception("No file type to save to specified.");
		AudioSystem.write(audio_input_stream, file_type, file_to_save_to);
	}
}