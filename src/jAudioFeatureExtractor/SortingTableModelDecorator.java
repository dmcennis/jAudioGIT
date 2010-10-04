package jAudioFeatureExtractor;

import java.awt.Component;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * Decorator model on top of normal feature selection model.
 * 
 * @author Daniel McEnnis
 */
public class SortingTableModelDecorator implements TableModelListener,
		TableModel {

	private int[] indeci;

	private TableModel base;

	/**
	 * Creates a new model that decorates the underlying model with sorting
	 * capabilities
	 * 
	 * @param base
	 *            Underlying model this model is built on top of.
	 */
	public SortingTableModelDecorator(TableModel base) {
		this.base = base;
		base.addTableModelListener(this);
		resetIndeci();
	}

	/**
	 * Method for handling generic changes. Since these changes could involve
	 * creation or deletion of features from the table, a the table is
	 * completely reset to the default order.
	 */
	public void tableChanged(TableModelEvent e) {
		resetIndeci();
	}

	/**
	 * passed directly to underlying model
	 */
	public int getRowCount() {

		return base.getRowCount();
	}

	/**
	 * passed directly to underlying model
	 */
	public int getColumnCount() {

		return base.getColumnCount();
	}

	/**
	 * passed directly to underlying model
	 */
	public String getColumnName(int columnIndex) {

		return base.getColumnName(columnIndex);
	}

	/**
	 * passed directly to underlying model
	 */
	public Class<?> getColumnClass(int columnIndex) {
		return base.getColumnClass(columnIndex);
	}

	/**
	 * passed directly to underlying model
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return base.isCellEditable(indeci[rowIndex], columnIndex);
	}

	/**
	 * passed to underlying model with a translated row index
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		return base.getValueAt(indeci[rowIndex], columnIndex);
	}

	/**
	 * passed to underlying model with a translated row index.
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		base.setValueAt(aValue, indeci[rowIndex], columnIndex);
	}

	/**
	 * passed directly to underlying model
	 */
	public void addTableModelListener(TableModelListener l) {
		base.addTableModelListener(l);
	}

	/**
	 * passed directly to underlying model
	 */
	public void removeTableModelListener(TableModelListener l) {
		base.removeTableModelListener(l);
	}

	/**
	 * Sorts the rows using quicksort
	 * 
	 * @param index
	 *            Which column is being sorted
	 */
	public void sort(int index) {
		Vector<Integer> vectorIndeci = new Vector<Integer>();
		if (index == 2) {
			Vector<Integer> vectorColumn = new Vector<Integer>();
			for (int i = 0; i < base.getRowCount(); ++i) {
				base.getValueAt(i, index);
				if (base.getValueAt(i, index).getClass().equals(String.class)) {
					vectorColumn.add(0);
				} else {
					vectorColumn.add((Integer) base.getValueAt(i, index));
				}
				vectorIndeci.add(i);
			}
			intQuickSort(vectorColumn, vectorIndeci);
		} else {
			Vector<Comparable> vectorColumn = new Vector<Comparable>();
			for (int i = 0; i < base.getRowCount(); ++i) {
				vectorColumn.add((Comparable) base.getValueAt(i, index));
				vectorIndeci.add(i);
			}
			quickSort(vectorColumn, vectorIndeci);
		}
		Integer[] tmp = vectorIndeci.toArray(new Integer[] {});
		indeci = new int[tmp.length];

		for (int i = 0; i < tmp.length; ++i) {
			indeci[i] = tmp[i].intValue();
		}
	}

	/**
	 * This method restores the indeci to their default encoding.
	 */
	public void resetIndeci() {
		indeci = new int[base.getRowCount()];
		for (int i = 0; i < indeci.length; ++i) {
			indeci[i] = i;
		}
	}

	private void quickSort(Vector<Comparable> vectorColumn,
			Vector<Integer> vectorIndeci) {
		Comparable splitO;
		int splitI;
		Vector<Comparable> greaterO = new Vector<Comparable>();
		Vector<Comparable> lesserO = new Vector<Comparable>();
		Vector<Integer> greaterI = new Vector<Integer>();
		Vector<Integer> lesserI = new Vector<Integer>();
		splitO = vectorColumn.get(0);
		splitI = vectorIndeci.get(0);
		int i = 1;
		while (i < vectorColumn.size()) {
			int compare = (vectorColumn.get(i)).compareTo(splitO);
			if (compare <= 0) {
				lesserO.add(lesserO.size(), vectorColumn.get(i));
				lesserI.add(lesserI.size(), vectorIndeci.get(i));
			} else {
				greaterO.add(greaterO.size(), vectorColumn.get(i));
				greaterI.add(greaterI.size(), vectorIndeci.get(i));
			}
			++i;
		}
		if (lesserO.size() > 0) {
			quickSort(lesserO, lesserI);
		}
		if (greaterO.size() > 0) {
			quickSort(greaterO, greaterI);
		}
		vectorColumn.clear();
		vectorIndeci.clear();
		for (i = 0; i < lesserO.size(); ++i) {
			vectorColumn.add(lesserO.get(i));
			vectorIndeci.add(lesserI.get(i));
		}
		vectorColumn.add(splitO);
		vectorIndeci.add(splitI);
		for (i = 0; i < greaterO.size(); ++i) {
			vectorColumn.add(greaterO.get(i));
			vectorIndeci.add(greaterI.get(i));
		}
	}

	private void intQuickSort(Vector<Integer> vectorColumn,
			Vector<Integer> vectorIndeci) {
		int splitO;
		int splitI;
		Vector<Integer> greaterO = new Vector<Integer>();
		Vector<Integer> lesserO = new Vector<Integer>();
		Vector<Integer> greaterI = new Vector<Integer>();
		Vector<Integer> lesserI = new Vector<Integer>();
		splitO = vectorColumn.get(0);
		splitI = vectorIndeci.get(0);
		int i = 1;
		while (i < vectorColumn.size()) {
			int compare = (vectorColumn.get(i)).intValue() - splitO;
			if (compare <= 0) {
				lesserO.add(lesserO.size(), vectorColumn.get(i));
				lesserI.add(lesserI.size(), vectorIndeci.get(i));
			} else {
				greaterO.add(greaterO.size(), vectorColumn.get(i));
				greaterI.add(greaterI.size(), vectorIndeci.get(i));
			}
			++i;
		}
		if (lesserO.size() > 0) {
			intQuickSort(lesserO, lesserI);
		}
		if (greaterO.size() > 0) {
			intQuickSort(greaterO, greaterI);
		}
		vectorColumn.clear();
		vectorIndeci.clear();
		for (i = 0; i < lesserO.size(); ++i) {
			vectorColumn.add(lesserO.get(i));
			vectorIndeci.add(lesserI.get(i));
		}
		vectorColumn.add(splitO);
		vectorIndeci.add(splitI);
		for (i = 0; i < greaterO.size(); ++i) {
			vectorColumn.add(greaterO.get(i));
			vectorIndeci.add(greaterI.get(i));
		}
	}

	private int getRealPrefferedHeaderWidth(JTable t, int col) {
		TableColumn tc = t.getColumn(t.getColumnName(col));
		TableCellRenderer tcr = tc.getHeaderRenderer();
		if (tcr == null) {
			tcr = t.getTableHeader().getDefaultRenderer();
		}
		Component c = tcr.getTableCellRendererComponent(t, tc.getHeaderValue(),
				false, false, 0, 0);
		return c.getPreferredSize().width;
	}

	/**
	 * This method calculates a reasonable preffered width by calculating the
	 * width of both the header and each element of the table.
	 * 
	 * @param t
	 *            link to the JTable associated with this model
	 * @param col
	 *            which column's width is bein calcualted.
	 * @return Preferred width of the column.
	 */
	public int getRealPrefferedWidth(JTable t, int col) {
		int h = getRealPrefferedHeaderWidth(t, col);
		int b = getRealPrefferedBodyWidth(t, col);
		return Math.max(h, b);
	}

	private int getRealPrefferedBodyWidth(JTable t, int col) {
		int minWidth = 0;
		int thisRowSize = 0;
		for (int i = 0; i < this.getRowCount(); ++i) {
			int ret = 0;
			TableCellRenderer r = t.getCellRenderer(i, col);
			Component c = r.getTableCellRendererComponent(t, t.getValueAt(i,
					col), false, false, i, col);
			ret = c.getPreferredSize().width;
			if (ret > minWidth) {
				minWidth = ret;
			}
		}
		return minWidth;
	}

}
