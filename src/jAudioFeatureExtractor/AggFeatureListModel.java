/**
 * 
 */
package jAudioFeatureExtractor;

import javax.swing.table.DefaultTableModel;

/**
 * AggFeatureListModel
 * 
 * Lists the features in the given window of the edit aggregator window.
 *
 * @author Daniel McEnnis
 *
 */
public class AggFeatureListModel extends DefaultTableModel {

	/**
	 * Default constructor that calls the superclass constructor.
	 */
	public AggFeatureListModel(){
		super();
	}
	
	/**
	 * Default constructxor that calls the superclass constructor.
	 */
	public AggFeatureListModel(Object[] o,int rows){
		super(o,rows);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	
}
