/**
 * MagicCheck
 *
 * Created Sep 15, 2010-4:45:31 PM by Daniel McEnnis
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
 * MagicCheck
 *
 * Interface for a UNIX style automated choice of FileNode based on file or directory content.
 *
 * @author Daniel McEnnis
 */
public interface MagicCheck {
	/**
	 * perform a UNIX magic check.
	 */
	public FileNode determineType(File root);
}
