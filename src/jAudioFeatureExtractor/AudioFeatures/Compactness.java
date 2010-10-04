/*
 * @(#)Compactness.java	1.0	April 7, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

/**
 * A feature extractor that extracts the Compactness. This is a measure of the
 * noisiness of a signal.
 * <p>
 * This is calculated by comparing the value of a magnitude spectrum bin with
 * its surrounding values.
 * <p>
 * No extracted feature values are stored in objects of this class.
 * <p>
 * Daniel McEnnis 05-07-05 added check for degenerate case of 0 in magnitude
 * spectrum and added clone
 * 
 * @author Cory McKay
 */
public class Compactness extends FeatureExtractor {
	/* CONSTRUCTOR ************************************************************* */

	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public Compactness() {
		String name = "Compactness";
		String description = "A measure of the noisiness of a signal. "
				+ "Found by comparing the components of a window's "
				+ "magnitude spectrum with the magnitude spectrum "
				+ "of its neighbouring windows.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, description, is_sequential,
				dimensions);

		dependencies = new String[1];
		dependencies[0] = "Magnitude Spectrum";

		offsets = new int[1];
		offsets[0] = 0;
	}

	/* PUBLIC METHODS ********************************************************* */

	/**
	 * Extracts this feature from the given samples at the given sampling rate
	 * and given the other feature values.
	 * <p>
	 * In the case of this feature, the sampling_rate parameter is ignored.
	 * <p>
	 * Daniel McEnnis 05-07-05 checks for degenerate case where magnitude
	 * spectrum entry is exactly zero - skips these values
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
		double[] mag_spec = other_feature_values[0];
		double compactness = 0.0;
		for (int i = 1; i < mag_spec.length - 1; i++) {
			if ((mag_spec[i - 1] > 0.0) && (mag_spec[i] > 0.0)
					&& (mag_spec[i + 1] > 0.0)) {
				compactness += Math
						.abs(20.0
								* Math.log(mag_spec[i])
								- 20.0
								* (Math.log(mag_spec[i - 1])
										+ Math.log(mag_spec[i]) + Math
										.log(mag_spec[i + 1])) / 3.0);
			}
		}

		double[] result = new double[1];
		result[0] = compactness;
		return result;
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		return new Compactness();
	}
}