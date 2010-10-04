/*
 * @(#)SynthesisFrame.java	1.0	April 5, 2005.
 *
 * Cory McKay
 * McGill Univarsity
 */

package jAudioFeatureExtractor;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import jAudioFeatureExtractor.jAudioTools.*;
import jAudioFeatureExtractor.GeneralTools.StringMethods;


/**
 * This dialog allows the user to generate audio using additive
 * synthesis. This audio can then be previewed or stored in the parent object.
 *
 * <p>There are two types of parameters that the user can set. The Overall Synthesis
 * parameters apply to all components that are to be added. The Individual Components
 * parameters apply to each component that is to be added.
 *
 * <p>The JTextArea near the bottom of the screen will display all
 * components that have been added to date. To add a component, ensure that the
 * Individual Components settings are as desired, and then click the Add Compnonent
 * button. Components can be added cumulatively this way. The Clear All Components
 * button will erase all components added to date. The synthesis can be previewed
 * (and analyzed) by pressing the Play button. The playing of the
 * synthesis can be stopped by pressing the Stop Playback button. Pressing
 * the Save button will synthesize the audio as selected on the
 * GUI and store it in a file and send it to the parent object. The file is saved
 * in the format specified in the File Format For Saving combo box.
 *
 * <p>The Choose Audio Format button brings up an AudioFormatJFrame
 * that can be used to select the encoding to use for the synthesized sounds. Not
 * all possible parameters will necessarily be accepted.
 *
 * <p>The Duration field specifie the length, in seconds, of audio to be synthesized.
 *
 * <p>The Gain fields specifies the gain to apply to the synthesised sound, where
 * 0 is complete attenuation and 1 is maximum gain.
 *
 * <p>The Panning field specifies the relative strength of the two stereo channels.
 * This value is ignored in non-stereo cases. This value must be between -1.0 and +1.0
 * with -1.0 corresponding to full amplitude on the left channel and silence on the right,
 * and +1.0 corresponding to the reverse. A value of 0 indicates equal balance.
 *
 * <p>The Click Avoidance Env. Length (ms) field is used to determine the
 * duration of the envelope applied at the beginning and end of the synthesized audio
 * in order to avoid clicks. Linear attenuation is applied for this amount of time
 * on each end. The value is in seconds.
 * 
 * <p>The Synthesis Type combo box allows the user to select the type of sound
 * to be added as a component. This corresponds to the types of sound available from the
 * AudioMethodsSynthesis class.
 *
 * <p>The Fundamental Frequency field specifies the fundamental frequency of
 * the a component to be synthesized. This value is ignored for some types of synthesis.
 *
 * <p>The Relative Amplitude field specifies the relative amplitude of a component
 * to be synthesized relative to other components. At the time of additive synthesis
 * the amplitude of all components are normalized based on this.
 *
 * <p>The Max Fraction of Sampling Rate field is used to determine the maximum 
 * allowable partial that will be permitted to be synthesized. This is to avoid aliasing,
 * and generally a value of 0.45 is best, with values below 0.5 always needed to ensure
 * protection against aliasing. The maximum allowable frequency is determined
 * by multiplying this value by the sampling rate used.
 * 
 * @author	Cory McKay
 */
