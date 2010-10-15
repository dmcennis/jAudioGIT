/**
 * JavadocFile
 *
 * Created Sep 15, 2010-2:45:07 PM by Daniel McEnnis
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
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.tree.DefaultMutableTreeNode;

import org.multihelp.HelpViewer;

/**
 * JavadocFile
 *
 * Custom FileNode for the proper display of Javadoc documentation.
 *
 * @author Daniel McEnnis
 */
public class JavadocFile extends FileNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	private Icon icon=null;
	
	private String packageName="";
	
	private String text = "";
	
	private File fileLocation;
	
	private Filter filter = new Filter();
	
	boolean isDirectory;
	
	/**
	 * Constructor loading icons and text.
	 */
	public JavadocFile(File file){
		super(file);
		fileLocation = file;
		if(file.getName().endsWith(".html")){
			isDirectory=false;
			icon = new ImageIcon("icons/javadoc.png");
			if(icon==null){
				System.out.println("Can not find help/javadoc.png");
			}
			text =file.getName().substring(0, file.getName().lastIndexOf(".html"));
		}else{
			isDirectory=true;
			text = file.getName();
		}
	}
	
	/**
	 * Load the Javadoc pages into the viewer.
	 *
	 * @see org.multihelp.file.FileNode#setPage(org.multihelp.HelpViewer)
	 */	
	public void setPage(HelpViewer viewer) {
		try {
			if(isDirectory){
				File loc = new File(fileLocation.getCanonicalPath()+File.separator+"index.html");
				if(loc.exists()){
					viewer.setPage(loc.toURI().toURL());
				}else{
					System.out.println(loc.getCanonicalPath()+" not found.");
					HTMLDocument doc = new HTMLDocument();
					try {
						doc.insertString(0, generateDefaultIndex(), null);
						doc.setBase(this.fileLocation.toURI().toURL());
					} catch (BadLocationException e) {
						Logger.getLogger(this.getClass().getCanonicalName());
					}
					viewer.setContentType("text/html");
					viewer.setDocument(doc);
				}
			}else{
				viewer.setPage(fileLocation.toURI().toURL());
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getStackTrace(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Generate a default page in the case of malformed or missing content.
	 */
	protected String generateDefaultIndex(){
		if(fileLocation.isDirectory()&&(fileLocation.listFiles().length>0)){
			File[] list = fileLocation.listFiles();
			StringBuffer ret = new StringBuffer("<html><body><ul>");
			for(int i=0;i<list.length;++i){
				ret.append("<li><a href=\""+list[i].getName()+"\">"+list[i].getName()+"</a></li>");
			}
			ret.append("<ul></body></html>");
			return ret.toString();
		}else if(fileLocation.isDirectory()){
			return "<html><body>ERROR: Empty directory in the help system.</body></html>";
		}else{
			return "<html><body>INTERNAL ERROR: Loading non-existant file</body></html>";
		}
	}

	/**
	 * Inner class for custom filtering files in descend directories to load FileTreeReader with only viewable entries.
	 */
	public class Filter implements FileFilter{

		/**
		 * Default constructor
		 */
		public Filter(){
		}
		/**
		 * Function for determining whether a file entry should be displayed or not.
		 *
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File pathname) {
			if(pathname.isDirectory()){
				return true;
			}else if(pathname.getName().endsWith(".html")||pathname.getName().endsWith(".htm")){
				
				if(pathname.getName().contentEquals("index.html")){
					return false;
				} else if(pathname.getName().contentEquals("index.html")){
					return false;
				} else if(pathname.getName().contentEquals("allclasses-frame.html")){
					return false;
				} else if(pathname.getName().contentEquals("allclasses-noframe.html")){
					return false;
				} else if(pathname.getName().contentEquals("constant-values.html")){
					return false;
				} else if(pathname.getName().contentEquals("deprecated-list.html")){
					return false;
				} else if(pathname.getName().contentEquals("index-all.html")){
					return false;
				} else if(pathname.getName().contentEquals("overview-frame.html")){
					return false;
				} else if(pathname.getName().contentEquals("overview-summary.html")){
					return false;
				} else if(pathname.getName().contentEquals("overview-tree.html")){
					return false;
				} else if(pathname.getName().contentEquals("package-frame.html")){
					return false;
				} else if(pathname.getName().contentEquals("package-list.html")){
					return false;
				} else if(pathname.getName().contentEquals("package-summary.html")){
					return false;
				} else if(pathname.getName().contentEquals("package-tree.html")){
					return false;
				} else if(pathname.getName().contentEquals("serialized-form.html")){
					return false;
				}
				return true;
			}
			return false;
		}
		
	}
	
	/**
	 * Traverses the directory, loading the Javadoc directory in.  Assumes that only Javadoc entries are present in a descent directory.
	 */
 	public void traverseFileSystem(File root, int depth) {
		if (depth < 512) {
			File[] children = root.listFiles();
			if ((children != null) && (children.length != 0)) {
				for (int i = 0; i < children.length; ++i) {
					if(filter.accept(children[i])){
						JavadocFile childNode = new JavadocFile(
								children[i]);
						this.add(childNode);
						childNode.setParent(this);
//						System.out.println(childNode.getText());
						childNode.traverseFileSystem(children[i], depth+1);
					}else{
//						System.out.println("Rejected "+children[i].getName());
					}
				}
			}
		}
	}

	/**
	 * Return the icon loaded in the constructor.
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
