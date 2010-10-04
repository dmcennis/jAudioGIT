/**
 * @author Daniel McEnnis
 *
 * This file is part of Solo695.
 *
 *   Solo695 is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   Solo695 is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MusicalBlackboard; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jAudioFeatureExtractor.AudioFeatures;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;

/**
 * A peak based calculation of smoothness. Caculated by evaluting the log of a
 * partial minus the average of the log of the surrounding partials. Is based
 * upon Stephan McAdams Spectral Smoothness in (McAdams 1999).
 * <p>
 * McAdams, S. 1999. Perspectives on the contribution of timbre to musical
 * structure. <i>Computer Music Journal</i>. 23(3):85-102.
 * 
 * @author Daniel McEnnis
 */
public class HarmonicSpectralSmoothness extends FeatureExtractor {

	/**
	 * Basic constructor that sets dependencies, definition, and offsets.
	 *
	 */
	public HarmonicSpectralSmoothness() {
		String name = "Peak Based Spectral Smoothness";
		String description = "Peak Based Spectral Smoothness is calculated from partials, not frequency bins. It is implemented accortding to McAdams 99 "
				+ System.getProperty("line.separator")
				+ System.getProperty("line.separator") + "McAdams, S. 1999. ";
		definition = new FeatureDefinition(name, description, true, 1);
		dependencies = new String[] { "Peak Detection" };
		offsets = new int[] { 0 };
	}

	/**
	 * Extract the peak based spectral smoothness from the window.
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
	 * @see jAudioFeatureExtractor.AudioFeatures.FeatureExtractor#extractFeature(double[],
	 *      double, double[][])
	 */
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) {
		double[] result = new double[1];
		double[] peak = other_feature_values[0];

		result[0] = 0.0;
		for (int i = 1; i < peak.length - 1; i++) {
			result[0] += Math.abs(20
					* Math.log(peak[i])
					- 20
					* (Math.log(peak[i - 1]) + Math.log(peak[i]) + Math
							.log(peak[i + 1])) / 3);
		}
		return result;
	}

	/**
	 * Proviede a complete copy of this feature. Used to implement the prottype
	 * pattern
	 */
	public Object clone() {
		return new HarmonicSpectralSmoothness();
	}
}
