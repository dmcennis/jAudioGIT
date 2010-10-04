package jAudioFeatureExtractor.actions;

import jAudioFeatureExtractor.Controller;
import jAudioFeatureExtractor.ACE.DataTypes.Batch;
import jAudioFeatureExtractor.ACE.XMLParsers.XMLDocumentParser;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * Action for loading previously saved batch files.
 * 
 * @author Daniel McEnnis
 */
public class LoadBatchAction extends AbstractAction {

	static final long serialVersionUID = 1;

	Controller controller;

	/**
	 * Constructor that sets menu text and saves reference to the controller
	 * @param c near global controller
	 */
	public LoadBatchAction(Controller c) {
		super("Load Batchfile...");
		controller = c;
	}

	/**
	 * Loads the contents of a saved batch file into batches in the application
	 */
	public void actionPerformed(ActionEvent e) {

		JFileChooser chooser = new JFileChooser();
		int state = chooser.showOpenDialog(null);
		File file = chooser.getSelectedFile();
		if ((file != null) && (state == JFileChooser.APPROVE_OPTION)) {
			try {
				controller.batches.clear();
				Object[] retList = (Object[]) XMLDocumentParser
						.parseXMLDocument(file.getAbsolutePath(), "batchFile");
				for (int i = 0; i < retList.length; ++i) {
					controller.batches.add((Batch) retList[i]);
					controller.batches.get(i).setDataModel(controller.dm_);

					JMenuItem remove = new JMenuItem(controller.batches.get(i)
							.getName());
					remove.addActionListener(controller.removeBatchAction);
					controller.removeBatch.add(remove);

					JMenuItem view = new JMenuItem(controller.batches.get(i)
							.getName());
					view.addActionListener(controller.viewBatchAction);
					controller.viewBatch.add(view);
				}
				if (controller.batches.size() > 0) {
					controller.removeBatch.setEnabled(true);
					controller.viewBatch.setEnabled(true);
				}
				JOptionPane.showMessageDialog(null,
						"Loading of batch file successful.", "Information",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}

		}

	}

}
