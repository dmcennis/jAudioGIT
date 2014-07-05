package org.jaudio.features;

import org.jaudio.Buffer;
import org.jaudio.FeatureIn;
import org.jaudio.FeatureOut;
import org.jaudio.SynchronizedBuffer;


public class FFT extends Thread implements FeatureIn, FeatureOut {

	int inputWindowSize = 512;
	
	int outputWindowSize = 256;
	
	
	SynchronizedBuffer inBuffer = null;
	
	String name = "Output Eater";
	
	Integer inputMonitor = new Integer(0);
	
	Integer outputMonitor = new Integer(0);
	
	SynchronizedBuffer out = new SynchronizedBuffer(this,0);
	
	long windowCounter=0;	
	
	double[] data = new double[512];

	@Override
	public void notifyPutReady() {
		synchronized(outputMonitor){
			outputMonitor.notifyAll();
		}

	}

	@Override
	public SynchronizedBuffer register(FeatureIn in) {
		out.registerListener(in);
//		System.out.println("FFT OutputBuffer Length:"+out.getWindowSize());
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
		this.inputWindowSize = windowSize;
		data = new double[windowSize];
	}

	@Override
	public void setBuffer(SynchronizedBuffer buffer,FeatureOut out) {
		inBuffer = buffer;

	}

	@Override
	public String getNameID() {
		return "FFT Magnitude";
	}

	@Override
	public long getWindowCount() {
		return windowCounter;
	}

	@Override
	public void run() {
		while(!inBuffer.isEof()){
			if(inBuffer.lock(data, this, null, true)){
				double[] result = getFFT();
//				System.out.println("Result Size:"+result.length);
//				System.out.flush();
				while(!out.lock(result, null, this, false)){
					synchronized(outputMonitor){
						try {
							outputMonitor.wait();
//							Thread.sleep(2);
//							System.out.print("w");
//							System.out.flush();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}else{
				if(!inBuffer.isEof()){
					synchronized(inputMonitor){
						try {
							inputMonitor.wait();
//							Thread.sleep(2);
//							System.out.print("r");
//							System.out.flush();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		if(inBuffer.lock(data, this, null, true)){
			double[] result = getFFT();
//			System.out.println("Result Size:"+result.length);
//			System.out.flush();
			while(!out.lock(result, null, this, false)){
				synchronized(outputMonitor){
					try {
//						outputMonitor.wait();
						Thread.sleep(2);
//						System.out.print("w");
//						System.out.flush();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
//		System.out.println("FFT Done");
//		System.out.flush();
		out.setEof(true);
		
	}

	@Override
	public void setNameID(String name) {
		this.name = name;
		
	}
	
	private double[] getFFT(){
//		MagSpectrum fft=null;
		try {
//			fft = new MagSpectrum(data, (double[])null, false, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new double[]{};//fft.getMagnitudeSpectrum();
		
	}

}
