/*
 * @(#)ParseFeatureDefinitionssFileHandler.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.ACE.XMLParsers;

import org.xml.sax.*;
import java.util.LinkedList;
import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;


/**
 * An extension of the Xerces XML DefaultHandler class that implements the
 * SAX ContentHandler. The methods of this class are called by an instance of
 * an XMLReader while it is parsing an XML document.
 *
 * <p>This particular implementation is custom designed to parse XML files of
 * the feature_key_file type used by the ACE classification system. A custom
 * exception is thrown if the file is not of this type. At the end of parsing,
 * the contents of the files elements are stored in the parsed_file_contents
 * field.
 *
 * @author Cory McKay
 */
public class ParseFeatureDefinitionsFileHandler
	extends ParseFileHandler
{
	/* FIELDS ******************************************************************/


	/*
	 * FeatureDefinition[] parsed_file_contents
	 * Holds the data extracted from the XML file.
	 */
	

	/**
	 * Holds the FeatureDefinitions
	 */
	private	LinkedList<FeatureDefinition>	definitions;


	/**
	 * The FeatureDefinition currently being filled
	 */
	private	FeatureDefinition				current_definition;
	

	/**
	 * Identifies what tag has been found
	 */
	private	int								tag_identifier;


	/**
	 * A count of the number of start elements encountered
	 */
	private	int								count;


	/* PUBLIC METHODS **********************************************************/


	/**
	 * This method is called when the start of the XML file to be
	 * parsed is reached. Instantiates the definitions field
	 * and sets the count to 0.
	 */
	public void startDocument()
	{
		definitions = new LinkedList<FeatureDefinition>();
		count = 0;
	}


	/**
	 * This method is called when the start of an XML element
	 * is encountered. Instantiates a new FeatureDefinition
	 * object if a feature tag was encountered. Otherwise lets
	 * the characters method know what kind of action to take.
	 *
	 * @param	name			Name of the element that is encountered.
	 * @throws	SAXException	Exception thrown if is wrong type of XML file.
	 */
	public void startElement(String namespace, String name, String qName, Attributes atts)
		throws SAXException
	{
		// Make sure is correct file type
		if (count == 0)
			if (!name.equals("feature_key_file")&&!qName.equals("feature_key_file"))
				throw new SAXException("\n\nIt is in reality of the type " + name + ".");
		count++;

		// Identify the type of tag
		tag_identifier = 0;
		if (name.equals("feature")||qName.equals("feature"))
			current_definition = new FeatureDefinition();
		else if (name.equals("name")||qName.equals("name"))
			tag_identifier = 1;
		else if (name.equals("description")||qName.equals("description"))
			tag_identifier = 2;
		else if (name.equals("is_sequential")||qName.equals("is_sequential"))
			tag_identifier = 3;
		else if (name.equals("parallel_dimensions")||qName.equals("parallel_dimensions"))
			tag_identifier = 4;
	}


	/**
	 * This method responds to the contents of tags in a way
	 * determined by the name of the tag (as determined by the
	 * startElement method).
	 * 
	 * <p>The FeatureDefinition object represented by
	 * current_definition is filled by the contents
	 * of the name, description, is_sequential and
	 * parallel_dimensions tags.
	 */
	public void characters(char[] ch, int start, int length)
	{
		String contents = new String(ch, start, length);

		if (tag_identifier == 1)
			current_definition.name = contents;
		else if (tag_identifier == 2)
			current_definition.description = contents;
		else if (tag_identifier == 3)
		{
			if (contents.equals("false"))
				current_definition.is_sequential = false;
			else
				current_definition.is_sequential = true;
		}
		else if (tag_identifier == 4)
			current_definition.dimensions = Integer.parseInt(contents);
	}


	/**
	 * This method is called when the end tag of an XML element
	 * is encountered. Adds the current_definition to the
	 * definitions list when the feature end tag is encountered.
	 *
	 * @param	name			Name of the element that is encountered.
	 */
	public void endElement(String namespace, String name, String qName)
	{
		if (name.equals("feature")||qName.equals("feature"))
			definitions.add(current_definition);
	}

	
	/**
	 * This method is called when the end tag of an XML element
	 * is encountered. Fills the parsed_file_contents
	 * field with the definitions LinkedList.
	 */
	public void endDocument()
	{
		parsed_file_contents = definitions.toArray();
	}
}