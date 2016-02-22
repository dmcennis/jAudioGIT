package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

import java.util.ResourceBundle;

/**
 * This class implements 2D statistical methods of moments as implemented by
 * Fujinaga (1997). The number of consecutive windows that one can edit across
 * is an editable property. Furthermore, this classes window property is
 * affected by global window size changes.
 * <p>
 * Fujinaga, I. <i>Adaptive Optical Music Recognition</i>. PhD thesis, McGill
 * University, 1997.
 * 
 */
public class TraditionalAreaMoments extends FeatureExtractor {

	int lengthOfWindow = 10;

	double x;

	double y;

	double x2;

	double xy;

	double y2;

	double x3;

	double x2y;

	double xy2;

	double y3;

	/**
	 * Constructor that sets description, dependencies, and offsets from
	 * FeatureExtractor
	 */
	public TraditionalAreaMoments() {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = " Traditional Area Method of Moments";
		String description = bundle.getString("2d.statistical.method.of.moments");
		String[] attributes = new String[] {bundle.getString("area.method.of.moments.window.length") };

		definition = new FeatureDefinition(name, description, true, 10,
				attributes);
		dependencies = new String[lengthOfWindow];
		for (int i = 0; i < dependencies.length; ++i) {
			dependencies[i] = "Magnitude Spectrum";
		}
		offsets = new int[lengthOfWindow];
		for (int i = 0; i < offsets.length; ++i) {
			offsets[i] = 0 - i;
		}

	}

	/**
	 * Calculates based on windows of magnitude spectrum. Encompasses portion of
	 * Moments class, but has a delay of lengthOfWindow windows before any
	 * results are calculated.
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
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) throws Exception {
		double[] ret = new double[10];
		double sum = 0.0;
		for (int i = 0; i < other_feature_values.length; ++i) {
			for (int j = 0; j < other_feature_values[i].length; ++j) {
				sum += other_feature_values[i][j];
			}
		}
		if(sum==0.0){
			java.util.Arrays.fill(ret,0.0);
			return ret;
		}
		for (int i = 0; i < other_feature_values.length; ++i) {
			for (int j = 0; j < other_feature_values[i].length; ++j) {
				double tmp = other_feature_values[i][j] / sum;
				x += tmp * i;
				y += tmp * j;
				x2 += tmp * i * i;
				xy += tmp * i * j;
				y2 += tmp * j * j;
				x3 += tmp * i * i * i;
				x2y += tmp * i * i * j;
				xy2 += tmp * i * j * j;
				y3 += tmp * j * j * j;
			}
		}
		ret[0] = sum;
		ret[1] = x;
		ret[2] = y;
		ret[3] = x2 - x * x;
		ret[4] = xy - x * y;
		ret[5] = y2 - y * y;
		ret[6] = 2 * Math.pow(x, 3.0) - 3 * x * x2 + x3;
		ret[7] = 2 * x * xy - y * x2 + x2 * y;
		ret[8] = 2 * y * xy - x * y2 + y2 * x;
		ret[9] = 2 * Math.pow(y, 3.0) - 3 * y * y2 + y3;

		return ret;
	}

	/**
	 * Function that must be overridden to allow this feature to be set globally
	 * by GlobalChange frame.
	 * 
	 * @param n
	 *            the number of windows of offset to be used in calculating this
	 *            feature
	 */
	public void setWindow(int n) throws Exception {
		if (n < 2) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(
					bundle.getString("area.method.of.moment.s.window.length.must.be.two.or.greater"));
		} else {
			lengthOfWindow = n;
			dependencies = new String[lengthOfWindow];
			offsets = new int[lengthOfWindow];
			for (int i = 0; i < lengthOfWindow; ++i) {
				dependencies[i] = "Magnitude Spectrum";
				offsets[i] = 0 - i;
			}
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
	public String getElement(int index) throws Exception {
		if (index != 0) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(String.format(bundle.getString("internal.error.invalid.index.d.sent.to.areamoments.getelement3"),index));
		} else {
			return Integer.toString(lengthOfWindow);
		}
	}

	/**
	 * Function permitting an unintelligent outside function (i.e. EditFeatures
	 * frame) to set the default values used to populate the table's entries.
	 * Like getElement, the correct index values are inferred from the
	 * definition.attributes value.
	 * 
	 * @param index
	 *            attribute to be set
	 * @param value
	 *            new value of the attribute
	 */
	public void setElement(int index, String value) throws Exception {
		if (index != 0) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(String.format(bundle.getString("internal.error.invalid.index.d.sent.to.areamoments.setelement3"),index));
		} else {
			try {
				int type = Integer.parseInt(value);
				setWindow(type);
			} catch (Exception e) {
				ResourceBundle bundle = ResourceBundle.getBundle("Translations");
				throw new Exception(
						bundle.getString("length.of.area.method.of.moments.must.be.an.integer"));
			}
		}
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		TraditionalAreaMoments ret = new TraditionalAreaMoments();
		ret.lengthOfWindow = lengthOfWindow;
		return ret;
	}

}
