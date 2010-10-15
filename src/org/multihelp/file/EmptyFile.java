/**
 * EmptyFile
 *
 * Created Sep 15, 2010-5:01:49 PM by Daniel McEnnis
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

import org.multihelp.HelpViewer;

/**
 * EmptyFile
 *
 * Catch all FileNode class for handling binary or other unexpected files.
 *
 * @author Daniel McEnnis
 */
public class EmptyFile extends FileNode {
	
	public EmptyFile(File root){
		super(root);
	}
	
	/**
	 * Deliberately null operation (for binary files)
	 * @see org.multihelp.file.FileNode#setPage(org.multihelp.HelpViewer)
	 */
	public void setPage(HelpViewer viewer) {
		// TODO Auto-generated method stub

	}

	/**
	 * null operation, no directories are traversed.
	 * @see org.multihelp.file.FileNode#traverseFileSystem(java.io.File, int)
	 */
	public void traverseFileSystem(File root, int depth) {
		// TODO Auto-generated method stub

	}

}
