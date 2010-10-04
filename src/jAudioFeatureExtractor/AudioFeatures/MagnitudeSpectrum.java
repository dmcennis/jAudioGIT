/*
 * @(#)MagnitudeSpectrum.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;
import jAudioFeatureExtractor.jAudioTools.FFT;


/**
 * A feature extractor that extracts the FFT magnitude spectrum from a set of
 * samples. This is a good measure of the magnitude of different frequency
 * components within a window.
 *
 * <p>The magnitude spectrum is found by first calculating the FFT with a Hanning
 * window. The magnitude spectrum value for each bin is found by first summing
 * the squares of the real and imaginary components. The square root of this is
 * then found and the result is divided by the number of bins.
 *
 * <p>The dimensions of this feature depend on the number of FFT bins, which
 * depend on the number of input samples. The dimensions are stored in the
 * definition field are therefore 0, in order to indicate this variability.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class MagnitudeSpectrum
	extends FeatureExtractor
{
	/* CONSTRUCTOR **************************************************************/
	
	
	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public MagnitudeSpectrum()
	{
		String name = "Magnitude Spectrum";
		String description = "A measure of the strength of different frequency components.";
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
		FFT fft = new FFT(samples, null, false, true);
		return fft.getMagnitudeSpectrum();
	}
	
	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone(){
		return new MagnitudeSpectrum();
	}
}
