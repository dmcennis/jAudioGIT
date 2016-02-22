package org.jaudio;

import jAudioFeatureExtractor.AudioStreamProcessor;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.Vector;
import java.lang.ProcessBuilder;
import java.lang.Process;
/**
 * Created with IntelliJ IDEA.
 * User: dmcennis
 * Date: 9/26/13
 * Time: 9:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class JAudioFile {
    public final static int PORT = 3759;

    public static AudioStreamProcessor processor;

    public static void main(String[] args) throws Exception{
    	processor = new AudioStreamProcessor(args[0],args[1]);

	String base_prefix =args[2];
	base_prefix += args[4];

	ProcessBuilder gstreamerBuilder = new ProcessBuilder("gst-launch-1.0", "-q", "filesrc", "location="+args[3], "!", "decodebin", "!", "audioconvert", "!", "audio/x-raw,format=F32LE", "!", "fdsink");
	Process gstreamer =  gstreamerBuilder.start();
        DataInputStream input = new DataInputStream(gstreamer.getInputStream());
	ByteBuffer buffer = ByteBuffer.allocateDirect(1000000000);
	buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        byte[] b = new byte[1000000];
	int read=0;
	int total=0;
	while((read = input.read(b))>-1){
		total += read;
		if(read > 0)
			buffer.put(b,0,read);
	}
	System.out.println();
	buffer.flip();
	FloatBuffer db = buffer.asFloatBuffer();

        double[] samples = new double[total / 8];
	for(int i=0;i<samples.length;++i){
		samples[i] = (db.get()+db.get()) / 2.0;
	}	
	db = null;
        buffer = null;
	double max=0.0;
	double min=0.0;
	boolean okay = false;
	if(samples.length < 512){
		throw new Exception("File is too small to analyze - "+samples.length+" samples");
	}
	for(int i=0;i<samples.length;++i){
 		if((samples[i] > 1.0) || (samples[i] < -1.0)){
			throw new Exception("Badly formatted data "+i+" "+samples[i]);
		}
		if((samples[i] > 0.7)){
			okay = true;
		}
	}
	if(!okay){
		throw new Exception("Data is artificially small - probable endianess problem");
	}
	processor.process(samples);

	Date date = new Date();
        String attach = date.toString();
        attach = Pattern.compile("\\s").matcher(attach).replaceAll("_");
        base_prefix += Pattern.compile(":").matcher(attach).replaceAll("-");
        processor.output(base_prefix);
    }
}
