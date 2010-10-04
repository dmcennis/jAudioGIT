package jAudioFeatureExtractor.ACE.XMLParsers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class for parsing XML documents that store GUI settings.
 * 
 * @author Daniel McEnnis
 */
public class ParseSaveSettings extends ParseFileHandler {

	private HashMap<String,Boolean> checkedMap_;
	
	private HashMap<String,String[]> attributeMap_;
	
	private LinkedList<String> aggregatorNames;
	
	private LinkedList<String[]> aggregatorFeatures;
	
	private LinkedList<String[]> aggregatorParameters;
	
	private LinkedList<String> tmpAggregatorFeature_;
	
	private LinkedList<String> tmpAggregatorParameters_;
	 
	private LinkedList<String> tmpAttributes_;
	
	private String name;

	private String windowLength;;

	private String windowOverlap;

	private double sampleRate;

	private boolean savePerWindow;

	private boolean saveOverall;

	private boolean normalise;

	private int tagType = -1;

	private boolean inFeature = false;

	private int count = 0;

	private String outputType = "";

	Pattern t = Pattern.compile(".*true.*");

	Pattern f = Pattern.compile(".*false.*");

	Matcher tm;

	Matcher fm;

	/**
	 * Logical start of the XML document.
	 */
	public void startDocument() throws SAXException {
		checkedMap_ = new HashMap<String,Boolean>();
		tmpAttributes_ = new LinkedList<String>();
		attributeMap_ = new HashMap<String,String[]>();
		aggregatorNames = new LinkedList<String>();
		aggregatorFeatures = new LinkedList<String[]>();
		aggregatorParameters = new LinkedList<String[]>();
		tmpAggregatorFeature_ = new LinkedList<String>();
		tmpAggregatorParameters_ = new LinkedList<String>();
		count = 0;
		tagType = -1;
	}

	/**
	 * Used to process character data in the XML file.  
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String tmp = new String(ch,start,length);
		tmp = tmp.substring(start, start + length);
		switch (tagType){
			case 0:
				break;
			case 1:
				break;
			case 2:
				windowLength = tmp;
				break;
			case 3:
				windowOverlap = tmp;
				break;
			case 4:
				try {
					sampleRate = Double.parseDouble(tmp);
				} catch (NumberFormatException e) {
					throw new SAXException("sampleRate must be a double");
				}
				break;
			case 5:
				tm = t.matcher(tmp);
				fm = f.matcher(tmp);
				if (tm.matches()) {
					savePerWindow = true;
				} else if (fm.matches()) {
					savePerWindow = false;
				} else {
					throw new SAXException("Valid states are true or false, not "
							+ tmp);
				}
				break;
			case 6:
				tm = t.matcher(tmp);
				fm = f.matcher(tmp);
				if (tm.matches()) {
					saveOverall = true;
				} else if (fm.matches()) {
					saveOverall = false;
				} else {
					throw new SAXException("Valid states are true or false, not "
							+ tmp);
				}
				break;
			case 7:
				tm = t.matcher(tmp);
				fm = f.matcher(tmp);
				if (tm.matches()) {
					normalise = true;
				} else if (fm.matches()) {
					normalise = false;
				} else {
					throw new SAXException("Valid states are true or false, not "
							+ tmp);
				}
				break;
			case 8:
				outputType = tmp;
				break;
			case 9:
				tm = t.matcher(tmp);
				fm = f.matcher(tmp);
				if (tm.matches()) {
					checkedMap_.put(name,true);
				} else if (fm.matches()) {
					checkedMap_.put(name,false);
				} else {
					throw new SAXException("Valid states are true or false, not "
							+ tmp);
				}
				break;
			case 10:
				tmpAttributes_.add(tmp);
				break;
			case 11:
				name = tmp;
				break;
			case 12:
				break;
			case 13:
				aggregatorNames.add(tmp);
				break;
			case 14:
				tmpAggregatorFeature_.add(tmp);
				break;
			case 15:
				tmpAggregatorParameters_.add(tmp);
				break;
			default:
				throw new SAXException("Unknwon Tag Type " + tagType + "in characters");
		}
	}

	/**
	 * Logical end of an XML file
	 */
	public void endDocument() throws SAXException {
		parsed_file_contents = new Object[12];
		int i = 0;
		parsed_file_contents[i++] = windowLength;
		parsed_file_contents[i++] = windowOverlap;
		parsed_file_contents[i++] = new Double(sampleRate);
		parsed_file_contents[i++] = new Boolean(normalise);
		parsed_file_contents[i++] = new Boolean(savePerWindow);
		parsed_file_contents[i++] = new Boolean(saveOverall);
		parsed_file_contents[i++] = outputType;
		parsed_file_contents[i++] = checkedMap_;
		parsed_file_contents[i++] = attributeMap_;
		parsed_file_contents[i++] = aggregatorNames;
		parsed_file_contents[i++] = aggregatorFeatures;
		parsed_file_contents[i] = aggregatorParameters;
		count = -1;
	}

