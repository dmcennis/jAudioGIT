/*
 * @(#)XMLDocumentParser.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.ACE.XMLParsers;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.util.ResourceBundle;


/**
 * A holder class for the <code>XMLDocumentParser</code> method.
 * This method is a general purpose method for loading an XML
 * file, testing that the file exists, validating it as a valid
 * XML file, ensuring that it is of the correct type, parsing
 * it and extracting its data into the required form. Informative
 * error exceptions are thrown in a format that can be displayed
 * directly to users.
 *
 * <p>Custom handlers can be written to properly extract
 * information from arbitrary XML files. The types of files
 * currently implemented are: feature_vector_file, feature_key_file,
 * taxonomy_file and classifications_file. See the file handlers 
 * for each of these file types for more information on the kind 
 * of data returned.
 *
 * @author Cory McKay
 */
public class XMLDocumentParser
{
	/**
	 * This method is a general purpose method for loading an XML
	 * file, testing that the file exists, validating it as a valid
	 * XML file, ensuring that it is of the correct type, parsing
	 * it and extracting its data into the required form. Informative
	 * error exceptions are thrown in a format that can be displayed
	 * directly to users.
	 *
	 * <p>Custom handlers can be written to properly extract
	 * information from arbitrary XML files. The types of files
	 * currently implemented are: feature_vector_file, feature_key_file,
	 * taxonomy_file and classifications_file. See the file handlers for
	 * each of these file types for more information on the kind of data
	 * returned.
	 *
	 * @param	file_path		The path of an XML file that will be parsed.
	 * @param	document_type	The type of XML file. Defined by the name 
	 *							of the first element in the file.
	 * @return					An array of objects containing information
	 *							extracted from the XML file. Object types
	 *							depend on type of document parsed.
	 * @throws	Exception		Informative exceptions are thrown if an
	 *							invalid file path is specified.
	 */
	public static Object parseXMLDocument(String file_path, String document_type)
		throws Exception
	{
		// Verify that the file referred to in file_path exists and is not a directory
		File test_file = new File(file_path);
		if (!test_file.exists()) {
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
            throw new Exception(String.format(bundle.getString("the.specified.path.s.does.not.refer.to.an.existing.file"), file_path));
        }
		if (test_file.isDirectory()) {
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
            throw new Exception(String.format(bundle.getString("the.specified.path.s.refers.to.a.directory.not.to.a.file"),file_path));
        }
		// Prepare the XML parser with the validation feature on and the error handler
		// set to throw exceptions on all warnings and errors
		javax.xml.parsers.SAXParser reader = javax.xml.parsers.SAXParserFactory.newInstance().newSAXParser();
//		reader.setFeature("http://xml.org/sax/features/validation", true);
//		reader.setErrorHandler(new ParsingXMLErrorHandler());
		ParseFileHandler handler;

        ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		// Choose the correct type handler based on the type of XML file
		if (document_type.compareTo("feature_vector_file")==0)
			handler = new ParseDataSetFileHandler();
		else if (document_type.compareTo("feature_key_file")==0)
			handler = new ParseFeatureDefinitionsFileHandler();
		else if (document_type.compareTo("taxonomy_file")==0)
			handler = new ParseTaxonomyFileHandler();
		else if (document_type.compareTo("classifications_file")==0)
			handler = new ParseClassificationsFileHandler();
		else if (document_type.compareTo("save_settings")==0)
			handler = new ParseSaveSettings();
		else if (document_type.compareTo("batchFile")==0)
			handler = new ParseBatchJobHandler();
		else if (document_type.compareTo("feature_list")==0)
			handler = new FeatureListHandler();

		// Throw an exception if an unknown type of XML file is specified
		else throw new Exception(String.format(bundle.getString("invalid.type.of.xml.file.specified.the.xml.file.type.s.is.not.known"),document_type));

		// Parse the file so that the contents are available in the parsed_file_contents field of the handler
//		reader.setContentHandler(handler);
		try {reader.parse(file_path,handler);}
		catch (SAXParseException e) // throw an exception if the file is not a valid XML file
		{
			throw new Exception(String.format(bundle.getString("the.s.file.is.not.a.valid.xml.file.n.ndetails.of.the.problem.s.n.nthis.error.is.likely.in.the.region.of.line.s"),file_path,e.getLocalizedMessage(),e.getLineNumber()));
		}
		catch (SAXException e) // throw an exception if the file is not an XML file of the correct type
		{
			throw new Exception(String.format(bundle.getString("the.s.file.must.be.of.type.s.s"),file_path,document_type,e.getLocalizedMessage()));
		}
		catch (Exception e) // throw an exception if the file is not an XML file of the correct type
		{
			throw new Exception(String.format(bundle.getString("the.s.file.is.not.formatted.properly.n.ndetails.of.the.problem.s"),file_path,e.getLocalizedMessage()));
		}

		// Return the contents of the parsed file
		return handler.parsed_file_contents;
	}
}
