/**
 * 
 */
package jAudioFeatureExtractor.Aggregators;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;
import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.AudioFeatures.LPC;
import jAudioFeatureExtractor.AudioFeatures.MagnitudeSpectrum;
import jAudioFeatureExtractor.AudioFeatures.RMS;
import junit.framework.TestCase;

/**
 * @author mcennis
 *
 */
public class MeanTest extends TestCase {

	Mean test;
	RMS feature1;
	MagnitudeSpectrum feature2;
	LPC feature3;
	double[][][] values;
	int[] featureIndex;
	
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(MeanTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		test = new Mean();
		feature1 = new RMS();
		feature2 = new MagnitudeSpectrum();
		feature3 = new LPC();
	}
	
	public void testGetFeaturesToApply(){
		assertEquals(null,test.getFeaturesToApply());
	}
	
	public void testInit() throws Exception{
		featureIndex = new int[]{2};
		test.setSource(feature1);
		test.init(featureIndex);
		assertEquals("Root Mean Square Overall Average",test.definition.name);
		assertEquals(2,test.feature);
	}
	
	public void testGetFeatureDefinition() throws Exception {
		featureIndex = new int[]{2};
		test.setSource(feature1);
		test.init(featureIndex);
		FeatureDefinition definition = test.getFeatureDefinition();
		String description = "A measure of the power of a signal."+System.getProperty("line.separator")+"This is the overall average over all windows.";
		
		assertEquals("Root Mean Square Overall Average",definition.name);
		assertEquals(description,definition.description);
		assertEquals(1,definition.dimensions);
	}
	
	public void testAggregateBasic() throws Exception{
		featureIndex = new int[]{2};
		test.setSource(feature1);
		test.init(featureIndex);
		
		values = new double[10][3][1];
		for(int i=0;i<10;++i){
			values[i][2][0]=i;
		}
		
		test.aggregate(values);
		assertTrue(test.result != null);
		assertEquals(1,test.result.length);
		assertEquals(4.5,test.result[0],0.001);
	}
	
	public void testAggregateWithNulls() throws Exception {
		featureIndex = new int[]{2};
		test.setSource(feature1);
		test.init(featureIndex);
		
		values = new double[10][3][1];
		values[0][2] = null;
		values[1][2] = null;
		for(int i=2;i<10;++i){
			values[i][2][0] = i;
		}
		
		test.aggregate(values);
		assertTrue(test.result != null);
		assertEquals(1,test.result.length);
		assertEquals(5.5,test.result[0],0.001);
	}
	
	public void testAggregateWithMultiDimensions() throws Exception{
		featureIndex = new int[]{2};
		test.setSource(feature1);
		test.init(featureIndex);
		
		values = new double[10][3][3];
		for(int i=0;i<10;++i){
			values[i][2][0] = i;
			values[i][2][1] = 2*i;
			values[i][2][2] = 3*i;
		}
		
		test.aggregate(values);
		assertTrue(test.result != null);
		assertEquals(3,test.result.length);
		assertEquals(4.5,test.result[0],0.001);
		assertEquals(9,test.result[1],0.001);
		assertEquals(13.5,test.result[2],0.001);
	}
	
	public void testAggregateWithVariableMultiDimensions() throws Exception{
		featureIndex = new int[]{2};
		test.setSource(feature1);
		test.init(featureIndex);
		
		values = new double[10][3][];
		for(int i=0;i<5;++i){
			values[i][2]= new double[1];
		}
		for(int i=5;i<10;++i){
			values[i][2]=new double[2];
		}
		for(int i=0;i<10;++i){
			values[i][2][0] = i;
			if(i>4){
				values[i][2][1] = i;
			}
		}
		
		test.aggregate(values);
		assertTrue(test.result != null);
		assertEquals(2,test.result.length);
		assertEquals(4.5,test.result[0],0.001);
		assertEquals(7.0,test.result[1],0.001);
	}

}
