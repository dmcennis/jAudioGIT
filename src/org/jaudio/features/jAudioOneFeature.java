package org.jaudio.features;

import org.jaudio.Buffer;
import org.jaudio.FeatureIn;
import org.jaudio.FeatureOut;
import org.jaudio.SynchronizedBuffer;

import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;

public class jAudioOneFeature extends Thread implements FeatureIn, FeatureOut {

	long windowCount = 0;
	
	int windowSize;
	
	Integer monitor = new Integer(0);
	
	FeatureExtractor extractor = null;
	
	SynchronizedBuffer outBuff;
	
	SynchronizedBuffer[] inBuff;
	
	String name;
	
	public jAudioOneFeature(FeatureExtractor feature){
		extractor = feature;
		windowSize = feature.getFeatureDefinition().dimensions;
	}
	
	SynchronizedBuffer outBuffer;
	
	@Override
	public void notifyPutReady() {
		synchronized(monitor){
			monitor.notifyAll();
		}
	}

	@Override
	public SynchronizedBuffer register(FeatureIn in) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int windowOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInputWindowSize(SynchronizedBuffer buffer) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getOutputWindowSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void doGet(Buffer buff) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWindowSize(int windowSize,SynchronizedBuffer buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBuffer(SynchronizedBuffer buffer, FeatureOut out) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getNameID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getWindowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setNameID(String name) {
		this.name = name;
		
	}

}
