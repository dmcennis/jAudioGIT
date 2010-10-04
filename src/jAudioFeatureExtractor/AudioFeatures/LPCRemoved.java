package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

/**
 * Linear Prediction Coeffecients calculated according to 'Numerical Recipes in C' (Press at al. 1992)
 * <p>Press, W., and S. Teukolsky, and W. Vetterling, and B. Flannery. 1992. <i>Numerical Recipes in C</i>. Cambridge: Cambridge University Press.
 * 
 * @author Daniel McEnnis
 *
 */
public class LPCRemoved extends FeatureExtractor {

	private int num_dimensions = 10;

	public LPCRemoved() {
		String name = "LPC";
		String description = "Linear Predictive Encoding implemented from 'Numerical Recipes in C'";

		definition = new FeatureDefinition(name, description, true,
				num_dimensions,
				new String[] { "Number of LPC Coeffecients to Calculate" });
		dependencies = null;
		offsets = null;
	}

	/**
	 * Blatantly stolen from Numerical Recipes (Press et al. 1992).
	 * 
	 * @param samples
	 *            The samples to extract the feature from.
	 * @param sampling_rate
	 *            The sampling rate that the samples are encoded with.
	 * @param other_feature_values
	 *            The values of other features that are needed to calculate this
	 *            value. The order and offsets of these features must be the
	 *            same as those returned by this class's getDependencies and
	 *            getDependencyOffsets methods respectively. The first indice
	 *            indicates the feature/window and the second indicates the
	 *            value.
	 * @return The extracted feature value(s).
	 * @throws Exception
	 *             Throws an informative exception if the feature cannot be
	 *             calculated.
	 */
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) throws Exception {
		double ret[] = new double[10];
		double wk1[] = new double[samples.length];
		double wk2[] = new double[samples.length];
		double wkm[] = new double[num_dimensions];
		wk1[0] = samples[0];
		wk2[samples.length - 2] = samples[samples.length - 1];
		for (int i = 1; i < samples.length - 1; ++i) {
			wk1[i] = samples[i];
			wk2[i - 1] = samples[i];
		}
		for (int i = 0; i < num_dimensions; ++i) {
			double num = 0.0;
			double denom = 0.0;
			for (int j = 0; j < (samples.length - i); ++j) {
				num += wk1[j] * wk2[j];
				denom += wk1[j] * wk1[j] + wk2[j] * wk2[j];
			}
			ret[i] = 2.0 * num / denom;
			for (int j = 0; j < i; ++j) {
				ret[j] = wkm[j] - ret[i] * wkm[i - j];
			}
			for (int j = 0; j <= i; ++j) {
				wkm[j] = ret[j];
			}
			for (int j = 0; j < (samples.length - i - 1); ++j) {
				wk1[j] -= wkm[i] * wk2[j];
				wk2[j] = wk2[j + 1] - wkm[i] * wk1[j + 1];
			}
		}

		return ret;
	}

	/**
	 * Permits the number o LPC coeffecients to be calculated. This is a unique
	 * feature in that the number of dimensions of the feature are changed by
	 * this function, requiring a reference back to the parent to redraw the
	 * table displaying this information.
	 * 
	 * @param n
	 *            number of coeffecients to be calculated.
	 * @throws Exception
	 *             thrown if less than 1 feature is to be calculated.
	 */
	public void setNumberDimensions(int n) throws Exception {
		if (n < 1) {
			throw new Exception("LPC must have at least 1 dimension");
		} else {
			num_dimensions = n;
			definition.dimensions = num_dimensions;
			parent.updateTable();
			// System.out.println("Updating Table");
		}
	}

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to get the default values used to populate the table's entries.
	 * The correct index values are inferred from definition.attribute value.
	 * 
	 * @param index
	 *            which of AreaMoment's attributes should be edited.
	 */
	public String getElement(int index) throws Exception {
		if (index != 0) {
			throw new Exception("INTERNAL ERROR: invalid index " + index
					+ " sent to AreaMoments:getElement");
		} else {
			return Integer.toString(num_dimensions);
		}
	}

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to set the default values used to popylate the table's entries.
	 * Like getElement, the correct index values are inferred from the
	 * definition.attributes value.
	 * 
	 * @param index
	 *            attribute to be set
	 * @param value
	 *            new value of the attribute
	 */
	public void setElement(int index, String value) throws Exception {
		if (index != 0) {
			throw new Exception("INTERNAL ERROR: invalid index " + index
					+ " sent to AreaMoments:setElement");
		} else {
			try {
				int type = Integer.parseInt(value);
				setNumberDimensions(type);
			} catch (NumberFormatException e) {
				throw new Exception("Number of Coeffecients must be an integer");
			} catch (Exception e) {
				throw e;
			}
		}
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		return new LPCRemoved();
	}

}
