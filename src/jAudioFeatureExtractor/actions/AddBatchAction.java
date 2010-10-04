package jAudioFeatureExtractor.actions;

import jAudioFeatureExtractor.AddBatchGUI;
import jAudioFeatureExtractor.Controller;
import jAudioFeatureExtractor.FeatureSelectorTableModel;
import jAudioFeatureExtractor.ACE.DataTypes.Batch;
import jAudioFeatureExtractor.Aggregators.Aggregator;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * Action that adds a batch to the internal system. This class is tightly
 * coupled to the AddBatchGUI. Requires context from various GUI elements to
 * extract the state information needed to create a batch.
 * 
 * @author Daniel McEnnis
 */
public class AddBatchAction extends AbstractAction {

	static final long serialVersionUID = 1;

	private JTextArea window_length_text_field;

	private JTextArea window_overlap_fraction_text_field;

	/**
	 * GUI check boxes
	 */
	private JCheckBox save_window_features_check_box;

	private JCheckBox save_overall_file_featurese_check_box;

	private JTextArea destinationFV;

	private JTextArea destinationFK;

	private Controller controller;

	private AddBatchGUI addBatchGUI;

	/**
	 * Constructor for creating this action.
	 * 
	 * @param c
	 *            Reference to the global controller.
	 */
	public AddBatchAction(Controller c) {
		super("Define Batch");
		controller = c;
	}

	/**
	 * This method creates a new batch, then activates the AddBatchGUI to finish
	 * the task.
	 */
	public void actionPerformed(ActionEvent e) {
		Batch b = new Batch();
		boolean good = true;

		// get the files for recording
		LinkedList<File> filesL = new LinkedList<File>();
		File[] files;
		for (int i = 0; i < controller.rtm_.getRowCount(); ++i) {
			filesL.add(new File((String) controller.rtm_.getValueAt(i, 1)));
		}
		files = filesL.toArray(new File[] {});
		if (files.length == 0) {
			JOptionPane
					.showMessageDialog(
							null,
							"Batches must contain files to record.  Add files to recording table.",
							"ERROR", JOptionPane.ERROR_MESSAGE);
			good = false;
		}
		try {
			b.setRecordings(files);
		} catch (Exception e1) {
			good = false;
			JOptionPane.showMessageDialog(null, e1.getMessage(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}

		// set the data model
		b.setDataModel(controller.dm_);

		// set fetures properly
		// set boolean List
		HashMap<String,Boolean> active = new HashMap<String,Boolean>();
		HashMap<String,String[]> attributes = new HashMap<String,String[]>();
		LinkedList<String> tmpAttributes = new LinkedList<String>();
		// set feature attribute list
		for (int i = 0; i < controller.dm_.features.length; ++i) {
			String name = controller.dm_.features[i].getFeatureDefinition().name;
			active.put(name,(Boolean) controller.fstm_.getValueAt(i, 0));
			int count = controller.dm_.features[i].getFeatureDefinition().attributes.length;
			for (int j = 0; j < count; ++j) {
				try {
					tmpAttributes
							.add(controller.dm_.features[i].getElement(j));
				} catch (Exception e1) {
					good = false;
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"ERROR", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
			attributes.put(name,tmpAttributes.toArray(new String[] {}));
			tmpAttributes.clear();
		}
		b.setFeatures(active, attributes);

		// set settings
		// get window length and window overlap
		int windowLength = 512;
		double windowOverlap = 0.0;
		try {
			windowLength = Integer.parseInt(window_length_text_field.getText());
		} catch (NumberFormatException e1) {
			good = false;
			JOptionPane.showMessageDialog(null,
					"window length must be an integer", "ERROR",
					JOptionPane.ERROR_MESSAGE);
			;
		}
		try {
			windowOverlap = Double
					.parseDouble(window_overlap_fraction_text_field.getText());
		} catch (NumberFormatException e1) {
			good = false;
			JOptionPane.showMessageDialog(null,
					"window overlap must be a double", "ERROR",
					JOptionPane.ERROR_MESSAGE);

		}
		if ((windowOverlap < 0.0) || (windowOverlap >= 1.0)) {
			good = false;
			JOptionPane
					.showMessageDialog(
							null,
							"Window Overlap must be equal or greater than 0 and less than 1.0",
							"ERROR", JOptionPane.ERROR_MESSAGE);
		}
		if (windowLength <= 0) {
			good = false;
			JOptionPane.showMessageDialog(null,
					"Window length must be greater than 0", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
		
		Aggregator[] aggs = controller.dm_.aggregators;
		String[] names = new String[aggs.length];
		String[][] features = new String[aggs.length][];
		String[][] parameters = new String[aggs.length][];
		for(int i=0;i<aggs.length;++i){
			names[i] = aggs[i].getAggregatorDefinition().name;
			features[i] = aggs[i].getFeaturesToApply();
			parameters[i] = aggs[i].getParamaters();
		}
		b.setAggregators(names, features, parameters);
		
		b.setSettings(windowLength, windowOverlap,
				controller.samplingRateAction.getSamplingRate(),
				controller.normalise.isSelected(),
				save_window_features_check_box.isSelected(),
				save_overall_file_featurese_check_box.isSelected(),
				controller.outputTypeAction.getSelected());

		// set the destination
		b.setDestination(destinationFK.getText(), destinationFV.getText());

		if (good) {
			addBatchGUI = new AddBatchGUI(controller, b);
		} else {
			JOptionPane.showMessageDialog(null, "Batch creation failed",
					"ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Aquires references to GUI componenents needed to save settings for a batch.
	 * 
	 * @param saveWindow	Should features be saved from each window of data.
	 * @param overall		Should features be saved from over the entire file.
	 * @param windowLength	Size of the analysis windo in samples
	 * @param windowOverlap	Percent of the window that should be duplicated.
	 */
	public void setSettings(JCheckBox saveWindow, JCheckBox overall,
			JTextArea windowLength, JTextArea windowOverlap) {
		save_window_features_check_box = saveWindow;
		save_overall_file_featurese_check_box = overall;
		window_length_text_field = windowLength;
		window_overlap_fraction_text_field = windowOverlap;
	}

	/**
	 * Aquires references to GUI componenents needed to save settings for a batch.
	 * 
	 * @param FV	TextArea containing the location to save feature values.
	 * @param FK	TextArea containing the location to save feature definitions.
	 */
	public void setFilePath(JTextArea FV, JTextArea FK) {
		destinationFV = FV;
		destinationFK = FK;
	}

}
