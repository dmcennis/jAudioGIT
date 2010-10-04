/**
 * 
 */
package jAudioFeatureExtractor.Aggregators;

import java.util.Arrays;

import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.AudioFeatures.MFCC;
import jAudioFeatureExtractor.AudioFeatures.Moments;
import jAudioFeatureExtractor.AudioFeatures.RMS;
import jAudioFeatureExtractor.AudioFeatures.ZeroCrossings;
import junit.framework.TestCase;

/**
 * @author mcennis
 *
 */
public class MultipleFeatureHistogramTest extends TestCase {

	MultipleFeatureHistogram test;
	String[] base1;
	String[] base2;
	String[] base3;
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(MultipleFeatureHistogramTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		base1 = new String[]{"RMS", "ZeroCrossings"};
		base2 = new String[]{"MFCC"};
		base3 = new String[]{"RMS", "Moments"};
	}
	

	/*
	 * Test method for 'jAudioFeatureExtractor.Aggregators.MultipleFeatureHistogram.collapseFeatures(double[][][])'
	 */
	public void testCollapseFeatures() throws Exception{
		test = new MultipleFeatureHistogram(base3,8);
		test.init(new int[]{1,3});
		double[][][] data = new double[5][4][];
		for(int i=0;i<data.length;++i){
			for(int j=0;j<data[0].length;++j){
				if(j==1){
					data[i][j] = new double[]{0.0};
				}else if(j==3){
					data[i][j] = new double[5];
					Arrays.fill(data[i][j],0.0);
				}else{
					data[i][j]=null;
				}
			}
		}
		int[][] result = test.collapseFeatures(data,new int[]{1,3});
		// start off with a check of the total number of features
		assertEquals(6,result.length);
		// verify the feature and dimension entries indivudally
		assertEquals("result 0 0",1,result[0][0]);
		assertEquals("result 0 1",0,result[0][1]);
		assertEquals("result 1 0",3,result[1][0]);
		assertEquals("result 1 1",0,result[1][1]);
		assertEquals("result 2 0",3,result[2][0]);
		assertEquals("result 2 1",1,result[2][1]);
		assertEquals("result 3 0",3,result[3][0]);
		assertEquals("result 3 1",2,result[3][1]);
		assertEquals("result 4 0",3,result[4][0]);
		assertEquals("result 4 1",3,result[4][1]);
		assertEquals("result 5 0",3,result[5][0]);
		assertEquals("result 5 1",4,result[5][1]);
	}
	
	/*
	 * Test method for 'jAudioFeatureExtractor.Aggregators.MultipleFeatureHistogram.assignToBins(double[][][], int, int)'
	 */
	public void testAssignToBins() throws Exception{
		test = new MultipleFeatureHistogram(base1,8);
		test.init(new int[]{1,3});
		double[][][] data = new double[6][4][];
		for(int i=0;i<data.length;++i){
			for(int j=0;j<data[0].length;++j){
				if((j==1)&&(i==0)){
					data[i][j]=null;
				}
				else if(j==1){
					data[i][j] = new double[]{i};
				}else if(j==3){
					data[i][j] = new double[]{i};
				}else{
					data[i][j]=null;
				}
			}
		}
		
		Integer[] result = test.assignToBins(data,1,0);
		assertEquals("size",6,result.length);
		assertEquals("0",null,result[0]);
		assertEquals("1",0,result[1].intValue());
		assertEquals("2",1,result[2].intValue());
		assertEquals("3",2,result[3].intValue());
		assertEquals("4",3,result[4].intValue());
		assertEquals("5",3,result[5].intValue());
		
	}


	/*
	 * Test method for 'jAudioFeatureExtractor.Aggregators.MultipleFeatureHistogram.calculateOffset(double[][][], int[][])'
	 */
	public void testCalculateOffset() throws Exception{
		test = new MultipleFeatureHistogram(base1,8);
		test.init(new int[]{1,3});
		double[][][] data = new double[6][4][];
		for(int i=0;i<data.length;++i){
			for(int j=0;j<data[0].length;++j){
				if((j==1)&&(i==0)){
					data[i][j]=null;
				}else if((j==3)&&(i<2)){
					data[i][j] = null;
				}
				else if(j==1){
					data[i][j] = new double[]{i};
				}else if(j==3){
					data[i][j] = new double[]{i};
				}else{
					data[i][j]=null;
				}
			}
		}
		int[][] featureList = test.collapseFeatures(data,new int[]{1,3});
		int ret = test.calculateOffset(data,new int[]{1,3});
		assertEquals(2,ret);
		
	}

	/*
	 * Test method for 'jAudioFeatureExtractor.Aggregators.MultipleFeatureHistogram.combineBins(Integer[][], int)'
	 */
	public void testCombineBinsBasic() {
		test = new MultipleFeatureHistogram(base1,8);
		Integer[][] binnedFeatures = new Integer[2][5];
		binnedFeatures[0][0] = new Integer(0);
		binnedFeatures[0][1] = new Integer(0);
		binnedFeatures[0][2] = new Integer(2);
		binnedFeatures[0][3] = new Integer(3);
		binnedFeatures[0][4] = new Integer(3);
		binnedFeatures[1][0] = new Integer(0);
		binnedFeatures[1][1] = new Integer(1);
		binnedFeatures[1][2] = new Integer(2);
		binnedFeatures[1][3] = new Integer(3);
		binnedFeatures[1][4] = new Integer(3);
		double[] expectedResults = new double[64];
		java.util.Arrays.fill(expectedResults,0.0);
		expectedResults[0] = 1.0/5.0;
		expectedResults[8] = 1.0/5.0;
		expectedResults[18] = 1.0/5.0;
		expectedResults[27] = 2.0/5.0;
		
		double[] result = test.combineBins(binnedFeatures,0);
		assertEquals(64,result.length);
		for(int i=0;i<64;++i){
			assertEquals(Integer.toString(i),expectedResults[i],result[i]);
		}

	}
	
	public void testCombineBinsWithOffset(){
		test = new MultipleFeatureHistogram(base1,8);
		Integer[][] binnedFeatures = new Integer[2][5];
		binnedFeatures[0][0] = null;
		binnedFeatures[0][1] = new Integer(0);
		binnedFeatures[0][2] = new Integer(2);
		binnedFeatures[0][3] = new Integer(3);
		binnedFeatures[0][4] = new Integer(3);
		binnedFeatures[1][0] = new Integer(0);
		binnedFeatures[1][1] = new Integer(1);
		binnedFeatures[1][2] = new Integer(2);
		binnedFeatures[1][3] = new Integer(3);
		binnedFeatures[1][4] = new Integer(3);
		double[] expectedResults = new double[64];
		java.util.Arrays.fill(expectedResults,0.0);
		expectedResults[8] = 1.0/4.0;
		expectedResults[18] = 1.0/4.0;
		expectedResults[27] = 2.0/4.0;
		
		double[] result = test.combineBins(binnedFeatures,1);
		assertEquals(64,result.length);
		for(int i=0;i<64;++i){
			assertEquals(Integer.toString(i),expectedResults[i],result[i]);
		}
		
	}

}
