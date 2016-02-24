package org.jaudio.gui.actions;

import jAudioFeatureExtractor.DataModel;
import jAudioFeatureExtractor.GlobalWindowChange;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Action for changing all feature window sizes simultaneously.
 * 
 * @author Daniel McEnnis
 */
public class GlobalWindowChangeAction extends AbstractAction {

	static final long serialVersionUID = 1;

	private DataModel fm_;

	private GlobalWindowChange gwb_;

	/**
	 * Constructor that sets menu text and adds reference to the data model
	 * where features are stored.
	 * 
	 * @param fm data model to be altered
	 */
	public GlobalWindowChangeAction(DataModel fm) {
		super("Global Window Change");
		fm_ = fm;
	}

	/**
	 * Shows the GlobalWindowChange window.
	 */
	public void actionPerformed(ActionEvent e) {
		if (gwb_ == null) {
			gwb_ = new GlobalWindowChange(fm_);
		}
		gwb_.setVisible(true);
	}

}
