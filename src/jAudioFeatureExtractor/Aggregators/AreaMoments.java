/**
 * Area Moments Aggregator
 *
 * Created by Daniel McEnnis for ISMIR 2006 jAUdio release.
 * Published under the LGPL see license.txt or at http://www.fsf.org
 */
package jAudioFeatureExtractor.Aggregators;

import jAudioFeatureExtractor.ACE.DataTypes.AggregatorDefinition;
import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

/**
 * <h2>Area Method of Moments Aggregator</h2>
 * <p></p>
 * <h3>Algorithm Description</h3>
 * <p>This specific aggregator was originally intended to be the first 10 statistical moments of a 2D area. 
 * This algorithm was first used in graphic machine learning by Fujinaga in 1998.  Its first use in digital
 * signal processing is in McEnnis and Fujinaga 2005.
 * <p>It is a specific feature as the effectiveness of the resulting features is heavily dependent on the importance of the feature ordering.
 * </p>
 * <h3>Algorithm History</h3>
 * <p>The algorithm treats the image as a 2D function f(x,y) = z where x and y are indecis of the underlying matrix.
 * The order of x and y is increased together from order 0 to order 3, caluclated with a coeefcient calculated by the binomial 
 * of the x and y order.</p>
 * <p>The original DSP version is a collaborative effort between the author of the code and Ichiro Fujinaga.
 * <p>Fujinaga, I. Adaptive Optical Music Recognition. PhD thesis, McGill University, 1997. </p>
 * <p></p>
 * <p>Code utilizes the Colt matrix package available under either LGPL or BSD license.  See Colt's online documentation for more details.
 * 
 * @author Daniel McEnnis
 *
 */
public class AreaMoments extends Aggregator {

	String[] featureNames = null;
	int[] featureNameIndecis = null;

    int order = 7;
	
	/**
	 * Constructs an AreaMoments aggregator.  This isn't valid until specific features are adde to the system (in a particular order).
	 */
	public AreaMoments(){
		metadata = new AggregatorDefinition("Area Moments","Calculates 2D statistical moments for the given features",false,
                new String[]{"maximum order (length is order^2) of 2D statistical moments to calculate"});
	}
	
	@Override
	public void aggregate(double[][][] values) {
		result = new double[order*order];
		java.util.Arrays.fill(result,0.0);
		int offset = super.calculateOffset(values,featureNameIndecis);
		int[][] featureIndecis = super.collapseFeatures(values,featureNameIndecis);
        for (int i=offset; i < values.length; ++i) {
            double row = (2.0*((double)(i-offset))/((double)(values.length - offset))) - 1.0;
            for (int j = 0; j < featureIndecis.length; ++j) {
                double column = (2.0*((double)j)/((double)(featureIndecis.length)))-1.0;
                double xpow = 1.0;
                for(int x=0;x<order;++x){
                    double ypow = 1.0;
                    for (int y=0;y<order;++y){
                        result[order*x+y] += values[i][featureIndecis[j][0]][featureIndecis[j][1]] * xpow * ypow;
                        ypow *= column;
                    }
                    xpow *= row;
                }
            }
        }
	}

	@Override
	public Object clone() {
		AreaMoments ret = new AreaMoments();
		if(featureNames != null){
			ret.featureNames = featureNames.clone();
		}
		if(featureNameIndecis != null){
			ret.featureNameIndecis = featureNameIndecis.clone();
		}
		return new AreaMoments();
	}

	@Override
	public FeatureDefinition getFeatureDefinition() {
		return definition;
	}

	@Override
	public String[] getFeaturesToApply() {
		return featureNames;
	}

	@Override
	public void init(int[] featureIndecis) throws Exception {
		if(featureIndecis.length != featureNames.length){
			throw new Exception("INTERNAL ERROR (Agggregator.AreaMoments): number of feature indeci does not match number of features");
		}
		this.featureNameIndecis = featureIndecis;
	}

	@Override
	public void setParameters(String[] featureNames, String[] params) throws Exception {
		this.featureNames = featureNames;
		String names = featureNames[0];
		for(int i=1;i<featureNames.length;++i){
			names += " " + featureNames[i];
		}
        if((params != null) && (params.length > 0)){
            order = Integer.parseInt(params[0]);
        }
		definition = new FeatureDefinition("Area Moments: "+names,"2D moments constructed from features "+names+".",true,order*order);
	}

    /**
     * Provide a list of the values of all parameters this aggregator uses.
     * Aggregators without parameters return null.
     *
     * @return list of the values of parmeters or null.
     */
    @Override
    public String[] getParamaters() {
        return new String[]{Integer.toString(order)};   //To change body of overridden methods use File | Settings | File Templates.
    }


}
