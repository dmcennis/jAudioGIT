package jAudioFeatureExtractor;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

/**
 * This class provides a set of progress bars for the normal (non-batch) feature
 * extrcation
 * 
 * @author Daniel McEnnis
 */
public class ProgressFrame extends JFrame {

	static final long serialVersionUID = 1;

	/**
	 * Progress within this file
	 */
	public JProgressBar fileProgressBar;

	/**
	 * Overall progress (in files)
	 */
	public JProgressBar overallProgressBar;

	/**
	 * Creates the progress window but does not show it.
	 *
	 */
	public ProgressFrame() {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		fileProgressBar = new JProgressBar();
		fileProgressBar.setStringPainted(true);
		overallProgressBar = new JProgressBar();
		overallProgressBar.setStringPainted(true);
		setLayout(new GridLayout(4, 1, 6, 11));
		JLabel tmp = new JLabel(bundle.getString("file.progress"));
		tmp.setHorizontalAlignment(SwingConstants.CENTER);
		add(tmp);
		add(fileProgressBar);
		tmp = new JLabel(bundle.getString("overall.progress"));
		tmp.setHorizontalAlignment(SwingConstants.CENTER);
		add(tmp);
		add(overallProgressBar);
		pack();
	}

}
