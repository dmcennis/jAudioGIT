package jAudioFeatureExtractor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * Cut Action that is included for completenes only.
 * @author Daniel McEnnis
 *
 */
public class CutAction extends AbstractAction {
	
	static final long serialVersionUID = 1;

	/**
	 * Constructore that sets the menu text.
	 *
	 */
	public CutAction(){
		super("Cut");
		setEnabled(false);
	}
	
	/**
	 * Performs the cut action
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
