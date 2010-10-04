package jAudioFeatureExtractor.GeneralTools;

import java.awt.Component;
import javax.swing.*;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class FeatureDisplay extends DefaultTableCellRenderer {
	
	static final long serialVersionUID = 1;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Boolean isPrimary = (Boolean)table.getModel().getValueAt(row,3);
		if(((Boolean)table.getModel().getValueAt(row,3)).booleanValue()){
			JLabel tmp =  (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
					row, column);
			tmp.setFont(tmp.getFont().deriveFont(java.awt.Font.BOLD));
			return tmp;
		}else{
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
					row, column);
		}
	}

}
