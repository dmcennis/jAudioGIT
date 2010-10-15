/**
 * FileTreeReader
 *
 * Created Sep 15, 2010-12:23:30 PM by Daniel McEnnis
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
package org.multihelp;

import java.io.File;
import java.net.URL;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.multihelp.file.FileNode;
import org.multihelp.file.FileNodeRenderer;

/**
 * FileTreeReader
 *
 * Reads a directory, creating a custom documentation tree.  Its components are FileNodes.
 * Each FileNode is responsible for its 
 * own directory parsing.  This is called from the constructor that handles nearly all setup.
 * The Viewer window listens to selection events, calling back to FileNodes referenced by
 * the selection event and has a custom TreeSelectionListener for this purpose.
 * 
 * NOTE: TreeSelectionListener is replaced by MouseListener in the current model and may be 
 * deprecated in 0.3.
 * 
 * @author Daniel McEnnis
 */
public class FileTreeReader extends JTree{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;

	/**
	 * FileTreeRader traverses a directory Presented by the HelpWindow class.  It is 
	 * pre-set so that the localization, if present, is already implicitly
	 * included in the start directory.
	 * 
	 */
	public FileTreeReader(File root) {
		FileNode base = FileNode.determineType(root);
		setModel(new DefaultTreeModel(base));
		this.setCellRenderer(new FileNodeRenderer());
		if ((root != null) && root.exists() && root.isDirectory()) {
			System.out.println(root.getAbsolutePath());
			base.traverseFileSystem( root, 0);
		}
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.setRootVisible(true);
		this.setShowsRootHandles(true);
	}
	

	/**
	 * May be deprecated in 0.3.  Adds a selection listener for HelpViewer to locate changes in the current
	 * selected FileNode.
	 */
	public void addTreeSelectionListener(TreeSelectionListener listener){
		getSelectionModel().addTreeSelectionListener(listener);		
	}
}
