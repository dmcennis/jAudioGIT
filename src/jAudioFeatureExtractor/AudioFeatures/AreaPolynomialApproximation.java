package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

/**
 * 2D Polynomial Approximation Feature
 *
 * Creates a set of polynomial factors for a 2D polynomial of order k*l where k is the number of
 * terms in the x direction and l is the number of terms in the y direction.  The source is a 
 * square matrix of DSP data of some sort.  (This version is over FFT data.)  The output is a 
 * vector of the coeffecients of the polynomial that best fits this data.  
 * 
 * @author Daniel McEnnis
 */
public class AreaPolynomialApproximation extends FeatureExtractor {

	int windowLength=50;
	
	int featureLength=512;
	
	int k=5;
	
	int l=20;
	
	DenseDoubleMatrix2D terms;
	
	DenseDoubleMatrix2D z;
	
	/**
	 * Constructor that sets description, dependencies, and offsets from
	 * FeatureExtractor
	 */
	public AreaPolynomialApproximation() {
		String name = "2D Polynomial Approximation";
		String description = "coeffecients of 2D polynomial best describing the input matrtix.";
		String[] attributes = new String[] { "horizontal size (window length)",
											"vertical size (number of feature dimensions)",
											"number of x (horizontal) terms",
											"number of y (vertical) terms" };

		definition = new FeatureDefinition(name, description, true, 0,
				attributes);
				
		dependencies = new String[windowLength];
		for (int i = 0; i < dependencies.length; ++i) {
			dependencies[i] = "Magnitude Spectrum";
		}
		
		offsets = new int[windowLength];
		for (int i = 0; i < offsets.length; ++i) {
			offsets[i] = 0 - i;
		}
		
		terms = new DenseDoubleMatrix2D(k*l,windowLength*featureLength);
		z = new DenseDoubleMatrix2D(1,featureLength*windowLength);
		calcTerms(terms);
	}

