package jAudioFeatureExtractor.ACE.DataTypes;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import jAudioFeatureExtractor.DataModel;
import jAudioFeatureExtractor.Aggregators.Aggregator;
import jAudioFeatureExtractor.DataTypes.RecordingInfo;
import jAudioFeatureExtractor.jAudioTools.AudioSamples;


/**
 * Batch
 *
 * Batch is the class used to represent an execution unit including both files and settings in jAudio.  All settings
 * have defaults except the input data.  This requires setRecordings(File[]) or setRecordings(RecordingInfo[]) to be 
 * executed first.  By default, Batch.execute() is a blocking calculation.  For non-blocking, see CommandLineThread 
 * for the simplist way to get non-blocking capabailities.
 * 
 * @author Daniel McEnnis
 */
public class Batch implements Serializable {

	static final long serialVersionUID = 1;

	String name;

	RecordingInfo[] recording = new RecordingInfo[0];

	int windowSize;

	double windowOverlap;

	double samplingRate;

	boolean normalise;

	boolean perWindow;

	boolean overall;

	String destinationFK = null;

	String destinationFV = null;

	int outputType;

	transient DataModel dm_;

	HashMap<String, Boolean> activated;

	HashMap<String, String[]> attributes;

	String[] aggregatorNames;

	String[][] aggregatorFeatures;

	String[][] aggregatorParameters;

	/**
	 * Set the data model against which this batch is executed.
	 * 
	 * @param dm
	 *            Context of this batch.
	 */
	public void setDataModel(DataModel dm) {
		dm_ = dm;
	}

	/**
	 * Execute this batch by first setting the context ass specified in the
	 * batch, then executing using the data model.
	 * 
	 * @throws Exception
	 */
	public void execute() throws Exception {
		applyAttributes();
		dm_.extract(windowSize, windowOverlap, samplingRate, normalise,
				perWindow, overall, recording, outputType);
	}

	/**
	 * Sets the recordings that this batch will load and execute.
	 * 
	 * @param files
	 *            recordings which are to be scheduled for porcessing.
	 * @throws Exception
	 */
	public void setRecordings(File[] files) throws Exception {
		recording = new RecordingInfo[files.length];
		// Go through the files one by one
		for (int i = 0; i < files.length; i++) {
			// Verify that the file exists
			if (files[i].exists()) {
				try {
					// Generate a RecordingInfo object for the loaded file
					recording[i] = new RecordingInfo(files[i].getName(),
							files[i].getPath(), null, false);
				} catch (Exception e) {
					recording = null;
					throw e;
				}
			} else {
				recording = null;
				throw new Exception("The selected file " + files[i].getName()
						+ " does not exist.");
			}
		}
	}

	/**
	 * Sets the attributes for how the features are to be extracted when
	 * executed.
	 * 
	 * @param windowSize
	 *            Size of the analysis window in samples.
	 * @param windowOverlap
	 *            Percent overlap of the windows. Must be greater than or equal
	 *            to 0 and less than 1.
	 * @param samplingRate
	 *            number of samples per second of audio.
	 * @param normalise
	 *            should the files be normalised before execution.
	 * @param perWindow
	 *            should features be extracted for each window in each file.
	 * @param overall
	 *            should overall features be extracted for each files.
	 * @param outputType
	 *            what format should the extracted features be saved in.
	 */
	public void setSettings(int windowSize, double windowOverlap,
			double samplingRate, boolean normalise, boolean perWindow,
			boolean overall, int outputType) {
		this.windowSize = windowSize;
		this.windowOverlap = windowOverlap;
		this.samplingRate = samplingRate;
		this.normalise = normalise;
		this.perWindow = perWindow;
		this.overall = overall;
		this.outputType = outputType;
	}

