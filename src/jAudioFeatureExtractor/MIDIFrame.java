/*
 * @(#)MIDIFrame.java	1.0	April 5, 2005.
 *
 * Cory McKay
 * McGill Univarsity
 */

package jAudioFeatureExtractor;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import jAudioFeatureExtractor.jMIDITools.*;


/**
 * This Frame allows the user to load MIDI files using the
 * Load MIDI File Button and play them as audio using the
 * Play MIDI and Stop Playback buttons. This audio can then
 * be recorded (and analyzed) as audio samples and
 * stored by any other object that records audio in the system,
 * such as a RecordingFrame. The Done button hides this frame
 * and stops any playback currently in progress.
 *
 * <b>WARNING:</b> It may be necessary to adjust the MIDI synthesis
 * volume externally in order to avoid clipping when recording it
 * as audio.
 * 
 * <b>WARNING:</b> Do not press the Play MIDI button repeatedly
 * very quickly as this may prevent the playing of MIDI files.
 *
 * @author	Cory McKay
 */
public class MIDIFrame
	extends JFrame
	implements ActionListener
{
	/* FIELDS ******************************************************************/
	
	static final long serialVersionUID = 1;

	
	/**
	 * The GUI component that instantiated this object
	 */
	private Controller	controller;

	/**
	 * The currently loaded MIDI file contents and a Sequencer to play it
	 */
	private Sequence				midi_sequence;
	private Sequencer				midi_sequencer;

	/**
	 * A JFileChooser for loading MIDI files
	 */
	private JFileChooser			load_file_chooser;
	
	/**
	 * A JTextArea to display the currently loaded MIDI file path
	 */
	private JTextArea				midi_file_name_text_area;

	/**
	 * GUI buttons
	 */
	private JButton					load_midi_file_button;
	private JButton					play_midi_button;
	private JButton					stop_playing_midi_button;
	private JButton					done_button;


	/* CONSTRUCTOR *************************************************************/


	/**
	 * Sets up the GUI.
	 *
	 */
	public MIDIFrame()
	{
		// Set window title
		setTitle("Load and Play MIDI Files");
		Color blue = new Color((float)0.75,(float)0.85,(float)1.0);
		this.getContentPane().setBackground(blue);

		// Cause program to react when the exit box is pressed
		addWindowListener(new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				done();
			}
		});

		// Set the MIDI file chooser dialog and the midi file contents to null
		midi_sequence = null;
		midi_sequencer = null;
		load_file_chooser = null;
		
		// Store parent window
		//this.parent_window = parent_window;

		// Initialize the midi_file_path_text_area
		midi_file_name_text_area = new JTextArea();
		midi_file_name_text_area.setEditable(false);
		midi_file_name_text_area.setLineWrap(false);
		midi_file_name_text_area.setRows(1);

		// Initialize buttons
		load_midi_file_button = new JButton("Load MID File");
		load_midi_file_button.addActionListener(this);
		play_midi_button = new JButton("Play MIDI");
		play_midi_button.addActionListener(this);
		stop_playing_midi_button = new JButton("Stop Playback");
		stop_playing_midi_button.addActionListener(this);
		done_button = new JButton("Done");
		done_button.addActionListener(this);

		// Add elements
		int horizontal_gap = 6; // horizontal space between GUI elements
		int vertical_gap = 11; // horizontal space between GUI elements
		setLayout(new GridLayout(6, 2, horizontal_gap, vertical_gap));
		add(load_midi_file_button);
		add(new JLabel(""));
		add(new JLabel("Currently Loaded File Name:"));
		add(midi_file_name_text_area);
		add(new JLabel(""));
		add(new JLabel(""));
		add(play_midi_button);
		add(stop_playing_midi_button);
		add(new JLabel(""));
		add(new JLabel(""));
		add(new JLabel(""));
		add(done_button);

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
		// React to the load_midi_file_button
		if (event.getSource().equals(load_midi_file_button))
			loadMIDIFile();

		// React to the play_midi_button
		else if (event.getSource().equals(play_midi_button))
			playMIDI();

		// React to the stop_playing_midi_button
		else if (event.getSource().equals(stop_playing_midi_button))
			stopMIDIPlayback();

		// React to the done_button
		else if (event.getSource().equals(done_button))
			done();
	}


	/* PRIVATE METHODS *********************************************************/


	/**
	 * Instantiates a JFileChooser for the load_file_chooser if 
	 * one does not already exist. This dialog box allows the user to choose
	 * a file from which to load MIDI into the midi_sequence field. Only MIDI
	 * files (.mid or .midi extensions) are displayed. Displays the name of the loaded
	 * file in the midi_file_name_text_area if loading is succesful.
	 */
	private void loadMIDIFile()
	{
		// Initialize the load_file_chooser if it has not been opened yet
		if (load_file_chooser == null)
		{
			load_file_chooser = new JFileChooser();
			load_file_chooser.setCurrentDirectory(new File("."));
			load_file_chooser.setFileFilter(new FileFilterMIDI());
		}

		// Allow the user to select the file to load
		File midi_file = null;
		int dialog_result = load_file_chooser.showOpenDialog(MIDIFrame.this);

		// Load the file into the midi_sequence field and update the midi_file_name_text_area
		if (dialog_result == JFileChooser.APPROVE_OPTION) // only do if OK chosen
		{
			midi_file = load_file_chooser.getSelectedFile();
			if ( midi_file.exists() )
			{
				try
				{
					midi_sequence = MidiSystem.getSequence(midi_file);
					midi_file_name_text_area.setText(midi_file.getName());
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);							
				}
			}
			else
				JOptionPane.showMessageDialog(null, "The selected file does not exist.", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * Play any MIDI that has been loaded from a MIDI file. No analysis occurs.
	 * Display an error message if no MIDI file has been loaded. Stops any MIDI
	 * playback already in progress.
	 */
	private void playMIDI()
	{
		try
		{
			if (midi_sequencer != null)
				stopMIDIPlayback();
			if (midi_sequence == null)
				throw new Exception("No MIDI file has been loaded yet.");
			midi_sequencer = MIDIMethodsPlayback.playMIDISequence(midi_sequence);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);							
		}
	}


	/**
	 * Stop any playback of a MIDI file in progress.
	 */
	private void stopMIDIPlayback()
	{
		if (midi_sequencer != null)
		{
			midi_sequencer.stop();
			midi_sequencer = null;
		}
	}
	
	
	/**
	 * Stop any playback in progress and hide this window.
	 */
	private void done()
	{
		stopMIDIPlayback();
		this.setVisible(false);
	}
}