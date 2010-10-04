/*
 * @(#)FeatureSelectorPanel.java	1.01	April 9, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import java.io.*;

import jAudioFeatureExtractor.Aggregators.Aggregator;
import jAudioFeatureExtractor.AudioFeatures.*;
import jAudioFeatureExtractor.DataTypes.RecordingInfo;
import jAudioFeatureExtractor.GeneralTools.FeatureDisplay;
import jAudioFeatureExtractor.actions.MultipleToggleAction;
import jAudioFeatureExtractor.ACE.XMLParsers.FileFilterXML;

/**
 * A window that allows users to select which features to save as well as some
 * basic parameters relating to these features. These parameters include the
 * window length to use for analyses, the amount of overlap between analysis
 * windows, whether or not to normalise recordings and thh sampling rate to
 * convert files to before analysis. The user may also see the features that can
 * be extracted and some details about them.
 * <p>
 * The resulting feature values and the features used are saved to the specified
 * feature_vector_file and a feature_key_file respectively.
 * <p>
 * For multi-track audio, analyses are performed of all tracks mixed down into
 * one channel.
 * <p>
 * Note that some features need other features in order to be extracted. Even if
 * a feature is not checked for saving, it will be extracted (but not saved) if
 * another feature that needs it is checked for saving.
 * <p>
 * The table allows the user to view all features which are possible to extract.
 * The Save click box indicates whether this feature is to be saved during
 * feature extraction. The Dimensions indicate how many values are produced for
 * a given feature each time that it is extracted. Double clicking on a feature
 * brings up a window describing it.
 * <p>
 * The Window Size indicates the number of samples that are used in each window
 * that features are extracted from.
 * <p>
 * The Window Overlap indicates the fraction, from 0 to 1, of overlap between
 * adjacent analysis windows.
 * <p>
 * The Normalise check box indicates whether recordings are to be normalised
 * before playback.
 * <p>
 * The Extract Features button extracts all appropriate features and from the
 * loaded recordings, and saves the results to disk.
 * <p>
 * The Feature Values Save Path and Feature Definitions Save Path allow the user
 * to choose what paths to save extracted feature values and feature definitions
 * respectively.
 * <p>
 * Double clicking on a feature will bring up a description of it.
 * 
 * @author Cory McKay
 */
public class FeatureSelectorPanel extends JPanel implements ActionListener {
	/* FIELDS ***************************************************************** */

	static final long serialVersionUID = 1;

	/**
	 * Holds a reference to the JPanel that holds objects of this class.
	 */
	public OuterFrame outer_frame;

	/**
	 * Holds references to all of the features that it's possible to extract.
	 */
	// private FeatureExtractor[] feature_extractors;
	/**
	 * The default as to whether each feature is to be saved after feature
	 * extraction. Indices correspond to those of feature_extractors.
	 */
	// private boolean[] feature_save_defaults;
	/**
	 * Replaces feature_extractors and feature_save_defaults with a view neutral
	 * model.
	 */
	// private FeatureModel featureModel;
	private MultipleToggleAction multipleToggleAction;

	/**
	 * GUI panels
	 */
	private JPanel features_panel;

	private JScrollPane features_scroll_pane;

	/**
	 * GUI table-related fields
	 */
	private JTable features_table;

	private SortingTableModelDecorator decorator;

	/**
	 * GUI text areas
	 */
	private JTextArea window_length_text_field;

	private JTextArea window_overlap_fraction_text_field;

	// private JTextArea values_save_path_text_field;
	//
	// private JTextArea definitions_save_path_text_field;

	/**
	 * GUI check boxes
	 */
	private JCheckBox save_window_features_check_box;

	private JCheckBox save_overall_file_featurese_check_box;

	/**
	 * GUI buttons
	 */
	// private JButton values_save_path_button;
	//
	// private JButton definitions_save_path_button;
	private JButton extract_features_button;

	private JButton set_aggregators_button;

	/**
	 * GUI dialog boxes
	 */
	private JFileChooser save_file_chooser;

	private AggregatorFrame aggregator_editor = null;

	/**
	 * Children Windows
	 */
	private EditFeatures ef_ = null;

	/**
	 * Responsible for redistributing the control to another class
	 */
	private Controller controller;

	/* CONSTRUCTOR ************************************************************ */

