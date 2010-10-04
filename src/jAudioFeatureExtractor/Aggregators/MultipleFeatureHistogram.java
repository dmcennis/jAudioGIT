/**
 * Multiple Feature Histogram
 * 
 * created by Daniel McEnnis for the 2006 ISMIR jAUdio release
 * 
 */
package jAudioFeatureExtractor.Aggregators;

import jAudioFeatureExtractor.ACE.DataTypes.AggregatorDefinition;
import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;
import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.GeneralTools.StringMethods;

import java.io.DataOutputStream;

/**
 * <h2>Multiple Fetaure Histogram</h2>
<p>This specific algorithm correlates different feature dimensions provided, binning them
into equal bins relative to each feature's range. The resulting feature set, similar to the 
area method of moments, captures cross-feature information.  However, this feature uses an extremely
large feature set that grows exponentially with the number of features added.  On the positive side, the order
of the features is irrelevant to its performance.
</p>
<h2>Histogram Binning History</h2>
<p>First used in 2000, it was not formally published until McEnnis and Fujinaga 2005.</p>
 *
 * @author Daniel McEnnis
 * 
 */
public class MultipleFeatureHistogram extends Aggregator {

	String[] base = null;

	int[] indecis = null;

	int binsPerDimension;

	/**
	 * Constructs a new aggregator.  This aggregator is not valid until it has a feature list set as a parameter.
	 */
	public MultipleFeatureHistogram() {
		metadata = new AggregatorDefinition("Multiple Feature Histogram",
				"a histogram of categories of input", false,
				new String[] { "Number of bins for 1st dimension" });
	}

	/**
	 * Constructs a fully functional aggregator.
	 */
	public MultipleFeatureHistogram(String[] fe, int bins) {
		base = fe;
		String name = "Histogram:";
		String description = "Histogram of concurrent changes in";
		int dimensions = 0;
		for (int i = 0; i < fe.length; ++i) {
			name += " " + fe[i];
			description += " " + fe[i];
		}
		definition = new FeatureDefinition(name, description, true, dimensions);
		binsPerDimension = bins;
		metadata = new AggregatorDefinition("Multiple Feature Histogram",
				"a histogram of categories of input", false,
				new String[] { "Number of bins for 1st dimension" });
	}

	/* (non-Javadoc)
	 * @see jAudioFeatureExtractor.Aggregators.Aggregator#getParamaters()
	 */
	@Override
	public String[] getParamaters() {
		return new String[]{Integer.toString(binsPerDimension)};
	}

	@Override
	public void setParameters(String[] features, String[] params)
			throws Exception {
		if (features == null) {
			throw new Exception(
					"MultipleFeatureHistogram requires a list of features to aggregate");
		}
		if (params.length == 1) {
			try {
				binsPerDimension = Integer.parseInt(params[0]);
				base = features;
				String name = "Histogram:";
				String description = "Histogram of concurrent changes in";
				int dimensions = 0;
				for (int i = 0; i < features.length; ++i) {
					name += " " + features[i];
					description += " " + features[i];
					// dimensions +=
					// features[i].getFeatureDefinition().dimensions;
				}
				// dimensions = (int) Math.pow(binsPerDimension, dimensions);
				definition = new FeatureDefinition(name, description, true,
						dimensions);

			} catch (NumberFormatException e) {
				throw new Exception(
						"Parameters to MultipleFeatureHistogram must be an integer");
			}
		} else {
			throw new Exception(
					"MultipleFeatureHistogram takes exactly one argument of type integer");
		}
	}

	@Override
	public void aggregate(double[][][] values) throws Exception{

		// flatten features/dimensions into a single array
		int[][] featureList = super.collapseFeatures(values, indecis);

		// now we know how precisely how many dimensions have values, adjust the
		// FeatureDefinition accordingly
		definition.dimensions = (int) Math.pow(binsPerDimension,
				featureList.length);

		if(definition.dimensions > 1048576){
			throw new Exception("Number of dimensions for "+definition.name+" exceeds 1048576 - "+ definition.dimensions);
		}
		
		// calculate earliest window that has values for all features to be
		// included
		int offset = super.calculateOffset(values, indecis);

		// transform the arrays of values into arrays of bins
		// The dimensions are [dimension][windows]
		Integer[][] bins = new Integer[featureList.length][];
		for (int i = 0; i < featureList.length; ++i) {
			bins[i] = assignToBins(values, featureList[i][0], featureList[i][1]);
		}

		// combine these bins into a single histogram
		result = combineBins(bins, offset);

	}

	@Override
	public Object clone() {
		MultipleFeatureHistogram ret = new MultipleFeatureHistogram();
		if (base != null) {
			try {
				ret.setParameters(base, new String[] { Integer
						.toString(binsPerDimension) });
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return ret;
	}

	@Override
	public FeatureDefinition getFeatureDefinition() {
		return definition;
	}

	@Override
	public String[] getFeaturesToApply() {

		return base;
	}

	@Override
	public void init(int[] featureIndecis) throws Exception {
		indecis = featureIndecis;
	}

	/**
	 * Takes a particular dimension of a particular feature and divides the
	 * distance between the largest and smallest value into 4 linear bins.
	 * values less than min+25% are bin 0, values less than min+50% are bin 1,
	 * etc. Null entries become null entries in the resulting Integer array.
	 * 
	 * @param values
	 *            array holding output of all features
	 * @param feature
	 *            which feature in the values array to extract
	 * @param dimension
	 *            which dimension of this feature to extract
	 * @return
	 */
	Integer[] assignToBins(double values[][][], int feature, int dimension) {
		Integer[] ret = new Integer[values.length];

		// first calculate the bin values
		double[] bin = new double[binsPerDimension - 1];
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		for (int i = 0; i < values.length; ++i) {
			if ((values[i][feature] != null)
					&& (values[i][feature][dimension] > max)) {
				max = values[i][feature][dimension];
			}
			if ((values[i][feature] != null)
					&& (values[i][feature][dimension] < min)) {
				min = values[i][feature][dimension];
			}
		}
		double separator = (max - min) / ((double) binsPerDimension);
		bin[0] = min + separator;
		for (int j = 1; j < bin.length; ++j) {
			bin[j] = bin[j - 1] + separator;
		}

		// now assign a bin to every value in the dataset
		for (int i = 0; i < values.length; ++i) {
			if (values[i][feature] == null) {
				ret[i] = null;
			} else {
				ret[i] = null;
				for (int j = 0; j < bin.length; ++j) {
					if (values[i][feature][dimension] < bin[j]) {
						ret[i] = new Integer(j);
						break;
					}
				}
				if (ret[i] == null) {
					ret[i] = new Integer(binsPerDimension - 1);
				}
			}
		}

		return ret;
	}

	double[] combineBins(Integer[][] bins, int offset) {
		int numDimensions = (int) Math.pow(binsPerDimension, bins.length);
		double[] ret = new double[numDimensions];

		// for every bin combiniation, increment the histogram
		for (int i = offset; i < bins[0].length; ++i) {
			int index = 0;
			int factor = 1;
			for (int j = 0; j < bins.length; ++j) {
				index += (bins[j][i].intValue()) * factor;
				factor *= binsPerDimension;
			}
			ret[index] += 1.0;
		}

		// Normalize the histogram
		for (int i = 0; i < ret.length; ++i) {
			ret[i] /= (bins[0].length - offset);
		}
		return ret;
	}

}
