/*
OC Volume - Java Speech Recognition Engine
Copyright (c) 2002-2004, OrangeCow organization
All rights reserved.

Redistribution and use in source and binary forms,
with or without modification, are permitted provided
that the following conditions are met:

* Redistributions of source code must retain the
  above copyright notice, this list of conditions
  and the following disclaimer.
* Redistributions in binary form must reproduce the
  above copyright notice, this list of conditions
  and the following disclaimer in the documentation
  and/or other materials provided with the
  distribution.
* Neither the name of the OrangeCow organization
  nor the names of its contributors may be used to
  endorse or promote products derived from this
  software without specific prior written
  permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

Contact information:
Please visit http://ocvolume.sourceforge.net.
*/

package org.oc.ocvolume.dsp;

/**
 * last updated on June 15, 2002<br>
 * <b>description:</b> feature extraction class used to extract mel-frequency cepstral coefficients from input signal<br>
 * <b>calls:</b> none<br>
 * <b>called by:</b> volume, train<br>
 * <b>input:</b> speech signal<br>
 * <b>output:</b> mel-frequency cepstral coefficient
 * @author Danny Su
 */
public class featureExtraction{
    /**
     * sample rate in Hz
     */
   // protected final static double samplingRate = 16000.0;
    /**
     * Number of samples per frame
     */
    protected final static int frameLength = 512;
    /**
     * Number of overlapping samples (usually 50% of frame length)
     */
    protected final static int shiftInterval = frameLength / 2;
    /**
     * Number of MFCCs per frame
     * Modifed 4/5/06 to be non final variable - Daniel McEnnnis
     */
    public int numCepstra = 13;
    /**
     * FFT Size (Must be be a power of 2)
     */
    protected final static int fftSize = frameLength;
    /**
     * Pre-Emphasis Alpha (Set to 0 if no pre-emphasis should be performed)
     */
    protected final static double preEmphasisAlpha = 0.95;
    /**
     * lower limit of filter (or 64 Hz?)
     */
    protected final static double lowerFilterFreq = 133.3334;
    /**
     * upper limit of filter (or half of sampling freq.?)
     */
    protected final static double upperFilterFreq = 6855.4976;
    /**
     * number of mel filters (SPHINX-III uses 40)
     */
    protected final static int numMelFilters = 23;
    /**
     * All the frames of the input signal
     */
    protected double frames[][];
    /**
     * hamming window values
     */
    protected double hammingWindow[];
    /**
     * Fast Fourier Transformation
     */
    protected fft FFT;
    /**
     * takes a speech signal and returns the Mel-Frequency Cepstral Coefficient (MFCC)<br>
     * calls: fft<br>
     * called by: volume, train
     * 
     * 5-3-05 Daniel McEnnis - paramatrized sampling rate.
     * 
     * @param inputSignal Speech Waveform (16 bit integer data)
     * @return Mel Frequency Cepstral Coefficients (32 bit floating point data)
     */
    public double[][] process(short inputSignal[],double samplingRate){
        double MFCC[][];

        // Pre-Emphasis
        double outputSignal[] = preEmphasis(inputSignal);
        
        // Frame Blocking
        framing(outputSignal);

        // Initializes the MFCC array
        MFCC = new double[frames.length][numCepstra];

        // apply Hamming Window to ALL frames
        hammingWindow();
        
        //
        // Below computations are all based on individual frames with Hamming Window already applied to them
        //
        for (int k = 0; k < frames.length; k++){
            FFT = new fft();
            
            // Magnitude Spectrum
            double bin[] = magnitudeSpectrum(frames[k]);

            // Mel Filtering
            int cbin[] = fftBinIndices(samplingRate,512);
            // get Mel Filterbank
            double fbank[] = melFilter(bin, cbin);

            // Non-linear transformation
            double f[] = nonLinearTransformation(fbank);

            // Cepstral coefficients
            double cepc[] = cepCoefficients(f);

            // Add resulting MFCC to array
            for (int i = 0; i < numCepstra; i++){
                MFCC[k][i] = cepc[i];
            }
        }

        return MFCC;
    }
    /**
     * calculates the FFT bin indices<br>
     * calls: none<br>
     * called by: featureExtraction
     * 
     * 5-3-05 Daniel MCEnnis paramaterize sampling rate and frameSize
     * 
     * @return array of FFT bin indices
     */
    public int[] fftBinIndices(double samplingRate,int frameSize){
        int cbin[] = new int[numMelFilters + 2];
        
        cbin[0] = (int)Math.round(lowerFilterFreq / samplingRate * frameSize);
        cbin[cbin.length - 1] = (int)(frameSize / 2);
        
        for (int i = 1; i <= numMelFilters; i++){
            double fc = centerFreq(i,samplingRate);

            cbin[i] = (int)Math.round(fc / samplingRate * frameSize);
        }
        
        return cbin;
    }
    /**
     * Calculate the output of the mel filter<br>
     * calls: none
     * called by: featureExtraction
     */
    public double[] melFilter(double bin[], int cbin[]){
        double temp[] = new double[numMelFilters + 2];

        for (int k = 1; k <= numMelFilters; k++){
            double num1 = 0, num2 = 0;

            for (int i = cbin[k - 1]; i <= cbin[k]; i++){
                num1 += ((i - cbin[k - 1] + 1) / (cbin[k] - cbin[k-1] + 1)) * bin[i];
            }

            for (int i = cbin[k] + 1; i <= cbin[k + 1]; i++){
                num2 += (1 - ((i - cbin[k]) / (cbin[k + 1] - cbin[k] + 1))) * bin[i];
            }

            temp[k] = num1 + num2;
        }

        double fbank[] = new double[numMelFilters];
        for (int i = 0; i < numMelFilters; i++){
            fbank[i] = temp[i + 1];
        }

        return fbank;
    }
    /**
     * Cepstral coefficients are calculated from the output of the Non-linear Transformation method<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param f Output of the Non-linear Transformation method
     * @return Cepstral Coefficients
     */
    public double[] cepCoefficients(double f[]){
        double cepc[] = new double[numCepstra];
        
        for (int i = 0; i < cepc.length; i++){
            for (int j = 1; j <= numMelFilters; j++){
                cepc[i] += f[j - 1] * Math.cos(Math.PI * i / numMelFilters * (j - 0.5));
            }
        }
        
        return cepc;
    }
    /**
     * the output of mel filtering is subjected to a logarithm function (natural logarithm)<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param fbank Output of mel filtering
     * @return Natural log of the output of mel filtering
     */
    public double[] nonLinearTransformation(double fbank[]){
        double f[] = new double[fbank.length];
        final double FLOOR = -50;
        
        for (int i = 0; i < fbank.length; i++){
            f[i] = Math.log(fbank[i]);
            
            // check if ln() returns a value less than the floor
            if (f[i] < FLOOR) f[i] = FLOOR;
        }
        
        return f;
    }
    /**
     * calculates logarithm with base 10<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param value Number to take the log of
     * @return base 10 logarithm of the input values
     */
    protected static double log10(double value){
        return Math.log(value) / Math.log(10);
    }
    /**
     * calculates center frequency<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param i Index of mel filters
     * @return Center Frequency
     */
    private static double centerFreq(int i,double samplingRate){
        double mel[] = new double[2];
        mel[0] = freqToMel(lowerFilterFreq);
        mel[1] = freqToMel(samplingRate / 2);
        
        // take inverse mel of:
        double temp = mel[0] + ((mel[1] - mel[0]) / (numMelFilters + 1)) * i;
        return inverseMel(temp);
    }
    /**
     * calculates the inverse of Mel Frequency<br>
     * calls: none<br>
     * called by: featureExtraction
     */
    private static double inverseMel(double x){
        double temp = Math.pow(10, x / 2595) - 1;
        return 700 * (temp);
    }
    /**
     * convert frequency to mel-frequency<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param freq Frequency
     * @return Mel-Frequency
     */
    protected static double freqToMel(double freq){
        return 2595 * log10(1 + freq / 700);
    }
    /**
     * computes the magnitude spectrum of the input frame<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param frame Input frame signal
     * @return Magnitude Spectrum array
     */
    public double[] magnitudeSpectrum(double frame[]){
        double magSpectrum[] = new double[frame.length];
        
        // calculate FFT for current frame
        fft.computeFFT( frame );
        
        // calculate magnitude spectrum
        for (int k = 0; k < frame.length; k++){
            magSpectrum[k] = Math.pow(fft.real[k] * fft.real[k] + fft.imag[k] * fft.imag[k], 0.5);
        }

        return magSpectrum;
    }
    /**
     * performs Hamming Window<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param frame A frame
     * @return Processed frame with hamming window applied to it
     */
    private void hammingWindow(){
        double w[] = new double[frameLength];
        for (int n = 0; n < frameLength; n++){
            w[n] = 0.54 - 0.46 * Math.cos( (2 * Math.PI * n) / (frameLength - 1) );
        }

        for (int m = 0; m < frames.length; m++){
            for (int n = 0; n < frameLength; n++){
                frames[m][n] *= w[n];
            }
        }
    }
    /**
     * performs Frame Blocking to break down a speech signal into frames<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param inputSignal Speech Signal (16 bit integer data)
     */
    protected void framing(double inputSignal[]){
        double numFrames = (double)inputSignal.length / (double)(frameLength - shiftInterval);
        
        // unconditionally round up
        if ((numFrames / (int)numFrames) != 1){
            numFrames = (int)numFrames + 1;
        }
        
        // use zero padding to fill up frames with not enough samples
        double paddedSignal[] = new double[(int)numFrames * frameLength];
        for (int n = 0; n < inputSignal.length; n++){
            paddedSignal[n] = inputSignal[n];
        }

        frames = new double[(int)numFrames][frameLength];

        // break down speech signal into frames with specified shift interval to create overlap
        for (int m = 0; m < numFrames; m++){
            for (int n = 0; n < frameLength; n++){
                frames[m][n] = paddedSignal[m * (frameLength - shiftInterval) + n];
            }
        }
    }
    /**
     * perform pre-emphasis to equalize amplitude of high and low frequency<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param inputSignal Speech Signal (16 bit integer data)
     * @return Speech signal after pre-emphasis (16 bit integer data)
     */
    protected static double[] preEmphasis(short inputSignal[]){
        double outputSignal[] = new double[inputSignal.length];
        
        // apply pre-emphasis to each sample
        for (int n = 1; n < inputSignal.length; n++){
            outputSignal[n] = inputSignal[n] - preEmphasisAlpha * inputSignal[n - 1];
        }
        
        return outputSignal;
    }
}