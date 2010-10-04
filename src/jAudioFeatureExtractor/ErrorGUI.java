/**
 * 
 */
package jAudioFeatureExtractor;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Generic executable code snippet designed to send an error report from an
 * execution thread to the main thread.
 * 
 * @author Daniel McEnnis
 */
class ErrorGUI implements Runnable {

	private JFrame frame;

	/**
	 * This constructor creates the error snippet
	 * 
	 * @param parent
	 *            name of the progress bar window that is to be killed by this
	 *            message
	 */
	public ErrorGUI(JFrame parent) {
		frame = parent;
	}

	public Exception e;

	/**
	 * Code snippet to be executed against the swinf thread.
	 */
	public void run() {
		frame.setVisible(false);
		JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR",
				JOptionPane.ERROR_MESSAGE);
	}
}