/**
 * Mean Aggregator
 * Created for the 2006 ISMIR jAudio release
 * Created by Daniel McEnnis
 */
package jAudioFeatureExtractor.Aggregators;

import jAudioFeatureExtractor.ACE.DataTypes.AggregatorDefinition;
import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;
import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.GeneralTools.StringMethods;

import java.io.DataOutputStream;
import java.util.Vector;

/**
 * Calculates the mean of a feature accross all windows where it is defined.
 * When the feature has more than one dimension, the mean has an equal number of
 * dimensions and the value of each dimension is the mean of that dimension. If
 * the feature has a variable number of dimensions, the dimensionality of the
 * result is the largest number of dimensions present and the mean for each
 * dimension is calculated over all values defined for that dimension.
 * 
 * @author Daniel McEnnis
 */
public class Mean extends Aggregator {
	
	int feature;
	
	public Mean(){
		metadata = new AggregatorDefinition("Mean","This is the overall average over all windows.",true,null);
	}

	/**
	 * Provide a list of features that are to be aggregated by this feature.
	 * Returning null indicates that this aggregator accepts only one feature
	 * and every feature avaiable should be used.
	 * 
	 * @return list of features to be used by this aggregator or null
	 */
	public String[] getFeaturesToApply() {
		return null;
	}

	/**
	 * 
	 * @see jAudioFeatureExtractor.Aggregators.Aggregator#getFeatureDefinition()
	 */
	public FeatureDefinition getFeatureDefinition() {
		return definition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jAudioFeatureExtractor.Aggregators.Aggregator#init(jAudioFeatureExtractor.AudioFeatures.FeatureExtractor[])
	 */
	public void init(int[] featureIndeci)
			throws Exception {
		feature = featureIndeci[0];
	}
	
	

	@Override
	public Object clone() {
		return new Mean();
	}

	@Override
	public void setSource(FeatureExtractor feature) {
		FeatureDefinition this_def = feature.getFeatureDefinition();
		definition = new FeatureDefinition(this_def.name + " Overall Average",
				this_def.description + System.getProperty("line.separator")
						+ "This is the overall average over all windows.",
				this_def.is_sequential, this_def.dimensions);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jAudioFeatureExtractor.Aggregators.Aggregator#aggregate(double[][][])
	 */
	public void aggregate(double[][][] values) {
		if ((values == null) || (values.length == 0)) {
			result = new double[1];
			result[0] = Double.NaN;
			definition.dimensions = 1;
		} else {
			// find the max number of dimensions
			int max = -1;
			for (int i = 0; i < values.length; ++i) {
				if ((values[i][feature] != null)
						&& (values[i][feature].length > max)) {
					max = values[i][feature].length;
				}
			}
			if (max <= 0) {
				result = new double[] { 0.0 };
				definition.dimensions = 1;
			} else {
				// now calculate means over all the dimensions
				result = new double[max];
				definition.dimensions = max;
				for (int i = 0; i < max; ++i) {
					int count = 0;
					double sum = 0.0;
					for (int j = 0; j < values.length; ++j) {
						if ((values[j][feature] != null)
								&& (values[j][feature].length > i)) {
							sum += values[j][feature][i];
							count++;
						}
					}
					if (count == 0) {
						result[i] = 0.0;
					} else {
						result[i] = sum / ((double) count);
					}
				}
			}
		}
	}


}
