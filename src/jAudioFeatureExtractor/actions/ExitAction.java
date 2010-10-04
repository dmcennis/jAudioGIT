package jAudioFeatureExtractor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * Action for exiting the program
 * @author Daniel McEnnis
 *
 */
public class ExitAction extends AbstractAction {
	
	static final long serialVersionUID = 1;

	/**
	 * Create an exit action and set its menu text.
	 *
	 */
	public ExitAction(){
		super("Exit");
	}
	
	/**
	 * Exit jAudio
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		System.exit(0);
	}

}
