package jAudioFeatureExtractor.ACE.XMLParsers;

import jAudioFeatureExtractor.ACE.DataTypes.Batch;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class responsible for the parsing of XML batch files.
 * 
 * @author Daniel McEnnis
 *
 */
public class ParseBatchJobHandler extends ParseFileHandler {
	
	public static final int BATCH_FILE=0;
	
	public static final int BATCH = 1;
	
	public static final int FILE_SET = 2;
	
	public static final int FILE = 3;
	
	public static final int SETTINGS = 4;
	
	public static final int WINDOW_SIZE = 5;
	
	public static final int WINDOW_OVERLAP=6;
	
	public static final int SAMPLING_RATE=7;
	
	public static final int NORMALISE = 8;
	
	public static final int PER_WINDOW_STATS=9;
	
	public static final int OVERALL_STATS = 10;
	
	public static final int OUTPUT_TYPE = 11;
	
	public static final int FEATURE = 12;
	
	public static final int ACTIVE = 13;
	
	public static final int ATTRIBUTE = 14;
	
	public static final int DESTINATION = 15;
	
	public static final int NAME = 16;
	
	public static final int AGGREGATOR = 17;
	
	public static final int AGGREGATOR_NAME = 18;
	
	public static final int AGGREGATOR_FEATURE = 19;
	
	public static final int AGGREGATOR_PARAMETER = 20;

	LinkedList<File> fileSet = new LinkedList<File>();

	LinkedList<Batch> batchSet = new LinkedList<Batch>();

	HashMap<String,Boolean> activeFeatureSet = new HashMap<String,Boolean>();

	HashMap<String,String[]> featureAttributeSet = new HashMap<String,String[]>();

	LinkedList<String> tmpAttributeSet = new LinkedList<String>();
	
	LinkedList<String> aggregatorList = new LinkedList<String>();
	
	LinkedList<String[]> aggregatorFeatures = new LinkedList<String[]>();
	
	LinkedList<String> tmpAggregatorFeatures = new LinkedList<String>();
	
	LinkedList<String[]> aggregatorParameters = new LinkedList<String[]>();
	
	LinkedList<String> tmpAggregatorParameters = new LinkedList<String>();
	
	String name;
	
	String featureName;

	int windowSize;

	double windowOverlap;

	double sampleRate;

	boolean saveWindows;

	boolean overall;

	boolean normalise;

	int outputType;

	String destinationFV;

	String destinationFK;

	int tagType = -1;
	
	String fileName = "";

