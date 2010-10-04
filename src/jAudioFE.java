/*
 * @(#)jAudioFE.java	1.01	April 9, 2005.
 *
 * McGill Univarsity
 */

import jAudioFeatureExtractor.Controller;
import jAudioFeatureExtractor.OuterFrame;


/**
 * Runs the jAudio Feature Extractor GUI.
 *
 * @author Cory McKay
 */
public class jAudioFE
{
	/**
	 * Runs the GUI.
	 */
	public static void main(String[] args) 
	{
		if(args.length>0){
			JAudioCommandLine.execute(args);
		}else{
			Controller c = new Controller();
			new OuterFrame(c);
		}
	}
}