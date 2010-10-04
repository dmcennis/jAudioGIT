/*
 * @(#)FFT.java	1.0	April 5, 2005.
 *
 * Cory McKay
 * McGill Univarsity
 */

package jAudioFeatureExtractor.jAudioTools;


/**
 * This class performs a complex to complex Fast Fourier Transform. Forward and inverse
 * transforms may both be performed. The transforms may be performed with or without
 * the application of a Hanning window.
 *
 * <p>The FFT is performed by this class' constructor. The real and imaginary results
 * are both stored, and the magnitude spectrum, power spectrum and phase angles may
 * also be accessed (along with appropriate frequency bin labels for the magnitude
 * and power spectra).
 *
 * @author	Cory McKay
 */
public class FFT 
{
	/* FIELDS ******************************************************************/


	// The results of the FFT.
	private	double[]	real_output;
	private	double[]	imaginary_output;

	// The phase angles
	private double[]	output_angle;

	// Magnitude and power spectra
	private double[]	output_magnitude;
	private double[]	output_power;


	/* CONSTRUCTOR *************************************************************/


	/**
	 * Performs the Fourier transform and stores the real and imaginary results.
	 * Input signals are zero-padded if they do not have a length equal to a
	 * power of 2.
	 *
	 * @param	real_input			The real part of the signal to be transformed.
	 * @param	imaginary_input		The imaginary part of the signal to be.
	 *								transformed. This may be null if the signal
	 *								is entirely real.
	 * @param	inverse_transform	A value of false implies that a forward
	 *								transform is to be applied, and a value of
	 *								true means that an inverse transform is tob
	 *								be applied.
	 * @param	use_hanning_window	A value of true means that a Hanning window
	 *								will be applied to the real_input. A value
	 *								of valse will result in the application of
	 *								a Hanning window.
	 * @throws	Exception			Throws an exception if the real and imaginary
	 *								inputs are of different sizes or if less than
	 *								three input samples are provided.
	 */
	public FFT( double[] real_input,
	            double[] imaginary_input,
	            boolean inverse_transform,
	            boolean use_hanning_window )
		throws Exception
	{
		// Throw an exception if non-matching input signals are provided
		if (imaginary_input != null)
			if (real_input.length != imaginary_input.length)
				throw new Exception("Imaginary and real inputs are of different sizes.");
		
		// Throw an exception if less than three samples are provided
		if (real_input.length < 3)
			throw new Exception( "Only " + real_input.length + " samples provided.\n" +
			                     "At least three are needed." );

		// Verify that the input size has a number of samples that is a
		// power of 2. If not, then increase the size of the array using
		// zero-padding. Also creates a zero filled imaginary component
		// of the input if none was specified.
		int valid_size = jAudioFeatureExtractor.GeneralTools.Statistics.ensureIsPowerOfN(real_input.length, 2);
		if (valid_size != real_input.length)
		{
			double[] temp = new double[valid_size];
			for (int i = 0; i < real_input.length; i++)
				temp[i] = real_input[i];
			for (int i = real_input.length; i < valid_size; i++)
				temp[i] = 0.0;
			real_input = temp;

			if (imaginary_input == null)
			{
				imaginary_input = new double[valid_size];
				for (int i = 0; i < imaginary_input.length; i++)
					imaginary_input[i] = 0.0;
			}
			else
			{
				temp = new double[valid_size];
				for (int i = 0; i < imaginary_input.length; i++)
					temp[i] = imaginary_input[i];
				for (int i = imaginary_input.length; i < valid_size; i++)
					temp[i] = 0.0;
				imaginary_input = temp;
			}
		}
		else if (imaginary_input == null)
		{
			imaginary_input = new double[valid_size];
			for (int i = 0; i < imaginary_input.length; i++)
				imaginary_input[i] = 0.0;
		}

		// Instantiate the arrays to hold the output and copy the input
		// to them, since the algorithm used here is self-processing
		real_output = new double[valid_size];
		System.arraycopy(real_input, 0, real_output, 0, valid_size);
		imaginary_output = new double[valid_size];
		System.arraycopy(imaginary_input, 0, imaginary_output, 0, valid_size);

		// Apply a Hanning window to the real values if this option is
		// selected
		if (use_hanning_window)
		{
			for (int i = 0; i < real_output.length; i++)
			{
				double hanning = 0.5 - 0.5 * Math.cos(2 * Math.PI * i / valid_size);
				real_output[i] *= hanning;
			}
		}

		// Determine whether this is a forward or inverse transform
		int forward_transform = 1;
		if (inverse_transform)
			forward_transform = -1;

		// Reorder the input data into reverse binary order
		double scale = 1.0;
		int j = 0;
		for (int i = 0; i < valid_size; ++i)
		{
			if (j >= i) 
			{
				double tempr = real_output[j] * scale;
				double tempi = imaginary_output[j] * scale;
				real_output[j] = real_output[i] * scale;
				imaginary_output[j] = imaginary_output[i] * scale;
				real_output[i] = tempr;
				imaginary_output[i] = tempi;
			}
			int m = valid_size / 2;
			while (m >= 1 && j >= m)
			{
				j -= m;
				m /= 2;
			}
			j += m;
		}

		// Perform the spectral recombination stage by stage
		int stage = 0;
		int max_spectra_for_stage;
		int step_size;
		for( max_spectra_for_stage = 1, step_size = 2 * max_spectra_for_stage;
		     max_spectra_for_stage < valid_size;
			 max_spectra_for_stage = step_size, step_size = 2 * max_spectra_for_stage)
		{
			double delta_angle = forward_transform * Math.PI / max_spectra_for_stage;
			
			// Loop once for each individual spectra
			for (int spectra_count = 0; spectra_count < max_spectra_for_stage; ++spectra_count)
			{
				double angle = spectra_count * delta_angle;
				double real_correction = Math.cos(angle);
				double imag_correction = Math.sin(angle);

				int right = 0;
				for (int left = spectra_count; left < valid_size; left += step_size)
				{
					right = left + max_spectra_for_stage;
					double temp_real = real_correction * real_output[right] - 
					                   imag_correction * imaginary_output[right];
					double temp_imag = real_correction * imaginary_output[right] +
					                   imag_correction * real_output[right];
					real_output[right] = real_output[left] - temp_real;
					imaginary_output[right] = imaginary_output[left] - temp_imag;
					real_output[left] += temp_real;
					imaginary_output[left] += temp_imag;
				}
			}
			max_spectra_for_stage = step_size;
		}

		// Set the angle and magnitude to null originally
		output_angle = null;
		output_power = null;
		output_magnitude = null;
	}


