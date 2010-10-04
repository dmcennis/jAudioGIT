/**
 * MFCC aggregator
 * Created for the 2006 ISMIR jAudio release
 * Created by Daniel McEnnis
 */
package jAudioFeatureExtractor.Aggregators;

import jAudioFeatureExtractor.ACE.DataTypes.AggregatorDefinition;
import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;
import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.GeneralTools.Statistics;
import jAudioFeatureExtractor.GeneralTools.StringMethods;

import java.io.DataOutputStream;

import org.oc.ocvolume.dsp.featureExtraction;
import org.oc.ocvolume.dsp.fft;

/**
 * MFCC Aggregator
 * 
 * MFCC Aggregator is a general aggregator that produces the fist 10 MFCCs of each feature dimension independently.
 * Treats a signal as a 16 KHz signal, then calculates the MFCC's of this signal.
 *
 * @author Daniel McEnnis
 * 
 */
public class MFCC extends Aggregator {

	featureExtraction fe = new featureExtraction();

	int index = -1;
	
	/**
	 * Constructs a MFCC aggregator
	 */
	public MFCC() {
		metadata = new AggregatorDefinition("MFCC","Treats the window-by-window data as a 16kHz signal",true,null);
	}

	@Override
	public void aggregate(double[][][] values) {
		fe.numCepstra = 4;
		int valuesOffset = 0;
		while((valuesOffset<values.length)&&(values[valuesOffset][index]==null)){
			valuesOffset++;
		}
		
		//Handle Degenerate case here
		if(valuesOffset >= values.length){
			result = new double[definition.dimensions*4];
			for(int i=0;i<result.length;++i){
				result[i] = 0.0;
			}
		}else{
			result = new double[values[values.length-1][index].length*4];
			definition.dimensions = result.length;
			// get needed power of two array length for FFT.
			int size = Statistics.ensureIsPowerOfN(values.length-valuesOffset,2);
			double[] fftArray = new double[size];		
			java.util.Arrays.fill(fftArray,0.0);
			for(int i=0;i<values[values.length-1][index].length;++i){
				// build the next fft array
				java.util.Arrays.fill(fftArray,0.0);
				for(int fftArrayIndex = 0; fftArrayIndex+valuesOffset < values.length;++fftArrayIndex){
					fftArray[fftArrayIndex]=values[fftArrayIndex+valuesOffset][index][i];
				}
				
				fft data = new fft();
				
		        double magSpectrum[] = new double[fftArray.length];
		        
		        // calculate FFT for current frame
		        fft.computeFFT( fftArray );
		        
		        // calculate magnitude spectrum
		        for (int j = 0; j < fftArray.length; j++){
		            magSpectrum[j] = Math.pow(fft.real[j] * fft.real[j] + fft.imag[j] * fft.imag[j], 0.5);
		        }

				int[] cbin = fe.fftBinIndices(16000,
						magSpectrum.length);
				double[] fbank = fe.melFilter(magSpectrum,
						cbin);
				double[] f = fe.nonLinearTransformation(fbank);
				double[] cepc = fe.cepCoefficients(f);
				for(int j=0;j<cepc.length;++j){
					result[i*4+j] = cepc[j];
				}

			}

		}
	}

	@Override
	public Object clone() {
		return new MFCC();
	}

	@Override
	public String[] getFeaturesToApply() {
		return null;
	}

	@Override
	public void init(int[] featureIndecis) throws Exception {
		index = featureIndecis[0];
	}

	@Override
	public void setSource(FeatureExtractor feature) {
		FeatureDefinition source = feature.getFeatureDefinition();
		definition = new FeatureDefinition("MFCC: " + source.name,
				source.description + System.getProperty("line.separator")
						+ "MFCC of each dimension of this feature",
				source.is_sequential, source.dimensions * 4);

	}

}