	/**
	 * Sets where the extracted features should be stored.
	 * 
	 * @param FK
	 *            Location where feature descriptions should be stored.
	 * @param FV
	 *            Location where extracted features should be stored.
	 */
	public void setDestination(String FK, String FV) {
		destinationFK = FK;
		destinationFV = FV;
	}

	/**
	 * Sets which features are active and the parameters of these features.
	 * 
	 * @param activated
	 *            Which features are to be extracted.
	 * @param attributes
	 *            settings of parameters of these features.
	 */
	public void setFeatures(HashMap<String, Boolean> activated,
			HashMap<String, String[]> attributes) {
		this.activated = activated;
		this.attributes = attributes;
	}

	/**
	 * Returns the name of this batch.
	 * 
	 * @return name assigned to this batch.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this batch. This name must be unique.
	 * 
	 * @param name
	 *            Name of this batch.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Apply the stored attributes against the current feature list.
	 * 
	 * @throws Exception
	 */
	private void applyAttributes() throws Exception {

		for (int i = 0; i < dm_.features.length; ++i) {
			String name = dm_.features[i].getFeatureDefinition().name;
			if (attributes.containsKey(name)) {
				dm_.defaults[i] = activated.get(name);
				String[] tmp = attributes.get(name);
				for (int j = 0; j < tmp.length; ++j) {
					dm_.features[i].setElement(j, tmp[j]);
				}
			}else{
				dm_.defaults[i] = false;
			}
		}
		LinkedList<Aggregator> aggregatorList = new LinkedList<Aggregator>();
		for(int i=0;i<aggregatorNames.length;++i){
			Aggregator tmp = (Aggregator)dm_.aggregatorMap.get(aggregatorNames[i]).clone();
//			if(!tmp.getAggregatorDefinition().generic){
				tmp.setParameters(aggregatorFeatures[i],aggregatorParameters[i]);
//			}
			aggregatorList.add(tmp);
		}
		if(overall && (aggregatorList.size()==0)){
			throw new Exception("Attempting to get overall stats without specifying any aggregators to create it");
		}
		dm_.aggregators = aggregatorList.toArray(new Aggregator[]{});
	}
	
	/**
	 * Returns the expanded list of aggregators that would be executed if execute() were called.  This returns
	 * an empty array if either aggregators or features to be extracted have not been seet yet.
	 *
	 * @return array of aggregators scheduled to execute
	 */
	public Aggregator[] getAggregator() throws Exception{
		LinkedList<Aggregator> aggregatorList = new LinkedList<Aggregator>();
		if(aggregatorNames != null){
			for(int i=0;i<aggregatorNames.length;++i){
				Aggregator tmp = (Aggregator)dm_.aggregatorMap.get(aggregatorNames[i]).clone();
//			if(!tmp.getAggregatorDefinition().generic){
				tmp.setParameters(aggregatorFeatures[i],aggregatorParameters[i]);
//			}
				aggregatorList.add(tmp);
			}
		}
		if(overall && (aggregatorList.size()==0)){
			throw new Exception("Attempting to get overall stats without specifying any aggregators to create it");
		}
		return aggregatorList.toArray(new Aggregator[]{});

	}

