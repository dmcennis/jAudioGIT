package jAudioFeatureExtractor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import java.awt.event.*;
import javax.swing.JFrame;

/**
 * This is a window for changing all window based features simultaneously. The
 * primary use of this feature is to keep the length of windows identical in
 * time after a change in sample rate.
 * 
 * @author Daniel McEnnis
 */
public class GlobalWindowChange extends JFrame implements ActionListener {

	static final long serialVersionUID = 1;

	private DataModel fm_;

	private JButton cancel;

	private JButton save;

	private JTextArea inputBox;

	/**
	 * The constructor creates the gui that is used to initiate a global window
	 * change.
	 * 
	 * @param fm
	 */
	public GlobalWindowChange(DataModel fm) {
		fm_ = fm;
		this.setTitle("Globally change window sizes");
		Color blue = new Color((float) 0.75, (float) 0.85, (float) 1.0);
		this.getContentPane().setBackground(blue);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cancel();
			}
		});

		inputBox = new JTextArea();
		JLabel boxLabel = new JLabel("Global Window Width");
		save = new JButton("save");
		save.addActionListener(this);
		cancel = new JButton("cancel");
		cancel.addActionListener(this);

		this.setLayout(new GridLayout(2, 2, 6, 11));
		this.add(inputBox);
		this.add(boxLabel);
		this.add(save);
		this.add(cancel);

		pack();
	}

	private void cancel() {
		this.setVisible(false);
	}

	private void save() {
		try {
			int type = Integer.parseInt(inputBox.getText());
			for (int i = 0; i < fm_.features.length; ++i) {
				fm_.features[i].setWindow(type);
			}
			this.setVisible(false);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, inputBox.getText()
					+ " is not an integer", "ERROR", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * The action performed is mapped to either the save or cancel function.
	 * Utilizes the Composite pattern (Gamma et al. 1995)
	 * <p>
	 * Gamme, E., R. Helm, R. Johnson, J. Vlissides. 1995. <i>Design Patterns:
	 * Elements of Reusable Object Oriented Software</i>. Reading:
	 * Addison-Wesley Publishing Company.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(cancel)) {
			cancel();
		} else if (e.getSource().equals(save)) {
			save();
		}

	}
}