	/* PUBLIC METHODS **********************************************************/


	/**
	 * Returns the magnitudes spectrum. It only makes sense to call
	 * this method if this object was instantiated as a forward Fourier
	 * transform.
	 *
	 * <p>Only the left side of the spectrum is returned, as the folded
	 * portion of the spectrum is redundant for the purpose of the magnitude
	 * spectrum. This means that the bins only go up to half of the
	 * sampling rate.
	 *
	 * @return	The magnitude of each frequency bin.
	 */
	public double[] getMagnitudeSpectrum()
	{
		// Only calculate the magnitudes if they have not yet been calculated
		if (output_magnitude == null)
		{
			int number_unfolded_bins = imaginary_output.length / 2;
			output_magnitude = new double[number_unfolded_bins];
			for(int i = 0; i < output_magnitude.length; i++)
				output_magnitude[i] = ( Math.sqrt(real_output[i] * real_output[i] + imaginary_output[i] * imaginary_output[i]) ) / real_output.length;
		}

		// Return the magnitudes
		return output_magnitude;
	}


	/**
	 * Returns the power spectrum. It only makes sense to call
	 * this method if this object was instantiated as a forward Fourier
	 * transform.
	 *
	 * <p>Only the left side of the spectrum is returned, as the folded
	 * portion of the spectrum is redundant for the purpose of the power
	 * spectrum. This means that the bins only go up to half of the
	 * sampling rate.
	 *
	 * @return	The magnitude of each frequency bin.
	 */
	public double[] getPowerSpectrum()
	{
		// Only calculate the powers if they have not yet been calculated
		if (output_power == null)
		{
			int number_unfolded_bins = imaginary_output.length / 2;
			output_power = new double[number_unfolded_bins];
			for(int i = 0; i < output_power.length; i++)
				output_power[i] = (real_output[i] * real_output[i] + imaginary_output[i] * imaginary_output[i]) / real_output.length;
		}

		// Return the power
		return output_power;
	}


	/**
	 * Returns the phase angle for each frequency bin. It only makes sense to 
	 * call this method if this object was instantiated as a forward Fourier
	 * transform.
	 *
	 * <p>Only the left side of the spectrum is returned, as the folded
	 * portion of the spectrum is redundant for the purpose of the phase
	 * angles. This means that the bins only go up to half of the
	 * sampling rate.
	 *
	 * @return	The phase angle for each frequency bin in degrees.
	 */
	public double[] getPhaseAngles()
	{
		// Only calculate the angles if they have not yet been calculated
		if (output_angle == null)
		{
			int number_unfolded_bins = imaginary_output.length / 2;
			output_angle = new double[number_unfolded_bins];
			for(int i = 0; i < output_angle.length; i++)
			{
				if(imaginary_output[i] == 0.0 && real_output[i] == 0.0)
					output_angle[i] = 0.0;
				else
					output_angle[i] = Math.atan(imaginary_output[i] / real_output[i]) * 180.0 / Math.PI;

				if(real_output[i] < 0.0 && imaginary_output[i] == 0.0)
					output_angle[i] = 180.0;
				else if(real_output[i] < 0.0 && imaginary_output[i] == -0.0)
					output_angle[i] = -180.0;
				else if(real_output[i] < 0.0 && imaginary_output[i] > 0.0)
					output_angle[i] += 180.0;
				else if(real_output[i] < 0.0 && imaginary_output[i] < 0.0)
					output_angle[i] += -180.0;
			}
		}
		
		// Return the phase angles
		return output_angle;
	}


	/**
	 * Returns the frequency bin labels for each bin referred to by the
	 * real values, imaginary values, magnitudes and phase angles as
	 * determined by the given sampling rate.
	 *
	 * @param	sampling_rate	The sampling rate that was used to perform
	 *							the FFT.
	 * @return					The bin labels.
	 */
	public double[] getBinLabels(double sampling_rate)
	{
		int number_bins = real_output.length;
		double bin_width = sampling_rate / (double) number_bins;
		int number_unfolded_bins = imaginary_output.length / 2;
		double[] labels = new double[number_unfolded_bins];
		labels[0] = 0.0;
		for (int bin = 1; bin < labels.length; bin++)
			labels[bin] = bin * bin_width;
		return labels;
	}

	
	/**
	 * Returns the real values as calculated by the FFT.
	 *
	 * @return	The real values.
	 */
	public double[] getRealValues()
	{
		return real_output;
	}


	/**
	 * Returns the real values as calculated by the FFT.
	 *
	 * @return	The real values.
	 */
	public double[] getImaginaryValues()
	{
		return imaginary_output;
	}
}