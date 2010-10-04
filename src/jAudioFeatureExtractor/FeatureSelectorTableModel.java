/*
 * @(#)FeatureSelectorTableModel.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor;

import javax.swing.table.DefaultTableModel;
import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;


/**
 * A table model used by the FeatureSelectorPanel to store references to feature
 * definitions.
 *
 * <p>Provides methods to fill a table row by row. Makes all except the first
 * column non-editable. The first column is filled with check boxes.
 *
 * @author Cory McKay
 */
public class FeatureSelectorTableModel
	extends DefaultTableModel
{
	
	static final long serialVersionUID = 1;
	
	/* CONSTRUCTOR *************************************************************/


	/**
	 * Same constructor as DefaultTableModel
	 */
	FeatureSelectorTableModel(Object[] columnNames, int rows)
	{
		super(columnNames, rows);
	}


	/* PUBLIC METHODS **********************************************************/


	/**
	 * Deletes everything in the table and then fills it up one row at a time
	 * based on the given FeatureDefinition array.
	 *
	 * @param	definitions				Data to place in the table.
	 * @param	feature_save_defaults	The default save setting for each feature.
	 */
	public void fillTable(FeatureDefinition[] definitions, boolean[] feature_save_defaults, boolean[] is_primary_feature)
	{
		while (getRowCount() != 0)
			removeRow(0);

		for (int i = 0; i < definitions.length; i++)
		{
			Object[] row_contents = new Object[4];
			row_contents[0] = new Boolean(feature_save_defaults[i]);
			row_contents[1] = definitions[i].name;
			if (definitions[i].dimensions > 0)
				row_contents[2] = definitions[i].dimensions;
			else
				row_contents[2] = "variable";
			row_contents[3] = new Boolean(is_primary_feature[i]);
			addRow(row_contents);
		}
	}
	
	public void clearTable(){
		while (getRowCount() != 0)
			removeRow(0);
	}


	/**
	 * Returns the type of class used for each column.
	 * Necessary in order for text boxes to be properly displayed.
	 *
	 * @param	column	Column to check.
	 */
	public Class getColumnClass(int column)
	{
		return getValueAt(0, column).getClass();
	}


	/**
	 * Returns false for all cells except those in the first column,
	 * so that only cells in the first column are editable.
	 */
	public boolean isCellEditable(int row, int column)
	{
		if (column == 0)
			return true;
		else
			return false;
	}
}