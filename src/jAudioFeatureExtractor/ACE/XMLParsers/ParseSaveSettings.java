package jAudioFeatureExtractor.ACE.XMLParsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.text.MessageFormat;import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
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
					throw new SAXException(bundle.getString("samplerate.must.be.a.double"));
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
					throw new SAXException(MessageFormat.format(bundle.getString("valid.states.are.true.or.false.not.0"), tmp));
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
					throw new SAXException(MessageFormat.format(bundle.getString("valid.states.are.true.or.false.not.0"), tmp));
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
					throw new SAXException(MessageFormat.format(bundle.getString("valid.states.are.true.or.false.not.0"), tmp));
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
					throw new SAXException(MessageFormat.format(bundle.getString("valid.states.are.true.or.false.not.0"), tmp));
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
				throw new SAXException(String.format(bundle.getString("unknown.tag.type.d.in.characters"),tagType));
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
		if ((localName.compareTo("name")==0)||(qName.compareTo("name")==0)){
			tagType = 1;
		}else if ((localName.compareTo("active")==0)||(qName.compareTo("active")==0)) {
			tagType = 1;
		} else if ((localName.compareTo("attribute")==0)||(qName.compareTo("attribute")==0)) {
			tagType = 1;
		} else if ((localName.compareTo("feature")==0)||(qName.compareTo("feature")==0)) {
			attributeMap_.put(name,tmpAttributes_.toArray(new String[] {}));
			tmpAttributes_.clear();
			tagType = 0;
		} else if ((localName.compareTo("aggregatorName")==0)||(qName.compareTo("aggregatorName")==0)){
			tagType=12;
		}else if((localName.compareTo("aggregatorFeature")==0)||(qName.compareTo("aggregatorFeature")==0)){
			tagType=12;
		}else if((localName.compareTo("aggregatorAttribute")==0)||(qName.compareTo("aggregatorAttribute")==0)){
			tagType=12;
		}else if((localName.compareTo("aggregator")==0)||(qName.compareTo("aggregator")==0)){
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
			if ((localName.compareTo("save_settings")!=0)&&(qName.compareTo("save_settings")!=0)) {
				throw new SAXException("\n\nIt is in reality of the type ["
						+ localName + "].");
			}
		}
		count++;
		if ((localName.compareTo("feature")==0)||(qName.compareTo("feature")==0)) {
			tagType = 1;
		} else if ((localName.compareTo("windowSize")==0)||(qName.compareTo("windowSize")==0)) {
			tagType = 2;
		} else if ((localName.compareTo("windowOverlap")==0)||(qName.compareTo("windowOverlap")==0)) {
			tagType = 3;
		} else if ((localName.compareTo("samplingRate")==0)||(qName.compareTo("samplingRate")==0)) {
			tagType = 4;
		} else if ((localName.compareTo("perWindowStats")==0)||(qName.compareTo("perWindowStats")==0)) {
			tagType = 5;
		} else if ((localName.compareTo("overallStats")==0)||(qName.compareTo("overallStats")==0)) {
			tagType = 6;
		} else if ((localName.compareTo("normalise")==0)||(qName.compareTo("normalise")==0)) {
			tagType = 7;
		} else if ((localName.compareTo("outputType")==0)||(qName.compareTo("outputType")==0)) {
			tagType = 8;
		} else if ((localName.compareTo("active")==0)||(qName.compareTo("active")==0)) {
			tagType = 9;
		} else if ((localName.compareTo("attribute")==0)||(qName.compareTo("attribute")==0)) {
			tagType = 10;
		} else if((localName.compareTo("name")==0)||(qName.compareTo("name")==0)){
			tagType = 11;
		} else if((localName.compareTo("aggregator")==0)||(qName.compareTo("aggregator")==0)){
			tagType = 12;
		} else if((localName.compareTo("aggregatorName")==0)||(qName.compareTo("aggregatorName")==0)){
			tagType = 13;
		} else if((localName.compareTo("aggregatorFeature")==0)||(qName.compareTo("aggregatorFeature")==0)){
			tagType = 14;
		} else if((localName.compareTo("aggregatorAttribute")==0)||(qName.compareTo("aggregatorAttribute")==0)){
			tagType = 15;
		}
	}
}
