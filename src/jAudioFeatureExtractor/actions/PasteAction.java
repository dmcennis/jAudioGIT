package jAudioFeatureExtractor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * Paste action included for completeness.
 * 
 * @author Daniel McEnnis
 *
 */
public class PasteAction extends AbstractAction {
	
	static final long serialVersionUID = 1;

	/**
	 * Constructor that sets the menu text.
	 *
	 */
	public PasteAction(){
		super("Paste");
		setEnabled(false);
	}
	
	/**
	 * Performs the paste action.
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
