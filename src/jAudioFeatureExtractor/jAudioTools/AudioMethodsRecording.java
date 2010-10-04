/*
 * @(#)AudioMethodsRecording.java	1.0	April 5, 2005.
 *
 * Cory McKay
 * McGill Univarsity
 */

package jAudioFeatureExtractor.jAudioTools;

import javax.sound.sampled.*;
import java.io.*;


/**
 * A holder class for static methods and internal classes relating to recording
 * audio.
 *
 * @author	Cory McKay
 */
public class AudioMethodsRecording
{
	/* STATIC METHODS **********************************************************/


	/**
	 * Records the samples coming in on the given <code>TargetDataLine</code> to
	 * a <code>ByteArrayOutputStream</code>. Returns the thread that can be stopped
	 * and from which the samples can be extracted. Useful for real-time recording.
	 *
	 * @param	target_data_line	Where the samples are being recorded from.
	 * @return						The thread that performs the recording.
	 * @throws	Exception			Throws an exception if the parameter is null.
	 */
	public static RecordThread recordByteArrayOutputStream(TargetDataLine target_data_line)
		throws Exception
	{
		RecordThread record_thread = new RecordThread(target_data_line);
		record_thread.start();
		return record_thread;
	}


	/**
	 * Records the samples coming in on the given <code>TargetDataLine</code> to
	 * the given file. Returns the thread that can be stopped when recording is complete.
	 * Useful for real-time recording.
	 *
	 * @param	target_data_line	The source of the audio to be recorded.
	 * @param	file_to_save_to		The file to save the audio to.
	 * @param	file_type			The type of audio file to save to.
	 * @return						The thread that performs the recording.
	 * @throws	Exception			Throws an exception if the target_data_line parameter 
	 *								is null or if the given file cannot be written to.
	 */
	public static RecordToFileThread recordToFile( TargetDataLine target_data_line, 
		                                           File file_to_save_to,
		                                           AudioFileFormat.Type file_type )
		throws Exception
	{
		RecordToFileThread record_thread = new RecordToFileThread( target_data_line, 
		                                                           file_to_save_to,
		                                                           file_type );
		record_thread.start();
		return record_thread;
	}

	
	/* INTERNAL CLASSES ********************************************************/


	/**
	 * A thread for recording from a given <code>TargetDataLine</code> into a
	 * <code>ByteArrayOutputStream</code>. Can be interrupted by using the
	 * <code>stopRecording</code> method.
	 *
	 * <p><b>WARNING:</b> The <code>ByteArrayOutputStream</code> could continue
	 * to fill up and eventually use up all available memory if the data being written
	 * to it is not dealt with. This could result in an <code>OutOfMemory</code> error.
	 *
	 * <p><b>WARNING:</b>Note that the <code>TargetDataLine</code> is stopped and
	 * closed after recording has terminated. It will need to be re-started if it is to 
	 * be used elsewhere.
	 */
	public static class RecordThread
		extends Thread
	{
		private byte rw_buffer[]; // Temporary buffer to store information between reading and writing
		private boolean stop_recording; // Set to true if recording should be terminated
		private TargetDataLine target_data_line; // The source of the audio to be recorded
		private ByteArrayOutputStream byte_array_output_stream; // The output stream of what is being recorded

		/**
		 * Constructor that performs standard operations of the <code>Thread</code> class 
		 * as well as setting the source <code>TargetDataLine</code> from which the audio 
		 * to be recorded will arrive, initializing an intermediate buffer to hold
		 * 1/4 of a second of audio and setting the thread to be logically ready to record.
		 *
		 * @throws	Exception	Throws an exception if the parameter is null.
		 */
		RecordThread(TargetDataLine target_data_line)
			throws Exception
		{
			// Call the super class' constructor
			super();

			// Calculate the buffer size to use
			int frame_size_in_bytes = target_data_line.getFormat().getFrameSize();
			float frame_rate = target_data_line.getFormat().getFrameRate();
			float buffer_duration_in_seconds = 0.25F;
			int buffer_size = (int) (frame_size_in_bytes * frame_rate * buffer_duration_in_seconds);

			// Throw an exception if the target_data_line is null
			if (target_data_line == null)
				throw new Exception("Target TargetDataLine for recording is empty.");

			// Prepare the rw_buffer
			this.target_data_line = target_data_line;
			rw_buffer = new byte[buffer_size];

			// Prepare misc initializations
			stop_recording = false;
			byte_array_output_stream = null;
		}

		/**
		 * Begins recording from the <code>TargetDataLine</code> to the 
		 * <code>ByteArrayOutputStream</code>. Continues until the <code>stopRecording</code>
		 * method is called.
		 *
		 * <p><b>WARNING:</b>Note that the <code>TargetDataLine</code> is stopped and
		 * closed after recording has terminated. It will need to be re-started if it is to 
		 * be used elsewhere.
		 */
		public void run()
		{
			stop_recording = false;
			byte_array_output_stream = new ByteArrayOutputStream();
			while (!stop_recording)
			{
				int position = target_data_line.read(rw_buffer, 0, rw_buffer.length);
				if (position > 0)
					byte_array_output_stream.write(rw_buffer, 0, position);
			}
			try
			{
				byte_array_output_stream.close();
			}
			catch (IOException e)
			{
				System.out.println(e);
				System.exit(0);
			}
			target_data_line.stop(); 
			target_data_line.close(); 
		}

		/**
		 * Causes any recording currently in progress to end.
		 *
		 * <p><b>WARNING:</b>Note that the <code>TargetDataLine</code> is stopped after 
		 * recording has terminated. It will need to be re-started if it is to be used 
		 * elsewhere.
		 */
		public void stopRecording()
		{
			stop_recording = true;
		}

		/**
		 * Returns the stream of recorded data. Note that stream is set to null if no data
		 * has been recorded yet.
		 */
		public ByteArrayOutputStream getRecordedData()
		{
			return byte_array_output_stream;
		}

		/**
		 * Returns the <code>AudioFormat</code> that was used for recording.
		 */
		public AudioFormat getFormatUsedForRecording()
		{
			return target_data_line.getFormat();
		}
	}


