/*
 * @(#)RecordingsTableModel.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor;

import javax.swing.table.DefaultTableModel;
import jAudioFeatureExtractor.DataTypes.RecordingInfo;


/**
 * A table model used by the RecordingSelectorPanel to store references to audio
 * recordings.
 *
 * <p>Provides methods to fill a table row by row or delete everything on it. Makes
 * all cells non-editable.
 *
 * @author Cory McKay
 */
public class RecordingsTableModel
	extends DefaultTableModel
{
	
	static final long serialVersionUID = 1;

	/* CONSTRUCTOR *************************************************************/


	/**
	 * Same constructor as DefaultTableModel
	 */
	RecordingsTableModel(Object[] columnNames, int rows)
	{
		super(columnNames, rows);
	}


	/* PUBLIC METHODS **********************************************************/


	/**
	 * Deletes everything in the table and then fills it up one row at a time
	 * based on the given RecorcdingInfo array.
	 *
	 * @param	recording_list	Data to place in the table.
	 */
	public void fillTable(RecordingInfo[] recording_list)
	{
		// Remove the contents of the table
		clearTable();

		// Populate each row one by one
		if (recording_list != null)
			for (int i = 0; i < recording_list.length; i++)
			{
				Object[] row_contents = new Object[2];
				row_contents[0] = recording_list[i].identifier;
				row_contents[1] = recording_list[i].file_path;
				addRow(row_contents);
			}
	}


	/**
	 * Removes all contents of the table.
	 */
	public void clearTable()
	{
		while (getRowCount() != 0)
		{
			removeRow(0);
		}
	}

	
	/**
	 * Returns false for all cells, so that no cells are editable.
	 */
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}
}