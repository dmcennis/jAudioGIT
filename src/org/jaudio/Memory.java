package org.jaudio;

public class Memory {
	double[][] data;
	
	int maxSize;
	
	int windowSize;
	
	int startIndex=0;
	
	int size=0;
	
	public Memory(int size, int windowSize){
		data = new double[size][windowSize];
		maxSize = size;
		this.windowSize = windowSize;
	}
	
	public double[] get(int i){
		if(i < size){
			return data[(startIndex + i)%maxSize];
		}else{
			return null;
		}
	}
	
	public void put(double[] data){
		for(int i=0;i<windowSize;++i){
			this.data[(startIndex+size)%maxSize][i]=data[i];
		}
		if(size == maxSize){
			startIndex = (startIndex +1)%maxSize;
		}else{
			size++;
		}
	}
	public int size(){
		return size;
	}
	
	public int maxSize(){
		return data.length;
	}
}
