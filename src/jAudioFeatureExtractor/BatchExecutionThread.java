package jAudioFeatureExtractor;

import jAudioFeatureExtractor.ACE.DataTypes.Batch;
import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.swing.SwingUtilities;

/**
 * GUI components that handles the execution of the feature extraction. The
 * unusual structure is to get around the limitations of Swing - since swing is
 * not thread safe, gui componenets can only operate from the main thread
 * without race conditions. This means that either long running activities must
 * execute on the main thread (making the system unresponsive) or fire updates
 * on the main thread, potentially causing race conditions. This problem can be
 * circumvented by pumping updates to the main event thread via small runnable
 * objects (Geary 1999, 281-91). It works, but makes for obscure code with
 * several inner or anonymous classes that encapsulate a message to the gui
 * thread.
 * <p>
 * Geary, D. 1999. <i>Graphic Java 2: Mastering the JFC</i>. Palo Alto: Sun
 * Microsystems.
 * 
 * @author Daniel McEnnis
 */
public class BatchExecutionThread extends Thread implements Updater {

	Runnable suspendGUI;

	Runnable resumeGUI;

	RestoreSettings restoreSettings;

	BatchProgressFrame batchProgressFrame;

	UpdateGUI updateGui;

	ErrorGUI errorGUI;

	UpdateGUI updateGUI;

	OuterFrame outerFrame;

	Controller controller;

	FeatureExtractor[] features;

	boolean[] defaults;

	Vector<Batch> batches;

	boolean hasRun = false;

	/**
	 * Creates a new thread. Prepares the thread for execution, creating each of
	 * the runnable objects that can be later inserted into the swing event
	 * queue.
	 * 
	 * @param controller
	 *            Near global object containing actions and model components.
	 * @param of
	 *            Reference to the main frame so it can be disabled during
	 *            execution - preventing modification to the system in mid run.
	 */
	public BatchExecutionThread(Controller controller, OuterFrame of) {
		outerFrame = of;
		this.controller = controller;
		batchProgressFrame = new BatchProgressFrame();
		batchProgressFrame.setVisible(true);
		errorGUI = new ErrorGUI(batchProgressFrame);
		updateGUI = new UpdateGUI();
		suspendGUI = new Runnable() {
			public void run() {
				outerFrame.setEnabled(false);
			}
		};

		resumeGUI = new Runnable() {
			public void run() {
				outerFrame.setEnabled(true);
			}
		};

		restoreSettings = new RestoreSettings();

		updateGUI = new UpdateGUI();
		controller.dm_.setUpdater(this);
		restoreSettings.fe = controller.dm_.features.clone();
		restoreSettings.d = controller.dm_.defaults.clone();
		restoreSettings.c = controller;
	}

	/**
	 * Executes a batch. Executes in its own thread, communicating with the
	 * swing thread via runnable objects that are inserted into the swing
	 * thread. Acts as a controller in a classic MVC pattern (with the batches
	 * as model and the swing thread as view).
	 */
	public void run() {
		ModelListener modelListener = controller.dm_.ml_;
		controller.dm_.ml_ = null;
		try {
			SwingUtilities.invokeAndWait(suspendGUI);
			for (int i = 0; i < controller.batches.size(); ++i) {
//				controller.dm_.validateFile(controller.batches.get(i)
//						.getDestinationFK(), controller.batches.get(i)
//						.getDestinationFV());
				File feature_values_save_file = new File(controller.batches
						.get(i).getDestinationFV());
				File feature_definitions_save_file = new File(
						controller.batches.get(i).getDestinationFK());

				// Prepare stream writers
				controller.dm_.featureKey = new FileOutputStream(
						feature_values_save_file);
				controller.dm_.featureValue = new FileOutputStream(
						feature_definitions_save_file);
				controller.batches.get(i).execute();
				updateGUI.incrementBatch();
				SwingUtilities.invokeLater(updateGUI);
			}
			SwingUtilities.invokeLater(resumeGUI);
		} catch (Exception e) {
			errorGUI.e = e;
			SwingUtilities.invokeLater(errorGUI);
			SwingUtilities.invokeLater(resumeGUI);
		}
		hasRun = true;
		batchProgressFrame.setVisible(false);
		controller.dm_.ml_ = modelListener;
		try {
			SwingUtilities.invokeAndWait(restoreSettings);
			sleep(1000);
		} catch (InterruptedException e) {
			System.err.println("INTERNAL ERROR: " + e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.err.println("INTERNAL ERROR: " + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Called after each file executes
	 */
	public void announceUpdate(int fileNumber, int fileDone) {
		updateGUI.setPos(fileNumber, fileDone);
		SwingUtilities.invokeLater(updateGUI);
	}

	/**
	 * Called at periodic intervals during the execution of the code.
	 */
	public void announceUpdate(int fileDone) {
		updateGUI.setPos(fileDone);
		SwingUtilities.invokeLater(updateGUI);

	}

	/**
	 * Informs the batch progress bar about many files are present in this
	 * batch.
	 */
	public void setNumberOfFiles(int files) {
		updateGUI.setLengths(files);
		SwingUtilities.invokeLater(updateGUI);
	}

	/**
	 * Informs the file progress bar on how many windows of data are present in
	 * this file.
	 */
	public void setFileLength(int windows) {
		updateGUI.setMaxWindows(windows);
		SwingUtilities.invokeLater(updateGUI);
	}

	class UpdateGUI implements Runnable {
		int numberOfFiles;

		int file;

		int thisFileLength = 0;

		int pos;

		int batch = 0;

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

		public void incrementBatch() {
			this.batch++;
		}

		public void run() {
			batchProgressFrame.setVisible(true);
			batchProgressFrame.fileProgressBar.setMaximum(thisFileLength);
			batchProgressFrame.batchProgressBar.setMaximum(numberOfFiles);
			batchProgressFrame.overallProgressBar.setMaximum(controller.batches
					.size());
			batchProgressFrame.fileProgressBar.setValue(pos);
			batchProgressFrame.batchProgressBar.setValue(file);
			batchProgressFrame.overallProgressBar.setValue(batch);
		}
	}

	class RestoreSettings implements Runnable {
		public Controller c;

		public FeatureExtractor[] fe;

		public boolean[] d;

		public void run() {
			for (int i = 0; i < fe.length; ++i) {
				int numberOfAttributes = fe[i].getFeatureDefinition().attributes.length;
				for (int j = 0; j < numberOfAttributes; ++j) {
					try {
						c.dm_.features[i].setElement(j, fe[i].getElement(j));
					} catch (Exception e) {
						System.err.println("INTERNAL ERROR: " + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}
}
