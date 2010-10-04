package jAudioFeatureExtractor.actions;

import jAudioFeatureExtractor.Controller;
import jAudioFeatureExtractor.ACE.DataTypes.Batch;
import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.DataTypes.RecordingInfo;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * Action that loads all settings in the chosen batch file into the current
 * window.
 * 
 * @author Daniel McEnnis
 */
public class ViewBatchAction extends AbstractAction {

	static final long serialVersionUID = 1;

	private Controller controller;

	// CheckBoxes
	JCheckBox saveWindow;

	JCheckBox saveOverall;

	// TextFields
	JTextArea windowSize;

	JTextArea windowOverlap;

	JTextArea definition;

	JTextArea value;

	/**
	 * Constructor that sets menu text and stores a reference to the controller.
	 * 
	 * @param c
	 *            near global controller.
	 */
	public ViewBatchAction(Controller c) {
		super("View Batch");
		controller = c;
	}

	/**
	 * Sets references to gui elements so it can set them to the designated
	 * values when executed.
	 * 
	 * @param saveWindow
	 *            checkbox indicating whether features should be saved per
	 *            window
	 * @param saveOverall
	 *            checkbox indicating whether global features should be saved. .
	 * @param windowSize
	 *            text box that holds the width of the analysis window in
	 *            samples.
	 * @param windowOverlap
	 *            text box that holds the percent overlap between windows
	 */
	public void setFeatureFields(JCheckBox saveWindow, JCheckBox saveOverall,
			JTextArea windowSize, JTextArea windowOverlap) {
		this.saveWindow = saveWindow;
		this.saveOverall = saveOverall;
		this.windowSize = windowSize;
		this.windowOverlap = windowOverlap;
	}

	/**
	 * Sets references to gui elements so it can set them to the correct values when executed.
	 * @param definition	text box containing the location to save the feature declarations.
	 * @param value			text box containing the location to save the extracted features.
	 */	
	public void setRecordingFields(JTextArea definition, JTextArea value) {
		this.definition = definition;
		this.value = value;
	}

	/**
	 * Resets all gui paramters to the settings of the chosen batch file.
	 */
	public void actionPerformed(ActionEvent e) {
		int count = 0;

		Component src = controller.viewBatch.getMenuComponent(count);
		String action = "";
		if (src instanceof JMenuItem) {
			action = ((JMenuItem) src).getActionCommand();
		}

		while (!e.getActionCommand().equals(action)) {
			src = controller.viewBatch.getMenuComponent(++count);
			if (src instanceof JMenuItem) {
				action = ((JMenuItem) src).getActionCommand();
			}
		}

		try {
			Batch batch = controller.batches.get(count);
			controller.dm_.aggregators = batch.getAggregator();
			RecordingInfo[][] recording = new RecordingInfo[1][];
			int[] windowSize = new int[] { 0 };
			double[] windowOverlap = new double[] { 0.0 };
			double[] samplingRate = new double[] { 0.0 };
			boolean[] normalise = new boolean[] { false };
			boolean[] perWindow = new boolean[] { false };
			boolean[] overall = new boolean[] { false };
			String[] destinationFK = new String[] { "" };
			String[] destinationFV = new String[] { "" };
			int[] outputType = new int[] { 0 };
			batch.applySettings(recording, windowSize, windowOverlap, samplingRate,
					normalise, perWindow, overall, destinationFK, destinationFV,
					outputType);
			controller.rtm_.clearTable();
			controller.rtm_.fillTable(recording[0]);
			controller.fstm_.clearTable();
			controller.fstm_.fillTable(controller.dm_.featureDefinitions,
					controller.dm_.defaults, controller.dm_.is_primary);

			this.windowSize.setText(Integer.toString(windowSize[0]));
			this.windowOverlap.setText(Double.toString(windowOverlap[0]));
			setSamplingRateBox(samplingRate[0]);
			this.controller.normalise.setSelected(normalise[0]);
			saveWindow.setSelected(perWindow[0]);
			saveOverall.setSelected(overall[0]);
			definition.setText(destinationFK[0]);
			value.setText(destinationFV[0]);
			if (outputType[0] == 0) {
				((javax.swing.JRadioButtonMenuItem) controller.outputType
						.getMenuComponent(0)).setSelected(true);
				controller.outputTypeAction.setSelected(0);
			} else {
				((javax.swing.JRadioButtonMenuItem) controller.outputType
						.getMenuComponent(1)).setSelected(true);
				controller.outputTypeAction.setSelected(1);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, e1.getMessage(),
					"ERROR", JOptionPane.ERROR_MESSAGE);
		}
		
	}

	private void setSamplingRateBox(double rate) {
		if (rate <= 8000.0) {
			((javax.swing.JRadioButtonMenuItem) controller.sampleRate
					.getMenuComponent(0)).setSelected(true);
		} else if (rate <= 11025.0) {
			((javax.swing.JRadioButtonMenuItem) controller.sampleRate
					.getMenuComponent(1)).setSelected(true);
		} else if (rate <= 16000.0) {
			((javax.swing.JRadioButtonMenuItem) controller.sampleRate
					.getMenuComponent(2)).setSelected(true);
		} else if (rate <= 22050.0) {
			((javax.swing.JRadioButtonMenuItem) controller.sampleRate
					.getMenuComponent(3)).setSelected(true);
		} else {
			((javax.swing.JRadioButtonMenuItem) controller.sampleRate
					.getMenuComponent(4)).setSelected(true);
		}
	}

}
