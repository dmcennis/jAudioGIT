/*
 * @(#)DSPMethods.java	1.0	April 5, 2005.
 *
 * Cory McKay
 * McGill Univarsity
 */

package jAudioFeatureExtractor.jAudioTools;


/**
 * A holder class for general static methods relating to processing signals
 * in the form of samples stored as arrays of doubles.
 *
 * @author	Cory McKay
 */
public class DSPMethods 
{
	/**
	 * Applies a gain to the given samples. Note that a negative gain effectively
	 * applies a phase shift of pi. There is no guarantee that the returned samples
	 * will be between -1 and +1. The returned samples are copies, so changes to
	 * them will not affect the original samples.
	 *
	 * @param	samples		Audio samles to modify, usually with a minimum value
	 *						of -1 and a maximum value of +1. The first indice 
	 *						corresponds to the channel and the second indice
	 *						corresponds to the sample number.
	 * @param	gain		The gain to apply.
	 * @return				The given audio samples after the application of
	 *						the given gain.
	 */
	public static double[][] applyGain(double[][] samples, double gain)
	{
		double[][] altered_samples = new double[samples.length][];
		for (int i = 0; i < altered_samples.length; i++)
		{
			altered_samples[i] = new double[samples[i].length];
			for (int j = 0; j < altered_samples[i].length; j++)
				altered_samples[i][j] = samples[i][j] * gain;
		}
		return altered_samples;
	}
	
	
	/**
	 * Returns the given set of samples as a set of samples mixed down into one
	 * channel.
	 *
	 * @param	audio_samples	Audio samles to modify, with a minimum value of
	 *							-1 and a maximum value of +1. The first indice 
	 *							corresponds to the channel and the second indice
	 *							corresponds to the sample number.
	 * @return					The given audio samples mixed down, with equal
	 *							gain, into one channel.
	 */
	public static double[] getSamplesMixedDownIntoOneChannel(double[][] audio_samples) 
	{
		if (audio_samples.length == 1)
			return audio_samples[0];

		double number_channels = (double) audio_samples.length;
		int number_samples = audio_samples[0].length;

		double[] samples_mixed_down = new double[number_samples];
		for (int samp = 0; samp < number_samples; samp++)
		{
			double total_so_far = 0.0;
			for (int chan = 0; chan < number_channels; chan++)
				total_so_far += audio_samples[chan][samp];
			samples_mixed_down[samp] = total_so_far / number_channels;
		}

		return samples_mixed_down;
	}


	/**
	 * Normalizes the given samples so that the absolute value of the highest 
	 * sample amplitude is 1. Does nothing if also samples are 0.
	 *
	 * @param	samples_to_normalize	The samples to normalize.
	 * @return							Returns a copy of the given samples
	 *									after normalization.
	 */
	public static double[] normalizeSamples(double[] samples_to_normalize)
	{
		double[] normalized_samples = new double[samples_to_normalize.length];
		for (int samp = 0; samp < normalized_samples.length; samp++)
			normalized_samples[samp] = samples_to_normalize[samp];

		double max_sample_value = 0.0;
		for (int samp = 0; samp < normalized_samples.length; samp++)
			if (Math.abs(normalized_samples[samp]) > max_sample_value)
				max_sample_value = Math.abs(normalized_samples[samp]);
		if (max_sample_value != 0.0)
			for (int samp = 0; samp < normalized_samples.length; samp++)
				normalized_samples[samp] /= max_sample_value;

		return normalized_samples;
	}
	

	/**
	 * Normalizes the given samples dependantly so that the absolute
	 * value of the highest sample amplitude is 1. Does nothing if all
	 * samples are 0.
	 *
	 * @param	samples_to_normalize	The samples to normalize. The first indice
	 *									denotes channel and the second denotes sample
	 *									number.
	 * @return							Returns a copy of the given samples
	 *									after normalization.
	 */
	public static double[][] normalizeSamples(double[][] samples_to_normalize)
	{
		double[][] normalized_samples = new double[samples_to_normalize.length][samples_to_normalize[0].length];
		for (int chan = 0; chan < normalized_samples.length; chan++)
			for (int samp = 0; samp < normalized_samples[chan].length; samp++)
				normalized_samples[chan][samp] = samples_to_normalize[chan][samp];

		double max_sample_value = 0.0;
		for (int chan = 0; chan < normalized_samples.length; chan++)
			for (int samp = 0; samp < normalized_samples[chan].length; samp++)
				if (Math.abs(normalized_samples[chan][samp]) > max_sample_value)
					max_sample_value = Math.abs(normalized_samples[chan][samp]);
		if (max_sample_value != 0.0)
			for (int chan = 0; chan < normalized_samples.length; chan++)
				for (int samp = 0; samp < normalized_samples[chan].length; samp++)
					normalized_samples[chan][samp] /= max_sample_value;

		return normalized_samples;
	}