	/**
	 * Calculates based on windows of magnitude spectrum. Encompasses portion of
	 * Moments class, but has a delay of lengthOfWindow windows before any
	 * results are calculated.
	 * 
	 * @param samples
	 *            The samples to extract the feature from.
	 * @param sampling_rate
	 *            The sampling rate that the samples are encoded with.
	 * @param other_feature_values
	 *            The values of other features that are needed to calculate this
	 *            value. The order and offsets of these features must be the
	 *            same as those returned by this class's getDependencies and
	 *            getDependencyOffsets methods respectively. The first indice
	 *            indicates the feature/window and the second indicates the
	 *            value.
	 * @return The extracted feature value(s).
	 * @throws Exception
	 *             Throws an informative exception if the feature cannot be
	 *             calculated.
	 */
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) throws Exception {
		if((featureLength != other_feature_values[0].length)||(windowLength != other_feature_values.length)){
			terms = new DenseDoubleMatrix2D(k*l,windowLength*featureLength);
			z = new DenseDoubleMatrix2D(1,featureLength*windowLength);
			calcTerms(terms);
		}
		for(int i=0;i<windowLength;++i){
			for(int j=0;j<featureLength;++j){
				z.set(0,windowLength*i+j,other_feature_values[i][j]);
			}
		}
		DoubleMatrix2D retMatrix = (new Algebra()).solve(terms,z);
		return retMatrix.viewRow(0).toArray();
	}

	/**
	 * Function that must be overridden to allow this feature to be set globally
	 * by GlobalChange frame.
	 * 
	 * @param n
	 *            the number of windows of offset to be used in calculating this
	 *            feature
	 */
	public void setWindow(int n) throws Exception {
		if (n < 1) {
			throw new Exception(
					"Area Polynomial Approximation window length must be positive");
		} else {
			windowLength = n;
			dependencies = new String[windowLength];
			offsets = new int[windowLength];
			for (int i = 0; i < windowLength; ++i) {
				dependencies[i] = "Magnitude Spectrum";
				offsets[i] = 0 - i;
			}
			terms = new DenseDoubleMatrix2D(k*l,windowLength*featureLength);
			z = new DenseDoubleMatrix2D(1,featureLength*windowLength);
			calcTerms(terms);
		}
	}

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to get the default values used to populate the table's entries.
	 * The correct index values are inferred from definition.attribute value.
	 * 
	 * @param index
	 *            which of AreaMoment's attributes should be edited.
	 */
	public String getElement(int index) throws Exception {
		switch (index){
			case 0:
				// get windowLength
				return Integer.toString(windowLength);
			
			case 1:
				// get featureLength
				return Integer.toString(featureLength);
				
			case 2:
				// get number of x terms
				return Integer.toString(k);
				
			case 3:
				// get number of y terms
				return Integer.toString(k);
				
			default:
				// get number of y terms
				throw new Exception("INTERNAL ERROR: invalid index " + index
					+ " sent to AreaPolynomialApproximation:getElement");
		}
	}

	/**
	 * Function permitting an unintelligent outside function (i.e. EditFeatures
	 * frame) to set the default values used to populate the table's entries.
	 * Like getElement, the correct index values are inferred from the
	 * definition.attributes value.
	 * 
	 * @param index
	 *            attribute to be set
	 * @param value
	 *            new value of the attribute
	 */
	public void setElement(int index, String value) throws Exception {
		switch (index){
			case 0:
				// get windowLength
			try {
				int val = Integer.parseInt(value);
				if (val < 1) {
					throw new Exception(
						"Area Polynomial Approximation window length must be positive");
				} else {
					windowLength = val;
					dependencies = new String[windowLength];
					offsets = new int[windowLength];
					for (int i = 0; i < windowLength; ++i) {
						dependencies[i] = "Magnitude Spectrum";
						offsets[i] = 0 - i;
					}
					terms = new DenseDoubleMatrix2D(k*l,windowLength*featureLength);
					z = new DenseDoubleMatrix2D(1,featureLength*windowLength);
					calcTerms(terms);
				}
			} catch (Exception e) {
				throw new Exception(
						"horizontal (windowLength) of Area Polynomial Approximation must be an integer");
			}
			break;
			
			case 1:
				// get featureLength
			try {
				int val = Integer.parseInt(value);
				if (val < 1) {
					throw new Exception(
						"Area Polynomial Approximation feature dimension length must be positive");
				} else {
					featureLength = val;
					terms = new DenseDoubleMatrix2D(k*l,windowLength*featureLength);
					z = new DenseDoubleMatrix2D(1,featureLength*windowLength);
					calcTerms(terms);
				}
			} catch (Exception e) {
				throw new Exception(
						"vertical (feature dimensions) of Area Polynomial Approximation must be an integer");
			}
			break;
				
			case 2:
				// get number of x terms
			try {
				int val = Integer.parseInt(value);
				if (val < 1) {
					throw new Exception(
						"Number of x terms in Area Polynomial Approximation must be positive");
				} else {
					k = val;
					terms = new DenseDoubleMatrix2D(k*l,windowLength*featureLength);
					z = new DenseDoubleMatrix2D(1,featureLength*windowLength);
					calcTerms(terms);
				}
			} catch (Exception e) {
				throw new Exception(
						"Number of x terms in Area Polynomial Approximation must be an integer");
			}
			break;
				
			case 3:
				// get number of y terms
			try {
				int val = Integer.parseInt(value);
				if (val < 1) {
					throw new Exception(
						"Number of y terms in Area Polynomial Approximation must be positive");
				} else {
					l = val;
					terms = new DenseDoubleMatrix2D(k*l,windowLength*featureLength);
					z = new DenseDoubleMatrix2D(1,featureLength*windowLength);
					calcTerms(terms);
				}
			} catch (Exception e) {
				throw new Exception(
						"Number of y terms of Area Polynomial Approximation must be an integer");
			}
			break;
				
			default:
				throw new Exception("INTERNAL ERROR: invalid index " + index
					+ " sent to AreaPolynomialApproximation:getElement");
		}
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		AreaPolynomialApproximation ret = new AreaPolynomialApproximation();
		return ret;
	}
	
	private void calcTerms(DoubleMatrix2D terms){
		terms.assign(0.0);
		for(int x=0;x<windowLength;++x){
			for(int y=0;y<featureLength;++y){
				for(int i=0;i<k;++i){
					for(int j=0;j<l;++j){
						terms.set(l*i+j,featureLength*x+y,Math.pow(x,i)*Math.pow(y,j));
					}
				}
			}
		}
	}
	
}
