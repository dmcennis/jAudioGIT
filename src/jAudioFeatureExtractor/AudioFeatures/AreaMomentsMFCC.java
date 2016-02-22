package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

import java.util.ResourceBundle;

/**
 * This class implements 2D statistical methods of moments as implemented by
 * Fujinaga (1997). The number of consecutive windows that one can edit across
 * is an editable property. Furthermore, this classes window property is
 * affected by global window size changes.
 * <p>
 * Fujinaga, I. <i>Adaptive Optical Music Recognition</i>. PhD thesis, McGill
 * University, 1997.
 * 
 *  Created by Daniel McEnnis on August 18, 2010.
 *  Published under the LGPL license.  See most recent LGPL license on www.fsf.org
 *  for a copy of this license.
 *
 * @author Daniel McEnnis
 */
public class AreaMomentsMFCC extends FeatureExtractor {

	int lengthOfWindow = 10;

    int order = 10;

	/**
	 * Constructor that sets description, dependencies, and offsets from
	 * FeatureExtractor
	 */
	public AreaMomentsMFCC() {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");

		String name = bundle.getString("area.method.of.moments.of.mfccs");
		String description = bundle.getString("2d.statistical.method.of.moments.of.mfccs");
		String[] attributes = new String[] {bundle.getString("area.method.of.moments.window.length") };

		definition = new FeatureDefinition(name, description, true, 0,
				attributes);
		dependencies = new String[lengthOfWindow];
		for (int i = 0; i < dependencies.length; ++i) {
			dependencies[i] = "MFCC";
		}
		offsets = new int[lengthOfWindow];
		for (int i = 0; i < offsets.length; ++i) {
			offsets[i] = 0 - i;
		}

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
        double[] ret = new double[order*order];
        for (int i = 0; i < other_feature_values.length; ++i) {
            double row = (2.0*((double)i)/((double)(other_feature_values.length))) - 1.0;
            for (int j = 0; j < other_feature_values[i].length; ++j) {
                double column = (2.0*((double)j)/((double)(other_feature_values[0].length)))-1.0;
		double xpow = 1.0;
                for(int x=0;x<order;++x){
                    double ypow = 1.0;
                    for (int y=0;y<order;++y){
                        ret[order*x+y] = other_feature_values[i][j] * xpow * ypow;
                        ypow *= column;
                    }
                    xpow *= row;
                }
            }
        }

        return ret;
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
		if (n < 2) {
			throw new Exception(
					"Area Method of Moment's Window length must be two or greater");
		} else {
			lengthOfWindow = n;
			dependencies = new String[lengthOfWindow];
			offsets = new int[lengthOfWindow];
			for (int i = 0; i < lengthOfWindow; ++i) {
				dependencies[i] = "MFCC";
				offsets[i] = 0 - i;
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
        if (index > 1) {
            throw new Exception("INTERNAL ERROR: invalid index " + index
                    + " sent to AreaMoments:getElement");
        } else if (index == 1){
            return Integer.toString(order);
        } else{
            return Integer.toString(lengthOfWindow);
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
        if (index > 1) {
            throw new Exception("INTERNAL ERROR: invalid index " + index
                    + " sent to AreaMoments:setElement");
        } else if(index == 1){
            try {
                int type = Integer.parseInt(value);
                order = type;
            } catch (Exception e) {
                throw new Exception(
                        "Order of Area Method Of Moments must be an integer");
            }
        } else {
            try {
                int type = Integer.parseInt(value);
                setWindow(type);
            } catch (Exception e) {
                throw new Exception(
                        "Length of Area Method of Moments must be an integer");
            }
        }
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		AreaMomentsMFCC ret = new AreaMomentsMFCC();
		ret.lengthOfWindow = lengthOfWindow;
        ret.order=order;
		return ret;
	}

}