	/**
	 * Returns a copy of the given array of samples.
	 *
	 * @param	original_samples	Audio samles to modify, usually with a minimum value of
	 *								value of- 1 and a maximum value of +1. The first indice 
	 *								corresponds to the channel and the second indice
	 *								corresponds to the sample number.
	 * @return						A copy of the original_samples parameter.
	 */
	public static double[][] getCopyOfSamples(double[][] original_samples)
	{
		double[][] new_samples = new double[original_samples.length][];
		for (int chan = 0; chan < new_samples.length; chan++)
		{
			new_samples[chan] = new double[original_samples[chan].length];
			for (int samp = 0; samp < new_samples[chan].length; samp++)
				new_samples[chan][samp] = original_samples[chan][samp];
		}
		return new_samples;
	}


	/**
	 * Returns the sample corresponding to the given time with the given
	 * sampling rate.
	 *
	 * @param	time			The time in seconds to convert to a sample indice.
	 * @param	sampling_rate	The sampling rate of the audio in question.
	 * @return					The corresponding sample indice.
	 */
	public static int convertTimeToSample(double time, float sampling_rate)
	{
		return (int) Math.round((time * (double) sampling_rate));
	}


	/**
	 * Returns the time corresponding to the given sample indice with the given
	 * sampling rate.
	 *
	 * @param	sample			The sample indice to convert to time.
	 * @param	sampling_rate	The sampling rate of the audio in question.
	 * @return					The corresponding time in seconds.
	 */
	public static double convertSampleToTime(int sample, float sampling_rate)
	{
		return ((double) sample) / ((double) sampling_rate);
	}


	/**
	 * Calculates the auto-correlation of the given signal. The auto-correlation
	 * is only calculated between the given lags.
	 *
	 * <p>The getAutoCorrelationLabels method can be called to find the labels
	 * in Hz for each of the returned bins.
	 *
	 * @param	signal	The digital signal to auto-correlate.
	 * @param	min_lag	The minimum lag in samples to look for in the auto-correlation.
	 * @param	max_lag	The maximum lag in samples to look for in the auto-correaltion.
	 * @return			The auto-correlation for each lag from min_lag to
	 *					max_lag. Entry 0 corresponds to min_lag, and the last
	 *					entry corresponds to max_lag.
	 */
	public static double[] getAutoCorrelation( double[] signal,
	                                           int min_lag,
		                                       int max_lag )
	{
		double[] autocorrelation = new double[max_lag - min_lag + 1];
		for (int lag = min_lag; lag <= max_lag; lag++)
		{
			int auto_indice = lag - min_lag;
			autocorrelation[auto_indice] = 0.0;
			for (int samp = 0; samp < signal.length - lag; samp++)
				autocorrelation[auto_indice] += signal[samp] * signal[samp + lag];
		}
		return autocorrelation;
	}


	/**
	 * Returns the bin labels for each bin of an auto-correlation calculation
	 * that involved the given paremeters (most likely using the 
	 * getAutoCorrelation method).
	 *
	 * @param	sampling_rate	The sampling rate that was used to encode
	 *							the signal that was auto-correlated.
	 * @param	min_lag			The minimum lag in samples that was used in the
	 *							auto-correlation.
	 * @param	max_lag			The maximum lag in samples that was used in the
	 *							auto-correlation.
	 * @return					The labels, in Hz, for the corresponding
	 *							bins produced by the getAutoCorrelation
	 *							method.
	 */
	public static double[] getAutoCorrelationLabels( double sampling_rate,
	                                                 int min_lag, 
		                                             int max_lag )
	{
		double[] labels = new double[max_lag - min_lag + 1];
		for (int i = 0; i < labels.length; i++)
			labels[i] = sampling_rate / ((double) (i + min_lag));
		return labels;
	}
}