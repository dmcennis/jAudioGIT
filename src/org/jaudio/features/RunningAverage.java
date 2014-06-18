/**
 * 
 */
package org.jaudio.features;

import org.jaudio.Buffer;
import org.jaudio.FeatureIn;
import org.jaudio.FeatureOut;
import org.jaudio.Memory;
import org.jaudio.SynchronizedBuffer;

/**
 * @author dmcennis
 *
 */
public class RunningAverage extends Thread implements FeatureIn, FeatureOut {
	int inputWindowSize = 0;
	
	int outputWindowSize = 0;
	
	double meanCount = 0.0;
	
	double mean[] = new double[0];
	
	SynchronizedBuffer inBuffer = null;
	
	String name = "Running Average";
	
	Integer inputMonitor = new Integer(0);
	
	Integer outputMonitor = new Integer(0);
	
	SynchronizedBuffer out = null;
	
	long windowCounter;	
	
	double[] data = new double[0];
	
	Memory mem;
	
	int windowOffset;
	
	public RunningAverage(int av){
		windowOffset = av - 1;
		windowCounter = av-1;
	}

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
		// TODO Auto-generated method stub
		synchronized(outputMonitor){
			outputMonitor.notifyAll();
		}
	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureOut#register(org.jaudio.FeatureIn)
	 */
	@Override
	public SynchronizedBuffer register(FeatureIn in) {
		out.registerListener(in);
		return out;
	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureOut#windowOffset()
	 */
	@Override
	public int windowOffset() {
		return windowOffset;
	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureIn#getInputWindowSize(org.jaudio.SynchronizedBuffer)
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
	 * @see org.jaudio.FeatureIn#setWindowSize(int, org.jaudio.SynchronizedBuffer)
	 */
	@Override
	public void setWindowSize(int windowSize, SynchronizedBuffer buffer) {
		mem = new Memory(windowOffset+1,windowSize);
		outputWindowSize = windowSize;
		if(out == null){
			out = new SynchronizedBuffer(this,windowOffset);
		}
		out.resize(windowSize);
		data = new double[windowSize];
	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureIn#setBuffer(org.jaudio.SynchronizedBuffer, org.jaudio.FeatureOut)
	 */
	@Override
	public void setBuffer(SynchronizedBuffer buffer, FeatureOut out) {
		inBuffer = buffer;
		setWindowSize(buffer.getWindowSize(),buffer);

	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureIn#getNameID()
	 */
	@Override
	public String getNameID() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureIn#setNameID(java.lang.String)
	 */
	@Override
	public void setNameID(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureIn#getWindowCount()
	 */
	@Override
	public long getWindowCount() {
		return windowCounter;
	}

	@Override
	public void run() {
		double[] average = new double[outputWindowSize];
		while((mem.size()< mem.maxSize()) && !inBuffer.isEof()){
			if(inBuffer.lock(data, this, null, true)){
				mem.put(data);
			}else{
				if(!inBuffer.isEof()){
					synchronized(inputMonitor){
						try {
//							inputMonitor.wait();
							Thread.sleep(2);
//							System.out.print("r");
//							System.out.flush();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		if(inBuffer.isEof()&& (mem.size() < mem.maxSize())){
			if(inBuffer.lock(data, this, null, true)){
				mem.put(data);
			}		
		}
		if(mem.size() == mem.maxSize()){
			calculateAverages(average);
			while(!out.lock(average, null, this, false)){
				synchronized(outputMonitor){
					try {
//						inputMonitor.wait();
						Thread.sleep(2);
//						System.out.print("r");
//						System.out.flush();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			windowCounter++;
		}
		while(!inBuffer.isEof()&&(mem.size()==mem.maxSize())){
			
			if(inBuffer.lock(data, this, null, true)){
				mem.put(data);
				calculateAverages(average);
				while(!out.lock(average, null, this, false)){
					synchronized(outputMonitor){
						try {
//							inputMonitor.wait();
							Thread.sleep(2);
//							System.out.print("r");
//							System.out.flush();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				windowCounter++;
				
			}else{
				if(!inBuffer.isEof()){
					synchronized(inputMonitor){
						try {
//							inputMonitor.wait();
							Thread.sleep(2);
//							System.out.print("r");
//							System.out.flush();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}

		}
		if((mem.size() ==mem.maxSize())&&inBuffer.lock(data, this, null, true)){
			mem.put(data);
			calculateAverages(average);
			while(!out.lock(average, null, this, false)){
				synchronized(outputMonitor){
					try {
//						inputMonitor.wait();
						Thread.sleep(2);
//						System.out.print("r");
//						System.out.flush();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
//		System.out.println("Done "+name);
		out.setEof(true);
	}

	private void calculateAverages(double[] result){
		for(int i=0;i<result.length;++i){
			result[i] = 0.0;
			for(int j=0;j<mem.size();++j){
				result[i] += mem.get(j)[i];
			}
			result[i] /= mem.size();
		}
	}
}