	/**
	 * Used to process character data in the XML file.  
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String data = new String(ch, start, length);
		switch (tagType){
			case BATCH_FILE:
				break;
			case BATCH:
				break;
			case FILE_SET:
				break;
			case FILE:
				fileName += data;
				break;
			case SETTINGS:
				break;
			case WINDOW_SIZE:
				try {
					windowSize = Integer.parseInt(data);
				} catch (NumberFormatException e) {
					throw new SAXException("windowSize data must be an integer");
				}
				break;
			case WINDOW_OVERLAP:
				try {
					double d = Double.parseDouble(data);
					if (Double.isNaN(d) || Double.isNaN(d) || (d < 0.0)
							|| (d >= 1.0)) {
						throw new SAXException(
								"windowOverlap must be at least zero and less than the window size");
					} else {
						windowOverlap = d;
					}
				} catch (NumberFormatException e) {
					throw new SAXException("windowOverlap must be a double");
				}
				break;
			case SAMPLING_RATE:
				try {
					sampleRate = Double.parseDouble(data);
				} catch (NumberFormatException e) {
					throw new SAXException("Sampling rate must be a double");
				}
				break;
			case NORMALISE:
				if (data.equals("true")) {
					normalise = true;
				} else {
					normalise = false;
				}
				break;
			case PER_WINDOW_STATS:
				if (data.equals("true")) {
					saveWindows = true;
				} else {
					saveWindows = false;
				}
				break;
			case OVERALL_STATS:
				if (data.equals("true")) {
					overall = true;
				} else {
					overall = false;
				}
				break;
			case OUTPUT_TYPE:
				if (data.equals("ACE")) {
					outputType = 0;
				} else {
					outputType = 1;
				}
				break;
			case FEATURE:
				break;
			case ACTIVE:
				if (data.equals("true")) {
					activeFeatureSet.put(featureName,true);
				} else {
					activeFeatureSet.put(featureName,false);
				}
				break;
			case ATTRIBUTE:
				tmpAttributeSet.add(data);
				break;
			case DESTINATION:
				if (destinationFK != null) {
					destinationFV = data;
				} else {
					destinationFK = data;
				}
				break;
			case NAME:
				featureName = data;
				break;
			case AGGREGATOR:
				break;
			case AGGREGATOR_NAME:
				aggregatorList.add(data);
				break;
			case AGGREGATOR_FEATURE:
				tmpAggregatorFeatures.add(data);
				break;
			case AGGREGATOR_PARAMETER:
				tmpAggregatorParameters.add(data);
				break;
			default:
				throw new SAXException("Unknwon tagType "+tagType+" in characters");
		}
	}

	/**
	 * Use to signal end of a current XML tag.
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		switch(tagType) {
			case BATCH_FILE:
				parsed_file_contents = batchSet.toArray(new Batch[]{});
				break;
			case BATCH:
				Batch batch = new Batch();
				
				// Set the files this batch will act on
				try {
					batch.setRecordings(fileSet.toArray(new File[] {}));
				} catch (Exception e) {
					throw new SAXException(e.getMessage());
				}
				
				// set the analysis settings to apply to this batch
				batch.setSettings(windowSize, windowOverlap, sampleRate, normalise,
						saveWindows, overall, outputType);
				batch.setDestination(destinationFK,destinationFV);
				
				// set the features attributes
				batch.setFeatures(activeFeatureSet,featureAttributeSet);
				
				batch.setName(name);
				
				batch.setAggregators(aggregatorList.toArray(new String[]{}), aggregatorFeatures.toArray(new String[][]{}), aggregatorParameters.toArray(new String[][]{}));
				name = null;
				
				batchSet.add(batch);
				fileSet.clear();
				activeFeatureSet = new HashMap<String,Boolean>();
				featureAttributeSet = new HashMap<String,String[]>();
				aggregatorList = new LinkedList<String>();
				aggregatorFeatures = new LinkedList<String[]>();
				aggregatorParameters = new LinkedList<String[]>();
				tmpAttributeSet.clear();
				tmpAggregatorFeatures.clear();
				tmpAggregatorParameters.clear();
				destinationFV = null;
				destinationFK = null;
				tagType = BATCH_FILE;
				break;
			case FILE_SET:
				tagType = BATCH;
				break;
			case FILE:
				File tmp = new File(fileName);
				fileSet.add(tmp);
				tagType = FILE_SET;
				break;
			case SETTINGS:
				tagType = BATCH;
				break;
			case WINDOW_SIZE:
				tagType=SETTINGS;
				break;
			case WINDOW_OVERLAP:
				tagType=SETTINGS;
				break;
			case SAMPLING_RATE:
				tagType=SETTINGS;
				break;
			case NORMALISE:
				tagType=SETTINGS;
				break;
			case PER_WINDOW_STATS:
				tagType=SETTINGS;
				break;
			case OVERALL_STATS:
				tagType = SETTINGS;
				break;
			case OUTPUT_TYPE:
				tagType = SETTINGS;
				break;
			case FEATURE:
				tagType = SETTINGS;
				featureAttributeSet.put(featureName,tmpAttributeSet.toArray(new String[]{}));
				tmpAttributeSet.clear();
				break;
			case ACTIVE:
				tagType = FEATURE;
				break;
			case ATTRIBUTE:
				tagType = FEATURE;
				break;
			case DESTINATION:
				tagType = BATCH;
				break;
			case NAME:
				tagType = FEATURE;
				break;
			case AGGREGATOR:
				aggregatorFeatures.add(tmpAggregatorFeatures.toArray(new String[]{}));
				tmpAggregatorFeatures.clear();
				aggregatorParameters.add(tmpAggregatorParameters.toArray(new String[]{}));
				tmpAggregatorParameters.clear();
				tagType = SETTINGS;
				break;
			case AGGREGATOR_NAME:
				tagType= AGGREGATOR;
				break;
			case AGGREGATOR_FEATURE:
				tagType = AGGREGATOR;
				break;
			case AGGREGATOR_PARAMETER:
				tagType = AGGREGATOR;
				break;
			default:
				throw new SAXException("Unknown tag type "+tagType+" in end element");
		}
	}

	/**
	 * Logical start of the XML document.
	 */
	public void startDocument() throws SAXException {
		fileSet.clear();
		batchSet.clear();
		activeFeatureSet.clear();
		featureAttributeSet.clear();
		tmpAttributeSet.clear();
		aggregatorList.clear();
		aggregatorFeatures.clear();
		tmpAggregatorFeatures.clear();
		tmpAggregatorParameters.clear();
		aggregatorParameters.clear();
		tagType = -1;
	}

