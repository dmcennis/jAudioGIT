package org.jaudio;

public interface FeatureIn {
	public abstract int getInputWindowSize(SynchronizedBuffer buffer);
	public abstract void doGet(Buffer buff);
	public abstract void setWindowSize(int windowSize, SynchronizedBuffer buffer);
	public void setBuffer(SynchronizedBuffer buffer, FeatureOut out);
	public String getNameID();
	public void setNameID(String name);
	public long getWindowCount();
}
