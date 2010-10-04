package jAudioFeatureExtractor.actions;

import jAudioFeatureExtractor.Controller;
import jAudioFeatureExtractor.OuterFrame;
import jAudioFeatureExtractor.ProcessSamplesFrame;
import jAudioFeatureExtractor.RecordingSelectorPanel;
import jAudioFeatureExtractor.DataTypes.RecordingInfo;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 * Shows the ProceessSamplesFrame - allowing editing of several aspects of audio
 * files. Also requires that at least 1 recording be selected in the recordings
 * table.
 * 
 * @author Daniel McEnnis
 */
public class EditRecordingsAction extends AbstractAction {

	static final long serialVersionUID = 1;

	private Controller controller;

	private JTable recordings_table;

	private OuterFrame of_;

	/**
	 * Gives EditRecordingsAction access to the global controller.
	 * @param c
	 */
	public EditRecordingsAction(Controller c) {
		super("Edit Recordings...");
		controller = c;
	}

	/**
	 * This action requires access to the Recordings table - provided by this method.
	 * @param jt Recording table where recordings are accessed.
	 * @param of reference to the outer frame of the application.
	 */
	public void setTable(JTable jt, OuterFrame of) {
		recordings_table = jt;
		of_ = of;
	}

	/**
	 * Bring up a ProcessSamplesFrame to allow the user to edit the samples of
	 * the selected recording. If multiple recordings are selected, only the top
	 * one is edited.
	 */
	public void actionPerformed(ActionEvent e) {
		int selected_row = recordings_table.getSelectedRow();
		if (selected_row < 0)
			JOptionPane.showMessageDialog(null,
					"No recording selected for editing.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		else {
			RecordingInfo selected_audio = controller.dm_.recordingInfo[selected_row];
			try {
				ProcessSamplesFrame processor = new ProcessSamplesFrame(
						controller, of_, selected_audio);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
