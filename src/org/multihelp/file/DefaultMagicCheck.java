/**
 * DefaultMagicCheck
 *
 * Created Sep 15, 2010-4:50:10 PM by Daniel McEnnis
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

/**
 * DefaultMagicCheck
 *
 * Determines which type of FileNode is appropriate and then returns the appropriate type in the style of the UNIX
 * magic check.
 *
 * @author Daniel McEnnis
 */
public class DefaultMagicCheck implements MagicCheck {

	/**
	 * Determines the FileNode tyope and returns the correct type (like the magic file type check of the UNIX system).
	 *
	 * @see org.multihelp.file.MagicCheck#determineType(java.io.File)
	 */
	public FileNode determineType(File root) {
		if((root != null) &&(root.listFiles()!=null)&&(root.listFiles().length>0) ){
			File[] children = root.listFiles();
			boolean html = false;
			for(int i=0;i<children.length;++i){
				if(children[i].getName().equals("allclasses-frame.html")){
					System.out.println("Entering Javadoc - "+root.getAbsolutePath());
					return new JavadocFile(root);
				}
				if(children[i].getName().endsWith(".java")){
					System.out.println("Entering Source - "+root.getAbsolutePath());
					return new SourceFile(root);
				}
				if(children[i].getName().endsWith(".html")||children[i].getName().endsWith(".htm")){
					System.out.println("Entering HTML - "+root.getAbsolutePath());
					html = true;
				}
			}
			if(html){
				return new HTMLFile(root);
			}
		}
		if(root.getName().endsWith(".java")){
			return new SourceFile(root);
		}
		if(root.getName().endsWith(".html")||root.getName().endsWith(".htm")){
			return new HTMLFile(root);
		}
		if(root.getName().endsWith(".exe")||root.getName().endsWith(".dll")
				||root.getName().endsWith(".zip")
				||root.getName().endsWith(".jar")
				||root.getName().endsWith(".tar")
				||root.getName().endsWith(".bz2")
				||root.getName().endsWith(".gz")
				||root.getName().endsWith(".tgz")){
			return new EmptyFile(root);
		}
		return new DefaultFile(root);
	}

}
