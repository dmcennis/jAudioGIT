package org.jaudio.features;

import org.jaudio.Buffer;
import org.jaudio.FeatureIn;
import org.jaudio.FeatureOut;
import org.jaudio.SynchronizedBuffer;
import org.oc.ocvolume.dsp.featureExtraction;

public class MFCC extends Thread implements FeatureIn, FeatureOut {

	int inputWindowSize = 0;
	
	int outputWindowSize;
	
	
	SynchronizedBuffer inBuffer = null;
	
	String name = "MFCC";
	
	Integer inputMonitor = new Integer(0);
	
	Integer outputMonitor = new Integer(0);
	
	SynchronizedBuffer out = new SynchronizedBuffer(this,0);
	
	long windowCounter=0;	
	
	double[] data = new double[0];

	featureExtraction fe = new featureExtraction();
	
	public MFCC(int numCoeffecients){
		outputWindowSize = numCoeffecients;
		fe.numCepstra=numCoeffecients;
		out = new SynchronizedBuffer(this,0);
	}
	
	@Override
	public void notifyPutReady() {
		synchronized(outputMonitor){
			outputMonitor.notifyAll();
		}

	}

	@Override
	public SynchronizedBuffer register(FeatureIn in) {
		out.registerListener(in);
		return out;
	}

	@Override
	public int windowOffset() {
		return 0;
	}

	@Override
	public int getInputWindowSize(SynchronizedBuffer buffer) {
		return inputWindowSize;
	}

	@Override
	public int getOutputWindowSize() {
		return outputWindowSize;
	}

	@Override
	public void doGet(Buffer buff) {
		synchronized(inputMonitor){
			inputMonitor.notifyAll();
		}

	}

	@Override
	public void setWindowSize(int windowSize,SynchronizedBuffer buffer) {
		inputWindowSize = windowSize;
		data = new double[windowSize];
	}

	@Override
	public void setBuffer(SynchronizedBuffer buffer, FeatureOut out) {
		inBuffer = buffer;
		setWindowSize(buffer.getWindowSize(),buffer);
	}

	@Override
	public String getNameID() {
		return name;
	}

	@Override
	public long getWindowCount() {
		return windowCounter;
	}

	@Override
	public void setNameID(String name) {
		this.name = name;
		
	}

	@Override
	public void run() {
			while(!inBuffer.isEof()){
				if(inBuffer.lock(data, this, null, true)){
					double[] result = getMFCC();
//					System.out.println("Result Size:"+result.length);
//					System.out.flush();
					while(!out.lock(result, null, this, false)){
						synchronized(outputMonitor){
							try {
//								outputMonitor.wait();
								Thread.sleep(2);
//								System.out.print("w");
//								System.out.flush();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}else{
					if(!inBuffer.isEof()){
						synchronized(inputMonitor){
							try {
//								inputMonitor.wait();
								Thread.sleep(2);
//								System.out.print("r");
//								System.out.flush();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			if(inBuffer.lock(data, this, null, true)){
				double[] result = getMFCC();
//				System.out.println("Result Size:"+result.length);
//				System.out.flush();
				while(!out.lock(result, null, this, false)){
					synchronized(outputMonitor){
						try {
//							outputMonitor.wait();
							Thread.sleep(2);
//							System.out.print("w");
//							System.out.flush();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
//			System.out.println("MFCC Done");
//			System.out.flush();
			out.setEof(true);
			
		}

		private double[] getMFCC(){
			int[] cbin = fe.fftBinIndices(44100,
					data.length);
			double[] fbank = fe.melFilter(data,
					cbin);
			double[] f = fe.nonLinearTransformation(fbank);
			double[] cepc = fe.cepCoefficients(f);
			return cepc;
		}
}
