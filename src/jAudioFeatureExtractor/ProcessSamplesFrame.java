/*
 * @(#)ProcessSamplesFrame.java	1.0	April 5, 2005.
 *
 * Cory McKay
 * McGill Univarsity
 */

package jAudioFeatureExtractor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.sound.sampled.*;
import jAudioFeatureExtractor.jAudioTools.*;
import jAudioFeatureExtractor.GeneralTools.PlotDisplay;
import jAudioFeatureExtractor.DataTypes.RecordingInfo;

/**
 * A window that allows the user to process and edit the samples belonging to an
 * audio recording. The resulting samples can then be saved if desired.
 * <p>
 * The Choose Possible Format and Change to Possible Format buttons allow the
 * user to change bit depth, sampling rate, etc.
 * <p>
 * The Apply Gain button applies a gain to the signal corresponding to the text
 * box to the right of this button. A negative value introduces a phase shift of
 * pi.
 * <p>
 * The Normalize button normalizes the samples so that the highest amplitude
 * sample has an amplitude corresponding to the maximum value allowed by the bit
 * depth.
 * <p>
 * The Mix Down To One Channel button reduces multi-channel audio to one
 * channel.
 * <p>
 * The Time and Sample Indices radio buttons switch the meaning of the Start and
 * End fields between time and sample values. These affect the sections of the
 * recording that are played or displayed.
 * <p>
 * The Play button plays the region entered in the Start and End text boxes.
 * <p>
 * The Stop button ends playback.
 * <p>
 * The Restore button returns the values of the samples to what they were when
 * this dialog box was instantiated.
 * <p>
 * The View Samples button displays the sample values in the region entered in
 * the Start and End text boxes.
 * <p>
 * The Plot Samples button displays a separate plot of the samples in each
 * channel in the region entered in the Start and End text boxes.
 * <p>
 * The Plot Spectrum button displays a separate plot of the power spectrum of
 * the sample values for each channel between the limits given in the Start and
 * End specifications entered in the Start and End text boxes.
 * <p>
 * The Cancel button erases all changes.
 * <p>
 * The Save button saves the changes to the audio file that the samples were
 * derived, as well to the related AudioSamples object, if appropriate.
 * 
 * @author Cory McKay
 */
public class ProcessSamplesFrame extends JFrame implements ActionListener {
	/* FIELDS ***************************************************************** */

	static final long serialVersionUID = 1;

	/**
	 * Holds information regarding the recording to be processed.
	 */
	private RecordingInfo recording_info;

	/**
	 * Holds the audio samples that are being processed. Before processing
	 * begins, these are a <i>copy</i> of the AudioSamples in the
	 * recording_info field.
	 */
	private AudioSamples processed_audio_samples;

	/**
	 * Thread for playing back the processed audio. Null if nothing playing.
	 */
	private AudioMethodsPlayback.PlayThread playback_thread;

	/**
	 * The parent window where references to saved files can be stored.
	 */
	// private RecordingSelectorPanel parent_window;
	private OuterFrame outer_frame;

	private Controller controller;

	/**
	 * Dialog box to change the audio format for the recording.
	 */
	private AudioFormatJFrame audio_format_selector;

	/**
	 * GUI buttons
	 */
	private JButton choose_possible_format_button;

	private JButton change_encoding_format_button;

	private JButton apply_gain_button;

	private JButton normalize_button;

	private JButton mix_down_button;

	private JButton play_samples_button;

	private JButton stop_playback_button;

	private JButton restore_button;

	private JButton view_samples_button;

	private JButton plot_samples_button;

	private JButton plot_FFT_button;

	private JButton cancel_button;

	private JButton save_button;

	/**
	 * GUI text fields
	 */
	private JTextArea audio_info_text_field;

	private JTextArea choose_gain_text_field;

	private JTextArea start_text_field;

	private JTextArea end_text_field;

	/**
	 * GUI radio buttons
	 */
	private ButtonGroup start_end_type_radio_button_group;

	private JRadioButton time_start_end_type_radio_button;

	private JRadioButton samples_start_end_type_radio_button;

	/* CONSTRUCTOR ************************************************************ */

