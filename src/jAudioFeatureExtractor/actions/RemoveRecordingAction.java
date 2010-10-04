package jAudioFeatureExtractor.actions;

import jAudioFeatureExtractor.Controller;
import jAudioFeatureExtractor.DataTypes.RecordingInfo;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

/**
 * Removes the selected recordings from the recordings table
 * 
 * @author Daniel McEnnis
 */
public class RemoveRecordingAction extends AbstractAction {

	static final long serialVersionUID = 1;

	Controller controller;

	JTable recordingTable;

	/**
	 * Constructor that sets the menu text.
	 */
	public RemoveRecordingAction() {
		super("Delete Recording");
	}

	/**
	 * Sets references to both the controller and the recordings table.
	 * 
	 * @param c
	 *            near global controller.
	 * @param recordingTable
	 *            table holding references to audio files.
	 */
	public void setModel(Controller c, JTable recordingTable) {
		this.controller = c;
		this.recordingTable = recordingTable;
	}

	/**
	 * Removes selected files from the recording table.
	 */
	public void actionPerformed(ActionEvent e) {
		int[] selected_rows = recordingTable.getSelectedRows();
		for (int i = 0; i < selected_rows.length; i++)
			controller.dm_.recordingInfo[selected_rows[i]] = null;
		Object[] results = jAudioFeatureExtractor.GeneralTools.GeneralMethods
				.removeNullEntriesFromArray(controller.dm_.recordingInfo);
		if (results != null) {
			controller.dm_.recordingInfo = new RecordingInfo[results.length];
			for (int i = 0; i < results.length; i++)
				controller.dm_.recordingInfo[i] = (RecordingInfo) results[i];
			controller.rtm_.fillTable(controller.dm_.recordingInfo);
		} else {
			controller.dm_.recordingInfo = null;
			controller.rtm_.clearTable();
		}
	}

}
