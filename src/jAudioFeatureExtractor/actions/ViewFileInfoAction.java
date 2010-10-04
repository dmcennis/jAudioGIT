package jAudioFeatureExtractor.actions;

import jAudioFeatureExtractor.Controller;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTable;

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
	 * @param jt
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
				String message = "Could not display file information for file "
						+ controller.dm_.recordingInfo[selected_rows[i]].file_path + "\n"
						+ e1.getMessage();
				JOptionPane.showMessageDialog(null, message, "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}	
}
