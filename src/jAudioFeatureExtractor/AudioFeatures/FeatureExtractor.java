/*
 * @(#)FeatureExtractor.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.DataModel;
import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

/**
 * The prototype for feature extractors. Each class that extends this class will
 * extract a particular feature from a window of audio samples. Such classes do
 * not store feature values, only extract them.
 * <p>
 * Classes that extend this class should have a constructor that sets the three
 * protected fields of this class.
 * <p>
 * Daniel McEnnis 05-07-05 Added code to allow generic access to feature
 * attributes
 * <p>
 * Daniel McEnnis 05-08-05 Added setWindow and setParent features following the
 * composite pattern for new features.
 * 
 * @author Cory McKay
 */
public abstract class FeatureExtractor {
	/* FIELDS ***************************************************************** */

	/**
	 * Meta-data describing a feature.
	 */
	protected FeatureDefinition definition;

	/**
	 * The names of other features that are needed in order for a feature to be
	 * calculated. Will be null if there are no dependencies.
	 */
	protected String[] dependencies;

	/**
	 * The offset in windows of each of the features named in the dependencies
	 * field. An offset of -1, for example, means that the feature in
	 * dependencies with the same indice should be provided to this class's
	 * extractFeature method with a value that corresponds to the window prior
	 * to the window corresponding to this feature. Will be null if there are no
	 * dependencies. This must be null, 0 or a negative number. Positive numbers
	 * are not allowed.
	 */
	protected int[] offsets;

	/**
	 * If a feature alters its number of dimensions, it needs to be able to
	 * notify the holding object that a visible change has occured.
	 */
	protected DataModel parent;

	/* PUBLIC METHODS ********************************************************* */

	/**
	 * Returns meta-data describing this feature.
	 * <p>
	 * <b>IMPORTANT:</b> Note that a value of 0 in the returned dimensions of
	 * the FeatureDefinition implies that the feature dimensions are variable,
	 * and depend on the analyzed data.
	 */
	public FeatureDefinition getFeatureDefinition() {
		return definition;
	}

	/**
	 * Returns the names of other features that are needed in order to extract
	 * this feature. Will return null if no other features are needed.
	 */
	public String[] getDepenedencies() {
		return dependencies;
	}

	/**
	 * Returns the offsets of other features that are needed in order to extract
	 * this feature. Will return null if no other features are needed.
	 * <p>
	 * The offset is in windows, and the indice of the retuned array corresponds
	 * to the indice of the array returned by the getDependencies method. An
	 * offset of -1, for example, means that the feature returned by
	 * getDependencies with the same indice should be provided to this class's
	 * extractFeature method with a value that corresponds to the window prior
	 * to the window corresponding to this feature.
	 */
	public int[] getDepenedencyOffsets() {
		return offsets;
	}

	/**
	 * The prototype function that classes extending this class will override in
	 * order to extract their feature from a window of audio.
	 * 
	 * @param samples
	 *            The samples to extract the feature from.
	 * @param sampling_rate
	 *            The sampling rate that the samples are encoded with.
	 * @param other_feature_values
	 *            The values of other features that are needed to calculate this
	 *            value. The order and offsets of these features must be the
	 *            same as those returned by this class's getDependencies and
	 *            getDependencyOffsets methods respectively. The first indice
	 *            indicates the feature/window and the second indicates the
	 *            value.
	 * @return The extracted feature value(s).
	 * @throws Exception
	 *             Throws an informative exception if the feature cannot be
	 *             calculated.
	 */
	public abstract double[] extractFeature(double[] samples,
			double sampling_rate, double[][] other_feature_values)
			throws Exception;

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to get the default values used to populate the table's entries.
	 * The correct index values are inferred from definition.attribute value.
	 * 
	 * @param index
	 *            which of AreaMoment's attributes should be edited.
	 */
	public String getElement(int index) throws Exception {
		throw new Exception(
				"INTERNAL ERROR: This feature has no method defined for editing attributes.  Perhaps the author forgot to define this method.");
	}

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to set the default values used to popylate the table's entries.
	 * Like getElement, the correct index values are inferred from the
	 * definition.attributes value.
	 * 
	 * @param index
	 *            attribute to be set
	 * @param value
	 *            new value of the attribute
	 */
	public void setElement(int index, String value) throws Exception {
		throw new Exception(
				"INTERNAL ERROR: This feature has no method defined for editing attributes.  Perhaps the author forgot to define this method.");
	}

	/**
	 * Function that must be overridden to allow this feature to be set globally
	 * by GlobalChange frame.
	 * 
	 * @param windowSize
	 *            the number of windows of offset to be used in calculating this
	 *            feature
	 */
	public void setWindow(int windowSize) throws Exception {

	}

	/**
	 * Gives features a reference to the container frame to notify it that
	 * features have changed state and need to be redrawn.
	 * 
	 * @param parent
	 *            container frame which holds the model for displaying features
	 *            in the feature display panel.
	 */
	public void setParent(DataModel parent) {
		this.parent = parent;
		// System.out.println(this.getClass());
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public abstract Object clone();

}