	/**
	 * Output this batch in XML format.
	 * 
	 * @return String contains a complete batch XML file.
	 */
	public String outputXML() {
		StringBuffer ret = new StringBuffer();
		String sep = System.getProperty("line.separator");
		ret.append("\t<batch ID=\"").append(name).append("\">").append(sep);
		ret.append("\t\t<fileSet>").append(sep);
		for (int i = 0; i < recording.length; ++i) {
			ret.append("\t\t\t<file>").append(recording[i].file_path).append(
					"</file>").append(sep);
		}
		ret.append("\t\t</fileSet>").append(sep);
		ret.append("\t\t<settings>").append(sep);
		ret.append("\t\t\t<windowSize>").append(windowSize).append(
				"</windowSize>").append(sep);
		ret.append("\t\t\t<windowOverlap>").append(windowOverlap).append(
				"</windowOverlap>").append(sep);
		ret.append("\t\t\t<samplingRate>").append(samplingRate).append(
				"</samplingRate>").append(sep);
		ret.append("\t\t\t<normalise>").append(normalise)
				.append("</normalise>").append(sep);
		ret.append("\t\t\t<perWindowStats>").append(perWindow).append(
				"</perWindowStats>").append(sep);
		ret.append("\t\t\t<overallStats>").append(overall).append(
				"</overallStats>").append(sep);
		if (outputType == 0) {
			ret.append("\t\t\t<outputType>ACE</outputType>").append(sep);
		} else {
			ret.append("\t\t\t<outputType>ARFF</outputType>").append(sep);
		}
		Set s = attributes.entrySet();
		for (Iterator<Map.Entry<String, String[]>> iterator = s.iterator(); iterator
				.hasNext();) {
			Map.Entry<String, String[]> i = iterator.next();
			String name = i.getKey();
			String[] att = i.getValue();
			ret.append("\t\t\t<feature>").append(sep);
			ret.append("\t\t\t\t<name>").append(name).append("</name>").append(
					sep);
			ret.append("\t\t\t\t<active>").append(activated.get(name)).append(
					"</active>").append(sep);
			for (int j = 0; j < att.length; ++j) {
				ret.append("\t\t\t\t<attribute>").append(att[j]).append(
						"</attribute>").append(sep);
			}
			ret.append("\t\t\t</feature>").append(sep);

		}
		for(int i=0;i<aggregatorNames.length;++i){
			ret.append("\t\t\t<aggregator>").append(sep);
			ret.append("\t\t\t\t<aggregatorName>").append(aggregatorNames[i]).append("</aggregatorName>").append(sep);
			if(aggregatorFeatures[i] != null){
				for(int j=0;j<aggregatorFeatures[i].length;++j){
					ret.append("\t\t\t\t<aggregatorFeature>").append(aggregatorFeatures[i][j]).append("</aggregatorFeature>").append(sep);
				}
			}
			if(aggregatorParameters[i]!= null){
				for(int j=0;j<aggregatorParameters[i].length;++j){
					ret.append("\t\t\t\t<aggregatorAttribute>").append(aggregatorParameters[i][j]).append("</aggregatorAttribute>").append(sep);
				}
			}
			ret.append("\t\t\t</aggregator>").append(sep);
		}
		ret.append("\t\t</settings>").append(sep);
		ret.append("\t\t<destination>").append(destinationFK).append(
				"</destination>").append(sep);
		ret.append("\t\t<destination>").append(destinationFV).append(
				"</destination>").append(sep);
		ret.append("\t</batch>").append(sep);
		return ret.toString();
	}