	@Override
	/**
	 * Use to signal end of a current XML tag.
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals("name")||qName.equals("name")){
			tagType = 1;
		}else if (localName.equals("active")||qName.equals("active")) {
			tagType = 1;
		} else if (localName.equals("attribute")||qName.equals("attribute")) {
			tagType = 1;
		} else if (localName.equals("feature")||qName.equals("feature")) {
			attributeMap_.put(name,tmpAttributes_.toArray(new String[] {}));
			tmpAttributes_.clear();
			tagType = 0;
		} else if (localName.equals("aggregatorName")||qName.equals("aggregatorName")){
			tagType=12;
		}else if(localName.equals("aggregatorFeature")||qName.equals("aggregatorFeature")){
			tagType=12;
		}else if(localName.equals("aggregatorAttribute")||qName.equals("aggregatorAttribute")){
			tagType=12;
		}else if(localName.equals("aggregator")||qName.equals("aggregator")){
			aggregatorFeatures.add(tmpAggregatorFeature_.toArray(new String[]{}));
			aggregatorParameters.add(tmpAggregatorParameters_.toArray(new String[]{}));
			tmpAggregatorFeature_.clear();
			tmpAggregatorParameters_.clear();
			tagType = 0;
		}
		else {
			tagType = 0;
		}
	}

	/**
	 * Signals start of an XML tag
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (count == 0) {
			if (!localName.equals("save_settings")&&!qName.equals("name")) {
				throw new SAXException("\n\nIt is in reality of the type ["
						+ localName + "].");
			}
		}
		count++;
		if (localName.equals("feature")||qName.equals("feature")) {
			tagType = 1;
		} else if (localName.equals("windowSize")||qName.equals("windowSize")) {
			tagType = 2;
		} else if (localName.equals("windowOverlap")||qName.equals("windowOverlap")) {
			tagType = 3;
		} else if (localName.equals("samplingRate")||qName.equals("samplingRate")) {
			tagType = 4;
		} else if (localName.equals("perWindowStats")||qName.equals("perWindowStats")) {
			tagType = 5;
		} else if (localName.equals("overallStats")||qName.equals("overallStats")) {
			tagType = 6;
		} else if (localName.equals("normalise")||qName.equals("normalise")) {
			tagType = 7;
		} else if (localName.equals("outputType")||qName.equals("outputType")) {
			tagType = 8;
		} else if (localName.equals("active")||qName.equals("active")) {
			tagType = 9;
		} else if (localName.equals("attribute")||qName.equals("attribute")) {
			tagType = 10;
		} else if(localName.equals("name")||qName.equals("name")){
			tagType = 11;
		} else if(localName.equals("aggregator")||qName.equals("aggregator")){
			tagType = 12;
		} else if(localName.equals("aggregatorName")||qName.equals("aggregatorName")){
			tagType = 13;
		} else if(localName.equals("aggregatorFeature")||qName.equals("aggregatorFeature")){
			tagType = 14;
		} else if(localName.equals("aggregatorAttribute")||qName.equals("aggregatorAttribute")){
			tagType = 15;
		}
	}
}
