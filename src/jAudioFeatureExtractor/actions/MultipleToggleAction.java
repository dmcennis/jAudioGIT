package jAudioFeatureExtractor.actions;

import jAudioFeatureExtractor.FeatureSelectorTableModel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTable;

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
	 * Contructor that takes a reference to a table.
	 * @param f fetaures table.
	 */
	public MultipleToggleAction(JTable f) {
		features = f;
	}

	/**
	 * Group select or deselct on all selected rows.
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
