//
//  ConstantQ.java
//  jAudio
//
//  Created by Daniel McEnnis on August 17, 2010.
//  Published under the LGPL license.  See most recent LGPL license on www.fsf.org
//  a copy of this license.
//

package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

/*
 * Constant Q
 * 
 * Transform from the time domain to the frequency domain that uses logarithmic bins. 
 * 
 * @author Daniel McEnnis
 */
public class ConstantQ extends FeatureExtractor
{

	int n;
	double alpha = 1.0;
	int nk[];
	double[] freq;
	double[][] kernelReal;
	double[][] kernelImaginary;
	
	/* CONSTRUCTOR **************************************************************/
	
	
	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public ConstantQ()
	{
		String name = "ConstantQ";
		String description = "signal to frequency transform using exponential-spaced frequency bins.";
		boolean is_sequential = true;
		int dimensions = 0;
		String[] attributes = new String[]{"Percent of a semitone per bin"};
		definition = new FeatureDefinition( name,
		                                    description,
		                                    is_sequential,
		                                    dimensions,
											attributes );

		dependencies = null;
		
		offsets = null;
		
		alpha=1.0;
		
	}


	/* PUBLIC METHODS **********************************************************/

	
	/**
	 * Extracts this feature from the given samples at the given sampling
	 * rate and given the other feature values.
	 *
	 * <p>In the case of this feature, the sampling_rate and 
	 * other_feature_values parameters are ignored.
	 *
	 * @param samples				The samples to extract the feature from.
	 * @param sampling_rate			The sampling rate that the samples are
	 *								encoded with.
	 * @param other_feature_values	The values of other features that are
	 *								needed to calculate this value. The
	 *								order and offsets of these features
	 *								must be the same as those returned by
	 *								this class's getDependencies and
	 *								getDependencyOffsets methods respectively.
	 *								The first indice indicates the feature/window
	 *								and the second indicates the value.
	 * @return						The extracted feature value(s).
	 * @throws Exception			Throws an informative exception if
	 *								the feature cannot be calculated.
	 */
	public double[] extractFeature( double[] samples,
	                                double sampling_rate,
	                                double[][] other_feature_values )
		throws Exception
	{
		calcFreq(samples,sampling_rate);
		calcNk(samples);
		calcKernels();
		double[] ret = new double[2*nk.length];
		java.util.Arrays.fill(ret,0.0);
		for(int bankCounter=0;bankCounter<(ret.length/2);++bankCounter){
			for(int i=0;i<nk[bankCounter];++i){
				ret[bankCounter] += kernelReal[bankCounter][i]*samples[i];
				ret[bankCounter+nk.length] += kernelImaginary[bankCounter][i]*samples[i];
			}
		}
		return ret;
	}
	
	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone(){
		return new ConstantQ();
	}

	private void calcFreq(double[] samples, double samplerate){
		double maxFreq = samplerate/2.0;
		double minFreq = samplerate/((double)samples.length);
		double carry = Math.log(maxFreq/minFreq);
		carry /= Math.log(2);
		carry *= 12/alpha;
		int numFields = (int)(Math.floor(carry));
		
		freq = new double[numFields];
		double currentFreq = minFreq;
		for(int i=0;i<numFields;++i){
			freq[i]=currentFreq;
			currentFreq = Math.pow(2,alpha/12.0);
		}
	}
	
	private void calcNk(double[] samples){
		nk = new int[freq.length];
		double windowLength=samples.length;
		for(int i=0;i<nk.length;++i){
			nk[0] = (int)Math.ceil(windowLength/(Math.pow(2,((double)i)*alpha/12)));
		}
	}

	private void calcKernels(){
		kernelReal = new double[nk.length][];
		kernelImaginary = new double[nk.length][];
		double q = Math.pow(2,alpha/12)-1;
		double hammingFactor = 25.0/46.0;
		for(int i=0;i<kernelReal.length;++i){
			kernelReal[i] = new double[nk[i]];
			kernelImaginary[i] = new double[nk[i]];
			for(int j=0;j<kernelReal[i].length;++j){
				kernelReal[i][j] = hammingFactor + (1-hammingFactor)*Math.cos(2.0*Math.PI*((double)j)/((double)nk[i]));
				kernelReal[i][j] /= ((double)nk[i]);
				kernelImaginary[i][j] = kernelReal[i][j];
				kernelReal[i][j] *= Math.cos(-2.0*Math.PI*q*j/((double)nk[i]));
				kernelImaginary[i][j] *= Math.sin(-2.0*Math.PI*q*j/((double)nk[i]));
			}
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
		switch (index) {
		case 0:
			return Double.toString(alpha);
		default:
			throw new Exception("INTERNAL ERROR: invalid index " + index
					+ " passed to LPC:getElement()");
		}
	}

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to set the default values used to popylate the table's entries.
	 * Like getElement, the correct index values are inferred from the
	 * definition.attributes value.
	 * 
	 * @param index
	 *            attribute to be set
	 * @param value
	 *            new value of the attribute
	 */
	public void setElement(int index, String value) throws Exception {
		switch (index) {
		case 0:
			try {
				double val = Double.parseDouble(value);
				if(val <= 0.0){
					throw new Exception("Alpha must be a positive value");
				}else{
					alpha = val;
				}
			} catch (NumberFormatException e) {
				throw new Exception("Alpha value must be a double");
			}
			break;
		default:
			throw new Exception(
					"INTERNAL ERROR: invalid index passed to ConstantQ:setElement");
		}
	}

}
