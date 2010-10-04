package jAudioFeatureExtractor.actions;

import jAudioFeatureExtractor.Controller;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

/**
 * Removes the selected batch from the set of batch files currently loaded. Can
 * not be executed if there are no batches to be removed.
 * 
 * @author Daniel McEnnis
 */
public class RemoveBatchAction extends AbstractAction {

	static final long serialVersionUID = 1;

	private Controller controller;

	/**
	 * Constructor that sets the menu text and stores a reference to the controller.
	 * @param c near global controller.
	 */
	public RemoveBatchAction(Controller c) {
		super("Remove Batch...");
		controller = c;
	}

	/**
	 * Removes the selected batch from the list of stored batches.
	 */
	public void actionPerformed(ActionEvent e) {
		int count = 0;

		Component src = controller.removeBatch.getMenuComponent(count);
		String action = "";
		if (src instanceof JMenuItem) {
			action = ((JMenuItem) src).getActionCommand();
		}

		while (!e.getActionCommand().equals(action)) {
			src = controller.removeBatch.getMenuComponent(++count);
			if (src instanceof JMenuItem) {
				action = ((JMenuItem) src).getActionCommand();
			}
		}
		controller.batches.remove(count);
		controller.removeBatch.remove(count);
		controller.viewBatch.remove(count);
		if (controller.batches.size() == 0) {
			controller.removeBatch.setEnabled(false);
			controller.viewBatch.setEnabled(false);
		}
	}

}
