/**
 * 
 */
package org.jaudio;

import java.util.HashMap;
import java.util.Vector;

/**
 * @author dmcennis
 * 
 */
public class SynchronizedBuffer implements Buffer {

	HashMap<FeatureIn, Integer> offsetMap = new HashMap<FeatureIn, Integer>();

	FeatureOut out;

//	Vector<EntryPoint> getNextQueue = new Vector<EntryPoint>();
	HashMap<FeatureIn,Integer> queue = new HashMap<FeatureIn,Integer>();

	RevolvingBuffer buffer = new RevolvingBuffer();

	int windowCount = 0;

	boolean eof = false;

	public boolean isEof() {
		return eof;
	}

	public void setEof(boolean eof) {
		this.eof = eof;
		for(FeatureIn in : offsetMap.keySet()){
			in.doGet(this);
		}
	}

	public SynchronizedBuffer(FeatureOut out, int windowOffset) {
		this.out = out;
		windowCount = windowOffset;
		buffer.setBuffer(out.getOutputWindowSize() + 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jaudio.Buffer#get(double[])
	 */
	@Override
	public boolean get(double[] data, FeatureIn in) {
		int offset = offsetMap.get(in);
		if (buffer.get(data, offset)) {
			offsetMap.put(in, offsetMap.get(in) + data.length);
			int min = Integer.MAX_VALUE;
//			System.out.println("SIZE : "+offsetMap.keySet().size());
			for (FeatureIn i : offsetMap.keySet()) {
//				System.out.println(i.getNameID()+" OFFSET = "+offsetMap.get(i)+" window "+i.getWindowCount());
				if (offsetMap.get(i) < min) {
					min = offsetMap.get(i);
				}
			}
//			System.out.println("Minimum: "+min);
//			System.out.flush();
			if (min > 0) {
				buffer.advanceStart(min);
				windowCount++;
				for (FeatureIn i : offsetMap.keySet()) {
					offsetMap.put(i, offsetMap.get(i) - min);
				}
				out.notifyPutReady();
			}
			queue.remove(in);
//			System.out.println("GET: ");
//			System.out.flush();
			for(FeatureIn i : offsetMap.keySet()){
				in.doGet(this);
			}
			return true;
		} else {
			if(!queue.containsKey(in)){
				queue.put(in, data.length);
//				System.out.println("Window Length: "+in.getNameID()+" "+data.length);
			}
//			System.out.flush();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jaudio.Buffer#put(double[])
	 */
	@Override
	public boolean put(double[] data, FeatureOut out) {
		if (buffer.put(data)) {
			int push = data.length;
			Vector<FeatureIn> remove = new Vector<FeatureIn>();
			for(FeatureIn in : offsetMap.keySet()){
				in.doGet(this);
			}
			for(FeatureIn in : queue.keySet()){
				if(queue.get(in) <= push){
					remove.add(in);
					in.doGet(this);
				}else{
					queue.put(in, queue.get(in)-push);
					System.out.println("BAD: "+in.getNameID()+" "+queue.get(in));
					System.out.flush();
				}
			}
			for(FeatureIn in : remove){
				queue.remove(in);
//				System.out.println("Write "
//						+ in.getNameID());
//				System.out.flush();
			}
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jaudio.Buffer#registerListener(org.jaudio.FeatureIn)
	 */
	@Override
	public void registerListener(FeatureIn feature) {
		if (feature.getInputWindowSize(this) > ((buffer.size() - 1) / 2)) {
			buffer.setBuffer(feature.getInputWindowSize(this) + 1);
		}
		offsetMap.put(feature, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jaudio.Buffer#getSampleCount(org.jaudio.FeatureIn)
	 */
	@Override
	public long getWindowCount(FeatureIn feature) {
		if (offsetMap.get(feature) > 0) {
			return windowCount + 1;
		} else {
			return windowCount;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jaudio.Buffer#getSampleCount()
	 */
	@Override
	public long getWindowCount() {
		return windowCount;
	}

	public synchronized boolean lock(double[] data, FeatureIn in,
			FeatureOut out, boolean featureIn) {
		boolean id = false;
//		synchronized (buffer) {
			if (featureIn) {
//				System.out.println("In Read "+in.getNameID());
				id =  get(data, in);
//				System.out.println("Out Read "+in.getNameID());
			} else {
//				System.out.println("In Write "+out.getNameID());
				id =  put(data, out);
//				System.out.println("Out Write "+out.getNameID());
			}
//		}
		return id;
	}

	public class EntryPoint {
		private int offset = 0;
		private FeatureIn feature;

		public int getOffset() {
			return offset;
		}

		public void setOffset(int offset) {
			this.offset = offset;
		}

		public FeatureIn getFeature() {
			return feature;
		}

		public void setFeature(FeatureIn feature) {
			this.feature = feature;
		}

	}
	
	public int getWindowSize(){
		return buffer.length()-1;
	}
	
	public void poke(){
		for(FeatureIn in : offsetMap.keySet()){
			in.doGet(this);
		}
	}
	
	public void resize(int windowSize){
		buffer.setBuffer(windowSize+1);
		for(FeatureIn in : offsetMap.keySet()){
			in.setWindowSize(windowSize,this);
		}
	}
}
