/**
 * SourceFile
 *
 * Created Sep 15, 2010-3:09:19 PM by Daniel McEnnis
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

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.tree.DefaultMutableTreeNode;

import org.multihelp.HelpViewer;

/**
 * SourceFile
 *
 * FileNode class for the display of source code directories.
 *
 * @author Daniel McEnnis
 */
public class SourceFile extends FileNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	File base;

	boolean isDirectory;

	private Icon icon = null;

	private String text = "";

	Filter filter = new Filter();

	/**
	 * Default constructor loading icons and text for the given node.
	 */
	public SourceFile(File root) {
		super(root);
		base = root;
		isDirectory = base.isDirectory();
		if (root.getName().endsWith(".java")) {
			icon = new ImageIcon("icons/java.jpg");
			text = root.getName().substring(0,
					root.getName().lastIndexOf(".java"));
		} else if (root.getName().endsWith(".xml")) {
			icon = new ImageIcon("xml.png");
			text = root.getName().substring(0,
					root.getName().lastIndexOf(".xml"));
		} else {
			text = root.getName();
		}
	}

	
	/**
	 * Load the viewer page with a HTML version of the source code. 
	 *
	 * @see org.multihelp.file.FileNode#setPage(org.multihelp.HelpViewer)
	 */
	public void setPage(HelpViewer viewer) {
		if (isDirectory) {
			String header = "<html><body><ul>";
			String footer = "</ul></body></html>";
			StringBuffer content = new StringBuffer();
			File[] children = base.listFiles();
			if ((children != null) && (children.length > 0)) {
				for (int i = 0; i < children.length; ++i) {
					content.append("<li>" + children[i].getName() + "</li>");
				}
			}
			HTMLDocument doc = new HTMLDocument();
			try {
				doc.insertString(0, header + content.toString() + footer, null);
				try {
					doc.setBase(this.base.toURI().toURL());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			} catch (BadLocationException e) {
				Logger.getLogger(this.getClass().getCanonicalName());
			}
			viewer.setContentType("text/html");
			viewer.setDocument(doc);
		} else {
			FileReader stream;
			try {
				stream = new FileReader(base);
				char[] buffer = new char[10240];
				StringBuffer fileBuffer = new StringBuffer();
				int read = 0;
				while ((read = stream.read(buffer)) > 0) {
					fileBuffer.append(buffer, 0, read);
				}
				String file = fileBuffer.toString();

				Pattern lt = Pattern.compile("<");
				Matcher match = lt.matcher(file);
				file = match.replaceAll("&lt;");

				Pattern gt = Pattern.compile(">");
				match = gt.matcher(file);
				file = match.replaceAll("&gt;");

				Pattern amp = Pattern.compile("&");
				match = amp.matcher(file);
				file = match.replaceAll("&amp;");
				HTMLDocument doc = new HTMLDocument();
				try {
					doc.insertString(0, "<html><body><code><pre>" + file
							+ "</pre></code></body></html>", null);
					doc.setBase(this.base.toURI().toURL());
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				viewer.setContentType("text/html");
				viewer.setDocument(doc);
			} catch (FileNotFoundException e) {
				HTMLDocument doc = new HTMLDocument();
				try {
					doc.insertString(
							0,
							"<html><body>ERROR: Help file '"
									+ base.getAbsolutePath()
									+ "' not found</body></html>", null);
					try {
						doc.setBase(this.base.toURI().toURL());
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					}
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				viewer.setContentType("text/html");
				viewer.setDocument(doc);
				e.printStackTrace();
			} catch (IOException e) {
				HTMLDocument doc = new HTMLDocument();
				try {
					doc.insertString(
							0,
							"<html><body>ERROR: Help file '"
									+ base.getAbsolutePath()
									+ "' had an IO error</body></html>", null);
					try {
						doc.setBase(this.base.toURI().toURL());
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					}
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				viewer.setContentType("text/html");
				viewer.setDocument(doc);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Implements a filter for determining which files in the source directories are included.
	 */
	public class Filter implements FileFilter {

		/**
		 * Returns whether or not a file should be listed in a source directory.
		 *
		 * @see java.io.FileFilter#accept(java.io.File)
		 */		
		public boolean accept(File pathname) {
			if (pathname.isDirectory()) {
				return true;
			} else if (pathname.getName().endsWith(".java")) {
				return true;
			} else if (pathname.getName().endsWith(".xml")) {
				return true;
			}
			return false;
		}

	}

	/**
	 *
	 * Descend a file hierarchy.  Assumes all children are of type Source files.
	 *
	 * @see org.multihelp.file.FileNode#traverseFileSystem(java.io.File, int)
	 */
	public void traverseFileSystem(File root, int depth) {
		if (depth < 512) {
			File[] children = root.listFiles();
			if ((children != null) && (children.length != 0)) {
				for (int i = 0; i < children.length; ++i) {
					if (filter.accept(children[i])) {
						SourceFile childNode = new SourceFile(children[i]);
						this.add(childNode);
						childNode.setParent(this);
						// System.out.println(childNode.toString());
						childNode.traverseFileSystem(children[i], depth + 1);
					}
				}
			}
		}
	}

	/**
	 * Return the icon loaded in the constructor.
	 * 
	 * @see org.multihelp.file.FileNode#getIcon()
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * Return the text loaded in the constructor.
	 *
	 * @see org.multihelp.file.FileNode#getText()
	 */
	public String getText() {
		return text;
	}

}
