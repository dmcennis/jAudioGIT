/*
 * @(#)FractionOfLowEnergyWindows.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

/**
 * A feature extractor that extracts the Fraction Of Low Energy Windows from
 * window to window. This is a good measure of how much of a signal is quiet
 * relative to the rest of a signal. <pThis is calculated by taking the mean of
 * the RMS of the last 100 windows and finding what fraction of these 100
 * windows are below the mean.
 * <p>
 * No extracted feature values are stored in objects of this class.
 * <p>
 * Daniel McEnnis 05-07-05 added number_of_windows as editable property. Added
 * getElement, setElement, and clone
 * <p>
 * Daniel McEnnis 05-08-05 added setWindow to permit this feature to be edited
 * by GlobalWindow frame.
 * 
 * @author Cory McKay
 */
public class FractionOfLowEnergyWindows extends FeatureExtractor {

	private int number_windows = 100;

	/* CONSTRUCTOR ************************************************************* */

	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public FractionOfLowEnergyWindows() {
		String name = "Fraction Of Low Energy Windows";
		String description = "The fraction of the last 100 windows that has an "
				+ "RMS less than the mean RMS in the last 100 windows. "
				+ "This can indicate how much of a signal is quiet "
				+ "relative to the rest of the signal.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, description, is_sequential,
				dimensions);

		dependencies = new String[number_windows];
		for (int i = 0; i < dependencies.length; i++)
			dependencies[i] = "Root Mean Square";

		offsets = new int[number_windows];
		for (int i = 0; i < offsets.length; i++)
			offsets[i] = 0 - i;
	}

	/* PUBLIC METHODS ********************************************************* */

	/**
	 * Extracts this feature from the given samples at the given sampling rate
	 * and given the other feature values.
	 * <p>
	 * In the case of this feature the sampling_rate is ignored.
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
		double average = 0.0;
		for (int i = 0; i < other_feature_values.length; i++)
			average += other_feature_values[i][0];
		average = average / ((double) other_feature_values.length);

		int count = 0;
		for (int i = 0; i < other_feature_values.length; i++)
			if (other_feature_values[i][0] < average)
				count++;

		double[] result = new double[1];
		result[0] = ((double) count) / ((double) other_feature_values.length);

		return result;
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
			throw new Exception(
					"Fraction Of Low Energy Frames's window length must be 2 or greater");
		} else {
			number_windows = n;
			dependencies = new String[number_windows];
			offsets = new int[number_windows];
			for (int i = 0; i < number_windows; ++i) {
				dependencies[i] = "Root Mean Square";
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
			throw new Exception("INTERNAL ERROR: invalid index " + index
					+ " sent to FractionOfLowEnergyFrames:getElement");
		} else {
			return Integer.toString(number_windows);
		}
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
		if (index != 0) {
			throw new Exception("INTERNAL ERROR: invalid index " + index
					+ " sent to FractionOfLowEnergyFrames:setElement");
		} else {
			try {
				int type = Integer.parseInt(value);
				setWindow(type);
			} catch (Exception e) {
				throw new Exception(
						"Length of Fraction Of Low Energy Frames's window must be an integer");
			}
		}
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		FractionOfLowEnergyWindows ret = new FractionOfLowEnergyWindows();
		ret.number_windows = number_windows;
		return ret;
	}

}