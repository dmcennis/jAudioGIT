/*
 * @(#)AudioMethodsPlayback.java	1.0	April 5, 2005.
 *
 * Cory McKay
 * McGill Univarsity
 */

package jAudioFeatureExtractor.jAudioTools;

import javax.sound.sampled.*;
import java.io.*;


/**
 * A holder class for static methods and internal classes relating to playing
 * back audio recordings.
 *
 * @author	Cory McKay
 */
public class AudioMethodsPlayback
{
	/* PUBLIC METHODS **********************************************************/


	/**
	 * Plays the samples in the given <code>AudioInputStream</code> to the
	 * specified <code>SourceDataLine</code>.
	 *
	 * <p>The thread performing the playback is returned to the user, so the user has
	 * the option of interrupting it if he or she wishes.
	 *
	 * <p>Note that the same <code>AudioFormat</code> must be used by both
	 * the <code>AudioInputStream</code> and the <code>SourceDataLine</code>.
	 *
	 * @param	audio_input_stream	The audio stream to be played.
	 * @param	source_data_line	Where the samples are to be played to.
	 * @throws	Exception			Throws an exception if the <code>AudioFormat</code>
	 *                              of the audio_input_stream parameter does not match 
	 *                              that of the source_data_line parameter.
	 */
	public static PlayThread playAudioInputStreamInterruptible( AudioInputStream audio_input_stream, 
	                                                            SourceDataLine source_data_line )
		throws Exception
	{
		// Throw an exception if the AudioFormat of audio_input_format does not match
		// the AudioFormat of source_data_line_format.
		AudioFormat audio_input_format = audio_input_stream.getFormat();
		AudioFormat source_data_line_format = source_data_line.getFormat();
		if (!audio_input_format.matches(source_data_line_format))
		{
			throw new Exception("AudioFormat that audio is presented in differs from " +
			                    "AudioFormat that it is to be played in.");
		}
		
		// Begin playback and return the thread performing playback
		PlayThread play_thread = null;
		play_thread = new PlayThread(source_data_line, audio_input_stream);
		play_thread.start();
		return play_thread;
	}


	/**
	 * Plays the samples in the given <code>AudioInputStream</code> to the
	 * specified <code>SourceDataLine</code>.
	 *
	 * <p>This playback is not interruptible by the user, and will continue until the
	 * <code>AudioInputStream</code> is empty. The
	 * <code>playAudioInputStreamInterruptible</code> method should be called 
	 * instead if the user wishes to be able to interrupt playback.
	 *
	 * <p>Note that the same <code>AudioFormat</code> must be used by both
	 * the <code>AudioInputStream</code> and the <code>SourceDataLine</code>.
	 *
	 * @param	audio_input_stream	The audio stream to be played.
	 * @param	source_data_line	Where the samples are to be played to.
	 * @throws	Exception			Throws an exception if the <code>AudioFormat</code>
	 *                              of the audio_input_stream parameter does not match 
	 *                              that of the source_data_line parameter.
	 */
	public static void playAudioInputStream( AudioInputStream audio_input_stream, 
	                                         SourceDataLine source_data_line )
		throws Exception
	{
		// Throw an exception if the AudioFormat of audio_input_format does not match
		// the AudioFormat of source_data_line_format.
		AudioFormat audio_input_format = audio_input_stream.getFormat();
		AudioFormat source_data_line_format = source_data_line.getFormat();
		if (!audio_input_format.matches(source_data_line_format))
		{
			throw new Exception("AudioFormat that audio is presented in differs from " +
			                    "AudioFormat that it is to be played in.");
		}

		// Begin playback
		PlayThread play_thread = new PlayThread(source_data_line, audio_input_stream);
		play_thread.start();
	}

	
	/* INTERNAL CLASSES ********************************************************/


	/**
	 * A thread for playing an <code>AudioInputStream</code> directly to a
	 * <code>SourceDataLine</code>. Can be interrupded using the <code>stopPlaying</code>
	 * method.
	 *
	 * <p><b>WARNING:</b>Note that the <code>SourceDataLine</code> is closed after 
	 * playback has terminated. It will need to be reopened if it is to be used elsewhere.
	 */
	public static class PlayThread
		extends Thread
	{
		private byte rw_buffer[]; // Temporary buffer to store information between reading and writing
		private boolean stop_playing; // Set to true if playing should be terminated
		private SourceDataLine source_data_line = null; // Where the audio is coming from
		private AudioInputStream audio_input_stream = null; // Where the audio is being sent

		/**
		 * Constructor that performs standard operations of the Thread class as well as
		 * setting the source <code>AudioInputStream</code>, setting the target
		 * <code>SourceDataLine</code> and initializing an intermediate buffer to hold
		 * 1/4 seconds of audio.
		 *
		 * @throws	Exception	Throws an exception if either parameter is null.
		 * @throws	Exception	Throws an exception if the <code>AudioFormat</code>
		 *                      of the audio_input_stream parameter does not match 
		 *                      that of the source_data_line parameter.
		 */
		PlayThread(SourceDataLine source_data_line, AudioInputStream audio_input_stream)
			throws Exception
		{
			// Perform the super class' constructor
			super();
			
			// Throw an exception if the AudioFormat of audio_input_format does not match
			// the AudioFormat of source_data_line_format.
			AudioFormat audio_input_format = audio_input_stream.getFormat();
			AudioFormat source_data_line_format = source_data_line.getFormat();
			if (!audio_input_format.matches(source_data_line_format))
			{
				throw new Exception("AudioFormat that audio is presented in differs from " +
									"AudioFormat that it is to be played in.");
			}
		
			// Calculate the buffer size to use
			float buffer_duration_in_seconds = 0.25F;
			int buffer_size = AudioMethods.getNumberBytesNeeded( buffer_duration_in_seconds, 
			                                                     source_data_line.getFormat() );

			// Throw exceptions if the parameters are null
			if (source_data_line == null)
				throw new Exception("Target SourceDataLine for playback is empty.");
			if (audio_input_stream == null)
				throw new Exception("Source AudioInputStream for playback is empty.");

			// Prepare the source_data_line and the audio_input_stream
			this.source_data_line = source_data_line;
			this.audio_input_stream = audio_input_stream;

			// Perform other initializations
			rw_buffer = new byte[buffer_size];
			stop_playing = false;
		}
		
		/**
		 * Writes data from the <code>AudioInputStream</code> to the <code>SourceDataLine</code>.
		 *
		 * <p><b>WARNING:</b>Note that the <code>SourceDataLine</code> is closed after 
		 * playback has terminated. It will need to be reopened if it is to be used elsewhere.
		 */
		public void run()
		{
			// Note that playback is permitted
			stop_playing = false;
			
			// Read samples from an AudioInputStream into a SourceDataLine
			int position;
			try
			{
				while ( (position = audio_input_stream.read(rw_buffer, 0, rw_buffer.length)) != -1 )
				{	
					if (position > 0)
						source_data_line.write(rw_buffer, 0, position);
					if (stop_playing)
						break;
				}
			}
			catch (IOException e)
			{
				System.out.println(e);
				System.exit(0);
			}

			// Block until all samples in source_data_line have been played and then close
			// the line
			if (!stop_playing)
				source_data_line.drain();
			source_data_line.close();
		}

		/**
		 * Causes any playback currently in progress to end.
		 *
		 * <p><b>WARNING:</b>Note that the <code>SourceDataLine</code> is closed after 
		 * playback has terminated. It will need to be reopened if it is to be used elsewhere.
		 */
		public void stopPlaying()
		{
				stop_playing = true;
		}
	}
}