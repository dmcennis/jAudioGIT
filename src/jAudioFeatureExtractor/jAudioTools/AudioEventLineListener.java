/*
 * @(#)AudioEventLineListener.java	1.0	April 5, 2005.
 *
 * Cory McKay
 * McGill Univarsity
 */

package jAudioFeatureExtractor.jAudioTools;

import javax.sound.sampled.*;


/**
 * An implementation of the <code>LineListener</code> interface. Objects of this
 * class can be attached to audio <code>Line</code> objects, and react to open,
 * start, stop or close events that they generate.
 *
 * <p>This particular implementation prints data to regarding each such event to
 * standard out. The information includes the type of event, what type of line
 * it occured on, a unique code for the particular line that fired the event
 * and the sample frame on the line that the event happened on.
 *
 * <p>Note that all this data may not be available on all systems, as some of the 
 * data parsing that was used may be system specific.
 *
 * <p>Note that some information produced regarding the line details and the 
 * overview is not outputted, as it is unlikely to be useful.
 *
 * @author	Cory McKay
 */
public class AudioEventLineListener
	implements LineListener
{
	public void update(LineEvent event)
	{
		// The event that is being reacted to
		Line line_firing_event = event.getLine();
		
		// Data provided by the event
		String event_type = event.getType().toString();
		String line_info = line_firing_event.getLineInfo().toString();
		String event_position = Long.toString(event.getFramePosition());
		String event_overview = event.toString();

		// First distillation of data
		String line_type = line_info.substring(10);
		int space_location = line_type.indexOf(" ");
		if (space_location != -1)
			line_type = line_type.substring(0, space_location);

		// Second distillation of data
		String line_instance = "";
		int dollar_location = event_overview.indexOf("$");
		if (dollar_location == -1) 
		{
			dollar_location = event_overview.indexOf("@");
			if (dollar_location != -1)
				line_instance = event_overview.substring(dollar_location);
		}
		else
			line_instance = event_overview.substring(dollar_location);

		// Output data
		System.out.print("---------------------------------------------\n");
		System.out.print("LINE EVENT REPORT:\n");
		System.out.print("---------------------------------------------\n");
		System.out.print("EVENT TYPE: " + event_type + "\n");
		System.out.print("LINE TYPE: " + line_type + "\n");
		System.out.print("LINE INSTANCE: " + line_instance + "\n");
		System.out.print("EVENT POSITION (in sample frames): " + event_position + "\n");
		// System.out.print("LINE DETAILS: " + line_info + "\n");
		// System.out.print("OVERVIEW: " + event.toString() + "\n");
		System.out.print("---------------------------------------------\n\n");
	}
}