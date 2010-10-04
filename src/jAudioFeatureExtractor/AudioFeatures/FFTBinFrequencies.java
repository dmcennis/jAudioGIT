/*
 * @(#)FFTBinFrequencies.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;


/**
 * A "feature extractor" that calculates the bin labels, in Hz, of power spectrum
 * or magnitude spectrum bins that would be produced by the FFT of a window of the
 * size of that provided to the feature extractor.
 *
 * <p>Although this is not a useful feature for the purposes of classifying,
 * it can be useful for calculating other features.
 *
 *<p>Daniel McEnnis	05-07-05	Added clone
 * @author Cory McKay
 */
public class FFTBinFrequencies
	extends FeatureExtractor
{
	/* CONSTRUCTOR **************************************************************/
	
	
	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public FFTBinFrequencies()
	{
		String name = "FFT Bin Frequency Labels";
		String description = "The bin label, in Hz, of each power spectrum or " + 
		                     "magnitude spectrum bin. Not useful as a feature in " +
			                 "itself, but useful for calculating other features " +
			                 "from the magnitude spectrum and power spectrum.";
		boolean is_sequential = true;
		int dimensions = 0;
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
		// Find the size that an FFT window would be. This is the size
		// of the given samples, or the next highes power of 2 if it
		// is not a power of 2.
		int fft_size = jAudioFeatureExtractor.GeneralTools.Statistics.ensureIsPowerOfN(samples.length, 2);

		// Find the width in Hz of each bin
		int number_bins = fft_size;
		double bin_width = sampling_rate / (double) number_bins;
		double offset = bin_width / 2.0;
		
		// Find the number of bins in the power or magnitude spectrum
		int number_unfolded_bins = fft_size / 2;
		double[] labels = new double[number_unfolded_bins];
		for (int bin = 0; bin < labels.length; bin++)
			labels[bin] = (bin * bin_width) + offset;
		
		// Return the result 
		return labels;
	}
	
	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone(){
		return new FFTBinFrequencies();
	}
}
