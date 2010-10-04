package jAudioFeatureExtractor.AudioFeatures;

import org.oc.ocvolume.dsp.featureExtraction;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

/**
 * Utilizes the MFCC code from the OrangeCow Volume project.
 * <p>
 * S. Pfeiffer, and C. Parker, and T. Vincent. 2005. <i>OC volume: Java speech
 * recognition engine</i>. 2005. [cited April 14, 2005].
 * 
 * @author Daniel McEnnis
 */
public class MFCC extends FeatureExtractor {

	featureExtraction fe;
	
	/**
	 * Construct a MFCC object, setting definition, dependencies, and offsets.
	 */
	public MFCC() {
		String name = "MFCC";
		String description = "MFCC calculations based upon Orange Cow code";
		String[] attributes = new String[]{"Number of Coeffecients"};
		definition = new FeatureDefinition(name, description, true, 13,attributes);
		dependencies = new String[] { "Magnitude Spectrum" };
		offsets = new int[] { 0 };
		fe = new featureExtraction();
	}

	/**
	 * Calculate Mel Frequency Cepstrum Coeffecients from the magnitude spectrum
	 * of a signal
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

		int[] cbin = fe.fftBinIndices(sampling_rate,
				other_feature_values[0].length);
		double[] fbank = fe.melFilter(other_feature_values[0],
				cbin);
		double[] f = fe.nonLinearTransformation(fbank);
		double[] cepc = fe.cepCoefficients(f);
		return cepc;
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		return new MFCC();
	}

	@Override
	public String getElement(int index) throws Exception {
		switch (index) {
		case 0:
			return Integer.toString(fe.numCepstra);
		default:
			throw new Exception("INTERNAL ERROR: invalid index " + index
					+ " passed to LPC:getElement()");
		}
	}

	@Override
	public void setElement(int index, String value) throws Exception {
		switch (index) {
		case 0:
			try {
				fe.numCepstra = Integer.parseInt(value);
				String name = definition.name;
				String description = definition.description;
				String[] attributes = definition.attributes;
				definition = new FeatureDefinition(name, description, true,
						fe.numCepstra, attributes);
				if (parent != null) {
					parent.updateTable();
				}
			} catch (NumberFormatException e) {
				throw new Exception("Lambda value must be a double");
			}
			break;
		default:
			throw new Exception(
					"INTERNAL ERROR: invalid index passed to LPC:setElement");
		}
	}

}
