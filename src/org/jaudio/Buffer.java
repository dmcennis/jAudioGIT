package org.jaudio;

public interface Buffer {

	public abstract boolean get(double[] data, FeatureIn in);
	public abstract boolean put(double[] data, FeatureOut out);
	public abstract boolean lock(double[] data, FeatureIn in, FeatureOut out, boolean featureIn);
	public abstract void registerListener(FeatureIn feature);
	public abstract long getWindowCount(FeatureIn feature);
	public abstract long getWindowCount();
}