	/**
	 * apply this batch against info needed for a datamodel so that it can be
	 * executed.
	 * 
	 * @param recording
	 *            list of files to be analyzed
	 * @param windowSize
	 *            size of the analysis window in samples
	 * @param windowOverlap
	 *            percent overlap as a value between 0 and 1.
	 * @param samplingRate
	 *            number of samples per second
	 * @param normalise
	 *            should the file be normalized before execution
	 * @param perWindow
	 *            should features be extracted on a window bby window basis
	 * @param overall
	 *            should global features be extracted
	 * @param destinationFK
	 *            location of the feature declaration file
	 * @param destinationFV
	 *            location where extracted features should be stored
	 * @param outputType
	 *            what output format should extracted features be stored in.
	 */
	public void applySettings(RecordingInfo[][] recording, int[] windowSize,
			double[] windowOverlap, double[] samplingRate, boolean[] normalise,
			boolean[] perWindow, boolean[] overall, String[] destinationFK,
			String[] destinationFV, int[] outputType) {
		try {
			applyAttributes();
			dm_.featureDefinitions = new FeatureDefinition[dm_.features.length];
			for (int i = 0; i < dm_.featureDefinitions.length; ++i) {
				dm_.featureDefinitions[i] = dm_.features[i]
						.getFeatureDefinition();
			}
			dm_.recordingInfo = this.recording;
		} catch (Exception e) {
			System.err.println("INTERNAL ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		recording[0] = this.recording;
		windowSize[0] = this.windowSize;
		windowOverlap[0] = this.windowOverlap;
		samplingRate[0] = this.samplingRate;
		normalise[0] = this.normalise;
		perWindow[0] = this.perWindow;
		overall[0] = this.overall;
		destinationFK[0] = this.destinationFK;
		destinationFV[0] = this.destinationFV;
		outputType[0] = this.outputType;
	}

	/**
	 * Returns a map of all parameters for all features in the feature set.
	 *
	 * @return map of all feature parameters
	 */
	public HashMap<String, String[]> getAttributes() {
		return attributes;
	}

	/**
	 * Returns a map defining which features will appear in the output.
	 *
	 * @return map of features and whether they are scheduled for output or not.
	 */
	public HashMap<String, Boolean> getActivated() {
		return activated;
	}

	/**
	 * sets parameter values for all features simultaneously. Exceptions for bad parameters
	 * are thrown on application (i.e. Batch.execute()) not here.
	 *
	 * @param map of paramter settings.
	 */
	public void setAttributes(HashMap<String, String[]> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Returns the location for the XML key file.  Null means either a stream set
	 * on the backing DataModel or no key to be outputed (weka format).
	 *
	 * @return location to store the ACE key XML file
	 */
	public String getDestinationFK() {
		return destinationFK;
	}

	/**
	 * sets the file location for the ACE key XML file. This location is not
	 * validated until Batch.execute() is run.
	 * 
	 * @param location to store the ACE key XML file
	 */
	public void setDestinationFK(String destinationFK) {
		this.destinationFK = destinationFK;
	}

	/**
	 * Returns the location of either the location to put the ACE XML data file 
	 * or the Weka output file. Null implies stream output.
	 */
	public String getDestinationFV() {
		return destinationFV;
	}

	/**
	 * sets the file location for the result file. This location is not
	 * validated until Batch.execute() is run.
	 * 
	 * @param location to store the output data file in
	 */
	public void setDestinationFV(String destinationFV) {
		this.destinationFV = destinationFV;
	}

	/**
	 * Scheduled to normalise input files before analysis.
	 *
	 * @return are files to be normalised or not on loading
	 */
	public boolean isNormalise() {
		return normalise;
	}

	/**
	 * Set or cancel normalisation of input audio files before analysis.
	 *
	 * @return normalise or not
	 */
	public void setNormalise(boolean normalise) {
		this.normalise = normalise;
	}

	/**
	 * Get the type of output to be produced
	 *
	 * @return integer representing the output type (by Java constant)
	 */
	public int getOutputType() {
		return outputType;
	}

	/**
	 * Set the style of output (Weka, ACE, etc.)
	 *
	 * @param style of output by constant
	 */
	public void setOutputType(int outputType) {
		this.outputType = outputType;
	}

	/**
	 * Should aggregated per-file results be generated?
	 *
	 * @return are aggregated per-file results generated
	 */
	public boolean isOverall() {
		return overall;
	}

	/**
	 * Should aggregated per-file results be generated?
	 *
	 * @param overall sets whether to output aggregated results or not.
	 */
	public void setOverall(boolean overall) {
		this.overall = overall;
	}

	/**
	 * Should per-window results be generated?
	 *
	 * @return are per-window results generated
	 */
	public boolean isPerWindow() {
		return perWindow;
	}

	/**
	 * Should per-window results be generated?
	 *
	 * @param overall sets whether to output per-window results or not.
	 */
	public void setPerWindow(boolean perWindow) {
		this.perWindow = perWindow;
	}

	/**
	 * Get the sampling rate to which all files will be transformed to whether
	 * or not they are at that rate to begiun with.
	 *
	 * @return analysis sampling rate
	 */
	public double getSamplingRate() {
		return samplingRate;
	}

	/**
	 * Set the sampling rate to which all files will be transformed to whether
	 * or not they are at that rate to begiun with.
	 *
	 * @param samplingRate new analysis sample rate
	 */
	public void setSamplingRate(double samplingRate) {
		this.samplingRate = samplingRate;
	}

	/**
	 * Gets the number of samples per window that are also present in the previous window
	 *
	 * @return degree of overlap between successive windows
	 */
	public double getWindowOverlap() {
		return windowOverlap;
	}

	/**
	 * Sets the number of samples per window that are also present in the previous window
	 *
	 * @param windowOverlap degree of overlap between successive windows
	 */
	public void setWindowOverlap(double windowOverlap) {
		this.windowOverlap = windowOverlap;
	}

	/**
	 * Gets the number of samples inside of each analysis window
	 *
	 * @return number of samples per window
	 */
	public int getWindowSize() {
		return windowSize;
	}

	/**
	 * Sets the number of samples inside of each analysis window
	 *
	 * @param windowSize number of samples per window
	 */
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	/**
	 * Sets the audio content to be analyzed when execute() is called 
	 * whether or not the sources are from a file or not.
	 *
	 * @param recording array of RecordingInfo objects representing independent units of audio data.
	 */
	public void setRecording(RecordingInfo[] recording) {
		this.recording = recording;
	}

	/**
	 * Gets the audio content to be analyzed when execute() is called 
	 * whether or not the sources are from a file or not.
	 *
	 * @return array of RecordingInfo objects representing independent units of audio data.
	 */
	public RecordingInfo[] getRecording() {
		return recording;
	}

	/**
	 * Gets the underlying DataModel this Batch is encapsulating
	 *
	 * @return underlying DataModel
	 */
	public DataModel getDataModel() {
		return dm_;
	}

	/**
	 * Sets the aggregators, their parameters, and which features are used by them.  Internally
	 * Batch creates the aggregators from the name, sets the features and parameters using 
	 * Aggregator.setParameters() from the given content. See Aggregator
	 * for the specific syntax of feature and parameter string arrays.
	 */
	public void setAggregators(String[] aggNames, String[][] aggFeatures,
			String[][] aggParam) {
		if ((aggNames.length == aggFeatures.length)
				&& (aggFeatures.length == aggParam.length)) {
			aggregatorNames = aggNames;
			aggregatorFeatures = aggFeatures;
			aggregatorParameters = aggParam;
		} else {
			System.out
					.println("INTERNAL ERROR: Parameters are not of the same length - implying differing numbers of aggregators to define:"
							+ aggNames.length
							+ " "
							+ aggFeatures.length
							+ " "
							+ aggParam.length);
		}
	}

	/**
	 * Get an array containing an ordered array of all aggregators.  Unlike getAggregator(), this returns the list of
	 * aggregators <b>before</b> the generic aggregators have been expanded with the list of chosen features.
	 *
	 */
	public String[] getAggregatorNames(){
		return aggregatorNames;
	}
	
	/**
	 * Returns an array of String arrays where the first index is the index of the source aggregator, the second the index of the feature
	 * in the aggregator, and the String value the value of the feature to be included. Only specific aggregators have non-null entries.
	 *
	 * @return array defining all features assigned to each (specific) aggregator.  General Aggregators have none set.
	 */
	public String[][] getAggregatorFeatures(){
		return aggregatorFeatures;
	}
	
	/**
	 * Gets an array of String arrays where the first index is the index of the source aggregator, the second the paramater
	 * in the aggregator, and the String value the value of the parameter to be set.
	 *
	 * @return  array defining all features assigned to each (specific) aggregator.  General Aggregators have none set.
	 */
	public String[][] getAggregatorParameters(){
		return aggregatorParameters;
	}
}
