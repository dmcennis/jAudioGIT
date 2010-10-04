/**
 * 
 */
package jAudioFeatureExtractor;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import jAudioFeatureExtractor.Aggregators.Aggregator;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Rectangle;
import java.awt.Dimension;
import javax.swing.table.DefaultTableModel;
import java.awt.Point;

/**
 * AggEditorFrame
 * 
 * Provides a window for setting parameters and features on an aggregator.
 *
 * @author Daniel McEnnis
 *
 */
public class AggEditorFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private Aggregator aggregator = null;  //  @jve:decl-index=0:
	
	private Controller controller = null;
	
	private boolean edited = false;
	
	private JPanel jContentPane = null;

	private JPanel Attributes = null;
	
	private JTextField[] attributes = null;

	private JPanel ButtonPanel = null;

	private JButton Save = null;

	private JPanel Description = null;

	private JButton Cancel = null;

	private JLabel DescriptionTitle = null;

	private JTextArea DescriptionText = null;

	private JPanel FeatureChooser = null;

	private JLabel AttributesLabel = null;

	private JScrollPane ChosenFeatures = null;

	private JTable ChosenFieldTable = null;

	private JPanel FeatureControls = null;

	private JScrollPane FeatureList = null;

	private JTable FeatureListTable = null;

	private JButton AddFeature = null;

	private JButton RemoveFeature = null;

	private JPanel ChosenFeaturePanel = null;

	private JPanel FeatureListPanel = null;

	/**
	 * This is the default constructor
	 */
	public AggEditorFrame(Aggregator agg, Controller c) {
		super();
		controller = c;
		aggregator = agg;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("title");//aggregator.getAggregatorDefinition().name+" Editor");
		this.setBounds(new Rectangle(0, 22, 800, 200));
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			jContentPane.setBackground(new Color(192, 218, 255));
			if(aggregator.getAggregatorDefinition().parameters != null){
				jContentPane.add(getAttributes(), null);
			}
			if(!aggregator.getAggregatorDefinition().generic){
				jContentPane.add(getFeatureChooser(), null);
			}
			jContentPane.add(getDescription(), null);
			jContentPane.add(getButtonPanel(), null);
		}
		return jContentPane;
	}
	
	/**
	 * returns the aggregator being edited by this particular editor.
	 *
	 * @returns aggregator associated with this editor.
	 */
	public Aggregator getAggregator(){
		return aggregator;
	}
	
	/**
	 * Switch which aggregator this editor is editing.
	 * 
	 * @param a new aggregator to be edited by this window.
	 */
	public void setAggregator(Aggregator a){
		aggregator = a;
	}

	/**
	 * Returns whether or not this aggregator has been edited or not.
	 * 
	 * @return is this aggregator edited
	 */
	public boolean isEdited(){
		return edited;
	}

	/**
	 * This method initializes Attributes	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAttributes() {
		if (Attributes == null) {
			AttributesLabel = new JLabel();
			AttributesLabel.setText("Aggregator Attributes");
			Attributes = new JPanel();
			Attributes.setLayout(new GridLayout(aggregator.getAggregatorDefinition().parameters.length+1,2,6,11));
			Attributes.setBackground(new Color(192, 218, 255));
			Attributes.add(AttributesLabel);
			Attributes.add(new JLabel(""));
			attributes = new JTextField[aggregator.getAggregatorDefinition().parameters.length];
			JLabel[] attributeLabel = new JLabel[aggregator.getAggregatorDefinition().parameters.length];
			for(int i=0;i<attributes.length;++i){
				String[] p = aggregator.getParamaters();
				attributes[i] = new JTextField();
				attributes[i].setText(p[i]);
				attributeLabel[i] = new JLabel();
				attributeLabel[i].setText(aggregator.getAggregatorDefinition().parameters[i]);
				Attributes.add(attributes[i]);
				Attributes.add(attributeLabel[i]);
			}
		}
		return Attributes;
	}

	/**
	 * This method initializes ButtonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (ButtonPanel == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			ButtonPanel = new JPanel();
			ButtonPanel.setLayout(gridLayout);
			ButtonPanel.add(getSave(), null);
			ButtonPanel.add(getCancel(), null);
		}
		return ButtonPanel;
	}

	/**
	 * This method initializes Save	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSave() {
		if (Save == null) {
			Save = new JButton();
			Save.setText("Save");
			Save.setBackground(new Color(192, 218, 255));
			Save.setToolTipText("Save the changes made to this aggregator and return to the previous window");
			Save.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(!aggregator.getAggregatorDefinition().generic && (aggregator.getFeaturesToApply()==null)){
						//TODO: set aggregator as not edited
						
					}else{
						//TODO:  set aggreagtors as edited
					}

					java.util.Vector<String> features = new java.util.Vector<String>();
					DefaultTableModel model = (DefaultTableModel)ChosenFieldTable.getModel();
					for(int i=0;i<model.getRowCount();++i){
						features.add((String)model.getValueAt(i, 0));
					}
					String[] parameters = new String[features.size()];
					for(int i=0;i<parameters.length;++i){
						parameters[i] = attributes[i].getText();
					}
					try {
						aggregator.setParameters(features.toArray(new String[]{}), parameters);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					shutdown();
				}
			});
		}
		return Save;
	}

	/**
	 * This method initializes Description	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDescription() {
		if (Description == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.weightx = 1.0;
			DescriptionTitle = new JLabel();
			DescriptionTitle.setText("Description");
			Description = new JPanel();
			Description.setLayout(new BorderLayout());
			Description.setBackground(new Color(192, 218, 255));
			Description.add(DescriptionTitle, BorderLayout.NORTH);
			Description.add(getDescriptionText(), BorderLayout.SOUTH);
		}
		return Description;
	}

	/**
	 * This method initializes Cancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancel() {
		if (Cancel == null) {
			Cancel = new JButton();
			Cancel.setText("Cancel");
			Cancel.setBackground(new Color(192, 218, 255));
			Cancel.setToolTipText("Return to previous window without changing the aggregator");
			Cancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					shutdown();
				}
			});
		}
		return Cancel;
	}

	/**
	 * This method initializes DescriptionText	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getDescriptionText() {
		if (DescriptionText == null) {
			DescriptionText = new JTextArea();
			DescriptionText.setBackground(new Color(192, 218, 255));
			DescriptionText.setEditable(false);
			DescriptionText.setText(aggregator.getAggregatorDefinition().description);
		}
		return DescriptionText;
	}

	/**
	 * This method initializes FeatureChooser	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFeatureChooser() {
		if (FeatureChooser == null) {
			FeatureChooser = new JPanel();
			FeatureChooser.setLayout(new BorderLayout());
			FeatureChooser.add(getFeatureControls(), BorderLayout.CENTER);
			FeatureChooser.add(getChosenFeaturePanel(), BorderLayout.WEST);
			FeatureChooser.add(getFeatureListPanel(), BorderLayout.EAST);
		}
		return FeatureChooser;
	}

	/**
	 * This method initializes ChosenFeatures	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getChosenFeatures() {
		if (ChosenFeatures == null) {
			ChosenFeatures = new JScrollPane();
			ChosenFeatures.setPreferredSize(new Dimension(354, 420));
			ChosenFeatures.setViewportView(getChosenFieldTable());
		}
		return ChosenFeatures;
	}

	/**
	 * This method initializes ChosenFieldTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getChosenFieldTable() {
		if (ChosenFieldTable == null) {
			ChosenFieldTable = new JTable();
			ChosenFieldTable.setLocation(new Point(0, 0));
			DefaultTableModel model = new AggFeatureListModel(new Object[]{"Selected Features"},0);
			String[] names = aggregator.getFeaturesToApply();
			if(names != null){
				for(int i=0;i<names.length;++i){
					model.addRow(new Object[]{names[i]});
				}
			}
			ChosenFieldTable.setModel(model);
		}
		return ChosenFieldTable;
	}

	/**
	 * This method initializes FeatureControls	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFeatureControls() {
		if (FeatureControls == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 1;
			FeatureControls = new JPanel();
			FeatureControls.setLayout(new GridBagLayout());
			FeatureControls.setBackground(new Color(192, 218, 255));
			FeatureControls.add(getAddFeature(), new GridBagConstraints());
			FeatureControls.add(getRemoveFeature(), gridBagConstraints1);
		}
		return FeatureControls;
	}

	/**
	 * This method initializes FeatureList	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getFeatureList() {
		if (FeatureList == null) {
			FeatureList = new JScrollPane();
			FeatureList.setPreferredSize(new Dimension(354, 420));
			FeatureList.setViewportView(getFeatureListTable());
		}
		return FeatureList;
	}

	/**
	 * This method initializes FeatureListTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getFeatureListTable() {
		if (FeatureListTable == null) {
			FeatureListTable = new JTable();
			DefaultTableModel model = new AggFeatureListModel(new Object[]{"Feature List"},0);
			for(int i=0;i<controller.dm_.featureDefinitions.length;++i){
				model.addRow(new Object[]{controller.dm_.featureDefinitions[i].name});
			}
			FeatureListTable.setModel(model);
		}
		return FeatureListTable;
	}

	/**
	 * This method initializes AddFeature	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddFeature() {
		if (AddFeature == null) {
			AddFeature = new JButton();
			AddFeature.setBackground(new Color(192, 218, 255));
			AddFeature.setText("Add");
			AddFeature.setToolTipText("Add a feature to the list of features analyzed by this aggregator");
			AddFeature.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int[] rows = FeatureListTable.getSelectedRows();
					for(int i=0;i<rows.length;++i){
						String name = (String)FeatureListTable.getModel().getValueAt( rows[i],0);
						((DefaultTableModel)ChosenFieldTable.getModel()).addRow(new Object[]{name});
					}
				}
			});
		}
		return AddFeature;
	}

	/**
	 * This method initializes RemoveFeature	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRemoveFeature() {
		if (RemoveFeature == null) {
			RemoveFeature = new JButton();
			RemoveFeature.setBackground(new Color(192, 218, 255));
			RemoveFeature.setToolTipText("Remove an aggregator from the list of applied features.");
			RemoveFeature.setText("Remove");
			RemoveFeature.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int[] rows = ChosenFieldTable.getSelectedRows();
					for(int i=rows.length-1;i>=0;--i){
						((DefaultTableModel)ChosenFieldTable.getModel()).removeRow(rows[i]);
					}
				}
			});
		}
		return RemoveFeature;
	}

	/**
	 * This method initializes ChosenFeaturePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getChosenFeaturePanel() {
		if (ChosenFeaturePanel == null) {
			ChosenFeaturePanel = new JPanel();
			ChosenFeaturePanel.setLayout(new GridLayout(1,1));
			ChosenFeaturePanel.add(getChosenFeatures());
		}
		return ChosenFeaturePanel;
	}

	/**
	 * This method initializes FeatureListPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFeatureListPanel() {
		if (FeatureListPanel == null) {
			FeatureListPanel = new JPanel();
			FeatureListPanel.setLayout(new GridLayout(1,1));
			FeatureListPanel.add(getFeatureList());
		}
		return FeatureListPanel;
	}

	protected void shutdown(){
		this.setVisible(false);
	}
}
