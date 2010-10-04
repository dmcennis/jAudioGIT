/*
 * @(#)SpectralFlux.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;


/**
 * A feature extractor that extracts the Spectral Flux from a window of samples and
 * the preceeding window. This is a good measure of the amount of spectral chagne
 * of a signal.
 *
 * <p>Spectral flux is calculated by first calculating the difference between
 * the current value of each magnitude spectrum bin in the current window from
 * the corresponding value of the magnitude spectrum of the previous window.
 * Each of these differences is then squared, and the result is the sum of the
 * squares.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class SpectralFlux
	extends FeatureExtractor
{
	/* CONSTRUCTOR **************************************************************/
	
	
	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public SpectralFlux()
	{
		String name = "Spectral Flux";
		String description = "A measure of the amount of spectral change in a signal. "+//\n" +
		                     "Found by calculating the change in the magnitude spectrum "+//\n" +
			                 "from frame to frame.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition( name,
		                                    description,
		                                    is_sequential,
		                                    dimensions);

		dependencies = new String[2];
		dependencies[0] = "Magnitude Spectrum";
		dependencies[1] = "Magnitude Spectrum";
		
		offsets = new int[2];
		offsets[0] = 0;
		offsets[1] = -1;
	}


	/* PUBLIC METHODS **********************************************************/

	
	/**
	 * Extracts this feature from the given samples at the given sampling
	 * rate and given the other feature values.
	 *
	 * <p>In the case of this feature, the sampling_rate parameter is ignored.
	 *
	 * @param samples				The samples to extract the feature from.
	 * @param sampling_rate			The sampling rate that the samples are
	 *								encoded with.
	 * @param other_feature_values	The values of other features that are
	 *								needed to calculate this value. The
	 *								order and offsets of these features
	 *								must be the same as those returned by
	 *								this class's getDependencies and
	 *								getDependencyOffsets methods respectively.
	 *								The first indice indicates the feature/window
	 *								and the second indicates the value.
	 * @return						The extracted feature value(s).
	 * @throws Exception			Throws an informative exception if
	 *								the feature cannot be calculated.
	 */
	public double[] extractFeature( double[] samples,
	                                double sampling_rate,
	                                double[][] other_feature_values )
		throws Exception
	{
		double[] this_magnitude_spectrum = other_feature_values[0];
		double[] previous_magnitude_spectrum = other_feature_values[1];
		
		double sum = 0.0;
		for (int bin = 0; bin < this_magnitude_spectrum.length; bin++)
		{
			double difference = this_magnitude_spectrum[bin]
			                    - previous_magnitude_spectrum[bin];
			double differences_squared = difference * difference;
			sum += differences_squared;
		}

		double[] result = new double[1];
		result[0] = sum;
		return result;
	}
	
	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone(){
		return new SpectralFlux();
	}
}