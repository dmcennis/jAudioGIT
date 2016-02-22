/**
 * Area Moments Aggregator
 *
 * Created by Daniel McEnnis for ISMIR 2006 jAUdio release.
 * Published under the LGPL see license.txt or at http://www.fsf.org
 */
package jAudioFeatureExtractor.Aggregators;

import jAudioFeatureExtractor.ACE.DataTypes.AggregatorDefinition;
import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

import java.util.ResourceBundle;

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
public class ZernikeMoments extends Aggregator {

	String[] featureNames = null;
	int[] featureNameIndecis = null;
    int order=10;
	/**
	 * Constructs an AreaMoments aggregator.  This isn't valid until specific features are adde to the system (in a particular order).
	 */
	public ZernikeMoments(){
        ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		metadata = new AggregatorDefinition("Zernike Moments", bundle.getString("calculates.the.first.39.2d.zernike.moments.for.the.given.features"),false,
                new String[] {bundle.getString("largest.order.number.of.zernike.moment.to.calculate") });
	}
	
	@Override
	public void aggregate(double[][][] values) {
		result = new double[zernikeCount(order)];
        int offset = super.calculateOffset(values,featureNameIndecis);
        int[][] featureIndecis = super.collapseFeatures(values,featureNameIndecis);
        int index=0;
        double[] powersOfP = new double[8];
        for(int i=offset;i<values.length;++i){
            for(int j=0;j<featureNameIndecis.length;++j){
                double base = Math.sqrt(Math.pow((2*((double)(i-offset))/((double)(values.length)))-1.0,2.0)+Math.pow((2*((double)j)/((double)(featureNameIndecis.length)))-1.0,2.0));
                double value=values[i][featureIndecis[j][0]][featureIndecis[j][1]];
                for(int p=0;p<powersOfP.length;++p){
                    powersOfP[p] += value;
                    value *= base;
                }
            }
        }
        while((index < result.length)&&(index<19)){
            switch(index){
                case 0:
                    result[index++] = powersOfP[1];
                    break;
                case 1:
                    result[index++] = 2*powersOfP[2]-powersOfP[0];
                    break;
                case 2:
                    result[index++] = powersOfP[2];
                    break;
                case 3:
                    result[index++] = 3*powersOfP[3]-2*powersOfP[1];
                    break;
                case 4:
                    result[index++] = powersOfP[3];
                    break;
                case 5:
                    result[index++] = 6*powersOfP[4] - 6*powersOfP[2]+powersOfP[0];
                    break;
                case 6:
                    result[index++] = 4*powersOfP[4] - 3*powersOfP[2];
                    break;
                case 7:
                    result[index++] = powersOfP[4];
                    break;
                case 8:
                    result[index++] = 10*powersOfP[5]-12*powersOfP[3]+3*powersOfP[1];
                    break;
                case 9:
                    result[index++] = 5*powersOfP[5]-4*powersOfP[3];
                    break;
                case 10:
                    result[index++] = powersOfP[5];
                    break;
                case 11:
                    result[index++] = 20*powersOfP[6]-30*powersOfP[4]+12*powersOfP[2]-powersOfP[0];
                    break;
                case 12:
                    result[index++] = 15*powersOfP[6]-20*powersOfP[4]+6*powersOfP[2];
                    break;
                case 13:
                    result[index++] = 6*powersOfP[6]-5*powersOfP[4];
                    break;
                case 14:
                    result[index++] = powersOfP[6];
                    break;
                case 15:
                    result[index++] = 35*powersOfP[7]-60*powersOfP[5]+30*powersOfP[3]-4*powersOfP[1];
                    break;
                case 16:
                    result[index++] = 21*powersOfP[7]-30*powersOfP[5]+10*powersOfP[3];
                    break;
                case 17:
                    result[index++] = 7*powersOfP[7] - 6*powersOfP[5];
                    break;
                case 18:
                    result[index++] = powersOfP[7];
                    break;
            }
        }
        for(int n=8;n<order;n+=1){
            for(int m=n;m>=0;m-=2){
                result[index]=0.0;
                for(int k=0;k<(n-m)/2;++k){
                    double constant = ((-1*(k%2))*factorial(n-k))/(factorial(k)*factorial(((n+m)/2)-k)*factorial(((n-m)/2)-k));
                    for(int i=offset;i<values.length;++i){
                        for(int j=0;j<featureIndecis.length;++j){
                            double radius = Math.sqrt(Math.pow((2.0*((double)(i-offset))/((double)(values.length)))-1.0,2.0)+Math.pow((2.0*((double)j)/((double)(featureIndecis.length)))-1.0,2.0));
                            result[index] += constant*Math.pow(radius, n-2*k);
                        }
                    }
                }
                index++;
            }
        }
	}

    protected double factorial(int order){
        double ret =1.0;
        for(int i=2;i<order;++i){
            ret *= i;
        }
        return ret;
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
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(bundle.getString("internal.error.agggregator.zernikemoments.number.of.feature.indeci.does.not.match.number.of.features1"));
		}
		this.featureNameIndecis = featureIndecis;
	}

    public int zernikeCount(int order){
        int ret = 0;
        for(int i=1;i<order;i+=1){
            ret += (i/2) + 1;
        }
        return ret;
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
        ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		definition = new FeatureDefinition("Zernike Moments: "+names,String.format(bundle.getString("2d.moments.constructed.from.features.s"),names),true,zernikeCount(order));
	}

    /**
     * Provide a list of the values of all parameters this aggregator uses.
     * Aggregators without parameters return null.
     *
     * @return list of the values of parmeters or null.
     */
    @Override
    public String[] getParamaters() {
        return new String[]{Integer.toString(order)};
    }

    @Override
    public Object clone() {
        ZernikeMoments ret = new ZernikeMoments();
        if(featureNameIndecis != null){
            ret.featureNameIndecis = featureNameIndecis.clone();
        }
        if (featureNames != null) {
            try {
                ret.setParameters(featureNames, new String[] { Integer
                        .toString(order) });
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return ret;
    }

}
