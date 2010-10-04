/*
 * @(#)SpectralRolloffPoint.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

/**
 * A feature extractor that extracts the Spectral Rolloff Point. This is a
 * measure measure of the amount of the right-skewedness of the power spectrum.
 * <p>
 * The spectral rolloff point is the fraction of bins in the power spectrum at
 * which 85% of the power is at lower frequencies.
 * <p>
 * No extracted feature values are stored in objects of this class.
 * 
 * @author Cory McKay
 */
public class SpectralRolloffPoint extends FeatureExtractor {

	protected double cutoff = 0.85;

	/* CONSTRUCTOR ************************************************************* */

	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public SpectralRolloffPoint() {
		String name = "Spectral Rolloff Point";
		String description = "The fraction of bins in the power spectrum at which 85% "
				+ // System.getProperty("line.separator") +
				"of the power is at lower frequencies. This is a measure " + // System.getProperty("line.separator")
				// +
				"of the right-skewedness of the power spectrum.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, description, is_sequential,
				dimensions, new String[] { "Cutoff point (0-1)" });

		dependencies = new String[1];
		dependencies[0] = "Power Spectrum";

		offsets = new int[1];
		offsets[0] = 0;
	}

	/* PUBLIC METHODS ********************************************************* */

	/**
	 * Extracts this feature from the given samples at the given sampling rate
	 * and given the other feature values.
	 * <p>
	 * In the case of this feature, the sampling_rate parameter is ignored.
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
		double[] pow_spectrum = other_feature_values[0];

		double total = 0.0;
		for (int bin = 0; bin < pow_spectrum.length; bin++)
			total += pow_spectrum[bin];
		double threshold = total * cutoff;

		total = 0.0;
		int point = 0;
		for (int bin = 0; bin < pow_spectrum.length; bin++) {
			total += pow_spectrum[bin];
			if (total >= threshold) {
				point = bin;
				bin = pow_spectrum.length;
			}
		}

		double[] result = new double[1];
		result[0] = ((double) point) / ((double) pow_spectrum.length);
		return result;
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		SpectralRolloffPoint ret = new SpectralRolloffPoint();
		ret.cutoff = cutoff;
		return ret;
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
					+ " sent to AreaMoments:getElement");
		} else {
			return Double.toString(cutoff);
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
					+ " sent to AreaMoments:setElement");
		} else {
			try {
				double type = Double.parseDouble(value);
				setCutoff(type);
			} catch (Exception e) {
				throw new Exception(
						"Length of Area Method of Moments must be an integer");
			}
		}
	}

	/**
	 * Permits users to set the rpecise cutoff point. THis value should be
	 * strictly between 0 and 1.
	 * 
	 * @param c
	 *            new cutoff point
	 * @throws Exception
	 *             thrown if c is not a real number strictly between 0 and 1.
	 */
	public void setCutoff(double c) throws Exception {
		if (Double.isInfinite(c) || Double.isNaN(c)) {
			throw new Exception("SpectralRolloff cutoff must be a real number");
		} else if ((c <= 0.0) || (c >= 1.0)) {
			throw new Exception(
					"SpectralRolloff cutoff must be gretaer than 0 and less than 1");
		} else {
			cutoff = c;
		}
	}
}