package jAudioFeatureExtractor.ACE.XMLParsers;

import jAudioFeatureExtractor.Aggregators.Aggregator;
import jAudioFeatureExtractor.Aggregators.AggregatorContainer;
import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.AudioFeatures.MetaFeatureFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class FeatureListHandler extends ParseFileHandler {

	public static final int FEATURE_LIST = 0;

	public static final int FEATURE = 1;

	public static final int CLASS = 2;

	public static final int ON = 4;
	
	public static final int PLUGIN_LOCATION = 5;
	
	public static final int AGGREGATOR = 6;
	
	public boolean firstTag = true;
	
	LinkedList<Boolean> b;
	
	LinkedList<FeatureExtractor> f;
	
	LinkedList<Aggregator> a;
	
	URLClassLoader classLoader;

	int tagType = -1;

	boolean isOn = false;

	String className = "";
		
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		switch (tagType) {
		case FEATURE_LIST:
			break;
		case FEATURE:
			break;
		case CLASS:
			className = new String(ch, start, length);
			break;
		case ON:
			break;
		case PLUGIN_LOCATION:
			try {
					URL[] pluginURL = new URL[]{new URL(new String(ch,start,length))};
					classLoader =URLClassLoader.newInstance(pluginURL,java.lang.Thread.currentThread().getContextClassLoader());
//					java.lang.Thread.currentThread().setContextClassLoader(classLoader);
				} catch (MalformedURLException e) {
					throw new SAXException("Plugin location not a valid URL:"+new String(ch,start,length));
				}
				break;
		case AGGREGATOR:
			className = new String(ch,start,length);
			break;
		default:
			if(!firstTag){
				throw new SAXException("Unknown tagType found (" + tagType + ") - content='"+new String(ch,start,length)+"'");
			}
		}
	}

	
	public void endDocument() throws SAXException {
		parsed_file_contents = new Object[3];
		parsed_file_contents[0] = f;
		parsed_file_contents[1] = b;
		parsed_file_contents[2] = a;
	}

	
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals("featureList")||qName.equals("featureList")) {
			tagType = -1;
		} else if (localName.equals("feature")||localName.equals("metaFeature")||qName.equals("feature")||qName.equals("metaFeature")) {
			try {
				Class c = null;
//				try{
					c = Class.forName(className,true,classLoader);
//				}catch(ClassNotFoundException e){
//					c = Class.forName(className);
//				}
				Object o = (c.getConstructor(new Class[]{})).newInstance(null);
				f.add((FeatureExtractor)o);
				if(isOn){
					b.add(true);
				}else{
					b.add(false);
				}
				isOn=false;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new SAXException("<IllegalAccessException> Class '"+className+"' does not have a zero argument constructor");
			} catch (SecurityException e) {
				e.printStackTrace();
				throw new SAXException("<SecurityException> Class '"+className+"' does not have permission to create a new object of that class");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new SAXException("<ClassNotFoundException> Class '"+className+"' does not exist in the path.");
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new SAXException("<InstantiationException> Class '"+className+"' is either abstract or an interface");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new SAXException("<IllegalAccessException> Class '"+className+"' could not be created");
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new SAXException("<InvocationTargetException> Class '"+className+"' threw an execption in its constructor");
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				throw new SAXException("<NoSuchMethodException> Class '"+className+"' could not find its constructor");
			} catch (ClassCastException e){
				e.printStackTrace();
				throw new SAXException("<ClassCastException> Class '"+className+"' was succesfully created but was not a subclass of FeatureExtractor: "+e.getMessage());
			}
			tagType = FEATURE_LIST;
		}else if (localName.equals("class")||qName.equals("class")){
			tagType = FEATURE;
		}else if (localName.equals("on")||qName.equals("on")){
			tagType = FEATURE;
		}else if (localName.equals("pluginFolder")||qName.equals("pluginFolder")){
			tagType = FEATURE_LIST;
		}else if(localName.equals("aggregator")||qName.equals("aggregator")){
			try {
				Class c = null;
//				try{
					c = Class.forName(className,true,classLoader);
//				}catch(ClassNotFoundException e){
//					c = Class.forName(className);
//				}
				Object o = (c.getConstructor(new Class[]{})).newInstance(null);
				a.add((Aggregator)o);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new SAXException("<IllegalAccessException> Class '"+className+"' does not have a zero argument constructor");
			} catch (SecurityException e) {
				e.printStackTrace();
				throw new SAXException("<SecurityException> Class '"+className+"' does not have permission to create a new object of that class");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new SAXException("<ClassNotFoundException> Class '"+className+"' does not exist in the path.");
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new SAXException("<InstantiationException> Class '"+className+"' is either abstract or an interface");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new SAXException("<IllegalAccessException> Class '"+className+"' could not be created");
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new SAXException("<InvocationTargetException> Class '"+className+"' threw an execption in its constructor");
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				throw new SAXException("<NoSuchMethodException> Class '"+className+"' could not find its constructor");
			} catch (ClassCastException e){
				e.printStackTrace();
				throw new SAXException("<ClassCastException> Class '"+className+"' was succesfully created but was not a subclass of Aggregator: "+e.getMessage());
			}
			tagType = FEATURE_LIST;
		}else{
			throw new SAXException("Unknown tag type "+tagType+" discovered - content='"+localName+"'");
		}
	}

	
	public void startDocument() throws SAXException {
		f = new LinkedList<FeatureExtractor>();
		b = new LinkedList<Boolean>();
		a = new LinkedList<Aggregator>();
		classLoader  = new URLClassLoader(new URL[]{});
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
			firstTag=false;
		if (localName.equals("featureList")||qName.equals("featureList")) {
			tagType = FEATURE_LIST;
		} else if (localName.equals("feature")||qName.equals("feature")) {
			tagType = FEATURE;
		} else if (localName.equals("class")||qName.equals("class")) {
			tagType = CLASS;
		}else if(localName.equals("on")||qName.equals("on")){
			tagType = ON;
			isOn = true;
		}else if(localName.equals("pluginFolder")||qName.equals("pluginFolder")){
			tagType = PLUGIN_LOCATION;
		}else if(localName.equals("aggregator")||qName.equals("aggregator")){
			tagType = AGGREGATOR;
		}else{
			throw new SAXException("Unknown tagType "+tagType +" discovered - content='"+localName+"'"+"'"+qName+"'"+uri+"'");
		}
	}

}
