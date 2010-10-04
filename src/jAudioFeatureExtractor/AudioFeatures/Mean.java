package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

/**
 * Calculates the running mean of an underlying feature.
 * 
 * @author Daniel McEnnis
 */
public class Mean extends MetaFeatureFactory {

	protected int runningAverage = 100;

	/**
	 * Basic constructor that initializes the metafeautres values properly for
	 * use as a factory.
	 */
	public Mean() {
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
	public Mean(MetaFeatureFactory mff) {
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
		MetaFeatureFactory tmp = new Mean();
		if ((fe_ != null) & (fe_ instanceof MetaFeatureFactory)) {
			tmp.fe_ = ((MetaFeatureFactory) fe_).defineFeature(fe);
		} else {
			tmp.fe_ = fe;
		}
			tmp.fe_ = fe;
			String name = "Running Mean of " + fe.getFeatureDefinition().name;
			String description = "Running Mean of "
					+ fe.getFeatureDefinition().name + ". "
//					+ System.getProperty("line.separator")
					+ fe.getFeatureDefinition().description;

			String[] oldAttributes = fe.getFeatureDefinition().attributes;
			String[] myAttributes = new String[oldAttributes.length + 1];
			for (int i = 0; i < oldAttributes.length; ++i) {
				myAttributes[i] = oldAttributes[i];
			}
			myAttributes[myAttributes.length - 1] = "Size of Window to Average accross";

			tmp.definition = new FeatureDefinition(name, description, true, fe
					.getFeatureDefinition().dimensions, myAttributes);
			tmp.dependencies = new String[runningAverage];
			tmp.offsets = new int[runningAverage];
			for (int i = 0; i < runningAverage; ++i) {
				tmp.dependencies[i] = fe.getFeatureDefinition().name;
				tmp.offsets[i] = 0 - i;
			}
			return tmp;
	}

	/**
	 * Calculates the mean over last 100 windows
	 * 
	 * @param samples
	 *            signal being processed
	 * @param sampling_rate
	 *            sample rate of the signal
	 * @param other_feature_values
	 *            dependancies of the current signal
	 * @return mean over last 100 values of dependant feature.
	 */
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) throws Exception {
		double[] ret = new double[other_feature_values[0].length];
		for (int i = 0; i < ret.length; ++i) {
			for (int j = 0; j < other_feature_values.length; ++j) {
				ret[i] += other_feature_values[j][i];
			}
			ret[i] /= other_feature_values.length;
		}
		return ret;
	}

	/**
	 * Changes the number of dependant samples extracted for each object.
	 * 
	 * @param n
	 *            number of samples that should be included in the running
	 *            average.
	 * @throws Exception
	 *             thrown if n is equal to or less than one
	 */
	public void setWindow(int n) throws Exception {
		if (n <= 1) {
			throw new Exception(
					"new value for running average must be greater than one");
		} else {
			runningAverage = n;
			if (fe_ != null) {
				String tmp = fe_.getFeatureDefinition().name;
				dependencies = new String[runningAverage];
				offsets = new int[runningAverage];
				for (int i = 0; i < runningAverage; ++i) {
					dependencies[i] = tmp;
					offsets[i] = 0 - i;
				}
			} else {
				dependencies = null;
				offsets = null;
			}

		}
		super.setWindow(n);
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
		} else if (index == definition.attributes.length - 1) {
			return Integer.toString(runningAverage);
		} else if (fe_ != null) {
			return fe_.getElement(index);
		} else {
			throw new Exception("INTERNAL ERROR: non-existant index for Mean:getElement - claims to have children, but child is null");
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
			throw new Exception("INTERNAL ERROR: non-existant index for Mean:getElement - claims to have children, but child is null");
		}
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		if(fe_ == null){
			return new Mean();
		}
		if (this.fe_ instanceof MetaFeatureFactory) {
			Mean ret = new Mean();
			ret.fe_ = (FeatureExtractor)fe_.clone();
			try {
				ret.setWindow(runningAverage);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String name = definition.name;
			String description = definition.description;
			String[] attributes = definition.attributes;
			int dim = definition.dimensions;
			ret.definition = new FeatureDefinition(name,description,true,dim,attributes);
			ret.dependencies = this.dependencies.clone();
			ret.offsets = this.offsets.clone();
			try{
				ret.setWindow(runningAverage);
			}catch(Exception e){
				e.printStackTrace();
			}
			return ret;
		} else {
			Mean ret = (Mean)defineFeature((FeatureExtractor) fe_.clone());
			try {
				ret.setWindow(runningAverage);
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
		FeatureDefinition childFD=null;
		if(fe_ != null){
			childFD = fe_.getFeatureDefinition();
		}else{
			return definition;
		}
		attributes = new String[childFD.attributes.length + 1];
		for(int i=0;i<childFD.attributes.length;++i){
			attributes[i] = childFD.attributes[i];
		}
		attributes[attributes.length-1] = "Size of Window to Average accross";
		dimensions = childFD.dimensions;
		definition = new FeatureDefinition(name,description,true,dimensions,attributes);
		return definition;
	}

}
