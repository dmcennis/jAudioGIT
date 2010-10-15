/**
 * Created October 8, 2010 by Daniel McEnnis
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
import java.io.IOException;
import java.net.URL;

import org.multihelp.HelpViewer;

/**
 * ExternalNode
 * 
 * FileNode for representing files and/or resources outside of the MultiHelp system.
 * 
 * @author Daniel McEnnis
 *
 */
public class ExternalNode extends FileNode {

	URL location;
	
	/**
	 * Create a new external FileNode from the give URL (file or net).
	 * @param base url to load
	 */
	public ExternalNode(URL base) {
		super(new File("."));
		location=base;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3159932910889236328L;

	/**
	 * Load the content of this external resource into the help viewer.
	 * 
	 * @see org.multihelp.file.FileNode#setPage(org.multihelp.HelpViewer)
	 */
	@Override
	public void setPage(HelpViewer viewer) {
		try {
			viewer.setPage(location);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/** 
	 * no traversal is performed
	 * 
	 * @see org.multihelp.file.FileNode#traverseFileSystem(java.io.File, int)
	 */
	@Override
	public void traverseFileSystem(File root, int depth) {
		// deliberately null

	}

	/**
	 * Load a resource from a link inside the external resource HTML.
	 */
	@Override
	public FileNode resolveURL(URL url) {
		return new ExternalNode(url);
	}

}
