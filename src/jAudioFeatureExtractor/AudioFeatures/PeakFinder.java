package jAudioFeatureExtractor.AudioFeatures;

import java.util.LinkedList;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

/**
 * Implements a very basic peak detection algorithm. Peaks are calculated by
 * finding local maximum in the values of the frequency bins. All maxima within
 * a threshold of the largest value is considered a peak. The thresholds of all
 * peaks are provided in order without its bin location in the original signal.
 * 
 * @author Daniel McEnnis
 */
public class PeakFinder extends FeatureExtractor {

	int peakThreshold = 10;

	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public PeakFinder() {
		String name = "Peak Detection";
		String description = "All peaks that are within an order of magnitude of the highest point";
		definition = new FeatureDefinition(name, description, true, 0,
				new String[] { "Threshold for peak detection" });
		dependencies = new String[] { "Magnitude Spectrum" };
		offsets = new int[] { 0 };
	}

	/**
	 * Extracts a set of peaks from this window.
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
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) throws Exception {
		int count = 0;
		double max = 0.0;
		double bins[] = other_feature_values[0];
		for (int i = 0; i < other_feature_values[0].length; ++i) {
			if (other_feature_values[0][i] > max) {
				max = other_feature_values[0][i];
			}
		}
		max /= peakThreshold;
		// double[] tmp = fv.getFeatureVector();
		LinkedList<Double> val = new LinkedList<Double>();
		for (int i = 1; i < bins.length - 1; ++i) {
			if ((bins[i - 1] < bins[i]) && (bins[i + 1] < bins[i])
					&& (bins[i] > max)) {
				val.add(bins[i]);
			}
		}
		Double[] ret_tmp = val.toArray(new Double[] {});
		double[] ret = new double[ret_tmp.length];
		for (int i = 0; i < ret.length; ++i) {
			ret[i] = ret_tmp[i].doubleValue();
		}
		return ret;
	}

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to get the default values used to populate the table's entries.
	 * The correct index values are inferred from definition.attribute value.
	 * <p>
	 * As a metafeature, recursively calls children for the feature requested.
	 * 
	 * @param index
	 *            which of AreaMoment's attributes should be edited.
	 */
	public String getElement(int index) throws Exception {
		if (index != 0) {
			throw new Exception("INTERNAL ERROR: PeakFinder index != 0 ("
					+ index + ")");
		} else {
			return Integer.toString(peakThreshold);
		}
	}

	/**
	 * Sets the minumum fraction of the max point that will register as a peak. The value is interpreted as 1/N of the maximum.
	 * @param peak			sets 1/N as threshold for peak detection.
	 * @throws Exception	thrown if a non-positive threshold is set.
	 */
	public void setPeakThreshold(int peak) throws Exception {
		if (peak <= 0) {
			throw new Exception(
					"PeakFinder peakThreshold must be a positive value.");
		} else {
			peakThreshold = peak;
		}
	}

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to set the default values used to popylate the table's entries.
	 * Like getElement, the correct index values are inferred from the
	 * definition.attributes value.
	 * <p>
	 * As a metafeature, recursively calls children to set the feature
	 * requested.
	 * 
	 * @param index
	 *            attribute to be set
	 * @param value
	 *            new value of the attribute
	 */
	public void setElement(int index, String value) throws Exception {
		if (index != 0) {
			throw new Exception("INTERNAL ERROR: PeakFinder index != 0 ("
					+ index + ")");
		} else {
			try {
				setPeakThreshold(Integer.parseInt(value));
			} catch (NumberFormatException e) {
				throw new Exception("Peak Threshold Must be an integer");
			}

		}
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		PeakFinder ret = new PeakFinder();
		try {
			ret.setPeakThreshold(peakThreshold);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

}
