/*
 * @(#)MIDIMethodsPlayback.java	1.0	April 5, 2005.
 *
 * Cory McKay
 * McGill Univarsity
 */

package jAudioFeatureExtractor.jMIDITools;

import javax.sound.midi.*;
import java.io.*;


/**
 * A holder class for static methods and internal classes relating to playing
 * MIDIrecordings.
 *
 * @author	Cory McKay
 */
public class MIDIMethodsPlayback
{
	/* PUBLIC METHODS **********************************************************/


	/**
	 * Plays the given MIDI Sequence and returns the
	 * Sequencer that is playing it. The default system
	 * Sequencer and Synthesizer are used.
	 *
	 * @param	midi_sequence	The MIDI sequence to play
	 * @return					A sequencer that is playing the midi_sequence
	 * @throws	Exception		Throws an exception if an empty MIDI sequence
	 *							is passed as an argument or if cannoth play.
	 */
	public static Sequencer playMIDISequence(Sequence midi_sequence)
		throws Exception
	{
		// Throw exception if empty midi_sequence passed
		if (midi_sequence == null)
			throw new Exception("No MIDI data passed for playback.");
		
		// Acquire a MIDI Sequencer from the system
		Sequencer sequencer = MidiSystem.getSequencer();
		if (sequencer == null)
			throw new Exception("Could not acquire a MIDI sequencer from the system.");

		// Prepare a holder for a MIDI Synthesizer
		Synthesizer synthesizer = null;

		// Open the sequencer
		sequencer.open();

		// Feed the sequencer the sequence it is to play
		sequencer.setSequence(midi_sequence);

		// Set the desinations that the Sequence should be played on.
		// Some Java Sound implemntations combine the default
		// sequencer and the default synthesizer into one. This
		// checks if this is the case, and forms the needed
		// connections if it is not the case.
		if ( !(sequencer instanceof Synthesizer))
		{
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
			Receiver synth_receiver = synthesizer.getReceiver();
			Transmitter	seq_transmitter = sequencer.getTransmitter();
			seq_transmitter.setReceiver(synth_receiver);
		}

		// Begin playback
		sequencer.start();

		// Return the sequencer that is performing playback
		return sequencer;
	}
}