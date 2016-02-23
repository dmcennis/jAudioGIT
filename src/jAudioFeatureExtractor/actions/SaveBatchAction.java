package jAudioFeatureExtractor.actions;

import jAudioFeatureExtractor.Controller;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Save the current list of batches into an XML format file.
 * 
 * @author Daniel McEnnis
 *
 */
public class SaveBatchAction extends AbstractAction {

	static final long serialVersionUID = 1;
	
	private Controller controller;

	/**
	 * Constructor that sets the menu text and stores a reference to the controller
	 * @param c	near global controller.
	 */
	public SaveBatchAction(Controller c) {
		super("Save BatchFile...");
		controller = c;
	}

	/**
	 * Saves the current set of batches to file.
	 */
	public void actionPerformed(ActionEvent e) {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		JFileChooser chooser = new JFileChooser();
		chooser
				.setBackground(new Color((float) 0.75, (float) 0.85,
						(float) 1.0));
		int state = chooser.showSaveDialog(null);
		File path = chooser.getSelectedFile();
		if ((path != null) && (state == JFileChooser.APPROVE_OPTION)) {
			try {
				boolean save = true;
				if (controller.batches.size() == 0) {
					JOptionPane
							.showMessageDialog(
									null,
									bundle.getString("no.batches.have.been.prepared.to.save.try.adding.batches.first"),
									"ERROR", JOptionPane.ERROR_MESSAGE);
				} else {
					if (path.exists()) {
						int overwrite = JOptionPane
								.showConfirmDialog(
										null,
										bundle.getString("this.file.already.exists.ndo.you.wish.to.overwrite.it1"),
										"WARNING", JOptionPane.YES_NO_OPTION);
						if (overwrite != JOptionPane.YES_OPTION) {
							save = false;
						}
					}
					if (save == true) {
						path.createNewFile();
					}
					FileWriter fw = new FileWriter(path);
					String sep = System.getProperty("line.separator");
					fw.write("<?xml version=\"1.0\"?>" + sep);
					fw.write("<!DOCTYPE batchFile ["+sep);
					fw.write("\t<!ELEMENT batchFile (batch+)>"+sep);
					fw.write("\t<!ELEMENT batch (fileSet,settings,destination+)>"+sep);
					fw.write("\t<!ATTLIST batch ID CDATA \"\" >"+sep);
					fw.write("\t<!ELEMENT fileSet (file+)>"+sep);
					fw.write("\t<!ELEMENT file (#PCDATA)>"+sep);
					fw.write("\t<!ELEMENT settings (windowSize,windowOverlap,samplingRate,normalise,perWindowStats,overallStats,outputType,feature+,aggregator+)>"+sep);
					fw.write("\t<!ELEMENT windowSize (#PCDATA)>"+sep);
					fw.write("\t<!ELEMENT windowOverlap (#PCDATA)>"+sep);
					fw.write("\t<!ELEMENT samplingRate (#PCDATA)>"+sep);
					fw.write("\t<!ELEMENT normalise (#PCDATA)>"+sep);
					fw.write("\t<!ELEMENT perWindowStats (#PCDATA)>"+sep);
					fw.write("\t<!ELEMENT overallStats (#PCDATA)>"+sep);
					fw.write("\t<!ELEMENT outputType (#PCDATA)>"+sep);
					fw.write("\t<!ELEMENT feature (name,active,attribute*)>"+sep);
					fw.write("\t<!ELEMENT name (#PCDATA)>"+sep);
					fw.write("\t<!ELEMENT active (#PCDATA)>"+sep);
					fw.write("\t<!ELEMENT attribute (#PCDATA)>"+sep);
					fw.write("\t<!ELEMENT destination (#PCDATA)>"+sep);
					fw.write("\t<!ELEMENT aggregator (aggregatorName, aggregatorFeature*, aggregatorAttribute*)>"+sep);
					fw.write("\t<!ELEMENT aggregatorName (#PCDATA)>"+sep);
					fw.write("\t<!ELEMENT aggregatorFeature (#PCDATA)>"+sep);
					fw.write("\t<!ELEMENT aggregatorAttribute (#PCDATA)>"+sep);
					fw.write("]>"+sep);
					fw.write(sep);
					fw.write("<batchFile>"+sep);
					for(int i=0;i<controller.batches.size();++i){
						fw.write(controller.batches.get(i).outputXML());
					}
					fw.write("</batchFile>"+sep);
					fw.close();
					fw = null;
				}
			} catch (HeadlessException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "ERROR",
						JOptionPane.ERROR_MESSAGE);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}

}
