/*
 * @(#)AudioFormatJFrame.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.jAudioTools;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.sound.sampled.*;


/**
 * <code>JFrame</code> used to select encoding options for recording or synthsizing audio. 
 * These options include sampling rate, bit depth, number of channels, whether samples
 * are signed or not and byte order. Note that only PCM encoding is accounted for
 * here. Not all sound cards will support all settings.
 *
 * <p>Options that are not available on the radio buttons can be entered in the
 * text fields. The contents of text fields are ignored unless the corresponding
 * <i>Other</i> radio button is selected
 *
 * <p>The <i>Low Quality Settings</i>, <i>Mid Quality Settings</i> and <i>High 
 * Quality Settings</i> buttons set the GUI settings to pre-defined defaults.
 *
 * <p>It is not necessary to press the <i>OK</i> button in order for changes to be 
 * accessible to external classes. However, pressing the <i>Cancel</i> button will
 * restore settings to those that were selected when the <code>JFrame</code> was
 * last made visible.
 *
 * <p>The GUI settings may be set or accessed externally by the <code>setAudioFormat</code>
 * and <code>getAudioFormat</code> methods respectively.
 *
 * <p>This class also includes several static methods that are unrelated to the GUI
 * settings. These include the <code>getStandardLowQualityRecordAudioFormat</code>,
 * <code>getStandardMidQualityRecordAudioFormat</code> and 
 * <code>getStandardHighQualityRecordAudioFormat</code> methods which return
 * default <code>AudioFormat</code> presets corresponding to the buttons with
 * similar names. The <code>defineAudioFormat</code> static method does the same thing
 * as the basic PCM <code>AudioFormat</code> constructor, but is better documented.
 *
 * @author Cory McKay
 */
 public class AudioFormatJFrame
	extends JFrame
	implements ActionListener
{
	/* FIELDS ******************************************************************/
		
	static final long serialVersionUID = 1;

	// Temporarily holds the AudioFormat corresponding to the GUI's settings whenever
	// the setVisible method is called. For use in restoring settings if the cancel
	// button is pressed.
	AudioFormat temp_format;

	// Containers to hold Swing elements
	private Container		content_pane;
	private JPanel			settings_panel;
	private JPanel			button_panel;
	
	// The sampling rate selectors
	private ButtonGroup		sampling_rate_rb_group;
	private JRadioButton	sr_8000_rb;
	private JRadioButton	sr_11025_rb;
	private JRadioButton	sr_16000_rb;
	private JRadioButton	sr_22050_rb;
	private JRadioButton	sr_44100_rb;
	private JRadioButton	sr_other_rb;
	private JTextArea		sr_text_area;

	// The bit depth selectors
	private ButtonGroup		bit_depth_rb_group;
	private JRadioButton	bd_8_rb;
	private JRadioButton	bd_16_rb;
	private JRadioButton	bd_other_rb;
	private JTextArea		bd_text_area;

	// The channels selectors
	private ButtonGroup		channels_rb_group;
	private JRadioButton	chan_1_rb;
	private JRadioButton	chan_2_rb;
	private JRadioButton	chan_other_rb;
	private JTextArea		chan_text_area;

	// The signed selectors
	private ButtonGroup		signed_rb_group;
	private JRadioButton	signed_rb;
	private JRadioButton	unsigned_rb;

	// The endian selectors
	private ButtonGroup		endian_rb_group;
	private JRadioButton	big_endian_rb;
	private JRadioButton	little_endian_rb;

	// Buttons
	private JButton			low_quality_button;
	private JButton			mid_quality_button;
	private JButton			high_quality_button;
	private JButton			cancel_button;
	private JButton			ok_button;


	/* CONSTRUCTOR *************************************************************/


	/**
	 * Basic constructor. Configures the panel and its fields to low quality audio.
	 * Prepares the <code>JFrame</code>, but does not show it. The <code>setVisible</code>
	 * method must be called externally to show this.
	 */
	public AudioFormatJFrame()
	{
		Color blue = new Color((float)0.75,(float)0.85,(float)1.0);
		// Instantiate sampling rate GUI elements
		sampling_rate_rb_group = new ButtonGroup();
		sr_8000_rb = new JRadioButton("8 kHz");
		sr_8000_rb.setBackground(blue);
		sr_11025_rb = new JRadioButton("11.025 kHz");
		sr_11025_rb.setBackground(blue);
		sr_16000_rb = new JRadioButton("16 kHz");
		sr_16000_rb.setBackground(blue);
		sr_22050_rb = new JRadioButton("22.05 kHz");
		sr_22050_rb.setBackground(blue);
		sr_44100_rb = new JRadioButton("44.1 kHz");
		sr_44100_rb.setBackground(blue);
		sr_other_rb = new JRadioButton("Other (kHz):");
		sr_other_rb.setBackground(blue);
		sr_text_area = new JTextArea("");

		// Instantiate bit depth GUI elements
		bit_depth_rb_group = new ButtonGroup();
		bd_8_rb = new JRadioButton("8 bit");
		bd_8_rb.setBackground(blue);
		bd_16_rb = new JRadioButton("16 bit");
		bd_16_rb.setBackground(blue);
		bd_other_rb = new JRadioButton("Other:");
		bd_other_rb.setBackground(blue);
		bd_text_area = new JTextArea("");

		// Instantiate channels GUI elements
		channels_rb_group = new ButtonGroup();
		chan_1_rb = new JRadioButton("Mono");
		chan_1_rb.setBackground(blue);
		chan_2_rb = new JRadioButton("Stereo");
		chan_2_rb.setBackground(blue);
		chan_other_rb = new JRadioButton("Other:");
		chan_other_rb.setBackground(blue);
		chan_text_area = new JTextArea("");

		// Instantiate signed GUI elemens
		signed_rb_group = new ButtonGroup();
		signed_rb = new JRadioButton("Signed PCM");
		signed_rb.setBackground(blue);
		unsigned_rb = new JRadioButton("Unsigned PCM");
		unsigned_rb.setBackground(blue);

		// Instantiate endian GUI elements
		endian_rb_group = new ButtonGroup();;
		big_endian_rb = new JRadioButton("Big Endian");
		big_endian_rb.setBackground(blue);
		little_endian_rb = new JRadioButton("Little Endian");
		little_endian_rb.setBackground(blue);

		// Add radio buttons to their groups
		sampling_rate_rb_group.add(sr_8000_rb);
		sampling_rate_rb_group.add(sr_11025_rb);
		sampling_rate_rb_group.add(sr_16000_rb);
		sampling_rate_rb_group.add(sr_22050_rb);
		sampling_rate_rb_group.add(sr_44100_rb);
		sampling_rate_rb_group.add(sr_other_rb);
		bit_depth_rb_group.add(bd_8_rb);
		bit_depth_rb_group.add(bd_16_rb);
		bit_depth_rb_group.add(bd_other_rb);
		channels_rb_group.add(chan_1_rb);
		channels_rb_group.add(chan_2_rb);
		channels_rb_group.add(chan_other_rb);
		signed_rb_group.add(signed_rb);
		signed_rb_group.add(unsigned_rb);
		endian_rb_group.add(big_endian_rb);
		endian_rb_group.add(little_endian_rb);

		// Instantiate buttons
		low_quality_button = new JButton("Low Quality Settings");
		mid_quality_button = new JButton("Mid Quality Settings");
		high_quality_button = new JButton("High Quality Settings");
		cancel_button = new JButton("Cancel");
		ok_button = new JButton("OK");

		// Attach action listeners to buttons
		low_quality_button.addActionListener(this);
		mid_quality_button.addActionListener(this);
		high_quality_button.addActionListener(this);
		cancel_button.addActionListener(this);
		ok_button.addActionListener(this);

		// Initialize radio button settings to mid quality
		setAudioFormat( getStandardMidQualityRecordAudioFormat() );

		// Cause program to react when the exit box is pressed
		addWindowListener(new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				cancel();
			}
		});

		// Configure overall window settings
		setTitle("PCM Audio Format Selector");
		int horizontal_gap = 6; // horizontal space between GUI elements
		int vertical_gap = 11; // horizontal space between GUI elements
		content_pane = getContentPane();
		content_pane.setBackground(blue);
		content_pane.setLayout(new BorderLayout(horizontal_gap, vertical_gap));
		settings_panel = new JPanel(new GridLayout(19, 2, horizontal_gap, vertical_gap));
		settings_panel.setBackground(blue);
		button_panel = new JPanel(new GridLayout(3, 2, horizontal_gap, vertical_gap));
		button_panel.setBackground(blue);

		// Add items to settings panel (with labels)
		settings_panel.add(new JLabel("Sampling Rate:"));
		settings_panel.add(sr_8000_rb);
		settings_panel.add(new JLabel(""));
		settings_panel.add(sr_11025_rb);
		settings_panel.add(new JLabel(""));
		settings_panel.add(sr_16000_rb);
		settings_panel.add(new JLabel(""));
		settings_panel.add(sr_22050_rb);
		settings_panel.add(new JLabel(""));
		settings_panel.add(sr_44100_rb);
		settings_panel.add(new JLabel(""));
		settings_panel.add(sr_other_rb);
		settings_panel.add(new JLabel(""));
		settings_panel.add(sr_text_area);
		settings_panel.add(new JLabel("Bit Depth:"));
		settings_panel.add(bd_8_rb);
		settings_panel.add(new JLabel(""));
		settings_panel.add(bd_16_rb);
		settings_panel.add(new JLabel(""));
		settings_panel.add(bd_other_rb);
		settings_panel.add(new JLabel(""));
		settings_panel.add(bd_text_area);
		settings_panel.add(new JLabel("Channels:"));
		settings_panel.add(chan_1_rb);
		settings_panel.add(new JLabel(""));
		settings_panel.add(chan_2_rb);
		settings_panel.add(new JLabel(""));
		settings_panel.add(chan_other_rb);
		settings_panel.add(new JLabel(""));
		settings_panel.add(chan_text_area);
		settings_panel.add(new JLabel("Signed Samples:"));
		settings_panel.add(signed_rb);
		settings_panel.add(new JLabel(""));
		settings_panel.add(unsigned_rb);
		settings_panel.add(new JLabel("Byte Order:"));
		settings_panel.add(big_endian_rb);
		settings_panel.add(new JLabel(""));
		settings_panel.add(little_endian_rb);

		// Add buttons to buttons panels
		button_panel.add(low_quality_button);
		button_panel.add(new JLabel(""));
		button_panel.add(mid_quality_button);
		button_panel.add(cancel_button);
		button_panel.add(high_quality_button);
		button_panel.add(ok_button);

		// Add panels to content pane
		content_pane.add(settings_panel, BorderLayout.CENTER);
		content_pane.add(button_panel, BorderLayout.SOUTH);

		// Prepare for display
		pack();
	}


	/* STATIC METHODS **********************************************************/


	/**
	 * Returns a new mono <code>AudioFormat</code> that uses an 8 kHz sampling rate,
	 * a 8 bit bit-depth (signed) and big endian linear PCM encoding.
	 *
	 * <p>This audio format is a typical format for use when recording low-quality audio
	 * from a microphone.
	 */
	public static AudioFormat getStandardLowQualityRecordAudioFormat()
	{
		return defineAudioFormat( 8000.0F, 8, 1, true, true );
	}


	/**
	 * Returns a new mono <code>AudioFormat</code> that uses an 8 kHz sampling rate,
	 * a 8 bit bit-depth (signed) and big endian linear PCM encoding.
	 *
	 * <p>This audio format is a typical format for use when recording low-quality audio
	 * from a microphone.
	 */
	public static AudioFormat getStandardMidQualityRecordAudioFormat()
	{
		return defineAudioFormat( 16000.0F, 16, 1, true, true );
	}


	/**
	 * Returns a new mono <code>AudioFormat</code> that uses an 44.1 kHz sampling rate,
	 * a 16 bit bit-depth (signed) and big endian linear PCM encoding.
	 *
	 * <p>This audio format is a typical format for use when recording low-quality audio
	 * from a microphone.
	 */
	public static AudioFormat getStandardHighQualityRecordAudioFormat()
	{
		return defineAudioFormat( 44100.0F, 16, 1, true, true );
	}


	/**
	 * Returns a new <code>AudioFormat</code> with the given parameters. This object 
	 * describes the particular arrangement of data in a sound stream.
	 * 
	 * <p>Linear PCM encoding is used automatically. An alternative constructory
	 * of <code>AudioFormat</code> can be used if a different encoding is desired.
	 *
	 * <p>This method does not do anything that a basic <code>AudioFormat</code>
	 * constructor does not already do. The purpose of this method is to give
	 * better documentation.
	 *
	 * <p>The possible parameters given below may varay from sound card to sound
	 * card, and others may be available as well.
	 * 
	 * @param	sample_rate	Number of samples per second. Standard values
	 *                      are 8000,11025,16000,22050 or 44100.
	 * @param	sample_size	Number of bits per sample. Standard values are 8 or 16.
	 * @param	channels	Number of channels. Standard values are 1 or 2.
	 * @param	signed		True if data is signed, false if not.
	 * @param	big_endian	True if data is big endian, false if small endian.
	 * @return				A linear PCM encoded <code>AudioFormat</code> with the
	 *						specified parameters.
	 */
	public static AudioFormat defineAudioFormat( float sample_rate,
	                                             int sample_size,
	                                             int channels,
	                                             boolean signed,
	                                             boolean big_endian )
	{
		return new AudioFormat( sample_rate,
		                        sample_size,
		                        channels,
		                        signed,
		                        big_endian );
	}


	/* PUBLIC METHODS **********************************************************/


	/**
	 * Sets GUI settings to those of a pre-defined <code>AudioFormat</code>.
	 * Does nothing if null is passed to parameter.
	 *
	 * <b>IMPORTANT:</b> Only PCM encoding is made possible in this GUI.
	 *
	 * @param	audio_format	The <code>AudioFormat</code> to base GUI values on.
	 */
	public void setAudioFormat(AudioFormat audio_format)
	{
		if (audio_format != null)
		{
			// Set sampling rate GUI elements
			float sample_rate = audio_format.getSampleRate();
			if (sample_rate == 8000.0F) sr_8000_rb.setSelected(true);
			else if (sample_rate == 11025.0F) sr_11025_rb.setSelected(true);
			else if (sample_rate == 16000.0F) sr_16000_rb.setSelected(true);
			else if (sample_rate == 22050.0F) sr_22050_rb.setSelected(true);
			else if (sample_rate == 44100.0F) sr_44100_rb.setSelected(true);
			else
			{
				sr_other_rb.setSelected(true);
				sr_text_area.setText((new Float(sample_rate * 1000.0F)).toString());
			}

			// Set bit depth GUI elements
			int bit_depth = audio_format.getSampleSizeInBits();
			if (bit_depth == 8) bd_8_rb.setSelected(true);
			else if (bit_depth == 16) bd_16_rb.setSelected(true);
			else
			{
				bd_other_rb.setSelected(true);
				bd_text_area.setText((new Integer(bit_depth)).toString());
			}

			// Set channel GUI elements
			int channels = audio_format.getChannels();
			if (channels == 1) chan_1_rb.setSelected(true);
			else if (channels == 2) chan_2_rb.setSelected(true);
			else
			{
				chan_other_rb.setSelected(true);
				chan_text_area.setText((new Integer(bit_depth)).toString());
			}

			// Set signed GUI elements
			AudioFormat.Encoding encoding = audio_format.getEncoding();
			if (encoding == AudioFormat.Encoding.PCM_SIGNED) signed_rb.setSelected(true);
			else if (encoding == AudioFormat.Encoding.PCM_UNSIGNED)	unsigned_rb.setSelected(true);

			// Set endian GUI elements
			boolean is_big_endian = audio_format.isBigEndian();
			if (is_big_endian) big_endian_rb.setSelected(true);
			else little_endian_rb.setSelected(true);
		}
	}


	/**
	 * Gets the <code>AudioFormat</code> corresponding to the settings on the GUI.
	 * Note that only PCM encoding is possible.
	 *
	 * @param	allow_text_selections	If this is not set to true, then this method
	 *									will throw an exception if the "Other" radio
	 *									button is selected for one or more of the
	 *									sampling rate, bit depth or number of channels.
	 *									If this parameter is set to false, then
	 *									an exception will not be thrown.
	 * @return							The <code>AudioFormat</code> corresponding to
	 *									the GUI settings.
	 * @throws Exception				Throws an exception if the <i>allow_text_selections</i>
	 *									parameter is true and the "Other" radio
	 *									button is selected for one or more of the
	 *									sampling rate, bit depth or number of channels.
	 */
	public AudioFormat getAudioFormat(boolean allow_text_selections)
		throws Exception
	{
		// Throw an exception if one of the Other radio buttons is selected,
		// and this is not allowed
		if (!allow_text_selections)
		{
			if (sr_other_rb.isSelected())
				throw new Exception( "Illegal sampling rate of " + sr_text_area.getText() + ".\n" +
				                     "Only sampling rates of 8, 11.025, 16, 22.05 and 44.1 kHz are\n" +
				                     "accepted under the current settings." );
			if (bd_other_rb.isSelected())
				throw new Exception( "Illegal bit depth of " + bd_text_area.getText() + ".\n" +
				                     "Only bit depths of 8 or 16 bits are accepted under the current settings." );
			if (chan_other_rb.isSelected())
				throw new Exception( "Illegal number of channels (" + chan_text_area.getText() + ").\n" +
				                     "Only 1 or 2 channels are accepted under the current settings." );
		}

		// Get sampling rate from GUI
		float sample_rate = 8000.0F;
		if (sr_8000_rb.isSelected()) sample_rate = 8000.0F;
		else if (sr_11025_rb.isSelected()) sample_rate = 11025.0F;
		else if (sr_16000_rb.isSelected()) sample_rate = 16000.0F;
		else if (sr_22050_rb.isSelected()) sample_rate = 22050.0F;
		else if (sr_44100_rb.isSelected()) sample_rate = 44100.0F;
		else if (sr_other_rb.isSelected())
			sample_rate = Float.parseFloat(sr_text_area.getText());

		// Get bit depth from GUI
		int bit_depth = 8;
		if (bd_8_rb.isSelected()) bit_depth = 8;
		else if (bd_16_rb.isSelected()) bit_depth = 16;
		else if (bd_other_rb.isSelected())
			bit_depth = Integer.parseInt(bd_text_area.getText());

		// Get channels from GUI
		int channels = 1;
		if (chan_1_rb.isSelected()) channels = 1;
		else if (chan_2_rb.isSelected()) channels = 2;
		else if (chan_other_rb.isSelected())
			channels = Integer.parseInt(chan_text_area.getText());

		// Get whether or not samples signed from GUI
		boolean is_signed = true;
		if (signed_rb.isSelected()) is_signed = true;
		else if (unsigned_rb.isSelected()) is_signed = false;

		// Get endian setting from GUI
		boolean is_big_endian= true;
		if (big_endian_rb.isSelected()) is_big_endian = true;
		else if (little_endian_rb.isSelected()) is_big_endian = false;

		// Return the AudioFormat
		return new AudioFormat(sample_rate, bit_depth, channels, is_signed, is_big_endian);
	}


	/**
	 * Calls the appropriate methods when the buttons are pressed.
	 *
	 * @param	event		The event that is to be reacted to.
	 */
	public void actionPerformed(ActionEvent event)
	{
		// React to the low_quality_button
		if (event.getSource().equals(low_quality_button))
			setAudioFormat( getStandardLowQualityRecordAudioFormat() );

		// React to the mid_quality_button
		else if (event.getSource().equals(mid_quality_button))
			setAudioFormat( getStandardMidQualityRecordAudioFormat() );

		// React to the high_quality_button
		else if (event.getSource().equals(high_quality_button))
			setAudioFormat( getStandardHighQualityRecordAudioFormat() );

		// React to the cancel_button
		else if (event.getSource().equals(cancel_button))
			cancel();

		// React to the ok_button
		else if (event.getSource().equals(ok_button))
			this.setVisible(false);
	}


	/**
	 * Makes this <code>JFrame</code> visible or hidden, exactly as the inherited
	 * <code>setVisible</code> method does. Also temporarily stores the
	 * currently selected settings on the GUI if the window the parameter is true.
	 *
	 * @param	b	Show if true, hide if false.
	 */
	public void setVisible(boolean b)
	{
		super.setVisible(b);
		try
		{
			if (b) temp_format = getAudioFormat(true);
		}
		catch (Exception e)
		{
			System.out.println(e);
			System.exit(0);
		}
	}


	/* PRIVATE METHODS *********************************************************/


	/**
	 * Hides the <code>JFrame</code> and restores the GUI settings to those that
	 * were selected when it was last made visible.
	 */
	private void cancel()
	{
		setAudioFormat(temp_format);
		this.setVisible(false);
	}
}