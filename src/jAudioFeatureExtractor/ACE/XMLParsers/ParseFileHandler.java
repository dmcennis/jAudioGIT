/*
 * @(#)ParseFileHandler.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.ACE.XMLParsers;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;


/**
 * An extension of the <i>Xerces</i> <code>XML DefaultHandler</code>
 * class that adds an array of objects that can contain information
 * derived from files during parsing.
 *
 * @see DefaultHandler
 */
public class ParseFileHandler
	extends DefaultHandler
{
	/* PUBLIC FIELDS ***********************************************************/


	/**
	 * Holds the data extracted from the XML file.
	 */
	public Object[] parsed_file_contents;


	/**
	 * Holds any comments extracted from the XML file.
	 */
	public String comments;
}