/**
 * 
 */
package jAudioFeatureExtractor;

import java.util.Vector;
import jAudioFeatureExtractor.Aggregators.Aggregator;

/**
 * AggListTableModel
 *
 * The model backing the list of available aggregators.
 *
 * @author Daniel McEnnis
 *
 */
public class AggListTableModel extends javax.swing.table.DefaultTableModel{

	private Vector<Aggregator> agg;
	
	/**
	 * Construct an empty list of aggregators
	 */
	public AggListTableModel(){
		super(new Object[]{"Global","Name"},2);
		agg = new Vector<Aggregator>();
		
	}
	
	/**
	 * Load in the data on the available aggregators.
	 *
	 * @param data map containing aggregators and their metadata.
	 */
	public void init(java.util.HashMap<String, Aggregator> data){
		agg.clear();
		super.dataVector.clear();
		agg.addAll(data.values());
		for(int i=0;i<agg.size();++i){
			Vector row = new Vector();
			row.add(new Boolean(agg.get(i).getAggregatorDefinition().generic));
			row.add(agg.get(i).getAggregatorDefinition().name);
			dataVector.add(row);
		}
		fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}


	@Override
	public void removeRow(int row) {
		agg.remove(row);
		super.removeRow(row);
	}
	
	/** 
	 * Resets the lists of available aggregators to empty.
	 */
	public void clear(){
		super.dataVector.clear();
		agg.clear();
	}	
	
	/**
	 * returns the prototype aggregator at the given index.
	 *
	 * @param row location to get the aggregator from.
	 * @return aggregator located at row.
	 */
	public Aggregator getAggregator(int row){
		return agg.elementAt(row);
	}
	
}