	/**
	 * Set up frame.
	 * <p>
	 * Daniel McEnnis 05-08-05 Added GlobalWindowChange button
	 * 
	 * @param outer_frame
	 *            The GUI element that contains this object.
	 */
	public FeatureSelectorPanel(OuterFrame outer_frame, Controller c) {
		// Store containing panel
		this.outer_frame = outer_frame;
		this.controller = c;
		// Set the file chooser to null initially
		save_file_chooser = null;

		// Set the global color
		Color blue = new Color((float) 0.75, (float) 0.85, (float) 1.0);

		// General container preparations containers
		int horizontal_gap = 6; // horizontal space between GUI elements
		int vertical_gap = 11; // horizontal space between GUI elements
		setLayout(new BorderLayout(horizontal_gap, vertical_gap));

		// Set up the list of feature extractors
		setUpFeatureTable();

		// Add an overall title for this panel
		add(new JLabel("FEATURES:"), BorderLayout.NORTH);

		// Set up buttons and text area
		JPanel control_panel = new JPanel(new GridLayout(4, 2, horizontal_gap,
				vertical_gap));

		save_window_features_check_box = new JCheckBox(
				"Save Features For Each Window", false);
		save_window_features_check_box.setBackground(blue);
		save_window_features_check_box.addActionListener(this);
		control_panel.add(save_window_features_check_box);

		save_overall_file_featurese_check_box = new JCheckBox(
				"Save For Overall Recordings", true);
		save_overall_file_featurese_check_box.setBackground(blue);
		save_overall_file_featurese_check_box.addActionListener(this);

		control_panel.add(save_overall_file_featurese_check_box);

		control_panel.add(new JLabel("Window Size (samples):"));

		window_length_text_field = new JTextArea("512", 1, 20);
		control_panel.add(window_length_text_field);

		control_panel.add(new JLabel("Window Overlap (fraction):"));
		window_overlap_fraction_text_field = new JTextArea("0.0", 1, 20);
		control_panel.add(window_overlap_fraction_text_field);

		set_aggregators_button = new JButton("Alter Aggregators");
		set_aggregators_button.addActionListener(this);
		control_panel.add(set_aggregators_button);

		extract_features_button = new JButton("Extract Features");
		extract_features_button.addActionListener(this);
		control_panel.add(extract_features_button);

		control_panel.setBackground(blue);

		add(control_panel, BorderLayout.SOUTH);

		// Cause the table to respond to double clicks
		addTableMouseListener();
		controller.saveAction.setObjectReferences(window_length_text_field,
				window_overlap_fraction_text_field,
				save_window_features_check_box,
				save_overall_file_featurese_check_box);
		controller.loadAction.setObjectReferences(window_length_text_field,
				window_overlap_fraction_text_field,
				save_window_features_check_box,
				save_overall_file_featurese_check_box);
		controller.outputTypeAction.setTarget(outer_frame.ace,
				outer_frame.arff, save_window_features_check_box,
				save_window_features_check_box);
		controller.addBatchAction.setSettings(save_window_features_check_box,
				save_overall_file_featurese_check_box,
				window_length_text_field, window_overlap_fraction_text_field);
		controller.viewBatchAction.setFeatureFields(
				save_window_features_check_box,
				save_overall_file_featurese_check_box,
				window_length_text_field, window_overlap_fraction_text_field);
		controller.dm_.aggregators = new Aggregator[] {
				(Aggregator) (controller.dm_.aggregatorMap
						.get("Standard Deviation").clone()),
				(Aggregator) (controller.dm_.aggregatorMap.get("Mean").clone()) };
	}

	/* PUBLIC METHODS ********************************************************* */

	/**
	 * Calls the appropriate methods when the buttons are pressed.
	 * 
	 * @param event
	 *            The event that is to be reacted to.
	 */
	public void actionPerformed(ActionEvent event) {
		// React to the extract_features_button
		if (event.getSource().equals(extract_features_button))
			extractFeatures();
		else if (event.getSource()
				.equals(save_overall_file_featurese_check_box)) {
			JCheckBox tmp = (JCheckBox) event.getSource();
			if (tmp.isSelected()) {
				if (save_window_features_check_box.isSelected()) {
					if (controller.outputTypeAction.getSelected() == 1) {
						JOptionPane
								.showMessageDialog(
										null,
										"Weka format only supports one type of output - either output per file or output per window.",
										"ERROR", JOptionPane.ERROR_MESSAGE);
						tmp.setSelected(false);

					}
				}
			}
		} else if (event.getSource().equals(save_window_features_check_box)) {
			JCheckBox tmp = (JCheckBox) event.getSource();
			if (tmp.isSelected()) {
				if (save_overall_file_featurese_check_box.isSelected()) {
					if (controller.outputTypeAction.getSelected() == 1) {
						JOptionPane
								.showMessageDialog(
										null,
										"Weka format only supports one type of output - either output per file or output per window.",
										"ERROR", JOptionPane.ERROR_MESSAGE);
						tmp.setSelected(false);
					}
				}
			}
		} else if (event.getSource().equals(set_aggregators_button)) {
			launchAggEditTable();
		}
	}