	/**
	 * Signals start of an XML tag
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equals("batchFile")||qName.equals("batchFile")) {
			tagType = BATCH_FILE;
		} else if (localName.equals("batch")||qName.equals("batch")) {
	//		System.out.println(attributes.getLength());
			if(attributes.getLength() != 1){
				throw new SAXException("Batch must have an ID attribute");
			}
			name = attributes.getValue(0);
			tagType = BATCH;
		} else if (localName.equals("fileSet")||qName.equals("fileSet")) {
			tagType = FILE_SET;
		} else if (localName.equals("file")||qName.equals("file")) {
			tagType = FILE;
			fileName = "";
		} else if (localName.equals("settings")||qName.equals("settings")) {
			tagType = SETTINGS;
		} else if (localName.equals("windowSize")||qName.equals("windowSize")) {
			tagType = WINDOW_SIZE;
		} else if (localName.equals("windowOverlap")||qName.equals("windowOverlap")) {
			tagType = WINDOW_OVERLAP;
		} else if (localName.equals("samplingRate")||qName.equals("samplingRate")) {
			tagType = SAMPLING_RATE;
		} else if (localName.equals("normalise")||qName.equals("normalise")) {
			tagType = NORMALISE;		
		} else if (localName.equals("perWindowStats")||qName.equals("perWindowStats")) {
			tagType = PER_WINDOW_STATS;
		} else if (localName.equals("overallStats")||qName.equals("overallStats")) {
			tagType = OVERALL_STATS;
		} else if (localName.equals("outputType")||qName.equals("outputType")) {
			tagType = OUTPUT_TYPE;
		} else if (localName.equals("feature")||qName.equals("feature")) {
			tagType = FEATURE;
		}else if (localName.equals("name")||qName.equals("name")){
			tagType=NAME;
		} else if (localName.equals("active")||qName.equals("active")) {
			tagType = ACTIVE;
		} else if (localName.equals("attribute")||qName.equals("attribute")) {
			tagType = ATTRIBUTE;
		} else if (localName.equals("destination")||qName.equals("destination")) {
			tagType = DESTINATION;
		} else if(localName.equals("aggregator")||qName.equals("aggregator")){
			tagType = AGGREGATOR;
		} else if(localName.equals("aggregatorName")||qName.equals("aggregatorName")){
			tagType = AGGREGATOR_NAME;
		} else if(localName.equals("aggregatorFeature")||qName.equals("aggregatorFeature")){
			tagType = AGGREGATOR_FEATURE;
		} else if(localName.equals("aggregatorAttribute")||qName.equals("aggregatorAttribute")){
			tagType = AGGREGATOR_PARAMETER;
		}else{
			throw new SAXException("Unknown tag '" + localName + "'");
		}
	}
	
	
}
