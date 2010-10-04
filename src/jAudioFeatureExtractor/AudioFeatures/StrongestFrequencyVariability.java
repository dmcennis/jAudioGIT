/*
 * @(#)StrongestFrequencyVariability.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;


/**
 * A feature extractor that extracts the Strongest Frequency Variability
 * from window to window. This is a good measure of the amount of change
 * in fundamental frequency  that a signal goes through
 * over a moderate amount of time.
 *
 * <p>This is calculated by taking the standard deviation of the frequency
 * of the power spectrum bin with the highest power over the last 100 windows.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class StrongestFrequencyVariability
	extends FeatureExtractor
{
	/* CONSTRUCTOR **************************************************************/
	
	
	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public StrongestFrequencyVariability()
	{
		String name = "Strongest Frequency Variability";
		String description = "The standard deviation of the frequency of the" +
							 "power spectrum bin with the highest power over" + 
							 "the last 100 windows.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition( name,
		                                    description,
		                                    is_sequential,
		                                    dimensions );

		int number_windows = 100;
		
		dependencies = new String[number_windows];
		for (int i = 0; i < dependencies.length; i++)
			dependencies[i] = "Strongest Frequency Via FFT Maximum";
		
		offsets = new int[number_windows];
		for (int i = 0; i < offsets.length; i++)
			offsets[i] = 0 - i;
	}


	/* PUBLIC METHODS **********************************************************/

	
	/**
	 * Extracts this feature from the given samples at the given sampling
	 * rate and given the other feature values.
	 *
	 * <p>In the case of this feature the sampling_rate is ignored.
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
		double[] freq = other_feature_values[0];
		double std_dev = jAudioFeatureExtractor.GeneralTools.Statistics.getStandardDeviation(freq);

		double[] result = new double[1];
		result[0] = std_dev;
		return result;
	}
	
	public Object clone(){
		return new StrongestFrequencyVariability();
	}
}