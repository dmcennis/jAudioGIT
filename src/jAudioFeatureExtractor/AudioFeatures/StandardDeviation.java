package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

/**
 * Calculates the Standard Deviation of a feature over a large running window.
 * 
 * @author Daniel McEnnis
 *
 */
public class StandardDeviation extends MetaFeatureFactory {

	int sampleWidth = 100;

	/**
	 * Basic constructor that initializes the metafeautres values properly for
	 * use as a factory.
	 */
	public StandardDeviation() {
		super();
	}

	/**
	 * Convenience constructor to create a new factory object with the given
	 * dependant metafeature
	 * 
	 * @param mff
	 *            metafeature factory that this newly created object should
	 *            depend upon.
	 */
	public StandardDeviation(MetaFeatureFactory mff) {
		super();
		this.chainMetaFeatureFactory(mff);
	}

	/**
	 * Factory method for this class which generates a fully usable MetaFeature
	 * object. Using the structure stored in this Mean object, create a new
	 * FeatureExtractor with the given specific FeatureExtraction as a base. If
	 * we are calulating the mean of another meta-feature, recursively create
	 * the underlying meta feature first.
	 */
	public MetaFeatureFactory defineFeature(FeatureExtractor fe) {
		MetaFeatureFactory ret = new StandardDeviation();
		if ((fe_ != null) & (fe_ instanceof MetaFeatureFactory)) {
			ret.fe_ = ((MetaFeatureFactory) fe_).defineFeature(fe);
		} else {
			ret.fe_ = fe;
		}
		String name = "Standard Deviation of "
				+ ret.fe_.getFeatureDefinition().name;
		String description = "Standard Deviation of "
				+ ret.fe_.getFeatureDefinition().name + "."
				// + System.getProperty("line.separator")
				+ ret.fe_.getFeatureDefinition().description;

		String[] oldAttributes = fe.getFeatureDefinition().attributes;
		String[] myAttributes = new String[oldAttributes.length + 1];
		for (int i = 0; i < oldAttributes.length; ++i) {
			myAttributes[i] = oldAttributes[i];
		}
		myAttributes[myAttributes.length - 1] = "Size of Window to calculate accross";

		ret.definition = new FeatureDefinition(name, description, true, ret.fe_
				.getFeatureDefinition().dimensions, myAttributes);
		ret.dependencies = new String[sampleWidth];
		ret.offsets = new int[sampleWidth];
		for (int i = 0; i < sampleWidth; ++i) {
			ret.dependencies[i] = ret.fe_.getFeatureDefinition().name;
			ret.offsets[i] = 0 - i;
		}
		return ret;
	}

	/**
	 * Calculates the standard deviation over last 100 windows
	 * 
	 * @param samples
	 *            signal being processed
	 * @param sampling_rate
	 *            sample rate of the signal
	 * @param other_feature_values
	 *            dependancies of the current signal
	 * @return standard deviation over last 100 values of dependant feature.
	 */
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) throws Exception {
		double[] ret = new double[other_feature_values[0].length];
		double[] x2 = new double[other_feature_values[0].length];
		double[] x = new double[other_feature_values[0].length];
		for (int i = 0; i < other_feature_values[0].length; ++i) {
			x2[i] = 0.0;
			x[i] = 0.0;
			for (int j = 0; j < other_feature_values.length; ++j) {
				x2[i] += other_feature_values[j][i] * other_feature_values[j][i];
				x[i] += other_feature_values[j][i];
			}
		}
		for (int i = 0; i < other_feature_values[0].length; ++i) {
			ret[i] = x[i] * x[i] - x2[i];
			ret[i] /= other_feature_values.length-1;
			ret[i] = Math.sqrt(ret[i]);
		}
		return ret;
	}

	/**
	 * Function permits this class to respond to a global window change request.
	 * 
	 * @param n
	 *            new window length
	 * @throws Exception
	 *             thrown if the new window size is less than 2
	 */
	public void setWindow(int n) throws Exception {
		if (n <= 1) {
			throw new Exception("Width must be 2 or greater");
		} else {
			sampleWidth = n;
			String tmp;
			if (fe_ != null) {
				tmp = fe_.getFeatureDefinition().name;
				dependencies = new String[sampleWidth];
				offsets = new int[sampleWidth];
				for (int i = 0; i < sampleWidth; ++i) {
					dependencies[i] = tmp;
					offsets[i] = 0 - i;
				}
			} else {
				dependencies = null;
				offsets = null;
			}
		}
	}

	/**
	 * Function that must be overridden to allow this feature to be set globally
	 * by GlobalChange frame.
	 * 
	 * @param index
	 *            the number of windows of offset to be used in calculating this
	 *            feature
	 */
	public String getElement(int index) throws Exception {
		if ((index >= definition.attributes.length) || (index < 0)) {
			throw new Exception("INTERNAL ERROR: Request for an invalid index "
					+ index);
		} else if (index == definition.attributes.length - 1) {
			return Integer.toString(sampleWidth);
		} else if (fe_ != null) {
			return fe_.getElement(index);
		} else {
			throw new Exception("INTERNAL ERROR: Request for child attribute in Standrad Deviation when the child is null");
		}
	}

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to get the default values used to populate the table's entries.
	 * The correct index values are inferred from definition.attribute value.
	 * 
	 * @param index
	 *            which of AreaMoment's attributes should be edited.
	 */
	public void setElement(int index, String value) throws Exception {
		if ((index >= definition.attributes.length) || (index < 0)) {
			throw new Exception("INTERNAL ERROR: Request for an invalid index "
					+ index);
		} else if (index == definition.attributes.length - 1) {
			try {
				int type = Integer.parseInt(value);
				if (type <= 1) {
					throw new Exception(
							"width of the window must be greater than 1");
				} else {
					setWindow(type);
				}
			} catch (NumberFormatException e) {
				throw new Exception("Width of window must be an integer");
			}
		} else if (fe_ != null) {
			fe_.setElement(index, value);
		} else {
			throw new Exception("Request to set a child in StandardDeviation attrbiute when the child is null");
		}
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		if(fe_ == null){
			return new StandardDeviation();
		}else if (fe_ instanceof MetaFeatureFactory) {
			StandardDeviation ret = new StandardDeviation();
			ret.fe_ = (FeatureExtractor)fe_.clone();
			ret.definition = new FeatureDefinition(definition.name,definition.description,true,definition.dimensions,definition.attributes.clone());
			try {
				ret.setWindow(sampleWidth);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ret;
		} else {
			StandardDeviation ret = (StandardDeviation)defineFeature((FeatureExtractor)fe_.clone());
			try {
				ret.setWindow(sampleWidth);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ret;
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
		String name = definition.name;
		String description = definition.description;
		String[] attributes;
		int dimensions;
		FeatureDefinition childFD = null;
		if (fe_ != null) {
			childFD = fe_.getFeatureDefinition();
		} else {
			return definition;
		}
		attributes = new String[childFD.attributes.length + 1];
		for (int i = 0; i < childFD.attributes.length; ++i) {
			attributes[i] = childFD.attributes[i];
		}
		attributes[attributes.length - 1] = "Size of Window for Standard Deviation";
		dimensions = childFD.dimensions;
		definition = new FeatureDefinition(name, description, true, dimensions,
				attributes);
		return definition;
	}

}
