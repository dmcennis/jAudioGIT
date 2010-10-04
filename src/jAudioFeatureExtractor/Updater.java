package jAudioFeatureExtractor;

/**
 * Interface used by the underlying system to provide updates on system progress
 * without locking the execution into a particular type of view (gui or
 * console).
 * 
 * @author Daniel McEnnis
 */
public interface Updater {

	/**
	 * Called when a file is completed.
	 * 
	 * @param fileNumber
	 *            How many files are completed.
	 * @param fileDone
	 *            Position in the file (usually 0).
	 */
	public void announceUpdate(int fileNumber, int fileDone);

	/**
	 * Called periodically when processing a file
	 * 
	 * @param fileDone
	 *            Position in file.
	 */
	public void announceUpdate(int fileDone);

	/**
	 * Establishes how many files are in this execution
	 * 
	 * @param files
	 *            Number of files to be processed.
	 */
	public void setNumberOfFiles(int files);

	/**
	 * Establishes how big the current file is.
	 * 
	 * @param windows
	 *            Total number of windows of data to be processed.
	 */
	public void setFileLength(int windows);
}
