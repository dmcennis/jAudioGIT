/*
 * @(#)Sorter.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.GeneralTools;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.text.Collator;


/**
 * A holder class for static methods relating to sorting.
 *
 * @author	Cory McKay
 * @see		Collator
 */
public class Sorter 
{
	/**
	 * Takes the tree with the given root and reorders it so that all siblings are
	 * sorted alphebetically. Recurses through the children so that each level is
	 * sorted alphabetically. Returns the root of the new sorted tree.
	 *
	 * @param	root_unsorted_tree The root of the tree to be sorted.
	 * @return	A copy of the tree that has been sorted.
	 * @see		Collator
	 * @see		DefaultMutableTreeNode
	 */
	public static DefaultMutableTreeNode sortTree(DefaultMutableTreeNode root_unsorted_tree)
	{
		// Children of root_unsorted_tree
		DefaultMutableTreeNode children[] = new DefaultMutableTreeNode[root_unsorted_tree.getChildCount()];
		
		// Sort the children of root_unsorted_tree
		Collator string_comparer = Collator.getInstance();
		for (int i = 0; i < root_unsorted_tree.getChildCount(); i++)
			children[i] = (DefaultMutableTreeNode) root_unsorted_tree.getChildAt(i);
		for (int i = 0; i < root_unsorted_tree.getChildCount(); i++)
		{
			// Find if any nodes should come before children[i]
			DefaultMutableTreeNode first = children[i];
			String first_name = (String) first.getUserObject();
			int first_indice = i;
			for (int j = i + 1; j < children.length; j++)
			{
				String test_name = (String) children[j].getUserObject();
				if (string_comparer.compare(first_name, test_name) > 0)
				{
					first_name = test_name;
					first_indice = j;
				}
			}

			// Perform a switch if necessary
			if (first_indice != i)
			{
				DefaultMutableTreeNode temp = children[i];
				children[i] = children[first_indice];
				children[first_indice] = temp;
			}
		}

		// Sort the children of the children of root_unsorted_tree
		for (int i = 0; i < children.length; i++)
			if (!children[i].isLeaf())
				children[i] = sortTree(children[i]);

		// Return the sorted tree
		DefaultMutableTreeNode root_sorted_tree = new DefaultMutableTreeNode(root_unsorted_tree.getUserObject());
		for (int i = 0; i < children.length; i++)
			root_sorted_tree.add(children[i]);
		return root_sorted_tree;
	}


	/**
	 * Takes a two dimensional String array representing a table (column indice
	 * first, then row indice) and returns a copy of the table sorted by the
	 * data in the given column indice.
	 * 
	 * <p>The sorted_ordering array is filled to reflect the positions that the
	 * old rows were assigned to. This array should have the same size as the 
	 * number of rows of the original_table parameter. The original values of
	 * the sorted_ordering array are irrelevant.
	 *
	 * @param	original_table	The table to be sorted.
	 * @param	column_to_sort_by	The column to base the sort on.
	 * @param	sorted_ordering	The number position assigned to each row after sorting.
	 * @return	A copy of the table that has been sorted.
	 * @see		Collator
	 */
	public static String[][] sortTable(String[][] original_table, int column_to_sort_by, int[] sorted_ordering)
	{
		Collator string_comparer = Collator.getInstance();

		boolean[] added_already = new boolean[original_table.length];
		for (int i = 0; i < original_table.length - 1; i++)
			added_already[i] = false;

		for (int i = 0; i < original_table.length; i++)
		{
			String earliest_yet = null;
			int earliest_yet_index = -1;
			for (int j = 0; j < original_table.length; j++)
			{
				if(!added_already[j])
				{
					if (earliest_yet == null)
					{
						earliest_yet = original_table[j][column_to_sort_by];
						earliest_yet_index = j;
					}
					else if (string_comparer.compare(earliest_yet, original_table[j][column_to_sort_by]) > 0)
					{
						earliest_yet = original_table[j][column_to_sort_by];
						earliest_yet_index = j;
					}
				}
			}
			sorted_ordering[i] = earliest_yet_index;
			added_already[earliest_yet_index] = true;
		}

		String[][] sorted_table = new String[original_table.length][];
		for (int i = 0; i < original_table.length; i++)
			sorted_table[i] = original_table[sorted_ordering[i]];

		return sorted_table;	
	}


	/**
	 * Takes in an array of doubles and returns a copy of the array sorted
	 * from lowest to highest. The orignal array is not altered.
	 *
	 * @param	to_sort	The data that is to be sorted.
	 * @return	A copy of the array sorted from lowest to highest.
	 */
	public static double[] sortDoubleArray(double[] to_sort)
	{
		// Copy to_sort
		double[] sorted = new double[to_sort.length];
		for (int i = 0; i < to_sort.length; i++)
			sorted[i] = to_sort[i];

		// Sort sorted
		for (int i = 0; i < sorted.length; i++)
		{
			// Find the lowest
			double min = sorted[i];
			int min_index = i;
			for (int j = i; j < sorted.length; j++)
				if (sorted[j] < min)
				{
					min_index = j;
					min = sorted[j];
				}
			
			// Exchange the lowest with i
			double temp = sorted[i];
			sorted[i] = sorted[min_index];
			sorted[min_index] = temp;
		}

		// Return the sorted array
		return sorted;
	}


	/**
	 * Takes in an array of doubles and returns an array holding
	 * the rankings from lowest (0) to highest (to_sort -1) of
	 * each entry from lowest to highest. The orignal array is not
	 * altered.
	 *
	 * @param	to_sort	The data that is to be sorted.
	 * @return	The rankings of to_sort.
	 */
	public static int[] getDoubleArraySortKey(double[] to_sort)
	{
		// Create the key
		int[] key = new int[to_sort.length];
		for (int i = 0; i < key.length; i++)
			key[i] = -1;

		// Find the ordering
		for (int pos = 0; pos < key.length; pos++)
		{
			int lowest_index_yet = -1;
			double lowest_value_yet = 0;
			boolean found_one = false;
			for (int i = 0; i < to_sort.length; i++)
			{
				if (key[i] == -1)
				{
					if (!found_one)
					{
						lowest_index_yet = i;
						lowest_value_yet = to_sort[i];
						found_one = true;
					}
					else if (to_sort[i] < lowest_value_yet)
					{
						lowest_index_yet = i;
						lowest_value_yet = to_sort[i];
					}
				}
			}
			key[lowest_index_yet] = pos;
		}

		// Return the key
		return key;
	}
}