package org.jaudio;

public interface FeatureOut {
	public abstract int getOutputWindowSize();
	public abstract void notifyPutReady();
	public abstract SynchronizedBuffer register(FeatureIn in);
	public abstract int windowOffset();
	public long getWindowCount();
	public String getNameID();
	public void setNameID(String name);
}