public class SynthesisFrame 
	extends JFrame
	implements ActionListener
{
	/* FIELDS ******************************************************************/
	
	static final long serialVersionUID = 1;

	/**
	 * Used to hold the data describing components that have been selected
	 * for use in synthesis. Is null if none are selected.
	 */
	private SynthesisParameters					selected_components;

	/*
	 * A thread used to preview synthesized audio.
	 */
	private	AudioMethodsPlayback.PlayThread		playback_thread;

	/**
	 *The GUI component that instantiated this object
	 */
//	private RecordingSelectorPanel				parent_window;

	Controller controller;
	
	/**
	 * Dialog box to choose audio format for recording
	 */
	private AudioFormatJFrame					audio_format_selector;

	/**
	 * JFileChooser for saving recorded audio.
	 */
	private JFileChooser						save_file_chooser;

	/**
	 * GUI elements for individual components of synthesis
	 */
	private JComboBox							synthesis_selection_combo_box;
	private	JTextArea							fund_freq_text_area;
	private JTextArea							relative_amplitude_multiplier_text_area;
	private JTextArea							max_fraction_of_sampling_rate_text_area;

	/**
	 * Display of synthesis components added
	 */
	private JTextArea							selected_components_text_area;
	private JScrollPane							selected_components_scroll_pane;

	/**
	 * GUI elements for overall synthesis
	 */
	private JTextArea							duration_text_area;
	private	JTextArea							gain_text_area;
	private	JTextArea							panning_text_area;
	private JTextArea							click_avoid_env_length_text_area;

	/**
	 * GUI buttons
	 */
	private JButton								choose_encoding_format_button;
	private JButton								add_synthesis_component_button;
	private JButton								clear_synthesis_components_button;
	private JButton								play_button;
	private JButton								stop_playback_button;
	private JButton								save_button;
	private JButton								cancel_button;


	/**
	 * GUI combo box
	 */
	private JComboBox							choose_file_format_combo_box;


	/* CONSTRUCTOR *************************************************************/


	/**
	 * Set up the GUI.
	 *
	 * @param c near global controller.
	 */
	public SynthesisFrame(Controller c)
	{
		// Set window title
		setTitle("Synthesize Audio");
		getContentPane().setBackground(new java.awt.Color((float)0.75,(float)0.85,(float)1.0));

		// Cause program to react when the exit box is pressed
		addWindowListener(new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				cancel();
			}
		});

		// Set initial selected components to none
		selected_components = null;
		
		// Initialize to nothing to play
		playback_thread = null;

		// Initialize to no save dialog box
		save_file_chooser = null;

		// Store parent window