	/**
	 * Set up the GUI.
	 * 
	 * @param c
	 *            near global controller
	 * @param of
	 *            reference to the outer frame of the application
	 * @param recording_info
	 *            Contains audio data to be processed, or a reference to a file
	 *            from which this data can be derived.
	 * @throws Exception
	 *             Throws an inormative exception if no audio samples can be
	 *             extracted from the given recording_info.
	 */
	public ProcessSamplesFrame(Controller c, OuterFrame of,
			RecordingInfo recording_info) throws Exception {
		try {
			outer_frame = of;
			controller = c;
			// Set window title and make window modal
			setTitle("Process Audio Samples");
			Color blue = new Color((float) 0.75, (float) 0.85, (float) 1.0);
			this.getContentPane().setBackground(blue);
			of.setEnabled(false);

			// Cause program to react when the exit box is pressed
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					cancel();
				}
			});

			// Set the parent_window and the recording to be processed
			this.recording_info = recording_info;

			// Derive the processed_audio_samples
			if (recording_info == null)
				throw new Exception(
						"Reference to recording to process is empty");
			processed_audio_samples = null;
			if (recording_info.samples != null)
				processed_audio_samples = recording_info.samples
						.getCopyOfAudioSamples();
			else {
				File load_file = new File(recording_info.file_path);
				processed_audio_samples = new AudioSamples(load_file, load_file
						.getPath(), false);
			}

			// Set playback thread to null initially
			playback_thread = null;

			// Prepare the audio format selection dialog box
			audio_format_selector = new AudioFormatJFrame();
			AudioFormat default_format = AudioFormatJFrame
					.getStandardMidQualityRecordAudioFormat();
			audio_format_selector.setAudioFormat(default_format);

			// Set up the top-level GUI elements
			int horizontal_gap = 6; // horizontal space between GUI elements
			int vertical_gap = 11; // horizontal space between GUI elements
			setLayout(new BorderLayout(horizontal_gap, vertical_gap));
			JPanel top_panel = new JPanel(new BorderLayout(horizontal_gap,
					vertical_gap));
			top_panel.setBackground(this.getContentPane().getBackground());
			JPanel bottom_panel = new JPanel(new GridLayout(13, 3,
					horizontal_gap, vertical_gap));
			bottom_panel.setBackground(this.getContentPane().getBackground());

			// Set up the top_panel
			JPanel top_left_panel = new JPanel();
			top_left_panel.setBackground(this.getContentPane().getBackground());
			JPanel top_right_panel = new JPanel();
			top_right_panel
					.setBackground(this.getContentPane().getBackground());
			top_left_panel.add(new JLabel("Recording Info:"));
			audio_info_text_field = new JTextArea(12, 25);
			audio_info_text_field.setEditable(false);
			updateRecordingInformation();
			top_right_panel.add(audio_info_text_field);
			top_panel.add(top_left_panel, BorderLayout.WEST);
			top_panel.add(top_right_panel, BorderLayout.CENTER);
			add(top_panel, BorderLayout.NORTH);

			// Set up the bottom_panel
			choose_possible_format_button = new JButton(
					"Choose Possible Format");
			choose_possible_format_button.addActionListener(this);
			bottom_panel.add(choose_possible_format_button);
			change_encoding_format_button = new JButton(
					"Change to Possible Format");
			change_encoding_format_button.addActionListener(this);
			bottom_panel.add(change_encoding_format_button);
			apply_gain_button = new JButton("Apply Gain:");
			apply_gain_button.addActionListener(this);
			bottom_panel.add(apply_gain_button);
			choose_gain_text_field = new JTextArea();
			bottom_panel.add(choose_gain_text_field);
			normalize_button = new JButton("Normalize");
			normalize_button.addActionListener(this);
			bottom_panel.add(normalize_button);
			mix_down_button = new JButton("Mix Down To One Channel");
			mix_down_button.addActionListener(this);
			bottom_panel.add(mix_down_button);
			bottom_panel.add(new JLabel(""));
			bottom_panel.add(new JLabel(""));
			bottom_panel.add(new JLabel("Limits Based On:"));
			bottom_panel.add(new JLabel(""));
			start_end_type_radio_button_group = new ButtonGroup();
			time_start_end_type_radio_button = new JRadioButton("Time");
			time_start_end_type_radio_button.setBackground(this
					.getContentPane().getBackground());
			time_start_end_type_radio_button.addActionListener(this);
			start_end_type_radio_button_group
					.add(time_start_end_type_radio_button);
			samples_start_end_type_radio_button = new JRadioButton(
					"Sample Indices");
			samples_start_end_type_radio_button.setBackground(this
					.getContentPane().getBackground());
			samples_start_end_type_radio_button.addActionListener(this);
			samples_start_end_type_radio_button.setSelected(true);
			start_end_type_radio_button_group
					.add(samples_start_end_type_radio_button);
			bottom_panel.add(time_start_end_type_radio_button);
			bottom_panel.add(samples_start_end_type_radio_button);
			bottom_panel.add(new JLabel("Start"));
			start_text_field = new JTextArea();
			bottom_panel.add(start_text_field);
			bottom_panel.add(new JLabel("End"));
			end_text_field = new JTextArea();
			bottom_panel.add(end_text_field);
			convertStartEndFields(true);
			play_samples_button = new JButton("Play");
			play_samples_button.addActionListener(this);
			bottom_panel.add(play_samples_button);
			stop_playback_button = new JButton("Stop");
			stop_playback_button.addActionListener(this);
			bottom_panel.add(stop_playback_button);
			restore_button = new JButton("Restore");
			restore_button.addActionListener(this);
			bottom_panel.add(restore_button);
			view_samples_button = new JButton("View Samples");
			view_samples_button.addActionListener(this);
			bottom_panel.add(view_samples_button);
			plot_samples_button = new JButton("Plot Samples");
			plot_samples_button.addActionListener(this);
			bottom_panel.add(plot_samples_button);
			plot_FFT_button = new JButton("Plot Spectrum");
			plot_FFT_button.addActionListener(this);
			bottom_panel.add(plot_FFT_button);
			bottom_panel.add(new JLabel(""));
			bottom_panel.add(new JLabel(""));
			cancel_button = new JButton("Canel");
			cancel_button.addActionListener(this);
			bottom_panel.add(cancel_button);
			save_button = new JButton("Save");
			save_button.addActionListener(this);
			bottom_panel.add(save_button);
			add(bottom_panel, BorderLayout.CENTER);

			// Display GUI
			pack();
			setVisible(true);
		} catch (Exception e) {
			outer_frame.setEnabled(true);
			throw e;
		}
	}

	/* PUBLIC METHODS ********************************************************* */

	/**
	 * Call the appropriate methods when the buttons are pressed.
	 * 
	 * @param event
	 *            The event that is to be reacted to.
	 */
	public void actionPerformed(ActionEvent event) {
		// React to the choose_possible_format_button
		if (event.getSource().equals(choose_possible_format_button))
			chooseEncodingFormt();

		// React to the display_current_audio_format_button
		else if (event.getSource().equals(change_encoding_format_button))
			changeEncodingFormat();

		// React to the apply_gain_button
		else if (event.getSource().equals(apply_gain_button))
			applyGain();

		// React to the normalize_button
		else if (event.getSource().equals(normalize_button))
			normalize();

		// React to the mix_down_button
		else if (event.getSource().equals(mix_down_button))
			mixDownToOneChannel();

		// React to the play_samples_button
		else if (event.getSource().equals(play_samples_button))
			play();

		// React to the stop_playback_button
		else if (event.getSource().equals(stop_playback_button))
			stopPlayback();

		// React to the restore_button
		else if (event.getSource().equals(restore_button))
			restore();

		// React to the view_samples_button
		else if (event.getSource().equals(view_samples_button))
			viewSamples();

		// React to the plot_samples_button
		else if (event.getSource().equals(plot_samples_button))
			viewSignalPlot();

		// React to the plot_FFT_button
		else if (event.getSource().equals(plot_FFT_button))
			viewSpectrum();

		// React to the cancel_button
		else if (event.getSource().equals(cancel_button))
			cancel();

		// React to the save_button
		else if (event.getSource().equals(save_button))
			save();

		// React to the time_start_end_type_radio_button
		else if (event.getSource().equals(time_start_end_type_radio_button))
			convertStartEndFields(false);

		// React to the samples_start_end_type_radio_button
		else if (event.getSource().equals(samples_start_end_type_radio_button))
			convertStartEndFields(false);
	}

	/* PRIVATE METHODS ******************************************************** */

	/**
	 * Display the audio format selector.
	 */
	private void chooseEncodingFormt() {
		audio_format_selector.setVisible(true);
	}

	/**
	 * Change the encoding format used to encode the samples to that selected in
	 * the audio_format_selector dialog box. The number of channels must remain
	 * the same, however. Also, signed big endian samples must always be used.
	 * Only radio button selections are allowed.
	 */
	private void changeEncodingFormat() {
		try {
			AudioFormat new_audio_format = audio_format_selector
					.getAudioFormat(false);
			if ((new_audio_format.getSampleSizeInBits() != 16 && new_audio_format
					.getSampleSizeInBits() != 8)
					|| !new_audio_format.isBigEndian()
					|| new_audio_format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED)
				throw new Exception(
						"Only 8 or 16 bit signed PCM samples with a big-endian\n"
								+ "byte order can be used currently.");

			AudioInputStream original_audio_input_stream = processed_audio_samples
					.getAudioInputStreamChannelSegregated();
			int original_number_channels = original_audio_input_stream
					.getFormat().getChannels();
			if (original_number_channels != new_audio_format.getChannels())
				throw new Exception("Original audio has "
						+ original_number_channels + " channels but the\n"
						+ "new format has " + new_audio_format.getChannels()
						+ "channels.\n" + "These must match.");

			AudioInputStream new_audio_input_stream = AudioSystem
					.getAudioInputStream(new_audio_format,
							original_audio_input_stream);

			processed_audio_samples = new AudioSamples(new_audio_input_stream,
					processed_audio_samples.getUniqueIdentifier(), false);
			updateRecordingInformation();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Apply the gain entered in the choose_gain_text_field to the samples.
	 */
	private void applyGain() {
		try {
			double[][] samples = processed_audio_samples
					.getSamplesChannelSegregated();
			double gain = (new Double(choose_gain_text_field.getText()))
					.doubleValue();
			samples = DSPMethods.applyGain(samples, gain);
			processed_audio_samples.setSamples(samples);
			updateRecordingInformation();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Normalize the samples so that the absolute value of the largest sample
	 * value is 1.
	 */
	private void normalize() {
		processed_audio_samples.normalize();
		updateRecordingInformation();
	}

	/**
	 * Replace the data stored in the processed_audio_samples field with a
	 * single channel that is the result of the previous channels being mixed
	 * down into one.
	 */
	private void mixDownToOneChannel() {
		double[][] new_samples = new double[1][];
		new_samples[0] = processed_audio_samples.getSamplesMixedDown();
		new_samples = DSPMethods.getCopyOfSamples(new_samples);
		AudioFormat old_audio_format = processed_audio_samples.getAudioFormat();
		AudioFormat new_audio_format = new AudioFormat(old_audio_format
				.getSampleRate(), old_audio_format.getSampleSizeInBits(), 1,
				true, old_audio_format.isBigEndian());
		try {
			processed_audio_samples = new AudioSamples(new_samples,
					new_audio_format, processed_audio_samples
							.getUniqueIdentifier(), false);
			updateRecordingInformation();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Plays the samples extracted in the processed_audio_samples field between
	 * the limits specified in the start and end text areas. Any previous
	 * playback is stopped.
	 */
	private void play() {
		try {
			// Verify the text field entries
			verifyStartAndEndFields();

			// Get the audio data from the start and stop positions
			AudioInputStream audio_input_stream = null;
			if (time_start_end_type_radio_button.isSelected()) {
				double start_time = (new Double(start_text_field.getText())
						.doubleValue());
				double end_time = (new Double(end_text_field.getText())
						.doubleValue());
				audio_input_stream = processed_audio_samples
						.getAudioInputStreamChannelSegregated(start_time,
								end_time);
			} else {
				int start_sample = (new Integer(start_text_field.getText())
						.intValue());
				int end_sample = (new Integer(end_text_field.getText())
						.intValue());
				audio_input_stream = processed_audio_samples
						.getAudioInputStreamChannelSegregated(start_sample,
								end_sample);
			}

			// Get the place to play to
			SourceDataLine source_data_line = AudioMethods.getSourceDataLine(
					audio_input_stream.getFormat(), null);

			// Stop any previous playback
			stopPlayback();

			// Begin playback
			playback_thread = AudioMethodsPlayback
					.playAudioInputStreamInterruptible(audio_input_stream,
							source_data_line);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Stop any playback currently in progress.
	 */
	private void stopPlayback() {
		if (playback_thread != null)
			playback_thread.stopPlaying();
		playback_thread = null;
	}

	/**
	 * Display a dialog box showing the sample values for each separate channel
	 * between the limits given in the Start and End specifications entered by
	 * the user.
	 */
	private void viewSamples() {
		try {
			// Verify the text field entries
			verifyStartAndEndFields();

			// Get the audio data from the start and stop positions
			int start_sample = 0;
			int end_sample = 0;
			if (time_start_end_type_radio_button.isSelected()) {
				double start_time = (new Double(start_text_field.getText()))
						.doubleValue();
				double end_time = (new Double(end_text_field.getText()))
						.doubleValue();
				float sampling_rate = processed_audio_samples.getAudioFormat()
						.getSampleRate();
				start_sample = DSPMethods.convertTimeToSample(start_time,
						sampling_rate);
				end_sample = DSPMethods.convertTimeToSample(end_time,
						sampling_rate);
			} else {
				start_sample = (new Integer(start_text_field.getText()))
						.intValue();
				end_sample = (new Integer(end_text_field.getText())).intValue();
			}

			// Generate the dialog box
			SampleTextDialog displayer = new SampleTextDialog();
			JTextArea text_area = displayer.getTextArea();

			// Get the samples
			double[][] samples = processed_audio_samples
					.getSamplesChannelSegregated(start_sample, end_sample);

			// Fill and show the dialog box
			text_area.append("SAMPLE\t");
			for (int chan = 0; chan < samples.length; chan++)
				text_area.append("CHANNEL " + chan + "\t\t");
			for (int samp = start_sample; samp <= end_sample; samp++) {
				text_area.append("\n" + samp + "\t");
				for (int chan = 0; chan < samples.length; chan++)
					text_area.append(samples[chan][samp] + "\t\t");
			}
			displayer.display();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Display a dialog box showing a plot of the sample values for each
	 * channel. between the limits given in the Start and End specifications
	 * entered by the user.
	 */
	private void viewSignalPlot() {
		try {
			// Verify the text field entries
			verifyStartAndEndFields();

			// Get the audio data from the start and stop positions
			int start_sample = 0;
			int end_sample = 0;
			if (time_start_end_type_radio_button.isSelected()) {
				double start_time = (new Double(start_text_field.getText()))
						.doubleValue();
				double end_time = (new Double(end_text_field.getText()))
						.doubleValue();
				float sampling_rate = processed_audio_samples.getAudioFormat()
						.getSampleRate();
				start_sample = DSPMethods.convertTimeToSample(start_time,
						sampling_rate);
				end_sample = DSPMethods.convertTimeToSample(end_time,
						sampling_rate);

			} else {
				start_sample = (new Integer(start_text_field.getText()))
						.intValue();
				end_sample = (new Integer(end_text_field.getText())).intValue();
			}

			// Get the samples
			double[][] samples = processed_audio_samples
					.getSamplesChannelSegregated(start_sample, end_sample);

			// Set up the x-label fields
			double[][] x_labels = new double[samples.length][samples[0].length];
			for (int chan = 0; chan < x_labels.length; chan++)
				for (int samp = 0; samp < x_labels[chan].length; samp++) {
					int ef_sample = samp + start_sample;
					if (time_start_end_type_radio_button.isSelected())
						x_labels[chan][samp] = (double) ef_sample
								/ processed_audio_samples
										.getSamplingRateAsDouble();
					else
						x_labels[chan][samp] = ef_sample;
				}

			// Display the plot
			PlotDisplay plotter = new PlotDisplay(samples, x_labels, false,
					processed_audio_samples.getUniqueIdentifier(), false);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Display a dialog box showing a plot of the power spectrum of the sample
	 * values for each channel between the limits given in the Start and End
	 * specifications entered by the user.
	 */
	private void viewSpectrum() {
		try {
			// Verify the text field entries
			verifyStartAndEndFields();

			// Get the audio data from the start and stop positions
			int start_sample = 0;
			int end_sample = 0;
			if (time_start_end_type_radio_button.isSelected()) {
				double start_time = (new Double(start_text_field.getText()))
						.doubleValue();
				double end_time = (new Double(end_text_field.getText()))
						.doubleValue();
				float sampling_rate = processed_audio_samples.getAudioFormat()
						.getSampleRate();
				start_sample = DSPMethods.convertTimeToSample(start_time,
						sampling_rate);
				end_sample = DSPMethods.convertTimeToSample(end_time,
						sampling_rate);

			} else {
				start_sample = (new Integer(start_text_field.getText()))
						.intValue();
				end_sample = (new Integer(end_text_field.getText())).intValue();
			}

			// Get the samples
			double[][] samples = processed_audio_samples
					.getSamplesChannelSegregated(start_sample, end_sample);

			// Calculate the FFT magnitudes and the bin labels
			double[][] powers = new double[samples.length][];
			double[] labels = null;
			for (int chan = 0; chan < powers.length; chan++) {
				FFT fft = new FFT(samples[chan], null, false, true);
				powers[chan] = fft.getPowerSpectrum();
				if (labels == null)
					labels = fft.getBinLabels(processed_audio_samples
							.getSamplingRateAsDouble());
			}

			// Format the bin labels
			double[][] x_labels = new double[samples.length][];
			for (int chan = 0; chan < x_labels.length; chan++)
				x_labels[chan] = labels;

			// Display the plot
			PlotDisplay plotter = new PlotDisplay(powers, x_labels, false,
					processed_audio_samples.getUniqueIdentifier(), false);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Reset the processed_audio_samples field to the contents of the
	 * processed_audio_samples field and update the GUI.
	 */
	private void restore() {
		try {
			// Restore processed_audio_samples to the contents of
			// processed_audio_samples
			processed_audio_samples = null;
			if (recording_info.samples != null)
				processed_audio_samples = recording_info.samples
						.getCopyOfAudioSamples();
			else {
				File load_file = new File(recording_info.file_path);
				processed_audio_samples = new AudioSamples(load_file, load_file
						.getPath(), false);
			}

			// Update the GUI
			convertStartEndFields(true);
			updateRecordingInformation();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Delete all changes, stop any playback in progress and effectively erase
	 * this window. Reactivates calling window.
	 */
	private void cancel() {
		stopPlayback();
		recording_info = null;
		processed_audio_samples = null;
		audio_format_selector = null;
		System.gc();
		outer_frame.setEnabled(true);
		this.setVisible(false);
	}

	/**
	 * Saves the file and any modifications to the file referred to in the
	 * recording_info passed to this object at instantiation. Hides and erases
	 * this window. Also updates the samples in the RecordingInfo object passed
	 * to this object at instantiation if they weren't null to begin with.
	 */
	private void save() {
		try {
			File save_file = new File(recording_info.file_path);
			AudioFileFormat.Type file_format = AudioFileFormat.Type.WAVE;
			if (save_file.exists())
				file_format = AudioSystem.getAudioFileFormat(save_file)
						.getType();
			processed_audio_samples.saveAudio(save_file, true, file_format,
					false);
			if (recording_info.samples != null)
				recording_info.samples = processed_audio_samples;
			cancel();
		} catch (Exception e) {
			String message = "Unable to save changes:\n";
			JOptionPane.showMessageDialog(null, message + e.getMessage(),
					"ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Update the audio_info_text_field to reflect any changes.
	 */
	private void updateRecordingInformation() {
		String header = "FILE: " + recording_info.file_path + "\n";
		audio_info_text_field.setText(header
				+ processed_audio_samples.getRecordingInfo());
	}

	/**
	 * Converts the start_text_field and end_text_field fields back and forth
	 * between sample indices and times. If the reset parameter is true, then
	 * The values are set to the beginning and end of the audio.
	 * 
	 * @param reset
	 *            Whether the values should be reset to encompass the entire
	 *            recording.
	 */
	private void convertStartEndFields(boolean reset) {
		// If initializing to go from start to end
		if (reset) {
			if (time_start_end_type_radio_button.isSelected()) {
				start_text_field.setText("0");
				String end_time = String.valueOf(processed_audio_samples
						.getDuration());
				end_text_field.setText(end_time);
			} else {
				start_text_field.setText("0");
				String end_sample = String.valueOf(processed_audio_samples
						.getNumberSamplesPerChannel() - 1);
				end_text_field.setText(end_sample);
			}
		}

		// If switching between time and sample indice, or vice versa
		else {
			float sampling_rate = processed_audio_samples.getSamplingRate();
			if (time_start_end_type_radio_button.isSelected()) {
				int start_sample = (int) (new Double(start_text_field.getText())
						.doubleValue());
				double start_time = DSPMethods.convertSampleToTime(
						start_sample, sampling_rate);
				start_text_field.setText(String.valueOf(start_time));
				int end_sample = (int) (new Double(end_text_field.getText())
						.doubleValue());
				double end_time = DSPMethods.convertSampleToTime(end_sample,
						sampling_rate);
				end_text_field.setText(String.valueOf(end_time));
			} else {
				double start_time = (new Double(start_text_field.getText()))
						.doubleValue();
				int start_sample = DSPMethods.convertTimeToSample(start_time,
						sampling_rate);
				start_text_field.setText(String.valueOf(start_sample));
				double end_time = (new Double(end_text_field.getText()))
						.doubleValue();
				int end_sample = DSPMethods.convertTimeToSample(end_time,
						sampling_rate);
				end_text_field.setText(String.valueOf(end_sample));
			}
		}
	}

	/**
	 * Throws an exception if invalid entries have been entered in the text
	 * fields.
	 */
	private void verifyStartAndEndFields() throws Exception {
		if (time_start_end_type_radio_button.isSelected()) {
			double start_time = (new Double(start_text_field.getText()))
					.doubleValue();
			double end_time = (new Double(end_text_field.getText()))
					.doubleValue();
			double maximum_duration = processed_audio_samples.getDuration();
			if (start_time < 0.0 || start_time > maximum_duration)
				throw new Exception("Start time is specified to be "
						+ start_time + ".\n"
						+ "This value must be between 0 and "
						+ maximum_duration + ".");
			if (end_time <= start_time)
				throw new Exception("End time is " + end_time
						+ " and start time is " + start_time + ".\n"
						+ "Start time must be after end time.");
			if (end_time > maximum_duration)
				throw new Exception("End time of " + end_time
						+ " is greater than the total duration of "
						+ maximum_duration + ".\n"
						+ "Start time must be after end time.");
			float sampling_rate = processed_audio_samples.getAudioFormat()
					.getSampleRate();
		} else {
			int start_sample = (new Integer(start_text_field.getText()))
					.intValue();
			int end_sample = (new Integer(end_text_field.getText())).intValue();
			int maximum_sample = processed_audio_samples
					.getNumberSamplesPerChannel();
			if (start_sample < 0 || start_sample > maximum_sample)
				throw new Exception("Start sample is specified to be "
						+ start_sample + ".\n"
						+ "This value must be between 0 and " + maximum_sample
						+ ".");
			if (end_sample <= start_sample)
				throw new Exception("End sample is " + end_sample
						+ " and start sample is " + start_sample + ".\n"
						+ "Start sample must be after end sample.");
			if (end_sample > maximum_sample)
				throw new Exception("End sample of " + end_sample
						+ " is greater than the total samples of "
						+ maximum_sample + ".\n"
						+ "Start sample must be after end sample.");
		}
	}
}