/*
 * @(#)ParseDataSetFileHandler.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.ACE.XMLParsers;

import org.xml.sax.*;
import java.util.LinkedList;
import jAudioFeatureExtractor.ACE.DataTypes.DataSet;


/**
 * An extension of the Xerces XML DefaultHandler class that implements the
 * SAX ContentHandler. The methods of this class are called by an instance of
 * an XMLReader while it is parsing an XML document.
 *
 * <p>This particular implementation is custom designed to parse XML files of
 * the feature_vector_file type used by the ACE classification system. A custom
 * exception is thrown if the file is not of this type. At the end of parsing,
 * the contents of the files elements are stored in the parsed_file_contents
 * field.
 *
 * @author Cory McKay
 */
public class ParseDataSetFileHandler
	extends ParseFileHandler
{
	/* FIELDS ******************************************************************/


	/*
	 * DataSet[] parsed_file_contents
	 * Holds the data extracted from the XML file.
	 */
	

	/**
	 * Stores all of the root level data sets in the file.
	 */
	private LinkedList<DataSet>		root_datasets;


	/**
	 * The root-level DataSet currently being processed.
	 */
	private	DataSet					current_root_dataset;
	

	/**
	 * The DataSets that are part of the section of another DataSet
	 * (i.e. through a section element).
	 */
	private LinkedList<DataSet>		subset_datasets;


	/**
	 * The DataSet that is part of a section of a root-level DataSet.
	 */
	private	DataSet					current_subset_dataset;
	

	/**
	 * The names of the features in a given DataSet.
	 */
	private LinkedList<String>		feature_name_list;


	/**
	 * The sets of values for each feature in a DataSet. In the same order
	 * as the feature_name_list field.
	 */
	private LinkedList<double[]>	feature_values_list;


	/**
	 * The value(s) for a particular feature.
	 */
	private	LinkedList<String>		feature_indidual_values_list;


	/**
	 * Identifies what tag has been found
	 */
	private	int						tag_identifier;


	/**
	 * A count of the number of start elements encountered
	 */
	private	int						count;


	/* PUBLIC METHODS **********************************************************/


	/**
	 * This method is called when the start of the XML file to be
	 * parsed is reached. Instantiates the root_datasets field, 
	 * sets other fielsds to null and sets the count to 0.
	 */
	public void startDocument()
	{
		root_datasets = new LinkedList<DataSet>();
		current_root_dataset = null;
		subset_datasets = null;
		current_subset_dataset = null;
		feature_name_list = null;
		feature_values_list = null;
		feature_indidual_values_list = null;
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
			if (!name.equals("feature_vector_file")&&!qName.equals("feature_vector_file"))
				throw new SAXException("\n\nIt is in reality of the type " + name + ".");
		count++;

		// Identify the type of tag
		tag_identifier = 0;
		if (name.equals("data_set")||qName.equals("data_set"))
		{
			// Create a new Dataset and add it to root_datasets.
			current_root_dataset = new DataSet();
			root_datasets.add(current_root_dataset);
		}
		else if (name.equals("section")||qName.equals("section"))
		{
			// Create a new set of sub-sets of a data set if subset_datasets is null
			if (subset_datasets == null)
				subset_datasets = new LinkedList<DataSet>();

			// Create a new sub-set DataSet. Place this DataSet in the list of
			// sub-set DataSets and gives it a reference to it's parent root
			// DataSet.
			current_subset_dataset = new DataSet();
			current_subset_dataset.parent = current_root_dataset;
			subset_datasets.add(current_subset_dataset);

			// Extract the name of the DataSet from the id attribute
			current_subset_dataset.start = Double.parseDouble(atts.getValue(0));
			current_subset_dataset.stop = Double.parseDouble(atts.getValue(1));
		}
		else if (name.equals("feature")||qName.equals("feature"))
		{
			// Prepare the linked lists to store feature values
			if (feature_name_list == null)
			{
				feature_name_list = new LinkedList<String>();
				feature_values_list = new LinkedList<double[]>();
			}
			feature_indidual_values_list = new LinkedList<String>();
		}
		else if (name.equals("data_set_id")||qName.equals("data_set_id"))
			tag_identifier = 1;
		else if (name.equals("name")||qName.equals("name"))
			tag_identifier = 2;
		else if (name.equals("v")||qName.equals("v"))
			tag_identifier = 3;
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
			current_root_dataset.identifier = contents;	
		else if (tag_identifier == 2)
			feature_name_list.add(contents);
		else if (tag_identifier == 3)
			feature_indidual_values_list.add(contents);
	}


	/**
	 * This method is called when the end tag of an XML element
	 * is encountered.
	 *
	 * @param	name			Name of the element that is encountered.
	 */
	public void endElement(String namespace, String name, String qName)
	{
		if (name.equals("data_set")||qName.equals("data_set"))
		{
			// Store feature_name_list and feature_values_list in 
			// current_root_dataset
			if (feature_name_list != null)
			{
				Object[] fnl_obj = (Object[]) feature_name_list.toArray();
				String[] fnl_string = new String[fnl_obj.length];
				for (int i = 0; i < fnl_string.length; i++)
					fnl_string[i] = (String) fnl_obj[i];
				current_root_dataset.feature_names = fnl_string;

				Object[] fvl_obj = (Object[]) feature_values_list.toArray();
				double[][] fvl_doub = new double[fvl_obj.length][];
				for (int i = 0; i < fvl_doub.length; i++)
					fvl_doub[i] = (double[]) fvl_obj[i];
				current_root_dataset.feature_values = fvl_doub;
			}

			// Store the sub-sets of the data set (null if none)
			if (subset_datasets != null)
			{
				Object[] dsl_obj = (Object[]) subset_datasets.toArray();
				DataSet[] dsl_ds = new DataSet[dsl_obj.length];
				for (int i = 0; i < dsl_ds.length; i++)
					dsl_ds[i] = (DataSet) dsl_obj[i];
				current_root_dataset.sub_sets = dsl_ds;
			}

			// Reset variables
			feature_name_list = null;
			feature_values_list = null;
			subset_datasets = null;
			current_root_dataset = null;
		}
		else if (name.equals("section")||qName.equals("section"))
		{
			// Store feature_name_list and feature_values_list in 
			// current_subset_dataset
			if (feature_name_list != null)
			{
				Object[] fnl_obj = (Object[]) feature_name_list.toArray();
				String[] fnl_string = new String[fnl_obj.length];
				for (int i = 0; i < fnl_string.length; i++)
					fnl_string[i] = (String) fnl_obj[i];
				current_subset_dataset.feature_names = fnl_string;

				Object[] fvl_obj = (Object[]) feature_values_list.toArray();
				double[][] fvl_doub = new double[fvl_obj.length][];
				for (int i = 0; i < fvl_doub.length; i++)
					fvl_doub[i] = (double[]) fvl_obj[i];
				current_subset_dataset.feature_values = fvl_doub;
			}

			// Reset variables
			feature_name_list = null;
			feature_values_list = null;
			current_subset_dataset = null;
		}
		else if (name.equals("feature")||qName.equals("feature"))
		{
			// Convert the feature_indidual_values_list into doubles
			// and store it in feature_values_list
			Object[] fvs_obj = (Object[]) feature_indidual_values_list.toArray();
			double[] fvs_double = new double[fvs_obj.length];
			for (int i = 0; i < fvs_double.length; i++)
				fvs_double[i] = Double.parseDouble( (String) fvs_obj[i] );
			
			// Reset variables
			feature_values_list.add(fvs_double);
			feature_indidual_values_list = null;
		}
	}

	
	/**
	 * This method is called when the end tag of an XML element
	 * is encountered. Fills the parsed_file_contents
	 * field with the DataSets.
	 */
	public void endDocument()
	{
		// Put contents of tree into parsed_file_contents
		parsed_file_contents = root_datasets.toArray();
	}
}