//		this.parent_window = parent_window;
		controller = c;

		// Initialize the AudioFormatJFrame
		audio_format_selector = new AudioFormatJFrame();
		AudioFormat default_format = AudioFormatJFrame.getStandardMidQualityRecordAudioFormat();
		audio_format_selector.setAudioFormat(default_format);

		// Initialize combo box and add eligible synthesis types to it
		String synthesis_types[] = AudioMethodsSynthesis.getSynthesisNames();
		synthesis_selection_combo_box = new JComboBox(synthesis_types);
		synthesis_selection_combo_box.setBackground(this.getContentPane().getBackground());

		// Initialize other GUI elements for individual components of synthesis
		fund_freq_text_area = new JTextArea("440.0");
		relative_amplitude_multiplier_text_area = new JTextArea("1.0");
		max_fraction_of_sampling_rate_text_area = new JTextArea("0.45");

		// Initialize other GUI elements for overall synthesis
		duration_text_area = new JTextArea("1.0");
		gain_text_area = new JTextArea("0.8");
		panning_text_area = new JTextArea("0.0");
		click_avoid_env_length_text_area = new JTextArea("0.05");
		
		// Initialize elements for display of synthesis
		selected_components_text_area = new JTextArea();
		selected_components_text_area.setEditable(false);
		selected_components_text_area.setLineWrap(false);
		selected_components_text_area.setRows(4);
		selected_components_scroll_pane = new JScrollPane(selected_components_text_area);

		// Initialize buttons and combo box
		choose_encoding_format_button = new JButton("Choose Audio Format");
		choose_encoding_format_button.addActionListener(this);
		add_synthesis_component_button = new JButton("Add Component");
		add_synthesis_component_button.addActionListener(this);
		clear_synthesis_components_button = new JButton("Clear All Components");
		clear_synthesis_components_button.addActionListener(this);
		play_button = new JButton("Play");
		play_button.addActionListener(this);
		stop_playback_button = new JButton("Stop Playback");
		stop_playback_button.addActionListener(this);
		save_button = new JButton("Save");
		save_button.addActionListener(this);
		cancel_button = new JButton("Cancel");
		cancel_button.addActionListener(this);
		choose_file_format_combo_box = new JComboBox();
		String file_types[] = AudioMethods.getAvailableFileFormatTypes();
		for (int i = 0; i < file_types.length; i++)
			choose_file_format_combo_box.addItem(file_types[i]);
		choose_file_format_combo_box.setBackground(this.getContentPane().getBackground());
		// Prepare containers
		int horizontal_gap = 6; // horizontal space between GUI elements
		int vertical_gap = 11; // horizontal space between GUI elements
		setLayout(new BorderLayout(horizontal_gap, vertical_gap));
		JPanel selections_panel = new JPanel(new GridLayout(13, 2, horizontal_gap, vertical_gap));
		selections_panel.setBackground(this.getContentPane().getBackground());
		JPanel report_panel = new JPanel(new BorderLayout(horizontal_gap, vertical_gap));
		report_panel.setBackground(this.getContentPane().getBackground());
		JPanel buttons_panel = new JPanel(new GridLayout(4, 2, horizontal_gap, vertical_gap));
		buttons_panel.setBackground(this.getContentPane().getBackground());
		
		// Add elements to containers
		selections_panel.add(new JLabel("OVERALL SYNTHESIS:"));
		selections_panel.add(new JLabel(""));
		selections_panel.add(new JLabel("Synthesis Encoding Format:"));
		selections_panel.add(choose_encoding_format_button);
		selections_panel.add(new JLabel("Duration (sec):"));
		selections_panel.add(duration_text_area);
		selections_panel.add(new JLabel("Gain (0.0 to 1.0):"));
		selections_panel.add(gain_text_area);
		selections_panel.add(new JLabel("Panning (-1.0 to 1.0):"));
		selections_panel.add(panning_text_area);
		selections_panel.add(new JLabel("Click Avoidance Env. Length (sec):"));
		selections_panel.add(click_avoid_env_length_text_area);
		selections_panel.add(new JLabel(""));
		selections_panel.add(new JLabel(""));
		selections_panel.add(new JLabel("INDIVIDUAL COMPONENTS:"));
		selections_panel.add(new JLabel(""));
		selections_panel.add(new JLabel("Synthesis Type:"));
		selections_panel.add(synthesis_selection_combo_box);
		selections_panel.add(new JLabel("Fundamental Frequency (Hz):"));
		selections_panel.add(fund_freq_text_area);
		selections_panel.add(new JLabel("Relative Amplitude (0.0 to 1.0):"));
		selections_panel.add(relative_amplitude_multiplier_text_area);
		selections_panel.add(new JLabel("Max Fraction of Sampling Rate:"));
		selections_panel.add(max_fraction_of_sampling_rate_text_area);
		selections_panel.add(clear_synthesis_components_button);
		selections_panel.add(add_synthesis_component_button);
		report_panel.add(selected_components_scroll_pane);
		buttons_panel.add(play_button);
		buttons_panel.add(stop_playback_button);
		buttons_panel.add(new JLabel("File Format For Saving:"));
		buttons_panel.add(choose_file_format_combo_box);
		buttons_panel.add(new JLabel(""));
		buttons_panel.add(new JLabel(""));
		buttons_panel.add(cancel_button);
		buttons_panel.add(save_button);
		add(selections_panel, BorderLayout.NORTH);
		add(report_panel, BorderLayout.CENTER);
		add(buttons_panel, BorderLayout.SOUTH);

		// Display GUI
		pack();
		setVisible(true);
	}


	/* PUBLIC METHODS **********************************************************/


	/**
	 * Calls the appropriate methods when the buttons are pressed.
	 *
	 * @param	event		The event that is to be reacted to.
	 */
	public void actionPerformed(ActionEvent event)
	{
		// React to the choose_encoding_format_button
		if (event.getSource().equals(choose_encoding_format_button))
			audio_format_selector.setVisible(true);

		// React to the add_synthesis_component_button
		else if (event.getSource().equals(add_synthesis_component_button))
			addSynthesisComponent();

		// React to the clear_synthesis_components_button
		else if (event.getSource().equals(clear_synthesis_components_button))
			clearSynthesisComponents();

		// React to the play_button
		else if (event.getSource().equals(play_button))
			play();

		// React to the stop_playback_button
		else if (event.getSource().equals(stop_playback_button))
			stopPlayback();

		// React to the record_analyze_synthesis_button
		else if (event.getSource().equals(save_button))
			save();

		// React to the cancel_button
		else if (event.getSource().equals(cancel_button))
			cancel();
	}


	/* PRIVATE METHODS *********************************************************/


	/**
	 * Synthesize audio as selected on GUI and plays it. Does not store 
	 * synthesized audio anywhere. Interrupts any test playing that is already in progress.
	 *
	 * <b>IMPORTANT:</b> Note that this method could cause the system to run out of
	 * memory if synthesis is too long.
	 */
	private void play()
	{
		try
		{
			// Stop playing anyting that is in progress			
			if (playback_thread != null)
				stopPlayback();

			// Generate samples to play
			byte[] audio_data = performAdditiveSynthesis();

			// Play the generated samples
			AudioFormat audio_format = audio_format_selector.getAudioFormat(false);
			AudioInputStream audio_input_stream = AudioMethods.getInputStream(audio_data, audio_format);
			SourceDataLine source_data_line = AudioMethods.getSourceDataLine(audio_format, null);
			playback_thread = AudioMethodsPlayback.playAudioInputStreamInterruptible( audio_input_stream,
																					  source_data_line );
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * Stop any playback in progress. 
	 */
	private void stopPlayback()
	{
		if (playback_thread != null)
		{
			playback_thread.stopPlaying();
			playback_thread = null;
		}
	}


	/**
	 * Synthesizes audio using the selected parameters, saves it to a file,
	 * passes a reference to the file to the parent_window and hides this window.
	 * Gives an error message if no synthesis components are specified. Ends
	 * any playback in progress.
	 *
	 * <b>IMPORTANT:</b> Note that this method could cause the system to run out of
	 * memory if synthesis is too long.
	 */
	private void save()
	{
		// Stop playback that is in progress			
		if (playback_thread != null)
			stopPlayback();

		try
		{
			// Generate samples to play
			byte[] audio_data = performAdditiveSynthesis();
			AudioFormat audio_format = audio_format_selector.getAudioFormat(false);
			AudioInputStream audio_input_stream = AudioMethods.getInputStream(audio_data, audio_format);

			// Initialize the save_file_chooser if it has not been opened yet
			if (save_file_chooser == null)
			{
				save_file_chooser = new JFileChooser();
				save_file_chooser.setCurrentDirectory(new File("."));
				save_file_chooser.setFileFilter(new FileFilterAudio());
			}

			// Save the synthesized audio and send the reference to the parent window
			// if the user chooses OK. Also hide this window.
			int dialog_result = save_file_chooser.showSaveDialog(SynthesisFrame.this);
			if (dialog_result == JFileChooser.APPROVE_OPTION) // only do if OK chosen
			{
				// Prepare a temporary File
				File save_file = save_file_chooser.getSelectedFile();
				boolean proceed = true;
				
				// Verify that the file has the correct extension
				String correct_format_name = (String) choose_file_format_combo_box.getSelectedItem();
				AudioFileFormat.Type correct_format = AudioMethods.getAudioFileFormatType(correct_format_name);
				save_file = ensureCorrectExtension(save_file, correct_format);

				// See if user wishes to overwrite if a file with the same name exists
				if (save_file.exists())
				{
					int overwrite = JOptionPane.showConfirmDialog( null,
																  "This file already exists.\nDo you wish to overwrite it?",
																  "WARNING",
																  JOptionPane.YES_NO_OPTION );
					if (overwrite != JOptionPane.YES_OPTION)
						proceed = false;
				}

				// If appropriate, save the final File, add it to the parent window
				// and close this window
				if (proceed)
				{
					try
					{
						AudioMethods.saveToFile(audio_input_stream, save_file, correct_format);
						File[] to_add_to_table = new File[1];
						to_add_to_table[0] = save_file;
						controller.addRecordingsAction.addRecording(to_add_to_table);
						cancel();
					}
					catch (Exception e)
					{
						JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);							
					}
				}
			}
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * Hides this window and ends any playback in progress.
	 */
	private void cancel()
	{
		stopPlayback();
		this.setVisible(false);
	}


	/**
	 * Add the Individual Component Settings to the selected_components field
	 * and to the selected_components_text_area.
	 */
	private void addSynthesisComponent()
	{
		// Extract parameters from GUI settings
		String synthesis_type_name = (String) synthesis_selection_combo_box.getSelectedItem();
		double fundamental_frequency = Double.parseDouble(fund_freq_text_area.getText());
		double relative_amplitude = Double.parseDouble(relative_amplitude_multiplier_text_area.getText());
		double max_allowable_fraction_of_sampling_rate = Double.parseDouble(max_fraction_of_sampling_rate_text_area.getText());

		try
		{
			// Store selected parameters
			if (selected_components == null)
				selected_components = new SynthesisParameters(synthesis_type_name, fundamental_frequency, relative_amplitude, max_allowable_fraction_of_sampling_rate);
			else
			{
				SynthesisParameters temp = selected_components;
				while (temp.next_set_of_parameters != null)
					temp = temp.next_set_of_parameters;
				temp.next_set_of_parameters = new SynthesisParameters(synthesis_type_name, fundamental_frequency, relative_amplitude, max_allowable_fraction_of_sampling_rate);
			}

			// Add selected parameters to JTextArea
			selected_components_text_area.append(synthesis_type_name + " " + fundamental_frequency + " Hz " + relative_amplitude + " rel. amp " + max_allowable_fraction_of_sampling_rate + " max. frac. SR\n");
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * Remove any synthesis components that have been added, both from memory
	 * and from the display.
	 */
	private void clearSynthesisComponents()
	{
		selected_components_text_area.setText("");
		selected_components = null;
	}


	/**
	 * Synthesizes the audio described in the selected_components field and the
	 * components of the GUI. Uses additive synthesis to combine all of these components
	 * together.
	 * 
	 * <p>The amplitude of each component is scaled based on its <i>relative_amplitude</i>
	 * value so that no clipping occurs when the samples are added.
	 *
	 * <p><b>WARNING:</b> This could cause the system to run out of memory if too much
	 * audio needs to by synthesized.
	 *
	 * @return					An array of bytes populated with audio samples based on the
	 *							settings selected in the <i>selected_components</i> field
	 *							and the settings in teh GUI.
	 * @throws	Exception		Throws an exception if invalid parameters are selected in GUI
	 *							or if no components have been added yet.
	 */
	private byte[] performAdditiveSynthesis()
		throws Exception
	{
		// Get the overall parameters for the synthesis
		AudioFormat audio_format = audio_format_selector.getAudioFormat(false);
		double gain = Double.parseDouble(gain_text_area.getText());
		double panning = Double.parseDouble(panning_text_area.getText());
		double duration = Double.parseDouble(duration_text_area.getText());
		double click_avoid_env_length = Double.parseDouble(click_avoid_env_length_text_area.getText());

		// Throw an exception if a negative duration or too long (over 2 hours)
		// a duration is selected. Maximum duration is there to avoid memory errors
		// and int overflows.
		if (duration > 7200)
			throw new Exception( "You have selected a duration of " + duration + " seconds,\n" +
								 "which is too long. Duration must be less than 2 hours.");
		if (duration <= 0)
			throw new Exception( "You have selected a duration of " + duration + "seconds.\n" +
								 "Duration must be over seconds.");

		// Find the number of components and the total of their amplitude multipliers.
		// Throw an exception if no components are available.
		int number_components = 0;
		double total_multipliers = 0.0;
		if (selected_components == null)
			throw new Exception("No additive synthesis components have been added yet.");
		else
		{
			SynthesisParameters temp = selected_components;
			while (temp.next_set_of_parameters != null)
			{
				number_components++;
				total_multipliers += temp.relative_amplitude;
				temp = temp.next_set_of_parameters;
			}
			number_components++;
			total_multipliers += temp.relative_amplitude;
		}

		// Find the true amplitude multiplier for each component
		double[] multipliers = new double[number_components];
		SynthesisParameters temp = selected_components;
		for (int i = 0; i < number_components; i++)
		{
			multipliers[i] = temp.relative_amplitude / total_multipliers;
			temp = temp.next_set_of_parameters;
		}

		// Calculate each component of the synthesis separately
		double[][][] components = new double[number_components][][];
		temp = selected_components;
		for (int comp = 0; comp < number_components; comp++)
		{
			int synthesis_type_code = AudioMethodsSynthesis.getSynthesisTypeCode(temp.synthesis_type);
			components[comp] = AudioMethodsSynthesis.synthesizeAndWriteToBuffer( null,
		                                                                         duration,
													                             audio_format,
														                         synthesis_type_code,
														                         gain,
														                         panning,
													                             temp.fundamental_frequency,
		                                                                         temp.max_fraction_of_sampling_rate,
		                                                                         click_avoid_env_length );
			temp = temp.next_set_of_parameters;
		}

		// Combine all components using additive synthesis and the appropriate
		// amplitude multipliers
		double[][] combined_components = new double[ components[0].length ][ components[0][0].length ];
		for (int chan = 0; chan < combined_components.length; chan++)
			for (int samp = 0; samp < combined_components[chan].length; samp++)
				combined_components[chan][samp] = 0.0;
		for (int samp = 0; samp < combined_components[0].length; samp++)
			for (int comp = 0; comp < components.length; comp++)
				for (int chan = 0; chan < combined_components.length; chan++)
					combined_components[chan][samp] += (multipliers[comp] * components[comp][chan][samp]);

		// Translate these sample values into an array of audio bytes
		int number_bytes_needed = AudioMethods.getNumberBytesNeeded( combined_components[0].length,
	                                                                 audio_format );
		byte[] audio_data = new byte[number_bytes_needed];
		AudioMethodsSynthesis.writeSamplesToBuffer( combined_components,
	                                                audio_format.getSampleSizeInBits(),
	                                                audio_data );

		// Store the AudioFormat of audio_data
		return audio_data;
	}


	/**
	 * Ensures that the given file has an extension corresponding to the given
	 * AudioFileFormat.Type. If it does not, then displays a warning
	 * and returns a File with the correct extension. Otherwise returns
	 * the original File.
	 *
	 * @param	file_to_verify		The File whose extension is to be validated.
	 * @param	file_format_type	The type of encoding that should correspond to the file.
	 * @return						The original file_to_verify if it had the correct
	 *								extension, and a new File with the correct
	 *								extension if it did not.
	 */
	private File ensureCorrectExtension(File file_to_verify, AudioFileFormat.Type file_format_type)
	{
		// Find the correct file extension
		String correct_extension = "." + file_format_type.getExtension();

		// Ensure that the file has the extension corresponding to its type
		String path = file_to_verify.getAbsolutePath();
		String ext = StringMethods.getExtension(path);
		if (ext == null)
			path += correct_extension;
		else if (!ext.equals(correct_extension))
			path = StringMethods.removeExtension(path) + correct_extension;
		else
			return file_to_verify;
		JOptionPane.showMessageDialog(null, "Incorrect file extension specified.\nChanged from " + ext + " to " + correct_extension + ".", "WARNING", JOptionPane.ERROR_MESSAGE);
		return new File(path);
	}

	
	/* INTERNAL CLASSES ********************************************************/


	/**
	 * Holds the parameters for a component of synthesis that can be used
	 * for additive synthesis. Also holds a reference to the next set
	 * of parameters, if any, so that a sequence of parameters, one for
	 * each component, can be strung together.
	 */
	private class SynthesisParameters
	{
		// Fields describing the parameters for one synthesis component
		public String synthesis_type;
		public double fundamental_frequency;
		public double relative_amplitude;
		public double max_fraction_of_sampling_rate;

		// Field giving a pointer to the next set of parameters.
		// Null if there are none.
		public SynthesisParameters next_set_of_parameters;

		/**
		 * Constructor setting fields. Throws an informative exception if
		 * fields have invalid parameters.
		 */
		public SynthesisParameters( String synthesis_type,
		                            double fundamental_frequency,
		                            double relative_amplitude,
		                            double max_fraction_of_sampling_rate )
			throws Exception
		{
			// Throw exceptions for invalid parameters
			String synthesis_types[] = AudioMethodsSynthesis.getSynthesisNames();
			boolean type_ok = false;
			for (int i = 0; i < synthesis_types.length; i++)
				if (synthesis_types[i].equals(synthesis_type))
					type_ok = true;
			if (!type_ok)
				throw new Exception("Synthesis type " + synthesis_type + " is unknown.");
			if (fundamental_frequency <= 0.0)
				throw new Exception( "Fundamental frequency is " + fundamental_frequency + ".\n" +
				                     "This value must be above zero." );
			if (relative_amplitude < 0.0 || relative_amplitude > 1.0)
				throw new Exception( "Relative amplitude is " + relative_amplitude + ".\n" +
				                     "This value must be between 0 and 1." );
			if (max_fraction_of_sampling_rate <= 0.0)
				throw new Exception( "Maximum fraction of sampling rate is " + max_fraction_of_sampling_rate + ",\n" +
				                     "This value must be above zero." );

			// Set fields
			this.synthesis_type = synthesis_type;
			this.fundamental_frequency = fundamental_frequency;
			this.relative_amplitude = relative_amplitude;
			this.max_fraction_of_sampling_rate = max_fraction_of_sampling_rate;
			next_set_of_parameters = null;
		}
	}
}