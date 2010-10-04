package jAudioFeatureExtractor.actions;

import jAudioFeatureExtractor.Controller;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * Halts previously any started playback.  This only works for playback from file.
 * @author mcennis
 *
 */
public class StopPlayBackAction extends AbstractAction {
	
	static final long serialVersionUID = 1;

	private Controller controller;
	
	/**
	 * Constructor that set the menu text and stores a reference to the controller.
	 * @param c	near global controller.
	 */
	public StopPlayBackAction(Controller c){
		super("Stop Playback");
//		setEnabled(false);
		controller = c;
	}
	
	/**
	 * Tries to stop the playback.
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		stopPlayback();
	}
	
	
	protected void stopPlayback(){
		if (controller.dm_.playback_thread != null)
			controller.dm_.playback_thread.stopPlaying();
		controller.dm_.playback_thread = null;
	}

}
