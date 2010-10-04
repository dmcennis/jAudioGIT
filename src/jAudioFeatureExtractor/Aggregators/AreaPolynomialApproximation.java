/**
 * AreaPolynomialApproximation
 * created August 21, 2010
 * Author: Daniel McEnnis
 * Published under the LGPL see license.txt or at http://www.fsf.org
 * Utilizes the colt matrix package under either the LGPL or BSD license (see colt's online documentation for specifics).
 */
package jAudioFeatureExtractor.Aggregators;

import jAudioFeatureExtractor.ACE.DataTypes.AggregatorDefinition;
import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

/**
<h2>2D Polynomial Approximation</h2>
<p>This specific aggregator was first released in an August 2010 working paper by Daniel McEnnis.  It transforms
a 2D matrix of signal feature vectors into a set of coeffecients of the polynomial function f(x,y) that bests matches the 
given signal. It is calucalated by constructing a matrix of coeffecients by substituting concrete data points into each
term that coeffecient is attached to (such as x^2*y^3) and a answer matrix is created from the signal at 
that matrix index.  The coeffecients are then calculated by a least squares minimization matrix solver (colt matrix function).</p>
 * <p></p>
 * Utuilizes the Colt java matrix package.
 * @author Daniel McEnnis
 *
 */
public class AreaPolynomialApproximation extends Aggregator {

	int xDim=20;
	int yDim=5;	
	
	int windowLength=1;
	int featureLength=1;
	
	DenseDoubleMatrix2D terms;
	
	DenseDoubleMatrix2D z;

	String[] featureNames = null;
	int[] featureNameIndecis = null;
	
	public AreaPolynomialApproximation(){
		metadata = new AggregatorDefinition("2D Polynomial Approximation of a signal","Calculates the coeefecients of a polynomial that approximates the signal",false,null);
	}
	
	@Override
	public void aggregate(double[][][] values) {
		result = null;
		int offset = super.calculateOffset(values,featureNameIndecis);
		int[][] featureIndecis = super.collapseFeatures(values,featureNameIndecis);
		result[0] = 0.0;
		windowLength = featureNameIndecis.length-offset;
		featureLength = featureIndecis[0].length;
		for (int i=offset;i<values.length;++i){
			for(int j=0;j<featureIndecis.length;++j){
				result[0] += values[i][featureIndecis[j][0]][featureIndecis[j][1]];
			}
		}		
		terms = new DenseDoubleMatrix2D(xDim*yDim,windowLength*featureLength);
		z = new DenseDoubleMatrix2D(1,featureLength);
		calcTerms(terms);
		result = ((new Algebra()).solve(terms,z)).viewRow(0).toArray();
	}

	@Override
	public Object clone() {
		AreaPolynomialApproximation ret = new AreaPolynomialApproximation();
		if(featureNames != null){
			ret.featureNames = featureNames.clone();
		}
		if(featureNameIndecis != null){
			ret.featureNameIndecis = featureNameIndecis.clone();
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
			throw new Exception("INTERNAL ERROR (Agggregator.AreaPolynomialApproximation): number of feature indeci does not match number of features");
		}
		this.featureNameIndecis = featureIndecis;
	}

	/* (non-Javadoc)
	 * @see jAudioFeatureExtractor.Aggregators.Aggregator#getParamaters()
	 */
	@Override
	public String[] getParamaters() {
		return new String[]{Integer.toString(xDim),Integer.toString(yDim)};
	}
	
	private void calcTerms(DoubleMatrix2D terms){
		terms.assign(0.0);
		for(int x=0;x<windowLength;++x){
			for(int y=0;y<featureLength;++y){
				for(int i=0;i<xDim;++i){
					for(int j=0;j<yDim;++j){
						terms.set(yDim*i+j,featureLength*x+y,Math.pow(x,i)*Math.pow(y,j));
					}
				}
			}
		}
	}

	@Override
	public void setParameters(String[] featureNames, String[] params) throws Exception {
				// get number of x terms
			if (params.length != 2){
				xDim = 20;
				yDim = 5;
			}else{
				try {
					int val = Integer.parseInt(params[0]);
					if (val < 1) {
						throw new Exception(
							"Number of x terms in Area Polynomial Approximation must be positive");
					} else {
						xDim = val;
					}
				} catch (Exception e) {
					throw new Exception(
							"Number of x terms in Area Polynomial Approximation must be an integer");
				}
				
					// get number of y terms
				try {
					int val = Integer.parseInt(params[1]);
					if (val < 1) {
						throw new Exception(
							"Number of y terms in Area Polynomial Approximation must be positive");
					} else {
						yDim = val;
					}
				} catch (Exception e) {
					throw new Exception(
							"Number of y terms of Area Polynomial Approximation must be an integer");
				}
			}
		this.featureNames = featureNames;
		String names = featureNames[0];
		for(int i=1;i<featureNames.length;++i){
			names += " " + featureNames[i];
		}
		definition = new FeatureDefinition("2D Polynomial Approximation: "+names,"2D moments constructed from features "+names+".",true,0);
	}
}
