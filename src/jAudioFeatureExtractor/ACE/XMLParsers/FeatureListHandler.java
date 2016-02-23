package jAudioFeatureExtractor.ACE.XMLParsers;

import jAudioFeatureExtractor.Aggregators.Aggregator;
import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;import java.util.LinkedList;
import java.util.ResourceBundle;

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
					classLoader =URLClassLoader.newInstance(pluginURL, Thread.currentThread().getContextClassLoader());
//					java.lang.Thread.currentThread().setContextClassLoader(classLoader);
				} catch (MalformedURLException e) {
                ResourceBundle bundle = ResourceBundle.getBundle("Translations");
                throw new SAXException(String.format(bundle.getString("plugin.location.not.a.valid.url.s"),new String(ch,start,length)));
				}
				break;
		case AGGREGATOR:
			className = new String(ch,start,length);
			break;
		default:
			if(!firstTag){
                ResourceBundle bundle = ResourceBundle.getBundle("Translations");
                throw new SAXException(String.format(bundle.getString("unknown.tagtype.found.d.content.s"),tagType,new String(ch,start,length)));
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
		if ((localName.compareTo("featureList")==0)||(qName.compareTo("featureList")==0)) {
			tagType = -1;
		} else if ((localName.compareTo("feature")==0)||(localName.compareTo("metaFeature")==0)||(qName.compareTo("feature")==0)||(qName.compareTo("metaFeature")==0)) {
			ResourceBundle bundle= ResourceBundle.getBundle("Translations");
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
				throw new SAXException(String.format(bundle.getString("illegalaccessexception.class.s.does.not.have.a.zero.argument.constructor"),className));
			} catch (SecurityException e) {
				e.printStackTrace();
				throw new SAXException(String.format(bundle.getString("securityexception.class.s.does.not.have.permission.to.create.a.new.object.of.that.class"),className));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new SAXException(String.format(bundle.getString("classnotfoundexception.class.s.does.not.exist.in.the.path"),className));
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new SAXException(String.format(bundle.getString("instantiationexception.class.s.is.either.abstract.or.an.interface"),className));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new SAXException(String.format(bundle.getString("illegalaccessexception.class.s.could.not.be.created"),className));
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new SAXException(String.format(bundle.getString("invocationtargetexception.class.s.threw.an.execption.in.its.constructor"),className));
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				throw new SAXException(String.format(bundle.getString("nosuchmethodexception.class.s.could.not.find.its.constructor"),className));
			} catch (ClassCastException e){
				e.printStackTrace();
				throw new SAXException(String.format(bundle.getString("classcastexception.class.s.was.succesfully.created.but.was.not.a.subclass.of.featureextractor.s"),className,e.getMessage()));
			}
			tagType = FEATURE_LIST;
		}else if ((localName.compareTo("class")==0)||(qName.compareTo("class")==0)){
			tagType = FEATURE;
		}else if ((localName.compareTo("on")==0)||(qName.compareTo("on")==0)){
			tagType = FEATURE;
		}else if ((localName.compareTo("pluginFolder")==0)||(qName.compareTo("pluginFolder")==0)){
			tagType = FEATURE_LIST;
		}else if((localName.compareTo("aggregator")==0)||(qName.compareTo("aggregator")==0)){
			ResourceBundle bundle= ResourceBundle.getBundle("Translations");
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
				throw new SAXException(String.format(bundle.getString("illegalaccessexception.class.s.does.not.have.a.zero.argument.constructor"),className));
			} catch (SecurityException e) {
				e.printStackTrace();
				throw new SAXException(String.format(bundle.getString("securityexception.class.s.does.not.have.permission.to.create.a.new.object.of.that.class"),className));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new SAXException(String.format(bundle.getString("classnotfoundexception.class.s.does.not.exist.in.the.path"),className));
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new SAXException(String.format(bundle.getString("instantiationexception.class.s.is.either.abstract.or.an.interface"),className));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new SAXException(String.format(bundle.getString("illegalaccessexception.class.s.could.not.be.created"),className));
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new SAXException(String.format(bundle.getString("invocationtargetexception.class.s.threw.an.execption.in.its.constructor"),className));
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				throw new SAXException(String.format(bundle.getString("nosuchmethodexception.class.s.could.not.find.its.constructor"),className));
			} catch (ClassCastException e){
				e.printStackTrace();
				throw new SAXException(String.format(bundle.getString("classcastexception.class.s.was.succesfully.created.but.was.not.a.subclass.of.aggregator.s"),className,e.getLocalizedMessage()));
			}
			tagType = FEATURE_LIST;
		}else{
			ResourceBundle bundle= ResourceBundle.getBundle("Translations");
			throw new SAXException(String.format(bundle.getString("unknown.tag.type.s.discovered.content.s"),tagType,localName));
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
		if ((localName.compareTo("featureList")==0)||(qName.compareTo("featureList")==0)) {
			tagType = FEATURE_LIST;
		} else if ((localName.compareTo("feature")==0)||(qName.compareTo("feature"))==0) {
			tagType = FEATURE;
		} else if ((localName.compareTo("class")==0)||(qName.compareTo("class")==0)) {
			tagType = CLASS;
		}else if((localName.compareTo("on")==0)||(qName.compareTo("on")==0)){
			tagType = ON;
			isOn = true;
		}else if((localName.compareTo("pluginFolder")==0)||(qName.compareTo("pluginFolder"))==0){
			tagType = PLUGIN_LOCATION;
		}else if((localName.compareTo("aggregator")==0)||(qName.compareTo("aggregator"))==0){
			tagType = AGGREGATOR;
		}else{
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
            throw new SAXException(String.format(MessageFormat.format(bundle.getString("unknown.tagtype.d.0.discovered.content.s.s.s"), tagType),tagType,localName,qName,uri));
		}
	}

}
