/**
 * FileNode
 *
 * Created Sep 15, 2010-3:16:25 PM by Daniel McEnnis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.multihelp.file;

import java.awt.Component;
import java.io.File;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.multihelp.HelpViewer;

/**
 * FileNode
 * 
 * Base abstract class that represents a model of a help system resource.
 *
 * @author Daniel McEnnis
 */
public abstract class FileNode extends DefaultMutableTreeNode{
	
	String emergencyName;

	/**
	 * FileNode constructor that stores an emergency name for use in fully default 
	 * FileReaderTree displays.
	 * @param root
	 */
	public FileNode(File root){
		super(root);
		emergencyName = root.getName();
	}
	
	protected static MagicCheck magic = new DefaultMagicCheck();
	
	/**
	 * Places the content represented by this model in the HelpViewer display.
	 * 
	 * Must be overwritten by concrete classes.
	 */
	public abstract void setPage(HelpViewer viewer);
	
	/**
	 * FileNode class specific traversal of resources, using the custom filtering and node creation
	 * routines specific to that concrete class.  Also must be overwritten.  
	 * 
	 * @param root 
	 * @param depth 
	 */
	public abstract void traverseFileSystem(File root, int depth);
	
	/**
	 * Determines the type using the static determined Magic analysis (strategy) class.
	 */
	public static FileNode determineType(File root){
		return magic.determineType(root);
	}
	
	/**
	 * Set which class will determine FileNode type.
	 */
	public static void setMagic(MagicCheck newMagic){
		if(newMagic != null){
			magic=newMagic;
		}
	}
	
	/**
	 * Return the algorithm class responsible for determining which FileNode class is needed (based on analysis of the directory/file).
	 */
	public static MagicCheck getMagic(){
		return magic;
	}
	
	/**
	 * Rendering all entries of this FileNode type.  Default returns null.
	 */
	public Component render(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus){
		return null;
	}
	
	/**
	 * Provide the icon for the leaf entry, using default directory for display.  If null, the icon is omitted in display.
	 */
	public Icon getIcon(){
		return null;
	}
	
	/**
	 * Provide text for the leaf entry, using default directory for display. 
	 */
	public String getText(){
		return emergencyName;
	}

	/**
	 * Rendering the file row from scratch, but using default directory entries.
	 */
	protected Component renderFile(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		return null;
	}
	
	/**
	 * Returns a (possibly munged) FileNode resource from the given URL. 
	 * @param url
	 * @return
	 */
	public FileNode resolveURL(URL url){
		return null;
	}
}
