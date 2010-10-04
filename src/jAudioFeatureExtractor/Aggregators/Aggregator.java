/**
 * 
 */
package jAudioFeatureExtractor.Aggregators;

import java.io.DataOutputStream;

import jAudioFeatureExtractor.ACE.DataTypes.AggregatorDefinition;
import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;
import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.GeneralTools.StringMethods;

/**
 * Aggregator is an interface for specifying the mechanism for collapsing
 * frame-by-frame features into per-file data. There exists two types of
 * aggregators - specific aggregators and generic aggregators.
 * <p>
 * Generic aggregators aggregate for each feature (seperately) that is to be
 * saved and should override init and setSource methods. Specific aggregators
 * can aggregate any number of features, but these features must be specified in
 * advance.
 * 
 * @author Daniel McEnnis
 * 
 */
public abstract class Aggregator {

	double[] result = null;

	AggregatorDefinition metadata;

	FeatureDefinition definition;

	/**
	 * Convenience variable containing the end of line characters for this
	 * system.
	 */
	public static final String LINE_SEP = System.getProperty("line.separator");

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
	 * Provide a list of the values of all parameters this aggregator uses.
	 * Aggregators without parameters return null.
	 * 
	 * @return list of the values of parmeters or null.
	 */
	public String[] getParamaters() {
		return null;
	}

	/**
	 * Create a new aggregator of the same class
	 * 
	 */
	public Object clone() {
		return null;
	}

	/**
	 * Description of a particular instantiation of an aggregate. This should
	 * not be called until after the specific features have been specified by
	 * the init function.
	 * 
	 * @return Feature Definition describing this instantiation of this
	 *         aggregate object
	 */
	public FeatureDefinition getFeatureDefinition() {
		return definition;
	}

	/**
	 * Returns a description of this instantiation of this class of aggregator
	 */
	public AggregatorDefinition getAggregatorDefinition() {
		return metadata;
	}

	/**
	 * Specifies which Features are to be extracted and the index of these
	 * features in the values array that will passed into the aggregate function
	 * 
	 * @param source
	 *            FeatureExtractor references used for this instantiation
	 * @param featureIndecis
	 *            Indecis of these features in the array passed in aggregate
	 * @throws Exception
	 *             if either parameter is null, of dicffering lengths, or
	 *             contain invalid index values.
	 */
	public void init(int[] featureIndecis) throws Exception {

	}

	public void setSource(FeatureExtractor feature) {

	}

	/**
	 * Aggregates the values of the features specified by the init function
	 * accross all windows of the data recieved.
	 * 
	 * @param values
	 *            complete array of the extracted features. Indecis are window,
	 *            feature, and then feature value.
	 */
	public void aggregate(double[][][] values) throws Exception {

	}

	/**
	 * Output the feature definition entry (for an ACE feature definition file)
	 * for this particular instantiation of the aggreagtor.
	 * 
	 * @param output
	 *            output stream to be used.
	 * @throws Exception
	 */
	public void outputACEFeatureKeyEntries(DataOutputStream output)
			throws Exception {
		output.writeBytes("	<feature>" + LINE_SEP);
		output.writeBytes("		<name>" + definition.name + "</name>" + LINE_SEP);
		output.writeBytes("		<description>" + definition.description
				+ "</description>" + LINE_SEP);
		output.writeBytes("		<is_sequential>" + definition.is_sequential
				+ "</is_sequential>" + LINE_SEP);
		output.writeBytes("		<parallel_dimensions>" + definition.dimensions
				+ "</parallel_dimensions>" + LINE_SEP);
		output.writeBytes("	</feature>" + LINE_SEP);

	}

	/**
	 * Output the data definition entries of a the ACE format
	 * 
	 * @param output stream to write the data to
	 * @throws Exception
	 */
	public void outputACEValueEntries(DataOutputStream output) throws Exception {
		output.writeBytes("		<feature>" + LINE_SEP);
		output.writeBytes("			<name>" + definition.name + "</name>" + LINE_SEP);
		for (int i = 0; i < result.length; ++i) {
			output.writeBytes("			<v>"
					+ StringMethods.getDoubleInScientificNotation(result[i], 4)
					+ "</v>" + LINE_SEP);
		}
		output.writeBytes("		</feature>" + LINE_SEP);
	}

	/**
	 * Output the header entries of a Weka ARFF file.  This should only be called once the 
	 * full aggregator output has been calculated.
	 * 
	 * @param output stream to write the data to
	 * @throws Exception
	 */
	public void outputARFFHeaderEntries(DataOutputStream output)
			throws Exception {
		for (int i = 0; i < definition.dimensions; ++i) {
			output.writeBytes("@ATTRIBUTE \"" + definition.name + i
					+ "\" NUMERIC" + LINE_SEP);
		}
	}

	/**
	 * Output the data in the ARFF body.
	 * 
	 * @param output
	 * @throws Exception
	 */
	public void outputARFFValueEntries(DataOutputStream output)
			throws Exception {
		output.writeBytes(StringMethods.getDoubleInScientificNotation(
				result[0], 4));
		for (int i = 1; i < definition.dimensions; ++i) {
			output
					.writeBytes(","
							+ StringMethods.getDoubleInScientificNotation(
									result[i], 4));
		}
	}

	/**
	 * Set parameters of the aggregator to the given values.  For specific aggregators, the feature list
	 * is non-null and references currently loaded features.
	 * Throws exception if the feature list is null or contains invalid entries only if the aggregator is specific.
	 * Otherwise it is ignored.
	 * If the number of given parameters
	 * is greater (but not neccessarily less) than the number of actual paramaters, or
	 * if the parameters are in the wrong format, an aggregator that uses parameters may throw an exception.
	 * Both null and zero length array imply no parameters, but only null guarantees an exception if a parameter
	 * is present.
	 *
	 * @param featureNames strings matching features for specific aggregation.
	 * @param params strings that can be cast by toString to the appropriate parameter types.
	 * @throws exceptions for a number of format or null entry conditions (see above).
	 */
	public void setParameters(String[] featureNames, String[] params)
			throws Exception {

	}

	protected int calculateOffset(double[][][] values, int[] featureList) {
		int ret = 0;
		for (int i = 0; i < featureList.length; ++i) {
			int offset = 0;
			while (values[offset][featureList[i]] == null) {
				offset++;
			}
			if (offset > ret) {
				ret = offset;
			}
		}
		return ret;
	}

	protected int[][] collapseFeatures(double[][][] values, int[] indecis) {
		int count = 0;
		for (int i = 0; i < indecis.length; ++i) {
			if (values[values.length - 1][indecis[i]] != null) {
				count += values[values.length - 1][indecis[i]].length;
			}
		}
		int[][] ret = new int[count][2];
		count = 0;
		for (int i = 0; i < indecis.length; ++i) {
			if (values[values.length - 1][indecis[i]] != null) {
				for (int j = 0; j < values[values.length - 1][indecis[i]].length; ++j) {
					ret[count][0] = indecis[i];
					ret[count][1] = j;
					count++;
				}
			}
		}
		return ret;
	}
	
	/**
	 * Returns the results in a double array (more useful for embedding than an XML pipe solution).
	 * 
	 * @output returns the calculated results of analysis or null depending on whether calculations have taken place or not.
	 */
	public double[] getResults(){
		return result;
	}

}
