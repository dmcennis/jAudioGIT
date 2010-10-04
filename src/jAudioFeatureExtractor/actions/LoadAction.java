package jAudioFeatureExtractor.actions;

import jAudioFeatureExtractor.Controller;
import jAudioFeatureExtractor.FeatureSelectorPanel;
import jAudioFeatureExtractor.FeatureSelectorTableModel;
import jAudioFeatureExtractor.ACE.XMLParsers.FileFilterXML;
import jAudioFeatureExtractor.ACE.XMLParsers.XMLDocumentParser;
import jAudioFeatureExtractor.Aggregators.Aggregator;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * Reads a file containing settings and resets jAUdio to match those settings.
 * 
 * @author Daniel McEnnis
 */
public class LoadAction extends AbstractAction {

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
	 * Constructor that sets menu text and loads references needed to load
	 * settings.
	 * 
	 * @param c
	 *            near global controller
	 * @param fstm
	 *            model containing features.
	 */
	public LoadAction(Controller c, FeatureSelectorTableModel fstm) {
		super("Load Settings...");
		fstm_ = fstm;
		controller = c;
	}

	/**
	 * Loads settings into the application. Settings include which features are
	 * selected, whether to extract globally or per window, the attributes for
	 * each feature, and the size and overlap of the analysis windows.
	 */
	public void actionPerformed(ActionEvent e) {
		if (save_file_chooser == null) {
			save_file_chooser = new JFileChooser();
			save_file_chooser.setCurrentDirectory(new File("."));
			save_file_chooser.setFileFilter(new FileFilterXML());
		}
		// Process the user's entry
		String path = null;
		int dialog_result = save_file_chooser.showOpenDialog(null);
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
			if (!to_save_to.exists()) {
				JOptionPane.showMessageDialog(null, "The file '"
						+ to_save_to.getName() + "' does not exist", "ERROR",
						JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					Object[] tmp = (Object[]) XMLDocumentParser
							.parseXMLDocument(path, "save_settings");
					window_length_text_field.setText((String) tmp[0]);
					window_overlap_fraction_text_field.setText((String) tmp[1]);
					double rate = ((Double) tmp[2]).doubleValue();
					if (rate <= 8000.0) {
						controller.samplingRateAction.setSelected(0);
					} else if (rate <= 11025.0) {
						controller.samplingRateAction.setSelected(1);
					} else if (rate <= 16000.0) {
						controller.samplingRateAction.setSelected(2);
					} else if (rate <= 22050.0) {
						controller.samplingRateAction.setSelected(3);
					} else {
						controller.samplingRateAction.setSelected(4);
					}
					controller.normalise.setSelected(((Boolean) tmp[3])
							.booleanValue());
					save_window_features_check_box
							.setSelected(((Boolean) tmp[4]).booleanValue());
					save_overall_file_featurese_check_box
							.setSelected(((Boolean) tmp[5]).booleanValue());
					if (tmp[6].equals("ACE")) {
						controller.outputTypeAction.setSelected(0);
					} else {
						controller.outputTypeAction.setSelected(1);
					}
					HashMap<String,Boolean> checked = (HashMap<String,Boolean>)tmp[7];
					HashMap<String,String[]> featureAttributes = (HashMap<String,String[]>)tmp[8];

					for(int i=0;i<fstm_.getRowCount();++i){
						String name = (String)fstm_.getValueAt(i,1);
						if(featureAttributes.containsKey(name)&&checked.containsKey(name)){
							fstm_.setValueAt(checked.get(name),i,0);
							String[] fa = featureAttributes.get(name);
							for(int j=0;j<fa.length;++j){
								controller.dm_.features[i].setElement(j,fa[j]);
							}
						}else{
							fstm_.setValueAt(Boolean.FALSE,i,0);
						}
					}
					fstm_.fireTableDataChanged();
					LinkedList<String> aggNames = (LinkedList<String>)tmp[9];
					LinkedList<String[]>aggFeatures = (LinkedList<String[]>)tmp[10];
					LinkedList<String[]>aggParameters = (LinkedList<String[]>)tmp[11];
					LinkedList<Aggregator> dest = new LinkedList<Aggregator>();
					Iterator<String> names_it = aggNames.iterator();
					Iterator<String[]> features_it = aggFeatures.iterator();
					Iterator<String[]> parameters_it = aggParameters.iterator();
					while(names_it.hasNext()){
						Aggregator agg = (Aggregator)controller.dm_.aggregatorMap.get(names_it.next());
						agg.setParameters(features_it.next(), parameters_it.next());
						dest.add(agg);
					}
					controller.dm_.aggregators = dest.toArray(new Aggregator[]{});
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * Provide access to additional settings.
	 * 
	 * @param win_length
	 *            text area that has window length
	 * @param win_overlap
	 *            text area that shows the degree of overlap
	 * @param save_window
	 *            checkbox for whether to save global average features
	 * @param save_overall
	 *            checkbox for saving features window by window
	 */
	public void setObjectReferences(JTextArea win_length,
			JTextArea win_overlap, JCheckBox save_window, JCheckBox save_overall) {
		window_length_text_field = win_length;
		window_overlap_fraction_text_field = win_overlap;
		save_window_features_check_box = save_window;
		save_overall_file_featurese_check_box = save_overall;
	}

}
