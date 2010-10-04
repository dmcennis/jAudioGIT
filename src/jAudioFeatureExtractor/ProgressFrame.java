package jAudioFeatureExtractor;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import java.awt.*;
import javax.swing.*;

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
		fileProgressBar = new JProgressBar();
		fileProgressBar.setStringPainted(true);
		overallProgressBar = new JProgressBar();
		overallProgressBar.setStringPainted(true);
		setLayout(new GridLayout(4, 1, 6, 11));
		JLabel tmp = new JLabel("File Progress");
		tmp.setHorizontalAlignment(SwingConstants.CENTER);
		add(tmp);
		add(fileProgressBar);
		tmp = new JLabel("Overall Progress");
		tmp.setHorizontalAlignment(SwingConstants.CENTER);
		add(tmp);
		add(overallProgressBar);
		pack();
	}

}
