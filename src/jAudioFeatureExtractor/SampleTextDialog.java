/*
 * @(#)SampleTextDialog.java.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


/**
 * A modal dialog box for displaying long lists of sample values.
 *
 * @author Cory McKay
 */
public class SampleTextDialog
	extends JDialog
{
	
	static final long serialVersionUID = 1;

	public	JTextArea	text_area;
	
	/* CONSTRUCTOR *************************************************************/


	/**
	 * Sets up the dialog box and displays it.
	 */
	public SampleTextDialog()
	{
		// Give the dialog boxits title and make it modal
		super();
		setTitle("Sample Values");
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Set up text_area
		int number_text_columns = 50;
		int number_rows = 35;
		text_area = new JTextArea(number_rows, number_text_columns);
		text_area.setEditable(false);
		text_area.setLineWrap(false);
		text_area.setText("");

		// Display the panel
		Container content_pane = getContentPane();
		content_pane.add(new JScrollPane(text_area));
		pack();
	}


	/**
	 * Return the text area on this dialog box so that other objects can
	 * write to it or perform other operations to it.
	 *
	 * @return	This object's text area.
	 */
	public JTextArea getTextArea()
	{
		return text_area;
	}

	
	/**
	 * Display this dialog box.
	 */
	public void display()
	{
		setVisible(true);
	}
}