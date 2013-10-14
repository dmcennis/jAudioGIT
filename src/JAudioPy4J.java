/**
 * Created with IntelliJ IDEA.
 * User: dmcennis
 * Date: 9/24/13
 * Time: 11:56 AM
 * To change this template use File | Settings | File Templates.
 */

import py4j.GatewayServer;
import jAudioFeatureExtractor.AudioStreamProcessor;

public class JAudioPy4J {

    AudioStreamProcessor processor;

    public JAudioPy4J(String a,String b){
	processor = new AudioStreamProcessor(a,b);
    }

    public AudioStreamProcessor get(){
	return processor;
    }

    public static void main(String[] args){
        GatewayServer gateway = new GatewayServer(new JAudioPy4J(args[0],args[1]));
        gateway.start();
    }
}
