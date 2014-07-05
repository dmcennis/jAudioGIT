/**
 * 
 */
package org.jaudio.aggregator;

import org.jaudio.Buffer;
import org.jaudio.FeatureIn;
import org.jaudio.FeatureOut;
import org.jaudio.SynchronizedBuffer;

/**
 * @author dmcennis
 *
 */
public class MeanAggregator extends Thread implements FeatureIn, FeatureOut {

	int inputWindowSize = 0;
	
	int outputWindowSize = 0;
	
	double meanCount = 0.0;
	
	double mean[] = new double[0];
	
	SynchronizedBuffer inBuffer = null;
	
	String name = "Aggregator";
	
	Integer inputMonitor = new Integer(0);
	
	Integer outputMonitor = new Integer(0);
	
	SynchronizedBuffer out = null;
	
	long windowCounter=0;	
	
	double[] data = new double[0];

	
	/* (non-Javadoc)
	 * @see org.jaudio.FeatureOut#getOutputWindowSize()
	 */
	@Override
	public int getOutputWindowSize() {
		return outputWindowSize;
	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureOut#notifyPutReady()
	 */
	@Override
	public void notifyPutReady() {
		synchronized(outputMonitor){
			outputMonitor.notifyAll();
		}

	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureOut#register(org.jaudio.FeatureIn)
	 */
	@Override
	public SynchronizedBuffer register(FeatureIn in) {
		if(out == null){
			out = new SynchronizedBuffer(this,0);
		}
		out.registerListener(in);
		return out;
	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureOut#windowOffset()
	 */
	@Override
	public int windowOffset() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureIn#getInputWindowSize()
	 */
	@Override
	public int getInputWindowSize(SynchronizedBuffer buffer) {
		return inputWindowSize;
	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureIn#doGet(org.jaudio.Buffer)
	 */
	@Override
	public void doGet(Buffer buff) {
		synchronized(inputMonitor){
			inputMonitor.notifyAll();
		}

	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureIn#setWindowSize(int)
	 */
	@Override
	public void setWindowSize(int windowSize,SynchronizedBuffer buffer) {
		data = new double[windowSize];
		mean = new double[windowSize];
		inputWindowSize = windowSize;
		outputWindowSize = windowSize;
		if(out == null){
			out = new SynchronizedBuffer(this,0);
		}
		out.resize(windowSize);
	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureIn#setBuffer(org.jaudio.SynchronizedBuffer)
	 */
	@Override
	public void setBuffer(SynchronizedBuffer buffer, FeatureOut out) {
		inBuffer = buffer;
		setWindowSize(inBuffer.getWindowSize(),buffer);
	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureIn#getNameID()
	 */
	@Override
	public String getNameID() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureIn#getWindowCount()
	 */
	@Override
	public long getWindowCount() {
		return 0;
	}

	@Override
	public void setNameID(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		while(!inBuffer.isEof()){
			if(inBuffer.lock(data, this, null, true)){
				meanCount += 1.0;
				for(int i=0;i<data.length;++i){
					mean[i] += data[i];
				}
//				System.out.print("m");
//				if(meanCount %1000==0){
//					System.out.print("m");
//				}
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
		for(int i=0;i<mean.length;++i){
			mean[i] /= meanCount;
		}
		while(!out.lock(mean, null, this, false)){
			synchronized(outputMonitor){
				try {
					outputMonitor.wait();
//					Thread.sleep(2);
//					System.out.print("w");
//					System.out.flush();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
//		System.out.println("Mean Aggregator Done");
//		System.out.flush();
		out.setEof(true);
	}

	
}
