package jAudioFeatureExtractor;

/**
 * This interface is responsible for updating a feature table when the
 * underlying data changes.
 * 
 * @author Daniel McEnnis
 */
public interface ModelListener {
	/**
	 * Method to trigger reloading the table.
	 */
	public void updateTable();
}
