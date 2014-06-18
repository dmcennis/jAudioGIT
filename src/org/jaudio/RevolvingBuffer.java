package org.jaudio;

import java.util.Arrays;

public class RevolvingBuffer {
	double[] internalBuffer = new double[0];
	
	int startIndex=0;
	int endIndex=0;
	
	public int bufferRemaining(){
		if(startIndex > endIndex){
			return (startIndex - endIndex) -1;
		}else{
			return (internalBuffer.length-endIndex)+startIndex-1;
		}
	}
	
	public int size(){
		if(startIndex > endIndex){
			return (internalBuffer.length-startIndex)+endIndex;
		}else{
			return endIndex-startIndex;
		}
	}
	
	public boolean put(double[] data){
		if(data.length > bufferRemaining()){
			return false;
		}else{
			int total = endIndex+data.length;
			if(total >= internalBuffer.length){
				int value = total % internalBuffer.length;
				int copied = internalBuffer.length-endIndex;
				for(int i=endIndex;i<internalBuffer.length;++i){
					internalBuffer[i] = data[i-endIndex];
				}
				for(int i=0;i<value;++i){
					internalBuffer[i] = data[i+copied];
				}
				endIndex = value;
			}else{
				for(int i=0;i<data.length;++i){
					internalBuffer[i+endIndex] = data[i];
				}
				endIndex += data.length;
			}
			return true;
		}
	}
	
	public boolean get(double[] data, int offset){
		if(data.length + offset > size()){
			return false;
		}else{
			int firstHalf = internalBuffer.length-startIndex;
			if(data.length +offset > firstHalf){
				for(int i=0;i<firstHalf-offset;++i){
					data[i] = internalBuffer[i+startIndex+offset];
				}
				for(int i=Math.max(0, firstHalf-offset);i<data.length;++i){
					data[i] = internalBuffer[i+offset-firstHalf];
				}
			}else{
				for(int i=0;i<data.length;++i){
					data[i] = internalBuffer[i+startIndex];
				}
			}
			return true;
		}
	}
	
	public boolean advanceStart(int position){
		if(position > size()){
			return false;
		}else{
			if(startIndex + position >= internalBuffer.length){
				startIndex = position - (internalBuffer.length - startIndex);
			}else{
				startIndex += position;
			}
			return true;
		}
	}
	
	public void setBuffer(int size){
		startIndex = 0;
		endIndex=0;
		internalBuffer = new double[size];
	}
	
	public int length(){
		return internalBuffer.length;
	}

}
