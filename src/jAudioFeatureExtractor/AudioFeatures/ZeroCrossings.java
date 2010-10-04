/*
 * @(#)ZeroCrossings.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;


/**
 * A feature extractor that extracts the Zero Crossings from a set of
 * samples. This is a good measure of the pitch as well as the noisiness
 * of a signal.
 *
 * <p>Zero crossings are calculated by finding the number of times the signal
 * changes sign from one sample to another (or touches the zero axis).
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class ZeroCrossings
	extends FeatureExtractor
{
	/* CONSTRUCTOR **************************************************************/
	
	
	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public ZeroCrossings()
	{
		String name = "Zero Crossings";
		String description = "The number of times the waveform changed sign. " +
		                     "An indication of frequency as well as noisiness.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition( name,
		                                    description,
		                                    is_sequential,
		                                    dimensions );

		dependencies = null;
		
		offsets = null;
	}


	/* PUBLIC METHODS **********************************************************/

	
	/**
	 * Extracts this feature from the given samples at the given sampling
	 * rate and given the other feature values.
	 *
	 * <p>In the case of this feature, the sampling_rate and 
	 * other_feature_values parameters are ignored.
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
		long count = 0;
		for (int samp = 0; samp < samples.length - 1; samp++)
		{
			if (samples[samp] > 0.0 && samples[samp + 1] < 0.0)
				count++;
			else if (samples[samp] < 0.0 && samples[samp + 1] > 0.0)
				count++;
			else if (samples[samp] == 0.0 && samples[samp + 1] != 0.0)
				count++;
		}
		double[] result = new double[1];
		result[0] = (double) count;
		return result;
	}
	
	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone(){
		return new ZeroCrossings();
	}
}