/*
 * @(#)StrengthOfStrongestBeat.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;


/**
 * A feature extractor that extracts the Strength of Strongest Beat from
 * a signal. This is a measure of how strong the strongest beat is compared
 * to other possible beats.
 *
 * <p>This is calculated by finding the entry in the beat histogram corresponding
 * to the strongest beat and dividing it by the sum of all entries in the
 * beat histogram.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class StrengthOfStrongestBeat
	extends FeatureExtractor
{
	/* CONSTRUCTOR **************************************************************/
	
	
	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public StrengthOfStrongestBeat()
	{
		String name = "Strength Of Strongest Beat";
		String description = "How strong the strongest beat in the beat histogram " +
						     "is compared to other potential beats.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition( name,
		                                    description,
		                                    is_sequential,
		                                    dimensions );

		dependencies = new String[2];
		dependencies[0] = "Beat Histogram";
		dependencies[1] = "Beat Sum";
		
		offsets = new int[2];
		offsets[0] = 0;
		offsets[1] = 0;
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
		double[] beat_histogram = other_feature_values[0];

		if (beat_histogram != null)
		{
			double beat_sum = other_feature_values[1][0];
			int highest_bin = jAudioFeatureExtractor.GeneralTools.Statistics.getIndexOfLargest(beat_histogram);
			double highest_strength = beat_histogram[highest_bin];
			double normalized_strength = highest_strength / beat_sum;

			double[] result = new double[1];
			result[0] = normalized_strength;
			return result;
		}
		else
			return null;
	}
	
	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone(){
		return new StrengthOfStrongestBeat();
	}
}