//
//  ConstantQ.java
//  jAudio0.4.5.1
//
//  Created by Daniel McEnnis on August 18, 2010.
//  Published under the LGPL license.  See most recent LGPL license on www.fsf.org
//  a copy of this license.
//

package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

/*
 * Contant Q MFCCs
 *
 * Implements MFCCs using the log constant q function.  Produces MFCC's without the error of 
 * rebinning linear bins to logarithmic bins.
 *
 * @author Daniel McEnnis
 */
public class ConstantQMFCC extends FeatureExtractor
{

	int numCepstra=13;
	
	/* CONSTRUCTOR **************************************************************/
	
	
	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public ConstantQMFCC()
	{
		String name = "ConstantQ derived MFCCs";
		String description = "MFCCs directly caluclated from ConstantQ exponential bins";
		boolean is_sequential = true;
		int dimensions = 0;
		String[] attributes = new String[]{"Number of cepstra to return"};
		definition = new FeatureDefinition( name,
		                                    description,
		                                    is_sequential,
		                                    dimensions,
											attributes );

		dependencies = new String[]{"Log of ConstantQ"};
		
		offsets = new int[]{0};
		
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
		return cepCoefficients(other_feature_values[0]);
	}
	
	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone(){
		return new ConstantQ();
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
			return Integer.toString(numCepstra);
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
				int val = Integer.parseInt(value);
				if(val <= 0.0){
					throw new Exception("Alpha must be a positive value");
				}else{
					numCepstra = val;
				}
			} catch (NumberFormatException e) {
				throw new Exception("Lambda value must be a double");
			}
			break;
		default:
			throw new Exception(
					"INTERNAL ERROR: invalid index passed to ConstantQ:setElement");
		}
	}

    /**
	 * Borrowed from Orange Cow MFCC implementation (BSD)
     * Cepstral coefficients are calculated from the output of the Non-linear Transformation method<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param f Output of the Non-linear Transformation method
     * @return Cepstral Coefficients
     */
    public double[] cepCoefficients(double f[]){
        double cepc[] = new double[numCepstra];
        
        for (int i = 0; i < cepc.length; i++){
            for (int j = 1; j <= f.length; j++){
                cepc[i] += f[j - 1] * Math.cos(Math.PI * i / f.length * (j - 0.5));
            }
        }
        
        return cepc;
    }
}
