/*
 * @(#)ParseClassificationsFileHandler.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.ACE.XMLParsers;

import org.xml.sax.*;
import java.util.LinkedList;
import jAudioFeatureExtractor.ACE.DataTypes.SegmentedClassification;


/**
 * An extension of the Xerces XML DefaultHandler class that implements the
 * SAX ContentHandler. The methods of this class are called by an instance of
 * an XMLReader while it is parsing an XML document.
 *
 * <p>This particular implementation is custom designed to parse XML files of
 * the classifications_file type used by the ACE classification system. A custom
 * exception is thrown if the file is not of this type. At the end of parsing,
 * the contents of the files elements are stored in the parsed_file_contents
 * field.
 *
 * @author Cory McKay
 */
public class ParseClassificationsFileHandler
	extends ParseFileHandler
{
	/* FIELDS ******************************************************************/


	/*
	 * SegmentedClassification[] parsed_file_contents
	 * Holds the data extracted from the XML file.
	 */
	

	/**
	 * Stores all of the root level SegmentedClassifications in the file.
	 */
	private LinkedList<SegmentedClassification>	root_classifications;


	/**
	 * The root-level SegmentedClassification currently being processed.
	 */
	private	SegmentedClassification				current_root_classification;
	

	/**
	 * The SegmentedClassifications that are part of a sub-classification of
	 * a root SegmentedClassification (i.e. through a section element).
	 */
	private LinkedList<SegmentedClassification>	subset_classifications;


	/**
	 * The SegmentedClassification that is a sub-set of a root-level
	 * SegmentedClassification that is currently being processed.
	 */
	private	SegmentedClassification				current_subset_classification;
	

	/**
	 * The class(es) that the SegmentedClassification currently being processed
	 * belong to.
	 */
	private	LinkedList<String>					classes;


	/**
	 * The keys of the meta-data fields of the current SegmentedClassification.
	 */
	private LinkedList<String>					meta_data_keys;
	

	/**
	 * The content of the meta-data fields of the current SegmentedClassification.
	 */
	private LinkedList<String>					meta_data_info;


	/**
	 * Identifies what tag has been found
	 */
	private	int									tag_identifier;


	/**
	 * A count of the number of start elements encountered
	 */
	private	int									count;


	/* PUBLIC METHODS **********************************************************/


	/**
	 * This method is called when the start of the XML file to be
	 * parsed is reached. Instantiates the root_classifications field, 
	 * sets other fielsds to null and sets the count to 0.
	 */
	public void startDocument()
	{
		root_classifications = new LinkedList<SegmentedClassification>();
		current_root_classification = null;
		subset_classifications = null;
		current_subset_classification = null;
		classes = null;
		meta_data_keys = null;
		meta_data_info = null;
		count = 0;
	}


	/**
	 * This method is called when the start of an XML element
	 * is encountered. Instantiates new objects when necessary
	 * and lets the characters method know what kind of action
	 * to take.
	 *
	 * @param	name			Name of the element that is encountered.
	 * @param	atts			The attributes encountered.
	 * @throws	SAXException	Exception thrown if is wrong type of XML file.
	 */
	public void startElement(String namespace, String name, String qName, Attributes atts)
		throws SAXException
	{
		// Make sure is correct file type
		if (count == 0)
			if (!name.equals("classifications_file")&&!qName.equals("classifications_file"))
				throw new SAXException("\n\nIt is in reality of the type " + name + ".");
		count++;

		// Identify the type of tag
		tag_identifier = 0;
		if (name.equals("data_set")||qName.equals("data_set"))
		{
			// Create a new SegmentedClassification and add it to root_classifications.
			current_root_classification = new SegmentedClassification();
			root_classifications.add(current_root_classification);
		}
		else if (name.equals("section")||qName.equals("section"))
		{
			// Create a new set of sub-sets of a SegmentedClassificaiton if
			// subset_classifications is null
			if (subset_classifications == null)
				subset_classifications = new LinkedList<SegmentedClassification>();

			// Create a new sub-set SegmentedClassificaiton. Place this
			// SegmentedClassificaiton in the list of sub-set SegmentedClassificaitons..
			current_subset_classification = new SegmentedClassification();
			subset_classifications.add(current_subset_classification);
		}
		else if (name.equals("class")||qName.equals("class"))
		{
			// Create a new set of classes if classes is null
			if (classes == null)
				classes = new LinkedList<String>();

			// Notify the characters method
			tag_identifier = 1;
		}
		else if (name.equals("misc_info")||qName.equals("misc_info"))
		{
			// Create a new set of meta_data_keys and meta_data_info
			// if meta_data_keys is null
			if (meta_data_keys == null)
			{
				meta_data_keys = new LinkedList<String>();
				meta_data_info = new LinkedList<String>();
			}

			// Extract the name of the meta-data key from the attribute
			// and store it
			meta_data_keys.add(atts.getValue(0));

			// Notify the characters method
			tag_identifier = 2;
		}
		else if (name.equals("data_set_id")||qName.equals("data_set_id"))
			tag_identifier = 3;
		else if (name.equals("role")||qName.equals("role"))
			tag_identifier = 4;
		else if (name.equals("start")||qName.equals("start"))
			tag_identifier = 5;
		else if (name.equals("stop")||qName.equals("stop"))
			tag_identifier = 6;
	}


	/**
	 * This method responds to the contents of tags in a way
	 * determined by the name of the tag (as determined by the
	 * startElement method).
	 */
	public void characters(char[] ch, int start, int length)
	{
		String contents = new String(ch, start, length);
		if (tag_identifier == 1)
			classes.add(contents);	
		else if (tag_identifier == 2)
			meta_data_info.add(contents);
		else if (tag_identifier == 3)
			current_root_classification.identifier = contents;
		else if (tag_identifier == 4)
			current_root_classification.role = contents;
		else if (tag_identifier == 5)
			current_subset_classification.start = Double.parseDouble(contents);
		else if (tag_identifier == 6)
			current_subset_classification.stop = Double.parseDouble(contents);
	}


	/**
	 * This method is called when the end tag of an XML element
	 * is encountered.
	 *
	 * @param	name			Name of the element that is encountered.
	 */
	public void endElement(String namespace, String name, String qName)
	{
		if (name.equals("data_set"))
		{
			// Store the classes in current_root_classification
			if (classes != null)
			{
				Object[] obj = (Object[]) classes.toArray();
				String[] str = new String[obj.length];
				for (int i = 0; i < str.length; i++)
					str[i] = (String) obj[i];
				current_root_classification.classifications = str;
			}

			// Store the meta_data_keys in current_root_classification
			if (meta_data_keys != null)
			{
				Object[] obj = (Object[]) meta_data_keys.toArray();
				String[] str = new String[obj.length];
				for (int i = 0; i < str.length; i++)
					str[i] = (String) obj[i];
				current_root_classification.misc_info_key = str;
			}

			// Store the meta_data_info in current_root_classification
			if (meta_data_info != null)
			{
				Object[] obj = (Object[]) meta_data_info.toArray();
				String[] str = new String[obj.length];
				for (int i = 0; i < str.length; i++)
					str[i] = (String) obj[i];
				current_root_classification.misc_info_info = str;
			}

			// Store the sub-sets of the data set (null if none)
			if (subset_classifications != null)
			{
				Object[] obj = (Object[]) subset_classifications.toArray();
				SegmentedClassification[] sc = new SegmentedClassification[obj.length];
				for (int i = 0; i < sc.length; i++)
					sc[i] = (SegmentedClassification) obj[i];
				current_root_classification.sub_classifications = sc;
			}

			// Reset variables
			classes = null;
			meta_data_keys = null;
			meta_data_info = null;
			subset_classifications = null;
			current_root_classification = null;
		}
		else if (name.equals("section"))
		{
			// Store the classes in current_subset_classification
			if (classes != null)
			{
				Object[] obj = (Object[]) classes.toArray();
				String[] str = new String[obj.length];
				for (int i = 0; i < str.length; i++)
					str[i] = (String) obj[i];
				current_subset_classification.classifications = str;
			}

			// Reset variables
			classes = null;
			current_subset_classification = null;
		}
	}

	
	/**
	 * This method is called when the end tag of an XML element
	 * is encountered. Fills the parsed_file_contents
	 * field with the SegmentedClassifications.
	 */
	public void endDocument()
	{
		// Put contents of tree into parsed_file_contents
		parsed_file_contents = root_classifications.toArray();
	}
}