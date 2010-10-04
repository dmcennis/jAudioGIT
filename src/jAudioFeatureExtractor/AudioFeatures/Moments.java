package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

/**
 * Class that calculates the first 5 stastical method of moments (Fujinaga
 * 1997). Very similar to Area Method of Moments feature, but does not have the
 * large offset - this feature starts producing results on the first window.
 * <p>
 * Fujinaga, I. <i>Adaptive Optical Music Recognition</i>. PhD thesis, McGill
 * University, 1997.
 * 
 * @author Daniel McEnnis
 */
public class Moments extends FeatureExtractor {

	/**
	 * Base constructor that defines definition, dependencies, and offsets.
	 */
	public Moments() {
		String name = "Method of Moments";
		String description = "Statistical Method of Moments of the Magnitude Spectrum.";

		definition = new FeatureDefinition(name, description, true, 5);
		dependencies = new String[] { "Magnitude Spectrum" };
		offsets = new int[] { 0 };
	}

	/**
	 * Calculates method of moments on the magnitude spectrum.
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
		double ret[] = new double[5];
		double[] mom = new double[5];
		double scale = 0;
		double tmp = 0.0;
		// first get the total area
		for (int i = 0; i < other_feature_values[0].length; ++i) {
			scale += other_feature_values[0][i];
		}

		// if the signal is complete silence, output zero
		if(scale  == 0.0){
			return new double[]{0.0,0.0,0.0,0.0,0.0};
		}
		
		// calculate each value using the mean to scale 'area' to a sum of 1
		for (int i = 0; i < other_feature_values[0].length; ++i) {
			tmp = other_feature_values[0][i] / scale;
			for (int j = 1; j < ret.length; ++j) {
				mom[j] += (tmp *= i);
			}
		}

		ret[0] = scale; // total 'area'
		ret[1] = mom[1]; // mean
		ret[2] = mom[2] - mom[1] * mom[1]; // spectral centroid
		ret[3] = 2 * Math.pow(mom[1], 3.0) - 3 * mom[1] * mom[2] + mom[3]; // skewness
		ret[4] = -3 * Math.pow(mom[1], 4.0) + 6 * mom[1] * mom[1] * mom[2] - 4
				* mom[1] * mom[3] + mom[4];// kurtosis
		return ret;
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		return new Moments();
	}

}
