/**
 * HelpViewer
 *
 * Created Sep 15, 2010-2:19:09 PM by Daniel McEnnis
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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.tree.TreePath;

import org.multihelp.file.ExternalNode;
import org.multihelp.file.FileNode;

/**
 * HelpViewer
 *
 * HTML Viewer class of the Help system.  In addition to display, it maintains a reference to the local FileNode.
 * The Help viewer is also responsible for registering with the FileReader in the '' design pattern.
 *
 * @author Daniel McEnnis
 */
public class HelpViewer extends JEditorPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	/**
	 * The base constructor is the JEditor default constructor.
	 */
	public HelpViewer() {
		super();
	}

	protected FileTreeReader fileSource;

	protected FileNode currentNode;

	/**
	 * Default anonymous class Tree selection listener for loading a new page.  Not currently used, but can be.  Replaced by hyperlink listener (to catch selection events
	 * that do not result in a selection set change).
	 */
	private MouseListener listener = new MouseListener() {

		/**
		 * Reload the window with a new FileNode.  Also sets the new file node in the page.
		 */
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 0) {
				if (fileSource.getSelectionCount() > 0) {
					TreePath path = fileSource.getLeadSelectionPath();
					if (path != null) {
						FileNode node = (FileNode) path.getLastPathComponent();
						currentNode = node;
						node.setPage(reflect());
					}
				}
			}
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

	};

//	private TreeSelectionListener processFileSelection = new TreeSelectionListener() {
//
//		public void valueChanged(TreeSelectionEvent arg0) {
//
//			// TODO Auto-generated method stub
//			TreePath path = arg0.getNewLeadSelectionPath();
//			if (path != null) {
//				FileNode node = (FileNode) path.getLastPathComponent();
//				node.setPage(reflect());
//			}
//		}
//	};

	/**
	 * Idiom for making the parent available to anonymous and inner classes without a variable.
	 */
	protected HelpViewer reflect() {
		return this;
	}

	/**
	 * Default anonymous inner class for loading a new page from a hyperlink activation inside the 
	 */
	protected HyperlinkListener urlHook = new HyperlinkListener() {

		/**
		 * Load the link activated. Calls FileNode to handle custom URL resolving and resolves itself if the result is null.  
		 */
		public void hyperlinkUpdate(HyperlinkEvent arg0) {
			try {
				if (arg0.getEventType().toString().equals("ACTIVATED")) {
					FileNode node = currentNode.resolveURL(arg0.getURL());
					if(node == null){
						node = new ExternalNode(arg0.getURL());
					}
					currentNode = node;
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						e.getMessage() + e.getStackTrace(), "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	};

	/**
	 * Create a HelpViewer object using the given fileSource as the intial page.
	 */
	public HelpViewer(FileTreeReader fileSource) {
		this.fileSource = fileSource;
		setEditable(false);
		// fileSource.addTreeSelectionListener(processFileSelection);
		fileSource.addMouseListener(listener);
		try {
			FileNode n = (FileNode) fileSource.getModel().getRoot();
			currentNode = n;
			n.setPage(this);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getStackTrace(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
		this.addHyperlinkListener(urlHook);
	}
}
