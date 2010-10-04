/**
 * 
 */
package jAudioFeatureExtractor;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import jAudioFeatureExtractor.Aggregators.Aggregator;
import jAudioFeatureExtractor.Aggregators.Mean;

/**
 * AggregatorFrame
 *
 * Window for altering the available aggregators for per-file analysis.
 * 
 * @author Daniel McEnnis
 *
 */
public class AggregatorFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JScrollPane ActiveAggList = null;

	private JTable ActiveAggTable = null;

	private JScrollPane AggList = null;

	private JTable AggListTable = null;

	private JPanel AggButtonPanel = null;

	private JButton AggAdd = null;

	private JButton AggRemove = null;

	private JButton AggEdit = null;
	
	private AggEditorFrame aggEditorFrame = null;
	
	private Controller controller;

	private JButton DoneButton = null;

	private JButton Abort = null;

	/**
	 * This is the default constructor
	 */
	public AggregatorFrame(Controller c) {
		super();
		
		controller = c;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("Aggregators");
		this.setBounds(new Rectangle(0, 22, 1000, 1000));
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getActiveAggList(), BorderLayout.WEST);
			jContentPane.add(getAggList(), BorderLayout.EAST);
			jContentPane.add(getAggButtonPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes ActiveAggList	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getActiveAggList() {
		if (ActiveAggList == null) {
			ActiveAggList = new JScrollPane();
			ActiveAggList.setViewportView(getActiveAggTable());
		}
		return ActiveAggList;
	}

	/**
	 * This method initializes ActiveAggTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getActiveAggTable() {
		if (ActiveAggTable == null) {
			ActiveAggTable = new JTable();
			controller.activeAgg_ = new ActiveAggTableModel();
			controller.activeAgg_.init(controller);
			ActiveAggTable.setModel(controller.activeAgg_);
			ActiveAggTable.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if(e.getClickCount()==2){
						int row = ActiveAggTable.getSelectedRow();
						if(row>=0){
							aggEditorFrame = new AggEditorFrame((jAudioFeatureExtractor.Aggregators.Aggregator)controller.activeAgg_.getAggregator(row),controller);
							aggEditorFrame.setVisible(true);
							((ActiveAggTableModel)ActiveAggTable.getModel()).setAggregator(row, aggEditorFrame.getAggregator(), aggEditorFrame.isEdited());			
						}
					}
				}
			});
		}
		return ActiveAggTable;
	}

	/**
	 * This method initializes AggList	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getAggList() {
		if (AggList == null) {
			AggList = new JScrollPane();
			AggList.setViewportView(getAggListTable());
		}
		return AggList;
	}

	/**
	 * This method initializes AggListTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getAggListTable() {
		if (AggListTable == null) {
			AggListTable = new JTable();
			controller.aggList_ = new AggListTableModel();
			controller.aggList_.init(controller.dm_.aggregatorMap);
			AggListTable.setModel(controller.aggList_);
			AggListTable.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if(e.getClickCount()==2){
						int[] row = AggListTable.getSelectedRows();
						for(int i=0;i<row.length;++i){
							AggListTableModel list = (AggListTableModel)AggListTable.getModel();
							Aggregator prototype = list.getAggregator(row[i]);
							Aggregator newAgg = (Aggregator)prototype.clone();
							((ActiveAggTableModel)ActiveAggTable.getModel()).addAggregator(newAgg);							
						}
					}
				}
			});
		}
		return AggListTable;
	}

	/**
	 * This method initializes AggButtonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAggButtonPanel() {
		if (AggButtonPanel == null) {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.gridy = 4;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 3;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			AggButtonPanel = new JPanel();
			AggButtonPanel.setLayout(new GridBagLayout());
			AggButtonPanel.add(getAggAdd(), gridBagConstraints);
			AggButtonPanel.add(getAggRemove(), gridBagConstraints1);
			AggButtonPanel.add(getAggEdit(), gridBagConstraints2);
			AggButtonPanel.add(getDoneButton(), gridBagConstraints11);
			AggButtonPanel.add(getAbort(), gridBagConstraints21);
		}
		return AggButtonPanel;
	}

	/**
	 * This method initializes AggAdd	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAggAdd() {
		if (AggAdd == null) {
			AggAdd = new JButton();
			AggAdd.setText("Add");
			AggAdd.setToolTipText("Add a new Aggregator to be applied");
			AggAdd.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int row = AggListTable.getSelectedRow();
					if(row != -1){
						AggListTableModel list = (AggListTableModel)AggListTable.getModel();
						Aggregator prototype = list.getAggregator(row);
						Aggregator newAgg = (Aggregator)prototype.clone();
						((ActiveAggTableModel)ActiveAggTable.getModel()).addAggregator(newAgg);
					}
					System.out.println("actionPerformed()"); 				}
			});
		}
		return AggAdd;
	}

	/**
	 * This method initializes AggRemove	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAggRemove() {
		if (AggRemove == null) {
			AggRemove = new JButton();
			AggRemove.setText("Remove");
			AggRemove.setToolTipText("Remove an aggregator that has been previously defined");
			AggRemove.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int row = ActiveAggTable.getSelectedRow();
					if(row >= 0){
						((ActiveAggTableModel)ActiveAggTable.getModel()).removeAggregator(row);
					}
				}
			});
		}
		return AggRemove;
	}

	/**
	 * This method initializes AggEdit	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAggEdit() {
		if (AggEdit == null) {
			AggEdit = new JButton();
			AggEdit.setText("Edit");
			AggEdit.setToolTipText("Edit the properities of the defined aggregator");
			AggEdit.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int row = ActiveAggTable.getSelectedRow();
					if(row != -1){
						aggEditorFrame = new AggEditorFrame((jAudioFeatureExtractor.Aggregators.Aggregator)controller.activeAgg_.getAggregator(row),controller);
						aggEditorFrame.setVisible(true);
						((ActiveAggTableModel)ActiveAggTable.getModel()).setAggregator(row, aggEditorFrame.getAggregator(), aggEditorFrame.isEdited());
					}
				}
			});
		}
		return AggEdit;
	}

	/**
	 * This method initializes DoneButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDoneButton() {
		if (DoneButton == null) {
			DoneButton = new JButton();
			DoneButton.setText("Save");
			DoneButton.setToolTipText("Save and exit aggregator editing.");
			DoneButton.addActionListener(this);
		}
		return DoneButton;
	}

	/**
	 * This method initializes Abort	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAbort() {
		if (Abort == null) {
			Abort = new JButton();
			Abort.setText("Cancel");
			Abort.setToolTipText("Exit without saving.");
			Abort.addActionListener(this);
		}
		return Abort;
	}
	
	/**
	 * handles events on this window
	 */
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == DoneButton){
			if((controller.activeAgg_.getAggregator() !=null)&&(controller.activeAgg_.getAggregator().length>0)){
				controller.dm_.aggregators = controller.activeAgg_.getAggregator();
				this.setVisible(false);
			}else{
				controller.dm_.aggregators = new Aggregator[]{new Mean()};
			}
		}else if(event.getSource() == Abort){
			this.setVisible(false);
		}
	}

}