	/* PRIVATE METHODS ******************************************************** */

	/**
	 * Extract the features from all of the files added in the GUI. Use the
	 * features and feature settings entered in the GUI. Save the results in a
	 * feature_vector_file and the features used in a feature_key_file. Daniel
	 * McEnnis 05-09-05 Moved guts into FeatureModel
	 */
	private void extractFeatures() {
		try {
			// Get the control parameters
			boolean save_features_for_each_window = save_window_features_check_box
					.isSelected();
			boolean save_overall_recording_features = save_overall_file_featurese_check_box
					.isSelected();
			String feature_values_save_path = outer_frame.recording_selector_panel.values_save_path_text_field
					.getText();
			String feature_definitions_save_path = outer_frame.recording_selector_panel.definitions_save_path_text_field
					.getText();
			int window_size = Integer.parseInt(window_length_text_field
					.getText());
			double window_overlap = Double
					.parseDouble(window_overlap_fraction_text_field.getText());
			boolean normalise = controller.normalise.isSelected();
			double sampling_rate = controller.samplingRateAction
					.getSamplingRate();
			int outputType = controller.outputTypeAction.getSelected();

			// Get the audio recordings to extract features from and throw an
			// exception
			// if there are none
			RecordingInfo[] recordings = controller.dm_.recordingInfo;
			if (recordings == null)
				throw new Exception(
						"No recordings available to extract features from.");

			// Ask user if s/he wishes to change window size to a power of 2 if
			// it
			// is not already.
			if (window_size >= 0) {
				int pow_2_size = jAudioFeatureExtractor.GeneralTools.Statistics
						.ensureIsPowerOfN(window_size, 2);
				if (window_size != pow_2_size) {
					String message = "Given window size is " + window_size
							+ ", which is not a power\n"
							+ "of 2. Would you like to increase this to the\n"
							+ "next highest power of 2 (" + pow_2_size + ")?";
					int convert = JOptionPane.showConfirmDialog(null, message,
							"WARNING", JOptionPane.YES_NO_OPTION);
					if (convert == JOptionPane.YES_OPTION) {
						window_length_text_field.setText(String
								.valueOf(pow_2_size));
						window_size = Integer.parseInt(window_length_text_field
								.getText());
					}
				}
			}

			// Find which features are selected to be saved
			// boolean[] features_to_save = new
			// boolean[controller.dm_.features.length];
			for (int i = 0; i < controller.dm_.defaults.length; i++) {
				// features_to_save[i] = ((Boolean)
				// controller.fstm_.getValueAt(i,
				// 0)).booleanValue();
				controller.dm_.defaults[i] = ((Boolean) controller.fstm_
						.getValueAt(i, 0)).booleanValue();
			}

			// threads can only execute once. Rebuild the thread here
			controller.extractionThread = new ExtractionThread(controller,
					outer_frame);

			controller.extractionThread.setup(save_overall_recording_features,
					save_features_for_each_window, feature_values_save_path,
					feature_definitions_save_path, window_size, window_overlap);
			// extract the features
			controller.extractionThread.start();

		} catch (Throwable t) {
			// React to the Java Runtime running out of memory
			if (t.toString().equals("java.lang.OutOfMemoryError"))
				JOptionPane
						.showMessageDialog(
								null,
								"The Java Runtime ran out of memory. Please rerun this program\n"
										+ "with a higher amount of memory assigned to the Java Runtime heap.",
								"ERROR", JOptionPane.ERROR_MESSAGE);
			else if (t instanceof Exception) {
				Exception e = (Exception) t;
				JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Initialize the table displaying the features which can be extracted.
	 */
	private void setUpFeatureTable() {
		controller.fstm_.fillTable(controller.dm_.featureDefinitions,
				controller.dm_.defaults, controller.dm_.is_primary);
		decorator = new SortingTableModelDecorator(controller.fstm_);
		features_table = new JTable(decorator);

		multipleToggleAction = new MultipleToggleAction(features_table);
		String key = "MultipleToggleAction";
		features_table.getInputMap().put(KeyStroke.getKeyStroke(' '), key);
		features_table.getActionMap().put(key, multipleToggleAction);

		int[] width = new int[3];
		width[0] = decorator.getRealPrefferedWidth(features_table, 0);
		width[1] = decorator.getRealPrefferedWidth(features_table, 1);
		width[1] -= 100;
		width[2] = decorator.getRealPrefferedWidth(features_table, 2);

		for (int i = 0; i < width.length; ++i) {
			features_table.getColumnModel().getColumn(i).setPreferredWidth(
					width[i]);
		}

		// add handler for sorting panel
		JTableHeader header = (JTableHeader) features_table.getTableHeader();
		header.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					TableColumnModel tcm = features_table.getColumnModel();
					int column = features_table.convertColumnIndexToModel(tcm
							.getColumnIndexAtX(e.getX()));
					decorator.sort(column);
				} else {
					decorator.resetIndeci();
				}
			}
		});

