package jAudioFeatureExtractor;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * This is a thread for executing the DataModel.extractFeatures without tying up
 * the swing dispatch thread.
 * 
 * @author Daniel McEnnis
 */
public class ExtractionThread extends Thread implements Updater {

	Runnable suspendGUI, resumeGUI;

	ErrorGUI errorGUI;

	UpdateGUI updateGUI;

	Controller controller;

	OuterFrame outerFrame;

	boolean perFile;

	boolean perWindow;

	String valuesSavePath;

	String definitionSavePath;

	int windowSize;

	double windowOverlap;

	boolean hasRun = false;

	ProgressFrame progressFrame;

	/**
	 * This constructor constructs the thread, partially preparing it for
	 * execution
	 * 
	 * @param c
	 *            Near global container for numerous controller and model
	 *            objects
	 * @param of
	 *            Link to outerframe of the gui. Used to disable the main frame
	 *            to prevent race conditions in the feature settings.
	 */
	public ExtractionThread(Controller c, OuterFrame of) {

		controller = c;
		outerFrame = of;
		suspendGUI = new Runnable() {
			public void run() {
				outerFrame.setEnabled(false);
			}
		};

		resumeGUI = new Runnable() {
			public void run() {
				outerFrame.setEnabled(true);
				progressFrame.setVisible(false);
				JOptionPane.showMessageDialog(null,
						"Features successfully extracted and saved.", "DONE",
						JOptionPane.INFORMATION_MESSAGE);
			}
		};

		updateGUI = new UpdateGUI();

		c.dm_.setUpdater(this);
		progressFrame = new ProgressFrame();
		errorGUI = new ErrorGUI(progressFrame);
	}

	/**
	 * This is the method to finish preparing the thread for execution
	 * 
	 * @param perFile
	 *            Should features be extracted over the entire file
	 * @param perWindow
	 *            Should features be extracted on a window by window basis
	 * @param valuesSavePath
	 *            File to save extracted features
	 * @param definitionSavePath
	 *            File to save descriptions of the features extracted
	 * @param windowSize
	 *            Size of the analysis window in samples
	 * @param windowOverlap
	 *            Percent of the window that is duplicated between analysis
	 *            windows
	 */
	public void setup(boolean perFile, boolean perWindow,
			String valuesSavePath, String definitionSavePath, int windowSize,
			double windowOverlap) {
		this.perFile = perFile;
		this.perWindow = perWindow;
		this.valuesSavePath = valuesSavePath;
		this.definitionSavePath = definitionSavePath;
		this.windowSize = windowSize;
		this.windowOverlap = windowOverlap;
	}

	/**
	 * Execute the thread, suspending the main frame, extracting the features,
	 * then enabling the main frame.
	 */
	public void run() {
		try {
			SwingUtilities.invokeAndWait(suspendGUI);
			controller.dm_.validateFile(definitionSavePath,valuesSavePath);
			File feature_values_save_file = new File(valuesSavePath);
			File feature_definitions_save_file = new File(definitionSavePath);

			// Prepare stream writers
			FileOutputStream values_to = new FileOutputStream(
					feature_values_save_file);
			FileOutputStream definitions_to = new FileOutputStream(
					feature_definitions_save_file);

			controller.dm_.featureKey = definitions_to;
			controller.dm_.featureValue = values_to;
			controller.dm_.extract(windowSize, windowOverlap,
					controller.samplingRateAction.getSamplingRate(),
					controller.normalise.isSelected(), perWindow, perFile,
					controller.dm_.recordingInfo, controller.outputTypeAction
							.getSelected());
			SwingUtilities.invokeLater(resumeGUI);
		} catch (Exception e) {
			e.printStackTrace();
			errorGUI.e = e;
			SwingUtilities.invokeLater(errorGUI);
			SwingUtilities.invokeLater(resumeGUI);
		}
		hasRun = true;
	}

	class UpdateGUI implements Runnable {
		int numberOfFiles;

		int file;

		int thisFileLength = 0;

		int pos;

		public void setLengths(int file) {
			numberOfFiles = file;
		}

		public void setMaxWindows(int maxWin) {
			thisFileLength = maxWin;
		}

		public void setPos(int file, int pos) {
			this.file = file;
			this.pos = pos;
		}

		public void setPos(int pos) {
			this.pos = pos;
		}

		public void run() {
			progressFrame.setVisible(true);
			progressFrame.fileProgressBar.setMaximum(thisFileLength);
			progressFrame.overallProgressBar.setMaximum(numberOfFiles);
			progressFrame.fileProgressBar.setValue(pos);
			progressFrame.overallProgressBar.setValue(file);
		}
	}

	/**
	 * This is part of the Updater interface. It notifies the gui that a file
	 * has been completed.
	 */
	public void announceUpdate(int fileNumber, int fileDone) {
		updateGUI.setPos(fileNumber, fileDone);
		SwingUtilities.invokeLater(updateGUI);
	}

	/**
	 * This is part of the Updater interface. It notifies the gui of an increase
	 * in the amount of the file processed.
	 */
	public void announceUpdate(int fileDone) {
		updateGUI.setPos(fileDone);
		SwingUtilities.invokeLater(updateGUI);
	}

	/**
	 * This is part of the Updater interface. It is used to set the total number
	 * of files to be processed.
	 */
	public void setNumberOfFiles(int files) {
		updateGUI.setLengths(files);
	}

	/**
	 * This is part of the Updater interface. It is used to notify the gui of
	 * the total size of the file (in windows of data).
	 */
	public void setFileLength(int window) {
		updateGUI.setMaxWindows(window);
	}

	/**
	 * Used to prevent this thread from executing twice.
	 * 
	 * @return whether or not this thread has run before
	 */
	public boolean hasRun() {
		return hasRun();
	}
}