	/**
	 * A thread for recording from a given <code>TargetDataLine</code> into an audio
	 * file belonging to one of a number of types. Can be interrupted by using the
	 * <code>stopRecording</code> method.
	 *
	 * <p><b>WARNING:</b>Note that the <code>TargetDataLine</code> is stopped and
	 * closed after recording has terminated. It will need to be re-started if it is to 
	 * be used elsewhere.
	 */
	public static class RecordToFileThread
		extends Thread
	{
		private TargetDataLine target_data_line; // The source of the audio to be recorded
		private File file_to_save_to; // The file to save the audio to
		private AudioFileFormat.Type file_type; // The type of audio file to save to

		/**
		 * Constructor that performs standard operations of the <code>Thread</code> class 
		 * as well as setting the source <code>TargetDataLine</code> from which the audio 
		 * to be recorded will arrive, setting the audio file to record to and its type
		 * and setting the thread to be logically ready to record.
		 *
		 * @param	target_data_line	The source of the audio to be recorded.
		 * @param	file_to_save_to		The file to save the audio to.
		 * @param	file_type			The type of audio file to save to.
		 * @throws	Exception			Throws an exception if the target_data_line parameter 
		 *								is null or if the given file cannot be written to.
		 */
		RecordToFileThread( TargetDataLine target_data_line, 
		                    File file_to_save_to,
		                    AudioFileFormat.Type file_type )
			throws Exception
		{
			// Call the super class' constructor
			super();

			// Throw an exception if the target_data_line is null, if the file format
			// is invalid or if the file is invalid
			if (target_data_line == null)
				throw new Exception("Target TargetDataLine for recording is empty.");
			if (file_type == null)
				throw new Exception("No file type to save to specified.");
			if (file_to_save_to == null)
				throw new Exception("No file to save to specified.");
			try
			{
				file_to_save_to.createNewFile();
			}
			catch (Exception e)
			{
				throw new Exception( "Cannot write to the given audio file: " + 
				                     file_to_save_to.getAbsolutePath() + ".");
			}
			if (!file_to_save_to.canWrite() || !file_to_save_to.isFile())
				throw new Exception( "Cannot write to the given audio file: " + 
				                     file_to_save_to.getAbsolutePath() + ".");

			// Prepare misc initializations
			this.target_data_line = target_data_line;
			this.file_to_save_to = file_to_save_to;
			this.file_type = file_type;
		}

		/**
		 * Begins recording from the <code>TargetDataLine</code> to the  file.
		 * Continues until the <code>stopRecording</code> method is called.
		 */
		public void run()
		{
			try
			{
				AudioSystem.write(new AudioInputStream( target_data_line),
														file_type,
														file_to_save_to );
			}
			catch (IOException e)
			{
				System.out.println(e);
				System.exit(0);
			}
		}

		/**
		 * Causes the recording currently in progress to end and saves the recorded
		 * data to disk.
		 *
		 * <p><b>WARNING:</b>Note that the <code>TargetDataLine</code> is stopped and
		 * closed after recording has terminated. It will need to be re-started if it is to 
		 * be used elsewhere.
		 */
		public void stopRecording()
		{
			target_data_line.stop(); // this causes data to be written to file, among other things
			target_data_line.close();
		}
	}
}