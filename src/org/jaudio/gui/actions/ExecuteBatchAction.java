package org.jaudio.gui.actions;

import jAudioFeatureExtractor.BatchExecutionThread;
import jAudioFeatureExtractor.Controller;
import jAudioFeatureExtractor.OuterFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Action for executing one or more batches.
 * 
 * @author Daniel McEnnis
 *
 */
public class ExecuteBatchAction extends AbstractAction {

	static final long serialVersionUID = 1;

	private Controller controller;
	
	private OuterFrame outerFrame;
	
	/**
	 * Constructor that stores references to the application shell and global controller.
	 * @param c near global controller
	 * @param of outer shell of the application
	 */
	public ExecuteBatchAction(Controller c, OuterFrame of){
		super("Execute Batches");
		controller = c;
		outerFrame = of;
	}
	
	/**
	 * Executes all currently defined batches.
	 */
	public void actionPerformed(ActionEvent e) {
		BatchExecutionThread executionThread = new BatchExecutionThread(controller,outerFrame);
		executionThread.start();
	}

}
