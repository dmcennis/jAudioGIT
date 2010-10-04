/*
 * @(#)Statistics.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.GeneralTools;


/**
 * A holder class for static methods relating to statistical and mathematical analysis.
 *
 * @author	Cory McKay
 */
public class Statistics 
{
	/**
	 * Returns the average of a set of doubles.
	 * Returns 0 if the length of the data is 0.
	 *
	 * @param	data the which is to be averaged.
	 * @return	the mean of the given data.
	 */
	public static double getAverage(double[] data)
	{
		if (data.length < 1)
			return 0.0;
		double sum = 0.0;
		for	(int i = 0; i < data.length; i++)
			sum = sum + data[i];
		return (sum / ((double) data.length));
    }


	/**
	 * Returns the average of a set of ints.
	 * Returns 0 if the length of the data is 0.
	 *
	 * @param	data the which is to be averaged.
	 * @return	the mean of the given data.
	 */
	public static double getAverage(int[] data)
	{
		if (data.length < 1)
			return 0.0;
		double sum = 0.0;
		for	(int i = 0; i < data.length; i++)
			sum = sum + (double) data[i];
		return (sum / ((double) data.length));
    }


	/**
	 * Returns the standard deviation of a set of doubles.
	 * Returns 0 if there is only one piece of data.
	 *
	 * @param	data for which the standard deviation is to be found.
	 * @return	the standard deviation of the given data.
	 */
	public static double getStandardDeviation(double[] data)
	{
		if (data.length  < 2)
			return 0.0;
		double average = getAverage(data);
		double sum = 0.0;
		for (int i = 0; i < data.length; i++)
		{
			double diff = data[i] - average;
			sum = sum + diff * diff;
		}
		return Math.sqrt(sum / ((double) (data.length - 1)));
	}


	/**
	 * Returns the standard deviation of a set of ints.
	 * Returns 0 if there is only one piece of data.
	 *
	 * @param	data for which the standard deviation is to be found.
	 * @return	the standard deviation of the given data.
	 */
	public static double getStandardDeviation(int[] data)
	{
		if (data.length  < 2)
			return 0.0;
		double average = getAverage(data);
		double sum = 0.0;
		for (int i = 0; i < data.length; i++)
		{
			double diff = ((double) data[i]) - average;
			sum = sum + diff * diff;
		}
		return Math.sqrt(sum / ((double) (data.length - 1)));
	}


	/**
	 * Returns whether or not x is either a factor or a multiple of y. 
	 * z denotes the possible multipliers to check for. True is returned
	 * if x is either a factor of a multiple of y (and vice versa, of
	 * course), and false otherwise.
	 */
	public static boolean isFactorOrMultiple(int x, int y, int[] z)
	{
		boolean is_factor_or_multiple = false;

		if (y > x)
		{
			for (int i = 0; i < z.length; i++)
				if ((x * z[i]) == y)
				{
					is_factor_or_multiple = true;
					i = z.length + 1; // exit loop
				}
		}
		else
		{
			for (int i = 0; i < z.length; i++)
				if ((y * z[i]) == x)
				{
					is_factor_or_multiple = true;
					i = z.length + 1; // exit loop
				}
		}
		
		return is_factor_or_multiple;
	}


	/**
	 * Returns the index of the entry of an array of doubles with the largest value.
	 * The first occurence is returned in the case of a tie.
	 */
	public static int getIndexOfLargest(double[] values)
	{
		int max_index = 0;
		for (int i = 0; i < values.length; i++)
			if (values[i] > values[max_index])
				max_index = i;
		return max_index;
	}


	/**
	 * Returns the index of the entry of an array of doubles with the smallest value.
	 * The first occurence is returned in the case of a tie.
	 */
	public static int getIndexOfSmallest(double[] values)
	{
		int min_index = 0;
		for (int i = 0; i < values.length; i++)
			if (values[i] < values[min_index])
				min_index = i;
		return min_index;
	}


	/**
	 * Returns the index of the entry of an array of floats with the largest value.
	 * The first occurence is returned in the case of a tie.
	 */
	public static int getIndexOfLargest(float[] values)
	{
		int max_index = 0;
		for (int i = 0; i < values.length; i++)
			if (values[i] > values[max_index])
				max_index = i;
		return max_index;
	}


	/**
	 * Returns the index of the entry of an array of itegers with the largest value.
	 * The first occurence is returned in the case of a tie.
	 */
	public static int getIndexOfLargest(int[] values)
	{
		int max_index = 0;
		for (int i = 0; i < values.length; i++)
			if (values[i] > values[max_index])
				max_index = i;
		return max_index;
	}


