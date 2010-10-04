/**
 * Standard Deviation
 * 
 * Created by Daniel McEnnis for the 2006 jAudio release
 * 
 */
package jAudioFeatureExtractor.Aggregators;

import jAudioFeatureExtractor.ACE.DataTypes.AggregatorDefinition;
import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;
import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.GeneralTools.StringMethods;

import java.io.DataOutputStream;

/**
<h2>Standard Deviation</h2>
<p>This generic aggregator calculates standard deviation of each feature dimension independently.  It is one of the
original aggregators used in MIR research, present in the original Marsyas (2000) by Tzanetakis and Cook.</p>
 * @author Daniel McEnnis
 * 
 */
public class StandardDeviation extends Aggregator {

	int feature = -1;

	/**
	 * Constructs a new standard deviation aggregator.
	 */
	public StandardDeviation(){
		metadata = new AggregatorDefinition("Standard Deviation","Standard Deviation of the window-by-window data",true,null);
	}

	@Override
	public void aggregate(double[][][] values) {
		if (values[values.length-1][feature] == null) {
			definition.dimensions = 1;
			result = new double[] { 0.0 };
		} else {
			int max = values[values.length - 1][feature].length;
			definition.dimensions = max;
			result = new double[max];
			for (int i = 0; i < max; ++i) {
				int count = 0;
				double average = 0.0;
				;
				for (int j = 0; j < values.length; ++j) {
					if ((values[j][feature] != null)
							&& (values[j][feature].length > i)) {
						average += values[j][feature][i];
						count++;
					}
				}
				if (count < 2) {
					result[i] = 0.0;
				} else {
					average /= ((double) count);
					for (int j = 0; j < values.length; ++j) {
						if ((values[j][feature] != null)
								&& (values[j][feature].length > i)) {
							result[i] += Math.pow(values[j][feature][i]
									- average, 2.0);
						}
					}
					result[i] = Math.sqrt(result[i] / (((double) count) - 1.0));
				}
			}
		}
	}

	@Override
	public Object clone() {
		return new StandardDeviation();
	}

	@Override
	public FeatureDefinition getFeatureDefinition() {
		return definition;
	}

	@Override
	public String[] getFeaturesToApply() {
		return null;
	}

	@Override
	public void init(int[] featureIndecis) throws Exception {
		feature = featureIndecis[0];
	}

	@Override
	public void setSource(FeatureExtractor feature) {
		FeatureDefinition this_def = feature.getFeatureDefinition();
		definition = new FeatureDefinition(this_def.name
				+ " Overall Standard Deviation", this_def.description
				+ LINE_SEP
				+ "This is the overall standard deviation over all windows.",
				this_def.is_sequential, this_def.dimensions);
	}

}
