package jAudioFeatureExtractor.actions;

import jAudioFeatureExtractor.Controller;
import jAudioFeatureExtractor.DataTypes.RecordingInfo;
import jAudioFeatureExtractor.jAudioTools.AudioMethods;
import jAudioFeatureExtractor.jAudioTools.AudioMethodsPlayback;
import jAudioFeatureExtractor.jAudioTools.AudioSamples;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 * Play Back from internally stored samples. This playback can not be stopped.
 * 
 * @author Daniel McEnnis
 */
public class PlaySamplesAction extends AbstractAction {

	static final long serialVersionUID = 1;

	private Controller controller;

	private JTable recordings_table;

	/**
	 * Constructor that sets the menu text and stores a reference to the controller
	 * @param c near global controller
	 */
	public PlaySamplesAction(Controller c) {
		super("Play Samples...");
		controller = c;
	}

	/**
	 * This has to be set in RecordingSelectorPanel
	 * 
	 * @param rt
	 *            reference to the recordings table
	 */
	public void setTable(JTable rt) {
		recordings_table = rt;
	}

	/**
	 * Playback the file.
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			// Get the RecordingInfo selected for playback
			int selected_row = recordings_table.getSelectedRow();
			if (selected_row < 0)
				throw new Exception("No file selcected for playback.");
			// controller.stopPlayBackAction.setEnabled(true);
			RecordingInfo selected_audio = controller.dm_.recordingInfo[selected_row];

			// Perform playback of the file
			try {
				// Extract the audio data in the file into raw samples if not
				// done already, and convert this into an AudioInputStream
				AudioInputStream audio_input_stream = null;
				if (selected_audio.samples != null)
					audio_input_stream = selected_audio.samples
							.getAudioInputStreamChannelSegregated();
				else {
					File load_file = new File(selected_audio.file_path);
					AudioSamples samples = new AudioSamples(load_file,
							load_file.getPath(), false);

					// Store the extracted samples if this option is selected
					if (controller.storeSamples.isSelected())
						selected_audio.samples = samples;

					audio_input_stream = samples
							.getAudioInputStreamChannelSegregated();
				}

				// Get the SourceDataLine to play to from the system
				SourceDataLine source_data_line = AudioMethods
						.getSourceDataLine(audio_input_stream.getFormat(), null);

				// Stop any previous playback
				controller.stopPlayBackAction.stopPlayback();

				// Begin playback
				controller.dm_.playback_thread = AudioMethodsPlayback
						.playAudioInputStreamInterruptible(audio_input_stream,
								source_data_line);
			} catch (UnsupportedAudioFileException ex) {
				throw new Exception("File " + selected_audio.file_path
						+ " has an unsupported audio format.");
			} catch (Exception ex) {
				throw new Exception("File " + selected_audio.file_path
						+ " is not playable.\n" + ex.getMessage());
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}

}
