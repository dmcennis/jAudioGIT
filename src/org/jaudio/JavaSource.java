/**
 * 
 */
package org.jaudio;


import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * @author dmcennis
 *
 */
public class JavaSource extends Thread implements FeatureOut {

	File source;
	
	SynchronizedBuffer out = new SynchronizedBuffer(this,512);
	
	int windowCounter=0;
	
	Integer monitor = new Integer(0);
	
	String name = "Java Source";
	
	public File getSource() {
		return source;
	}

	public void setSource(File source) {
		this.source = source;
	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureOut#getWindowSize()
	 */
	@Override
	public int getOutputWindowSize() {
		return 512;
	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureOut#notifyPutReady()
	 */
	@Override
	public void notifyPutReady() {
		synchronized (monitor){
			monitor.notifyAll();
		}
	}

	/* (non-Javadoc)
	 * @see org.jaudio.FeatureOut#register(org.jaudio.FeatureIn)
	 */
	@Override
	public SynchronizedBuffer register(FeatureIn in) {
		// TODO Auto-generated method stub
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
	 * @see org.jaudio.FeatureOut#getWindowCount()
	 */
	@Override
	public long getWindowCount() {
		// TODO Auto-generated method stub
		return windowCounter;
	}

	@Override
	public void run() {
		AudioInputStream new_stream = null;
		AudioFormat new_format = null;
		try
		{
		AudioInputStream original_stream = AudioSystem
				.getAudioInputStream(source);
		AudioFormat original_format = original_stream.getFormat();

		// Set the bit depth
		int bit_depth = original_format.getSampleSizeInBits();
		if (bit_depth != 8 && bit_depth != 16)
			bit_depth = 16;

		// If the audio is not PCM signed big endian, then convert it to PCM
		// signed
		// This is particularly necessary when dealing with MP3s
		AudioInputStream second_stream = original_stream;
		if (original_format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED
				|| original_format.isBigEndian() == false) {
			new_format = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED, original_format
							.getSampleRate(), bit_depth, original_format
							.getChannels(), original_format.getChannels()
							* (bit_depth / 8), original_format.getSampleRate(),
					true);
			second_stream = AudioSystem.getAudioInputStream(new_format,
					original_stream);
		}

		// Convert to the set sampling rate, if it is not already at this
		// sampling rate.
		// Also, convert to an appropriate bit depth if necessary.
		new_stream = second_stream;
		if (original_format.getSampleRate() != (float) 44100
				|| bit_depth != original_format.getSampleSizeInBits()) {
			new_format = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED, (float) 44100,
					bit_depth, original_format.getChannels(), original_format
							.getChannels()
							* (bit_depth / 8), original_format.getSampleRate(),
					true);
			new_stream = AudioSystem.getAudioInputStream(new_format,
					second_stream);
		}
		int number_of_channels = new_format.getChannels();
		bit_depth = new_format.getSampleSizeInBits();
		int bytes_per_sample = bit_depth / 8;
		double[] dataWindow = new double[512];
		if (bit_depth == 8)
		{
			byte[] buffer = null;
			while((buffer = readAudioStream(new_stream,new_format))!=null){
				buffer = readAudioStream(new_stream,new_format);
				ByteBuffer byte_buffer = ByteBuffer.wrap(buffer);
				for (int samp = 0; samp < 512; samp++){
					double value = 0;
					for (int chan = 0; chan < number_of_channels; chan++){
//						sample_values[chan][samp] = (double) byte_buffer.get() / max_sample_value;
						value += (double)byte_buffer.get();
					}
					value /= number_of_channels;
					dataWindow[samp] = value;
				}
				if(!out.lock(dataWindow,null, this,false)){
					try {
						synchronized(monitor){
							monitor.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				windowCounter++;
			}
			System.out.println("File done");	
			System.out.flush();
		}
		else if (bit_depth == 16)
		{
			byte[] buffer = null;
			while((buffer = readAudioStream(new_stream,new_format))!=null){
				ByteBuffer byte_buffer = ByteBuffer.wrap(buffer);
				ShortBuffer short_buffer = byte_buffer.asShortBuffer();
				for (int samp = 0; samp < 512; samp++){
					double value = 0;
					for (int chan = 0; chan < number_of_channels; chan++){
						value += (double)short_buffer.get();
					}
					value /= number_of_channels;
					dataWindow[samp] = value;
				}
				if(!out.lock(dataWindow,null, this,false)){
					try {
						synchronized(monitor){
//							monitor.wait();
							Thread.sleep(2);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				windowCounter++;
			}
			System.out.println("File done");
			System.out.flush();
		}
		out.setEof(true);
		}
		catch (UnsupportedAudioFileException ex)
		{
			System.out.println("File " + source.getName() + " has an unsupported audio format.");
		}
		catch (IOException ex)
		{
			System.out.println("File " + source.getName() + " is not readable.");
		}
		
	}

	public byte[] readAudioStream(AudioInputStream ais,AudioFormat af) throws IOException{
		int number_of_channels = af.getChannels();
		int bit_depth = af.getSampleSizeInBits();
		int bytes_per_sample = bit_depth / 8;
		byte[] buffer = new byte[512*number_of_channels*bytes_per_sample];
		int read = 0;
		int total = 0;
		int count=0;
		while((total < buffer.length)&&((read = ais.read(buffer, total, buffer.length-total))>0)){
			total += read;
		}
		if(total == buffer.length){
			return buffer;
		}else{
			return null;
		}
	}

	@Override
	public String getNameID() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public void setNameID(String name) {
		this.name = name;
		
	}
	
	
}
