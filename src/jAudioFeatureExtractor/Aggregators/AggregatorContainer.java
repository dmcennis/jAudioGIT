/**
 * Aggregator Container
 * 
 * Created by Daniel McEnnis
 */
package jAudioFeatureExtractor.Aggregators;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;
import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;

import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Container object that handles the creation of aggregators. Encapsulates the process of matching aggregators to feature 
 * sets, calulating the content of aggregators from feature output, and the output of per-file data.
 * 
 * @author Daniel McEnnis
 * 
 */
public class AggregatorContainer {

	Vector<Aggregator> aggregatorTemplate;

	Vector<Aggregator> aggregatorList;

	Vector<FeatureExtractor> featureList;

	Vector<Integer> featureIndecis2FeatureListMapping;

	/**
	 * Construct a new, empty container.
	 */
	public AggregatorContainer() {
		aggregatorTemplate = new Vector<Aggregator>();
		aggregatorList = new Vector<Aggregator>();
		featureList = new Vector<FeatureExtractor>();
		featureIndecis2FeatureListMapping = new Vector<Integer>();
	}
	
	/**
	 * Returns the number of concrete aggregators produced.  This is usually not the same as the number of aggregators submitted
	 * as multiple dimensions of an aggregator's output are typically collpased.
	 * 
	 * @output number of concrete aggregators after expansion of the extractors by a concrete feature set.
	 */
	public int getNumberOfAggregators(){
		return aggregatorList.size();
	}
	
	/**
	 * Sets the aggregators to be used in per-file extraction.  All aggregators should be fully configured before being added.
	 * The building of the final aggregator lists occurs automatically if a feature set is already present.
	 */
	public void add(Aggregator[] aggs) throws Exception{
		for (int i = 0; i < aggs.length; ++i) {
			aggregatorTemplate.add(aggs[i]);
		}
		if(featureList.size() > 0){
			buildAggregatorList();
		}
	}

	/**
	 * Sets the feature set to use in extraction.  If the aggregators are already added, the final aggregator 
	 * list is created automatically.
	 */
	public void add(FeatureExtractor[] feature) throws Exception{
		boolean[] toggle = new boolean[feature.length];
		Arrays.fill(toggle, true);
		add(feature, toggle);

	}

	/**
	 * Adds a feature set, but only those that have the same toggle `
	 */
	public void add(FeatureExtractor[] feature, boolean[] toggle) throws Exception{
		featureList.clear();
		for (int i = 0; i < feature.length; ++i) {
			if (toggle[i]) {
				featureList.add(feature[i]);
				featureIndecis2FeatureListMapping.add(i);
			}
		}
		if(aggregatorTemplate.size()>0){
			buildAggregatorList();
		}
	}

	/**
	 * Returns an array of feature definitions.  If either the feature list or the aggregator list is not yet added, 
	 * this function returns an empty array.
	 *
	 */
	public FeatureDefinition[] getFeatureDefinitions() {
		FeatureDefinition[] ret = new FeatureDefinition[aggregatorList.size()];
		for (int i = 0; i < aggregatorList.size(); ++i) {
			ret[i] = aggregatorList.get(i).getFeatureDefinition();
		}
		return ret;
	}

	/**
	 * The input of this procedure executes all aggregators across all feature output.  The results are stored internal
	 * to each aggregator.
	 *
	 * @param values
	 */
	public void aggregate(double[][][] values) throws Exception{
		for (int i = 0; i < aggregatorList.size(); ++i) {
			aggregatorList.get(i).aggregate(values);
		}
	}

	/**
	 * Output the header file for the ACE machine learning format.
	 * 
	 * @param output location to put the final output.
	 * @throws Exception IO error occurs in processing.
	 */
	public void outputACEFeatureKeyEntries(DataOutputStream output)
			throws Exception {
		for (int i = 0; i < aggregatorList.size(); ++i) {
			aggregatorList.get(i).outputACEFeatureKeyEntries(output);
		}
	}

	/**
	 * Output the data file of the ACE machine learning format.
	 * 
	 * @param output location to put the final output data in.
	 * @throws Exception IO Error occurs in processing.
	 */
	public void outputACEValueEntries(DataOutputStream output) throws Exception {
		for (int i = 0; i < aggregatorList.size(); ++i) {
			aggregatorList.get(i).outputACEValueEntries(output);
		}
	}

	/**
	 * Output the headers for the Weka machine learning format.
	 * 
	 * @param output data stream to place output header info to.
	 * @throws Exception
	 */
	public void outputARFFHeaderEntries(DataOutputStream output) throws Exception {
		for (int i = 0; i < aggregatorList.size(); ++i) {
			aggregatorList.get(i).outputARFFHeaderEntries(output);
		}
		output.writeBytes("@DATA"+System.getProperty("line.separator"));
	}

	/**
	 * Output the content in a Weka data format.
	 * 
	 * @param output data stream to place the Weka data in.
	 * @throws Exception IO error occurs.
	 */
	public void outputARFFValueEntries(DataOutputStream output) throws Exception {
		for (int i = 0; i < aggregatorList.size(); ++i) {
			aggregatorList.get(i).outputARFFValueEntries(output);
			if(i< aggregatorList.size()-1){
				output.writeBytes(",");
			}
		}
		output.writeBytes(Aggregator.LINE_SEP);
	}

	void buildAggregatorList() throws Exception{
		aggregatorList.clear();
		for(int i=0;i<aggregatorTemplate.size();++i){
			String[] list = aggregatorTemplate.get(i).getFeaturesToApply();
			if(list == null){
				for(int j=0; j<featureList.size();++j){
					Aggregator entry = ((Aggregator)aggregatorTemplate.get(i).clone());
					entry.setSource(featureList.get(j));
					entry.init(new int[]{featureIndecis2FeatureListMapping.get(j)});
					aggregatorList.add(entry);
				}
			}else{
				boolean good = false;
				int[] indeci = new int[list.length];
				for(int j=0;j<list.length;++j){
					good = false;
					for(int k=0;k<featureList.size();++k){
						if(featureList.get(k).getFeatureDefinition().name.equals(list[j])){
							good = true;
							indeci[j] = featureIndecis2FeatureListMapping.get(k);
							break;
						}
					}
					if(!good){
						break;
					}
				}
				if(good){
					aggregatorTemplate.get(i).init(indeci);
					aggregatorList.add(aggregatorTemplate.get(i));
				}
			}
		}
	}

	/**
	 * Returns a list of results of aggregator results.  If no results exist yet, the results 
	 * should be the empty set, but is dependent on the aggregator class.  If the list is not yet 
	 * defined, the results are an empty array.
	 *
	 */
	public double[][] getResults(){
		LinkedList<double[]> ret = new LinkedList<double[]>();
		for(int i=0;i<aggregatorList.size();++i){
			ret.add(aggregatorList.get(i).getResults());
		}
		return ret.toArray(new double[][]{});
	}
	
}
