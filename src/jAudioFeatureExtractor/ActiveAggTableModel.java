/**
 * 
 */
package jAudioFeatureExtractor;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import jAudioFeatureExtractor.Aggregators.Aggregator;

/**
 * ActiveAggTableModel
 * 
 * Model holding the selection of aggregators that are to be used in per file extraction.
 *
 * @author Daniel McEnnis
 */
public class ActiveAggTableModel extends DefaultTableModel {

	Vector<Aggregator> agg;

	/**
	 * Construct this model with a new, empty aggregator set.
	 *
	 */
	public ActiveAggTableModel() {
		super(new Object[] { "Name" }, 0);
		agg = new Vector<Aggregator>();
	}

	/**
	 * return the aggregator storede in the row'th row.
	 *
	 * @param row row of the model to extract.
	 */
	public Aggregator getAggregator(int row) {
		return agg.get(row);
	}

	/**
	 * return the array of all aggregators stored in this model.
	 *
	 * @return array of aggregators in this model.
	 */
	public Aggregator[] getAggregator() {
		return agg.toArray(new Aggregator[] {});
	}

	/**
	 * set the following row to the given aggregator.
	 *
	 * @param row row to be updated
	 * @param a aggregator to insert in this row
	 * @param edited is this aggregator edited yet or not.
	 */
	public void setAggregator(int row, Aggregator a, boolean edited) {
		agg.set(row, a);
		this.setValueAt(a.getAggregatorDefinition().name, row, 0);
		fireTableCellUpdated(row, 0);
	}

	/**
	 * add an aggregator to the end of the table
	 *
	 * @param a Aggregator to be added.
	 */
	public void addAggregator(Aggregator a) {
		agg.add(a);
		this.addRow(new Object[] { a.getAggregatorDefinition().name });
	}

	/**
	 * removes the aggregator at the given index in this model.
	 *
	 * @param row row to be removed.
	 */
	public void removeAggregator(int row) {
		agg.remove(row);
		this.removeRow(row);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	/**
	 * Load the set of available aggregators into the list of possible choices.
	 *
	 * @param c controller managing connections between the model and table.
	 */
	public void init(Controller c) {
		if (c.dm_.aggregators != null) {
			for (int i = 0; i < c.dm_.aggregators.length; ++i) {
				agg.add(c.dm_.aggregators[i]);
				this.addRow(new Object[] {
						c.dm_.aggregators[i].getAggregatorDefinition().name });
			}
		}
	}
}
