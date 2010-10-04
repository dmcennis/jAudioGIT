package jAudioFeatureExtractor;

import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dimension;

import javax.swing.*;

import javax.swing.JFrame;

/**
 * Window for allowing a user to edit the individual features in the table. The
 * window appears when a feature is double clicked in the FeatureSelectorPanel
 * table of features.
 * <p>
 * This window has three sections. The first is a sequence of textAreas and
 * labels dynamically matching the number of editable features present in the
 * feature that caused the window to appear. The second secction immediately
 * below the first contains the description of the feature. (NOTE features
 * should not include newline characters in their descriptions as words are
 * wrapped in the description box as needed.) The third section contains 2
 * buttons - save and cancel. The Save button attempts to save all the features
 * and then exits only if successful. If unsuccesful, an error box with an
 * explanation is presented to the user. The cancel box closes the window
 * without saving changes and has the same effect as closing the window
 * directly.
 * <p>
 * Note that this window is recreated every time a feature is double-clicked.
 * This is a workaround since I have not been able to figure out how to
 * dynamically change the contents of the frame when a new feature is double
 * clicked. This also has the desirable property of allowing multiple instances
 * of this window to coexist at the same time.
 * 
 * @author mcennis
 */
public class EditFeatures extends JFrame implements ActionListener {

	static final long serialVersionUID=1;
	
	private FeatureSelectorPanel parent_;

	private JTextArea[] inputBoxes;

	private JLabel[] inputBoxLabels;

	private JLabel descriptionTitle;

	private JTextArea infoLabel;

	private JButton save;

	private JButton cancel;

	private int row;

	private FeatureExtractor fe_;

	/**
	 * Create a new instance of this panel for editing a variable.
	 * 
	 * @param parent
	 *            parent window which created this window
	 * @param fe
	 *            The feature which is being edited by this window
	 */
	EditFeatures(FeatureSelectorPanel parent, FeatureExtractor fe) {
		setTitle("Edit " + fe.getFeatureDefinition().name);
		Color blue = new Color((float)0.75,(float)0.85,(float)1.0);
		this.getContentPane().setBackground(blue);
		this.setAlwaysOnTop(true);
		String[] attributes = fe.getFeatureDefinition().attributes;
		fe_ = fe;
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cancel();
			}
		});

		inputBoxes = new JTextArea[attributes.length];
		inputBoxLabels = new JLabel[attributes.length];
		for (int i = 0; i < inputBoxes.length; ++i) {
			inputBoxes[i] = new JTextArea();
			try {
				inputBoxes[i].setText(fe_.getElement(i));
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
			inputBoxLabels[i] = new JLabel(attributes[i]);
		}

		descriptionTitle = new JLabel("Description");

		infoLabel = new JTextArea(fe.getFeatureDefinition().description);
		infoLabel.setWrapStyleWord(true);
		infoLabel.setLineWrap(true);
		infoLabel.setEditable(false);
		infoLabel.setBackground(this.getContentPane().getBackground());
		Dimension base = new Dimension(400, 100);
		infoLabel.setPreferredSize(base);

		save = new JButton("Save");
		save.addActionListener(this);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);

		JPanel editingPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JPanel centerPanel = new JPanel();

		editingPanel.setBackground(blue);
		editingPanel.setLayout(new GridLayout(inputBoxLabels.length,2,6,11));
		for(int i=0;i<inputBoxLabels.length;++i){
			editingPanel.add(inputBoxes[i]);
			editingPanel.add(inputBoxLabels[i]);
		}
		this.setLayout(new BorderLayout());
		this.add(editingPanel,BorderLayout.NORTH);
	
		buttonPanel.setBackground(blue);
		buttonPanel.setLayout(new GridLayout(1,2,6,11));
		buttonPanel.add(save);
		buttonPanel.add(cancel);
		this.add(buttonPanel,BorderLayout.SOUTH);
		
		centerPanel.setBackground(blue);
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(descriptionTitle,BorderLayout.NORTH);
		centerPanel.add(infoLabel,BorderLayout.CENTER);
		this.add(centerPanel,BorderLayout.CENTER);
		
		pack();
		setVisible(true);
	}

	/**
	 * Calls the appropriate method function when buttons are pressed
	 * 
	 * @param e
	 *            event to be acted upon
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(cancel)) {
			cancel();
		} else if (e.getSource().equals(save)) {
			save();
		}

	}

	/**
	 * Called either when either the cancel button is pressed or when the window
	 * is eplicitly closed by a kill box.
	 */
	private void cancel() {
		setVisible(false);
	}

	/**
	 * Called when the save button is pressed. Saves all the features and exits
	 * if the saving is successful. NOTE: it is possible to only save a portion
	 * of the features if an error appears in the process of saving features.
	 */
	private void save() {
		boolean good = true;
		for (int i = 0; i < inputBoxes.length; ++i) {
			try {
				fe_.setElement(i, inputBoxes[i].getText());
			} catch (Exception e) {
				good = false;
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		if (good) {
			setVisible(false);
		}
	}

	/**
	 * Set which feature is to be edited
	 * 
	 * @param i
	 *            row of feature to be extracted
	 */
	void setRow(int i) {
		row = i;
	}
}
