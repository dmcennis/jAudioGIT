/*
 * @(#)ParseTaxonomyFileHandler.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.ACE.XMLParsers;

import org.xml.sax.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


/**
 * An extension of the Xerces XML DefaultHandler class that implements the
 * SAX ContentHandler. The methods of this class are called by an instance of
 * an XMLReaderwhile it is parsing an XML document.
 *
 * <p>This particular implementation is custom designed to parse XML files of 
 * the taxonomy_file type used by the ACE classification system. A custom
 * exception is thrown if the file is not of this type. At the end of parsing,
 * the contents of the file elements are stored in the parsed_file_contents
 * field as a one element array holding a DefaultTreeModel.
 *
 * @author Cory McKay
 */
public class ParseTaxonomyFileHandler
	extends ParseFileHandler
{
	/* PRIVATE FIELDS **********************************************************/


	/*
	 * DefaultTreeModel[] parsed_file_contents
	 * Holds the data extracted from the XML file.
	 */
	

	/*
	 * Whether or not the element currently being parsed is the first in the file.
	 */
	private boolean					is_first_element;


	/**
	 * Used to store the parsed contents of the file.
	 */
	private DefaultMutableTreeNode	current_node;


	/**
	 * Used to remember if in an element field is only a comment
	 */
	private boolean					is_comment;


	/* PUBLIC METHODS **********************************************************/


	/**
	 * This method is called when the start of the XML file to be
	 * parsed is reached. Initializes the fields.
	 */
	public void startDocument()
	{
		current_node = new DefaultMutableTreeNode("Taxonomy");
		is_first_element = true;
	}


	/**
	 * This method is called when the end of the XML file being
	 * parsed is reached. Puts the parsed file contents into the
	 * parsed_file_contents array in the form of a DefaultTreeModel.
	 */
	public void endDocument()
	{
		parsed_file_contents = new DefaultTreeModel[1];
		parsed_file_contents[0] = new DefaultTreeModel(current_node.getRoot());
	}
	

	/**
	 * This method is called when the start of an XML element
	 * is encountered. Throws an exception if the XML document
	 * is the wrong type of XML document.
	 *
	 * @param	name			Name of the element that is encountered.
	 * @throws	SAXException	Exception thrown if is wrong type of XML file.
	 */
	public void startElement(String namespace, String name, String qName, Attributes atts)
		throws SAXException
	{
		if (is_first_element == true)
		{
			if (!name.equals("taxonomy_file")&&!qName.equals("taxonomy_file"))
				throw new SAXException("\n\nIt is in reality of the type " + name + ".");
			is_first_element = false;
		}

		if (name.equals("comments")||qName.equals("comments"))
			is_comment = true;
		else
			is_comment = false;
	}


	/**
	 * This method is called when the end tag of an XML element
	 * is encountered. Moves the tree pointer to the root of the
	 * tree if this is the last element. Moves the tree pointer
	 * to the parent node of where it is currently pointing if this
	 * is not the end tag of a category_name element.
	 *
	 * @param	name			Name of the element that is encountered.
	 */
	public void endElement(String namespace, String name, String qName)
	{
		if (name.equals("taxonomy_file")||qName.equals(""))
			current_node = (DefaultMutableTreeNode) current_node.getRoot();
		else if (name.equals("sub_class") || name.equals("parent_class")||qName.equals("sub_class")||qName.equals("parent_class"))
			current_node = (DefaultMutableTreeNode) current_node.getParent();
	}


	/**
	 * This method creates a new node of the tree and places the
	 * textual content of the element in the new node. The tree
	 * pointer is then moved to this new child.
	 */
	public void characters(char[] ch, int start, int length)
	{
		String label = new String(ch, start, length);
		if (is_comment == false)
		{
			DefaultMutableTreeNode new_node = new DefaultMutableTreeNode(label);
			current_node.add(new_node);
			current_node = new_node;
		}
		else
			comments = label;
	}
}
