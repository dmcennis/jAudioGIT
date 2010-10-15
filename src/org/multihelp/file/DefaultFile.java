/**
 * MagicBase
 *
 * Created Sep 15, 2010-4:37:07 PM by Daniel McEnnis
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;

import org.multihelp.HelpViewer;

/**
 * DefaultFile
 *
 * FileNode type for any file that does not conform to any known file type.
 * 
 * @author Daniel McEnnis
 */
public class DefaultFile extends FileNode {
	File base;

	/**
	 * Default FileNode constructor.
	 */
	public DefaultFile(File root) {
		super(root);
		base = root;
	}

	/**
	 * Assumes a text file type, then loads the file by escaping all HTHL characters.
	 * 
	 * @see org.multihelp.file.FileNode#setPage(org.multihelp.HelpViewer)
	 */
	public void setPage(HelpViewer viewer) {
		FileReader stream;
		try {
			File source = base;
			if (source.isDirectory()) {
				source = (new File(base.getCanonicalPath() + File.pathSeparator
						+ "index.html"));
			}
			if (source.exists()) {
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
					doc.insertString(0, "<html><body><pre>" + file + "</pre></body></html>", null);
					doc.setBase(this.base.toURI().toURL());
				} catch (BadLocationException e) {
					Logger.getLogger(this.getClass().getCanonicalName());
				}
				viewer.setContentType("text/html");
				viewer.setDocument(doc);
			}
		} catch (FileNotFoundException e) {
			HTMLDocument doc = new HTMLDocument();
			try {
				doc.insertString(0, "<html><body>ERROR: Help file '"
						+ base.getAbsolutePath() + "' not found</body></html>", null);
				try {
					doc.setBase(this.base.toURI().toURL());
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
			} catch (BadLocationException e1) {
				Logger.getLogger(this.getClass().getCanonicalName());
			}
			viewer.setContentType("text/html");
			viewer.setDocument(doc);
			e.printStackTrace();
			Logger.getLogger(this.getClass().getCanonicalName());
		} catch (IOException e) {
			HTMLDocument doc = new HTMLDocument();
			try {
				doc.insertString(0, "<html><body>ERROR: Help file '"
						+ base.getAbsolutePath()
						+ "' had an IO error</body></html>", null);
				try {
					doc.setBase(this.base.toURI().toURL());
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
			} catch (BadLocationException e1) {
				Logger.getLogger(this.getClass().getCanonicalName());
			}
			viewer.setContentType("text/html");
			viewer.setDocument(doc);
			e.printStackTrace();
		}
	}

	/**
	 * Descend from this directory node into children files, using a magic check to determine the type of each file.
	 * 
	 * @see org.multihelp.file.FileNode#traverseFileSystem(java.io.File, int)
	 */
	public void traverseFileSystem(File root, int depth) {
		if (depth < 512) {
			File[] children = root.listFiles();
			if ((children != null) && (children.length != 0)) {
				for (int i = 0; i < children.length; ++i) {
					FileNode childNode = FileNode.determineType(children[i]);
					this.add(childNode);
					childNode.setParent(this);
//					System.out.println(childNode.toString());
					childNode.traverseFileSystem(children[i], depth + 1);
				}
			}
		}
	}

	/**
	 * Returns the text of the file name as its display name.
	 * 
	 * @see org.multihelp.file.FileNode#getText()
	 */
	public String getText() {
		// TODO Auto-generated method stub
		return base.getName();
	}

}
