package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

/**
 * Class implementing the most basic discrete derivative of a dependant feature.
 * Extends the MetaFeatureFactory abstract class.
 * 
 * @author Daniel McEnnis
 */
public class Derivative extends MetaFeatureFactory {

	/**
	 * Basic constructor that initializes the metaFeature class variables
	 * appropiaretely for factory use
	 */
	public Derivative() {
		super();
	}

	/**
	 * A convenience consrtuctor that extends the basic constructor to allow
	 * specifying a dependant MetaFeatureFactory object.
	 * 
	 * @param mff
	 *            dependant MetaFeatureFactory object
	 */
	public Derivative(MetaFeatureFactory mff) {
		super();
		this.chainMetaFeatureFactory(mff);
	}

	/**
	 * Factory class for creating a new FeatureExtraction object. Recursively
	 * constructs the new object if there exists a dependant MetaFeatureFactory
	 * object.
	 * 
	 * @param fe
	 *            base feature that this feature is to be bvased upon.
	 * @return fully constructed MetaFeatureFactory object ready for feature
	 *         extraction.
	 */
	public MetaFeatureFactory defineFeature(FeatureExtractor fe) {
		MetaFeatureFactory tmp = new Derivative();
		if ((fe_ != null) & (fe_ instanceof MetaFeatureFactory)) {
			tmp.fe_ = ((MetaFeatureFactory) fe_).defineFeature(fe);
		} else {
			tmp.fe_ = fe;
		}
		String name = "Derivative of " + tmp.fe_.getFeatureDefinition().name;
		String description = "Derivative of " + tmp.fe_.getFeatureDefinition().name
				+ "."
				// + System.getProperty("line.separator")
				+ tmp.fe_.getFeatureDefinition().description;
		String[] oldAttributes = tmp.fe_.getFeatureDefinition().attributes;
		tmp.definition = new FeatureDefinition(name, description, true, tmp.fe_
				.getFeatureDefinition().dimensions, oldAttributes);

		tmp.dependencies = new String[] { tmp.fe_.getFeatureDefinition().name,
				tmp.fe_.getFeatureDefinition().name };
		tmp.offsets = new int[] { 0, -1 };
		return tmp;
	}

	/**
	 * Extracts the difference between adjacent points as a basic implementation
	 * of a discrete dirivative.
	 * 
	 * @param samples
	 *            signal to be analyzed. Not used by this feature
	 * @param sampling_rate
	 *            sampling rate of the signal. Not used by this feature
	 * @param other_feature_values
	 *            provides most recent and next most recent values to be
	 *            compared
	 * @return discrete derivative of the underlying feature
	 */
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) throws Exception {
		double[] ret = new double[other_feature_values[0].length];
		for (int i = 0; i < ret.length; ++i) {
			ret[i] = other_feature_values[0][i] - other_feature_values[1][i];
		}
		return ret;
	}

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to get the default values used to populate the table's entries.
	 * The correct index values are inferred from definition.attribute value.
	 * <p>
	 * As a metafeature, recursively calls children for the feature requested.
	 * 
	 * @param index
	 *            which of AreaMoment's attributes should be edited.
	 */
	public String getElement(int index) throws Exception {
		if ((index >= definition.attributes.length) || (index < 0)) {
			throw new Exception("INTERNAL ERROR: Request for an invalid index "
					+ index);
		} else {
			return fe_.getElement(index);
		}
	}

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to set the default values used to popylate the table's entries.
	 * Like getElement, the correct index values are inferred from the
	 * definition.attributes value.
	 * <p>
	 * As a metafeature, recursively calls children to set the feature
	 * requested.
	 * 
	 * @param index
	 *            attribute to be set
	 * @param value
	 *            new value of the attribute
	 */
	public void setElement(int index, String value) throws Exception {
		if ((index >= definition.attributes.length) || (index < 0)) {
			throw new Exception("INTERNAL ERROR: Request for an invalid index "
					+ index);
		} else {
			fe_.setElement(index, value);
		}
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
public Object clone() {
		if(fe_ == null){
			return new Derivative();
		}else if(fe_ instanceof MetaFeatureFactory){
			Derivative ret = new Derivative();
			ret.fe_ = (FeatureExtractor)fe_.clone();
			String name = "Derivative of " + ret.fe_.getFeatureDefinition().name;
			String description = "Derivative of " + ret.fe_.getFeatureDefinition().name
					+ "."
					// + System.getProperty("line.separator")
					+ ret.fe_.getFeatureDefinition().description;
			String[] oldAttributes = ret.fe_.getFeatureDefinition().attributes;
			ret.definition = new FeatureDefinition(name, description, true, ret.fe_
					.getFeatureDefinition().dimensions, oldAttributes);

			ret.dependencies = new String[] { ret.fe_.getFeatureDefinition().name,
					ret.fe_.getFeatureDefinition().name };
			ret.offsets = new int[] { 0, -1 };
			return ret;
		}else{
			return (new Derivative()).defineFeature((FeatureExtractor)fe_.clone());
		}
	}
	/**
	 * Overridden to regenerate the feature definition. Perhaps its should be
	 * kept purely virtual, but currently, attributes and dimensions are
	 * recalculated for each iteration. This is necessary so that changes in
	 * children's defintions get propogated back to the top level. As of
	 * 05-08-05 LPC is the only feature that requires this.
	 */
	public FeatureDefinition getFeatureDefinition() {
		if ((fe_ != null)&&(fe_ instanceof MetaFeatureFactory)) {
			String[] oldAttributes = fe_.getFeatureDefinition().attributes;
			definition = new FeatureDefinition(definition.name, definition.description, true, fe_
					.getFeatureDefinition().dimensions, oldAttributes);
		} else if (fe_ != null) {
			String[] oldAttributes = fe_.getFeatureDefinition().attributes;
			definition = new FeatureDefinition(definition.name, definition.description, true, fe_
					.getFeatureDefinition().dimensions, oldAttributes);
		} 
		return definition;
	}

}
