package org.jaudio;

import jAudioFeatureExtractor.AudioStreamProcessor;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: dmcennis
 * Date: 9/26/13
 * Time: 9:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class JAudioServer {
    public final static int PORT = 3759;

    public static AudioStreamProcessor processor;

    public static void main(String[] args){
        while(true){
            try  {
               ServerSocket server = new ServerSocket(PORT);
               Thread task = new JAudioServerThread(server.accept(),args[0],args[1],args[2]);
               task.start();
            }catch (IOException e){

            }
        }
    }

    private static class JAudioServerThread extends Thread{
        private Socket socket;
        private String features;
        private String settings;
        private String base_prefix;
        private AudioStreamProcessor processor;

        JAudioServerThread(Socket s,String features,String settings,String prefix){
            socket = s;
            this.features = features;
            this.settings = settings;
            base_prefix=prefix;
        }

        @Override
        public void run() {
            try{
                processor = new AudioStreamProcessor(features,settings);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                java.nio.ByteBuffer buffer = ByteBuffer.allocateDirect(1000000000);
		int trackID  = input.readInt();
		base_prefix += Integer.toString(trackID);
                int total_read = 0;
                int this_read = 0;
                byte[] buff = new byte[1000000];
                while(this_read > -1 ){
			this_read = input.read(buff);
			if(this_read > 0){
                    		if(this_read + total_read < 1000000000)
                        		buffer.put(buff,0,this_read);
                    	}
			total_read += this_read;
                }
                input.close();
                socket.close();
		buffer.flip();
                FloatBuffer content = buffer.asFloatBuffer();
                double[] samples = new double[total_read/8];
		for(int i=0;i<samples.length;++i){
			samples[i] = (content.get()+content.get())/2.0;
		}
                content = null;
		buffer = null;
	        double max=0.0;
	        double min=0.0;
	        boolean okay = false;
	        if(samples.length < 512){
			throw new Exception("Data is less than one window in size (512 samples)");
		}
		for(int i=0;i<samples.length;++i){
	                if((samples[i] > 1.0) || (samples[i] < -1.0)){
                        throw new Exception("Badly formatted data "+ samples[i]+" at "+i);
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
            }catch(Exception e){
		System.out.println("Exception: "+e.getMessage());
            }
        }

    }
}
