package jAudioFeatureExtractor.actions;

import jAudioFeatureExtractor.Controller;
import jAudioFeatureExtractor.DataModel;
import jAudioFeatureExtractor.FeatureSelectorPanel;
import jAudioFeatureExtractor.FeatureSelectorTableModel;
import jAudioFeatureExtractor.ACE.XMLParsers.FileFilterXML;
import jAudioFeatureExtractor.Aggregators.Aggregator;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * Saves the settings of the system. Note that settings does not include the
 * files in the recording table. Bacth files should be used if it is desired to
 * save the file list as well.
 * 
 * @author Daniel McEnnis
 */
public class SaveAction extends AbstractAction {

	static final long serialVersionUID = 1;

	private JTextArea window_length_text_field;

	private JTextArea window_overlap_fraction_text_field;

	/**
	 * GUI check boxes
	 */
	private JCheckBox save_window_features_check_box;

	private JCheckBox save_overall_file_featurese_check_box;

	private FeatureSelectorTableModel fstm_;

	private JFileChooser save_file_chooser = null;

	private Controller controller;

	/**
	 * Constructor that sets the menu text and stores a reference to the
	 * controller and feature table model
	 * 
	 * @param c
	 *            near global controller
	 * @param fstm
	 *            table model that contains the features.
	 */
	public SaveAction(Controller c, FeatureSelectorTableModel fstm) {
		super("Save Settings...");
		fstm_ = fstm;
		controller = c;
	}

