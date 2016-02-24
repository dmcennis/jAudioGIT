package org.jaudio.gui.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Action for allowing keyboard shortcuts to select or deselect multiple
 * features simultaneously.
 * 
 * @author Daniel McEnnis
 */
public class MultipleToggleAction extends AbstractAction {

	static final long serialVersionUID = 1;

	private JTable features;

	/**
	 * Constructor that takes a reference to a table.
	 * @param f features table.
	 */
	public MultipleToggleAction(JTable f) {
		features = f;
	}

	/**
	 * Group select or deselect on all selected rows.
	 */
	public void actionPerformed(ActionEvent e) {
		int[] selectedRows = features.getSelectedRows();
		if (selectedRows.length > 0) {
			boolean valueToBeSet = ((Boolean) features.getValueAt(
					selectedRows[0], 0)).booleanValue();
			for (int i = 0; i < selectedRows.length; ++i) {
				features.setValueAt(new Boolean(!valueToBeSet),
						selectedRows[i], 0);
			}
		}
	}

}
