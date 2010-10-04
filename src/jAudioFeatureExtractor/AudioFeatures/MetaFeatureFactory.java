package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.DataModel;

/**
 * This class combines 2 sets of functionality:
 * <ul>
 * <li>Factory for generating instances of a particular metafeature</li>
 * <li>Instance of this particular metafeature</li>
 * </ul>
 * This class is used in the following manner:
 * <ol>
 * <li>Create an instance of the class
 * <li>Set dependant MetaFeatureFactory class
 * <li>Cycle over features, generating 1 instance per feature
 * </ol>
 * <p>
 * <b>NOTE</b>: Subclasses of this type must correctly set the three protected
 * classes required by FeatureExtractor in the defineFeature method <b>before</b>
 * returning an instance.
 * 
 * @author Daniel McEnnis
 */
public abstract class MetaFeatureFactory extends FeatureExtractor {

	protected FeatureExtractor fe_ = null;

	// Factory Methods

	/**
	 * Factory method for setting up the construction order for
	 * 
	 * @param mff
	 *            dependant meta feature used to scale setup
	 */
	public void chainMetaFeatureFactory(MetaFeatureFactory mff) {
		fe_ = mff;
	}

	/**
	 * Factory method for generating a specific feature with a given set of
	 * hierarchical metafeatures. Will typically split into two paths: one for
	 * when applying directly to features and another for recursively acting on
	 * a dependant metafeature.
	 * 
	 * @param fe
	 *            Feature to be used as base for feature extraction
	 * @return completed metafeature.
	 */
	public abstract MetaFeatureFactory defineFeature(FeatureExtractor fe);

	/**
	 * Generic window that allows leaves of a composite to be set as well.
	 */
	public void setWindow(int n) throws Exception {
		if (fe_ != null) {
			fe_.setWindow(n);
		}
	}

	/**
	 * Gemeric code that permits setParent to apply to all children as well as
	 * the current feature.
	 */
	public void setParent(DataModel parent) {
		this.parent = parent;
		// System.out.println("FE: "+fe_);
		if (fe_ != null) {
			// System.out.println("FE: " +this.getClass());
			fe_.setParent(parent);
		}
	}
}