	/**
	 * Save the current settings to a file on disk. The file is stored in XML
	 * format.
	 */
	public void actionPerformed(ActionEvent e) {
		if (save_file_chooser == null) {
			save_file_chooser = new JFileChooser();
			save_file_chooser.setCurrentDirectory(new File("."));
			save_file_chooser.setFileFilter(new FileFilterXML());
		}

		// Process the user's entry
		String path = null;
		int dialog_result = save_file_chooser.showSaveDialog(null);
		if (dialog_result == JFileChooser.APPROVE_OPTION) // only do if OK
		// chosen
		{
			// Get the file the user chose
			File to_save_to = save_file_chooser.getSelectedFile();

			// Make sure has .xml extension
			path = to_save_to.getPath();
			String ext = jAudioFeatureExtractor.GeneralTools.StringMethods
					.getExtension(path);
			if (ext == null) {
				path += ".xml";
				to_save_to = new File(path);
			} else if (!ext.equals(".xml")) {
				path = jAudioFeatureExtractor.GeneralTools.StringMethods
						.removeExtension(path)
						+ ".xml";
				to_save_to = new File(path);
			}

			// See if user wishes to overwrite if a file with the same name
			// exists
			if (to_save_to.exists()) {
				int overwrite = JOptionPane
						.showConfirmDialog(
								null,
								"This file already exists.\nDo you wish to overwrite it?",
								"WARNING", JOptionPane.YES_NO_OPTION);
				if (overwrite != JOptionPane.YES_OPTION)
					path = null;
			}

			// Return the selected file path

			File dest = new File(path);
			String winSize = this.window_length_text_field.getText();
			String winOverlap = this.window_overlap_fraction_text_field
					.getText();
			double sampleRate = controller.samplingRateAction.getSamplingRate();
			boolean normalise = controller.normalise.isSelected();
			boolean perWindow = this.save_window_features_check_box
					.isSelected();
			boolean overall = this.save_overall_file_featurese_check_box
					.isSelected();
			try {
				FileWriter fw = new FileWriter(dest);
				String sep = System.getProperty("line.separator");
				fw.write("<?xml version=\"1.0\"?>" + sep);
				fw.write("<!DOCTYPE save_settings [" + sep);
				fw
						.write("\t<!ELEMENT save_settings (windowSize,windowOverlap,samplingRate,normalise,perWindowStats,overallStats,outputType,feature+,aggregator+)>"
								+ sep);
				fw.write("\t<!ELEMENT windowSize (#PCDATA)>" + sep);
				fw.write("\t<!ELEMENT windowOverlap (#PCDATA)>" + sep);
				fw.write("\t<!ELEMENT samplingRate (#PCDATA)>" + sep);
				fw.write("\t<!ELEMENT normalise (#PCDATA)>" + sep);
				fw.write("\t<!ELEMENT perWindowStats (#PCDATA)>" + sep);
				fw.write("\t<!ELEMENT overallStats (#PCDATA)>" + sep);
				fw.write("\t<!ELEMENT outputType (#PCDATA)>" + sep);
				fw.write("\t<!ELEMENT feature (name,active,attribute*)>" + sep);
				fw.write("\t<!ELEMENT name (#PCDATA)>" + sep);
				fw.write("\t<!ELEMENT active (#PCDATA)>" + sep);
				fw.write("\t<!ELEMENT attribute (#PCDATA)>" + sep);
				fw.write("\t<!ELEMENT aggregator (aggregatorName, aggregatorFeature*, aggregatorAttribute*)>"+sep);
				fw.write("\t<!ELEMENT aggregatorName (#PCDATA)>"+sep);
				fw.write("\t<!ELEMENT aggregatorFeature (#PCDATA)>"+sep);
				fw.write("\t<!ELEMENT aggregatorAttribute (#PCDATA)>"+sep);
				fw.write("]>" + sep);
				fw.write(sep);
				fw.write("<save_settings>" + sep);
				fw.write("\t<windowSize>" + winSize + "</windowSize>" + sep);
				fw.write("\t<windowOverlap>" + winOverlap + "</windowOverlap>"
						+ sep);
				fw.write("\t<samplingRate>" + sampleRate + "</samplingRate>"
						+ sep);
				fw.write("\t<normalise>" + normalise + "</normalise>" + sep);
				fw.write("\t<perWindowStats>" + perWindow + "</perWindowStats>"
						+ sep);
				fw
						.write("\t<overallStats>" + overall + "</overallStats>"
								+ sep);
				fw.write("\t<outputType>"
						+ controller.outputTypeAction.getOutputType()
						+ "</outputType>" + sep);
				for (int i = 0; i < fstm_.getRowCount(); ++i) {

					Boolean active = (Boolean) fstm_.getValueAt(i, 0);
					String name = (String)fstm_.getValueAt(i,1);
					String[] attributes = new String[controller.dm_.features[i]
							.getFeatureDefinition().attributes.length];
					for (int j = 0; j < attributes.length; ++j) {
						try {
							attributes[j] = controller.dm_.features[i]
									.getElement(j);
						} catch (Exception e1) {
							attributes[i] = "";
							e1.printStackTrace();
						}
					}

					fw.write("\t<feature>" + sep);
					fw.write("\t\t<name>"+name+"</name>"+sep);
					fw.write("\t\t<active>" + active.toString() + "</active>"
							+ sep);
					for (int j = 0; j < attributes.length; ++j) {
						fw.write("\t\t<attribute>" + attributes[j]
								+ "</attribute>" + sep);
					}
					fw.write("\t</feature>" + sep);
				}
				Aggregator[] aggregatorArray = controller.dm_.aggregators;
				for(int i=0;i<aggregatorArray.length;++i){
					fw.write("\t\t\t<aggregator>"+sep);
					fw.write("\t\t\t\t<aggregatorName>"+aggregatorArray[i].getAggregatorDefinition().name+"</aggregatorName>"+sep);
					String[] aggregatorFeatures = aggregatorArray[i].getFeaturesToApply();
					if(aggregatorFeatures != null){
						for(int j=0;j<aggregatorFeatures.length;++j){
							fw.write("\t\t\t\t<aggregatorFeature>"+aggregatorFeatures[j]+"</aggregatorFeature>"+sep);
						}
					}
					String[] aggregatorParameters = aggregatorArray[i].getParamaters();
					if(aggregatorParameters!= null){
						for(int j=0;j<aggregatorParameters.length;++j){
							fw.write("\t\t\t\t<aggregatorAttribute>"+aggregatorParameters[j]+"</aggregatorAttribute>"+sep);
						}
					}
					fw.write("\t\t\t</aggregator>"+sep);
				}

				fw.write("</save_settings>" + sep);
				fw.close();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "ERROR",
						JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Set the references needed to store the settings.
	 * 
	 * @param win_length
	 *            text box that holds the width of the analysis window in
	 *            samples.
	 * @param win_overlap
	 *            text box that holds the percent overlap between windows
	 * @param save_window
	 *            checkbox indicating whether features should be saved per
	 *            window
	 * @param save_overall
	 *            checkbox indicating whether global features should be saved. .
	 */
	public void setObjectReferences(JTextArea win_length,
			JTextArea win_overlap, JCheckBox save_window, JCheckBox save_overall) {
		window_length_text_field = win_length;
		window_overlap_fraction_text_field = win_overlap;
		save_window_features_check_box = save_window;
		save_overall_file_featurese_check_box = save_overall;
	}

}