	/**
	 * Returns the Euclidian distance between x and y. Throws an exception if x an y have different
	 * sizes.
	 */
	public static double calculateEuclideanDistance(double[] x, double[] y)
		throws Exception
	{
		if (x.length != y.length)
			throw new Exception("The two given arrays have different sizes.");

		double total = 0.0;
		for (int dim = 0; dim < x.length; dim++)
			total += Math.pow( (x[dim] - y[dim]), 2 );
		return Math.sqrt(total);
	}

	
	/**
	 * Returns a random integer from 0 to max - 1, based on the uniform distribution.
	 */
	public static int generateRandomNumber(int max) 
	{
		int random_number = (int) ( ((double) Integer.MAX_VALUE) * Math.random() );
		return (random_number % max);
	}


	/**
	 * Returns an array <i>number_entries</i> arrays. Each entry has a value
	 * between 0 and <i>number_entries</i> - 1, and no numbers are repeated.
	 * Ordering of numbers is random.
	 */
	public static int[] getRandomOrdering(int number_entries)
	{
		// Generate an array of random numbers
		double[] random_values = new double[number_entries];		
		for (int i = 0; i < random_values.length; i++)
			random_values[i] = Math.random();

		// Fill in the array to return and return it
		int[] scrambled_values = new int[number_entries];
		for (int i = 0; i < scrambled_values.length; i++)
		{
			int largest_index = getIndexOfLargest(random_values);
			scrambled_values[i] = largest_index;
			random_values[largest_index] = -1.0; // to avoid double counting
		}
		return scrambled_values;
	}


	/**
	 * Returns the sum of the contents of all of the entries of the given array.
	 */
	public static double getArraySum(double[] to_sum)
	{
		double sum = 0.0;
		for (int i = 0; i < to_sum.length; i++)
			sum += to_sum[i];
		return sum;
	}


	/**
	 * Return a normalized copy of the the given array. The original array is not altered.
	 */
	public static double[] normalize(double[] to_normalize)
	{
		// Copy the to_normalize array
		double[] normalized = new double[to_normalize.length];
		for (int i = 0; i < normalized.length; i++)
			normalized[i] = to_normalize[i];
	
		// Perform the normalization
		double sum = getArraySum(normalized);
		for (int i = 0; i < normalized.length; i++)
			normalized[i] = normalized[i] / sum;

		// Return the normalized results
		return normalized;
	}

	
	/**
	 * Return a normalized copy of the the given array. Normalization is performed by
	 * row (i.e. the sum of each row (first indice) is one after normalization).
	 * Each row is independant. The original array is not altered.
	 */
	public static double[][] normalize(double[][] to_normalize)
	{
		// Copy the to_normalize array
		double[][] normalized = new double[to_normalize.length][];
		for (int i = 0; i < normalized.length; i++)
		{
			normalized[i] = new double[to_normalize[i].length];
			for (int j = 0; j < normalized[i].length; j++)
				normalized[i][j] = to_normalize[i][j];
		}
	
		// Perform the normalization
		double[] totals = new double[normalized.length];
		for (int i = 0; i < normalized.length; i++)
		{
			totals[i] = 0.0;
			for (int j = 0; j < normalized[i].length; j++)
				totals[i] += normalized[i][j];
		}
		for (int i = 0; i < normalized.length; i++)
			for (int j = 0; j < normalized[i].length; j++)
				normalized[i][j] = normalized[i][j] / totals[i];

		// Return the normalized results
		return normalized;
	}


	/**
	 * Returns the given a raised to the power of the given b.
	 *
	 * <p><b>IMPORTANT:</b> b must be greater than zero.
	 *
	 * @param	a	The base.
	 * @param	b	The exponent.
	 */
	public static int pow(int a, int b)
	{
		int result = a;
		for (int i = 1; i < b; i++)
			result *= a;
		return result;
	}
		

	/**
	 * Returns the logarithm of the specified base of the given number.
	 *
	 * <p><b>IMPORTANT:</b> Both x and n must be greater than zero.
	 *
	 * @param	x	The value to find the log of.
	 * @param	n	The base of the logarithm.
	 */
	public static double logBaseN(double x, double n)
	{
		return (Math.log10(x) / Math.log10(n));
	}



	/**
	 * If the given x is a power of the given n, then x is returned.
	 * If not, then the next value above the given x that is a power
	 * of n is returned.
	 *
	 * <p><b>IMPORTANT:</b> Both x and n must be greater than zero.
	 *
	 * @param	x	The value to ensure is a power of n.
	 * @param	n	The power to base x's validation on.
	 */
	public static int ensureIsPowerOfN(int x, int n)
	{
		double log_value = logBaseN((double) x, (double) n);
		int log_int = (int) log_value;
		int valid_size = pow(n, log_int);
		if (valid_size != x)
			valid_size = pow(n, log_int + 1);
		return valid_size;
	}
}