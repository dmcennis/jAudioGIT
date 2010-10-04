/*
 * @(#)ParsingXMLErrorHandler.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.ACE.XMLParsers;

import org.xml.sax.*;


/**
 * An implementation of the XML SAX ErrorHandler
 * class. The methods of this class are called by an instance of an 
 * XMLReader while it is parsing an XML document.
 *
 * <p>This particular implementation simply throws unaltered
 * exceptions of all three standard types.
 *
 * @author Cory McKay
 */
public class ParsingXMLErrorHandler
	implements ErrorHandler
{
	public void warning(SAXParseException exception)
		throws SAXParseException
	{
		throw exception;
	}

	public void error(SAXParseException exception)
		throws SAXParseException
	{
		throw exception;

	}

	public void fatalError(SAXParseException exception)
		throws SAXParseException
	{
		throw exception;
	}
}