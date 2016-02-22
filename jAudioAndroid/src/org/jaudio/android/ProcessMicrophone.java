/**
 * Created by Daniel McEnnis on 2/22/2016
 * <p/>
 * Copyright Daniel McEnnis 2015
 */

package org.jaudio.android;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import jAudioFeatureExtractor.ACE.DataTypes.Batch;
import jAudioFeatureExtractor.DataTypes.RecordingInfo;
import jAudioFeatureExtractor.jAudioTools.AudioSamples;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * Default Description Google Interview Project
 */
public class ProcessMicrophone  {

    MediaRecorder recordingDevice = new MediaRecorder();
    AudioRecord recorder;
    int buffer;
    byte[] rawData;
    ShortBuffer data;
    long time;
    Batch batch = null;
    /**
     * Default constructor for ProcessMicrophone
     */
    public ProcessMicrophone(long time,int buffer) throws IOException{
        this.time = time;
        this.buffer = buffer;
        byte[] rawData = new byte[buffer];
        ByteBuffer byteBuffer = ByteBuffer.wrap(rawData);
        ShortBuffer data = byteBuffer.asShortBuffer();
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,44100,AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,buffer);
    }

    public void record(){
        record(time);
    }

    public void record(long t){
        recorder.startRecording();
        try{
            Thread.currentThread().wait(time);
        }catch(InterruptedException e){

        }
        recorder.stop();
        int read=0;
        int offset=0;
        int total=buffer;
        while((read = recorder.read(rawData,offset,total))>0){
            offset += read;
            total -= read;
        }
        short[] audioInput = data.array();
        double[] audio = new double[audioInput.length];
        double scaling = Math.pow(2,15);
        for(int i=0;i<audioInput.length;++i){
            audio[i] = ((double)audioInput[i])/scaling;
        }
        try {
            AudioSamples samples = new AudioSamples(audio,"MicRecording");
        RecordingInfo[] info = new RecordingInfo[]{new RecordingInfo("MicRecording","None",samples,true)};
        batch = new Batch();
        batch.setRecording(info);
            batch.execute();
        }catch(Exception e){

        }
    }

    public double[][][] getResults(){
        if(batch != null){
            return batch.getResults();
        }else{
            return null;
        }
    }
}