		// Set up and display the table
		features_scroll_pane = new JScrollPane(features_table);
		features_panel = new JPanel(new GridLayout(1, 1));
		features_panel.add(features_scroll_pane);
		add(features_panel, BorderLayout.CENTER);
		controller.fstm_.fireTableDataChanged();
		TableColumn tableColumn = features_table.getColumn(features_table
				.getColumnName(1));
		tableColumn.setCellRenderer(new FeatureDisplay());
		features_table.removeColumn(features_table.getColumn(features_table
				.getColumnName(3)));
		repaint();
		outer_frame.repaint();
	}

	/**
	 * Makes it so that if a row is double clicked on, a description of the
	 * corresponding feature is displayed along with its dependencies. Daniel
	 * McEnnis 05-07-05 Replaced message box with editDialog frame.
	 */
	public void addTableMouseListener() {
		features_table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 2) {
					int[] row_clicked = new int[1];
					row_clicked[0] = features_table
							.rowAtPoint(event.getPoint());
					editDialog(controller.dm_.features[row_clicked[0]]);

				}
			}
		});
	}

	// /**
	// * Returns the types of feature extractors that are currently available.
	// * <p>
	// * Daniel McEnnis 05-04-05 Added MFCC,Moments and Area Moments to list of
	// * features
	// * <p>
	// * Daniel McEnnis 05-05-05 Added LPC to list of features
	// * <p>
	// * Daniel McEnnis 05-06-05 Added code to handle meta-features. Added Mean,
	// * derivative, and StandardDeviation to meta features. Daniel McEnnis
	// * 05-09-05 Moved contents into featureModel
	// * <p>
	// * Daniel McEnnis 05-13-05 Shifted code into Controller class
	// *
	// * @return The available feature extractors.
	// */
	// private void populateFeatureExtractors() {
	// //;
	// }

	/**
	 * Allows the user to select or enter a file path using a JFileChooser. If
	 * the selected path does not have an extension of .XML, it is given this
	 * extension. If the chosen path refers to a file that already exists, then
	 * the user is asked if s/he wishes to overwrite the selected file.
	 * <p>
	 * No file is actually saved or overwritten by this method. The selected
	 * path is simply returned.
	 * 
	 * @return The path of the selected or entered file. A value of null is
	 *         returned if the user presses the cancel button or chooses not to
	 *         overwrite a file.
	 */

	/**
	 * Creates and displays the Dialog for editing feature attributes.
	 */
	private void editDialog(FeatureExtractor fe) {
		ef_ = new EditFeatures(this, fe);
		ef_.setVisible(true);
	}

	// private void outputFormatAction(ActionEvent event) {
	// if (output_format.getSelectedIndex() == 1) {
	// definitions_save_path_button.setEnabled(false);
	// definitions_save_path_text_field.setEnabled(false);
	// if (save_window_features_check_box.isSelected()
	// && save_overall_file_featurese_check_box.isSelected()) {
	// save_overall_file_featurese_check_box.setSelected(false);
	// }
	// } else if (output_format.getSelectedIndex() == 0) {
	// definitions_save_path_button.setEnabled(true);
	// definitions_save_path_text_field.setEnabled(true);
	// }
	// }

	private void launchAggEditTable() {
		aggregator_editor = new AggregatorFrame(controller);
		aggregator_editor.setVisible(true);
	}

}