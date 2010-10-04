package jAudioFeatureExtractor;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

/**
 * Frame showing progress bars detailing the progress of the exeuction of batches.
 * @author mcennis
 *
 */
public class BatchProgressFrame extends JFrame {

	static final long serialVersionUID = 1;

	/**
	 * Progress wihtin a file
	 */
	public JProgressBar fileProgressBar;
	/**
	 * Progress in batch files.
	 */
	public JProgressBar overallProgressBar;
	/**
	 * Progress within a batch file.
	 */
	public JProgressBar batchProgressBar;
	
	/**
	 * Constructor that builds the frame.
	 *
	 */
	public BatchProgressFrame(){
		fileProgressBar = new JProgressBar();
		fileProgressBar.setStringPainted(true);
		overallProgressBar = new JProgressBar();
		overallProgressBar.setStringPainted(true);
		batchProgressBar = new JProgressBar();
		batchProgressBar.setStringPainted(true);
		setLayout(new GridLayout(6,1,6,11));
		JLabel tmp = new JLabel("File Progress");
		tmp.setHorizontalAlignment(SwingConstants.CENTER);
		add(tmp);
		add(fileProgressBar);
		tmp = new JLabel("Batch Progress");
		tmp.setHorizontalAlignment(SwingConstants.CENTER);
		add(tmp);
		add(batchProgressBar);
		tmp = new JLabel("Overall Progress");
		tmp.setHorizontalAlignment(SwingConstants.CENTER);
		add(tmp);
		add(overallProgressBar);
		pack();
	}
	
}
