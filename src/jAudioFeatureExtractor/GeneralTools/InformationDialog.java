/*
 * @(#)InformationDialog.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.GeneralTools;

import java.awt.*;
import javax.swing.*;

/**
 * A general purpose modal dialog box for displaying a long
 * text message. Wraps and includes a scroll bar.
 *
 * @author Cory McKay
 */
public class InformationDialog
	extends JDialog
{
	
	static final long serialVersionUID = 1;

	/* CONSTRUCTOR *************************************************************/


	/**
	 * Sets up the dialog box and displays it.
	 *
	 * @param	title		The name of the dialog box.
	 * @param	information	The information to be displayed.
	 */
	public InformationDialog(String title, String information)
	{
		// Give the dialog box its owner, its title and make it modal
		super();
		setTitle(title);
		setModal(true);

		// Set up text_area
		int number_text_columns = 35;
		int number_rows = 35;
		JTextArea text_area = new JTextArea(number_rows, number_text_columns);
		text_area.setEditable(false);
		text_area.setLineWrap(true);
		text_area.setWrapStyleWord(true);
		text_area.setText(information);

		// Display the panel
		Container content_pane = getContentPane();
		content_pane.add(new JScrollPane(text_area));
		pack();
		setVisible(true);
	}
}