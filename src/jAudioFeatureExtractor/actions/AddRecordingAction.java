package jAudioFeatureExtractor.actions;

import jAudioFeatureExtractor.Controller;
import jAudioFeatureExtractor.FeatureSelectorPanel;
import jAudioFeatureExtractor.RecordingSelectorPanel;
import jAudioFeatureExtractor.RecordingsTableModel;
import jAudioFeatureExtractor.ACE.XMLParsers.FileFilterXML;
import jAudioFeatureExtractor.DataTypes.RecordingInfo;
import jAudioFeatureExtractor.jAudioTools.AudioSamples;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Action for importing audio files into jAudio.
 * 
 * @author Daniel McEnnis
 */
public class AddRecordingAction extends AbstractAction {

	static final long serialVersionUID = 1;

	private JFileChooser load_recording_chooser = null;

	private Controller controller;

	/**
	 * Generic constructor that provides menu item text.
	 */
	public AddRecordingAction() {
		super("Add Recording...");
	}

	/**
	 * AddRecordingAction requires access to the table where recordings are
	 * stored. This method supplies that context.
	 * 
	 * @param c near global controller object
	 */
	public void setModel(Controller c) {
		controller = c;
	}

	/**
	 * Instantiates a JFileChooser for the load_recording_chooser field if one
	 * does not already exist. This dialog box allows the user to choose one or
	 * more files to add to the recording_list list of references to audio file
	 * and display the added files on the recordings_table.
	 * <p>
	 * Only audio files of known types (i.e. with known extensions) are
	 * displayed in the file chooser.
	 * <p>
	 * Verifies that the files are valid audio files that can be read if the
	 * validate_recordings_when_load_them_check_box checkbox is selected. Only
	 * stores the actual samples if the store_audio_samples_check_box check box
	 * is selected (otherwise just stores file references).
	 * <p>
	 * If a selected file path corresponds to a file that does not exist, then
	 * an error message is displayed.
	 */
	public void actionPerformed(ActionEvent e) {
		if (load_recording_chooser == null) {
			load_recording_chooser = new JFileChooser();
			load_recording_chooser.setCurrentDirectory(new File("."));
			load_recording_chooser
					.setFileFilter(new jAudioFeatureExtractor.jAudioTools.FileFilterAudio());
			load_recording_chooser
					.setFileSelectionMode(JFileChooser.FILES_ONLY);
			load_recording_chooser.setMultiSelectionEnabled(true);
		}

		// Read the user's choice of load or cancel
		int dialog_result = load_recording_chooser.showOpenDialog(null);

		// Add the files to the table and to recording_list
		if (dialog_result == JFileChooser.APPROVE_OPTION) // only do if OK
		// chosen
		{
			File[] load_files = load_recording_chooser.getSelectedFiles();
			try {
				addRecording(load_files);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Adds the given files to the table display and stores a reference to them.
	 * Ignores files that have already been added to the table.
	 * <p>
	 * Verifies that the files are valid audio files that can be read if the
	 * validate_recordings_when_load_them_check_box checkbox is selected. Only
	 * stores the actual samples if the store_audio_samples_check_box check box
	 * is selected (otherwise just stores file references).
	 * <p>
	 * If a given file path corresponds to a file that does not exist, then an
	 * error message is displayed.
	 * 
	 * @param toBeAdded
	 *            The files to add to the table.
	 */
	public void addRecording(File[] toBeAdded) throws Exception {
		// Prepare to store the information about each file
		RecordingInfo[] recording_info = new RecordingInfo[toBeAdded.length];

		// Go through the files one by one
		for (int i = 0; i < toBeAdded.length; i++) {
			// Assume file is invalid as first guess
			recording_info[i] = null;

			// Verify that the file exists
			if (toBeAdded[i].exists()) {
				try {
					// The samples extracted from each file
					AudioSamples audio_samples = null;

					// Load the samples if the
					// validate_recordings_when_load_them_check_box
					// is selected. Throw an exception if the file is not a
					// valid
					// audio file of a type that can be read and processed.
					if (controller.validate.isSelected()) {
						audio_samples = new AudioSamples(toBeAdded[i],
								toBeAdded[i].getPath(), false);
					}

					// Store the samples themselves in memory if the
					// store_audio_samples_check_box check box is selected.
					// Throw an
					// exception if the file is not a valid audio file of a type
					// that
					// can be read and processed.
					if (controller.storeSamples.isSelected()) {
						if (audio_samples == null) {
							audio_samples = new AudioSamples(toBeAdded[i],
									toBeAdded[i].getPath(), false);
						}
					} else
						audio_samples = null;

					// Generate a RecordingInfo object for the loaded file
					recording_info[i] = new RecordingInfo(toBeAdded[i]
							.getName(), toBeAdded[i].getPath(), audio_samples,
							false);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(),
							"ERROR", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(null, "The selected file "
						+ toBeAdded[i].getName() + " does not exist.", "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		// Update the recording_list field to include these new entries, while
		// removing
		// null entries due to problems with invalid files
		int number_old_recordings = 0;
		if (controller.dm_.recordingInfo != null)
			number_old_recordings = controller.dm_.recordingInfo.length;
		int number_new_recordings = 0;
		if (recording_info != null)
			number_new_recordings = recording_info.length;
		RecordingInfo[] temp_recording_list = new RecordingInfo[number_old_recordings
				+ number_new_recordings];
		for (int i = 0; i < number_old_recordings; i++)
			temp_recording_list[i] = controller.dm_.recordingInfo[i];
		for (int i = 0; i < number_new_recordings; i++)
			temp_recording_list[i + number_old_recordings] = recording_info[i];

		// Remove duplicate entries in the recording_list with the same file
		// path
		for (int i = 0; i < temp_recording_list.length - 1; i++)
			if (temp_recording_list[i] != null) {
				String current_path = temp_recording_list[i].file_path;
				for (int j = i + 1; j < temp_recording_list.length; j++)
					if (temp_recording_list[j] != null)
						if (current_path
								.equals(temp_recording_list[j].file_path))
							temp_recording_list[j] = null;
			}

		// Remove null entries in recording_list due to invalid files or
		// duplicate file names
		Object[] results = jAudioFeatureExtractor.GeneralTools.GeneralMethods
				.removeNullEntriesFromArray(temp_recording_list);
		if (results != null) {
			controller.dm_.recordingInfo = new RecordingInfo[results.length];
			for (int i = 0; i < results.length; i++)
				controller.dm_.recordingInfo[i] = (RecordingInfo) results[i];
		}

		// Update the table to display the new recording_list
		controller.rtm_.fillTable(controller.dm_.recordingInfo);
		controller.rtm_.fireTableDataChanged();
	}

}
