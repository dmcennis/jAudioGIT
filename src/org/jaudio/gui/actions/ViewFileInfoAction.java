package org.jaudio.gui.actions;

import jAudioFeatureExtractor.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Action that displays the details of a given file.
 * @author Daniel McEnnis
 *
 */
public class ViewFileInfoAction extends AbstractAction {
	
	static final long serialVersionUID = 1;

	private JTable recordings_table;
	
	private Controller controller;
	
	/**
	 * Constructor that sets menu text and stores a reference to the controller.
	 * @param c	near global controller.
	 */
	public ViewFileInfoAction(Controller c){
		super("View File Info...");
		controller = c;
	}
	
	/**
	 * Sets reference to the table containing references to files to be analyzed.
	 * @param jt sets the sister jTable
	 */
	public void setTable(JTable jt){
		recordings_table = jt;
	}
	
	/**
	 * Show the file info of the selected file.
	 */
	public void actionPerformed(ActionEvent e) {
		int[] selected_rows = recordings_table.getSelectedRows();
		for (int i = 0; i < selected_rows.length; i++) {
			try {
				File file = new File(controller.dm_.recordingInfo[selected_rows[i]].file_path);
				String data = jAudioFeatureExtractor.jAudioTools.AudioMethods
						.getAudioFileFormatData(file);
				JOptionPane.showMessageDialog(null, data, "FILE INFORMATION",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e1) {
				ResourceBundle bundle = ResourceBundle.getBundle("Translations");

				String message = MessageFormat.format(bundle.getString("could.not.display.file.information.for.file.0.n.1"), controller.dm_.recordingInfo[selected_rows[i]].file_path, e1.getMessage());
				JOptionPane.showMessageDialog(null, message, "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}	
}
