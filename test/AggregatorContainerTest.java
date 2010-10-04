/**
 * 
 */
package jAudioFeatureExtractor.Aggregators;

import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.AudioFeatures.LPC;
import jAudioFeatureExtractor.AudioFeatures.MFCC;
import jAudioFeatureExtractor.AudioFeatures.RMS;
import junit.framework.TestCase;

/**
 * @author mcennis
 *
 */
public class AggregatorContainerTest extends TestCase {

	AggregatorContainer aggContainer;
	FeatureExtractor[] features;
	boolean[] toggle;
	Aggregator[] aggs;
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(AggregatorContainerTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		aggContainer = new AggregatorContainer();
	}
	
	public void testAddAggregator() throws Exception{
		Aggregator agg1 = new DummyAgg();
		Aggregator agg2 = new DummyAgg();
		aggs = new Aggregator[]{agg1,agg2 };
		aggContainer.add(aggs);
		assertEquals(2,aggContainer.aggregatorTemplate.size());
		assertEquals(agg1,aggContainer.aggregatorTemplate.get(0));
		assertEquals(agg2,aggContainer.aggregatorTemplate.get(1));
	}
	
	public void testTrivialAddFeature() throws Exception{
		FeatureExtractor one = new RMS();
		FeatureExtractor two = new LPC();
		features = new FeatureExtractor[]{one,two};
		aggContainer.add(features);
		assertEquals(2,aggContainer.featureList.size());
		assertEquals(one,aggContainer.featureList.get(0));
		assertEquals(two,aggContainer.featureList.get(1));
		assertEquals(2,aggContainer.featureIndecis2FeatureListMapping.size());
		assertEquals(0,aggContainer.featureIndecis2FeatureListMapping.get(0).intValue());
		assertEquals(1,aggContainer.featureIndecis2FeatureListMapping.get(1).intValue());
	}
	
	public void testAddFeatureWithGaps() throws Exception{
		FeatureExtractor one = new RMS();
		FeatureExtractor two = new LPC();
		features = new FeatureExtractor[]{null,null,one,null,null,two,null};
		toggle = new boolean[]{false,false,true,false,false,true,false};
		aggContainer.add(features,toggle);
		assertEquals(2,aggContainer.featureList.size());
		assertEquals(one,aggContainer.featureList.get(0));
		assertEquals(two,aggContainer.featureList.get(1));
		assertEquals(2,aggContainer.featureIndecis2FeatureListMapping.size());
		assertEquals(2,aggContainer.featureIndecis2FeatureListMapping.get(0).intValue());
		assertEquals(5,aggContainer.featureIndecis2FeatureListMapping.get(1).intValue());
	}
	
	public void testAggregatorFeature() throws Exception{
		FeatureExtractor featureOne = new RMS();
		FeatureExtractor featureTwo = new LPC();
		features = new FeatureExtractor[]{null,null,featureOne,null,null,featureTwo,null};
		toggle = new boolean[]{false,false,true,false,false,true,false};
		Aggregator aggOne = new DummyAgg();
		Aggregator aggTwo = new DummyAgg(new FeatureExtractor[]{new RMS()});
		Aggregator aggThree = new DummyAgg(new FeatureExtractor[]{new LPC(),new RMS()});
		
		aggContainer.add(new Aggregator[]{aggOne,aggTwo,aggThree});
		aggContainer.add(features,toggle);
		assertEquals(4,aggContainer.aggregatorList.size());
		assertEquals(1,((DummyAgg)(aggContainer.aggregatorList.get(0))).featureIndex.length);
		assertEquals(2,((DummyAgg)(aggContainer.aggregatorList.get(0))).featureIndex[0]);
		assertEquals(1,((DummyAgg)(aggContainer.aggregatorList.get(1))).featureIndex.length);
		assertEquals(5,((DummyAgg)(aggContainer.aggregatorList.get(1))).featureIndex[0]);
		assertEquals(1,((DummyAgg)(aggContainer.aggregatorList.get(2))).featureIndex.length);
		assertEquals(2,((DummyAgg)(aggContainer.aggregatorList.get(2))).featureIndex[0]);
		assertEquals(1,((DummyAgg)(aggContainer.aggregatorList.get(2))).featureIndex.length);
		assertEquals(5,((DummyAgg)(aggContainer.aggregatorList.get(3))).featureIndex[0]);
		assertEquals(2,((DummyAgg)(aggContainer.aggregatorList.get(3))).featureIndex[1]);
	}
	
	public void testBadAggregatorFeature() throws Exception{
		FeatureExtractor featureOne = new RMS();
		FeatureExtractor featureTwo = new LPC();
		Aggregator aggOne = new DummyAgg(new FeatureExtractor[]{new MFCC()});
		Aggregator aggTwo = new DummyAgg(new FeatureExtractor[]{new RMS(),new MFCC()});
		aggContainer.add(new FeatureExtractor[]{featureOne,featureTwo});
		aggContainer.add(new Aggregator[]{aggOne,aggTwo});
		assertEquals(0,aggContainer.aggregatorList.size());
	}

}
