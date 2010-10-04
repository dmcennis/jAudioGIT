package jAudioFeatureExtractor;

import jAudioFeatureExtractor.ACE.DataTypes.Batch;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * AddBatchGui qllows a user to supply a name for an existing batch. It should
 * be noted that batcyh names should be unique. This is enforced by the system
 * by overwriting an existing batch if one with the same name is defined.
 * 
 * @author Daniel McEnnis
 */
public class AddBatchGUI extends JFrame implements ActionListener {

	static final long serialVersionUID = 1;

	private Controller controller;

	JButton saveBatch;

	JButton cancel;

	private JLabel nameLabel;

	private JTextArea nameArea;

	private Batch batch;

	/**
	 * Create the gui interface requesting a unique batch name from the user.
	 * Also stores the reference to the global controller object and a reference
	 * to the batch to be added.
	 * 
	 * @param c
	 *            Global controller that contains all the actions and model
	 *            data.
	 * @param b
	 *            Batch to be added.
	 */
	public AddBatchGUI(Controller c, Batch b) {
		controller = c;
		batch = b;

		Color blue = new Color((float) 0.75, (float) 0.85, (float) 1.0);
		this.getContentPane().setBackground(blue);

		nameLabel = new JLabel("Batch Name");
		nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		nameArea = new JTextArea("", 1, 20);
		nameArea.setColumns(15);

		saveBatch = new JButton("Save");
		saveBatch.addActionListener(this);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);

		this.setLayout(new GridLayout(2, 2, 6, 11));
		add(nameLabel);
		add(nameArea);
		add(saveBatch);
		add(cancel);
		pack();

		setVisible(true);
	}

	/**
	 * Handles listening for pressing of the buttons. 'Save' button saves the
	 * batch, 'Cancel' button cancels batch creation.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(saveBatch)) {
			save();
		} else if (e.getSource().equals(cancel)) {
			this.setVisible(false);
		}
	}

	/**
	 * This process is divided into three separate parts:
	 * <ul>
	 * <li>Save the unique batch name provided by the user. If the name is not
	 * unique, any batch with the same name is first deleted.</li>
	 * <li>Add the batch onto the queue of batches.</li>
	 * <li>Add the batch to the remove batch menu item and the view batch menu
	 * item.
	 * </ul>
	 */
	protected void save() {
		int count = 0;
		int same = -1;
		if (nameArea.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "Batch names cannot be empty",
					"ERROR", JOptionPane.ERROR_MESSAGE);
		} else {
			batch.setName(nameArea.getText());
			for (Iterator<Batch> i = controller.batches.iterator(); i.hasNext();) {
				Batch b = i.next();
				if (b.getName().equals(nameArea.getText())) {
					same = count;
				}
				count++;
			}
			if (same != -1) {
				int state = JOptionPane
						.showConfirmDialog(
								this,
								"This will overwrite an existing batch.  Continue Anyways?",
								"Confirm Overwrite", JOptionPane.YES_NO_OPTION);
				if (state == JOptionPane.OK_OPTION) {
					System.out.println(same);
					controller.batches.remove(same);
					controller.removeBatch.remove(same);
					controller.viewBatch.remove(same);
					add(batch);
				}
			} else {
				add(batch);
			}
			setVisible(false);
		}

	}

	private void add(Batch b) {
		controller.batches.add(b);

		if (!controller.removeBatch.isEnabled()) {
			controller.removeBatch.setEnabled(true);
		}
		JMenuItem remove = new JMenuItem(b.getName());
		remove.addActionListener(controller.removeBatchAction);
		controller.removeBatch.add(remove);

		if (!controller.viewBatch.isEnabled()) {
			controller.viewBatch.setEnabled(true);
		}
		JMenuItem view = new JMenuItem(b.getName());
		view.addActionListener(controller.viewBatchAction);
		controller.viewBatch.add(view);
	}
}
